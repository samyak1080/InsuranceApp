package com.notez.com.myapplication;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Policy#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Policy extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static TextView pol_id, policy_holder_name, active_since, policy_expiry_date, agent_id;
    public static TextView at_fault_accident, car_rental_travel_exp, collision_ded, drivercomprehensive_ded_w_glass_name;
    public static TextView emergency_road_service, issue_date, liabilty_bodily_injury, registered_state;
    public static TextView liabilty_prop_damage, license_status, major_violation, minor_violation, policy_effective_date;
    public static TextView primary_ind, under_insured_mv_pd, uninsured_mv_bi;

    public Policy() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Policy.
     */
    // TODO: Rename and change types and number of parameters
    public static Policy newInstance(String param1, String param2) {
        Policy fragment = new Policy();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_policy, container, false);
        pol_id=rootView.findViewById(R.id.policy_id);
        policy_holder_name=rootView.findViewById(R.id.policy_holder_name);
        active_since=rootView.findViewById(R.id.active_since);
        policy_expiry_date=rootView.findViewById(R.id.policy_expiry_date);
        agent_id=rootView.findViewById(R.id.agent_id);
        at_fault_accident=rootView.findViewById(R.id.at_fault_accident);
        car_rental_travel_exp=rootView.findViewById(R.id.car_rental_travel_exp);
        collision_ded=rootView.findViewById(R.id.collision_ded);
        drivercomprehensive_ded_w_glass_name=rootView.findViewById(R.id.drivercomprehensive_ded_w_glass_name);
        emergency_road_service=rootView.findViewById(R.id.emergency_road_service);
        issue_date=rootView.findViewById(R.id.issue_date);
        liabilty_bodily_injury=rootView.findViewById(R.id.liabilty_bodily_injury);
        registered_state=rootView.findViewById(R.id.registered_state);
        liabilty_prop_damage=rootView.findViewById(R.id.liabilty_prop_damage);
        license_status=rootView.findViewById(R.id.license_status);
        major_violation=rootView.findViewById(R.id.major_violation);
        minor_violation=rootView.findViewById(R.id.minor_violation);
        policy_effective_date=rootView.findViewById(R.id.policy_effective_date);
        primary_ind=rootView.findViewById(R.id.primary_ind);
        under_insured_mv_pd=rootView.findViewById(R.id.under_insured_mv_pd);
        uninsured_mv_bi=rootView.findViewById(R.id.uninsured_mv_bi);
        MainActivity.actionBar.show();
        new PolicyDetails(MainActivity.policy_id).execute();
        return rootView;
    }

    public  class  PolicyDetails extends AsyncTask<Void,Void, JSONArray> {
        String policy_id;


        public PolicyDetails(String policy_id){
            this.policy_id = policy_id;
        }


        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("policy_id", policy_id)
                        .build();


                Request request = new Request.Builder()
                        .url(getContext().getResources().getString(R.string.api_server)+"get_policy_details")
                        .post(requestBody)
                        .build();

                Log.e("Request:","Get_Policy_Details");

                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
                Log.e("Response",sb.toString());

                JSONArray myresponse= new JSONArray(sb.toString());

                Log.e("Request",response.message());
                return myresponse;



            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {
                Log.e("JSON_POL_DET",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray policy_details) {
            try {

                if (policy_details==null)
                    Toast.makeText(getContext(),"Policy Doesn't Exist",Toast.LENGTH_LONG).show();
                else {
                    JSONObject jsonobject = null;
                    jsonobject = policy_details.getJSONObject(0);
                    policy_holder_name.setText(jsonobject.getString("policy_holder_name"));
                    active_since.setText(jsonobject.getString("active_since"));
                    policy_expiry_date.setText(jsonobject.getString("policy_expiry_date"));
                    agent_id.setText(jsonobject.getString("agent_id"));
                    at_fault_accident.setText(jsonobject.getString("at_fault_accident"));
                    car_rental_travel_exp.setText(jsonobject.getString("car_rental_travel_exp"));
                    collision_ded.setText(jsonobject.getString("collision_ded"));
                    drivercomprehensive_ded_w_glass_name.setText(jsonobject.getString("drivercomprehensive_ded_w_glass_name"));
                    emergency_road_service.setText(jsonobject.getString("emergency_road_service"));
                    issue_date.setText(jsonobject.getString("issue_date"));
                    liabilty_bodily_injury.setText(jsonobject.getString("liabilty_bodily_injury"));
                    registered_state.setText(jsonobject.getString("registered_state"));
                    pol_id.setText(jsonobject.getString("policy_id"));
                    liabilty_prop_damage.setText(jsonobject.getString("liabilty_prop_damage"));
                    license_status.setText(jsonobject.getString("license_status"));
                    major_violation.setText(jsonobject.getString("major_violation"));
                    minor_violation.setText(jsonobject.getString("minor_violation"));
                    policy_effective_date.setText(jsonobject.getString("policy_effective_date"));
                    primary_ind.setText(jsonobject.getString("primary_ind"));
                    under_insured_mv_pd.setText(jsonobject.getString("under_insured_mv_pd"));
                    uninsured_mv_bi.setText(jsonobject.getString("uninsured_mv_bi"));
                }
            }catch (JSONException e) {
                Log.e("Exception_JSON",e.getMessage());
            }
            catch (Exception e){
                Log.e("Exception",e.getMessage());
            }

            super.onPostExecute(policy_details);
        }
    }
}
