/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author maste
 */
public class SolveState {
    public int depth;
    public int[][] board;
    public boolean[][] active;
    public int[][] freeq;
    public int[][] bombq;
    public SolveState(int d, boolean[][] a, int[][] fq, int[][] bq, int[][] b){
        depth = d;
        active = a;
        board = b;
        freeq = fq;
        bombq = bq;
    }
}
