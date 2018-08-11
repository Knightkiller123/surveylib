package easygov.saral.harlabh.fragments.checkout_pager_fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import easygov.saral.harlabh.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayFragment extends Fragment {



    private EditText etFirstName,etLastNAme,etContact,etDob,etGender,etStreet1,etStreet2,etCity,etZip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_meeting_address, container, false);
       // init(view);
        return view;
    }

   /* private void init(View view) {
        etFirstName= (EditText) view.findViewById(R.id.etFirstName);


        etLastNAme= (EditText) view.findViewById(R.id.etLastNAme);
        etContact= (EditText) view.findViewById(R.id.etContact);
        etDob= (EditText) view.findViewById(R.id.etDob);
        etGender= (EditText) view.findViewById(R.id.etGender);
        etStreet1= (EditText) view.findViewById(R.id.etStreet1);
        etStreet2= (EditText) view.findViewById(R.id.etStreet2);
        etCity= (EditText) view.findViewById(R.id.etCity);
        etZip= (EditText) view.findViewById(R.id.etZip);
    }*/

    public static Fragment newInstance(String s) {
        PayFragment frag=new PayFragment();
        return frag;
    }
}
