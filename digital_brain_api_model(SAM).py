Model: digital_brain_api_model.py

from py2neo import Graph, Node, Relationship
from flask import jsonify
from datetime import datetime
import time
import os
import uuid
import logging

logging.basicConfig(format='%(asctime)s - %(message)s', level=logging.INFO)

url = os.environ.get('', 'http://localhost:7474')

graph = Graph("bolt://localhost:7687", auth=("neo4j", "Test1234"))

#This class is use to create claim request, retrieve all claim requests
class DigitalBrainModel:
    logging.info('Digital Brain API Model')

    def __init__(self, desc):
        self.desc = desc

    #This function to add claim to the Neo4J graph DB
    def add_claim(self,policy_id):
        current_time_stamp=time.time()
        neo4j_object = graph.begin()
        claim_request = Node("Claim", claimid=self.desc["claimid"],overview=self.desc["overview"],path=self.desc["path"],status=self.desc["status"],timestamp=current_time_stamp)
        
        logging.info('Add claim request entry into Neo4J DB')
        neo4j_object.create(claim_request)
        neo4j_object.commit()        
        logging.info('Claim request added successfully into Neo4J DB')
        neo4j_object1 = graph.begin()
        fraud_claim = Node("Fraudulent_Claim", claimid=self.desc["claimid"],policy_id=policy_id,is_fraud="Fraud")
        neo4j_object1.create(fraud_claim)
        neo4j_object1.commit()

        #Create Relationship after claim request is raised
        graph.run("MATCH (a:Claim), (b:Policy) WHERE a.claimid = {claimid} AND b.policy_id = {policy_id} CREATE (a)-[r:RAISED_AGAINST]->(b)",claimid=self.desc["claimid"],policy_id=policy_id)

        #Create Relationship claim request with Fraudulent node
        graph.run("MATCH (a:Claim), (b:Fraudulent_Claim) WHERE a.claimid = {claimid} AND b.claimid = {claimid} CREATE (a)-[r:WAS_FRAUD]->(b)",claimid=self.desc["claimid"])

        res  = graph.run("MATCH (j:Policy) WHERE j.policy_id = {policy_id} RETURN j.agent_id", policy_id = policy_id).data()
        agent_id = res[0]["j.agent_id"]

        #Create Relationship claim request with Agent node
        graph.run("MATCH (a:Claim), (b:Agent) WHERE a.claimid = {claimid} AND b.agent_id = {agent_id} CREATE (a)-[r:CLAIM_ASSIGNED_TO]->(b)",claimid=self.desc["claimid"],agent_id=agent_id)
                                                                
    #This function to retrieve all claim requests
    def retrieve_claim_request(self):
        logging.info('Retrieve all Claim Requests under Claim Node')
        
        result = graph.run("MATCH (j:Claim) RETURN (j) ORDER BY j.timestamp desc")
        result = list(result)
        
        logging.info("Return all claim requests in json format")
        return jsonify([
            {
               "claimid":dict(dict(record)['j'])["claimid"],
               "overview":dict(dict(record)['j'])["overview"],
               "path":dict(dict(record)['j'])["path"],
               "status":dict(dict(record)['j'])["status"], 
                "timestamp":dict(dict(record)['j'])["timestamp"],
            }
            for record in result
        ])


    def update_status(self, claimid, status):
        logging.info('update_status method called to update status in Claim node')

        tx = graph.begin()

        graph.run("Match (a:Claim {claimid:{claimid}}) Set a.status = {status}", claimid = claimid, status = status)
        tx.commit()

    def agent_login(self,username):
        logging.info('agent_login method called to check authentication for agent')
        
        res  = graph.run("MATCH (j:Syslogin) WHERE j.username = {username} AND j.role_type = 'A' RETURN j.password", username = username).data()
        result = {}
        
        result["password"] = res[0]["j.password"]

        if result["password"] is not None:
           agent_data  = graph.run("MATCH (j:Agent) WHERE j.login_agentname = {login_agentname} RETURN j.agent_name", login_agentname = username).data()
           print(list(res))

           result["agent_name"] = agent_data[0]["j.agent_name"]
           result["password"] = res[0]["j.password"]

           print(result)
           return result
        else:
           return result

        
    def user_login(self,username):
        logging.info('user_login method called to check authentication for user')
        res  = graph.run("MATCH (j:Syslogin) WHERE j.username = {username} AND j.role_type = 'U' RETURN j.password", username = username).data()
        result = {}
        
        result["password"] = res[0]["j.password"]
        print(result["password"])
        if result["password"] is not None:
           cust_data  = graph.run("MATCH (j:Customer) WHERE j.login_username = {login_username} RETURN j.customer_name, j.policy_id", login_username = username).data()
           print(list(res))

           result["customer_name"] = cust_data[0]["j.customer_name"]
           result["password"] = res[0]["j.password"]
           result["policy_id"] = cust_data[0]["j.policy_id"]
        
           print(result)
           return result
        else:
           return result

    #This function to store auto detection model data into Neo4J
    def store_auto_detection_model(self, claimid, auto_detection_data):
        logging.info("Store auto detection model data")
        tx =  graph.begin()

        graph.run("match (a:Claim {claimid:{claimid}}) set a.auto_detection_data = {auto_detection_data}", claimid = claimid, auto_detection_data = auto_detection_data)
        tx.commit()

    #This function return auto detection model from Neo4J based on claim id
    def get_auto_detection_data(self, claimid):
        logging.info("Get Auto Detection Data based on claim id")

        auto_detection_data = graph.run("MATCH (a:Claim) WHERE a.claimid = {claimid} RETURN a.auto_detection_data", claimid = claimid).data()

        return auto_detection_data[0]['a.auto_detection_data']

    def get_city_events(self,city):
        res=graph.run("MATCH (a:Geo_Events) RETURN a")
        ret=[]
        
        for record in list(res):
            temp={}
            logging.info("Rank" + str(dict(record["a"])["rank"]))
            if dict(record["a"])["rank"] >= 80:
                temp["lat"]=dict(record["a"])["location_lat"]
                temp["long"]=dict(record["a"])["location_long"]
                temp["title"]=dict(record["a"])["title"]
                #temp["name"]=dict(record["a"])["name"]
                temp["category"]=dict(record["a"])["category"]
                ret+=[temp]
        return ret

    def get_customer_address(self, policy_id):
       
        res  = graph.run("MATCH (d:Customer{policy_id : {policy_id}}) RETURN d.mailing_address_line_1, d.mailing_address_line_2, d.mailing_state, d.mailing_country, d.mailing_city", policy_id = policy_id).data()

        result={}
        result["mailing_address_line_1"] = res[0]["d.mailing_address_line_1"]
        result["mailing_address_line_2"] = res[0]["d.mailing_address_line_2"]
        result["mailing_state"] = res[0]["d.mailing_state"]
        result[          "mailing_country"] = res[0]["d.mailing_country"]
        result[          "mailing_city"] = res[0]["d.mailing_city"]
        
        return result


    def get_dependent_details(self, customer_id):
        res = graph.run("MATCH (a:Customer {customer_id: {customer_id}})<-[:IS_DEPENDENT_UPON]-(j) RETURN j", customer_id = customer_id)
        res = list(res)
        return jsonify([
            {
                "relation_to_primary":dict(dict(record)['j'])["relation_to_primary"],
                "dependent_id":dict(dict(record)['j'])["dependent_id"],
                "policy_id":dict(dict(record)['j'])["policy_id"],
                "dob":dict(dict(record)['j'])["dob"],
                "dependent_name":dict(dict(record)['j'])["dependent_name"],
                "age":dict(dict(record)['j'])["age"],
            }
            for record in res
        ])       

    def get_customer_details(self, customer_id):
        res = graph.run("MATCH (j:Customer) WHERE j.customer_id = {customer_id} RETURN j", customer_id = customer_id)
        res = list(res)
        return jsonify([
            {
                "mailing_city":dict(dict(record)['j'])["mailing_city"],
                "mailing_county":dict(dict(record)['j'])["mailing_county"],
                "customer_id":dict(dict(record)['j'])["customer_id"],
                "policy_id":dict(dict(record)['j'])["policy_id"],
                "mailing_address_line_1":dict(dict(record)['j'])["mailing_address_line_1"],
                "mailing_address_line_2":dict(dict(record)['j'])["mailing_address_line_2"],
                "password":dict(dict(record)['j'])["password"],
                "mailing_country":dict(dict(record)['j'])["mailing_country"],
                "dob":dict(dict(record)['j'])["dob"],
                "active_since":dict(dict(record)['j'])["active_since"],
                "customer_name":dict(dict(record)['j'])["customer_name"],
                "age":dict(dict(record)['j'])["age"],
                "username":dict(dict(record)['j'])["username"],
            }
            for record in res
        ])     

    def get_policy_details(self, policy_id):
        res = graph.run("MATCH (j:Policy) WHERE j.policy_id = {policy_id} RETURN j", policy_id = policy_id)
        res = list(res)
        return jsonify([
            {
                "active_since":dict(dict(record)['j'])["active_since"],
                "at_fault_accident":dict(dict(record)['j'])["at_fault_accident"],
                "end_date":dict(dict(record)['j'])["end_date"],
                "license_status":dict(dict(record)['j'])["license_status"],
                "major_violation":dict(dict(record)['j'])["major_violation"],
                "minor_violation":dict(dict(record)['j'])["minor_violation"],
                "policy_holder_name":dict(dict(record)['j'])["policy_holder_name"],
                "policy_id":dict(dict(record)['j'])["policy_id"],
                "primary_ind":dict(dict(record)['j'])["primary_ind"],
                "registered_state":dict(dict(record)['j'])["registered_state"],
                "start_date":dict(dict(record)['j'])["start_date"],
            }
            for record in res
        ])        


    def get_driver_details(self, policy_id):
        res = graph.run("MATCH (j:Driver) WHERE j.policy_id = {policy_id} RETURN j", policy_id = policy_id)
        res = list(res)
        return jsonify([
            {
                "address_line_1":dict(dict(record)['j'])["address_line_1"],
                "address_line_2":dict(dict(record)['j'])["address_line_2"],
                "county":dict(dict(record)['j'])["county"],
                "city":dict(dict(record)['j'])["city"],
                "state":dict(dict(record)['j'])["state"],
                "zip_code":dict(dict(record)['j'])["zip_code"],
                "country":dict(dict(record)['j'])["country"],
                "age":dict(dict(record)['j'])["age"],
                "dl_expiry_date":dict(dict(record)['j'])["dl_expiry_date"],
                "dl_issue_date":dict(dict(record)['j'])["dl_issue_date"],
                "dl_no":dict(dict(record)['j'])["dl_no"],
                "dob":dict(dict(record)['j'])["dob"],
                "driver_id":dict(dict(record)['j'])["driver_id"],
                "driver_name":dict(dict(record)['j'])["driver_name"],
                "policy_id":dict(dict(record)['j'])["policy_id"],
            }
            for record in res
        ])