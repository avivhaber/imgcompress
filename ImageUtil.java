import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Arrays;

public class ImageUtil {
    int [][][] data;
    final int panelWidth = 600;
    final int panelHeight = 600;
    int imageWidth;
    int imageHeight;
    PrintWriter writer;

    public ImageUtil (String infile, String outfile) throws Exception {
        BufferedImage img = ImageIO.read(new File(infile));
        imageWidth = img.getWidth();
        imageHeight = img.getHeight();
        writer = new PrintWriter(outfile);
        data=new int[3][imageHeight][imageWidth];
        
        for (int j = 0; j < imageWidth; j++) {
            for (int k = 0; k < imageHeight; k++) {
                Color temp = new Color(img.getRGB(j, k));
                data[0][k][j] = temp.getRed();
                data[1][k][j] = temp.getGreen();
                data[2][k][j] = temp.getBlue();
            }
        }
    }

    private double step(double x) {
        return x==0 ? (1/Math.sqrt(2)) : 1;
    }

    // Adapted from https://unix4lyfe.org/dct/
    private void dct(double[][] a, int channel, int xoff, int yoff) {
        for (int v=0; v<8; v++) {
            for (int u=0; u<8; u++) {
                double sum=0;
                for (int y=0; y<8; y++) {
                    for (int x=0; x<8; x++) {
                        double val = data[channel][yoff+y][xoff+x]-128;
                        double cos1 = Math.cos((double)(2*x+1) * (double)u * Math.PI/16.0);
                        double cos2 = Math.cos((double)(2*y+1) * (double)v * Math.PI/16.0); 
                        sum += val * cos1 * cos2;
                    }
                }
                a[v][u] = 0.25 * step(u) * step(v) * sum;
            }
        }
    }

    private void quantize(double[][] a) {
        for (int j = 0; j < a.length; j++) {
            for (int k = 0; k < a[0].length; k++) {
                if (j>4 || k>4) a[j][k]=0;
            }
        }
    }

    void compress() {
        double[][][] cos = new double[3][8][8];
        for (int i=0; i<imageHeight/8; i++) {
            for (int j=0; j<imageWidth/8; j++) {
                int yoff=i*8;
                int xoff=j*8;
                for (int k=0; k<3; k++) {
                    dct(cos[k], k, xoff, yoff);
                    quantize(cos[k]);
                }
                writer.println(Arrays.deepToString(cos[0]));   
            }   
        }
        System.out.println("Success!");
    }

    public static void main (String[] args) throws Exception {
        ImageUtil img = new ImageUtil("meme3.png", "meme.arf");
        img.compress();
    }
}