package easygov.saral.harlabh.fragments.storyboardfrags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import easygov.saral.harlabh.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_information, container, false);

        return view;
    }



}
