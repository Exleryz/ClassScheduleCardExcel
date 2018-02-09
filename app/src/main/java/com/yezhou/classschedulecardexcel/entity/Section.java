package com.yezhou.classschedulecardexcel.entity;

/**
 * Created by Administrator on 2018/1/14.
 */

public class Section {

    private int id;
    private String sectionTime;
    private String sectionContent;
    private String className;
    private int row;
    private int col;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", sectionTime='" + sectionTime + '\'' +
                ", sectionContent='" + sectionContent + '\'' +
                ", row=" + row +
                ", col=" + col +
                '}';
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getSectionTime() {
        return sectionTime;
    }

    public void setSectionTime(String sectionTime) {
        this.sectionTime = sectionTime;
    }

    public String getSectionContent() {
        return sectionContent;
    }

    public void setSectionContent(String sectionContent) {
        this.sectionContent = sectionContent;
    }
}
