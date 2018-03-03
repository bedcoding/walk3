package com.example.ggavi.registeration.ahn1;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.phu1.Fragment_loggedInRecord;

public class CourseRankActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_rank);


        // 첫 화면에 상단 액션바(타이틀 바) 없애려고 넣음
        getSupportActionBar().hide();


        // 밑에꺼 1, 2 화면전환 변수 선언
        final LinearLayout normalScreen = (LinearLayout) findViewById(R.id.normal_Screen);  //해당 Fragment 눌렀을 때 화면의 레이아웃이 바뀌는 부분
        final Button button1_select = (Button) findViewById(R.id.button1_select);
        final Button button2_rank = (Button) findViewById(R.id.button2_rank);


        // 1. 첫번째 fragment 화면으로 전환
        button1_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                normalScreen.setVisibility(View.GONE);


                // 선택된 버튼만 색상을 어둡게 만들고 나머지 버튼은 밝은 색상으로 변경
                button1_select.setBackground(getResources().getDrawable(R.drawable.custombtn));
                button2_rank.setBackground(getResources().getDrawable(R.drawable.custombtn2));

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // fragment 부분을, new CourseFragment 이걸로 대체해주는 것
                fragmentTransaction.replace(R.id.fragment2, new CourseFragment());
                fragmentTransaction.commit();
            }
        });


        // 2. 두번째 fragment 화면으로 전환
        button2_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 공지사항 부분이 보이지 않도록 하는 부분
                // 즉 notice 라는 LinearLayout이 사라지고 다른 Fragment가 보일 수 있도록 화면을 바꿔주는 것
                normalScreen.setVisibility(View.GONE);


                // 선택된 버튼만 색상을 어둡게 만들고 나머지 버튼은 밝은 색상으로 변경
                button1_select.setBackground(getResources().getDrawable(R.drawable.custombtn2));
                button2_rank.setBackground(getResources().getDrawable(R.drawable.custombtn));

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // fragment 부분을, new CourseFragment 이걸로 대체해주는 것
                fragmentTransaction.replace(R.id.fragment2, new StatisticsFragment());
                fragmentTransaction.commit();
            }
        });

    }
}
