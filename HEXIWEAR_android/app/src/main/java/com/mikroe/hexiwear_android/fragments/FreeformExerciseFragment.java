package com.mikroe.hexiwear_android.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikroe.hexiwear_android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreeformExerciseFragment extends Fragment {


    public FreeformExerciseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_freeform_exercise, container, false);
    }

}