package com.notez.com.myapplication;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoadsideAssistance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoadsideAssistance extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static Button roadside_assistance;
    public static TextView name_tab, contact_number_tab, address_tab;

    public RoadsideAssistance() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoadsideAssistance.
     */
    // TODO: Rename and change types and number of parameters
    public static RoadsideAssistance newInstance(String param1, String param2) {
        RoadsideAssistance fragment = new RoadsideAssistance();
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
        View rootView=inflater.inflate(R.layout.fragment_roadside_assistance, container, false);
        roadside_assistance = rootView.findViewById(R.id.roadside_assistance);
        name_tab = rootView.findViewById(R.id.name);
        contact_number_tab = rootView.findViewById(R.id.contact_number);
        address_tab = rootView.findViewById(R.id.address);
        MainActivity.actionBar.show();
        new RoadsideAssistanceDetails().execute();
        return rootView;
    }

//    public void openDialog(){
//        Dialog dialog = new Dialog();
//        //dialog.show(getSupportFragmentManager(), "dialog");
//    }

    public  class  RoadsideAssistanceDetails extends AsyncTask<Void,Void, JSONArray> {
        public RoadsideAssistanceDetails(){
        }
        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new RequestBody() {

                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                    }
                };



                Request request = new Request.Builder()
                        .url(getContext().getResources().getString(R.string.api_server)+"get_roadside_assistance")

                        .post(requestBody)
                        .build();

                Log.e("Request:","Get_Roadside_Assistance");

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
        protected void onPostExecute(JSONArray roadside_assistance_details) {
            try {

                if (roadside_assistance_details==null)
                    Toast.makeText(getContext(),"No Assistance",Toast.LENGTH_LONG).show();
                else {
                    JSONObject jsonobject = null;
                    jsonobject = roadside_assistance_details.getJSONObject(0);
                    final String address = jsonobject.getString("address");
                    final String name = jsonobject.getString("name");
                    final String contact_number = jsonobject.getString("contact_number");
                    roadside_assistance.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Assistance")
                                    .setMessage("Nearest Roadside Assistance from "+ name +
                                            "(" + address + ")" + " is on the way and will " +
                                            "reach you within 5 minutes.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            name_tab.setText(name);
                                            address_tab.setText(address);
                                            contact_number_tab.setText(contact_number);
                                            //Prompt the user once explanation has been shown
                                            ActivityCompat.requestPermissions(getActivity(),
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    99);
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    });
                }
            }catch (JSONException e) {
                Log.e("Exception_JSON",e.getMessage());
            }
            catch (Exception e){
                Log.e("Exception",e.getMessage());
            }

            super.onPostExecute(roadside_assistance_details);
        }
    }


}
