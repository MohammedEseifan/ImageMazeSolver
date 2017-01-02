import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 02/02/14
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageFilter extends JComponent {

    public  BufferedImage image =null ;

    public ImageFilter(BufferedImage image){

     this.image=image;

    }

    public BufferedImage getFilteredImage(){
        ColorConvertOp op =  new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        
        op.filter(image,image);

        ImageProducer pro = image.getSource();
        ContrastFilter filter = new ContrastFilter();
        ImageProducer ip = new FilteredImageSource(pro,filter);
        Image newImage=getToolkit().createImage(ip);

        BufferedImage finalImage = new BufferedImage(newImage.getWidth(null),newImage.getHeight(null),BufferedImage.TYPE_INT_RGB);
        finalImage.getGraphics().drawImage(newImage,0,0,null);



       return finalImage;
    }

    @Override
    protected void paintComponent(Graphics g) {


    }


}
