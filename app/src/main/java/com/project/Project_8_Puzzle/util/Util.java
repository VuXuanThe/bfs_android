package com.project.Project_8_Puzzle.util;

public class Util {
    public static int findCost(String WinState, String now){
        int cost=0;
        for (int i = 0; i < 9; i++) {
            int a1=Integer.parseInt(now.substring(i,i+1));
            int a2=Integer.parseInt(WinState.substring(i,i+1));
            System.out.println();
            if(a1!=a2){
                cost++;
            }
        }
        return cost;
    }
}
