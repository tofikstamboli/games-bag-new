package com.example.nativeadinrecyclerview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewFragment extends Fragment implements RecyclerViewAdapter.ClickListen {

    // List of Native ads and MenuItems that populate the RecyclerView.
    private List<Object> mRecyclerViewItems;
    private static final int STORAGE_PERMISSION_CODE = 101;
    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);

        MainActivity activity = (MainActivity) getActivity();
        mRecyclerViewItems = activity.getRecyclerViewItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        RecyclerView.Adapter adapter = new RecyclerViewAdapter(getActivity(), mRecyclerViewItems,this);
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void noticeClick(int pos,String url) {

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)){
            if(isOnline()) {
                Toast.makeText(this.getContext(), "Clicked at " + url, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this.getContext(), "Please Connect to internet !" + url, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this.getContext(),"You Denied Storage permissions ! Please clear app data or reinstall",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isOnline() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false; }

        return  connected;
    }

    // Function to check and request permission.
    public boolean checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { permission },
                    requestCode);
            return false;
        }
        else {
            Toast.makeText(getContext(),
                    "Permission already granted",
                    Toast.LENGTH_SHORT)
                    .show();

        }
        return true;
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);


        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getContext(),
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}