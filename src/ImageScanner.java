import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 14/11/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageScanner {

//    public static void main(String[] args){
//        scanImage("",100,100);
//    }

     public static int[][] scanImage(String filepath,boolean shadows, int mapHeight,int mapWidth){
         BufferedImage img = null;
         do{
             try {
                 File f = new File(System.getProperty("user.dir")+"/src/"+filepath);
                 if (f.exists()){img = ImageIO.read(f);break;}

                 //If the gif file is not found then try other file types
                f = new File(System.getProperty("user.dir")+"\\src\\maze.jpg");
                if (f.exists()){img = ImageIO.read(new File(System.getProperty("user.dir")+"\\src\\maze.jpg"));break;}

                f  = new File(System.getProperty("user.dir")+"\\src\\maze.jpeg");
                if (f.exists()){img = ImageIO.read(new File(System.getProperty("user.dir")+"\\src\\maze.jpeg"));break;}

                f  = new File(System.getProperty("user.dir")+"\\src\\maze.png");
                if (f.exists()){img = ImageIO.read(new File(System.getProperty("user.dir")+"\\src\\maze.png"));break;}


             } catch (IOException e) {
             }
         }while (false);

        int[][] newMap = new int[mapHeight][mapWidth];

//         //clipping undesired borders
//         int startx;
//         int starty;
//         int width;
//         int hieght;
//         ArrayList<Integer> numbersx = new ArrayList<Integer>();
//         ArrayList<Integer> numbersy = new ArrayList<Integer>();
//
//         for(int x = 0;x<img.getWidth();x+=5){
//            for(int y = 0;y<img.getHeight();y+=5){
//                if(img.getRGB(x,y)==Color.black.getRGB()){
//                    numbersy.add(y);
//                    break;
//                }
//            }
//             if(numbersy.size()>0 && img.getRGB(x,numbersy.get(numbersy.size()-1))==Color.black.getRGB()){
//                 numbersx.add(x);
//
//             }
//         }
//         startx= Collections.min(numbersx);
//         starty= Collections.min(numbersy);
//         System.out.println(startx+"c "+starty);

         //Processing image for easier analysis
         //ImageFilter preOp = new ImageFilter(img);
         //img = OtsuBinarize.binarizeImage(img,shadows);


        //Analysing image to create maze
        int xScale = Math.round(img.getWidth()/(float)(mapWidth));
        int yScale = Math.round(img.getHeight()/(float)(mapHeight));
        int whiteTile = 0;
        int blackTile = 0;
         System.out.println(xScale+" "+yScale);
         System.out.println(mapWidth+" "+mapHeight);
        //For each tile in the grid
        for (int x = 0;x<mapWidth;x++){
            for (int y = 0;y<mapHeight;y++){
                whiteTile=0;
                blackTile=0;
                for (int addx=0;addx<xScale;addx++){
                    for (int addy=0;addy<yScale;addy++){
                        if (x*xScale+addx>img.getWidth()-1 ||y*yScale+addy>img.getHeight()-1){continue;}
                        if (img.getRGB(x*xScale+addx,y*yScale+addy)==Color.white.getRGB()){
                            whiteTile++;
                        }else{
                            blackTile++;
                        }

                    }

                }
                if (blackTile>=whiteTile && blackTile!=0){
                    newMap[y][x]=1;
                }else{
                    newMap[y][x]=0;
                }


            }
        }

        if (img.getWidth()%mapWidth!=0 || img.getHeight()%mapHeight!=0){
            if (img.getWidth()%mapWidth!=0){xScale=img.getWidth()%mapWidth;}
            if (img.getHeight()%mapHeight!=0){yScale=img.getHeight()%mapHeight;}
        }

        return newMap;
     }





}
