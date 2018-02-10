package com.example.ggavi.registeration.ahn3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ggavi.registeration.R;

public class open2_PlaceThirdActivity3 extends AppCompatActivity {
    int palace[] = {R.string.changdeok,R.string.changyeong,R.string.deoksung,R.string.gyeongbok,
            R.string.gyeonghui,R.string.jongmyo};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open2_place_thrid_informaton_layout);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        int imgId = intent.getIntExtra("imgId",0);
        int palaceId= palace[position];
        ImageView img = (ImageView)findViewById(R.id.image);
        TextView txt = (TextView)findViewById(R.id.text);
        img.setBackgroundResource(imgId);
        txt.setText(palaceId);

    }

}