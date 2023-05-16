package solo_project;

import java.awt.Graphics;

// this is for Background.Java
public class Background {
	private LayerImage[] surround; 
	private int w; 
	private int h; 
	
	public Background(int w, int h, String f, int imageNumber) {
		this.w = w;
		this.h = h;
		setBackground(f, imageNumber);
	}

	private void setBackground(String im, int imageNumber) {
		try {
			surround = new LayerImage[imageNumber];
			for(int i = 1; i <= imageNumber; i++) {
				surround[i - 1] = new LayerImage(im + i + ".png",
												0, 0,i, 
												w, h);
			}
		} catch (Exception g) {
			System.out.print(g.getMessage());
		}
		
	}
	
	public void sketch(Graphics e) {
		for(int i = surround.length-1; i >=0; i--)
			surround[i].sketch(e);
	}
	
	public void byMovingLeft(int dx) {
		for(int i = 0; i < surround.length; i++)
			surround[i].byMovingLeft(dx);
	}
	
	public void byMovingRight(int dx) {
		for(int i = 0; i < surround.length; i++)
			surround[i].byMovingRight(dx);
	}
	
	public void byMovingUp(int db) {
		for(int i = 0; i < surround.length; i++)
			surround[i].byMovingUp(db);
	}
	
	public void byMovingDown(int db) {
		for(int i = 0; i < surround.length; i++)
			surround[i].byMovingDown(db);
	}
	
	public void restart() {
		for(int i = 0; i < surround.length; i++)
			surround[i].restart();
	}
	
	public int getX(int i) {
		return surround[i].getX();
	}
	
	public int getSize() {
		return surround.length;
		
	}
	
	public boolean endOfLeftIsNot() {
		boolean bool = true;
		
		for(int i = 0; i  < surround.length; i++) {
			if (surround[i].getX() > 0) {
				bool = false;
				break;
			}
		}
		return bool;
	}
}
