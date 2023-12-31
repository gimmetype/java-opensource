package com.classscheduler.Classscheduler;

import com.classscheduler.Classscheduler.Graph;

import java.util.ArrayList;
public class MinimumVertexCoverSolver {
    public static boolean next_combination(int[] comb, int[] cap_element) {
        int i = 0;
        while (i < comb.length) {
            comb[i] += 1;
            if (comb[i] == cap_element[i]) {
                comb[i] = -1;
                i++;
            } else {
                return true;
            }
        }
        return false;
    }
    public static ArrayList<Integer[]> NaiveSolver(Graph g, ArrayList<Integer>[] group) {
        ArrayList<Integer[]> ans = new ArrayList<>();
        int[] cap_element = new int[group.length];
        int[] selected = new int[group.length];
        for (int i = 0; i < group.length; i++) {
            selected[i] = -1;
            cap_element[i] = group[i].size();
        }

        int min_cnt = 0;
        while (next_combination(selected, cap_element)) {
            int selected_cnt = 0;
            boolean chk = true;
            ArrayList<Integer> vertice_set = new ArrayList<Integer>();
            for (int i = 0; i < group.length; i++) {
                for (int j = 0; j < group[i].size(); j++) {
                    if (selected[i] == j) {
                        vertice_set.add(group[i].get(j));
                    } else selected_cnt += 1;
                }
            }
            if (selected_cnt > min_cnt) continue; // 최소 개수
            for (int i = 0; chk && i < vertice_set.size(); i++) {
                for (int j = i + 1; chk && j < vertice_set.size(); j++) {
                    if (g.has_edge(vertice_set.get(i), vertice_set.get(j)))
                        chk = false;
                }
            }
            if (chk) {
                if (selected_cnt < min_cnt) {
                    min_cnt = selected_cnt;
                    ans.clear();
                }
                Integer[] temp = new Integer[vertice_set.size()];
                for (int i = 0; i < vertice_set.size(); i++) {
                    temp[i] = vertice_set.get(i);
                }
                ans.add(temp);
            }
        }
        System.out.println("good");
        return ans;
    }
}
