package com.example.nativeadinrecyclerview;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LauncherActivity extends Activity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    Dialog myDialog;
    Button next;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_activity);
        next = (Button) findViewById(R.id.nextBtn);
        next.setVisibility(View.GONE);
        Boolean update = false;

        Boolean exit = getIntent().getBooleanExtra("Exit me",false);
        if(exit){
            finish();
            System.exit(0);
        }
        myDialog = new Dialog(this);
        try {
            update = forceUpdate();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        if (update){
            if (isOnline()) {
                final Intent i = new Intent(this, MainActivity.class);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        startActivity(i);
                    }
                }, 1000);

            } else {
                Toast.makeText(LauncherActivity.this,
                        "Please Turn On Your Internet!",
                        Toast.LENGTH_LONG)
                        .show();
                next.setVisibility(View.VISIBLE);

            }
    }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextClick();
            }
        });

    }

    // check version on play store and force update
    public Boolean forceUpdate() throws ExecutionException, InterruptedException {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        GetVersionCode versionChecker = new GetVersionCode(packageInfo.packageName);
        String latestVersion = versionChecker.execute().get();
        if (latestVersion != null){
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                Log.d("Update", "avilable");
                ShowPopup();
                return false;
            }
        }
        return true;
    }

    public void ShowPopup() {
        Button updatebtn;

        myDialog.setContentView(R.layout.custompopup);
        myDialog.setCancelable(false);
        updatebtn = (Button) myDialog.findViewById(R.id.btnupdate);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"CLicked",Toast.LENGTH_LONG).show();
                final String appPackageName = getPackageName(); // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(this,"On Start called",Toast.LENGTH_LONG).show();
    }

    public void NextClick(){
        if(isOnline()){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);

        }else{
            Toast.makeText(LauncherActivity.this,
                    "Please Turn On Your Internet!",
                    Toast.LENGTH_LONG)
                    .show();


        }
    }

    public boolean isOnline() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(LauncherActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(LauncherActivity.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
//            Toast.makeText(LauncherActivity.this,
//                    "Permission already granted",
//                    Toast.LENGTH_SHORT)
//                    .show();
        }
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
//                Toast.makeText(LauncherActivity.this,
//                        "Storage Permission Granted",
//                        Toast.LENGTH_SHORT)
//                        .show();
            }
            else {
//                Toast.makeText(LauncherActivity.this,
//                        "Storage Permission Denied",
//                        Toast.LENGTH_SHORT)
//                        .show();
            }
        }
    }
}
