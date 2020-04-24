package com.notez.com.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import Models.ClaimsModel;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClaimEstimation extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ImageView H_image;
    public static Context mContext;
    public static TextView C_estimations,claim_id, deductible, customer_amount;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClaimEstimation() {
        // Required empty public constructor
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
    public static ClaimEstimation newInstance(String param1, String param2) {
        ClaimEstimation fragment = new ClaimEstimation();
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
        View rootView=inflater.inflate(R.layout.fragment_claim_estimation, container, false);
        Log.e("DEbM","called");
        C_estimations=rootView.findViewById(R.id.c_estimations);
        H_image=rootView.findViewById(R.id.h_image);
        claim_id=rootView.findViewById(R.id.claimid);
        deductible = rootView.findViewById(R.id.deductible_amount);
        customer_amount = rootView.findViewById(R.id.amount_customer);

        mContext=getContext();
        return rootView;
    }

    @Override
    public void onResume() {
        mContext=getContext();
        super.onResume();


    }
    public static void callModel(){
        new Auto_Detection(MainActivity.estimation_required,mContext).execute();
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
                        .url("http://10.0.2.2:5000/api/v1/resources/auto/detection?location="+MainActivity.estimation_required.getImageurl().replaceAll("\\\\", "/")) .post(requestBody)
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
            Log.e("path 1:      ",s.get("labelled_image")+"");

            String path =s.getString("labelled_image");
            Log.e("path 2:      ",path);
            path=path.replaceAll("\\\\","/");
            Log.e("path 3:      ",path);
            new ClaimAmountDetails(MainActivity.policy_id, current.getClaimid()).execute();
            new Fetch_Highlights(path).execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            dialog.dismiss();
            super.onPostExecute(s);
        }
    }




    public static class  ClaimAmountDetails extends AsyncTask<Void,Void, JSONArray> {
        String policy_id;
        String claim_id;

        public ClaimAmountDetails(String policy_id, String claim_id){
            this.policy_id = policy_id;
            this.claim_id = claim_id;
        }


        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("policy_id", policy_id)
                        .addFormDataPart("claim_id", claim_id)
                        .build();


                Request request = new Request.Builder()
                        .url(mContext.getResources().getString(R.string.api_server)+"get_amount_by_customer")
                        .post(requestBody)
                        .build();

                Log.e("Request:","Get_Amount_By_Customer");

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
                Log.e("JSON_CL_AMT_DET",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray claim_amount_details) {
            try {
                if (claim_amount_details!=null)
                    {
                    JSONObject jsonobject = null;
                    jsonobject = claim_amount_details.getJSONObject(0);
                    deductible.setText(jsonobject.getString("deductible"));
                    customer_amount.setText(jsonobject.getString("amount_to_be_paid"));
                }
            }catch (JSONException e) {
                Log.e("Exception_JSON",e.getMessage());
            }
            catch (Exception e){
                Log.e("Exception",e.getMessage());
            }

            super.onPostExecute(claim_amount_details);
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
                        .url(mContext.getResources().getString(R.string.api_server)+"download/"+path)
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
}

