
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author maste
 */
class MoveContainer {
    static final int ROWS = Game.ROWS;
    static final int COLS = Game.COLS;
    private Stack<Move> moves;
    private boolean valid;
    private int[][] actions;
    public MoveContainer(){
        moves = new Stack<>();
        actions = new int[ROWS][COLS];
        valid = true;
    }
    
    public void add(Move mv){
        if(actions[mv.row][mv.col] == 0){
            actions[mv.row][mv.col] = mv.action;
            moves.push(mv);
        } else if(actions[mv.row][mv.col] != mv.action){
            valid = false;
        } 
    }
    
    public void add(int r, int c, int a){
        add(new Move(r, c, a));
    }
    
    public Move next(){
        return moves.pop();
    }
    
    public boolean empty(){
        return moves.isEmpty();
    }
    
    public boolean valid(){
        return valid;
    }
    
}
