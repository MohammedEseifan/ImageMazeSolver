import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 02/02/14
 * Time: 1:54 PM
 *
 * ####################################################################################
 *                                      Purpose :
 * Program to experiment with path finding and Breadth first search. Takes in
 * an image of a maze and converts it into an interactive graphic the user can play with.
 * Allows user to click to assign the 'enemy' a new target, then illustrates shortest
 * path and animates the enemy travelling to it. Also has an 'auto' mode that will
 * automatically assign the enemy a target and navigate towards it. Lastly has an edit
 * mode that allows the user to edit the maze by adding or removing walls to see how the
 * enemy's path finding will react.
 * ######################################################################################
 *
 */

//Main class that handles GUI and user interaction
public class GameProof extends JComponent implements MouseListener, MouseMotionListener, ActionListener, KeyListener {


    //Control Panel
    static int[] enemyCoords = {100,100}; //Starting position of red dot aka enemy
    static String mazeName = "TestingImages/maze.gif"; //Name of image to load and scan
    static int enemySpeed = 9; //Movement speed of enemy (pixels per frame)
    static int enemySize = 2; //Size of enemy
    static boolean shadows = false; //Are there shadows in the picture?

    //Initialize global variables
    static int enemyPathIndex = 0;
    static ArrayList<String> enemyPath = new ArrayList<String>();
    static int gridSize = enemySize;
    static int[][] grid = new int[0][0];
    static JFrame window = new JFrame("PathFinding Proof of Concept by Mohammed");
    static boolean editMode = false;
    static boolean auto = false;
    static boolean pause = false;
    static Image img;
    static String target = "";
    static Random generator = new Random();


    public static void main(String[] args) {
        try {
            //Load image and perform any scaling and processing needed
            img = ImageIO.read(new File(System.getProperty("user.dir")+"/src/"+mazeName));
            double factor = img.getWidth(null)/(double)img.getHeight(null);
            img = OtsuBinarize.binarizeImage((BufferedImage)img,shadows);
            int desiredHeight = 800;
            if (img.getHeight(null)*2<desiredHeight){
                desiredHeight=img.getHeight(null)*2;
            }else if (img.getHeight(null)<desiredHeight){
                desiredHeight=img.getHeight(null);
            }
            //Resizing image to fit screen while maintaining aspect ratio
            while (img.getHeight(null)%(desiredHeight/gridSize) !=0 ||img.getWidth(null)%((desiredHeight*factor)/gridSize) !=0){
                desiredHeight+=gridSize;
            }

            window.setSize((int)(desiredHeight*factor),desiredHeight);
        } catch (IOException e) {
            //window.setSize(800, 800);
        }

        //Initialing JFrame
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameProof game = new GameProof();
        window.add(game);
        window.addMouseListener(game);
        window.addKeyListener(game);
        window.addMouseMotionListener(game);
        window.setVisible(true);

        //Creating 2D array from image
        grid=ImageScanner.scanImage(mazeName,shadows,window.getHeight()/gridSize,window.getWidth()/gridSize);

        Timer t = new Timer(35,game);
        t.start();
    }

    //Cleans the path returned from the path finding algorithm
    private static ArrayList<String> cleanPath(ArrayList<String> path){
        //If no path was found return empty array
        if (path.get(0).equals("nopath")){return new ArrayList<String>();}

        //Init variables before loop
        ArrayList<String> newPath = new ArrayList<String>();
        int lock;
        String y = path.get(0).split(" ")[0];

        if(y.equals(path.get(1).split(" ")[0])){
            lock = 0;
        }else{
            lock = 1;
        }
        newPath.add(path.get(0));
        for (int index=1; index<path.size(); index++){
            //If its the last point add it
            if(index == path.size()-1){newPath.add(path.get(index)); break;}
            //If the x or y has changed since the last point then add it
            if(!path.get(index).split(" ")[lock].equals(path.get(index+1).split(" ")[lock])){
                newPath.add(path.get(index));
                //find the next coord to lock
                if(path.get(index).split(" ")[0].equals(path.get(index+1).split(" ")[0])){
                    lock = 0;
                }else{
                    lock = 1;
                }
            }
        }
        return newPath;
    }

    public static void moveEnemy(){
        if(enemyPath.size()<1 || pause){return;}

        //Target coords are determined, x and y coords are flipped from the enemyPath
        int[] targetCoords = {Integer.valueOf(enemyPath.get(enemyPathIndex).split(" ")[1])*gridSize,Integer.valueOf(enemyPath.get(enemyPathIndex).split(" ")[0])*gridSize};

        //If the enemy has reached it's target
        if(enemyCoords[0] == targetCoords[0]&&enemyCoords[1] == targetCoords[1]){
            //If there are more targets then assign it the next target
            if (enemyPathIndex<enemyPath.size()-1) {
                enemyPathIndex++;
            }else{ //else reset variables
                enemyPathIndex=0;
                enemyPath.clear();
                target="";
                //If the auto mode is enabled then automatically create a new path for the enemy
                if (auto){newEnemyPath();}
                return;
            }
        }else{
            if(Math.abs(targetCoords[0]-enemyCoords[0])>enemySpeed){
                enemyCoords[0] += Math.copySign(enemySpeed,targetCoords[0]-enemyCoords[0]);
            }else{
                enemyCoords[0] += Math.copySign(targetCoords[0]-enemyCoords[0],targetCoords[0]-enemyCoords[0]);
            }
            if(Math.abs(targetCoords[1]-enemyCoords[1])>enemySpeed){
                enemyCoords[1] += Math.copySign(enemySpeed,targetCoords[1]-enemyCoords[1]);
            }else{
                enemyCoords[1] += Math.copySign(targetCoords[1]-enemyCoords[1],targetCoords[1]-enemyCoords[1]);
            }

        }
    }




    @Override
    protected void paintComponent(Graphics g) {
        //Draw background and grid
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        drawGrid(g);

        //Drawing Target
        if (target!=""){
        g.setColor(Color.blue);
        g.fillOval(Integer.valueOf(target.split(" ")[1])*gridSize,Integer.valueOf(target.split(" ")[0])*gridSize,enemySize/2,enemySize/2);
        }

        //Drawing Text
        g.setColor(Color.BLACK);
        g.fillRect(0,getHeight()-30,110,21);
        g.setColor(Color.RED);
        if (editMode){ g.setColor(Color.GREEN);}
        g.drawString("Edit Mode: "+String.valueOf(editMode),0,getHeight()-20);
        if (auto){ g.setColor(Color.GREEN);}else{g.setColor(Color.red);}
        g.drawString("Auto Pilot: "+String.valueOf(auto),0,getHeight()-10);

        //Drawing enemy path
        if (enemyPath.size()>0){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(2));
        for (int index=0;index<enemyPath.size()-1;index++){

            g2.drawLine(Integer.valueOf(enemyPath.get(index).split((" "))[1])*gridSize,Integer.valueOf(enemyPath.get(index).split((" "))[0])*gridSize,Integer.valueOf(enemyPath.get(index+1).split((" "))[1])*gridSize,Integer.valueOf(enemyPath.get(index+1).split((" "))[0])*gridSize);
        }
        }

        //Drawing enemy
        g.setColor(Color.BLUE);
        g.fillOval(enemyCoords[0],enemyCoords[1],enemySize*2,enemySize*2);
    }

    private static void drawGrid(Graphics g){

        g.setColor(Color.black);
        int walls = 0;
        for (int y=0;y<grid.length;y++){
            for (int x=0;x<grid[0].length;x++){
                if(grid[y][x]==1){
                    g.fillRect(gridSize*x,gridSize*y,gridSize,gridSize);

                }
            }

        }
        g.setColor(Color.LIGHT_GRAY);
//        //Drawing X-Lines
//        for(int xline=0;xline<grid.length;xline++){
//            g.drawLine(xline*gridSize,0,xline*gridSize,window.getHeight());
//        }
////         //Drawing Y-Lines
//        for(int yline=0;yline<grid.length;yline++){
//            g.drawLine(0, yline*gridSize,window.getWidth(),yline*gridSize);
//        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveEnemy();
        repaint();
    }

    //Assigns the enemy a new target and calculate the new path
    @Override
    public void mouseClicked(MouseEvent e) {
        if (editMode || auto || pause){return;}
        enemyPath.clear();
        target = String.valueOf((int)((e.getY()-25)/gridSize))+" "+String.valueOf((int)(e.getX()/gridSize));
        enemyPath = cleanPath(PathFinding.findPath(grid,
                String.valueOf((int)(enemyCoords[1]/gridSize))+" "+String.valueOf((int)(enemyCoords[0]/gridSize)),
                target));

    }

    /**
     * Assigns the enemy a random target and calculate the best path to it (only available in 'auto' mode)
     */
    private static void newEnemyPath(){
        if (auto){
            //Randomly generate new target location and calculate path
            target=String.valueOf(generator.nextInt(grid.length))+" "+String.valueOf(generator.nextInt(grid[0].length));
            ArrayList<String> path = PathFinding.findPath(grid,
                    String.valueOf((int) (enemyCoords[1] / gridSize)) + " " + String.valueOf((int) (enemyCoords[0] / gridSize)),
                    target);

            //Generate new target locations while there is no path to the current target location or the current target location location is inside a wall
            while (path.get(0)=="nopath" || grid[Integer.valueOf(target.split(" ")[0])][Integer.valueOf(target.split(" ")[1])]==1){
                target=String.valueOf(generator.nextInt(grid.length))+" "+String.valueOf(generator.nextInt(grid[0].length));
                path = PathFinding.findPath(grid,
                        String.valueOf((int) (enemyCoords[1] / gridSize)) + " " + String.valueOf((int) (enemyCoords[0] / gridSize)),
                        target);
            }
            enemyPath = cleanPath(path);
        }
    }

    /**
     * Gets called when the user edit's the maze, used to ensure the path is still valid
     */
    private static void recalculate(){
        System.out.println("Recalc: ");
        System.out.println(enemyPath);

        //Clear the current path and recalculate from current location
        if (enemyPath.size()<1){return;}
        target = enemyPath.get(enemyPath.size()-1);
        enemyPath.clear();
        enemyPathIndex=0;
        enemyPath = cleanPath(PathFinding.findPath(grid,
                String.valueOf((int)(enemyCoords[1]/gridSize))+" "+String.valueOf((int)(enemyCoords[0]/gridSize)),
                target));
        System.out.println(enemyPath);
        System.out.println(target);
    }

    @Override
    //Handle toggling between modes
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar()=='e'){
            editMode=!editMode;
            pause = !pause;
            if (!pause){
                recalculate();
            }
        }else if (e.getKeyChar()=='a'){
             auto=!auto;
            if (auto&&enemyPath.size()<1){newEnemyPath();}
        }

    }

    @Override
    //Draws Walls
    public void mouseDragged(MouseEvent e) {

        if(editMode){
            //enemyPath.clear();
            //enemyPathIndex=0;
            int y = (e.getY()-25)/gridSize;
            int x = e.getX()/gridSize;
            if(x<grid[0].length && y<grid.length)
            if (SwingUtilities.isLeftMouseButton(e)){
                if (grid[y][x] !=1){
                     grid[y][x]=1;
                 }
          }else if (SwingUtilities.isRightMouseButton(e)){
                if (grid[y][x] !=0){
                    grid[y][x]=0;
                }

            }
         repaint();
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    @Override
    public void keyReleased(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
