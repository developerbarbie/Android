package com.isaacson.josie.jisaacsonlab9;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener{

    ArrayList<Manufacturer> manufacturers;
    DbxChooser mChooser;
    Button mChooserButton;
    private static final int DBX_CHOOSER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChooser = new DbxChooser("tohsxizl1f3kgoh");

        mChooserButton = (Button) findViewById(R.id.buttonDropBox);
        mChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChooser.forResultType(DbxChooser.ResultType.FILE_CONTENT)
                        .launch(MainActivity.this, DBX_CHOOSER_REQUEST);
            }
        });

        if(savedInstanceState != null){
            manufacturers = (ArrayList) savedInstanceState.getSerializable("manuList");
        }else{
            manufacturers = new ArrayList<>();
            boolean success = parseFile("vehicles.txt");
            if(!success){
                Toast.makeText(this, "Parse Failed.", Toast.LENGTH_SHORT).show();
            }
        }


        Adapter adapter = new Adapter(this, manufacturers);
        ExpandableListView eview = findViewById(R.id.eListView);
        eview.setAdapter(adapter);

        eview.setOnChildClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DBX_CHOOSER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                Log.d("main", "Link to selected file: " + result.getLink());
                Uri uri = result.getLink();
                ContentResolver resolver = this.getContentResolver();
                try {
                    InputStream inputStream = resolver.openInputStream(uri);
                    manufacturers.clear();
                    boolean success = parseFileIS(inputStream);
                    Adapter adapter = new Adapter(this, manufacturers);
                    ExpandableListView eview = findViewById(R.id.eListView);
                    eview.setAdapter(adapter);
                    if(!success){
                        Toast.makeText(this, "Parse Failed.", Toast.LENGTH_SHORT).show();
                    }

                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "No file found.", Toast.LENGTH_SHORT).show();
                }
                // Handle the result
            } else {
                // Failed or was cancelled by the user.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        //save manufacturers here
        savedInstanceState.putSerializable("manuList", manufacturers);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Toast.makeText(this,
                    "Lab 9, Winter 2019, Josie Isaacson",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean parseFile(String filename){
        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream file = assetManager.open(filename);
            parseFileIS(file);
            return true;
        }catch (IOException e){
            Toast.makeText(this,
                    "File Exception",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
    }

    private boolean parseFileIS(InputStream inStream){
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inStream));
            String line = fileReader.readLine();
            while(line != null){
                String[] lineParts = line.split(",");
                Manufacturer newManu = new Manufacturer(lineParts[0]);
                for(int i = 1; i < lineParts.length; i++){
                    newManu.addModel(lineParts[i]);
                }
                manufacturers.add(newManu);
                line = fileReader.readLine();
            }
            inStream.close();
            return true;

        }catch (IOException e){
            Toast.makeText(this,
                    "File Exception",
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(this,
                "Make: " + manufacturers.get(groupPosition).getName() + "\nModel: " + manufacturers.get(groupPosition).getModelName(childPosition),
                Toast.LENGTH_SHORT)
                .show();

        return false;
    }
}

