package com.example.ggavi.registeration.lee1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.ahn1.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SmsMainActivity extends AppCompatActivity {
    private Spinner spinner;
    private String userPassword;
    private String userGender;
    private String userMajor;
    private String userEmail;
    private AlertDialog dialog;  //알림창을 보여줌
    private boolean validate = false;  //사용할 수 있는 회원 아이디인지 체크
    private static String userID = MainActivity.userID;//연결하면 메인에서 아이디를 받아와야함.
    private TextView userIDtext;//목록에 대한 안내가 있는 텍스트
    private ListView smsNumberViewList;//전화번호가 기록되는 리스트뷰
    private SmsNumberAdapter adapter;//
    private List<SmsNumber> smsNumberList;

    //sms등록 할떄 묻는 다이얼로그들
    private Dialog smsInsertDialog;
    private Button dialog_reg;
    private Button dialog_cancel;
    private TextView dialog_text;
    private String smsNumCountCheck;
    //등록시에 안뜨는걸 해결하려고 텍스트로 목록 보이게 하기
    private String smsNum1pass;
    private String numNamepass;
    private String smsTextpass;
    private TextView LsmsNum1;
    private TextView LnumsName;
    private TextView LsmsText;
    private String smstextdef;   //"발송자의 심박수가 너무 높습니다. ";
    private boolean numAddFlag = false;//등록시와 그냥 화면에 뿌려줄 떄를 구분해서 등록시에는 에디트텍스트에 입력한 값을 받아서 싱크타스크에서 리스트에 추가한
//그냥 화면에 뿌려줄 떄는 그냥 통신해서 받아온 값을 사용한다.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lee1_activity_smsmain);

        smsNumberViewList = (ListView) findViewById(R.id.smsNumberViewList);
        smsNumberList = new ArrayList<SmsNumber>();

        userIDtext = (TextView) findViewById(R.id.userIDtext);
        userIDtext.setText(userID + "님이 등록한 비상연럭처");  //알림 글 리스트뷰 위에 아이디의 보호자 연락처임을 표시

        //adapter는 해당 List를 매칭 (각각 차례대로 매칭)
        adapter = new SmsNumberAdapter(getApplicationContext(), smsNumberList);
        smsNumberViewList.setAdapter(adapter);

        ((Button) findViewById(R.id.click)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //등록 버튼을 눌렀을 떄  다이얼 로그 창이 나옴.
                smsInsertDialog = new Dialog(SmsMainActivity.this);
                smsInsertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //다이얼로그 화면 구성하기.
                smsInsertDialog.setContentView(R.layout.lee1_smsinsertdialog);
                dialog_reg=(Button)smsInsertDialog.findViewById(R.id.dialog_reg);
                dialog_cancel=(Button)smsInsertDialog.findViewById(R.id.dialog_cancel);

                dialog_reg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        numAddFlag=true; //실행시 트루

                        //첫등록시 화면에 띄워주기 위해서
                        //System.out.println(smsNum1pass);
                       /* LsmsNum1=(TextView)findViewById(R.id.LsmsNum1);
                        LnumsName=(TextView)findViewById(R.id.LnumsName) ;

                        LsmsNum1.setText(smsNum1pass);
                        LnumsName.setText(numNamepass);*/
                       /* if(smsNumberList.){
                            //다이얼로그 종료
                            smsInsertDialog.dismiss();
                        }*/

                        Response.Listener<String> responseListener = new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                // 해당 웹사이트에 접속한 뒤 특정한 response(응답)을 다시 받을 수 있도록 한다
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");

                                    // 만약 사용할 수 있는 아이디라면
                                    if (success) {
                                        Toast.makeText(SmsMainActivity.this, "success", Toast.LENGTH_LONG).show();
                                    }

                                    // 중복체크 실패 (사용할 수 없는 아이디)
                                    else {
                                        Toast.makeText(SmsMainActivity.this, "failed", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };


                        // 실질적으로 접속할 수 있도록 생성자를 통해 객체를 만든다. (유저 ID, responseListener)
                        // ValidateRequest.java라는 파일을 만들어야 한다.
                        smsDB_Manager smsRequest = new smsDB_Manager(userID, ((EditText) findViewById(R.id.smsNum1)).getText().toString(), ((EditText) findViewById(R.id.numsName)).getText().toString(),((EditText) findViewById(R.id.smsText)).getText().toString(), responseListener);
                        RequestQueue queue = Volley.newRequestQueue(SmsMainActivity.this);
                        queue.add(smsRequest);  //준비된 데이터를 받아 통신을 시작한다.
                        queue.start();


                        /*smsNum1pass=  ((EditText) findViewById(R.id.smsNum1)).getText().toString();
                        numNamepass= ((EditText) findViewById(R.id.numsName)).getText().toString();
                        smsTextpass= ((EditText) findViewById(R.id.smsText)).getText().toString();*/

                        System.out.println("타스크 실행전"+numAddFlag);

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }});

                        new BackgroundTask().execute();//등록 버튼을 누르면 리스트 스레드를 돌려서 리스트를 만들어줌.

                        Toast.makeText(getApplicationContext(),"등록되었습니다.",Toast.LENGTH_SHORT).show();

                        numAddFlag=false;
                        System.out.println("타스크 실행후"+numAddFlag);
                        //다이얼로그 종료
                        smsInsertDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });


                //취소하면 그냥 취소됐다는 걸 알리는 토스트창 하나 띄워주고 종료함.
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //취소시에 종료 토스트 출력하기.
                        Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_SHORT).show();

                        //다이얼로그 종료
                        smsInsertDialog.dismiss();

                    }
                });

                //다이얼로그 화면에 보여주기
                smsInsertDialog.show();
            }
        });


        new BackgroundTask().execute();
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {
        // (로딩창 띄우기 작업 3/1) 로딩창을 띄우기 위해 선언해준다.
        ProgressDialog dialog = new ProgressDialog(SmsMainActivity.this);

        String target;  //우리가 접속할 홈페이지 주소가 들어감

        @Override
        protected void onPreExecute() {
            target = "http://ggavi2000.cafe24.com/smsList.php?userID=" + userID;  //해당 웹 서버에 접속

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
                String userID = "", numsName = "";
                String smsNum1 = "", smsText = "";
                while (count < jsonArray.length()) {
                    // 현재 배열의 원소값을 저장
                    JSONObject object = jsonArray.getJSONObject(count);

                    // 공지사항의 Content, Name, Date에 해당하는 값을 가져와라는 뜻
                    System.out.println(numAddFlag);

                    if (numAddFlag == false) {
                        userID = object.getString("userID");
                        smsNum1 = object.getString("smsNum1");
                        numsName = object.getString("numsName");
                        smsText = object.getString("smsText");
                    } else if (numAddFlag == true) {
                        userID = MainActivity.userID;
                        smsNum1 = smsNum1pass;
                        numsName = numNamepass;
                        smsText = smsTextpass;
                    }


                    // 하나의 공지사항에 대한 객체를 만들어줌
                    SmsNumber smsNumber = new SmsNumber(userID, smsNum1, numsName, smsText);

                    // smsNumCountCheck=smsNum1;
                    //리스트에 추가해줌
                    smsNumberList.add(smsNumber);
                    adapter.notifyDataSetChanged();

                    count++;
                }
                // (로딩창 띄우기 작업 3/3)
                // 작업이 끝나면 로딩창을 종료시킨다.
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
