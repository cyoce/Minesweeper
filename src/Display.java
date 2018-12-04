

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;



public class Display extends JComponent {
    
    private int width, height;
    private Game game; 
    private boolean loopmode;
    private Stack<JLabel> labels;
    
    
    final int CELL_TOP_X = 100;
    final int CELL_TOP_Y = 100;
    final int CELL_SIDE_PIXELS = 100;
    final int COLS = Game.COLS;
    final int ROWS = Game.ROWS;
    final Color GRID_COLOR = Color.DARK_GRAY;
    final Color EMPTY_COLOR = Color.LIGHT_GRAY;
    final Color UNMARKED_COLOR = Color.GRAY;
    final Color MINE_COLOR = Color.RED;
    final Color ADJ_COLOR = Color.GREEN;
    final Color FLAG_COLOR = Color.RED;
           
    public Display(int w, int h){
        width = w; 
        height = h;
        labels = new Stack<>();
        init();
        putButtons();
        loopmode = false;
    }
    
    private void init(){
        // new game
        game = new Game();
        avg = "--";
        while(!labels.empty()) 
            remove(labels.pop());
        System.out.println("reset");
    }
    
    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        drawCells(g2);
        drawGrid(g2);
        drawButtons(g2);
    }
    
    private void drawGrid(Graphics2D g2){
        int x1 = CELL_TOP_X;
        int x2 = x1 + COLS*CELL_SIDE_PIXELS;
        int y1, y2;
        
        g2.setColor(GRID_COLOR);
        
        for(int r = 0; r <= ROWS; r++){
            y1 = CELL_TOP_Y + r*CELL_SIDE_PIXELS;
            y2 = y1;
            g2.drawLine(x1, y1, x2, y2);
        }
        
        y1 = CELL_TOP_Y;
        y2 = y1 + ROWS * CELL_SIDE_PIXELS;
        
        for(int c = 0; c <= COLS; c++){
            x1 = CELL_TOP_X + c*CELL_SIDE_PIXELS;
            x2 = x1;
            g2.drawLine(x1, y1, x2, y2);
        }
    }
    
    private void drawCells(Graphics2D g2){
        for(int r = 0; r < ROWS; r++){
            int ytop = CELL_TOP_Y + r*CELL_SIDE_PIXELS;
            int ybot = ytop + CELL_SIDE_PIXELS;
            for(int c = 0; c < COLS; c++){
                int xleft = CELL_TOP_X + c * CELL_SIDE_PIXELS;
                
                int val = game.get(r,c);
                
                switch(val){
                    case 8:
                    case 7:
                    case 6:
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                    case 0:
                        g2.setColor(val == 0 ? EMPTY_COLOR : ADJ_COLOR);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("NOT A REAL FONT", Font.PLAIN, 48));
                        int size = CELL_SIDE_PIXELS/2 - 10;
                        g2.drawString(""+val, xleft + CELL_SIDE_PIXELS/2 - 12, ytop + CELL_SIDE_PIXELS/2 + 20);
                        break;
                    
                    case -2:
                        g2.setColor(UNMARKED_COLOR);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        break;
                    case -1:
                        g2.setColor(MINE_COLOR);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        ImageIcon ic = new ImageIcon("Mine50x50.png");
                        Image image = ic.getImage();
                        image = image.getScaledInstance(90, 90,  Image.SCALE_SMOOTH);
                        
                        JLabel jl = new JLabel(new ImageIcon(image));
                        jl.setBounds(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        add(jl);
                        labels.push(jl);
                        break;
                    case -3:
                        g2.setColor(MINE_COLOR);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        break;
                    default:
                        g2.setColor(EMPTY_COLOR);
                        g2.fillRect(xleft, ytop, CELL_SIDE_PIXELS, CELL_SIDE_PIXELS);
                        break;
                
                }
                
                
                
                
            }
        }
    }

    private final JLabel avglabel = new JLabel("average");
    private Button resetButton;
    private Button stepButton;
    private Button runButton;
    private Button avgButton;
    private String avg;
    private void putButtons(){
        resetButton = new Button("Reset", new Rectangle(100, 1100, 100, 80), () -> {
            init();
            repaint();
        });
        
        stepButton = new Button("Step", new Rectangle(100, 1200, 100, 80), () -> {
            System.out.println("step");
            loopmode = false;
            game.play();
            repaint();
        });
        
        runButton = new Button("Run", new Rectangle(260, 1200, 100, 80), () -> {
           System.out.println("run");
           while(!game.play()) repaint();
           repaint();
        });
        
        avgButton = new Button("Avg", new Rectangle(420, 1200, 200, 80), () -> {
            System.out.println("avg");
            final int NUM_AVG = 100;
            int successes = 0;
            int falseflags = 0;
            int repeats = 0;
            int mineclicks = 0;
            double sumTurns = 0; 
            for(int i = 0; i < NUM_AVG; i++){
                Game g = new Game();
                while(!g.play());
                System.out.println(g.numTurns);
                sumTurns += g.numTurns;
                switch(g.result){
                    case SUCCESS: successes++; break;
                    case FALSE_FLAG: falseflags++; break;
                    case REPEATED_MOVE: repeats++; break;
                    case MINE_CLICKED: mineclicks++; break;
                }
                System.out.println("Successes: " + successes);
                System.out.println("Bad flags: " + falseflags);
                System.out.println("Mines hit: " + mineclicks);
                System.out.println("Repeats:   " + repeats);
            }
            System.out.println(sumTurns / NUM_AVG);
            avg = Integer.toString((int) sumTurns/NUM_AVG);
            repaint();
        });
    }
    private void drawButtons(Graphics2D g2){
        
        add(runButton);
        add(stepButton);
        add(resetButton);
        add(avgButton);
        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("NOT A REAL FONT", Font.PLAIN, 24));
        
        g2.drawString("--", 620 + 50, 1240);
        g2.drawString("" + game.numTurns, 220, 1260);
    
        
    }
}

class Button extends JButton {
    public Button(String text, Rectangle r, ButtonListener listener){
        setText(text);
        setBounds(r);
        class OutputListener implements ActionListener {
            public void actionPerformed(ActionEvent e){
                listener.run();
            }
        }
        addActionListener(new OutputListener());
    }
    
    
}

@FunctionalInterface
interface ButtonListener {
     public abstract void run();
}