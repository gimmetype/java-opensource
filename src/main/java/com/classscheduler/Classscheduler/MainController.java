package com.classscheduler.Classscheduler;

import com.opencsv.exceptions.CsvException;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
@Controller
public class MainController {
    private ArrayListMultimap<Integer, Integer> map_key_grade;
    private ArrayListMultimap<String, Integer> map_key_major;
    private ArrayList<Timetable> lectures;
    private ArrayList<String[]> lecture_data;
    enum Column {
        INDEX, GRADE, 이수구분, SUBJECT_ID, SUBJECT_NAME, CLASS, CREDIT, COLLEGE, DEPARTMENT, 대표교수소속, PROF, INFO
    }

    MainController() {
        lectures = new ArrayList<>();
        lecture_data = new ArrayList<>();
        for (int i = 0; i < 3746; i++) {
            lectures.add(new Timetable());
            lecture_data.add(new String[0]);
        }
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("data/info.csv").getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                int idx = Integer.parseInt(row[0]);
                String timedata = row[11];
                String[] times = timedata.split("\\$");
                for (String time : times) {
                    time = time.strip();
                    int day = -1;
                    if (time.isEmpty()) continue;
                    System.out.println("idx : " + idx);
                    try {
                        day = switch (time.charAt(0)) {
                            case '월' -> 0;
                            case '화' -> 1;
                            case '수' -> 2;
                            case '목' -> 3;
                            case '금' -> 4;
                            case '토' -> 5;
                            case '일' -> 6;
                            default -> day;
                        };
                    } catch (StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        System.out.println(idx + " ERROR!");
                    }
                    if (day == -1) {
                        System.out.println(idx + " is error");
                        continue;
                    } else System.out.print((char) ((char) day + 'A'));
                    String[] timevalues = time.split(" ");
                    for (int i = 1; i < timevalues.length; i++) {
                        timevalues[i] = timevalues[i].strip();
                        if (!Character.isDigit(timevalues[i].charAt(0))) {
                            timevalues[i] = timevalues[i].replace(',', '0');
                        }
                        int t = Integer.parseInt(timevalues[i]);
                        lectures.get(idx).set_time(day, t);
                        System.out.print(" " + t);
                    }
                    System.out.println(" ");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("data/lecture_raw_data.csv").getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            int idx = 1;
            for (String[] row : rows) {
                lecture_data.set(idx++, row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/index")
    public String index() {
        return "desktop-1440px";
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        try {
            return String.valueOf(new ClassPathResource("data/info.csv").getURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/lecture/{idx}")
    @ResponseBody
    public String[] getLecture(@PathVariable("idx") int id) {
        return lecture_data.get(id);
    }

    @GetMapping("/lectures")
    @ResponseBody
    public String[][] getLectures(@RequestParam(value = "ids") int[] ids) {
        String[][] ret = new String[ids.length][];
        for (int i = 0; i < ids.length; i++) {
            ret[i] = lecture_data.get(ids[i]);
        }
        return ret;
    }

    @RequestMapping("/class/{idx}")
    @ResponseBody
    public String getClass(@PathVariable("idx") int id) {
        System.out.println("INPUT :  " + id);
        String ret = "";
        Timetable obj = lectures.get(id);
        for (int i = 0; i < obj.DAYS; i++) {
            ret += (char) ('A' + (char) i);
            for (int j = 0; j < obj.TIMES; j++) {
                if (obj.chk_time(i, j)) ret += " " + Integer.toString(j);
            }
            ret += '\n';
        }
        return ret;
    }
}
