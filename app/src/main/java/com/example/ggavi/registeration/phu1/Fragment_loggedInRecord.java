package com.example.ggavi.registeration.phu1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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


public class Fragment_loggedInRecord extends Fragment {


    // 푸푸씨가 만든 액티비티(activity)를 프래그먼트(fragment)로 수정
    private static String userID = MainActivity.userID;

    private ListView recordListView;
    private RecordListAdapter adapter;
    private List<Record> recordList;

    private TextView hsPedo;
    private TextView hsDis;
    private TextView hsCal;
    private TextView hsTime;
    private TextView hsSpeed;

    private TextView noRecAlert;

    private Integer maxPedo=0;
    private Double maxDis=0.000;
    private Double maxCal=0.00;
    private Double maxTime=0.0;
    private String maxTimeS="00:00:000";
    private Double maxSpeed=0.0;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment_loggedInRecord() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_loggedInRecord.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_loggedInRecord newInstance(String param1, String param2) {
        Fragment_loggedInRecord fragment = new Fragment_loggedInRecord();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override    // 액티비티가 만들어질 때 어떠한 처리가 이뤄지는 부분
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        recordListView = (ListView) getView().findViewById(R.id.recordListView);
        recordList = new ArrayList<Record>();

        //userID = getActivity().getIntent().getExtras().getString("userID").toString();
        //Intent intent = new Intent(getActivity(), LoggedInWalk.class);
        //intent.putExtra("userID", userID);

        hsPedo = (TextView)getView().findViewById(R.id.hsPedo);
        hsDis = (TextView)getView().findViewById(R.id.hsDis);
        hsCal = (TextView)getView().findViewById(R.id.hsCal);
        hsTime = (TextView)getView().findViewById(R.id.hsTime);
        hsSpeed = (TextView)getView().findViewById(R.id.hsSpeed);
        noRecAlert = (TextView)getView().findViewById(R.id.noRecAlert);

        // adapter에 해당 List를 매칭 (각각 차례대로 매칭)
        adapter = new RecordListAdapter(getActivity().getApplicationContext(), recordList);
        recordListView.setAdapter(adapter);

        new Fragment_loggedInRecord.BackgroundTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logged_in_record, container, false);


        // View view = inflater.inflate(R.layout.fragment_logged_in_record, container, false);
        // return view;
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        // (로딩창 띄우기 작업 3/1) 로딩창을 띄우기 위해 선언해준다.
        ProgressDialog dialog = new ProgressDialog(getActivity());

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

                System.out.println("length of jsonArray>>"+jsonArray.length());
                if(jsonArray.length()==0){
                    noRecAlert.setVisibility(View.VISIBLE);
                }else{
                    noRecAlert.setVisibility(View.GONE);
                }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
