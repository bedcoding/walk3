package com.example.ggavi.registeration.ahn1;

//import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ggavi.registeration.R;
import com.example.ggavi.registeration.ahn1.Course;

import java.util.List;

// (20) 하나의 코스 순위 정보를 불러올 수 있도록 하는 기능
// ~ Adapter.java 파일을 가져와서 수정하여 이 클래스를 만들었다.

public class RankListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courseList;      // Course가 들어가는 리스트를 만들어줌
    private Fragment parent;




    public RankListAdapter(Context context, List<Course> courseList, Fragment parent)  //자신을 불러낸 부모 Fragment를 담을 수 있도록 한다.
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

    @Override   // 각각 만들었던 디자인에 있는 내용들을 매칭시켜준다.
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        // 가장 먼저 레이아웃에 rank를 매칭시켜준다.
        View v = View.inflate(context, R.layout.rank, null);  // rank.xml

        // rank.xml이라는 레이아웃에 있는 모든 원소가 하나의 변수로써 자리잡게 되었다.
        TextView rankTextView = (TextView) v.findViewById(R.id.rankTextView);
        TextView courseTitle = (TextView) v.findViewById(R.id.courseTitle);

        rankTextView.setText((i+1) + "위");   // 몇 등인지 보여줌

        // 1위가 아닐 경우 모두 같은 배경색
        if(i != 0)
        {
            rankTextView.setBackgroundColor(parent.getResources().getColor(R.color.whiteBlack));
        }

        courseTitle.setText(courseList.get(i).getCourseTitle());

        v.setTag(courseList.get(i).getCourseID());
        return v;
    }
}

