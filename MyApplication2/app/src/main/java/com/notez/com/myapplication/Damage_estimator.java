package com.notez.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapters.DamagedPartsAdapter;
import Models.Damaged_part;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Damage_estimator.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Damage_estimator#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Damage_estimator extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    DamagedPartsAdapter madapter;
    List<Damaged_part> modellist = new ArrayList<>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CheckBox head_L_R,head_L_L,tail_L_L,tail_L_R,roof_window,windshield,bumper,hood;
    public Damaged_part Ohead_L_R,Ohead_L_L,Otail_L_L,Otail_L_R,Oroof_window,Owindshield,Obumper,Ohood;
    public TextView estimated_cost;

    private Integer Damage_Estimate =0 ;
    private ArrayList<String> Damaged_part_list= new ArrayList<String>();

    private OnFragmentInteractionListener mListener;

    public Damage_estimator() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Damage_estimator.
     */
    // TODO: Rename and change types and number of parameters
    public static Damage_estimator newInstance(String param1, String param2) {
        Damage_estimator fragment = new Damage_estimator();
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
        View rootView=inflater.inflate(R.layout.fragment_damage_estimator, container, false);
        estimated_cost=(TextView) rootView.findViewById(R.id.estimatedcost);
        head_L_L=(CheckBox) rootView.findViewById(R.id.head_L_L);
        Ohead_L_L=new Damaged_part("Left Head Light",1200);

        head_L_R=(CheckBox) rootView.findViewById(R.id.head_L_R);
        Ohead_L_R=new Damaged_part("Right Head Light",1200);

        tail_L_L=(CheckBox) rootView.findViewById(R.id.tail_L_L);
        Otail_L_L=new Damaged_part("Left Tail Light",1000);

        tail_L_R=(CheckBox) rootView.findViewById(R.id.tail_L_R);
        Otail_L_R=new Damaged_part("Right Tail Light",1000);

        roof_window=(CheckBox) rootView.findViewById(R.id.roof_window);
        Oroof_window=new Damaged_part("Roof Window",3000);

        hood=(CheckBox) rootView.findViewById(R.id.car_hood);
        Ohood=new Damaged_part("Car Hood",4000);

        bumper=(CheckBox) rootView.findViewById(R.id.bumper);
        Obumper=new Damaged_part("Car Bumper",3000);

        windshield=(CheckBox) rootView.findViewById(R.id.wind_shield);
        Owindshield=new Damaged_part("Front Windshield",6000);




        recyclerView = (RecyclerView) rootView.findViewById(R.id.damaged_parts_info);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        madapter = new DamagedPartsAdapter(getActivity(), modellist);
        recyclerView.setAdapter(madapter);

        tail_L_L.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Otail_L_L);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Otail_L_L);
                    madapter.notifyDataSetChanged();
                }

                projectCost();
            }
        });
        tail_L_R.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Otail_L_R);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Otail_L_R);
                    madapter.notifyDataSetChanged();
                }
                projectCost();
            }
        });

        head_L_L.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Ohead_L_L);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Ohead_L_L);
                    madapter.notifyDataSetChanged();
                }
                projectCost();
            }
        });

        head_L_R.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Ohead_L_R);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Ohead_L_R);
                    madapter.notifyDataSetChanged();
                }
                projectCost();

            }
        });

        hood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Ohood);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Ohood);
                    madapter.notifyDataSetChanged();
                }

                projectCost();
            }
        });
        bumper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Obumper);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Obumper);
                    madapter.notifyDataSetChanged();
                }

                projectCost();
            }
        });
        windshield.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Owindshield);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Owindshield);
                    madapter.notifyDataSetChanged();
                }

                projectCost();
            }
        });
        roof_window.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    modellist.add(Oroof_window);
                    madapter.notifyDataSetChanged();
                }else
                {
                    modellist.remove(Oroof_window);
                    madapter.notifyDataSetChanged();
                }

                projectCost();
            }
        });


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

    public  void projectCost(){
        Damage_Estimate=0;
        for (Damaged_part part : modellist)
        {
            Damage_Estimate+=part.getCost();
            estimated_cost.setText("₹ "+Damage_Estimate);
        }
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
}
