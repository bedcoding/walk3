package com.example.ggavi.registeration.phu1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ggavi.registeration.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoggedInRecord extends AppCompatActivity {

    // (4)values 폴더에 추가한 arrays.xml 이놈을 담기 위해 선언


    private static String userID = "";

    private ListView recordListView;
    private RecordListAdapter adapter;
    private List<Record> recordList;


    private TextView hsPedo;
    private TextView hsDis;
    private TextView hsCal;
    private TextView hsTime;
    private TextView hsSpeed;

    private Integer maxPedo=0;
    private Double maxDis=0.000;
    private Double maxCal=0.00;
    private Double maxTime=0.0;
    private String maxTimeS="00:00:000";
    private Double maxSpeed=0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logged_in_record);
        recordListView = (ListView) findViewById(R.id.recordListView);
        ((AppCompatActivity) LoggedInRecord.this).getSupportActionBar().setTitle((Html.fromHtml("<font color='#ffffff'>" + "나의 기록" + "</font>")));
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        recordList = new ArrayList<Record>();

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID").toString();

        hsPedo = (TextView)findViewById(R.id.hsPedo);
        hsDis = (TextView)findViewById(R.id.hsDis);
        hsCal = (TextView)findViewById(R.id.hsCal);
        hsTime = (TextView)findViewById(R.id.hsTime);
        hsSpeed = (TextView)findViewById(R.id.hsSpeed);



        // adapter에 해당 List를 매칭 (각각 차례대로 매칭)
        adapter = new RecordListAdapter(getApplicationContext(), recordList);
        recordListView.setAdapter(adapter);

        new BackgroundTask().execute();

    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        // (로딩창 띄우기 작업 3/1) 로딩창을 띄우기 위해 선언해준다.
        ProgressDialog dialog = new ProgressDialog(LoggedInRecord.this);

        String target;  //우리가 접속할 홈페이지 주소가 들어감

        @Override
        protected void onPreExecute() {
            target = "http://ggavi2000.cafe24.com/RecordList.php?userId="+ userID;  //해당 웹 서버에 접속

            // (로딩창 띄우기 작업 3/2)
            dialog.setMessage("로딩중");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 해당 서버에 접속할 수 있도록 URL을 커넥팅 한다.
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                // 넘어오는 결과값을 그대로 저장
                InputStream inputStream = httpURLConnection.getInputStream();

                // 해당 inputStream에 있던 내용들을 버퍼에 담아서 읽을 수 있도록 해줌
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                // 이제 temp에 하나씩 읽어와서 그것을 문자열 형태로 저장
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                // null 값이 아닐 때까지 계속 반복해서 읽어온다.
                while ((temp = bufferedReader.readLine()) != null) {
                    // temp에 한줄씩 추가하면서 넣어줌
                    stringBuilder.append(temp + "\n");
                }

                // 끝난 뒤 닫기
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();  //인터넷도 끊어줌
                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override  //해당 결과를 처리할 수 있는 onPostExecute()
        public void onPostExecute(String result) {
            try {
                // 해당 결과(result) 응답 부분을 처리
                JSONObject jsonObject = new JSONObject(result);

                // response에 각각의 공지사항 리스트가 담기게 됨
                JSONArray jsonArray = jsonObject.getJSONArray("response");  //아까 변수 이름

                int count = 0;
                String userId, pedometer, distance, calorie, time, speed, date, progress;

                while (count < jsonArray.length()) {
                    // 현재 배열의 원소값을 저장
                    JSONObject object = jsonArray.getJSONObject(count);

                    // 공지사항의 Content, Name, Date에 해당하는 값을 가져와라는 뜻

                    userId = object.getString("userId");
                    pedometer= object.getString("pedometer");
                    distance = object.getString("distance");
                    calorie = object.getString("calorie");
                    time = object.getString("time");
                    speed = object.getString("speed");
                    date = object.getString("date");
                    progress = object.getString("progress");

                    // 하나의 공지사항에 대한 객체를 만들어줌
                    Record record = new Record(userId, pedometer, distance, calorie, time, speed, date, progress);

                    // 리스트에 추가해줌
                    recordList.add(record);
                    adapter.notifyDataSetChanged();
                    count++;
                }


                for(int i=0;i<recordList.size();i++){
                    if(maxPedo<Integer.parseInt(recordList.get(i).getPedometer().trim())){
                        maxPedo = Integer.parseInt(recordList.get(i).getPedometer().trim());
                    }
                    if(maxDis<Double.parseDouble(recordList.get(i).getDistance().trim())){
                        maxDis = Double.parseDouble(recordList.get(i).getDistance().trim());
                    }
                    if(maxCal<Double.parseDouble(recordList.get(i).getCalorie().trim())){
                        maxCal = Double.parseDouble(recordList.get(i).getCalorie().trim());
                    }
                    if(maxSpeed<Double.parseDouble(recordList.get(i).getSpeed().trim())){
                        maxSpeed = Double.parseDouble(recordList.get(i).getSpeed().trim());
                    }
                    String time1 = recordList.get(i).getTime().trim();
                    int pos_1 = time1.indexOf(":");
                    String minute = time1.substring(0,pos_1);
                    System.out.println(minute);
                    String aa = time1.substring(pos_1);
                    String aa2 = aa.substring(1);
                    int pos_2 = aa2.indexOf(":");
                    String second = aa2.substring(0,pos_2);
                    System.out.println(second);
                    String aa3 = aa2.substring(pos_2);
                    String millisecond = aa3.substring(1);
                    System.out.println(millisecond);

                    //minute + second/60 + (millisecond/60)/1000 = total time
                    double totalTime = Double.parseDouble(minute) + (Double.parseDouble(second)/60) + ((Double.parseDouble(millisecond)/60)/1000);
                    if(maxTime<totalTime){
                       maxTime = totalTime;
                       maxTimeS = recordList.get(i).getTime();
                    }
                }

                hsPedo.setText(String.valueOf(maxPedo));
                hsDis.setText(Double.toString(maxDis)+"km");
                hsCal.setText(Double.toString(maxCal));
                hsSpeed.setText(Double.toString(maxSpeed)+"km/h");
                hsTime.setText(maxTimeS);



                // (로딩창 띄우기 작업 3/3)
                // 작업이 끝나면 로딩창을 종료시킨다.
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
