package com.classscheduler.Classscheduler;

public class Graph {
    public boolean[][] adj;
    public int size;
    public int edge_size;
    public int[] degree;
    Graph(int _size) {
        size = _size;
        edge_size = 0;
        adj = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                adj[i][j] = false;
            }
        }
        degree = new int[size];
        for (int i = 0; i < size; i++) {
            degree[i] = 0;
        }
    }
    public void add_edge(int u, int v) {
        adj[u][v] = true;
        adj[v][u] = true;
        degree[u] += 1;
        degree[v] += 1;
        edge_size += 1;
    }
    public boolean has_edge(int u, int v) {
        return adj[u][v];
    }
    public int get_degree(int v) {
        return degree[v];
    }
}
