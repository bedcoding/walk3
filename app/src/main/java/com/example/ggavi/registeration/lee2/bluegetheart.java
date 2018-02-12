package com.example.ggavi.registeration.lee2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ggavi.registeration.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class bluegetheart extends AppCompatActivity {
    private List<BTSmsNumber> smsNumberList;
    private String userID="a"; //연결하면 메인액티비티에서 아이디를 받아와야함.
    BTSmsNumber smsNumber;
    private int alertcount=0;
    static final int REQUEST_ENABLE_BT = 10;
    BluetoothAdapter mBluetoothAdapter;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    MediaPlayer mp;
    Thread mWorkerThread = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';
    byte[] readBuffer;
    int readBufferPosition;

    private String PHnum;//폰번호를 받아오는 사이트
    private String PHtext;
    EditText mEditReceive, mEditSend;

    public bluegetheart(){
        new BackgroundTask().execute();


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lee2_activity_bluegetheart);


        mEditReceive = (EditText)findViewById(R.id.receiveText);
        //mEditSend = (EditText)findViewById(R.id.sendText);
        mp=MediaPlayer.create(this,R.raw.beepcheck);



        checkBluetooth();

    }




    //블ㄹ투스가 켜져있는지 지원하는지 확인 활성화 상태로 바꾸기 위한 부분
    void checkBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            // 장치가 블루투스를 지원하지 않는 경우
            finish();	// 어플리케이션 종료
        }
        else {
            // 장치가 블루투스를 지원하는 경우
            if (!mBluetoothAdapter.isEnabled()) {
                // 블루투스를 지원하지만 비활성 상태인 경우
                // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요청
                Intent enableBtIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                // 블루투스를 지원하며 활성 상태인 경우
                // 페어링 된 기기 목록을 보여주고 연결할 장치를 선택
                selectDevice();
            }
        }
    }


    //페어링된 디바이스 목록
    void selectDevice(){
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if(mPairedDeviceCount == 0){
            // 페어링 된 장치가 없는 경우
            finish();		// 어플리케이션 종료
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 페어링 된 블루투스 장치의 이름 목록 작성
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");		// 취소 항목 추가

        final CharSequence[] items =
                listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int item){
                if(item == mPairedDeviceCount){
                    // 연결할 장치를 선택하지 않고 ‘취소’를 누른 경우
                    finish();
                }
                else{
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도함
                    connectToSelectedDevice(items[item].toString());
                }
            }
        });

        builder.setCancelable(false);	// 뒤로 가기 버튼 사용 금지
        AlertDialog alert = builder.create();
        alert.show();
    }

    //디바이스를 사용하기 위한 부분
    void connectToSelectedDevice(String selectedDeviceName){
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try{
            // 소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();

            // 데이터 송수신을 위한 스트림 얻기
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            // 데이터 수신 준비
            beginListenForData();
        }catch(Exception e){
            // 블루투스 연결 중 오류 발생
            finish();		// 어플리케이션 종료
        }
    }
   /* void sendData(String msg){
        msg += mStrDelimiter;	// 문자열 종료 표시
        try{
            mOutputStream.write(msg.getBytes());		// 문자열 전송
        }catch(Exception e){
            // 문자열 전송 도중 오류가 발생한 경우
            finish();		// 어플리케이션 종료
        }
    }*/

    BluetoothDevice getDeviceFromBondedList(String name){
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())){
                selectedDevice = device;
                break;
            }
        }

        return selectedDevice;
    }

    void beginListenForData() {
        final Handler handler = new Handler();

        readBuffer = new byte[1024];	// 수신 버퍼
        readBufferPosition = 0;		// 버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable(){
            String cutbmi;//bmi 인트로 바구려고 자른거
            int bminum;//경고음이나메시지 이벤트 발생 시키려고 만듬
            public void run(){

                while(!Thread.currentThread().isInterrupted()){
                    try {

                        int bytesAvailable = mInputStream.available();	// 수신 데이터 확인
                        if(bytesAvailable > 0){		// 데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i = 0; i < bytesAvailable; i++){
                                byte b = packetBytes[i];
                                if(b == mCharDelimiter){
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0, encodedBytes.length); //readBuffer의0번쨰부터 encodedBytes의 0번쨰이후에 encodeBytes의 길이 만큼 넣는다.
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    //System.out.println(data);

                                    cutbmi=data.replace("B","").trim();//B를 공백문자로 바꾸고 그trim으로 공백제거해서 숫자만 구해줌.
                                    //System.out.println(cutbmi);
                                    bminum=Integer.parseInt(cutbmi);
                                    //  System.out.println("폰번호:"+PHnum);
                                    // System.out.println("문자 내용:"+PHtext);

                                    if(bminum>200&&alertcount<1){

                                        //  System.out.println("문자 메세지를 전송 합니다.");
                                        //   sendSMS(PHnum,PHtext);
                                        alertcount++;
                                    }
                                    if(bminum>200){
                                        //System.out.println("비프음이 울림.");
                                        beeper();
                                    }
                                    handler.post(new Runnable(){
                                        public void run(){


                                            // 수신된 문자열 데이터에 대한 처리 작업
                                            mEditReceive.setText( data + mStrDelimiter);


                                        }
                                    });
                                }
                                else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex){
                        // 데이터 수신 중 오류 발생
                        finish();
                    }
                }
            }
        });

        mWorkerThread.start();
    }

    void beeper(){//비프음  시작
        mp.start();

    }
    @Override
    protected void onDestroy() {
        try{
            mWorkerThread.interrupt();	// 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        }catch(Exception e){}

        super.onDestroy();
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "알림 문자 메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

//정보를 받아오는 스레드


    class BackgroundTask extends AsyncTask<Void, Void, String> {
        // (로딩창 띄우기 작업 3/1) 로딩창을 띄우기 위해 선언해준다.
        // ProgressDialog dialog = new ProgressDialog(bluegetheart.this);

        String target;  //우리가 접속할 홈페이지 주소가 들어감

        @Override
        protected void onPreExecute() {
            target = "http://ggavi2000.cafe24.com/smsList.php?userID="+ userID;  //해당 웹 서버에 접속

            // (로딩창 띄우기 작업 3/2)
            // dialog.setMessage("로딩중");
            //dialog.show();
        }  @Override
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
            // System.out.println(result);
            try {
                //    System.out.println("어싱크타스크가 한번 돌아감");
                // 해당 결과(result) 응답 부분을 처리
                JSONObject jsonObject = new JSONObject(result);

                // response에 각각의 공지사항 리스트가 담기게 됨
                JSONArray jsonArray = jsonObject.getJSONArray("response");  //아까 변수 이름

                int count = 0;
                String userID, smsNum1,numsName,smsText;

                while (count < jsonArray.length()) {
                    // 현재 배열의 원소값을 저장
                    JSONObject object = jsonArray.getJSONObject(count);

                    // 공지사항의 Content, Name, Date에 해당하는 값을 가져와라는 뜻

                    userID = object.getString("userID");
                    smsNum1 = object.getString("smsNum1");
                    numsName = object.getString("numsName");
                    smsText = object.getString("smsText");
                    // 하나의 공지사항에 대한 객체를 만들어줌
                    //여기선 별 다른 의미 없음. 넘버를 받아서 전역변수에 저장하고 그 번호를 연락할 번호로 사용할 뿐임.
                    smsNumber = new BTSmsNumber(userID, smsNum1,numsName,smsText);

                    if(smsText.length()!=0){//smsText가 DB에서 입력 값이 없으면 길이가 0이므로 이떈 미리 정해진 텍스트가 대신 보내진다.
                        PHtext=smsText;
                    }else{
                        PHtext="심장에 무리가 갈 수 있는 수치입니다.";
                    }
                    //리스트에 추가해줌
                    // smsNumberList.add(smsNumber);
                    // adapter.notifyDataSetChanged();
                    // PHnum=smsNum1;

                    // System.out.println(smsNum1+"Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                    PHnum=smsNum1;
                    count++;
                }

                // (로딩창 띄우기 작업 3/3)
                // 작업이 끝나면 로딩창을 종료시킨다.
                // dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
