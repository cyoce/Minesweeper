
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author maste
 */
class Probability {
    private ArrayList<Double> vals;
    
    public Probability(){
        System.out.println("created");
        vals = new ArrayList<>();
        
    }
    
    public double val(){
        double sum = 0;
        double count = 0;
        double product = 1;
        for(double v : vals){
            if(v == 1 || v == 0) return v;
            product *= v;
        }
        
        return product;
    }
    
    public void add(double val){
        vals.add(val);
    }
}
