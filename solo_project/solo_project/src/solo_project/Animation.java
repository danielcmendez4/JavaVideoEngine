package solo_project;

// Project citations: 
// Developing Games in Java by David Brackeen
// Core Techniques and Algorithms in Game Programming by Daniel Sanchez and Crspo Dalmau 
// A1 Techniques for game programming by Mat Buckland and Andre LaMothe
// Post-Covid super Mario brothers Game  
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Animation {
	private ArrayList<Sketch> im;//im = image
	private int cur = 0;
	private int d; //d = delay 
	private int delayInitial;
	
	public Animation() {
		im = new ArrayList<>();
		
		cur = 0;
		d = 0;
		
	}
	
	public Animation(String p, String fmt, int period) throws IOException {
		this.delayInitial = period;
		d = period;
		im = new ArrayList<>();
		
		File f = new File(p);
		File fileParent = f.getParentFile();
		
		if(fileParent.exists()) {
			
			int imageNumber = fileParent.listFiles().length;
			for (int i = 0; i < imageNumber; i++) {
				String nameFile = p + i + fmt;
				im.add(new Sketch(nameFile));
			}
		} else {
			throw new IOException("File is Non-Existant!");
		}
	}
	
	public void upgrade() {
		if(isOverAnimation());
		restart();
			
	}
	
	public void setPeriod(int t) { // t = time
		delayInitial = t;
		d = t;
	}
	
	public void setDelayInitial(int DI) {
		delayInitial += DI;
		d += DI;
	}
	
	public BufferedImage imageNext() {
		if(cur != im.size()-1) {
			if(d == 0) {
				cur++;
				
				d = delayInitial;
		
			} else
				d--;
		}
		return im.get(cur).im;
	}

	private void restart() {
		cur = 0;
		
	}
	
	public BufferedImage imagePrevious() {
		
		if(cur > 0 ) {
			if(d == 0) {
				cur--;
				d = delayInitial;
			} else
				d--;
		}
		return im.get(cur).im;
	}
	
	public BufferedImage getIm() {
		return im.get(cur).im;
	}
	
	public BufferedImage getImMirror() {
		return im.get(cur).imMirror;
		
	}

	private boolean isOverAnimation() {
		return cur == im.size()-1;
	}
	
	public int getIndex() {
		return cur;
	}
	
	public int getImageNumber() {
		return im.size();
	}
	
	public void imageInclude(BufferedImage im) {
		this.im.add(new Sketch(im));
	}
	
	public void imageInclude(String f) {
		im.add(new Sketch(f));
	}
	
	public int getCur() {
		return cur;
	}
	
	public int getD() {
		return d;
	}
	
	public void setD(int d) {
		this.d = d;
		delayInitial = d;
	}
	
}
