package com.shows.allactivty;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yixinke.shows.R;
import com.shows.view.BaseActivity;

public class ShoperInfoActivity extends BaseActivity {

    ImageView back;
    ListView info_list;
    TextView info_detail,info_title,info_price,info_likenum,info_look_detail;
    ImageView info_head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shoper_info);

        back  = (ImageView) findViewById(R.id.shop_info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        info_list= (ListView) findViewById(R.id.shop_info_listview);
        info_list.setAdapter(new MyInfoListAdpapter());

    }

    class MyInfoListAdpapter  extends BaseAdapter{

        /**
         * @param
         */
        public MyInfoListAdpapter() {
        }

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(ShoperInfoActivity.this).inflate(R.layout.shoper_info_listview_item, null);
            }

            info_title = (TextView) convertView.findViewById(R.id.show_info_title);
            info_detail = (TextView) convertView.findViewById(R.id.show_info_detail);
            info_price = (TextView) convertView.findViewById(R.id.show_info_price);
            info_likenum = (TextView) convertView.findViewById(R.id.show_info_likenum);
            info_look_detail = (TextView) convertView.findViewById(R.id.show_info_look_detail);
            info_head = (ImageView) convertView.findViewById(R.id.show_info_head);

            // 将图片显示任务增加到执行池，图片将被显示到ImageView当轮到此ImageView
//            imageLoader.displayImage(map.getHeader(), iv_pic, options);


            return convertView;
        }
    }
}

