package com.project.Project_8_Puzzle;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.project.Project_8_Puzzle.control_astar.ControlAStar;
import com.project.Project_8_Puzzle.control_astar.astar;
import com.project.Project_8_Puzzle.control_bfs.bfs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import static com.project.Project_8_Puzzle.control_astar.ControlAStar.downAstar;
import static com.project.Project_8_Puzzle.control_astar.ControlAStar.leftAstar;
import static com.project.Project_8_Puzzle.control_astar.ControlAStar.rightAstar;
import static com.project.Project_8_Puzzle.control_astar.ControlAStar.upAstar;
import static com.project.Project_8_Puzzle.control_bdr.ControlBDR.downBDR;
import static com.project.Project_8_Puzzle.control_bdr.ControlBDR.leftBDR;
import static com.project.Project_8_Puzzle.control_bdr.ControlBDR.rightBDR;
import static com.project.Project_8_Puzzle.control_bdr.ControlBDR.upBDR;
import static com.project.Project_8_Puzzle.control_bfs.ControlBFS.down;
import static com.project.Project_8_Puzzle.control_bfs.ControlBFS.left;
import static com.project.Project_8_Puzzle.control_bfs.ControlBFS.right;
import static com.project.Project_8_Puzzle.control_bfs.ControlBFS.up;
import static com.project.Project_8_Puzzle.util.Util.findCost;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity {

    String TAG="myApp";
    TextView langkah, langkahke;
    Button btnNext,btnPrev ;

    ArrayList<ButtonPuzzle> listPuzzle;
    int stateNow=9;
    int jalan=0;
    int step=0;

    //winstate
    String WinState="123456789";
    ArrayList<String> stateJawaban=new ArrayList<>();
    int stateJawabanNow=-1;

    // BFS
    Queue<bfs> queueBFSOpen = new LinkedList<>();
    Stack<String> stackBFSClose = new Stack<>();
    ArrayList<bfs> lBfs = new ArrayList<>();
    //

    // Bidirectional
    Queue<bfs> queueBDR1Open = new LinkedList<>();
    Stack<String> stackBDR1Close= new Stack<>();
    Stack<bfs> stackBDR1Close2= new Stack<>();
    Queue<bfs> queueBDR2Open = new LinkedList<>();
    Stack<String> stackBDR2Close= new Stack<>();
    Stack<bfs> stackBDR2Close2= new Stack<>();

    PriorityQueue<astar> pkAsterOpen = new PriorityQueue<astar>(99999,new CompareAstar());
    Stack<String> stackAstarClose= new Stack<>();

    Button restart;
    TextView proses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrev=findViewById(R.id.btnPref);
        btnNext=findViewById(R.id.btnNext);
        proses=findViewById(R.id.idProses);
        langkah=findViewById(R.id.tvtLangkah);
        langkahke=findViewById(R.id.tvlangkahke);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stateJawabanNow>0){
                    stateJawabanNow--;
                    step--;
                    langkahke.setText("Langkah ke: "+step);
                    gantiTextButton(stateJawaban.get(stateJawabanNow));
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stateJawabanNow < stateJawaban.size()-1){
                    stateJawabanNow++;
                    step++;
                    langkahke.setText("Langkah ke: "+step);
                    gantiTextButton(stateJawaban.get(stateJawabanNow));
                }
            }
        });

        init();
        random();
    }

    public void gantiTextButton(String text){
        for (int i = 0; i < listPuzzle.size(); i++) {
            String potong =text.substring(i,i+1);
            listPuzzle.get(i).getBtn().setText(potong);
            setImage(listPuzzle.get(i).getBtn(), Integer.parseInt(potong));
            if(potong.equals("9")){
                listPuzzle.get(i).getBtn().setText("");
            }
        }
    }
    void setImage(Button btn, int num){
        if(num == 1){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.on));
        }
        else if(num == 2){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.tw));
        }
        else if(num == 3){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.thr));
        }
        else if(num == 4){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.fo));
        }
        else if(num == 5){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.fi));
        }
        else if(num == 6){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.si));
        }
        else if(num == 7){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.se));
        }
        else if(num == 8){
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.ei));
        }
        else{
            btn.setBackground(ContextCompat.getDrawable(this, R.drawable.ni));
        }
    }

    public void init(){
        listPuzzle= new ArrayList<>();
        restart = findViewById(R.id.btnRestart);
        addButtons(listPuzzle); //add button ke array button

        for (int i = 0; i< listPuzzle.size();i++) {
            Button btnClick = listPuzzle.get(i).getBtn();
            btnClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button btn=(Button)view;
                    int lokasi = (int)btn.getTag();
                    if(stateNow-3==lokasi){//swap atas
                        swap(btn,listPuzzle.get(stateNow).getBtn());
                    }
                    else if(stateNow+3==lokasi){//swap bawah
                        swap(btn,listPuzzle.get(stateNow).getBtn());
                    }
                    else if(stateNow+1==lokasi&&(stateNow+1)%3!=0){//swap kanan
                        swap(btn,listPuzzle.get(stateNow).getBtn());
                    }
                    else if(stateNow-1==lokasi&&(stateNow)%3!=0){//swap kiri
                        swap(btn,listPuzzle.get(stateNow).getBtn());
                    }

                }
            });
        }
    }

    public void restart_onclick(View view) {
        listPuzzle.clear();
        init();
        random();

        for (int j = 0; j < listPuzzle.size(); j++) {
            Button b = listPuzzle.get(j).getBtn();
            b.setEnabled(true);
        }

        //restart.setVisibility(View.INVISIBLE);
    }
    boolean win=false;

    public void bdr_onclick(View view){
        proses.setText("Proses : Proses Bi-Bfs");
        queueBDR1Open.clear();
        stackBDR1Close.clear();

        queueBDR2Open.clear();
        stackBDR2Close.clear();

        queueBDR1Open.add(new bfs(getState(),null));
        queueBDR2Open.add(new bfs(WinState,null));
        stateJawaban.clear();
        bfs x1=null;
        bfs x2=null;
        jalan=0;
        step=0;
        win=false;
        while(!queueBDR1Open.isEmpty()&&!queueBDR2Open.isEmpty()&&!win){
            x1=queueBDR1Open.peek();
            x2=queueBDR2Open.peek();

            stackBDR1Close.add(x1.now);
            stackBDR1Close2.add(x1);
            stackBDR2Close.add(x2.now);
            stackBDR2Close2.add(x2);

            bfs open1 = queueBDR1Open.peek();
            bfs open2 = queueBDR2Open.peek();

            queueBDR1Open.remove(open1);
            queueBDR2Open.remove(open2);
            int pos1 = x1.now.indexOf("9");
            int pos2 = x2.now.indexOf("9");

            stateNow=8;
            jalan++;

            if(stackBDR1Close.contains(open2.now)||stackBDR2Close.contains(open1.now)){
                proses.setText("Proses : Ketemu Bi-Bfs");

                stateJawabanNow=0;
                bfs lastBfs1=null;
                bfs lastBfs2=null;
                System.out.println("menang");
                if(stackBDR2Close.contains(open1.now)){
                    lastBfs1=open1;
                    lastBfs2=stackBDR2Close2.elementAt(stackBDR2Close.search(open1.now)-1);
                }else{
                    lastBfs1=stackBDR1Close2.elementAt(stackBDR1Close.search(open2.now)-1);
                    lastBfs2=open2;
                }


                ArrayList<bfs> jawaban = new ArrayList<>();
                while(lastBfs1.parent!=null){
                    jawaban.add(lastBfs1);
                    lastBfs1=lastBfs1.parent;
                }
                stateJawaban.add(lastBfs1.now);
                for (int i = jawaban.size()-1; i >=0 ; i--) {
                    stateJawaban.add(jawaban.get(i).now);
                }

                while(lastBfs2.parent!=null){
                    stateJawaban.add(lastBfs2.now);
                    lastBfs2=lastBfs2.parent;
                }
                stateJawaban.add(lastBfs2.now);


                langkah.setText("Banyak langkah: "+jalan);
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "bfs_onclick: Berhasil ");
                win=true;
            }else{
                String temp="";
                bfs parentAdd= (bfs) clone(open1);
                temp = downBDR(x1.now, pos1,queueBDR1Open,stackBDR1Close);
                if (!(temp.equals("-1"))){
                    queueBDR1Open.add(new bfs(temp,parentAdd));
                }
                temp = upBDR(x1.now, pos1,queueBDR1Open,stackBDR1Close);
                if (!(temp.equals("-1"))){
                    queueBDR1Open.add(new bfs(temp,parentAdd));
                }

                temp = leftBDR(x1.now, pos1,queueBDR1Open,stackBDR1Close);
                if (!(temp.equals("-1"))){
                    queueBDR1Open.add(new bfs(temp,parentAdd));
                }

                temp = rightBDR(x1.now, pos1,queueBDR1Open,stackBDR1Close);
                if (!(temp.equals("-1"))){
                    queueBDR1Open.add(new bfs(temp,parentAdd));
                }

                parentAdd= (bfs) clone(open2);
                temp = rightBDR(x2.now, pos2,queueBDR2Open,stackBDR2Close);
                if (!(temp.equals("-1"))){
                    queueBDR2Open.add(new bfs(temp,parentAdd));
                }
                temp = downBDR(x2.now, pos2,queueBDR2Open,stackBDR2Close);
                if (!(temp.equals("-1"))){
                    queueBDR2Open.add(new bfs(temp,parentAdd));
                }
                temp = upBDR(x2.now, pos2,queueBDR2Open,stackBDR2Close);
                if (!(temp.equals("-1"))){
                    queueBDR2Open.add(new bfs(temp,parentAdd));
                }

                temp = leftBDR(x2.now, pos2,queueBDR2Open,stackBDR2Close);
                if (!(temp.equals("-1"))){
                    queueBDR2Open.add(new bfs(temp,parentAdd));
                }
            }


        }
    }

    public void astar_onclick(View view) {
        proses.setText("Proses : Proses Astar");
        pkAsterOpen.clear();;
        stackAstarClose.clear();

        pkAsterOpen.add(new astar(getState(),null,9999,0));
        stateJawaban.clear();
        win=false;
        jalan=0;
        step=0;
        String x="";
        while(!pkAsterOpen.isEmpty()&&!win){
            x=pkAsterOpen.peek().now;
            stackAstarClose.add(x);
            astar parent = pkAsterOpen.peek();
            pkAsterOpen.remove(parent);
            int pos = x.indexOf("9");

            jalan++;
            if(x.equals(WinState)){
                proses.setText("Proses : Ketemu Astar");

                stateJawabanNow=0;
                astar lastAster = parent;
                ArrayList<astar> jawaban = new ArrayList<>();
                while(lastAster.parent!=null){
                    jawaban.add(lastAster);
                    lastAster=lastAster.parent;
                }


                stateJawaban.add(lastAster.now);
                for (int i = jawaban.size()-1; i >=0 ; i--) {
                    stateJawaban.add(jawaban.get(i).now);
                }
                stateNow=8;

                langkah.setText("Banyak langkah: "+jalan);
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "bfs_onclick: Berhasil ");
                win=true;
            }else{
                String temp="";
                astar parentAdd= (astar) clone(parent);
                temp = downAstar(pkAsterOpen, stackAstarClose, x, pos);
                if (!(temp.equals("-1"))){
                    pkAsterOpen.add(new astar(temp,parentAdd,findCost(WinState, temp),parentAdd.getDepth()));
                }
                temp = upAstar(pkAsterOpen, stackAstarClose, x, pos);
                if (!(temp.equals("-1"))){
                    pkAsterOpen.add(new astar(temp,parentAdd,findCost(WinState, temp),parentAdd.getDepth()));
                }

                temp = leftAstar(pkAsterOpen, stackAstarClose, x, pos);
                if (!(temp.equals("-1"))){
                    pkAsterOpen.add(new astar(temp,parentAdd,findCost(WinState, temp),parentAdd.getDepth()));
                }

                temp = rightAstar(pkAsterOpen, stackAstarClose, x, pos);
                if (!(temp.equals("-1"))){
                    pkAsterOpen.add(new astar(temp,parentAdd,findCost(WinState, temp),parentAdd.getDepth()));
                }
            }


        }
    }



    public void bfs_onclick(View view) {
        proses.setText("Proses : Bfs Bdr");
        queueBFSOpen.clear();
        stackBFSClose.clear();
        lBfs.clear();
        queueBFSOpen.add(new bfs(getState(),null));
        lBfs.add(new bfs(getState(),null));
        stateJawaban.clear();
        win=false;
        String x="";
        jalan=0;
        step=0;
        int ind=0;
        while(!queueBFSOpen.isEmpty()&&!win){
            x=queueBFSOpen.peek().now;

            stackBFSClose.add(x);
            bfs open = queueBFSOpen.peek();
            bfs parents = open;
            ind++;
            if(ind<10){
                System.out.println(x);
                System.out.println(parents);
            }
            queueBFSOpen.remove(open);
            int pos = x.indexOf("9");

            jalan++;

            if(x.equals(WinState)){

                proses.setText("Proses : Ketemu Bdr");
                stateJawabanNow=0;
                bfs lastBfs=open;
                ArrayList<bfs> jawaban = new ArrayList<>();
                while(lastBfs.parent!=null){
                    jawaban.add(lastBfs);
                    lastBfs=lastBfs.parent;
                }


                stateJawaban.add(lastBfs.now);
                for (int i = jawaban.size()-1; i >=0 ; i--) {
                    System.out.println("size "+jawaban.get(i).now);
                    stateJawaban.add(jawaban.get(i).now);
                }
                stateNow=8;

                Log.i(TAG, "bfs_onclick: "+stateJawaban);
                Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "bfs_onclick: Berhasil ");
                win=true;
            }else{
                String temp="";
                temp = down(queueBFSOpen, stackBFSClose, x, pos);
                bfs parentAdd= (bfs) clone(parents);
                if (!(temp.equals("-1"))){
                    queueBFSOpen.add(new bfs(temp,parentAdd));
                    lBfs.add(new bfs(temp,parentAdd));
                }
                temp = up(queueBFSOpen, stackBFSClose, x, pos);
                if (!(temp.equals("-1"))){
                    queueBFSOpen.add(new bfs(temp,parentAdd));
                    lBfs.add(new bfs(temp,parentAdd));
                }

                temp = left(queueBFSOpen, stackBFSClose, x, pos);
                if (!(temp.equals("-1"))){
                    queueBFSOpen.add(new bfs(temp,parentAdd));
                    lBfs.add(new bfs(temp,parentAdd));
                }

                temp = right(queueBFSOpen, stackBFSClose, x, pos);
                if (!(temp.equals("-1"))){
                    queueBFSOpen.add(new bfs(temp,parentAdd));
                    lBfs.add(new bfs(temp,parentAdd));
                }
            }


        }
    }

    private Object clone(Object parents) {
        return parents;
    }

    public String getState(){
        StringBuilder stateNow= new StringBuilder();
        for (ButtonPuzzle btn:listPuzzle) {
            stateNow.append(btn.getBtn().getText().toString());
            if(btn.getBtn().getText().toString().equals("")){
                stateNow.append(9);
            }
        }
        return stateNow.toString();
    }

    public void cheat_onclick(View view) {
        //-1 karna tidak cetak angka 9
        for (int i = 0; i < listPuzzle.size()-1; i++) {
            Button btn = listPuzzle.get(i).getBtn();
            btn.setText((i+1)+"");
            listPuzzle.get(i).setLokasi(i);
            int lokasi = listPuzzle.get(i).getLokasi();
            btn.setTag(lokasi);
        }

        Button btn = listPuzzle.get(8).getBtn();
        btn.setText("");
        swap(listPuzzle.get(4).getBtn(),listPuzzle.get(7).getBtn());
        swap(listPuzzle.get(4).getBtn(),listPuzzle.get(8).getBtn());
        stateNow = 4;
    }

    public void swap(Button b1,Button b2){
        String tmp=b2.getText().toString();
        Drawable drawable = b2.getBackground();
        b2.setText(b1.getText());
        b2.setBackground(b1.getBackground());
        b1.setText(tmp);
        b1.setBackground(drawable);
        stateNow=(int)b1.getTag();
    }

    public void random(){
        for (int i = 0; i < listPuzzle.size()-1; i++) {
            Button btn = listPuzzle.get(i).getBtn();
            btn.setText((i+1)+"");
            listPuzzle.get(i).setLokasi(i);
            int lokasi = listPuzzle.get(i).getLokasi();
            btn.setTag(lokasi);
        }

        Button btn = listPuzzle.get(8).getBtn();
        btn.setText("");
        swap(listPuzzle.get(4).getBtn(),listPuzzle.get(7).getBtn());
        swap(listPuzzle.get(4).getBtn(),listPuzzle.get(8).getBtn());
        stateNow = 4;
    }
    
    public void addButtons(ArrayList<ButtonPuzzle> list){
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b1),0,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b2),1,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b3),2,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b4),3,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b5),4,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b6),5,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b7),6,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b8),7,0));
        list.add(new ButtonPuzzle((Button)findViewById(R.id.b9),8,0));
    }

    class CompareAstar implements Comparator<astar> {
        @Override
        public int compare(astar a,astar b) {
            int asli=a.cost+a.depth;
            int baru =b.cost+b.depth;
            if (asli < baru)
                return -1;
            else if (asli > baru)
                return 1;
            return 0;
        }

    }
}