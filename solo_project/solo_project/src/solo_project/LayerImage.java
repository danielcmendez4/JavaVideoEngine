package solo_project;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class LayerImage {
	private Image im;
	
	private int x;
	private int b; 
	private int c;
	
	private int xPrimary;
	private int bPrimary;
	private int cPrimary;
	
	private int wiScreen;// wScreen = screenWidth
	private int heScreen; // gScreen = screenHeight
	
	public LayerImage(String f, int x, int b, int c, int wScreen, int hScreen) {
		try {
			im = ImageIO.read(new File(f));
		} catch (Exception g) {
			System.out.println(g.getMessage());
		}
		
		this.x = x;
		this.b = b;
		this.c = c;
		
		xPrimary = x;
		bPrimary = b;
		cPrimary = c;
		
		this.heScreen = heScreen;
		this.wiScreen = wiScreen;
	}
	
	public void sketch(Graphics e) {
		for(int i = 0; i < 10; i++) 
			e.drawImage(im, x/c + wiScreen*i, b, wiScreen, heScreen, null);
	}
	
	public void byMovingLeft(int dx) {
		x -= dx; 
	}
	
	public void byMovingRight(int dx) {
		x += dx; 
	}
	
	public void byMovingUp(int db) {
		b += db; 
	}
	
	public void byMovingDown(int db) {
		b -= db; 
	}
	
	public void setA(int x) {
		this.x = x;
	}
	
	public void setB(int b) {
		this.b = b;
	}
	
	public int getX() {
		return x;
	}
	
	public int getB() {
		return b;
	}
	
	public void setWiScreen(int w) {
		wiScreen = w;
	}
	
	public void setHeScreen(int h) {
		heScreen = h; 
	}
	
	public void restart() {
		x = xPrimary;
		b = bPrimary;
		c = cPrimary;
	}
	
}