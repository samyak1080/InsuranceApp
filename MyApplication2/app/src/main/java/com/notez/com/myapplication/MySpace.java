package com.notez.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
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
import java.util.Locale;

import Adapters.MySpaceAdapter;
import Models.ClaimsModel;
import Models.GeoEvents;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MySpace.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MySpace#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySpace extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mRecyclerView;
    private MySpaceAdapter mAdapter;
    TextView event_noti;
    CardView geoEvents;
    private int SUBCOUNT=0,APPCOUNT=0;

    private List<ClaimsModel> mClaims;
    private List<GeoEvents> mgeoEvents;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MySpace() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MySpace.
     */
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
        View rootView= inflater.inflate(R.layout.fragment_my_space, container, false);

        mRecyclerView = rootView.findViewById(R.id.notirecy);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        geoEvents=rootView.findViewById(R.id.geo_events);
        event_noti=rootView.findViewById(R.id.event_noti);


        //mProgressCircle = findViewById(R.id.progress_circle);

        mClaims= new ArrayList<>();
        mgeoEvents=new ArrayList<>();
        if (MainActivity.total_events==0)
            geoEvents.setVisibility(View.GONE);
        else
        {
            geoEvents.setVisibility(View.VISIBLE);
            event_noti.setText("There are total "+MainActivity.total_events+" Events happenning in your locality.Please drive safe and try to avoid traffic.");

        }
        // new GetNearbyEvents().execute();
        new RetrieveAll().execute();
        return rootView;
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

                if (all_claims==null)
                    Toast.makeText(getContext(),"No new Claims",Toast.LENGTH_LONG).show();
                else{
                    mClaims.clear();
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

                    }
                }
                mAdapter = new MySpaceAdapter(getActivity(), mClaims);
                mRecyclerView.setAdapter(mAdapter);

            }catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                Log.e("Exception",e.getMessage());
            }

//            Log.e("response",all_claims.toString());
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

   /* public class  GetNearbyEvents extends AsyncTask<Void,Void, JSONArray> {
        GeoEvents current;

        public GetNearbyEvents(){
        }


        @Override
        protected JSONArray doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();


                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("customer_id", MainActivity.customer_id)
                        .build();


                Request request = new Request.Builder()
                        .url(getContext().getResources().getString(R.string.api_server)+"nearby_events")
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
        protected void onPostExecute(JSONArray all_geo_events) {
            try {


                if (all_geo_events==null) {
                    Toast.makeText(getContext(), "No nearby Events", Toast.LENGTH_LONG).show();
                    geoEvents.setVisibility(View.GONE);
                }
                else{
                    int t=0;
                    mgeoEvents.clear();
                    for (int i = 0; i < all_geo_events.length(); i++) {
                        JSONObject jsonobject = null;
                        jsonobject = all_geo_events.getJSONObject(i);
                        t++;
                        String latitude = jsonobject.getString("latitude");
                        String longitude = jsonobject.getString("longitude");
                        String title = jsonobject.getString("title");
                        String category = jsonobject.getString("category");
                        GeoEvents temp = new GeoEvents();
                        temp.setLatitude(latitude);
                        temp.setLongitude(longitude);
                        temp.setTitle(title);
                        temp.setCategory(category);
                        mgeoEvents.add(temp);

                    }
                    if (t==0)
                        geoEvents.setVisibility(View.GONE);
                    else
                    {
                        geoEvents.setVisibility(View.VISIBLE);
                        event_noti.setText("There are total "+t+" Events happenning in your locality.Please drive safe and try to avoid traffic.");

                    }
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                Log.e("Exception",e.getMessage());
            }
      super.onPostExecute(all_geo_events);

        }
    }
*/
}
