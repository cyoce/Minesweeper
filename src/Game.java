
import java.util.Arrays;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author maste
 */
public class Game {
    static final int ROWS = 10, COLS = 10;
    
    static final int MINES = 20;
    
    enum GameResult { SUCCESS, MINE_CLICKED, FALSE_FLAG, REPEATED_MOVE }
    public GameResult result;
    private int[][] internal;
    // [0, 8] denotes number of neighbors
    // -1 is a mine
    // -2 for unmarked square
    static final int MINE = -1;
    static final int UNMARKED = -2;
    static final int FLAG = -3;
    static final int MISS = -4;
    
    private int[][] gameBoard;
    
    private Player p;
    
    private boolean[][] moved;
    
    public int numTurns;
    
    public Game(){
        result = GameResult.SUCCESS;
        internal = new int[ROWS][COLS];
        gameBoard = new int[ROWS][COLS];
        
        moved = new boolean[ROWS][COLS];
        
        p = new Player();
        numTurns = 0;
        
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                gameBoard[r][c] = UNMARKED;
            }
        }
    }
    
    public int get(int r, int c){
        return gameBoard[r][c];
//        return internal[r][c];
    }
    
    public boolean done(){
        return numTurns == ROWS*COLS;
    }
    
    public boolean play(){
        boolean steppedOnMine = false;
        boolean markedWrongMine = false;
        numTurns++;
        int[][] b = new int[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                b[r][c] = gameBoard[r][c];
            }
        }
        p.planNextMove(gameBoard);
        int r = p.getMoveR();
        int c = p.getMoveC();
        int action = p.getMoveAction();
        System.out.println("Move guessed: (" + r + ", " +  c + ") :: " + action);

        if(numTurns == 1){
            populateBoard(r, c);
        }
        
        if(action == 1 && internal[r][c] == MINE)
            steppedOnMine = true;
        if(action == -1 && internal[r][c] != MINE)
            markedWrongMine = true;
        if(steppedOnMine){
            gameBoard[r][c] = MINE;
            System.out.println("Stepped on mine");
            result = GameResult.MINE_CLICKED;
        }
        else if(markedWrongMine){
            gameBoard[r][c] = MISS;
            System.out.println("Marked empty square as mine");
            result = GameResult.FALSE_FLAG;
        }
        else {
            if(moved[r][c]){
                System.out.println("repeated move");
                result = GameResult.REPEATED_MOVE;
                return true;
            }
            gameBoard[r][c] = internal[r][c];
            if(action == -1) gameBoard[r][c] = FLAG;
        }
        return steppedOnMine || markedWrongMine || done();
    }
    private void printBoard(int[][] a){
        for(int r = 0; r < a.length; r++){
            for(int c = 0; c < a[r].length; c++){
                System.out.printf("%3d", a[r][c]);
            }
            System.out.println();
        }
    }
    
    private void populateBoard(int row, int col){
        int idx = row*COLS + col;
        Integer[] a = new Integer[ROWS*COLS];
        for(int i = 0; i < ROWS*COLS; i++){
            a[i] = i;
        }
        Collections.shuffle(Arrays.asList(a));
        
        int mines = MINES;
        
        for(int i = 0; i < mines-1; i++){
            int n = a[i];
            
            
            int r = n / COLS;
            int c = n % COLS;
            
            if((n == idx && false) || (Math.abs(r - row) < 2 && Math.abs(c - col) < 2)) {
                mines++;
                continue;
            }
            
            internal[r][c] = MINE;
            
            for(int dr = -1; dr <= 1; dr++){
                if(r+dr >= ROWS || r+dr < 0) continue;
                for(int dc = -1; dc <= 1; dc++){
                    if(c+dc >= COLS || c+dc < 0) continue;
                    if(internal[r+dr][c+dc] == MINE) continue;
                    internal[r+dr][c+dc]++;
                }   
            }
            
        }
        
        System.out.println("pop");
        printBoard(internal);
    }
}
