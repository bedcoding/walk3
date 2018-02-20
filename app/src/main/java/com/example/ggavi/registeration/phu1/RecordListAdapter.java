package com.example.ggavi.registeration.phu1;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ggavi.registeration.R;

import java.util.List;

public class RecordListAdapter extends BaseAdapter {

    private Context context;
    private List<Record> recordList; //Notice가 들어가는 리스트를 만들어줌

    public RecordListAdapter(Context context, List<Record> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int i) //i는 position
    {
        return recordList.get(i);
    }

    @Override
    public long getItemId(int i) //i는 position
    {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) //i는 position
    {
        // 하나의 View로 만들어 줄 수 있도록 한다. (R.layout.notice로 배달)
        View v = View.inflate(context, R.layout.logged_in_record_item, null);
        TextView pedometer = (TextView) v.findViewById(R.id.pedometerRecord);
        TextView distance = (TextView) v.findViewById(R.id.distanceRecord);
        TextView calorie = (TextView) v.findViewById(R.id.calorieRecord);
        TextView time = (TextView) v.findViewById(R.id.timeRecord);
        TextView speed = (TextView) v.findViewById(R.id.speedRecord);
        TextView date = (TextView) v.findViewById(R.id.dateRecord);
        TextView serialNum = (TextView)v.findViewById(R.id.serialNum);
        TextView progress = (TextView)v.findViewById(R.id.progress);

        // noticeText를 현재 리스트에 있는 값으로 넣어줄 수 있도록 한다.
        pedometer.setText(recordList.get(i).getPedometer());
        distance.setText(recordList.get(i).getDistance());
        calorie.setText(recordList.get(i).getCalorie());
        time.setText(recordList.get(i).getTime());
        speed.setText(recordList.get(i).getSpeed());
        date.setText(recordList.get(i).getDate());
        String progressSaved = recordList.get(i).getProgress().trim();
        if(progressSaved.equals("skipped")||progressSaved.equals("")){
            progress.setText("목표를 지정하지 않았습니다.");
            progress.setBackgroundColor(Color.parseColor("#c5c5c5"));
        }else {
            if(Integer.parseInt(progressSaved)<=33){ //low
                progress.setBackgroundColor(Color.parseColor("#fc8080"));
            }else if(Integer.parseInt(progressSaved)>33&&Integer.parseInt(progressSaved)<=66){ //middle
                progress.setBackgroundColor(Color.parseColor("#fcc980"));
            }else if(Integer.parseInt(progressSaved)>66){ //high
                progress.setBackgroundColor(Color.parseColor("#80fcc9"));
            }

            progress.setText("목표 달성률: "+recordList.get(i).getProgress()+"%");
        }
        serialNum.setText(i+1+")");

        v.setTag(recordList.get(i).getUserId());
        return v;
    }


}