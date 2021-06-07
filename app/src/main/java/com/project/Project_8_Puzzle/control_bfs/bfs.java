package com.project.Project_8_Puzzle.control_bfs;

import java.util.Stack;

public class bfs implements Cloneable{

    public bfs(String now, bfs parent) {
        this.now = now;
        this.parent = parent;
    }
    public String now;
    public bfs parent;
}
