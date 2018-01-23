package com.show.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.com.shows.utils.Utilty;
import com.shows.photos.PublishActivity;
import com.shows.typeinfoallcativity.TypeInfoActivity;
import com.yixinke.shows.R;

/**
 * Created by Administrator on 2016/3/28 0028.
 */
public class FragmentO extends Fragment  implements View.OnClickListener{

    private View v;

    TextView type_house,type_second_hand_info,type_full_time_info,type_part_time_info,type_food_info
            ,type_car_info,type_photography_info,type_education_info,type_medical_info;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_type_info, null, false);
        initView();

        ImageView reseale = (ImageView) v.findViewById(R.id.add_release);
        reseale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PublishActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("talkIs","talkIs");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return v;
    }

    private void initView() {
        type_house = (TextView) v.findViewById(R.id.type_house_info);
        type_house.setOnClickListener(this);
        type_second_hand_info = (TextView) v.findViewById(R.id.type_second_hand_info);
        type_second_hand_info.setOnClickListener(this);
        type_full_time_info = (TextView) v.findViewById(R.id.type_full_time_info);
        type_full_time_info.setOnClickListener(this);
        type_part_time_info = (TextView) v.findViewById(R.id.type_part_time_info);
        type_part_time_info.setOnClickListener(this);
        type_food_info = (TextView) v.findViewById(R.id.type_food_info);
        type_food_info.setOnClickListener(this);
        type_car_info = (TextView) v.findViewById(R.id.type_car_info);
        type_car_info.setOnClickListener(this);
        type_photography_info = (TextView) v.findViewById(R.id.type_photography_info);
        type_photography_info.setOnClickListener(this);
        type_education_info = (TextView) v.findViewById(R.id.type_education_info);
        type_education_info.setOnClickListener(this);
        type_medical_info = (TextView) v.findViewById(R.id.type_medical_info);
        type_medical_info.setOnClickListener(this);
    }

    Intent intent;
    Bundle bundle;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.type_house_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_house.getText().toString());
                bundle.putString("typeid", "0");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_second_hand_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_second_hand_info.getText().toString());
                bundle.putString("typeid", "1");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_full_time_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_full_time_info.getText().toString());
                bundle.putString("typeid", "2");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_part_time_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_part_time_info.getText().toString());
                bundle.putString("typeid", "3");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_food_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_food_info.getText().toString());
                bundle.putString("typeid", "4");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_car_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_car_info.getText().toString());
                bundle.putString("typeid", "5");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_photography_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_photography_info.getText().toString());
                bundle.putString("typeid", "6");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_education_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_education_info.getText().toString());
                bundle.putString("typeid","7");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.type_medical_info :
                intent = new Intent(getActivity(), TypeInfoActivity.class);
                bundle = new Bundle();
                bundle.putString("title",type_medical_info.getText().toString());
                bundle.putString("typeid", "8");
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }
    }
}
