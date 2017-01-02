import java.awt.image.ColorModel;
import java.awt.image.ImageFilter;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Mohammed
 * Date: 02/02/14
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContrastFilter extends ImageFilter {
    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {

        int comp1[] = null;
        //int oldComp2[] = null;
        int comp2[] = null;
        //ArrayList<int[]> oldPixels = new ArrayList<int[]>();
        for(int loop = 0; loop<scansize;loop++){
            if(loop>0){
                comp1 = model.getComponents(pixels[loop],comp1,0);
                comp2 = model.getComponents(pixels[loop-1],comp2,0);
                //oldPixels.add(loop,comp1.clone());
                //oldComp2 = oldPixels.get(loop-1);

                int average = (comp1[0]+comp1[1]+comp1[2])/3;
                int average2 = (comp2[0]+comp2[1]+comp2[2])/3;
                //System.out.println(average+" "+average2);
                if(Math.abs(average-average2)>125){
                    comp1[0]=Math.abs(comp2[0]-255);
                    comp1[1]=Math.abs(comp2[1]-255);
                    comp1[2]=Math.abs(comp2[2]-255);
                }else{
                    comp1[0]=comp2[0];
                    comp1[1]=comp2[1];
                    comp1[2]=comp2[2];

                }

                pixels[loop]=model.getDataElement(comp1,0);
            }else{
                comp1 = model.getComponents(pixels[loop],comp1,0);
                //oldPixels.add(comp1.clone());
                comp1[0]=255;
                comp1[1]=255;
                comp1[2]=255;

                pixels[loop]=model.getDataElement(comp1,0);
            }


        }
        super.setPixels(x, y, w, h, model, pixels, off, scansize);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setHints(int hints) {
        hints |= TOPDOWNLEFTRIGHT;
        super.setHints(hints);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
