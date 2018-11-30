
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the edito
 */

/**
 *
 * @author maste
 */
public class Player {
    static final int ROWS = Game.ROWS;
    static final int COLS = Game.COLS;
    static Random rand = new Random();
    private int nextR;
    private int nextC;
    private int nextAction;
    private int remainingMoves;
    private int[][] board;
    private double[][] probs;
    private double defprob;
    
    private int numTurns;
    
    public Player(){
        numTurns = 0;
        board = new int[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                board[r][c] = -2;
            }
        }
    }
    
    public void updateTile(int r, int c, int t){
        if(t == -2) t = 0;
        board[r][c] = t;
    }
    
    private double cleanUp(double v, int s){
        if(s <= 0) return v;
        return v;
    }
    
    private double combine(double a, double b){
        if(a == 0 || b == 0) return 0;
        if(a == 1 || b == 1) return 1;
        
        return a*b;
    }
    
    private void genProb(){
        
        remainingMoves = 0;
        probs = new double[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                probs[r][c] = -1;
            }
        }
        int total_bombs = Game.MINES;
        int total_free = ROWS*COLS;
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                if(revealed(r,c)){
                    total_free--;
                    int available = 0;
                    int adj_bombs = 0;
                    for(int dr = -1; dr <= 1; dr++){
                        if(r+dr >= ROWS || r+dr < 0) continue;    
                        for(int dc = -1; dc <= 1; dc++){
                            if(dr == 0 && dc == 0) continue;
                            if(c+dc >= COLS || c+dc < 0) continue;
                            
                            if(board[r+dr][c+dc] == Game.UNMARKED) available++;
                            else if(board[r+dr][c+dc] == Game.FLAG) adj_bombs++;
                            
                        }
                    }
                    double prob;
                    if(available == 0) prob = 0;
                    else if(board[r][c] == 0) prob = 0;
                    else prob = (double) (board[r][c] - adj_bombs) / (double) available;
                    if(prob < 0){
                        System.out.println("Negative prob @ (" + r + ", " + c + ")");
                    }
                    
                    
                    for(int dr = -1; dr <= 1; dr++){
                        if(r+dr >= ROWS || r+dr < 0) continue;
                        for(int dc = -1; dc <= 1; dc++){
                            if(dr == 0 && dc == 0) continue;
                            if(c+dc >= COLS || c+dc < 0) continue;
                            if(probs[r+dr][c+dc] == 0 || prob == 0) probs[r+dr][c+dc] = 0;
                            if(probs[r+dr][c+dc] < 0){
                                probs[r+dr][c+dc] = prob;
                                continue;
                            }
                            probs[r+dr][c+dc] = combine(probs[r+dr][c+dc], prob);
                            
                            
                        }
                    }
                } else if(board[r][c] == Game.MINE){
                    total_bombs--;
                }
                
                
            }
            
        }
//        for(int r = 0; r < ROWS; r++){
//            if(true) break; // TEMP
//            for(int c = 0; c < COLS; c++){
//                int squareSum = 0;
//                for(int dr = -1; dr <= 1; dr++){
//                    if(r+dr >= ROWS || r+dr < 0) continue;
//                    for(int dc = -1; dc <= 1; dc++){
//                        if(dr == 0 && dc == 0) continue;
//                        if(c+dc >= COLS || c+dc < 0) continue;
//                        if(revealed(r+dr, c+dc)){
//                            squareSum++;
//                        }
//                    }
//                }
//                if(probs[r][c] > 0) probs[r][c] = cleanUp(probs[r][c], squareSum);
//            }
//        }
//        defprob = (double) total_bombs / total_free;
    }
    
    private boolean revealed(int r, int c){
        int v = board[r][c];
        if(v >= 0) return true;
        if(v == Game.FLAG) return true;
        if(v == Game.MINE) v = 1/0;
        return false;
    }
    
    public void planNextMove(int[][] b){
        board = b;
        nextAction = 1;
        
        numTurns++;
        
        if(numTurns == 0){
            nextR = nextC = 0;
            return;
        }
        
        if(remainingMoves <= 0 || true){
            genProb();
        }
        printboard(probs);
        remainingMoves--;
        
        double maxP = -1; // or defprob
        int maxC = 7;
        int maxR = 7;
        
        double minP = -1; // or defprob
        int minC = 6;
        int minR = 6;
        
        System.out.println(defprob);
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                if(revealed(r,c)) continue;
                double p = probs[r][c];
                if(p < 0) continue;
                System.out.println("(" + r + ", " + c + ") :: " + p);
                if(p == 1){
                    nextR = r;
                    nextC = c;
                    nextAction = -1;
                    System.out.println("100% bomb");
                    return;
                }
                if(p == 0){
                    nextR = r;
                    nextC = c;
                    nextAction = 1;
                    System.out.println("100% safe");
                    return;
                }
                if(maxP == -1 || p >= maxP){
                    maxP = p;
                    maxR = r;
                    maxC = c;
                } 
                if (minP == -1 || p <= minP){
                    minP = p;
                    minR = r;
                    minC = c;
                }
            }
        }
        if(minP == 0){
            nextR = minR;
            nextC = minC;
            nextAction = 1;
        } else if(maxP == 1){
            nextR = maxR;
            nextC = maxC;
            nextAction = -1;
        } else if(/*Math.pow(maxP,2)-0.5*/ maxP < 1 - minP){
           nextR = minR;
           nextC = minC;
           nextAction = 1;
        } else {
            nextR = maxR;
            nextC = maxC;
            nextAction = -1;
        }
        
        
        
        System.out.println("...");
    }
    
    private void printboard(double[][] b){
        for(int r = 0; r < b.length; r++){
            for(int c = 0; c < b[r].length; c++){
                if(revealed(r,c)) System.out.print("  [" + (board[r][c] == Game.FLAG ? "F" : board[r][c]) + "]  ");
                else System.out.printf(" %5.2f ", b[r][c]);
            }
            System.out.println();
        }
    }
    
    
    public int getMoveR(){
        if(numTurns == 1) return 0;
        return nextR;
    }
    
    public int getMoveC(){
        if(numTurns == 1) return 0;
        return nextC;
    }
    
    /**
     * -1: mine
     * +1: safe
     * @return 
     */
    
    public int getMoveAction(){
        return nextAction;
    }
}
