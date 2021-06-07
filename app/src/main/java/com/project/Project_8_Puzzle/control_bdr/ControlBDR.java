package com.project.Project_8_Puzzle.control_bdr;

import com.project.Project_8_Puzzle.control_bfs.bfs;

import java.util.Queue;
import java.util.Stack;

public class ControlBDR {
    public static String upBDR(String s, int p,Queue<bfs> open, Stack<String> close) {
        String str = s;
        if (!(p < 3)) {
            char a = str.charAt(p - 3);
            String newS = str.substring(0, p) + a + str.substring(p + 1);
            str = newS.substring(0, (p - 3)) + '9' + newS.substring(p - 2);
        }
        // Eliminates child of X if its on OPEN or CLOSED
        if (!open.contains(str)  && !close.contains(str))
            return str;
        else
            return "-1";
    }

    public static String downBDR(String s, int p, Queue<bfs> open, Stack<String> close) {
        String str = s;
        if (!(p > 5)) {
            char a = str.charAt(p + 3);
            String newS = str.substring(0, p) + a + str.substring(p + 1);
            str = newS.substring(0, (p + 3)) + '9' + newS.substring(p + 4);
        }

        // Eliminates child of X if its on OPEN or CLOSED
        if (!open.contains(str) && !close.contains(str))
            return str;
        else
            return "-1";
    }
    public static String leftBDR(String s, int p,Queue<bfs> open, Stack<String> close) {
        String str = s;
        if (p%3!=0) {
            char a = str.charAt(p - 1);
            String newS = str.substring(0, p) + a + str.substring(p + 1);
            str = newS.substring(0, (p - 1)) + '9' + newS.substring(p);
        }
        // Eliminates child of X if its on OPEN or CLOSED
        if (!open.contains(str) && !close.contains(str))
            return str;
        else
            return "-1";
    }
    public static String rightBDR(String s, int p,Queue<bfs> open, Stack<String> close) {
        String str = s;
        if (p != 2 && p != 5 && p != 8) {
            char a = str.charAt(p + 1);
            String newS = str.substring(0, p) + a + str.substring(p + 1);
            str = newS.substring(0, (p + 1)) + '9' + newS.substring(p + 2);
        }
        // Eliminates child of X if its on OPEN or CLOSED
        if (!open.contains(str) && !close.contains(str))
            return str;
        else
            return "-1";
    }
}
