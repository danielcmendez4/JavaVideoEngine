package solo_project;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sketch {


	public BufferedImage im;
	public BufferedImage imMirror;
	
	public Sketch(String nameFile) {
		try {
			im = ImageIO.read(new File(nameFile));
			imMirror = imageMirror(im);
		} catch (IOException g) {
			g.printStackTrace();
		}
	}
	
	public Sketch(BufferedImage im) {
		this.im = getNewPicture(im);
		imMirror = imageMirror(im);
	}

	public static BufferedImage getNewPicture(BufferedImage im) { // private 
		int h = (im.getHeight(null));// h = height 
		int w = (im.getWidth(null)); // w = width
		
		BufferedImage newPicture = new BufferedImage(w, h, ((BufferedImage)im).getType());
		Graphics2D e2 = newPicture.createGraphics();
		
		e2.drawImage(im, 0, 0, w, h, null);
		e2.dispose();
		
		return newPicture;
	}


	public static BufferedImage imageMirror(BufferedImage im) { // private 
		int h = (im.getHeight(null));
		int w = (im.getWidth(null));
		
		BufferedImage newPicture = new BufferedImage(w, h, ((BufferedImage)im).getType());
		Graphics2D e2 = newPicture.createGraphics();
		
		e2.drawImage(im, 0, 0, -w, h, null);
		e2.dispose();
		
		return newPicture;
	}
	
	public int getW() {
		return im.getWidth();
	}
	
	public int getH() {
		return im.getHeight();
	}
}
