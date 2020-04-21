package com.notez.com.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import Adapters.ShowClaimsAdapter;
import Models.ClaimsModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MIME_TYPE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RaiseClaim.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RaiseClaim#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RaiseClaim extends Fragment implements DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected LocationManager  mLocationManager;
    protected Location current_loc;

    private Uri mImageUri;
    String firebaseuri;
    private ProgressBar PB;

    File finalFile;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static TextView b1,loc,highlights,incident_date,choose_image;
    EditText overView;
    ImageView i1,i2;

    private OnFragmentInteractionListener mListener;

    public RaiseClaim() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RaiseClaim.
     */
    // TODO: Rename and change types and number of parameters
    public static RaiseClaim newInstance(String param1, String param2) {
        RaiseClaim fragment = new RaiseClaim();
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
    private void startGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 99);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.raise_claim, container, false);

        b1 = (TextView) rootView.findViewById(R.id.bupload);
        highlights = (TextView) rootView.findViewById(R.id.response);
        loc = (TextView) rootView.findViewById(R.id.loc);
        incident_date = (TextView) rootView.findViewById(R.id.incident_date);
        choose_image = (TextView) rootView.findViewById(R.id.choose_image);

        i1 =(ImageView) rootView.findViewById(R.id.i1);
        i2 =(ImageView) rootView.findViewById(R.id.response_image);
        overView = (EditText) rootView.findViewById(R.id.overview);
        i1.setVisibility(View.GONE);
        incident_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

       /* mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");*/

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClaimsModel claim = new ClaimsModel(overView.getText().toString().trim(),"","SUBMITTED");
                claim.setClaimid("CL"+ System.currentTimeMillis());
                claim.setIncident_date(incident_date.getText().toString());
                new UploadClaim(claim).execute();


               // uploadFile();
            }
        });

        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new GetUserAddress(MainActivity.policy_id).execute();
            }
        });


        choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    startGallery();
                }
            }

        });

        new GetUserAddress(MainActivity.policy_id).execute();
        //getLoc();
        return rootView;
    }

    public void getLoc(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("Location Permission Needed")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        99);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }}
        else {

            mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            current_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (current_loc!=null)
                loc.setText("Longitude: "+ current_loc.getLongitude()+"\n"+"Latitude: "+ current_loc.getLatitude()+"\n");

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                    10, mLocationListener);

        }
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
           current_loc=location;
           loc.setText("Longitude: "+ location.getLongitude()+"\n"+"Latitude: "+ location.getLatitude()+"\n");


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if(resultCode == RESULT_OK) {
            if(requestCode == 99){
                Uri returnUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri tempUri = getImageUri(getActivity().getApplicationContext(),bitmapImage);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                finalFile = new File(getRealPathFromURI(tempUri));

                mImageUri = data.getData();
                i1.setVisibility(View.VISIBLE);
                i1.setImageBitmap(bitmapImage);
                i1.setBackground(null);
            }
        }


}

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



public class  UploadClaim extends AsyncTask<Void,Void,JSONObject>{
    ClaimsModel current;
    String imagename;

    public UploadClaim(ClaimsModel current){
        this.current=current;
    }


    @Override
    protected JSONObject doInBackground(Void... voids) {

        try {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.SECONDS)
                .build();
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        JSONObject claim_jason=new JSONObject();
        claim_jason.put("claimid",current.getClaimid());
        claim_jason.put("overview",current.getOverView());
        claim_jason.put("status",current.getStatus());
        claim_jason.put("incident_date",current.getIncident_date());



            final MediaType MEDIA_TYPE_PNG = MediaType.parse(MIME_TYPE);

        File sourceFile = new File(mImageUri.getPath()+".jpeg");
            Log.e("Request","exists   " + sourceFile.getAbsolutePath()+"    "+mImageUri.getPath()+current.getIncident_date());
        RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", finalFile.getName(), RequestBody.create(MEDIA_TYPE_PNG,finalFile ))
                    .addFormDataPart("policy_id",MainActivity.policy_id)
                    .addFormDataPart("json", claim_jason.toString())
                    .build();

            Log.e("Policy_id",MainActivity.policy_id);



            // RequestBody body = RequestBody.create(JSON, gg.toString());
        Request request = new Request.Builder()
                .url(getContext().getResources().getString(R.string.api_server)+"add_claim")
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

            Log.e("JE",e.getMessage());
        }

       return null;
    }


    @Override
    protected void onPostExecute(JSONObject s) {

        try {
            if (s.getString("Claim Submitted").equals("true")) {
                Log.e("Request", "Post");
                TextView cd_overview, cd_ok, cd_show_claims;
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.confirmation_dialog);
                dialog.setTitle("");
                cd_overview = (TextView) dialog.findViewById(R.id.cd_overview);

                cd_show_claims = (TextView) dialog.findViewById(R.id.cd_show_claims);
                cd_ok = (TextView) dialog.findViewById(R.id.cd_ok);
                String ht = "<b>" + current.getClaimid() + "</b>";
                cd_overview.setText("Your Claim with ClaimID : " + Html.fromHtml(ht) + " has been succesfully submitted. ");
                cd_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                cd_show_claims.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        MainActivity.viewPager.setCurrentItem(1);

                    }
                });
                dialog.show();
            }
            else
                Toast.makeText(getContext(),"Sorry,Claim Submission Failed",Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e)
        {

        }

        super.onPostExecute(s);
    }
}

public class  Fetch_Highlights extends AsyncTask<Void,Void,Bitmap>{
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
                        .url(getContext().getResources().getString(R.string.api_server)+"download/"+path)
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
                i2.setImageBitmap(s);
                Log.e("ImageRequest","Post");

            super.onPostExecute(s);
        }
    }

public class  GetUserAddress extends AsyncTask<Void,Void,JSONObject>{
        String policy_id;


        public GetUserAddress(String policy_id){
            this.policy_id=policy_id;
        }


        @Override
        protected JSONObject doInBackground(Void... voids) {

            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("policy_id", policy_id)
                        .build();


                Request request = new Request.Builder()
                        .url(getContext().getResources().getString(R.string.api_server)+"get_customer_address")
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
                    Toast.makeText(getContext(),"Can't Fetch Address",Toast.LENGTH_LONG).show();
                else {
                    String customer_location = customer_address.getString("mailing_address_line_1")+","
                            +customer_address.getString("mailing_address_line_2")+","
                            +customer_address.getString("mailing_state")+","
                            + customer_address.getString("mailing_country");
                    loc.setText(customer_location);
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




    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                RaiseClaim.this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = ++month + "/" + dayOfMonth + "/" + year;
        incident_date.setText(date);
    }

}