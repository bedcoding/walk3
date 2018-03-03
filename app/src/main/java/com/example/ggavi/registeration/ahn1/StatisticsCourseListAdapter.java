package com.example.ggavi.registeration.ahn1;

//import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.phu1.CustomConfirmDialog;

import org.json.JSONObject;

import java.util.List;

// NoticeListAdapter.java를 가져와서 CourseListAdapter를 만들고,
// (18) 그걸 또 가져와서 수정하여 이 클래스를 만들었다.

public class StatisticsCourseListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;      // Course가 들어가는 리스트를 만들어줌
    private Fragment parent;
    private String userID = MainActivity.userID;  // MainActivity에 있는 public 형태의 userID를 가져와서 해당 사용자의 아이디를 저장




    public StatisticsCourseListAdapter(Context context, List<Course> courseList, Fragment parent)  //자신을 불러낸 부모 Fragment를 담을 수 있도록 한다.
    {
        this.context = context;
        this.courseList = courseList;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int i) //i는 position
    {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) //i는 position
    {
        return i;
    }

    @Override  //i는 position, viewGroupParent는 처음에 parent로 자동완성이 되어서 밑에 있는 parent와 중복되서 에러 생겼음 (그래서 고침)
    public View getView(final int i, View convertView, ViewGroup viewGroupParent)
    {
        // 하나의 View로 만들어 줄 수 있도록 한다.
        View v = View.inflate(context, R.layout.statistics, null);  // statistics.xml


        // statistics.xml이라는 레이아웃에 있는 모든 원소가 하나의 변수로써 자리잡게 되었다.
        TextView courseTitle = (TextView) v.findViewById(R.id.courseTitle);
        TextView coursePersonnel = (TextView) v.findViewById(R.id.coursePersonnel);

        courseTitle.setText(courseList.get(i).getCourseTitle());
        coursePersonnel.setText("함께 좋아하는 사람수: " + courseList.get(i).getCourseRival());

        v.setTag(courseList.get(i).getCourseID());


        // (19) 삭제버튼 이벤트 추가
        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // CourseListAdapter.java에서 가져와서 복붙 (19)
                // 정상적으로 ID 값을 입력했을 경우 중복체크 시작
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // 해당 웹사이트에 접속한 뒤 특정한 response(응답)을 다시 받을 수 있도록 한다
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            // 만약 삭제할 수 있다면
                            if (success) {
                                new CustomConfirmDialog().showConfirmDialog(parent.getActivity(),"삭제하였습니다.",true);

                                // 삭제한 만큼 코스도 빼준다.
                                StatisticsFragment.totalCredit -= courseList.get(i).getCourseCredit();
                                StatisticsFragment.credit.setText(StatisticsFragment.totalCredit + "개");
                                courseList.remove(i);    // 리스트에서 삭제
                                notifyDataSetChanged();  // 바뀐걸 적용
                            }

                            // 삭제 실패
                            else {
                                new CustomConfirmDialog().showConfirmDialog(parent.getActivity(),"삭제를 실패하였습니다.",true);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 실질적으로 삭제할 수 있도록 생성자를 통해 객체를 만든다. (유저 ID, responseListener)
                // 그리고 어떤 회원이 어떤 강의를 삭제한다는 데이터는 DB에 넣어야 한다.
                // DeleteRequest.java를 만들어줘야 한다.
                DeleteRequest deleteRequest = new DeleteRequest(userID, courseList.get(i).getCourseID() + "", responseListener);  // + ""를 붙이면 문자열 형태로 바꿈
                RequestQueue queue = Volley.newRequestQueue(parent.getActivity());
                queue.add(deleteRequest);
            }
        });


        // 버튼 이벤트까지 마친 뒤 정상적으로 return 한다.
        return v;
    }
}