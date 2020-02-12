package com.ROSJoyController.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ROSJoyController.R;
import com.ROSJoyController.activities.MainActivity;
import com.jaredrummler.cyanea.app.CyaneaFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class ROS_Settings_Fragment extends CyaneaFragment {
    private Toolbar toolbar;
    public static String ROS_IP = "";
    private Toast mToast;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setup the listener for the fragment B
        ((MainActivity) getActivity()).setToolbar(toolbar);
    }

    @Override
    public void onDestroyView() {
        ((MainActivity) getActivity()).setToolbar(null);
        super.onDestroyView();
    }
    Button   mButton;
    EditText mEdit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_ros_settings, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        View headerView = v.findViewById(R.id.headerBackground);

        mButton = v.findViewById(R.id.button1);
        mEdit   = v.findViewById(R.id.rosuri);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.v("EditText", mEdit.getText().toString());
                ROS_IP = mEdit.getText().toString();
                sayToast("IP Address saved");

            }
        });



        //githubCat.setOnClickListener(v1 -> {
        //    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_link)));
        //    startActivity(browserIntent);
        //});

        //donateBtn.setOnClickListener(v1 -> {
        //    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donateLink)));
        //    startActivity(browserIntent);
        //});

        return v;
    }
    private void sayToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        mToast.show();
    }
}
