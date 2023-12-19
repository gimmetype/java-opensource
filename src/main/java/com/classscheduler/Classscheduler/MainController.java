package com.classscheduler.Classscheduler;

import com.opencsv.exceptions.CsvException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ClassPathResource;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ArrayListMultimap;
@Controller
public class MainController {
    @Setter
    @Getter
    class LectureKey {
        String major;
        int grade;
        LectureKey(String _major, int _grade) {
            major = _major;
            grade = _grade;
        }
        @Override
        public int hashCode() {
            return Objects.hash(major, grade);
        }
        @Override
        public boolean equals(Object o) {
            if(this == o) return true;
            if(o == null || getClass() != o.getClass()) return false;
            LectureKey other = (LectureKey) o;
            return major.equals(((LectureKey) o).major) && grade == ((LectureKey) o).grade;
        }
    }
    private ArrayListMultimap<LectureKey, Integer> map_key_comp;
    private ArrayListMultimap<String, Integer> map_key_name;
    private ArrayListMultimap<Integer, Integer> map_key_grade;
    private ArrayListMultimap<String, Integer> map_key_department;
    private ArrayList<Timetable> lectures;
    private ArrayList<String[]> lecture_data;
    enum Column {
        INDEX, GRADE, 이수구분, SUBJECT_ID, SUBJECT_NAME, CLASS, CREDIT, COLLEGE, DEPARTMENT, 대표교수소속, PROF, INFO
    }

    MainController() {
        lectures = new ArrayList<>();
        lecture_data = new ArrayList<>();
        map_key_name = ArrayListMultimap.create();
        map_key_comp = ArrayListMultimap.create();
        map_key_grade = ArrayListMultimap.create();
        map_key_department = ArrayListMultimap.create();
        for (int i = 0; i < 3746; i++) {
            lectures.add(new Timetable());
            lecture_data.add(new String[0]);
        }
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new ClassPathResource("data/info.csv").getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            for (String[] row : rows) {
                int idx = Integer.parseInt(row[Column.INDEX.ordinal()]);
                String timedata = row[Column.INFO.ordinal()];
                String[] times = timedata.split("\\$");
                map_key_name.put(row[Column.SUBJECT_NAME.ordinal()], idx);
                map_key_comp.put(new LectureKey(row[Column.DEPARTMENT.ordinal()],
                        Integer.parseInt(row[Column.GRADE.ordinal()])), idx);
                map_key_grade.put(Integer.parseInt(row[Column.GRADE.ordinal()]), idx);
                map_key_department.put(row[Column.DEPARTMENT.ordinal()], idx);
                for (String time : times) {
                    time = time.strip();
                    int day = -1;
                    if (time.isEmpty()) continue;
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
                    }
                    String[] timevalues = time.split(" ");
                    for (int i = 1; i < timevalues.length; i++) {
                        timevalues[i] = timevalues[i].strip();
                        if (!Character.isDigit(timevalues[i].charAt(0))) {
                            timevalues[i] = timevalues[i].replace(',', '0');
                        }
                        int t = Integer.parseInt(timevalues[i]);
                        lectures.get(idx).set_time(day, t);
                    }
                }
                lectures.get(idx).make_export_format();
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

    @GetMapping("/lecture")
    @ResponseBody
    public List<Integer> searchSubject(@RequestParam(value = "name") String subject_name) {
        return map_key_name.get(subject_name);
    }

    @GetMapping("/lecture/keys")
    @ResponseBody
    public List<Integer> searchSubject(@RequestParam(required = false, value = "grade") Integer grade,
                                       @RequestParam(required = false, value = "department") String department) {
        if (grade == null && department == null) return new ArrayList<>();
        else if (grade == null) {
            return map_key_department.get(department);
        } else if (department == null) {
            return map_key_grade.get(grade);
        } else
            return map_key_comp.get(new LectureKey(department, grade));
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

    @GetMapping("/gettimetable")
    @ResponseBody
    public Timetable.TimeInfo[][] mergeTimetable(@RequestParam(value = "ids") int[] ids) {
        var ret = new Timetable.TimeInfo[Timetable.DAYS][Timetable.TIMES];
        for (int i = 0; i < Timetable.DAYS; i++) {
            for (int j = 0; j < Timetable.TIMES; j++) {
                ret[i][j] = new Timetable.TimeInfo(-1, 0);
            }
        }
        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            var lecture = lectures.get(id);
            for (int d = 0; d < Timetable.DAYS; d++) {
                for (int t = 0; t < Timetable.TIMES; t++) {
                    int n = lecture.get_time(d, t);
                    if (n != 0) ret[d][t] = new Timetable.TimeInfo(i, n);
                }
            }
        }
        return ret;
    }

    @PostMapping("/calculate")
    public List<Integer> calculateTimetable(@RequestBody ArrayList<Integer>[] requestBody) {
        int cnt = 0;
        int idx = 0;
        int groupidx = 0;
        ArrayList<Integer>[] groups = new ArrayList[requestBody.length];
        Integer[] map;
        Integer[] getgroup;
        for (ArrayList group: requestBody) {
           cnt += group.size();
           groups[groupidx] = new ArrayList<>();
           for (int i = 0; i < group.size(); i++) {
               groups[groupidx].add(idx++);
           }
           groupidx++;
        }
        map = new Integer[cnt];
        getgroup = new Integer[cnt];
        idx = 0;
        groupidx = 0;
        for (ArrayList group: requestBody) {
            for (int i = 0; i < group.size(); i++) {
                getgroup[idx] = groupidx;
                map[idx++] = groups[groupidx].get(i);
            }
            groupidx++;
        }
        Graph g = new Graph(cnt);
        for (int i = 0; i < cnt; i++) {
            for (int j = i + 1; j < cnt; j++) {
                if (getgroup[i].equals(getgroup[j])) continue;
                if (lectures.get(map[i]).has_collision(lectures.get(map[j]))) {
                    g.add_edge(i, j);
                }
            }
        }
        Integer[] ans = MinimumVertexCoverSolver.NaiveSolver(g, groups);
        ArrayList<Integer> ret = new ArrayList<>();
        for (Integer v : ans) {
            ret.add(map[v]);
        }
        return ret;
    }
}
