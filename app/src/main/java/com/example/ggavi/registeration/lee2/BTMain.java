package com.example.ggavi.registeration.lee2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.ggavi.registeration.R;

public class BTMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lee2_bt_activity_main);
    }
    public void mOnclick(View v){
        Intent intent = new Intent(this, bluegetheart.class);
        startActivity(intent);
    }

}
