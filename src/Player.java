
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Player {
    static final int ROWS = Game.ROWS;
    static final int COLS = Game.COLS;
    private int nextR, nextC, nextA;
    private int turns;
    public Player(){
        turns = 0;
        int bombs = 3;
        int empty = 2;
        System.out.println("Permutations for " + bombs + " bombs, " + empty + " empty:");
        int[][] perms = permutations(bombs, empty);
        for(int i = 0; i < perms.length; i++){
            for(int j = 0; j < perms[i].length; j++){
                System.out.print(perms[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    
    
    
    private void solve(int[][] board, int depth){
        solve(dupe(board), new boolean[ROWS][COLS], depth);
    }
    
    private void solve(int[][] board, boolean[][] active, int depth){
        int minR = 0;
        int minC = 0;
        double minSquares = 10;
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                Tap t = tap(r,c, board);
                double temp = t.empty - (t.val - t.mines);
                if(temp < minSquares){
                    minR = r;
                    minC = c;
                    minSquares = temp;
                }
            }
        }
        Stack<SolveState> boards = new Stack<>();
        active[minR][minC] = true;
    }
    
    private int[][] permutations(int bombs, int empty){
        Stack<Stack<Integer>> perms = new Stack<>();
        ArrayList<Stack<Integer>> tout = new ArrayList<>();
        Stack<Integer> s = new Stack<>();
        s.push(1);
        perms.push(s); 
        s = new Stack<>();
        s.add((Integer) 0);
        perms.push(s);
        while(!perms.empty()){
            s = perms.pop();
            if(s.size() == empty + bombs){
                tout.add(s);
                continue;
            }
            
            int tbombs = 0, tempty = 0;
            for(int x:s){
                if(x == 1) tbombs++;
                else tempty++;
            }
            
            
            Stack<Integer> s1;
            if(tbombs < bombs){
                s1 = (Stack<Integer>) s.clone();
                s1.push(1);
                perms.push(s1);
            }
            if(tempty < empty){
                s1 = (Stack<Integer>) s.clone();
                s1.push(0);
                perms.push(s1);
            }
        }
        
        int[][] out = new int[tout.size()][empty+bombs];
        for(int i = 0; i < tout.size(); i++){
            Stack<Integer> si = tout.get(i);
            out[i] = si.stream().mapToInt(j->j).toArray();
        }
        return out;
    }
    
    public void planNextMove(int[][] board){
        turns++;
        if(turns == 1){
            nextR = ROWS/2;
            nextC = COLS/2;
            nextA = 1;
            return;
        }
        MoveContainer mc = autoMove(false, board);
        if(!mc.empty()){
            Move mv = mc.next();
            nextR = mv.row;
            nextC = mv.col;
            nextA = mv.action;
            return;
        }
        
        if(false){
            solve(board, 5);
            return;
        }
        
        Random r = new Random();
        do {
            nextR = r.nextInt(ROWS);
            nextC = r.nextInt(COLS);
        } while(board[nextR][nextC] != Game.UNMARKED && turns < ROWS*COLS);
        nextA = 1;
    }
    
    private MoveContainer autoMove(boolean all, int[][] board){
        MoveContainer out = new MoveContainer();
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                Tap t = tap(r, c, board);
                if(t.val < 0) continue;
                if(t.full) continue;
                if(t.mines == t.val){
                    for(int dr = -1; dr <= 1; dr++){
                        if(r+dr < 0 || r+dr >= ROWS) continue;
                        for(int dc = -1; dc <= 1; dc++){
                            if(dr == 0 && dc == 0) continue;
                            if(c+dc < 0 || c+dc >= ROWS) continue;
                            
                            if(board[r+dr][c+dc] == Game.UNMARKED){
                                out.add(r+dr, c+dc, 1);
                                if(!all) return out;
                            }
                        }
                    }
                } else if(t.val - t.mines == t.empty){
                    for(int dr = -1; dr <= 1; dr++){
                        if(r+dr < 0 || r+dr >= ROWS) continue;
                        for(int dc = -1; dc <= 1; dc++){
                            if(dr == 0 && dc == 0) continue;
                            if(c+dc < 0 || c+dc >= ROWS) continue;
                            
                            if(board[r+dr][c+dc] == Game.UNMARKED){
                                out.add(r+dr, c+dc, -1);
                                if(!all) return out;
                            }
                        }
                    }
                }
            }
        }
        
        return out;
    }
    
    private Tap tap(int r, int c, int[][] board){
        Tap t  = new Tap();
        t.val = board[r][c];
        for(int dr = -1; dr <= 1; dr++){
            if(r+dr < 0 || r+dr >= ROWS) continue;
            for(int dc = -1; dc <= 1; dc++){
                if(dr == 0 && dc == 0) continue;
                if(c+dc < 0 || c+dc >= COLS) continue;
                
                if(board[r+dr][c+dc] == Game.MINE || board[r+dr][c+dc] == Game.FLAG){
                    t.mines++;
                    t.full = false;
                }
                else if(board[r+dr][c+dc] == Game.UNMARKED){
                    t.empty++;
                    t.full = false;
                }
            }
        }
        return t;
    }

    private static class Tap {
        public int mines = 0;
        public int empty = 0;
        public int tiles = 0;
        public int val;
        public boolean full = true;
        public Tap(int m, int e, int t, int v) {
            mines = m;
            empty = e;
            tiles = t;
            val = v;
        }
        
        public Tap(){}
    }
    
    private int[][] dupe(int[][] board){
        int[][] out = new int[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                out[r][c] = board[r][c];
            }
        }
        return out;
    }
    
    private boolean[][] dupe(boolean[][] board){
        boolean[][] out = new boolean[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                out[r][c] = board[r][c];
            }
        }
        return out;
    }
    
    public int getMoveR(){
        return nextR;
    }
    
    public int getMoveC(){
        return nextC;
    }
    
    public int getMoveAction(){
        return nextA;
    }
}
