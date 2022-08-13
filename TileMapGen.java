import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/*
Code updated by: Krezmick (Github: https://github.com/Krezmick/Random-2D-Tile-Map-Generation)
Original problem made by: M. Harwood 
Source code for problem: http://www.quarkphysics.ca/ICS4U1/javaprogs/unit3/MapContinent.java
 */
public class TileMapGen {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TileMapGen();
            }
        });
    }//end main

//constants
final static int SCRSIZE = 720;
final static int SIZE = 48;
final static int NUM_LAND = (SIZE * SIZE/2);
final static int LAND = 1;
final static int MOUNTAIN = 4;
final static int EMPTY = 0;
final static int LAKE = 33;
final static int OCEAN = 89;
final static Color COLOUREMPTY = new Color(222,222,222);
final static Color COLOURBACK = new Color(242,242,242);
final static Color COLOURBLACK = new Color(1,1,1);
final static Color COLOURGREY = new Color(75,75,75);
final static Color COLOURLAND = new Color (100,200,100);
final static Color COLOURLAKE = new Color (100,100,255);
final static Color COLOUROCEAN = new Color (10,10,130);

//global vars
int[][] board = new int[SIZE][SIZE];
int totLand = 0;

TileMapGen() { //constructor
    initGame();
    createAndShowGUI();
}

void initGame() {
    //clear board
    for (int i=0;i<SIZE;i++) {
        for (int j=0;j<SIZE;j++) {
            board[i][j]=EMPTY;
        }
    }
//check setup
if (SCRSIZE / SIZE < 5) {
    System.out.println("Board size is too small for number of squares! Aborting...");
    System.exit(0);
}

//Methods
    makeRandomMap();
    makeContinents();
    makeElevation();
}

void makeRandomMap() {
    int i, j;
    boolean done = false;
    int landTiles = 0;
    while (!done) {
        i = (int)(Math.random() * SIZE);
        j = (int)(Math.random() * SIZE);
        if (board[i][j] == EMPTY) {
            board[i][j] = LAND;
            landTiles++;
            if (landTiles >= NUM_LAND/48) done=true;
        }
    }
    totLand = totLand + landTiles++;
}

void makeElevation() {
    int i,j;
    boolean elevdone = false;
    int mtTiles = 0;
    while (!elevdone) {
        i = (int)(Math.random() * SIZE);
        j = (int)(Math.random() * SIZE);
        if (board[i][j] == LAND) {
            board[i][j] = MOUNTAIN;
            mtTiles++;
            if (mtTiles >= NUM_LAND/32) elevdone=true;
        }
    }
    totLand = totLand + mtTiles;
}

void makeContinents() {
    int i,j;
    boolean done = false;
    while (!done) {
        i = (int)(Math.random() * SIZE);
        j = (int)(Math.random() * SIZE);
        if (board[i][j] == LAND) {
            if (i > 0 && board[i][j] == OCEAN || board[i][j] == EMPTY) board[i][j] = LAND;
            if (i > 0 && board[i-1][j] == OCEAN) board[i-1][j] = LAND;
            if (i > 0 && board[i-1][j] == EMPTY) board[i-1][j] = LAND;
            if (i < SIZE-1 && board[i+1][j] == OCEAN) board[i+1][j] = LAND;
            if (i < SIZE-1 && board[i+1][j] == EMPTY) board[i+1][j] = LAND;
            if (j > 0 && board[i][j-1] == OCEAN) board[i][j-1] = LAND;
            if (j > 0 && board[i][j-1] == EMPTY) board[i][j-1] = LAND;
            if (j > 0 && board[i][j] == OCEAN || board[i][j] == EMPTY) board[i][j] = LAND;
            if (j < SIZE-1 && board[i][j+1] == OCEAN) board[i][j+1] = LAND;
            if (j < SIZE-1 && board[i][j+1] == EMPTY) board[i][j+1] = LAND;
            totLand++;
            if (totLand >= NUM_LAND) done=true;
        }
    }
    for (int im=0;im<SIZE;im++) {
        for (int jm=0;jm<SIZE;jm++) {
            boolean border = im > 0 && jm >+ SIZE-1 || jm > 0 && im >= SIZE-1 || im <= 0 || jm <=0;
            if(board[im][jm] == LAKE || board[im][jm] == EMPTY && border) {
                findOceans(im,jm);
            }else if(board[im][jm] == EMPTY || board[im][jm] == OCEAN && border == false) {
                findLakes(im,jm);
            }
        }
    }
}

void findLakes(int x, int y) {
    boolean border = x > 0 && y >= SIZE-1 || y > 0 && x >= SIZE-1 || x <= 0 || y <= 0;
    if (board[x][y] == EMPTY) {
        board[x][y] = LAKE;
        if (x > 0 && board[x-1][y] == EMPTY) findLakes(x-1,y);
        if (x < SIZE-1 && board[x+1][y] == EMPTY) findLakes(x+1,y);
        if (y > 0 && board[x][y-1] == EMPTY) findLakes(x,y-1);
        if (y < SIZE-1 && board[x][y+1] == EMPTY) findLakes(x,y+1);
        if (board[x][y] == LAKE && border == true) {
            findOceans(x,y);
        }
    }else if (board[x][y] == LAKE) {
        board[x][y] = OCEAN;
        if (x > 0 && board[x-1][y] == LAKE) findLakes(x-1,y);
        if (x < SIZE-1 && board[x+1][y] == LAKE) findLakes(x+1,y);
        if (y > 0 && board[x][y-1] == LAKE) findLakes(x,y-1);
        if (y < SIZE-1 && board[x][y+1] == LAKE) findLakes(x,y+1);
    }else if (board[x][y] == OCEAN) {
        if (board[x][y] != LAND && board[x][y] != EMPTY) {
            board[x][y] = LAKE;
            if (x > 0 && board[x-1][y] == OCEAN) findLakes(x-1,y);
            if (x < SIZE-1 && board[x+1][y] == OCEAN) findLakes(x+1,y);
            if (y > 0 && board[x][y-1] == OCEAN) findLakes(x,y-1);
            if (y < SIZE-1 && board[x][y+1] == OCEAN) findLakes(x,y+1);
        }
    }
}

void findOceans(int x, int y) {
    //boolean border = x > 0 && y >= SIZE-1 || y > 0 && x >= SIZE-1 || x <= 0 || y <= 0;
    if (board[x][y] == EMPTY || board[x][y] == LAKE) {
        board[x][y] = OCEAN;
        if (x > 0 && board[x-1][y] == EMPTY) findOceans(x-1,y);
        if (x < SIZE-1 && board[x+1][y] == EMPTY) findOceans(x+1,y);
        if (y > 0 && board[x][y-1] == EMPTY) findOceans(x,y-1);
        if (y < SIZE-1 && board[x][y+1] == EMPTY) findOceans(x,y+1);
        if (x > 0 && board[x-1][y] == LAKE) findOceans(x-1,y);
        if (x < SIZE-1 && board[x+1][y] == LAKE) findOceans(x+1,y);
        if (y > 0 && board[x][y-1] == LAKE) findOceans(x,y-1);
        if (y < SIZE-1 && board[x][y+1] == LAKE) findOceans(x,y+1);
    }
}

void createAndShowGUI() {
    DrawingPanel panel = new DrawingPanel();
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("Random Grid Landscape Generation");
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
    Container content = frame.getContentPane();
    content.add(panel, BorderLayout.CENTER);
    frame.setSize(SCRSIZE, SCRSIZE);
    frame.setResizable(false);
    frame.setLocation(256,0);
    frame.pack();
    frame.setVisible(true);
    panel.initGraphics();
}

class DrawingPanel extends JPanel {

    int jpanW, jpanH;
    int blockX, blockY;

    public DrawingPanel() {
        setBackground(COLOURBACK);
        this.setPreferredSize(new Dimension(32*30,32*30));
        MyMouseListener m1 = new MyMouseListener();
        addMouseListener(m1);
    }

    void initGraphics() {
        jpanW = this.getSize().width;
        jpanH = this.getSize().height;
        blockX = (int)((jpanW/SIZE)+0.5);
        blockY = (int)((jpanH/SIZE)+0.5);
    }

    public void paintComponent(Graphics g)  {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        for (int i=0;i<SIZE;i++) {
            g.drawLine(blockX*i,0,blockX*i,jpanH);
            g.drawLine(0,blockY*i,jpanW,blockY*i);
        }

        for (int i=0;i<SIZE;i++) {
            for (int j=0;j<SIZE;j++) {
                colourRect(i,j,g);
            }
        }
    }

    void colourRect(int i, int j, Graphics g) {

        int terrain = board[i][j];
        if (terrain == EMPTY) {
            g.setColor(COLOUREMPTY);
            g.fillRect(blockX*i+1, blockY*j+1, blockX-2, blockY-2);
        }
        if (terrain == LAND) {
            g.setColor(COLOURLAND);
            g.fillRect(blockX*i+1, blockY*j+1, blockX-2, blockY-2);
        }
        if (terrain == LAKE) {
            g.setColor(COLOURLAKE);
            g.fillRect(blockX*i+1, blockY*j+1, blockX-2, blockY-2);
        }
        if (terrain == OCEAN) {
            g.setColor(COLOUROCEAN);
            g.fillRect(blockX*i+1, blockY*j+1, blockX-2, blockY-2);
        }
        if (terrain == MOUNTAIN) {
            g.setColor(COLOURGREY);
            g.fillRect(blockX*i+1, blockY*j+1, blockX-2, blockY-2);
        }

    }

    class MyMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            //calculate which square you clicked on
            int i = (int) x/blockX;
            int j = (int) y/blockY;
            int terrain = board[i][j];
            if (terrain == LAND) {
                System.out.println("Clicked on land block");
            }if (terrain == LAKE) {
                System.out.println("Clicked on lake block");
            }if (terrain == OCEAN) {
                System.out.println("Clicked on ocean block");
            }if (terrain == MOUNTAIN) {
                System.out.println("Clicked on mountain block");
            }
            //allow the right mouse buttom to toggle/cycle the terrain
            if (e.getButton() != MouseEvent.BUTTON1) {
                switch (board[i][j]) {
                    case LAND:
                        board[i][j] = MOUNTAIN;
                        break;
                    case MOUNTAIN:
                        board[i][j] = EMPTY;
                        break;
                    default:
                        board[i][j] = LAND;
                    }
                    repaint();
                    return;
                }
                findLakes(i,j);
                repaint();
            }
        }
    }
}
