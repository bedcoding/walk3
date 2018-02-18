package com.example.ggavi.registeration.ahn2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.ggavi.registeration.R;
import com.tsengvn.typekit.TypekitContextWrapper;



public class open1_Main1 extends AppCompatActivity {
    public static DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open1_weather_main);

        dbHelper = DbHelper.getsInstance(this);
        if(savedInstanceState == null){
            dbHelper = DbHelper.getsInstance(this);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();   //최초화면
        WeatherFragment weatherFragment = new WeatherFragment();

        transaction.replace(R.id.content_frame, weatherFragment);
        transaction.commit();
    }
}
