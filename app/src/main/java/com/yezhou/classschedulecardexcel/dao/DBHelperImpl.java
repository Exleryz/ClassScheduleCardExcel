package com.yezhou.classschedulecardexcel.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Editable;
import android.util.Log;

import com.yezhou.classschedulecardexcel.entity.Section;
import com.yezhou.classschedulecardexcel.entity.WeekDay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/14.
 */

public class DBHelperImpl extends SQLiteOpenHelper {

    private Context context;

    static final String CREATE_CLASS = "create table class (" +
            "id integer primary key autoincrement," +
            "gradename text," +
            "classname text)";    // 年级名 班级名

    static final String CREATE_CURRICULUM = "create table curriculum (" +
            "id integer primary key autoincrement," +
            "classname text," +
            "week text," +
            "section text," +
            "curriculumContent text," +
            "row integer," +
            "col integer)";    // 班级名 星期 节次 课程名 课程所在Excel表格中的行列

    public DBHelperImpl(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * 提供 班级名，年级名 插入 到班级表
     * @param db
     * @param gradeName
     * @param className
     */
    public void insertClass(SQLiteDatabase db, String gradeName, String className) {
        ContentValues cv = new ContentValues();
        cv.put("classname", className);
        cv.put("gradename", gradeName);
        db.insert("class", null, cv);
    }

    /**
     * 插入课程
     * @param db
     * @param className
     * @param wd
     */
    public void insertCURRICULUM(SQLiteDatabase db, String className, WeekDay wd) {
        ContentValues cv = new ContentValues();
        for (int i = 0; i < wd.getSections().size(); i++) {
            if (! wd.getSections().get(i).getSectionContent().isEmpty()) {
                cv.put("classname", className);
                cv.put("week", wd.getWeekDayName());
                cv.put("section", wd.getSections().get(i).getSectionTime());    // 节次
                cv.put("curriculumContent", wd.getSections().get(i).getSectionContent());
                cv.put("row", wd.getSections().get(i).getRow());
                cv.put("col", wd.getSections().get(i).getCol());
                db.insert("curriculum", null, cv);
            }
        }
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CURRICULUM);
        db.execSQL(CREATE_CLASS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<String> findGrade(SQLiteDatabase db) {
        Cursor cursor = db.query(true, "class", new String[] {"gradename"}, null, null, null, null, null, null, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {    // 遍历Cursor对象，去除数据并打印
                String gradename = cursor.getString(cursor.getColumnIndex("gradename"));
                Log.d("db_show", gradename);
                list.add(gradename);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> findClass(SQLiteDatabase db, String gradeName) {
        Cursor cursor = db.query(true, "class", new String[] {"classname"}, "gradename=?", new String[]{gradeName}, null, null, null, null, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {    // 遍历Cursor对象，去除数据并打印
                String className = cursor.getString(cursor.getColumnIndex("classname"));
                Log.d("db_show", className);
                list.add(className);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     *
     * @param db
     * @param className
     * @return
     */
    public List<WeekDay> findCurriculum(SQLiteDatabase db, String className) {
        Cursor cursor = db.query("curriculum",  null, "classname=?", new String[]{className}, null, null, "week", null);
        List<WeekDay> list = new ArrayList<>();
        WeekDay wd = new WeekDay();
        List<Section> sections = null;
        Section s = null;
        if (cursor.moveToFirst()) {
            do {    // 遍历Cursor对象，去除数据并打印
                String week = cursor.getString(cursor.getColumnIndex("week"));
                if (week.equals(wd.getWeekDayName())) {
                    s = new Section();
                    s.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    s.setSectionContent(cursor.getString(cursor.getColumnIndex("curriculumContent")));
                    s.setRow(cursor.getInt(cursor.getColumnIndex("row")));
                    s.setCol(cursor.getInt(cursor.getColumnIndex("col")));
                    s.setSectionTime(cursor.getString(cursor.getColumnIndex("section")));
                    sections.add(s);
                } else {
                    if (sections == null) {    // 第一次
                        wd = new WeekDay();
                        sections = new ArrayList<>();
                        wd.setWeekDayName(week);
                        s = new Section();
                        s.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        s.setSectionContent(cursor.getString(cursor.getColumnIndex("curriculumContent")));
                        s.setRow(cursor.getInt(cursor.getColumnIndex("row")));
                        s.setCol(cursor.getInt(cursor.getColumnIndex("col")));
                        s.setSectionTime(cursor.getString(cursor.getColumnIndex("section")));
                        sections.add(s);
                    } else {
                        wd.setSections(sections);
                        list.add(wd);
                        wd = new WeekDay();
                        sections = new ArrayList<>();
                        wd.setWeekDayName(week);
                        s = new Section();
                        s.setId(cursor.getInt(cursor.getColumnIndex("id")));
                        s.setSectionContent(cursor.getString(cursor.getColumnIndex("curriculumContent")));
                        s.setRow(cursor.getInt(cursor.getColumnIndex("row")));
                        s.setCol(cursor.getInt(cursor.getColumnIndex("col")));
                        s.setSectionTime(cursor.getString(cursor.getColumnIndex("section")));
                        sections.add(s);
                    }
                }
            } while (cursor.moveToNext());
            wd.setSections(sections);
            list.add(wd);
        }
        cursor.close();
        return list;
    }

    /**
     *
     */
    public Section findSectionById(SQLiteDatabase db, int id) {
        Cursor cursor = db.query("curriculum",  null, "id=?", new String[]{id+""}, null, null, null, null);
        Section s = new Section();
        if (cursor.moveToFirst()) {
            s.setId(cursor.getInt(cursor.getColumnIndex("id")));
            s.setSectionContent(cursor.getString(cursor.getColumnIndex("curriculumContent")));
            s.setRow(cursor.getInt(cursor.getColumnIndex("row")));
            s.setCol(cursor.getInt(cursor.getColumnIndex("col")));
            s.setSectionTime(cursor.getString(cursor.getColumnIndex("section")));
            s.setClassName(cursor.getString(cursor.getColumnIndex("classname")));
        }
        cursor.close();
        return s;
    }

    public void updateSectionById(SQLiteDatabase db, int id, String text) {
        ContentValues values = new ContentValues();
        values.put("curriculumContent", text);
        db.update("curriculum", values, "id=?", new String[] {id+""});
    }

    public List<Section> findSectionsBySection(SQLiteDatabase db, String className, int week) {
        Cursor cursor = db.query("curriculum",  null, "week=? and classname=?", new String[]{week+"", className}, null, null, "row", null);
        List<Section> sections = new ArrayList<>();
        Section section = null;
        if (cursor.moveToFirst()) {
            do {
                section = new Section();
                section.setSectionTime(cursor.getString(cursor.getColumnIndex("section")));
                section.setSectionContent(cursor.getString(cursor.getColumnIndex("curriculumContent")));
                sections.add(section);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return sections;
    }
}
