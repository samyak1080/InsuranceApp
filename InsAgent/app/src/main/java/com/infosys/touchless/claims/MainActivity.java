package com.infosys.touchless.claims;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import Adapters.PagerAdapter;
import Models.ClaimsModel;

public class MainActivity extends AppCompatActivity {
    public static Bitmap bitmap;
    public static ViewPager viewPager;
    public static Fragment fragment;
    public static FrameLayout home_screen;
    public static ClaimsModel estimation_required;
    public static ActionBar actionBar;
    String agent_name="Invalid Singh";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout=(TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("My Space"));
        tabLayout.addTab(tabLayout.newTab().setText("Show Claims"));
        tabLayout.addTab(tabLayout.newTab().setText("Claim Decisioning"));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            agent_name= extras.getString("agent_name");

        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        home_screen=findViewById(R.id.home_screen);

        final Fragment mhomescreen=new HomeScreen();
        Bundle args = new Bundle();
        args.putString("agent_name",agent_name );
        mhomescreen.setArguments(args);
        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_screen,mhomescreen);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();




        viewPager=(ViewPager) findViewById(R.id.view_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.app_action_bar,null);
        actionBar = ((AppCompatActivity) this).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(0xfffffff));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);
        ImageView homeButton=findViewById(R.id.home_butn);
        TextView usrnm=findViewById(R.id.usrnm);
        usrnm.setText(agent_name);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.settings:
                break;
            case R.id.sign_out:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
             }
        return true;
    }


}
