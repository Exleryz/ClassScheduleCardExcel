package com.yezhou.classschedulecardexcel.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/14.
 */

public class WeekDay {

    private String weekDayName;
    private List<Section> sections;

    public String getWeekDayName() {
        return weekDayName;
    }

    public void setWeekDayName(String weekDayName) {
        this.weekDayName = weekDayName;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}
