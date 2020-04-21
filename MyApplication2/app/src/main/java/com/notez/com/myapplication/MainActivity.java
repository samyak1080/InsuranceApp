package com.notez.com.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Adapters.PagerAdapter;
import Models.ClaimsModel;
import Models.GeoEvents;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements RaiseClaim.OnFragmentInteractionListener,Damage_estimator.OnFragmentInteractionListener{
    public static Bitmap bitmap;
    public Fragment fragment;
    public  static ViewPager viewPager;
    public static FrameLayout home_screen;
    public static ClaimsModel estimation_required;
    public static ActionBar actionBar;
    String user_name ="Invalid Singh";
    public static int total_events=0;
    public static String policy_id="";
    public List<GeoEvents> mgeoEvents=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Raise Claims"));
        tabLayout.addTab(tabLayout.newTab().setText("Show Claims"));
        tabLayout.addTab(tabLayout.newTab().setText("Damage Estimator"));
        tabLayout.addTab(tabLayout.newTab().setText("My Space"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user_name = extras.getString("user_name");
            policy_id= extras.getString("policy_id");



        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        home_screen=findViewById(R.id.home_screen);
        home_screen.setVisibility(View.VISIBLE);

        final android.app.Fragment mhomescreen=new HomeScreen();
        Bundle args = new Bundle();
        args.putString("user_name", user_name);
        mhomescreen.setArguments(args);
        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_screen,mhomescreen);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();



        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.app_action_bar,
                null);

        // Set up your ActionBar
        actionBar = ((AppCompatActivity) this).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xfffffff));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);
        ImageView homeButton=findViewById(R.id.home_butn);

        TextView usrnm=findViewById(R.id.usrnm);
        usrnm.setText(user_name);

        viewPager=(ViewPager) findViewById(R.id.view_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);



        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        new GetNearbyEvents().execute();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_screen.setVisibility(View.VISIBLE);
                actionBar.hide();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.home_screen,mhomescreen);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                //adapter.notifyDataSetChanged();
                Log.e("SLIDER", String.valueOf(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
             } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public class  GetNearbyEvents extends AsyncTask<Void,Void, JSONArray> {
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
                        .addFormDataPart("policy_id", MainActivity.policy_id)
                        .build();


                Request request = new Request.Builder()
                        .url(MainActivity.this.getResources().getString(R.string.api_server)+"nearby_events")
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
                    Toast.makeText(MainActivity.this, "No nearby Events", Toast.LENGTH_LONG).show();

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
                        GeoEvents temp = new GeoEvents();
                        temp.setLatitude(latitude);
                        temp.setLongitude(longitude);
                        temp.setTitle(title);
                        temp.setCategory(category);
                        mgeoEvents.add(temp);

                    }
                    total_events=t;
                    if(t>0)
                        HomeScreen.my_space_bell.setVisibility(View.VISIBLE);

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