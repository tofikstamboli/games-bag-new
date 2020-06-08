package com.example.nativeadinrecyclerview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewFragment extends Fragment implements RecyclerViewAdapter.ClickListen {

    String serverFilePath = "";
    String localPath = "";
    String filename = "";
    String fileUrl = "";
    String folder_name = "";
    private ProgressDialog progress;
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
    public void noticeClick(int pos,String url,String folder_name,String file_name) {

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE)){
            if(isOnline()) {
                Toast.makeText(this.getContext(), "Clicked at " + url, Toast.LENGTH_LONG).show();
                this.serverFilePath = url;
                this.filename = file_name;
                this.folder_name = folder_name;
                StartOperation();
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

    public void StartOperation(){
        progress=new ProgressDialog(getContext());
        progress.setMessage("Loading Content ...!\nOnly for once ... !\nPlease wait ...!");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        try {
            localPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.gamesfolder/";
            File root = new File(localPath);
            localPath = root.getAbsolutePath();
            Log.d("FOLDER PATH",localPath);
            if (!root.exists()) {
                root.mkdirs();
            }
            File f = new File(localPath + filename);
            if (!f.exists()) {
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                out.flush();
                out.close();
                Thread t = new Thread(new Runnable() {

                    public void run() {
                        downloadZipFile(serverFilePath,localPath+filename);
                    }
                });
                t.start();
            }else{
                File file = new File(localPath + folder_name);

                if(!file.exists()) {
                    unpackZip(localPath + filename);
                }else {
                    progress.dismiss();
                    sendView();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void sendView(){
        File file = new File(localPath + folder_name);
        fileUrl = file.getAbsolutePath() + "/index.html";
        if(file.exists()) {
            Intent intent = new Intent(getContext(), GameViewController.class);
            intent.putExtra("fileUrl", fileUrl);
            startActivity(intent);
        }else{
            Toast.makeText(getContext(),"Something Went Wrong !!!",Toast.LENGTH_LONG).show();
            file.delete();
            File f = new File(localPath + filename);
            f.delete();
            StartOperation();
        }
    }
    public void downloadZipFile(String urlStr, String destinationFilePath) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("downloadZipFile", "Server ResponseCode=" + connection.getResponseCode() + " ResponseMessage=" + connection.getResponseMessage());
            }

            // download the file
            input = connection.getInputStream();

            Log.d("downloadZipFile", "destinationFilePath=" + destinationFilePath);
            new File(destinationFilePath).createNewFile();
            output = new FileOutputStream(destinationFilePath);

            byte data[] = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (output != null) output.close();
                if (input != null) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connection != null) connection.disconnect();
        }

        File f = new File(destinationFilePath);

        Log.d("downloadZipFile", "f.getParentFile().getPath()=" + f.getParentFile().getPath());
        Log.d("downloadZipFile", "f.getName()=" + f.getName().replace(".zip", ""));
        unpackZip(destinationFilePath);
    }

    public void unpackZip(String filePath) {
        InputStream is;
        ZipInputStream zis;
        try {

            File zipfile = new File(filePath);
            String parentFolder = zipfile.getParentFile().getPath();
            String filename;

            is = new FileInputStream(filePath);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();

                if (ze.isDirectory()) {
                    File fmd = new File(parentFolder + "/" + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(parentFolder + "/" + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch(IOException e) {
            e.printStackTrace();
            progress.dismiss();

        }
        progress.dismiss();
        sendView();
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