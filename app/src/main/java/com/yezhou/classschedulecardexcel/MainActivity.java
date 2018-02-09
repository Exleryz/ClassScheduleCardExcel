package com.yezhou.classschedulecardexcel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.yezhou.classschedulecardexcel.Util.FileOperation;
import com.yezhou.classschedulecardexcel.dao.DBHelperImpl;
import com.yezhou.classschedulecardexcel.entity.WeekDay;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnFindFile, btnShow, btnRefresh;
    Spinner spinnerGrade;
    Spinner spinnerClass;
    private List<String> listGrade;
    private List<String> listClass;
    private ArrayAdapter<String> adapterGrade;
    private ArrayAdapter<String> adapterClass;
    DBHelperImpl dbHelper = null;
    SQLiteDatabase db = null;
    String path;

    private GridView detailCource;
    private String[][] contents;
    private AbsGridAdapter secondAdapter;
    private List<String> dataList;
    private ArrayAdapter<String> spinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFindFile = (Button) findViewById(R.id.btn_find_file);
        btnFindFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openFileTree();
                }
            }
        });

        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(this);

//        tvShowData = (TextView) findViewById(R.id.tv_show_data);
        spinnerGrade = (Spinner) findViewById(R.id.spinner_grade);

        spinnerClass = (Spinner) findViewById(R.id.spinner_class);
        listGrade = new ArrayList<String>();
        listClass = new ArrayList<String>();

        // 添加数据
        dbHelper = new DBHelperImpl(this, "my.db", null, 1);
        db = dbHelper.getWritableDatabase();
        listGrade = dbHelper.findGrade(db);
        //适配器
        adapterGrade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listGrade);
        //设置样式
        adapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinnerGrade.setAdapter(adapterGrade);

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listClass = dbHelper.findClass(db, adapterGrade.getItem(position));
                adapterClass = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, listClass);
//        //设置样式
                adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //加载适配器
                spinnerClass.setAdapter(adapterClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




//        //适配器
        adapterClass = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listClass);
//        //设置样式
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //加载适配器
        spinnerClass.setAdapter(adapterClass);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<WeekDay> list = dbHelper.findCurriculum(db, adapterClass.getItem(position));

//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < list.size(); i++) {
//                    sb.append(list.get(i).getWeekDayName() + "\n");
//                    for (int j = 0; j < list.get(i).getSections().size(); j++) {
//                        sb.append(list.get(i).getSections().get(j).getSectionContent() + "\n\n");
//                    }
//                }
//                tvShowData.setText(sb);
                fillStringArray(list);
                secondAdapter = new AbsGridAdapter(MainActivity.this);
                secondAdapter.setContent(contents, 6, 7);
                detailCource.setAdapter(secondAdapter);
                Toast.makeText(MainActivity.this, "sldkfjlskdj", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnShow = (Button) findViewById(R.id.btn_show);
        btnShow.setOnClickListener(this);




        detailCource = (GridView)findViewById(R.id.courceDetail);

    }

    public void openFileTree() {
        Intent intentOpenFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentOpenFile.setType("*/*");
        intentOpenFile.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intentOpenFile, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileTree();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = uri.getPath();
                FileOperation.file1(path, this);
//                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);
                FileOperation.file1(path, this);
                Toast.makeText(MainActivity.this, path+"222222", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    public void fillStringArray(List<WeekDay> list) {

        contents = new String[6][7];    // 后一位是列
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                contents[i][j]=" _ ";
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).getSections().size(); j++) {
                String[] s = list.get(i).getSections().get(j).getSectionTime().split("-");
                int k = Integer.parseInt(s[1]);
                if (Integer.parseInt(list.get(list.size()-1).getWeekDayName()) == 7) {
                    contents[k/2-1][Integer.parseInt(list.get(i).getWeekDayName())-1] = list.get(i).getSections().get(j).getId() + "_" + list.get(i).getSections().get(j).getSectionContent();   // i
                } else {
                    if (Integer.parseInt(list.get(i).getWeekDayName()) == 6) {
                        contents[k/2-1][Integer.parseInt(list.get(i).getWeekDayName())] = list.get(i).getSections().get(j).getId() + "_" + list.get(i).getSections().get(j).getSectionContent();   // i
                    }else {
                        contents[k/2-1][Integer.parseInt(list.get(i).getWeekDayName())-1] = list.get(i).getSections().get(j).getId() + "_" + list.get(i).getSections().get(j).getSectionContent();   // i
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:
                // 添加数据
                dbHelper = new DBHelperImpl(this, "my.db", null, 1);
                db = dbHelper.getWritableDatabase();
                listGrade = dbHelper.findGrade(db);
                //适配器
                adapterGrade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listGrade);
                //设置样式
                adapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //加载适配器
                spinnerGrade.setAdapter(adapterGrade);

                spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        listClass = dbHelper.findClass(db, adapterGrade.getItem(position));
                        adapterClass = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, listClass);
//        //设置样式
                        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //加载适配器
                        spinnerClass.setAdapter(adapterClass);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });




//        //适配器
                adapterClass = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listClass);
//        //设置样式
                adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //加载适配器
                spinnerClass.setAdapter(adapterClass);
                spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        List<WeekDay> list = dbHelper.findCurriculum(db, adapterClass.getItem(position));

//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < list.size(); i++) {
//                    sb.append(list.get(i).getWeekDayName() + "\n");
//                    for (int j = 0; j < list.get(i).getSections().size(); j++) {
//                        sb.append(list.get(i).getSections().get(j).getSectionContent() + "\n\n");
//                    }
//                }
//                tvShowData.setText(sb);
                        fillStringArray(list);
                        secondAdapter = new AbsGridAdapter(MainActivity.this);
                        secondAdapter.setContent(contents, 6, 7);
                        detailCource.setAdapter(secondAdapter);
                        Toast.makeText(MainActivity.this, "sldkfjlskdj", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                break;
            case R.id.btn_show:
                Intent intent = new Intent(this, ShowRecent.class);
                intent.putExtra("className", spinnerClass.getSelectedItem().toString());
                startActivity(intent);
                break;
        }
    }
}
