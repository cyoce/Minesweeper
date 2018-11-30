

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args){
        final int WIDTH = 1250; 
        final int HEIGHT = 1360;
        
        JFrame frame = new JFrame();
        
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("don't be a loser, buy a defuser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Display display = new Display(WIDTH, HEIGHT);
        frame.add(display);
        
        frame.setVisible(true);
    }
}
