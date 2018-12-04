
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor
 */

/**
 *
 * @author maste
 */
public class OldPlayer {
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
    private boolean[][] moved;
    private Queue<Integer> moveRs;
    private Queue<Integer> moveCs;
    private Queue<Integer> moveActions;
    private int numTurns;
    
    
    private static final double STRANDED = -5;
    
    public OldPlayer(){
        numTurns = 0;
        board = new int[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                board[r][c] = -2;
            }
        }
        moved = new boolean[ROWS][COLS];
        
        moveRs = new LinkedList<>();
        moveCs = new LinkedList<>();
        moveActions = new LinkedList<>();
    }
    
    public void updateTile(int r, int c, int t){
        if(t == -2) t = 0;
        board[r][c] = t;
    }
    
    
    
    private void genProb(){
        
        remainingMoves = 0;
        probs = new double[ROWS][COLS];
        double[][] weights = new double[ROWS][COLS];
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                probs[r][c] = 0.5;
            }
        }
        int total_bombs = Game.MINES;
        int total_free = ROWS*COLS;
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                if(board[r][c] >= 0 || board[r][c] == Game.MINE || board[r][c] == Game.FLAG){
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
//                        System.out.println("Negative prob @ (" + r + ", " + c + ")");
                        System.out.println(board[r][c]);
                        System.out.println(adj_bombs);
                    }
                    double weight = 0;
                    if(prob != 0 && prob != 1) weight = Math.abs(0.5-prob);
                    for(int dr = -1; dr <= 1; dr++){
                        if(r+dr >= ROWS || r+dr < 0) continue;
                        for(int dc = -1; dc <= 1; dc++){
                            if(dr == 0 && dc == 0) continue;
                            if(c+dc >= COLS || c+dc < 0) continue;
                            if(probs[r+dr][c+dc] == 0 || prob == 0) probs[r+dr][c+dc] = 0;
                            if(probs[r+dr][c+dc] == STRANDED || probs[r+dr][c+dc] == Game.FLAG){
                                probs[r+dr][c+dc] = prob;
                                continue;
                            }
                            if(prob == 0 || probs[r+dr][c+dc] == 0) probs[r+dr][c+dc] = 0;
                            else if (prob == 1 || probs[r+dr][c+dc] == 1) probs[r+dr][c+dc] = 1;
                            else {
//                                probs[r+dr][c+dc] *= prob;
                                double pr = probs[r+dr][c+dc];
                                if(pr < 0.5 && prob < 0.5){
                                    probs[r+dr][c+dc] = prob * pr;
                                } else if(pr > 0.5 && prob > 0.5){
                                    probs[r+dr][c+dc] = 1 - ((1 - prob) * (1 - pr));
                                } else {
                                    if(weights[r+dr][c+dc] == 0) weights[r+dr][c+dc] += Math.abs(0.5-pr);
                                    probs[r+dr][c+dc] += prob * weight;
                                    weights[r+dr][c+dc] += weight;
                                }
                                
//                                if(prob > 0.5) weights[r+dr][c+dc] += 0.2;
//                                if(prob < 0.5) weights[r+dr][c+dc] -= 0.2;
                            }

                            
                            
                        }
                    }
                } else if(board[r][c] == Game.FLAG){
                    total_bombs--;
                }
                
                
            }
            
        }
        for(int r = 0; r < ROWS && false; r++){
            for(int c = 0; c < COLS; c++){
                if(weights[r][c] > 0) probs[r][c] /= weights[r][c];
            }
        }
    }
    
    private boolean revealed(int r, int c){
        int v = board[r][c];
        if(v >= 0) return true;
        if(v == Game.FLAG) return true;
        if(v == Game.MINE) v = 1/0;
        return false;
    }
    
    private double minP;
    private int minR;
    private int minC;
    private int recurseLevel;
    
    public void planNextMove(int[][] b){
        
        numTurns++;
        
        
        if(numTurns == 1){
            nextR = nextC = 0;
            return;
        }
        if(!moveActions.isEmpty()){
            nextR = moveRs.remove();
            nextC = moveCs.remove();
            nextAction = moveActions.remove();
            return;
        }
        
        
        if(b != board){
            recurseLevel = 0;
            minP = -1;
            minR = 6;
            minC = 6;
            for(int i = 0; i < b.length; i++){
                for(int j = 0; j < b[i].length; j++){
                    if(b[i][j] == Game.MINE) board[i][j] = Game.FLAG;
                    else board[i][j] = b[i][j];
                }
            }
            
            genAutoMoves();
            if(!moveActions.isEmpty()){
                nextR = moveRs.remove();
                nextC = moveCs.remove();
                nextAction = moveActions.remove();
                return;
            }

        }
        
        nextAction = 1;
        
        
        
        
        
        
        
        genProb();
    
        printboard(probs);
        
        double maxP = -1; // or defprob
        int maxC = 7;
        int maxR = 7;
//        
//        double minP = -1; // or defprob
//        int minC = 6;
//        int minR = 6;
        
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                if(revealed(r,c)) continue;
                if(moved[r][c]) continue;
                double p = probs[r][c];
                if(p == -1) continue;
//                System.out.println("(" + r + ", " + c + ") :: " + p);
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
        } else if(maxP < 1 - minP){
           nextR = minR;
           nextC = minC;
           nextAction = 1;
        } else if(recurseLevel < 100){
            board[maxR][maxC] = Game.FLAG;
            recurseLevel++;
            planNextMove(board);
        } else {
            nextR = maxR;
            nextC = maxC;
            nextAction = -1;
        }
        moved[nextR][nextC] = true;
    }
    
    public void genAutoMoves(){
        for(int r = 0; r < ROWS; r++){
            for(int c = 0; c < COLS; c++){
                if(board[r][c] >= 0){
                    int adj_mines = 0;
                    int available = 0;
                    for(int dr = -1; dr <= 1; dr++){
                        if(r+dr >= ROWS || r+dr < 0) continue;
                        for(int dc = -1; dc <= 1; dc++){
                            if(dr == 0 && dc == 0) continue;
                            if(c+dc >= COLS || c+dc < 0) continue;
                            
                            if(board[r+dr][c+dc] == Game.UNMARKED) available++;
                            if(board[r+dr][c+dc] == Game.FLAG) adj_mines++;
                            
                        }
                    }
                    
                    if(board[r][c] == 0 || board[r][c] == adj_mines){
                        System.out.println("[safe reveal]");
                        for(int dr = -1; dr <= 1; dr++){
                            if(r+dr >= ROWS || r+dr < 0) continue;
                            for(int dc = -1; dc <= 1; dc++){
                                if(dr == 0 && dc == 0) continue;
                                if(c+dc >= COLS || c+dc < 0) continue;
                                if(board[r+dr][c+dc] == Game.UNMARKED){
                                    moveRs.add(r+dr);
                                    moveCs.add(c+dc);
                                    moveActions.add(1);
                                }
                            }
                        }
                        
                        return;
                    }
                    
                    if(available == board[r][c] - adj_mines){
                        System.out.println("[safe mark]");
                        for(int dr = -1; dr <= 1; dr++){
                            if(r+dr >= ROWS || r+dr < 0) continue;
                            for(int dc = -1; dc <= 1; dc++){
                                if(dr == 0 && dc == 0) continue;
                                if(c+dc >= COLS || c+dc < 0) continue;
                                if(board[r+dr][c+dc] == Game.UNMARKED){
                                    moveRs.add(r+dr);
                                    moveCs.add(c+dc);
                                    moveActions.add(-1);
                                }
                            }
                        }
                        
                        return;
                    }
                    
                   
                }
            }
        }
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
