package com.example.ggavi.registeration.ahn3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ggavi.registeration.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by com on 2016-10-14.
 */
public class open2_TabFragment2 extends Fragment {

    static final int ITEM_SIZE =17;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.open2_tab_fragment_2,container,false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        List<open2_CourseItem> items = new ArrayList<>();
        open2_CourseItem[] item = new open2_CourseItem[ITEM_SIZE];
        item[0] = new open2_CourseItem(R.drawable.boramae_park_pic, "Boramae Park");
        item[1] = new open2_CourseItem(R.drawable.dream_forest_pic, "Dream Forest");
        item[2] = new open2_CourseItem(R.drawable.gildong_park_pic, "Gildong Park");
        item[3] = new open2_CourseItem(R.drawable.independence_park_pic, "Independence Park");
        item[4] = new open2_CourseItem(R.drawable.iris_garden_pic, "Iris Garden");
        item[5] = new open2_CourseItem(R.drawable.jungnang_park_pic, "Jungnang Park");
        item[6] = new open2_CourseItem(R.drawable.namsan_park_pic, "Namsan Park");
        item[7] = new open2_CourseItem(R.drawable.olympic_park_pic, "Olympic Park");
        item[8] = new open2_CourseItem(R.drawable.pureun_arboretum_pic, "Pureun Arboretum");
        item[9] = new open2_CourseItem(R.drawable.sajik_park_pic, "Sajik Park");
        item[10] = new open2_CourseItem(R.drawable.seonyudo_park_pic, "Seonyudo Park");
        item[11] = new open2_CourseItem(R.drawable.seoseoul_park_pic, "Seoseoul Park");
        item[12] = new open2_CourseItem(R.drawable.seoul_forest_pic, "Seoul Forest");
        item[13] = new open2_CourseItem(R.drawable.tabgol_park_pic, "Tabgol Park");
        item[14] = new open2_CourseItem(R.drawable.world_cup_park_pic, "World Cup Park");
        item[15] = new open2_CourseItem(R.drawable.yeouido_park_pic, "Yeouido Park");
        item[16] = new open2_CourseItem(R.drawable.yongsan_park_pic, "Yongsan Park");





        for (int i = 0; i < ITEM_SIZE; i++) {

            items.add(item[i]);
        }

        recyclerView.setAdapter(new open2_CourseAdapter2(getActivity(), items, R.layout.open2_tab_fragment_2));

        return v;
    }
}