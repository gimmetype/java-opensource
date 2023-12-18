package com.classscheduler.Classscheduler;

import java.sql.Time;
import java.util.ArrayList;
public class Timetable {
    public final int DAYS = 7;
    public final int TIMES = 14;
    private ArrayList<ArrayList<Boolean>> timetable;
    Timetable() {
        timetable = new ArrayList<ArrayList<Boolean>>();
        for (int i = 0; i < DAYS; i++) {
            timetable.add(new ArrayList<Boolean>());
            for (int j = 0; j < TIMES; j++) {
                timetable.get(i).add(false);
            }
        }
    }
    public void set_time(int day, int time) {
        timetable.get(day).set(time, true);
    }
    public boolean chk_time(int day, int time) {
        return timetable.get(day).get(time);
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
