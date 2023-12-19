package com.classscheduler.Classscheduler;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.ArrayList;
public class Timetable {
    @Getter
    @Setter
    public static class TimeInfo {
        int idx;
        int length;
        TimeInfo(int _idx, int _length) {
            idx = _idx;
            length = _length;
        }
    }
    public static final int DAYS = 6;
    public static final int TIMES = 14;
    private ArrayList<ArrayList<Boolean>> timetable;
    private ArrayList<ArrayList<TimeInfo>> export_format;
    Timetable() {
        timetable = new ArrayList<>();
        export_format = new ArrayList<>();
        for (int i = 0; i < DAYS; i++) {
            timetable.add(new ArrayList<>());
            export_format.add(new ArrayList<>());
            for (int j = 0; j < TIMES; j++) {
                timetable.get(i).add(false);
                export_format.get(i).add(new TimeInfo(0, 0));
            }
        }
    }
    public void make_export_format() {
        int start = 0;
        int streak = 0;
        for (int i = 0; i < DAYS; i++) {
            for (int j = 0; j < TIMES; j++) {
                if (timetable.get(i).get(j)) {
                    if (streak == 0) start = j;
                    streak++;
                } else if (streak != 0) {
                    export_format.get(i).get(start).length = streak;
                    streak = 0;
                }
            }
            if (streak != 0) {
                export_format.get(i).get(start).length = streak;
                streak = 0;
            }
        }
    }
    public void set_time(int day, int time) {
        timetable.get(day).set(time, true);
    }
    public boolean chk_time(int day, int time) {
        return timetable.get(day).get(time);
    }
    public int get_time(int day, int time) {
        return export_format.get(day).get(time).length;
    }
    public boolean has_collision(Timetable other) {
        for (int i = 0; i < DAYS; i++) {
            for (int j = 0; j < TIMES; j++) {
                if (chk_time(i, j) && other.chk_time(i, j)) return true;
            }
        }
        return false;
    }
}
