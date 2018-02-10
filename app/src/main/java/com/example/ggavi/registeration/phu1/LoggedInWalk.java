package com.example.ggavi.registeration.phu1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.ahn2.LifeJisu_MainActivity;
import com.example.ggavi.registeration.phu1.FirstActivity;
import com.example.ggavi.registeration.ahn1.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LoggedInWalk extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, SensorEventListener, StepListener {

    private String userID;

    private GoogleMap mMap; //map (맵)
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Marker mCurrLocationMarker; //marker (마커)
    private TextView distanceTv; //distance text view (거리 텍스트 뷰)
    private TextView calTv; //calorie text view (칼로리 텍스트 뷰)
    private TextView stepsTv; //step text view (걸음수 - 만보기 텍스트 뷰)
    private TextView timeTv; //time text view (시간 텍스트 뷰)
    private LinearLayout achievementLayout;

    private DecimalFormat kcalFormat = new DecimalFormat("#0.00"); //calorie -> 0.00 format (칼로리 -> 0.00 형식)
    private DecimalFormat kmFormat = new DecimalFormat("#0.000");//km-distance -> 0.000 format (km-거리 -> 0.000 형식)


    private PolylineOptions polylineOptions; //poly line option
    private StepDetector simpleStepDetector; //step detector (만보기)
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps; //step numbers (걸음 수)


    private static GPSListener gpsListener = null; //object for tracking a route (루트를 트래킹 할 객체)

    List<LatLng> pointsOnRoute = new ArrayList<>(); //points passed on walking the route (걸을 때 지나간 포인트들)

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //boolean to decide whether tracking or not (트래킹하고 있는지 아닌지 결정하는 boolean)
    private Button trackingButton;
    private boolean isTracking = false;

    //variables for counting time (시간 카운트를 위한 변수들)
    long millisecondTime, startTime, timeBuff, updateTime = 0L;
    int seconds, minutes, milliSeconds;
    Handler handler;

    private boolean isGoalEntered = false;
    private double goal = 0;
    private TextView achievementPercentage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_walk);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");


        ((AppCompatActivity) LoggedInWalk.this).getSupportActionBar().setTitle((Html.fromHtml("<font color='#ffffff'>" + "걷기모드" + "</font>")));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        buildGoogleApiClient(); //GoogleApiClient를 빌드

        distanceTv = (TextView) findViewById(R.id.distanceL);
        calTv = (TextView) findViewById(R.id.calL);
        stepsTv = (TextView) findViewById(R.id.stepsL);
        timeTv = (TextView) findViewById(R.id.timeL);

        trackingButton = (Button) findViewById(R.id.trackL);

        achievementLayout = (LinearLayout) findViewById(R.id.achievementLayout);
        achievementPercentage = (TextView) findViewById(R.id.achievementPercentage);
        progressBar = (ProgressBar) findViewById(R.id.achievementProgressBar);

        progressBar.setMax(100);

        //registering step counting sensor (만보기 센서하게 레지스터)
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        handler = new Handler();

        //confirming if GPS is connected (GPS연결 여부 확인하기)
        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //getting whether gps is connected or not (연결 여부 가져오기)
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {//if not connected (연결 안된 경우)

            //shows a dialog to connect gps (gps연결 하기 위해 dialog 보여줌)

            final Dialog dialog = new Dialog(LoggedInWalk.this); //here, the name of the activity class that you're writing a code in, needs to be replaced
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //for title bars not to be appeared (타이틀 바 안보이게)
            dialog.setContentView(R.layout.alert_dialog); //setting view


            //getting textviews and buttons from dialog
            TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
            TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogMessage);
            Button dialogButton1 = (Button) dialog.findViewById(R.id.dialogButton1);
            Button dialogButton2 = (Button) dialog.findViewById(R.id.dialogButton2);

            //You can change the texts on java code shown as below
            dialogTitle.setText(" GPS연결 ");
            dialogMessage.setText(" 이 기능은 GPS 연결이 필요합니다. ");
            dialogButton1.setText("연결");
            dialogButton2.setText("취소");

            dialog.setCanceledOnTouchOutside(false); //dialog won't be dismissed on outside touch
            dialog.setCancelable(false); //dialog won't be dismissed on pressed back
            dialog.show(); //show the dialog

            //here, I will only dismiss the dialog on clicking on buttons. You can change it to your code.
            dialogButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //your code here
                    dialog.dismiss(); //to dismiss the dialog
                    //get gps
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.mapL);
                    mapFragment.getMapAsync(LoggedInWalk.this);


                }
            });

            dialogButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //your code here
                    dialog.dismiss(); //to dismiss the dialog
                    finish();
                    Intent intent = new Intent(LoggedInWalk.this, MainActivity.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent); //force back to first activity

                }
            });


        } else {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapL);
            mapFragment.getMapAsync(LoggedInWalk.this);
        }
    }


    //adding the menu on tool bar (액션바에서 메뉴 추가)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.normal_mode_menu, menu);
        return true;
    }

    // adding actions that will be done on clicking menu items
    // (메뉴 아이템들을 클릭할 때 발생할 이벤트 추가)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // 3번째 버튼: 날씨/대기정보
            case R.id.plus_weather:
                plus_weather();
                return true;

            default:
                return true;
        }
    }


    // 2번째 버튼 안넣음
    // ㅠㅠ

    // 3번째 버튼 (날씨/대기정보)
    public void plus_weather() {
        Intent intent = new Intent(getApplicationContext(), LifeJisu_MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) { //when Map is ready(Map이 ready되어 있는 상태)
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        trackingButton.setOnClickListener(new View.OnClickListener() {//start tracking (트래킹 시작)
            @Override
            public void onClick(View view) {

                if (!isTracking) {

                    final Dialog dialog = new Dialog(LoggedInWalk.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.goal_dialog);

                    dialog.setCanceledOnTouchOutside(false); //to prevent dialog getting dismissed on outside touch
                    dialog.setCancelable(false); //to prevent dialog getting dismissed on back button
                    dialog.show();

                    final EditText goalEntered = (EditText) dialog.findViewById(R.id.goalEntered);
                    Button submitButton = (Button) dialog.findViewById(R.id.submitButton);
                    Button skipButton = (Button) dialog.findViewById(R.id.skipButton);

                    skipButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isGoalEntered = false;
                            dialog.dismiss();
                        }
                    });

                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isGoalEntered = true;
                            goal = Double.parseDouble(goalEntered.getText().toString());
                            dialog.dismiss();

                        }
                    });

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface arg0) {
                            // do something
                            trackingButton.setText("STOP ");
                            trackingButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.standing_man, 0, 0, 0);
                            trackingButton.setBackgroundResource(R.drawable.rounded_button1);
                            isTracking = true; //tracking started (트래킹시작)

                            if (isGoalEntered) {
                                achievementLayout.setVisibility(View.VISIBLE);
                                System.out.println("goal set>>" + goal);
                            } else {
                                achievementLayout.setVisibility(View.GONE);
                                System.out.println("goal is not entered");
                            }

                            //setting system time as start time (시스템의 시간을 가져와 startTime로 설정)
                            startTime = SystemClock.uptimeMillis();
                            //handler starts (핸들러 시작)
                            handler.postDelayed(runnable, 0);


                            //step counter registered (걸음 수 측정 레지스터)
                            numSteps = 0;
                            sensorManager.registerListener(LoggedInWalk.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

                            //requesting location updates according to sdk version & whether permission is granted or not
                            //(sdk버전 & permission 여부에 따라 위치 업데이트를 요청)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(LoggedInWalk.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                                        ContextCompat.checkSelfPermission(LoggedInWalk.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                                PackageManager.PERMISSION_GRANTED) {
                                    mMap.setMyLocationEnabled(true);
                                    mLocationRequest = new LocationRequest();

                                    mLocationRequest.setInterval(10000).setFastestInterval(10000 / 2).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    //create object for tracking (트래킹을 위한 객체 생성)
                                    gpsListener = new GPSListener();
                                    if (ContextCompat.checkSelfPermission(LoggedInWalk.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, gpsListener);
                                    }


                                } else {
                                    checkLocationPermission();
                                    mLocationRequest = new LocationRequest();

                                    mLocationRequest.setInterval(10000).setFastestInterval(10000 / 2).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                    //create object for tracking (트래킹을 위한 객체 생성)
                                    gpsListener = new GPSListener();
                                    if (ContextCompat.checkSelfPermission(LoggedInWalk.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, gpsListener);
                                    }
                                    //setting system time as start time (시스템의 시간을 가져와 startTime로 설정)
                                    startTime = SystemClock.uptimeMillis();
                                    //handler starts (핸들러 시작)
                                    handler.postDelayed(runnable, 0);

                                    //step counter registered (걸음 수 측정 레지스터)
                                    numSteps = 0;
                                    sensorManager.registerListener(LoggedInWalk.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                                    // mMap.setMyLocationEnabled(true);
                                }
/*
if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
    googleMap.setMyLocationEnabled(true);
    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
} else {
    Toast.makeText(this, R.string.error_permission_map, Toast.LENGTH_LONG).show();
}
 */


                            } else {
                                //    buildGoogleApiClient();
                                mMap.setMyLocationEnabled(true);
                                mLocationRequest = new LocationRequest();
                                mLocationRequest.setInterval(10000).setFastestInterval(10000 / 2).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                gpsListener = new GPSListener();
                                if (ContextCompat.checkSelfPermission(LoggedInWalk.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, gpsListener);
                                }
                            }

                        }
                    });
                } else {
                    trackingButton.setText("START ");
                    trackingButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.walking_man, 0, 0, 0);
                    trackingButton.setBackgroundResource(R.drawable.rounded_button2);
                    isTracking = false; //tracking stopped (트래킹 멈춤)
                    achievementLayout.setVisibility(View.GONE);
                    if (mGoogleApiClient != null) {

                        final String stepsRecord = stepsTv.getText().toString();
                        final String distanceRecord = distanceTv.getText().toString();

                        final String calRecord = calTv.getText().toString();
                        final String timeRecord = timeTv.getText().toString();
                        final String progressRecord;
                        if(isGoalEntered) {
                            progressRecord = achievementPercentage.getText().toString();
                        }else{
                            progressRecord = "skipped";
                        }

                        //getting minutes, seconds and milliseconds from time record (시간 기록에서 minutes, seconds, milliseconds로 다시 나눔)
                        String time = timeRecord;
                        int pos_1 = time.indexOf(":");
                        String minute = time.substring(0, pos_1);
                        System.out.println(minute);
                        String aa = time.substring(pos_1);
                        String aa2 = aa.substring(1);
                        int pos_2 = aa2.indexOf(":");
                        String second = aa2.substring(0, pos_2);
                        System.out.println(second);
                        String aa3 = aa2.substring(pos_2);
                        String millisecond = aa3.substring(1);
                        System.out.println(millisecond);

                        //minute + second/60 + (millisecond/60)/1000 = total time
                        double totalTime = Double.parseDouble(minute) + (Double.parseDouble(second) / 60) + ((Double.parseDouble(millisecond) / 60) / 1000);
                        System.out.println("total time>>" + totalTime);
                        System.out.println("distance>>" + distanceRecord);
                        double speed = (Double.parseDouble(distanceRecord) / totalTime) * 60;


                        System.out.println("speed>>" + speed);
                        //speed = kmFormat.format(speed);
                        final String speedRecord = kmFormat.format(speed);
                        //  speedRecord = Double.toString((Double.parseDouble(distanceRecord) / Double.parseDouble(timeMinute)) * 60);

                        System.out.println("formatted>>" + speedRecord);
                        //System.out.println("speed>>"+speedRecord);
                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                // 해당 웹사이트에 접속한 뒤 특정한 response(응답)을 다시 받을 수 있도록 한다
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");


                                    if (success) {
                                         Toast.makeText(LoggedInWalk.this, "success", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoggedInWalk.this, "failed", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        // 실질적으로 접속할 수 있도록 생성자를 통해 객체를 만든다.

                        AddLoggedInRecordRequest request = new AddLoggedInRecordRequest(userID, stepsRecord, distanceRecord, calRecord, timeRecord, speedRecord, progressRecord,responseListener);
                        RequestQueue queue = Volley.newRequestQueue(LoggedInWalk.this);
                        queue.add(request);

                        //remove location updates (위치 업데이트 지우기)
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, gpsListener);

                        //unregistering step counter (걸음 수 측정 기능 없애기)
                        sensorManager.unregisterListener(LoggedInWalk.this);

                        //location disable
                        checkLocationPermission();
                        mMap.setMyLocationEnabled(false);

                        //displaying distance, kcal, steps etc. (걸어온 거리, 칼로리, 걸음 수 보여줌)
                        final Dialog dialog = new Dialog(LoggedInWalk.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.record_dialog);

                        TextView dialogSteps = (TextView) dialog.findViewById(R.id.dialogSteps);
                        TextView dialogDistance = (TextView) dialog.findViewById(R.id.dialogDistance);
                        TextView dialogCal = (TextView) dialog.findViewById(R.id.dialogCal);
                        TextView dialogTime = (TextView) dialog.findViewById(R.id.dialogTime);

                        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialogTitle);
                        dialogTitle.setText(userID + "님의 Walk Away 기록");


                        dialogSteps.setText("만보기 기록: " + stepsRecord + " 보");
                        dialogDistance.setText("거리 기록: " + distanceRecord + " km");
                        dialogCal.setText("칼로리 소모량 기록: " + calRecord + " 칼로리");
                        dialogTime.setText("시간 기록: " + timeRecord);

                        //resetting all values (모두 다시 리셋팅)
                        stepsTv.setText("0");
                        distanceTv.setText("0.000");
                        calTv.setText("0.00");
                        timeTv.setText("00:00:000");
                        pointsOnRoute.clear();
                        handler.removeCallbacks(runnable);
                        gpsListener.setPreviousLocation(null);
                        mMap.clear();
                        Button dialogHome = (Button) dialog.findViewById(R.id.dialogHome);
                        dialogHome.setText("메인화면");

                        dialog.setCanceledOnTouchOutside(false); //to prevent dialog getting dismissed on outside touch
                        dialog.setCancelable(false); //to prevent dialog getting dismissed on back button
                        dialog.show();
                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogBack);

                        Button dialogShare = (Button) dialog.findViewById(R.id.dialogShare);
                        // if button is clicked, close dialog (클릭 시 dialog 끔)
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        // if button is clicked, intent to HOME (클릭 시 홈으로 이동)
                        dialogHome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                finish();
                                Intent intent = new Intent(view.getContext(), MainActivity.class);
                                intent.putExtra("userID", userID);
                                startActivityForResult(intent, 0);
                            }
                        });

                        //if button is clicked share functions will be popped up (클릭 시 공유 기능 나옴)
                        dialogShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                String textBody = "유저 아이디 " + userID + "님의 Walk Away 기록\n\n만보기 기록: " + stepsRecord + " 보\n거리 기록: " + distanceRecord + " km\n칼로리 소모량 기록: " + calRecord + " 칼로리\n시간 기록: " + timeRecord;
                                //link can be added later
                                String textSubject = "[Walk Away] 나의 기록";
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, textSubject);
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, textBody);
                                startActivity(Intent.createChooser(sharingIntent, "Share using"));

                            }
                        });


                    }
                }

            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkLocationPermission() { //for checking permission (permission 체크 용)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoggedInWalk.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();

                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    protected void buildGoogleApiClient() { //for building google api client (google api client빌드)
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) { //when step counts increased (걸음 수 증가 시)
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel( //update (업데이트)
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void step(long timeNs) {
        //increasing steps (걸음 수 증가 시키기)
        numSteps++;
        //setting text for steps (걸음 수 보여주기
        stepsTv.setText(" " + numSteps);

    }

    //Runnable for counting time (시간을 카운트하는 Runnable)
    public Runnable runnable = new Runnable() {

        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + millisecondTime;
            seconds = (int) (updateTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliSeconds = (int) (updateTime % 1000);
            timeTv.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliSeconds));
            handler.postDelayed(this, 0);
        }

    };


    private class GPSListener implements LocationListener { //class for tracking (트래킹 하는 클래스)
        public void setPreviousLocation(Location previousLocation) {
            this.previousLocation = previousLocation;
        }

        private Location previousLocation = null; //previous location (이전 위치)
        private double totalDistance = 0D; //total distance (총 거리)
        private double totalDistance_hidden = 0D; //hidden distance to show counting (카운팅을 보여 줄 hidden distance)
        private double dis = 0D; //distance (거리)
        private double cal = 0.0; //calorie (칼로리)


        @Override
        public void onLocationChanged(Location location) {

            if (location != null) {
                if ((location.getAccuracy() > 15 && this != null)) { //if gps connection is week (gps수신이 약한 경우)
                    Toast.makeText(LoggedInWalk.this, "GPS 수신이 약하기 때문에 거리 측정이 어렵습니다.", Toast.LENGTH_SHORT).show();
                    //mentioning "GPS connection is week. It is difficult to predict the distance.
                }

                if (previousLocation != null/* && location.getAccuracy() < 15*/) { //previous location is not null (이전 위치가 null이 아님)

                    //calculate the values and show them on UI (값 계산하고 UI에 보여주기)
                    cal = totalDistance_hidden * (getCalorieRate() * 0.001);
                    // Toast.makeText(LoggedInWalk.this,"calorie rate:"+getCalorieRate(),Toast.LENGTH_LONG).show();
                    calTv.setText("" + kcalFormat.format(cal));


                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                        mCurrLocationMarker = null;
                    }
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    dis = location.distanceTo(previousLocation);
                    if (dis < 1.0) {
                        dis = 0D;
                    }
                    totalDistance += dis / 1000; //showing distance in km (km단위로 보여주기)
                    totalDistance_hidden += dis;
                    distanceTv.setText(" " + kmFormat.format(totalDistance));
                    //  Toast.makeText(NormalMode.this, "DISTANCE HIDDEN>>" + (int) totalDistance_hidden, Toast.LENGTH_SHORT).show();

                    if (isGoalEntered) {
                        Double percentage = (100 * totalDistance) / goal;
                        if(percentage<100){
                            achievementPercentage.setText(String.valueOf(percentage.intValue()));
                            progressBar.setProgress(percentage.intValue());
                        }
                    }
                    pointsOnRoute.add(latLng);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.MAGENTA);
                    polylineOptions.width(7);
                    Polyline route = mMap.addPolyline(polylineOptions);
                    route.setPoints(pointsOnRoute);

                    MarkerOptions op = new MarkerOptions();
                    op.title("현재 위치입니다.");
                    op.position(latLng);
                    op.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    mCurrLocationMarker = mMap.addMarker(op);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
                //setting current location as previous location for next location update (다음 위치 업데이트를 위해 현재 위치를 이전 위치로 세팅)
                this.previousLocation = location;

            }
        }
    }

    public double getCalorieRate() {

        double calorieRate = 0.00;
        String savedLoggedInWeight = SavedSharedPreference.getLoggedInModeWeight(LoggedInWalk.this);
        double weightInKm = 0.0;
        if (savedLoggedInWeight.length() == 0) {
            calorieRate = 55.511;
            return calorieRate;
        } else {
            weightInKm = Double.parseDouble(savedLoggedInWeight);
            if (SavedSharedPreference.getLoggedInModeWeightType(LoggedInWalk.this).trim().equals("lbs")) {
                weightInKm = weightInKm * 0.453592;
            }
            if (weightInKm <= 52) {
                calorieRate = 31.25;
            } else if (weightInKm >= 53 && weightInKm <= 59) {
                calorieRate = 36.25;
            } else if (weightInKm >= 60 && weightInKm <= 66) {
                calorieRate = 40.625;
            } else if (weightInKm >= 67 && weightInKm <= 73) {
                calorieRate = 45;
            } else if (weightInKm >= 74 && weightInKm <= 80) {
                calorieRate = 49.375;
            } else if (weightInKm >= 81 && weightInKm <= 86) {
                calorieRate = 53.75;
            } else if (weightInKm >= 87 && weightInKm <= 93) {
                calorieRate = 58.125;
            } else if (weightInKm >= 94 && weightInKm <= 100) {
                calorieRate = 62.5;
            } else if (weightInKm >= 101 && weightInKm <= 107) {
                calorieRate = 66.875;
            } else if (weightInKm >= 108 && weightInKm <= 130) {
                calorieRate = 76.25;
            } else if (weightInKm >= 131) {
                calorieRate = 90.625;
            }
        }
        return calorieRate;
    }


    // 두번 뒤로가기 버튼을 누르면 종료
    private long lastTimeBackPressed;

    @Override
    public void onBackPressed() {
        // 한번 버튼을 누른 뒤, 1.5초 이내에 또 누르면 종료
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
            finish();
            Intent intent = new Intent(LoggedInWalk.this, MainActivity.class);
            intent.putExtra("userID", userID);
            startActivityForResult(intent, 0);
            return;
        }

        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 메인 화면으로 넘어갑니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    public double getTotalTime(String timeRecord) {
        String time = timeRecord;
        int pos_1 = time.indexOf(":");
        String minute = time.substring(0, pos_1);
        System.out.println(minute);
        String aa = time.substring(pos_1);
        String aa2 = aa.substring(1);
        int pos_2 = aa2.indexOf(":");
        String second = aa2.substring(0, pos_2);
        System.out.println(second);
        String aa3 = aa2.substring(pos_2);
        String millisecond = aa3.substring(1);
        System.out.println(millisecond);

        //minute + second/60 + (millisecond/60)/1000 = total time
        double totalTime = Double.parseDouble(minute) + (Double.parseDouble(second) / 60) + ((Double.parseDouble(millisecond) / 60) / 1000);
        return totalTime;
    }
}
