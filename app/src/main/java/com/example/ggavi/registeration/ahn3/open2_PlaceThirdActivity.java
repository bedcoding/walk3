package com.example.ggavi.registeration.ahn3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ggavi.registeration.R;

public class open2_PlaceThirdActivity extends AppCompatActivity {
    int alley[] = {R.string.bossam,R.string.bukchon,R.string.central,R.string.dubu,R.string.garosu,R.string.insa,
            R.string.jaemi,R.string.kstar,R.string.kalguksu,R.string.lamb,R.string.mural,R.string.pajeon,R.string.saengseon,R.string.seochon,
            R.string.seorae,R.string.shoes,R.string.ttaeng,R.string.usadan,R.string.watch,R.string.yonsei};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open2_place_thrid_informaton_layout);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        int imgId = intent.getIntExtra("imgId",0);
        int alleyId= alley[position];
        ImageView img = (ImageView)findViewById(R.id.image);
        TextView txt = (TextView)findViewById(R.id.text);
        img.setBackgroundResource(imgId);
        txt.setText(alleyId);

    }

}