package com.example.ggavi.registeration.lee1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.ahn1.MainActivity;
import com.example.ggavi.registeration.phu1.CustomConfirmDialog;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by User on 2018-02-06.
 */
//아이디는 메인에서 받아오고 디비의 userID는 딱히 사용하지 않습니다.
//등록된 전화번호를 삭제하고 화면을 리플레쉬 시킵니다.
// SmsDeleteRequest을 사용해서 통신.

public class SmsNumberAdapter extends BaseAdapter{
    private View v;
    private String userID = MainActivity.userID;//메인에서 값 받아와야 한다.
    private TextView numsName;
    private TextView smsNum1;
    private TextView smsText;
    private Context context;
    private List<SmsNumber> smsNumberList; //넘버가 들어갈 리스트를 만들어줌.
    Response.Listener<String> responseListener;
    private int ii;
    public SmsNumberAdapter(Context context, List<SmsNumber> smsNumberList){
        this.context=context;
        this.smsNumberList = smsNumberList;
    }

    @Override
    public int getCount() {
        return smsNumberList.size();
    }

    @Override
    public Object getItem(int i) {//i는 position
        return smsNumberList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup Parent)//i는 position
    {// 하나의 View로 만들어 줄 수 있도록 한다. (R.layout.notice로 배달)
        v = View.inflate(context, R.layout.lee1_activity_sms_number, null);
        numsName = (TextView) v.findViewById(R.id.numsName);//등록할 번호의 주인이름
        smsNum1 = (TextView) v.findViewById(R.id.smsNum1);//등록 번호
        smsText=(TextView)v.findViewById(R.id.smsText);//등록할 메세지
        ii=i;//i가 전역 변수 선언하지 않아 발생하는 에러 떄문에 ii를 만들어 i값을 사용하기 위한 부분. 배열에서 사용
        // noticeText를 현재 리스트에 있는 값으로 넣어줄 수 있도록 한다.
        numsName.setText(smsNumberList.get(i).getNumsName());
        smsNum1.setText(smsNumberList.get(i).getSmsNum1());
        smsText.setText(smsNumberList.get(i).getSmsText());
        v.setTag(smsNumberList.get(i).getNumsName());
        v.setTag(smsNumberList.get(i).getSmsNum1());
        v.setTag(smsNumberList.get(i).getSmsText());
        Button deleteButton = v.findViewById(R.id.deletebtn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//Response.Listener<String>
                responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 해당 웹사이트에 접속한 뒤 특정한 response(응답)을 다시 받을 수 있도록 한다
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            // 만약 삭제할 수 있다면
                            if (success) {
                                //new CustomConfirmDialog().showConfirmDialog(parent.getActivity(),"삭제하였습니다.",true);

                                // 삭제한 만큼 학점도 빼준다.
                                //StatisticsFragment.totalCredit -= courseList.get(i).getCourseCredit();
                                // StatisticsFragment.credit.setText(StatisticsFragment.totalCredit + "개");
                                // smsNumberList.remove(i);    // 리스트에서 삭제
                                // notifyDataSetChanged();  // 바뀐걸 적용
                            }

                            // 삭제 실패
                            //else {
                            // new CustomConfirmDialog().showConfirmDialog(parent.getActivity(),"삭제를 실패하였습니다.",true);
                            // }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                // SmsDeleteRequest smsDeleteRequest = new SmsDeleteRequest(userID, smsNumberList.get(ii).getSmsNum1() + "", responseListener);  // + ""를 붙이면 문자열 형태로 바꿈
                SmsDeleteRequest smsDeleteRequest= new SmsDeleteRequest(userID, smsNumberList.get(ii).getSmsNum1(),smsNumberList.get(ii).getNumsName(),smsNumberList.get(ii).getSmsText(),responseListener);
                RequestQueue queue;
                queue = Volley.newRequestQueue(context);//(SmsNumberAdapter.this);
                queue.add(smsDeleteRequest);
                queue.start();

                smsNumberList.remove(ii);
                notifyDataSetChanged();

            }
        });
        // 버튼 이벤트까지 마친 뒤 정상적으로 return 한다.

        return v;
    }
}