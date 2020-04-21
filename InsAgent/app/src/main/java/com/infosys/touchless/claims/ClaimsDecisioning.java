package com.infosys.touchless.claims;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import Models.ClaimsModel;
import Models.GeoEvents;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClaimsDecisioning extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ImageView H_image;
    public static Context mContext;
    public static int total_events=0;
    public  static CheckBox isFraud;
    public static String policy_id="";
    public static List<GeoEvents> mgeoEvents=new ArrayList<>();


    public static LinearLayout st_options;
    public static TextView C_estimations,claim_id,approve,reject,hold,incident_location,incident_description,incident_date;

    public static CardView events;
    public static RelativeLayout event_1,event_2;
    public static ImageView event_1_icon,event_2_icon;
    public static TextView event_1_title,event_1_category,event_1_message,event_2_title,event_2_category,event_2_message;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClaimsDecisioning() {
        // Required empty public constructor
    }
    public static ClaimsDecisioning newInstance(String param1, String param2) {
        ClaimsDecisioning fragment = new ClaimsDecisioning();
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
        View rootView=inflater.inflate(R.layout.fragment_claims_decisioning, container, false);
        Log.e("DEbM","called");
        C_estimations=rootView.findViewById(R.id.c_estimations);
        incident_date=rootView.findViewById(R.id.incident_date);
        incident_description=rootView.findViewById(R.id.incident_description);
        incident_location=rootView.findViewById(R.id.incident_location);
        isFraud=rootView.findViewById(R.id.isfraud);
        H_image=rootView.findViewById(R.id.h_image);
        claim_id=rootView.findViewById(R.id.claimid);
        st_options=rootView.findViewById(R.id.st_options);
        approve=rootView.findViewById(R.id.approve);
        reject=rootView.findViewById(R.id.reject);
        hold=rootView.findViewById(R.id.hold);


        events=rootView.findViewById(R.id.events);
        event_1=rootView.findViewById(R.id.event_1);
        event_2=rootView.findViewById(R.id.event_2);
        event_1_icon=rootView.findViewById(R.id.event_1_icon);
        event_2_icon=rootView.findViewById(R.id.event_2_icon);
        event_1_message=rootView.findViewById(R.id.event_1_message);
        event_2_message=rootView.findViewById(R.id.event_2_message);
        event_1_category=rootView.findViewById(R.id.event_1_category);
        event_2_category=rootView.findViewById(R.id.event_2_category);
        event_1_title=rootView.findViewById(R.id.event_1_title);
        event_2_title=rootView.findViewById(R.id.event_2_title);
        events.setVisibility(View.GONE);


        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.estimation_required!=null)
                    new UpdateStatus("APPROVED",MainActivity.estimation_required.getClaimid(),isFraud.isChecked()?"Yes":"No").execute();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.estimation_required!=null)
                    new UpdateStatus("REJECTED",MainActivity.estimation_required.getClaimid(),isFraud.isChecked()?"Yes":"No").execute();
            }
        });
        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.estimation_required!=null)
                    new UpdateStatus("ON HOLD",MainActivity.estimation_required.getClaimid(),isFraud.isChecked()?"Yes":"No").execute();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        mContext=getContext();
        super.onResume();
    }
    public static void callModel(){
        new Auto_Detection(MainActivity.estimation_required,mContext).execute();
        new GetUserAddress(MainActivity.estimation_required.getClaimid()).execute();
        new GetPolicyId(MainActivity.estimation_required.getClaimid()).execute();

        Log.e("DEbM","resume"+MainActivity.estimation_required.getImageurl());

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public static class  Auto_Detection extends AsyncTask<Void,Void, JSONObject> {
        ClaimsModel current;
        String imagename;
        Context context;
        ProgressDialog dialog;
        public Auto_Detection(ClaimsModel current,Context context)
        {
            this.current=current;
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            dialog= new ProgressDialog(context);
            dialog.setMessage("Your message..");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(40, TimeUnit.SECONDS)
                        .writeTimeout(40, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .build();
                Log.e("Request","current.path() "+MainActivity.estimation_required.getImageurl());
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("claim_id",MainActivity.estimation_required.getClaimid())
                        .addFormDataPart("path", MainActivity.estimation_required.getImageurl())
                        .build();




                // RequestBody body = RequestBody.create(JSON, gg.toString());
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:5000/api/v1/resources/auto/detection?location="+MainActivity.estimation_required.getImageurl().replaceAll("\\\\","/"))
                        .post(requestBody)
                        .build();
                Log.e("Request","sent");

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

                JSONObject myresponse= new JSONObject(sb.toString());
                // Log.e("Response",myresponse.getJSONObject("image").toString());
                JSONArray Highlights=myresponse.getJSONArray("image");

                Log.e("Request",response.message());
                return Highlights.getJSONObject(0);


            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {

                Log.e("JE",e.getMessage());
            }
            catch (Exception e){
                Log.e("E",e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute(JSONObject s) {

            try {
                if (s!=null){
                    C_estimations.setText("Part:"+s.getString("part")+"\n"+"Total Cost:"+s.getString("total_cost"));
                    claim_id.setText(current.getClaimid());
                    incident_date.setText(current.getIncident_date());
                    incident_description.setText(current.getOverView());


                    if (current.getStatus().equals("SUBMITTED")||current.getStatus().equals("ON HOLD"))
                        st_options.setVisibility(View.VISIBLE);
                    Log.e("path 1:      ",s.get("labelled_image")+"");
                    String path =s.getString("labelled_image");
                    Log.e("path 2:      ",path);
                    path=path.replaceAll("\\\\","/");
                    Log.e("path 3:      ",path);
                    new Fetch_Highlights(path).execute();}
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            super.onPostExecute(s);
        }
    }

    public static class  Fetch_Highlights extends AsyncTask<Void,Void, Bitmap>{
        String path;
        Bitmap image;

        public Fetch_Highlights(String path){
            this.path=path;
        }


        @Override
        protected Bitmap doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("path", path)
                        .build();


                Request request = new Request.Builder()
                        .url("http://10.0.2.2:5000/api/v1/resources/download/"+path)
                        .post(requestBody)
                        .build();

                Log.e("Request","sent");

                Response response = client.newCall(request).execute();
                Log.e("Response",response.message()+"      "+response.body());
                InputStream inputStream = response.body().byteStream();
                image = BitmapFactory.decodeStream(inputStream);

            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            }
            return image;
        }


        @Override
        protected void onPostExecute(Bitmap s) {
            H_image.setImageBitmap(s);

            H_image.setBackgroundColor(Color.parseColor("#ffffff"));


            Log.e("ImageRequest","Post");

            super.onPostExecute(s);
        }
    }


    public class  UpdateStatus extends AsyncTask<Void,Void, JSONObject>{
        String status,claimid,isfraud;
        public UpdateStatus(String status,String claimid,String isfraud){
            this.status=status;
            this.claimid=claimid;
            this.isfraud=isfraud;
        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("claimid",claimid)
                        .addFormDataPart("status",status)
                        .addFormDataPart("is_fraud", isfraud)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"update_status")
                        .post(requestBody)
                        .build();

                Log.e("Request","sent");

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

                JSONObject myresponse= new JSONObject(sb.toString());

                Log.e("Request",response.message());
                return myresponse;

            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();

            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject s) {
            //  i2.setImageBitmap(s);
            Log.e("Status Update","Post");
            try{
                if(s.getString("Status Updated").equals("true")){
                    Toast.makeText(getContext(),"Claim with Claim ID: "+claimid+" is "+status,Toast.LENGTH_LONG).show();
                    st_options.setVisibility(View.GONE);


                }
                else{
                    Toast.makeText(getContext(),"Status update Failed!",Toast.LENGTH_LONG).show();

                }
            }
            catch (Exception e){

            }
            super.onPostExecute(s);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public static class  GetUserAddress extends AsyncTask<Void,Void,JSONObject>{
        String claim_id;


        public GetUserAddress(String claim_id){
            this.claim_id = claim_id;
        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("claim_id", claim_id)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"get_customer_address")
                        .post(requestBody)
                        .build();

                Log.e("Request:","Get_Customer_Address");

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

                JSONObject myresponse= new JSONObject(sb.toString());

                Log.e("Request",response.message());
                return myresponse;



            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {
                Log.e("JSON_GET_CUST_ADDR",e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject customer_address) {
            try {

                if (customer_address==null)
                    Toast.makeText(mContext,"Can't Fetch Address",Toast.LENGTH_LONG).show();
                else {
                    String customer_location = customer_address.getString("mailing_address_line_1")+","
                            +customer_address.getString("mailing_address_line_2")+","
                            +customer_address.getString("mailing_state")+","
                            + customer_address.getString("mailing_country");
                    incident_location.setText(customer_location);
                }
            }catch (JSONException e) {
                Log.e("Exception_JSON",e.getMessage());
            }
            catch (Exception e){
                Log.e("Exception",e.getMessage());
            }

            super.onPostExecute(customer_address);
        }
    }

    public static class GetPolicyId extends AsyncTask<Void,Void,JSONObject>{
        String claim_id;


        public GetPolicyId(String claim_id){
            this.claim_id = claim_id;
        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("claim_id", claim_id)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"get_policy_id")
                        .post(requestBody)
                        .build();

                Log.e("Request:","Get_policy_id");

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

                JSONObject myresponse= new JSONObject(sb.toString());

                Log.e("Request",response.message());
                return myresponse;



            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {
                Log.e("JSON_GET_CUST_id",e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject customer_address) {
            try {

                if (customer_address==null)
                    Toast.makeText(mContext,"Can't Fetch Address",Toast.LENGTH_LONG).show();
                else {
                    policy_id = customer_address.getString("policy_id");
                    new GetNearbyEvents().execute();
                    }
            }catch (JSONException e) {
                Log.e("Exception_JSON",e.getMessage());
            }
            catch (Exception e){
                Log.e("Exception",e.getMessage());
            }

            super.onPostExecute(customer_address);
        }
    }
    public static class  GetNearbyEvents extends AsyncTask<Void,Void, JSONArray> {
        GeoEvents current;

        public GetNearbyEvents(){
            Log.e("Request_EVENTS","Created");
        }


        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();

                Log.e("Request_EVENTS","Initiated");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("policy_id", policy_id)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"nearby_events")
                        .post(requestBody)
                        .build();
                Log.e("Request_EVENTS","sent");

                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                InputStreamReader isReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isReader);
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!= null){
                    sb.append(str);
                }
                Log.e("Response_EVENTS",sb.toString());

                JSONArray myresponse= new JSONArray(sb.toString());

                Log.e("Request_EVENTS",myresponse.toString());
                return myresponse;


            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {

                Log.e("JE",e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute(JSONArray all_geo_events) {
            try {


                if (all_geo_events==null) {
                    Toast.makeText(mContext, "No nearby Events", Toast.LENGTH_LONG).show();

                }
                else{
                    int t=0;
                    mgeoEvents.clear();
                    for (int i = 0; i < all_geo_events.length(); i++) {
                        JSONObject jsonobject = null;
                        jsonobject = all_geo_events.getJSONObject(i);
                        t++;
                        String latitude = jsonobject.getString("lat");
                        String longitude = jsonobject.getString("long");
                        String title = jsonobject.getString("title");
                        String category = jsonobject.getString("category");
                        String distance = jsonobject.getString("distance");
                        GeoEvents temp = new GeoEvents();
                        temp.setLatitude(latitude);
                        temp.setLongitude(longitude);
                        temp.setTitle(title);
                        temp.setDistance(distance);
                        temp.setCategory(category);
                        mgeoEvents.add(temp);

                    }
                    total_events=t;
                    if(t==0)
                       events.setVisibility(View.GONE);
                    if (t>=1){
                        events.setVisibility(View.VISIBLE);
                        event_1.setVisibility(View.VISIBLE);
                        event_2.setVisibility(View.GONE);
                        event_1_title.setText(mgeoEvents.get(0).getTitle());
                        event_1_category.setText(mgeoEvents.get(0).getCategory().toUpperCase());
                        if(mgeoEvents.get(0).getCategory().equals("severe-weather")){
                            event_1_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_storm));

                            event_1_message.setText("There was a severe weather warning in the locality of the user that might had been the cause of Incident.");
                        }
                        if(mgeoEvents.get(0).getCategory().equals("concerts")){
                            event_1_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_concert));
                           event_1_message.setText("There was a concert happening "+mgeoEvents.get(0).getDistance()+" km away from the incident loction that might had been the cause of Incident.");
                        }
                        if(mgeoEvents.get(0).getCategory().equals("sports")){
                            event_1_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_trophy));
                            event_1_message.setText("There was a sports event happening "+mgeoEvents.get(0).getDistance()+" km away from the incident loction that might had been the cause of Incident.");
                        }

                        if(mgeoEvents.get(0).getCategory().equals("festivals")){
                            event_1_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_festival));
                            event_1_message.setText("There was a festival happening "+mgeoEvents.get(0).getDistance()+" km away from the incident loction that might had been the cause of Incident.");
                        }
                    }
                   if (t==2){
                       event_2.setVisibility(View.VISIBLE);
                        event_2_title.setText(mgeoEvents.get(1).getTitle());
                        event_2_category.setText(mgeoEvents.get(1).getCategory().toUpperCase());
                        if(mgeoEvents.get(1).getCategory().equals("severe-weather")){
                            event_2_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_storm));

                            event_2_message.setText("There was a severe weather warning in the locality of the user that might had been the cause of Incident.");
                        }
                        if(mgeoEvents.get(1).getCategory().equals("concerts")){
                            event_2_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_concert));
                            event_2_message.setText("There was a concert happening "+mgeoEvents.get(1).getDistance()+" km away from the incident loction that might had been the cause of Incident.");
                        }
                        if(mgeoEvents.get(1).getCategory().equals("sports")){
                            event_2_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_trophy));
                            event_2_message.setText("There was a sports event happening "+mgeoEvents.get(1).getDistance()+" km away from the incident loction that might had been the cause of Incident.");
                        }

                        if(mgeoEvents.get(1).getCategory().equals("festivals")){
                            event_2_icon.setBackground(mContext.getResources().getDrawable(R.drawable.ic_festival));
                            event_2_message.setText("There was a festival happening "+mgeoEvents.get(1).getDistance()+" km away from the incident loction that might had been the cause of Incident.");
                        }
                    }

                }

            }catch (JSONException e) {
                Log.e("Exception JSON NEA",e.getMessage());
            }catch (Exception e){
                Log.e("Exception NEARBYEVENTS",e.getMessage());
            }
            super.onPostExecute(all_geo_events);

        }
    }


}

