package com.example.ggavi.registeration.phu1;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ggavi.registeration.R;

public class SettingActivity extends AppCompatActivity {

    private TextView loggedInWeightSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ((AppCompatActivity) SettingActivity.this).getSupportActionBar().setTitle((Html.fromHtml("<font color='#ffffff'>" + "설정" + "</font>")));

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loggedInWeightSetting=(TextView)findViewById(R.id.loggedInWeightSetting);
        loggedInWeightSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weightSetting();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
    public void weightSetting() {
        final Dialog dialog = new Dialog(SettingActivity.this); //here, the name of the activity class that you're writing a code in, needs to be replaced
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //for title bars not to be appeared (타이틀 바 안보이게)
        dialog.setContentView(R.layout.weight_dialog); //setting view
        dialog.show();
        EditText weightEntered = (EditText) dialog.findViewById(R.id.weightEntered);
        Button submitButton = (Button) dialog.findViewById(R.id.submitButton);

        String savedLoggedInModeWeight = SavedSharedPreference.getLoggedInModeWeight(SettingActivity.this);
        System.out.println("saved logged in mode weight>>" + savedLoggedInModeWeight);
        if (savedLoggedInModeWeight.length() > 0) {
            weightEntered.setText(savedLoggedInModeWeight);
            if(SavedSharedPreference.getLoggedInModeWeightType(SettingActivity.this).trim().equals("lbs")){
                RadioButton radioLbs = (RadioButton)dialog.findViewById(R.id.lbs);
                radioLbs.setChecked(true);
            }else{
                RadioButton radioKg = (RadioButton)dialog.findViewById(R.id.kg);
                radioKg.setChecked(true);
            }
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText weightEntered = (EditText) dialog.findViewById(R.id.weightEntered);
                weightEntered = (EditText) dialog.findViewById(R.id.weightEntered);
                String weightEnteredToString = weightEntered.getText().toString();

                RadioGroup weightType = (RadioGroup)dialog.findViewById(R.id.weightType);
                int selectedId = weightType.getCheckedRadioButtonId();

                SavedSharedPreference.setLoggedInModeWeight(SettingActivity.this, weightEnteredToString);
                SavedSharedPreference.setLoggedInModeWeightType(SettingActivity.this,((RadioButton)dialog.findViewById(selectedId)).getText().toString());
                System.out.println("saved data>>" + SavedSharedPreference.getLoggedInModeWeight(SettingActivity.this)+SavedSharedPreference.getLoggedInModeWeightType(SettingActivity.this));

                dialog.dismiss();
            }
        });
    }

}
