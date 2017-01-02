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
 * To change this template use File | Settings | File Templates.
 */
public class GameProof extends JComponent implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

    static int[] enemyCoords = {100,100};
    static ArrayList<String> enemyPath = new ArrayList<String>();
    static boolean convertImage = true;
    static String mazeName = "maze.gif";
    static boolean shadows = false; //Are there shadows in the picture?
    static int enemyPathIndex = 0;
    static int enemySpeed = 9;
    static int enemySize = 2;
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
            img = ImageIO.read(new File(System.getProperty("user.dir")+"/src/"+mazeName));
            double factor = img.getWidth(null)/(double)img.getHeight(null);
            //ImageFilter preOp = new ImageFilter((BufferedImage)img);
            //img=preOp.getFilteredImage();
            img = OtsuBinarize.binarizeImage((BufferedImage)img,shadows);
            int desiredHeight = 800;
            if (img.getHeight(null)*2<desiredHeight){
                desiredHeight=img.getHeight(null)*2;
            }else if (img.getHeight(null)<desiredHeight){
                desiredHeight=img.getHeight(null);

            }
            while (img.getHeight(null)%(desiredHeight/gridSize) !=0 ||img.getWidth(null)%((desiredHeight*factor)/gridSize) !=0){
                desiredHeight+=gridSize;
                System.out.println(desiredHeight);
            }

            window.setSize((int)(desiredHeight*factor),desiredHeight);
        } catch (IOException e) {
            //window.setSize(800, 800);
        }
        //window.setSize(450, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GameProof game = new GameProof();
        window.add(game);
        window.addMouseListener(game);
        window.addKeyListener(game);
        window.addMouseMotionListener(game);
        window.setVisible(true);

        //grid = new int[window.getHeight()/gridSize][window.getWidth()/gridSize];
       // if(convertImage){ImageFilter temp = new ImageFilter(mazeName);}
        grid=ImageScanner.scanImage(mazeName,shadows,window.getHeight()/gridSize,window.getWidth()/gridSize);


        Timer t = new Timer(35,game);
        t.start();
    }

    //Cleans the path returned from the path finding algorithm
    private static ArrayList<String> cleanPath(ArrayList<String> path){

        if (path.get(0).equals("nopath")){return new ArrayList<String>();}
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
        if(enemyCoords[0] == targetCoords[0]&&enemyCoords[1] == targetCoords[1]){
            if (enemyPathIndex<enemyPath.size()-1) {
                enemyPathIndex++;
            }else{
                enemyPathIndex=0;
                enemyPath.clear();
                target="";
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



    // linkwdlit <In t> nME = LINKED LIST
    //while loop(name){
    // int current  = name.poll;
    // }
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        drawGrid(g);
        //g.drawImage(img,0,0,getWidth(),getHeight(),null);


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

    @Override
    public void mouseClicked(MouseEvent e) {
        if (editMode || auto || pause){return;}
        enemyPath.clear();
        target = String.valueOf((int)((e.getY()-25)/gridSize))+" "+String.valueOf((int)(e.getX()/gridSize));
        enemyPath = cleanPath(PathFinding.findPath(grid,
                String.valueOf((int)(enemyCoords[1]/gridSize))+" "+String.valueOf((int)(enemyCoords[0]/gridSize)),
                target));

    }

    private static void newEnemyPath(){
        if (auto){



            target=String.valueOf(generator.nextInt(grid.length))+" "+String.valueOf(generator.nextInt(grid[0].length));
            ArrayList<String> path = PathFinding.findPath(grid,
                    String.valueOf((int) (enemyCoords[1] / gridSize)) + " " + String.valueOf((int) (enemyCoords[0] / gridSize)),
                    target);
            while (path.get(0)=="nopath" || grid[Integer.valueOf(target.split(" ")[0])][Integer.valueOf(target.split(" ")[1])]==1){
                target=String.valueOf(generator.nextInt(grid.length))+" "+String.valueOf(generator.nextInt(grid[0].length));
                path = PathFinding.findPath(grid,
                        String.valueOf((int) (enemyCoords[1] / gridSize)) + " " + String.valueOf((int) (enemyCoords[0] / gridSize)),
                        target);
            }
            enemyPath = cleanPath(path);


        }
    }

    private static void recalculate(){
        System.out.println("Recalc: ");
        System.out.println(enemyPath);
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
