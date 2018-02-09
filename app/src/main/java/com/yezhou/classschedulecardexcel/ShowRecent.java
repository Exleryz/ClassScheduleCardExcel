package com.yezhou.classschedulecardexcel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.yezhou.classschedulecardexcel.dao.DBHelperImpl;
import com.yezhou.classschedulecardexcel.entity.Section;

public class ShowRecent extends AppCompatActivity {

    TextView tvOne;
    TextView tvTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recent);

        tvOne = (TextView) findViewById(R.id.tv_one);
        tvTwo = (TextView) findViewById(R.id.tv_two);
        tvTwo.setText("没有课");
        tvOne.setText("没有课 ");

        Intent intent = getIntent();
        String className = intent.getStringExtra("className");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.get(Calendar.DAY_OF_WEEK);
        c.get(Calendar.HOUR_OF_DAY);
        Log.d("admin",timeToSection(c.get(Calendar.HOUR_OF_DAY)));
        DBHelperImpl dbHelper = new DBHelperImpl(this, "my.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Section> contents = dbHelper.findSectionsBySection(db, className, c.get(Calendar.DAY_OF_WEEK));    // 先查询当天的课程

        for (int i = 0; i < contents.size(); i++) {
            if (contents.get(i).getSectionTime().equals(timeToSection(c.get(Calendar.HOUR_OF_DAY)))) {
                tvOne.setText(contents.get(i).getSectionContent());
                if (i+1<contents.size()) {
                    tvTwo.setText(contents.get(i+1).getSectionContent());
                }
                return;
            }
        }
    }

    public String timeToSection(int time) {
        String s = null;
        switch (time) {
            case 8:
            case 9:
            case 10:
                s="1-2";
                break;
            case 11:
            case 12:
                s="3-4";
                break;
            case 13:
            case 14:
            case 15:
                s="5-6";
                break;
            case 16:
            case 17:
                s="7-8";
                break;
            case 18:
            case 19:
                s="9-10";
                break;
            case 20:
            case 21:
                s="11-12";
                break;
            default:
                s="1-2";
                break;
        }
        return s;
    }

}
