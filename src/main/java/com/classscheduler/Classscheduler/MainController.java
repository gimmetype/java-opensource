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
    private ArrayList<Timetable> classes;

    MainController() {
        classes = new ArrayList<Timetable>();
        for (int i = 0; i < 3746; i++) {
            classes.add(new Timetable());
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
                    if (time.length() == 0) continue;
                    System.out.println("idx : " + idx);
                    try {
                        switch (time.charAt(0)) {
                            case '월':
                                day = 0;
                                break;
                            case '화':
                                day = 1;
                                break;
                            case '수':
                                day = 2;
                                break;
                            case '목':
                                day = 3;
                                break;
                            case '금':
                                day = 4;
                                break;
                            case '토':
                                day = 5;
                                break;
                            case '일':
                                day = 6;
                                break;
                        }
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
                        classes.get(idx).set_time(day, t);
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

    @RequestMapping("/class/{idx}")
    @ResponseBody
    public String getClass(@PathVariable("idx") int id) {
        System.out.println("INPUT :  " + id);
        String ret = new String();
        Timetable obj = classes.get(id);
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
