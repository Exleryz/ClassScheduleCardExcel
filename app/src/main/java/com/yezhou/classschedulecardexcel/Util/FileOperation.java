package com.yezhou.classschedulecardexcel.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.yezhou.classschedulecardexcel.dao.DBHelperImpl;
import com.yezhou.classschedulecardexcel.entity.Section;
import com.yezhou.classschedulecardexcel.entity.WeekDay;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/1/14.
 */

public class FileOperation {

    public static String file1(String path, Context context) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + changePath(path));
            SharedPreferences.Editor editor = context.getSharedPreferences("SharedPreferences_data",MODE_PRIVATE).edit();
            editor.putString("path", Environment.getExternalStorageDirectory() + changePath(path));    // 储存String类型数据
            editor.apply();
            readXLS(file, context);
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            String readline = "";
//            StringBuffer sb = new StringBuffer();
//            while ((readline = br.readLine()) != null) {
//                System.out.println("readline:" + readline);
//                sb.append(readline);
//            }
//            br.close();
//            System.out.println("读取成功：" + sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String changePath(String path) {
        int i = path.indexOf("/",1);
        String newPath = path.substring(i);
        return newPath;
    }

    public static void readXLS(final File file, Context context) {
        final StringBuilder sb = new StringBuilder();
        try {
            Workbook book = Workbook.getWorkbook(file);
            for (int n = 0; n < book.getNumberOfSheets(); n++) {    // 获取sheet页的数目
                List<Integer> arrayList = new ArrayList<>();    // 存放节数
                Map<String, WeekDay> map = new HashMap<String, WeekDay>();    // 存放班级的单天课程
                Sheet sheet = book.getSheet(n);
                for (int i = 2; i< sheet.getColumns(); i++) {    // 初始化map class表
                    map.put(sheet.getCell(i,0).getContents(), null);
                    DBHelperImpl dbHelper = new DBHelperImpl(context, "my.db", null, 1);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    dbHelper.insertClass(db, sheet.getName(), sheet.getCell(i,0).getContents());
                }
                int j = 0;
                int i = 0;
                arrayList.add(0);
                for (i = 1; !sheet.getCell(0, i).getContents().equals("备注"); i++, j++) {    // 解析一个年级的课有几天每天有几节课
                    if (sheet.getCell(0, i).getContents().contains("星期")) {
                        if (j > 0) {
                            arrayList.add(j);
//                                    Log.d("file", j + "" +sheet.getCell(0, i).getContents());    // 16级表 6 12 18 24 30 31
                        }
                    }
                }
                arrayList.add(j);
                arrayList.add(arrayList.get(arrayList.size() - 1) + 1);    // 备注
//                        for (int k = 0; k < arrayList.size(); k++) {    // 测试天数
//                            System.out.println(arrayList.get(k));
//                        }

                int row = i;    // 真实行数 - 1 从0开始数
                int col = sheet.getColumns();    // 真实列数 从1开始数
                int l;

                WeekDay wd = null;
                List<Section> sections = null;
                for (int k = 1; k < arrayList.size(); k++) {
                    for (i = 2; i < col; i++) {    // 班级循环
                        Section s = null;
                        wd = new WeekDay();
                        sections = new ArrayList<>();
                        for (j = arrayList.get(k - 1) + 1, l = 1; j <= arrayList.get(k); j++, l++) {    // 课程循环
                            s = new Section();
                            s.setRow(j);
                            s.setCol(i);
                            s.setSectionTime(l * 2 - 1 + "-" + l * 2);
                            Log.d("file_rc", i + "  " + " " + j);
                            s.setSectionContent(sheet.getCell(i, j).getContents());
                            sb.append(s.toString());
                            sections.add(s);
                        }    // 一天课程
                        wd.setSections(sections);
                        wd.setWeekDayName(k+"");
                        map.put(sheet.getCell(i,0).getContents(), wd);
                        Log.d("file_fg", "--------------------------");
                    }
                    for (Map.Entry<String, WeekDay> m : map.entrySet()) {
//                                 调用数据库
                        DBHelperImpl dbHelper = new DBHelperImpl(context, "my.db", null, 1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        dbHelper.insertCURRICULUM(db, m.getKey(), m.getValue());
                    }
                }
            }
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int writeExcel(Section section, Context context) {
        int times;
        try {
            SharedPreferences pref = context.getSharedPreferences("SharedPreferences_data",MODE_PRIVATE);
            String path = pref.getString("path", "");
            times = pref.getInt("times", 0);

            Workbook rwb = Workbook.getWorkbook(new File(path));
            WritableWorkbook wwb = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory() + "/" +times +".xls"), rwb);// copy
            WritableSheet ws = wwb.getSheet(getSheetIdByName(section.getClassName()));
            WritableCell wc = ws.getWritableCell(section.getCol(), section.getRow());
            // 判断单元格的类型,做出相应的转换
            Label label = (Label) wc;
            label.setString(section.getSectionContent());
            Log.d("admin_ec", section.getSectionContent());
            Log.d("admin_ec", label.getString());
            wwb.write();
            wwb.close();
            rwb.close();

            SharedPreferences.Editor editor = context.getSharedPreferences("SharedPreferences_data",MODE_PRIVATE).edit();
            times++;
            editor.putInt("times", times);    // 增加次数
            editor.putString("path", Environment.getExternalStorageDirectory() + "/" + (times-1) +".xls");
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static int getSheetIdByName(String className) {
        int a = Integer.parseInt(className.substring(0,2));
        if (a == 13) {return 0;}
        if (a == 14) {return 1;}
        if (a == 15) {return 2;}
        if (a == 16) {return 3;}
        return 0;
    }

}
