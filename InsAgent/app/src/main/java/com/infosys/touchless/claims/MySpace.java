package com.infosys.touchless.claims;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.List;

import Adapters.ShowClaimsAdapter;
import Models.ClaimsModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;


public class MySpace extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mRecyclerView;
    private ShowClaimsAdapter mAdapter;
    private int SUBCOUNT=0,APPCOUNT=0,OHCOUNT=0,REJCOUNT=0;
    public TextView Subnoti,appnoti,ohnoti,rejnoti;
    public CardView SUBMITTED,APPROVED,REJECTED,ON_HOLD;

    private List<ClaimsModel> mClaims;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MySpace() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MySpace newInstance(String param1, String param2) {
        MySpace fragment = new MySpace();
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
        View rootView= inflater.inflate(R.layout.fragment_my_space, container, false);
        SUBMITTED =rootView.findViewById(R.id.Submitted);
        Subnoti=rootView.findViewById(R.id.SUBNOTI);
        APPROVED =rootView.findViewById(R.id.APPROVED);
        appnoti=rootView.findViewById(R.id.APPNOTI);
        ON_HOLD=rootView.findViewById(R.id.ON_HOLD);
        ohnoti=rootView.findViewById(R.id.OHNOTI);
        REJECTED =rootView.findViewById(R.id.REJECTED);
        rejnoti=rootView.findViewById(R.id.REJNOTI);

        SUBMITTED.setVisibility(View.GONE);
        APPROVED.setVisibility(View.GONE);
        ON_HOLD.setVisibility(View.GONE);
        REJECTED.setVisibility(View.GONE);

        mClaims= new ArrayList<>();
        new RetrieveAll().execute();

        Subnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)  getActivity()).viewPager.setCurrentItem(1);
            }
        });
        appnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)  getActivity()).viewPager.setCurrentItem(1);
            }
        });
        rejnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)  getActivity()).viewPager.setCurrentItem(1);
            }
        });
        ohnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)  getActivity()).viewPager.setCurrentItem(1);
            }
        });




        return  rootView;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public class  RetrieveAll extends AsyncTask<Void,Void, JSONArray> {
        ClaimsModel current;
        String imagename;

        public RetrieveAll(){
        }


        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();


                final MediaType MEDIA_TYPE_PNG = MediaType.parse(MIME_TYPE);

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
                        .url(getContext().getResources().getString(R.string.api_server)+"retrieve_all_claims")
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

                JSONArray myresponse= new JSONArray(sb.toString());

                Log.e("Request",response.message());
                return myresponse;


            } catch (IOException e) {
                Log.e("IO",e.getMessage());
            } catch (JSONException e) {

                Log.e("JE",e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute(JSONArray all_claims) {
            try {
                mClaims.clear();
                SUBCOUNT=0;
                APPCOUNT=0;
                REJCOUNT=0;
                OHCOUNT=0;
                if(all_claims==null){
                 Toast.makeText(getContext(),"No nw Claims",Toast.LENGTH_LONG).show();

                }
                else{
                for (int i = 0; i < all_claims.length(); i++) {
                    JSONObject jsonobject = null;
                    jsonobject = all_claims.getJSONObject(i);

                    String claimid = jsonobject.getString("claimid");
                    String overview = jsonobject.getString("overview");
                    String path = jsonobject.getString("path");
                    String st = jsonobject.getString("status");
                    ClaimsModel temp = new ClaimsModel();
                    temp.setStatus(st);
                    temp.setClaimid(claimid);
                    temp.setImageurl(path);
                    temp.setOverView(overview);
                    mClaims.add(temp);
                    if (temp.getStatus().equals("SUBMITTED"))
                        SUBCOUNT += 1;
                    else if (temp.getStatus().equals("APPROVED"))
                        APPCOUNT += 1;
                    else if (temp.getStatus().equals("ON HOLD"))
                        OHCOUNT += 1;
                    else if (temp.getStatus().equals("REJECTED"))
                        REJCOUNT += 1;

                    if(SUBCOUNT>0){
                        SUBMITTED.setVisibility(View.VISIBLE);
                        Subnoti.setText(SUBCOUNT + " new claims Submitted");

                    }
                    if(APPCOUNT>0){
                        APPROVED.setVisibility(View.VISIBLE);
                        appnoti.setText(APPCOUNT +  " Approved Claims waiting for Confirmation from Client");

                    }if(OHCOUNT>0){
                        ON_HOLD.setVisibility(View.VISIBLE);
                        ohnoti.setText(OHCOUNT + " Claims are put On Hold");

                    }if(REJCOUNT>0){
                        REJECTED.setVisibility(View.VISIBLE);
                        rejnoti.setText(REJCOUNT + " CLaims are Rejected");

                    }
                }

            }

                Log.e("Response",all_claims.toString());

            }catch (JSONException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                Log.e("Exception",e.getMessage());

            }

            super.onPostExecute(all_claims);

        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


}
