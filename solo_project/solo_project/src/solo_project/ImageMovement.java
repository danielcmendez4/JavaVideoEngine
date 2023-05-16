package solo_project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageMovement extends Rectangle {
	

	private static final long serialVersionUID = 1L;
	private ArrayList<Animation> mate = new ArrayList<>();
	private Point speedInitial = new Point(2,2);
	public Point finalSpeed = new Point(5,7);
	public Point speed = new Point(1,1); // velocity = acceleration
	
	private float isWScale = 1;
	private float isHScale = 1;
	private int cur = 0;
	private BufferedImage im;
	public String formatIm = "png";
	public String pathFile[];
	
	private int moveHorizontalLast = Motion.RIGHT;
	private int moveVerticalLast = Motion.DOWN;
	
	public boolean[] motions = new boolean[4];
	private char charID;
	
	public float boundaryLeft = 0;
	public float boundaryRight = 0;
	public float boundaryDown = 0;
	public float boundaryUp = 0;
	
	public ImageMovement(String[] pathFile, int[] period, char charID) {
		this.charID = charID;
		this.pathFile = pathFile;
		
		for(int i = 0; i < pathFile.length; i++) {
			try { 
				Animation animationNew = new Animation(pathFile[i],"."+formatIm, period[i]);
				mate.add(animationNew);
			} catch (IOException g) {
				g.printStackTrace();
			}
		}
		
		upgrade();
		
		width = (int) (im.getWidth() * isWScale);
		height = (int) (im.getHeight() * isHScale);
		
	}
	
	public ImageMovement(String pathFile, char charID) {
		try {
			this.charID = charID;
			im = ImageIO.read(new File(pathFile + "." + formatIm));
			width = (int)(isWScale * im.getWidth());
			height = (int) isHScale * im.getHeight();
		} catch (IOException g) {
			g.printStackTrace();
		}
	}
	
	public ImageMovement(ImageMovement random) {
		
		charID = random.getCharId();
		
		speedInitial = new Point(random.speedInitial);
		finalSpeed = new Point(random.finalSpeed);
		speed = new Point(random.speed);
		
		isWScale = random.isWScale;
		isHScale = random.isHScale;
		cur = random.cur;
		im = random.im;
		width = random.width;
		height = random.height;
		
		moveVerticalLast = random.moveVerticalLast;
		moveHorizontalLast = random.moveHorizontalLast;
		
		for(int i = 0; i < motions.length; i++) {
			motions[i] = random.motions[i];
		}
		
		mate = random.mate;
	}


	public void upgrade() {
		
		upgradeSpeed(); 
		
		if(mate.size() >= 1) {
			upgradeIm();
			mate.get(cur).upgrade();
		}
	}
	
	public void upgradeIm() {
		mate.get(cur).imageNext();
		
		if(leftMoveLast()) {
			im = mate.get(cur).getImMirror();
		} else {
			im = mate.get(cur).getIm();
		}
	}


	public void upgradeSpeed() {
		if(inMotion()) {
			cur = 1;
			if(speedInitial.x + speed.x <= finalSpeed.x)
				speedInitial.x += speed.x;
			
			if(speedInitial.y + speed.y <= finalSpeed.y)
				speedInitial.y += speed.y;
		} else {
			speedInitial.x = 0;
			// speedInitial.y = 0; TODO
			cur = 0;
		}
	}

	public void sketch(Graphics e) {
		color(e, x, y);
	}
	
	public void sketch(Graphics e, Point position) {// position = offset
		color(e, x + position.x,y + position.y);
	}
	
	public void sketch(Graphics e, int c, int y) {
		color(e,x,y);
	}
	
	private void color(Graphics e, int x, int y) {
		var wImg = (int) (im.getWidth() * isWScale);
		var hImg = (int) (im.getHeight() * isHScale);
		e.drawImage(im, x, y, wImg, hImg, null);
		
		e.setColor(Color.blue);
		e.drawRect(x, y, width, height);
	}
	
	public void MovingRight() {
		byMovingRight(speedInitial.x);
		moveHorizontalLast = Motion.RIGHT;
	}
	

	public void MovingLeft() {
		byMovingLeft(speedInitial.x);
		moveHorizontalLast = Motion.LEFT;
	}
	
	public void MovingUp() {
		byMovingUp(speedInitial.y);
		moveVerticalLast = Motion.UP;
	}
	
	public void MovingDown() {
		byMovingDown(speedInitial.x);
		moveVerticalLast = Motion.DOWN;
	}
	
	public void byMovingRight(float dx) {
		x -= dx; 
		
	}
	
	public void byMovingLeft(float dx) {
		x += dx; 
		
	}
	
	public void byMovingUp(float dy) {
		x -= dy; 
		
	}
	
	public void byMovingDown(float dy) {
		x += dy; 
		
	}
	
	public boolean inMotion() {
		for(int i = 0; i<motions.length; i++) {
			if (motions[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean rightMoveLast() {
		
		return moveHorizontalLast == Motion.RIGHT;
	}
	
	public boolean leftMoveLast() {
		
		return moveHorizontalLast == Motion.LEFT;
	}


	public boolean upMoveLast() {
		
		return moveVerticalLast == Motion.UP;
	}


	public boolean downMoveLast() {
		
		return moveVerticalLast == Motion.DOWN;
	}

	public boolean rightMovingIs() {
		return motions[0];
	}
	
	public boolean leftMovingIs() {
		return motions[1];
	}
	
	public boolean upMovingIs() {
		return motions[2];
	}
	
	public boolean downMovingIs() {
		return motions[3];
	}
	
	public void setFacingOffset(int offset) {
		moveHorizontalLast = offset;
	}
	
	public int getCur() {
		return cur; 
	}

	public char getCharId() {
	
		return charID;
	}
	
	public ImageMovement getDuplicate() {
		ImageMovement sprite = new ImageMovement(this);
		return sprite;
	}
	
	public void setScale(float isWScale, float isHScale ) {
		this.isWScale = isWScale;
		this.isHScale = isHScale; 
		
		width *= isWScale; 
		height *= isHScale; 
	}
	
	public float getXPosition() {
		
		float teeempVx = speedInitial.x + speed.x <= finalSpeed.x ?
				speedInitial.x + speed.x : speedInitial.x;
		
		return teeempVx;
	}
	
	public float getYPosition() {
		 float teeempVy = speedInitial.y + speed.x <= finalSpeed.y ?
				 speedInitial.y + speed.y : speedInitial.y;
		
		 return teeempVy; 
	}
	
	public float getVxs() {
		return speedInitial.x;
	}
	
	public float getVys() {
		return speedInitial.y;
	}
	
	public float getVxf() {
		return finalSpeed.x;
	}


	public float getVyf() {
		return finalSpeed.y;
	}
	
	public float getSx() {
		return speed.x;
	}
	
	public float getSy() {
		return speed.y;
	}

}