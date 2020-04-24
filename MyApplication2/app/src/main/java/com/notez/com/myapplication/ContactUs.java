package com.notez.com.myapplication;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactUs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactUs extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactUs() {
        // Required empty public constructor
    }
    public static TextView tel_line_1, tel_line_2, cell_phone, address, email, website;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactUs.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactUs newInstance(String param1, String param2) {
        ContactUs fragment = new ContactUs();
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
        View rootView=inflater.inflate(R.layout.fragment_contact_us, container, false);
        tel_line_1=rootView.findViewById(R.id.tel_line_1);
        tel_line_2=rootView.findViewById(R.id.tel_line_2);
        cell_phone=rootView.findViewById(R.id.cell_phone);
        address=rootView.findViewById(R.id.address);
        email=rootView.findViewById(R.id.email);
        website=rootView.findViewById(R.id.website);

        tel_line_1.setText("201-677-5423");
        tel_line_2.setText("201-677-5424");
        cell_phone.setText("777-987-1357");
        address.setText("123, Main Street, Hillsborough, Tampa, Florida-33601");
        email.setText("customer_care@xyz.com");
        website.setText("xyz.com/customer_care");
        MainActivity.actionBar.show();

        return rootView;
    }
}
