package com.desu.mapapp;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.desu.mapapp.fragments.AboutFragment;
import com.desu.mapapp.fragments.MapFragment;
import com.desu.mapapp.fragments.SettingsFragment;

import java.io.IOException;


public class MainActivity extends Activity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DataBaseHelper myDbHelper = new DataBaseHelper(this);//не менять, иначе бд не загружается
        myDbHelper = new DataBaseHelper(this);
        try {
            myDbHelper.createDataBase();
            System.out.println("База данных загружена");
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }


/*
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        try {
            DataBaseHelper.Data data = new DataBaseHelper.Data();
            data._id = "0";
            data.id = "0";
            data.title = "11";
            data.lat = "11";
            data.lng = "11";
            data.address = "11";
            data.phone = "11";
            data.vendor = "11";
            data.network = "11";
            data.features = "11";
            //myDbHelper.SetData(data);
            myDbHelper.ClearData();
        }catch (Exception e) {
            System.out.println("-------------------Exception---------------------\n"+e.toString());
        }
        myDbHelper.close();
*/
        displayView(0 + 1);
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_LONG).show();
                break;
            case 1:
                fragment = new MapFragment();
                break;
            case 2:
                fragment = new SettingsFragment();
                break;
            case 3:
                fragment = new AboutFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    public void onButtonClick(View v){
        //Toast.makeText(getApplicationContext(), "1111", Toast.LENGTH_LONG).show();

    }
}