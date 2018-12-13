package com.blackondev.my.gallery;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    HashMap<Integer, MediaModel> map = new HashMap<>();
    private TextView txtFileCount;
    Spinner spinner;

    public interface ImageSelectionListener {
        public boolean onImageSelected(int position, boolean isSelected);
    }

    LoaderManager loaderManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinnerFolders);
        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        txtFileCount = findViewById(R.id.txtFileCount);


    }

    String mCurFilter;

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri baseUri;
        String select = "";
        String[] selectionArgs = new String[]{"500", "200", "0"};

        baseUri = MediaStore.Images.Media.getContentUri("external");


        if (id == 0) {
            select = "(" + MediaStore.Images.ImageColumns.SIZE + ">? AND "
                    + MediaStore.Images.ImageColumns.HEIGHT + ">? AND " + MediaStore.Images.ImageColumns.BUCKET_ID
                    + ">?  )";

        } else if (id == 1) {
            selectionArgs = new String[]{"500", "200", "0", args.getString("data")};
            select = "(" + MediaStore.Images.ImageColumns.SIZE + ">? AND "
                    + MediaStore.Images.ImageColumns.HEIGHT + ">? AND " + MediaStore.Images.ImageColumns.BUCKET_ID
                    + ">? AND " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=? )";
        }

        return new CursorLoader(this, baseUri,
                null, select, selectionArgs, MediaStore.Images.ImageColumns._ID + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        CustomTask customTask = new CustomTask(data, loader.getId());
        customTask.execute();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    ArrayList<MediaModel> mediaModels = new ArrayList<>();

    class CustomTask extends AsyncTask<Integer, String, ArrayList<MediaModel>> implements ImageSelectionListener {
        private Cursor data;
        private int id;
        private ProgressDialog progressDialog;

        public CustomTask(Cursor cursor, int id) {

            this.data = cursor;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();


        }

        HashMap<String, String> mapFolders = new HashMap<>();

        @Override
        protected ArrayList<MediaModel> doInBackground(Integer... voids) {
            mediaModels = new ArrayList<>();


            for (int i = 0; i < data.getCount(); i++) {
                data.moveToPosition(i);

                MediaModel mediaModel = new MediaModel();
                mediaModel.set_data(data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                mediaModel.set_display_name(data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
                mediaModel.setBucket_display_name(data.getString(data.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                if (id == 0) {
                    mapFolders.put(mediaModel.getBucket_display_name(), mediaModel.getBucket_display_name());
                }
                mediaModels.add(mediaModel);

            }

            return mediaModels;
        }

        String[] dataFolders;
        int check = 0;
        boolean isAdapterSet = true;
        @Override
        protected void onPostExecute(ArrayList<MediaModel> mediaModels) {
            if (id == 0) {

                dataFolders = new String[mapFolders.size() + 1];
                dataFolders[0] = "All";
                int count = 1;
                for (String s : mapFolders.keySet()) {
                    dataFolders[count] = mapFolders.get(s);
                    count++;

                }
                ArrayAdapter<String> adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, dataFolders);
                check = 0;
                spinner.setAdapter(adapter);
                isAdapterSet = true;
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        if (!isAdapterSet) {
                            Bundle bundle = new Bundle();
                            bundle.putString("data", adapterView.getItemAtPosition(i).toString());
                            if (i != 0) {
                                loaderManager.restartLoader(1, bundle, MainActivity.this);
                            } else {
                                loaderManager.restartLoader(0, null, MainActivity.this);

                            }
                        }else{
                            isAdapterSet = false;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


            }

          /*  loaderManager.destroyLoader(0);
            loaderManager.destroyLoader(1);*/
            map = new HashMap<>();
            txtFileCount.setText("Selected (" + map.size() + "/4)");
            mediaAdapter = new MediaAdapter(MainActivity.this, mediaModels, this);
            recyclerView.setAdapter(mediaAdapter);
            progressDialog.dismiss();
            super.onPostExecute(mediaModels);
        }

        @Override
        public boolean onImageSelected(int position, boolean isSelected) {
            boolean b = true;
            if (isSelected && map.size() < 4) {

                map.put(position, mediaModels.get(position));
            } else {

                if (isSelected && map.size() >= 4) {

                    Toast.makeText(MainActivity.this, "You can only select maximum 4 images", Toast.LENGTH_LONG).show();
                    b = false;
                } else {

                    map.remove(position);
                    b = false;
                }
            }
            txtFileCount.setText("Selected (" + map.size() + "/4)");
            Log.d("Media Container", map.toString());

            return b;
        }
    }
}
