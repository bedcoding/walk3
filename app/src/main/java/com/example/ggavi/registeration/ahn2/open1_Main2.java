package com.example.ggavi.registeration.ahn2;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.ggavi.registeration.R;


public class open1_Main2 extends AppCompatActivity {
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
        MosquitoFragment mosquitoFragment = new MosquitoFragment();

        transaction.replace(R.id.content_frame, mosquitoFragment);
        transaction.commit();
    }

}
