package com.yezhou.classschedulecardexcel;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yezhou.classschedulecardexcel.Util.FileOperation;
import com.yezhou.classschedulecardexcel.dao.DBHelperImpl;
import com.yezhou.classschedulecardexcel.entity.Section;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnSureData;
    Button btnSureExcel;
    Button btnBack;

    TextView tvClass;
    TextView tvSection;
    TextView tvWeek;
    EditText etCurriculum;
    int id;
    Section section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btnBack = (Button) findViewById(R.id.d_btn_back);
        btnSureData = (Button) findViewById(R.id.d_btn_sure_database);
        btnSureExcel = (Button) findViewById(R.id.d_btn_sure_excel);
        tvClass = (TextView) findViewById(R.id.tv_class);
        tvSection = (TextView) findViewById(R.id.tv_section);
        tvWeek = (TextView) findViewById(R.id.tv_week);
        etCurriculum = (EditText) findViewById(R.id.et_curriculum);

        btnBack.setOnClickListener(this);
        btnSureData.setOnClickListener(this);
        btnSureExcel.setOnClickListener(this);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        Log.d("admin_d", id + "");

        DBHelperImpl dbHelper = new DBHelperImpl(this, "my.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        section = dbHelper.findSectionById(db, id);

        tvClass.setText(section.getClassName());
        tvSection.setText(section.getSectionTime());
        tvWeek.setText(numToWeek(section.getRow(), section.getClassName()));
        etCurriculum.setText(section.getSectionContent());
    }

    private String numToWeek(int row, String className) {
        String week = null;
        if (row >= 1 && row <=6) {
            week = "星期一";
        } else if (row>= 7 && row <= 12){
            week = "星期二";
        } else if (row >= 13 && row <= 18) {
            week = "星期三";
        } else if (row >= 19 && row <= 24) {
            week = "星期四";
        } else if (row >= 25 && row <= 30) {
            week = "星期五";
        } else if (row == 31) {
            if (className.substring(0,2).equals("15")) {
                week = "星期六";
            } else {
                week = "备注";
            }
        } else {
        week = "备注";
        }
        return week;

    }

    @Override
    public void onClick(View v) {
        section.setSectionContent(etCurriculum.getText().toString());
        switch (v.getId()) {
            case R.id.d_btn_sure_database:
                DBHelperImpl dbHelper = new DBHelperImpl(this, "my.db", null, 1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.updateSectionById(db, id, etCurriculum.getText().toString());
                Toast.makeText(this, "数据库修改成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.d_btn_sure_excel:
                FileOperation.writeExcel(section, this);
                Toast.makeText(this, "excel表修改成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.d_btn_back:
                finish();
                break;
        }
    }
}
