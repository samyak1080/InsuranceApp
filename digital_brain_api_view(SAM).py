digital_brain_api_view.py

import os             
import flask
from geopy import Nominatim
from flask import Flask ,jsonify , request ,send_from_directory
from digital_brain_api_model import DigitalBrainModel
import zipfile
from PIL import Image
import json
import shutil
import logging
from math import sin, cos, asin, sqrt, degrees, radians

Earth_radius_km = 6371.0
RADIUS = Earth_radius_km

logging.basicConfig(format='%(asctime)s - %(message)s', level=logging.INFO)

from auto_detection_wrapper import auto_detect_function

app = Flask(__name__)

logging.info('Auto Detection API Service Started')

#This API call to add claim request to Neo4J and save image in local path and return image path
@app.route("/api/v1/resources/add_claim",methods=['GET','POST'])
def add_claim():
    
                logging.info('Service called to add claim')
                if (request.method == 'POST'):
                                logging.info('Get Claim Request')
                                #Get claim request as json from POST method
                                claim_request = request.form['json']
                                
                                #Get Auto image path
                                auto_image_file = request.files['image']
                                #Load claim request
                                claim_request_json = json.loads(claim_request)

                                claim_id = claim_request_json['claimid']
                                policy_id=request.form['policy_id']
                                uploads_dir = os.path.join(app.instance_path, 'uploads/' , claim_id)
                                #uploads_dir = uploads_dir + '/' + claim_id
                                if not os.path.exists(uploads_dir):
                                                os.makedirs(uploads_dir)

                                logging.info('Save auto image in the path ' + uploads_dir)
                                #Save auto image in the path 
                                auto_image_file.save(os.path.join(uploads_dir, auto_image_file.filename))
                                
                                #Get image path
                                claim_request_json["path"] = os.path.join(uploads_dir, auto_image_file.filename)
                                
                                logging.info('Add claim request entry into Claim node in Neo4J')
                                #Create property for claim request under Claim node in Neo4J
                                DigitalBrainModel(claim_request_json).add_claim(policy_id)

                                logging.info('Added claim request successfully')
                                #Get image path
                                image_path = os.path.join(uploads_dir, auto_image_file.filename)

                                logging.info('Return image path as response from add_claim request')
                                # Return image path
                                return jsonify({'Claim Submitted':'true'}),200 #image_path #res
    
                else:
                                logging.info('Failed to process claim request. Check for logs')
                                return jsonify({'claim Received':'false'}),501
                
#This API call to download damaged auto vehicle image and return this image
@app.route("/api/v1/resources/download/<path:image_path>",methods=['GET','POST'])
def download_highlighted_image(image_path):
                try:
                                logging.info('Download damaged auto image')
                                # Get Image path and image file name
                                path=request.form['path']
                                auto_image_path, image_name = os.path.split(image_path)
                                
                                #Get auto damage image from the output folder
                                response = send_from_directory(auto_image_path, image_name)
                                
                                #Return image as response
                                return response
                except FileNotFoundError:
                                abort(404)


# This API call to execute Auto Detection model for the given image and return json data as response
@app.route('/api/v1/resources/auto/detection', methods=['GET', 'POST'])
def auto_detection():

    logging.info('Calling Auto Detection Model for uploaded auto image')
    location = None
    
    claim_id=request.form['claim_id']

    auto_detection_data=DigitalBrainModel("").get_auto_detection_data(claim_id);
    
    if auto_detection_data is None:
        if 'location' in request.args:
           logging.info('Get image location path and image file name ' + request.args['location'])
           location, tail = os.path.split(request.args['location'])
        else:
           logging.info('Failed to image location path and image file name')
           return "Error: No location field provided. Please specify location."

        location = location + '/'
    
        try:
            logging.info('Delete if json and output folders already exists')
            if (os.path.exists(location + 'json')) and (os.path.exists(location + 'Output')):
               shutil.rmtree(location + 'json')
               shutil.rmtree(location + 'Output')
               logging.info('Deleted json and Output folders in the path ' + location)
        except:
            logging.info('Error to delete json and Output folders in the path ' + location)

                    #Call Auto Detection Function to detect if any damages
        auto_detect_function(location)

        logging.info('Return json data as response after processing image')    
        if os.path.exists(location + 'json'):
          with open(location + 'json/data.json') as f: 
            auto_detection_data = json.load(f)

            DigitalBrainModel("").store_auto_detection_model(claim_id, str(auto_detection_data))
            return  auto_detection_data
        else:
            logging.info('Failed to process image in Auto Detection model')  
            return "Auto Detection Process Failed. Check for Logs"
    else:
         return auto_detection_data

#Retrieve all claim requests to show on Mobile App
@app.route("/api/v1/resources/retrieve_all_claims",methods=['GET', 'POST'])
def retrieve_claim_request():
    logging.info("Return all claim requests")
    return DigitalBrainModel("").retrieve_claim_request()

@app.route("/api/v1/resources/update_status",methods=['GET','POST'])
def update_status():
    logging.info("This update_status api to update status based agent response")
    claimid=request.form['claimid']
    status=request.form['status']
    
    DigitalBrainModel("").update_status(claimid, status)
    
    return jsonify({'Status Updated':'true'}),201

@app.route("/api/v1/resources/agent_login",methods=['GET','POST'])
def agent_login():
    logging.info("This agent_login api called to check for authentication for agent")

    username=request.form['username']
    password=request.form['password']
    
    response=DigitalBrainModel("").agent_login(username)
    
    if (password==response["password"]):
        return jsonify({'status':'true','agent_name':response["agent_name"]}),200
    else:  
        return jsonify({'status':'false'}),501

@app.route("/api/v1/resources/user_login",methods=['GET','POST'])
def user_login():
    logging.info("This user_login api called to check for authentication for agent")

    username=request.form['username']
    password=request.form['password']
    
    response=DigitalBrainModel("").user_login(username)
    
    if (password==response["password"]):
        return jsonify({'status':'true','user_name':response["customer_name"],"policy_id":response["policy_id"]}),200
    else:  
        return jsonify({'status':'false'}),501

@app.route('/api/v1/resources/nearby_events', methods=['GET', 'POST'])
def nearby_events():
    policy_id=request.form['policy_id']

    geolocator = Nominatim()
                #customer_id="428-59-7371"
    address=DigitalBrainModel("").get_customer_address(policy_id)
    logging.info("Test")
    location = geolocator.geocode(address["mailing_address_line_1"]+","+address["mailing_address_line_2"]+","+address["mailing_state"]+","+address["mailing_country"])
    print ("lat long customer: "+str(location.latitude)+"!!!!!!!!!!!!!!"+ str(location.longitude))
    ret=DigitalBrainModel("").get_city_events("")
    res=[]
    for r in ret:
        dist=distance_between_points(r["lat"], r["long"], location.latitude, location.longitude)
        #print ("distance:     "+str(dist) +"  lat: "+ str(r["lat"])+"  long: "+ str(r["long"]))
        if (dist<=3930):
             res+=[r]
    return str(res)

#Retrieve details of the driver involved in the claim
@app.route("/api/v1/resources/get_driver_details",methods=['GET', 'POST'])
def get_driver_details():
    logging.info("Return driver details")
    policy_id=request.form['policy_id']
    return DigitalBrainModel("").get_driver_details(policy_id)

#Retrieve details about the policy
@app.route("/api/v1/resources/get_policy_details",methods=['GET', 'POST'])
def get_policy_details():
    logging.info("Return policy details")
    policy_id=request.form['policy_id']
    return DigitalBrainModel("").get_policy_details(policy_id)

#Retrieve details of the customer
@app.route("/api/v1/resources/get_customer_details",methods=['GET', 'POST'])
def get_customer_details():
    logging.info("Return customer details")
    customer_id=request.form['customer_id']
    return DigitalBrainModel("").get_customer_details(customer_id)

#Retrieve details about the dependents on the customer
@app.route("/api/v1/resources/get_dependent_details",methods=['GET', 'POST'])
def get_dependent_details():
    logging.info("Return customer's dependents details")
    customer_id=request.form['customer_id']
    return DigitalBrainModel("").get_dependent_details(customer_id)

def haversine(angle_radians):
    return sin(angle_radians / 2.0) ** 2

def inverse_haversine(h):
    return 2 * asin(sqrt(h)) # radians

def distance_between_points(lat1, lon1, lat2, lon2):
    # all args are in degrees
    # WARNING: loss of absolute precision when points are near-antipodal
    lat1 = radians(lat1)
    lat2 = radians(lat2)
    dlat = lat2 - lat1
    dlon = radians(lon2 - lon1)
    h = haversine(dlat) + cos(lat1) * cos(lat2) * haversine(dlon)
    return RADIUS * inverse_haversine(h) 

app.run()
#app.run(host= '0.0.0.0')

