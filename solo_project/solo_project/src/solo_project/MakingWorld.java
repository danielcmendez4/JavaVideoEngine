package solo_project;

import static java.lang.StrictMath.abs;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class MakingWorld {
	
	private static ImageMovement[][] components; 
	private static ImageMovement character;
	private static String[] componentsFilePaths;
	
	private static ArrayList<String> worldCode;
	private static int count = 0;
	private static String worldFiles;
	private static Point position; 
	
	private static int worldHeight;
	private static int worldWidth;
	private static int componentsWidthAvg;
	private static int componentsHeightAvg;
	
	private boolean MovingLeft;
	private boolean MovingRight;
	private boolean MovingUp;
	private boolean MovingDown;
	
	private boolean wasGravity = true;
	
	private int leapPosition = 0;
	private int leapHeight = 155;
	private boolean startLeap = false;
	
	private boolean movedTile = false; 
	
	private Motion motion; 
	
	public MakingWorld(String world, Hashtable<Character,ImageMovement> assets) throws IOException {
		
		File file = new File(world);
		File parent = file.getParentFile();
		
		if (parent.exists()) {
			worldFiles = world; 
			
			worldCode = getContentFile(world+count+".txt");
			int cols = getLargestLine(worldCode);
			int rows = worldCode.size();
			
			components = new ImageMovement[rows][cols];
		} else {
			throw new IOException("File is Invalid!!!");
		}
		
		addComponents(assets);
		Show.h = worldHeight; 
		
		position = new Point();
		
		System.out.println("World Height: " + worldHeight);
        System.out.println("World Width: " +  worldWidth);
        
        System.out.println("component height: " +  componentsHeightAvg);
        System.out.println("component width: " +  componentsWidthAvg);
	}
	
	public void actionInput() {
		
		MovingLeft = VideoGame.key.boardDown(KeyEvent.VK_LEFT);
		MovingRight = VideoGame.key.boardDown(KeyEvent.VK_RIGHT);
		
		if(!MovingUp)
			MovingUp = VideoGame.key.boardDownOnce(KeyEvent.VK_UP); 
		
		character.motions[0] = MovingLeft; 
		character.motions[1] = MovingRight; 
		character.motions[2] = MovingDown; 
		character.motions[3] = MovingUp; 
	}
	
	public void upgrade() {
		Match outcome[] = new Match[4]; 
		Match teeemp1; 
		
		character.x += abs(position.x);
		character.y += abs(position.y);
		
		for(int theRow = 0; theRow<components.length; ++theRow) {
			for(int theCol = 0; theCol<components[theRow].length; ++theCol) {
				
				if(!wasVacant(theRow,theCol)) {
					
					if(!wasCharacter(theRow,theCol)) {
						
						if(wasGravity) {
							teeemp1 = wasCollisionBottom(theRow,theCol);
							if(!teeemp1.wasVacant()) outcome[Motion.DOWN] = teeemp1;
							
						}
						
						if(MovingRight) {
							teeemp1 = wasCollisionRight(theRow,theCol);
							if(!teeemp1.wasVacant()) outcome[Motion.RIGHT] = teeemp1;
							
						}
						
						if(MovingLeft) {
							teeemp1 = wasCollisionLeft(theRow,theCol);
							if(!teeemp1.wasVacant()) outcome[Motion.LEFT] = teeemp1;
							
						}
						
						if(MovingUp) {
							teeemp1 = wasCollisionTop(theRow,theCol);
							if(!teeemp1.wasVacant()) outcome[Motion.UP] = teeemp1;
							
						}
					}
					components[theRow][theCol].upgrade();
				}
			}
		}
		
		character.x -= abs(position.x);
		character.y -= abs(position.y);
		
		upgradeCharacter(outcome); 
		upgradeBlocks(outcome);
	}
	

	private void upgradeCharacter(Match collision[]) {
		upgradeMovingRight(collision[Motion.RIGHT]);
		
		upgradeMovingLeft(collision[Motion.LEFT]);
		
		if(collision[Motion.DOWN] != null && MovingUp)
			startLeap = true;
		
		upgradeMovingUp(collision[Motion.UP]);
		
		upgradeMovingDown(collision[Motion.DOWN]);
	}


	private void upgradeMovingRight(Match collision) {
		if(MovingRight)
			character.setFacingOffset(Motion.RIGHT); // on ImageMovement.Java 
		
		if(MovingRight) {
			
			if(wasCharacterFinishedScreen()) {
					character.x = Show.w - character.width - 2; 
			}
			else if(wasCharacterCenterScreen() && !wasFinishedOfMap()) {
				character.x = (Show.w/2 - character.width) -1;
			}
			
			else if(collision != null) {
				int R = collision.theRow; 
				int C = collision.theCol;
				character.x = abs(components[R][C].x - character.width - 1);
			}
			else {
				character.MovingRight(); // in ImageMovement.java in line 161
			}
		}
	}
	

	private void upgradeMovingLeft(Match collision) {
		if(MovingLeft)
			character.setFacingOffset(Motion.LEFT);
		if(MovingLeft) {
			
			if(collision != null && position.x >= 0 ) {
				int R = collision.theRow;
				int C = collision.theCol;
				character.x = abs(components[R][C].x + components[R][C].width + 1);
			}
			else if(wasCharacterBeginScreen()) {
				character.x = 0;
			}
			else if(position.x >= 0 || !wasCharacterCenterScreen()) {
				character.MovingLeft();
			}
		}
	}
	
	
	private void upgradeMovingUp(Match collision) {
		if(startLeap) {
			
			if(leapPosition <= leapHeight && collision == null) {
				
				character.MovingUp();
				leapPosition += character.getVxs() + character.getSy(); // in ImageMovement.java
				wasGravity = false; 
			}
			else {
				wasGravity = true; 
				startLeap = true; 
				MovingUp = false; 
			}
		}
	}
	
	private void upgradeMovingDown(Match collision) {
		if(wasGravity) {
			if (collision == null) {
				character.MovingDown();
			} else {
				int R = collision.theRow;
				int C = collision.theCol;
				character.y = abs(components[R][C].y - character.height - 1);
				leapPosition = 0;
				MovingUp = false; 
			}
		}
	}
	
	public void upgradeBlocks(Match collision[]) {
		
		if(!wasCharacterCenterScreen() || wasCharacterFinishedScreen() ) {
			return; 
		}
		
		int R, C;
		if (MovingRight) {
			if(collision[Motion.RIGHT] != null) {
				R = collision[Motion.RIGHT].theRow;
				C = collision[Motion.RIGHT].theCol;
				position.x = (components[R][C].x - character.x - character.width -1) * -1;
				System.out.println(character.x);
			}
			else if (wasFinishedOfMap()) {
				position.x = (getWorldWidth() - Show.w)* -1;
			}
			else {
				position.x -= character.getXPosition();
			}
		}
		if(MovingLeft) {
			
			if(collision[Motion.LEFT] != null ) {
				
				R = collision[Motion.LEFT].theRow;
				C = collision[Motion.LEFT].theCol;
				
				position.x = (components[R][C].x + components[R][C].width - character.x + 1) * -1;
			} 
			else if(wasBeginOfMap()) {
				
			}
		}	
	}

	//---------------------------------------- to render 
	public void sketch(Graphics e) {
		int R = getBeginningTheRow(); 
		int C = getBeginningTheCol(); 
		
		character.sketch(e);
		for(int i = R; getTheRow(i) < Show.h; i++) {
			for (int j = C; getTheCol(j) < Show.w; j++) {
				 if(!wasVacant(i,j)) {
					 if(!wasCharacter(i,j)) {
						 components[i][j].sketch(e,position);
					 }
				 }
			}
		}
	}
	//------------------------------------------------ for collide 
	
	public Match wasCollisionRight(int theRow, int theCol) {
		if(live(theRow, theCol) && wasSideLeft(theRow, theCol))
		return new Match(theRow, theCol);
		
		else return new Match();
	}



	public Match wasCollisionLeft(int theRow, int theCol) {
		if(live(theRow, theCol) && wasSideRight(theRow, theCol))
			return new Match(theRow, theCol);
			
			else return new Match();
	}
	
	public Match wasCollisionTop(int theRow, int theCol) {
		if(live(theRow, theCol) && wasSideBottom(theRow, theCol))
			return new Match(theRow, theCol);
			
			else return new Match();
	}
	
	public Match wasCollisionBottom(int theRow, int theCol) {
		if(live(theRow, theCol) && wasSideTop(theRow, theCol))
			return new Match(theRow, theCol);
			
			else return new Match();
	}
	
	public boolean wasSideLeft(int theRow, int theCol) {
		
		float xPosition = character.getVxs() + character.getSx();
		
		boolean chPlace1 = character.contains(LeftTopPlace(theRow,theCol, -xPosition, 0));
		boolean chPlace2 = character.contains(LeftBottomPlace(theRow,theCol, -xPosition, 0));
		
		boolean coPlace1 = components[theRow][theCol].contains(RightTopPlace(character,xPosition, 0));
		boolean coPlace2 = components[theRow][theCol].contains(RightBottomPlace(character,xPosition, 0));
	
		return chPlace1 || chPlace2 || coPlace1 || coPlace2;
	}
	

	public boolean wasSideRight(int theRow, int theCol) {
		
		float xPosition = character.getVxs() + character.getSx();
		
		boolean place1 = character.contains(RightTopPlace(theRow,theCol, xPosition, 0));
		boolean place2 = character.contains(RightBottomPlace(theRow,theCol, xPosition, 0));
		
		boolean coPlace1 = components[theRow][theCol].contains(LeftTopPlace(character,-xPosition, 0));
		boolean coPlace2 = components[theRow][theCol].contains(LeftBottomPlace(character,-xPosition, 0));
	
		return place1 || place2 || coPlace1 || coPlace2;
	}
	
	public boolean wasSideTop(int theRow, int theCol) {
		
		float yPosition = character.getVys() + character.getSy();
		
		boolean chPlace1 = character.contains(LeftTopPlace(theRow,theCol, 0, -yPosition));
		boolean chPlace2 = character.contains(RightTopPlace(theRow,theCol, 0, -yPosition));
		
		boolean coPlace1 = components[theRow][theCol].contains(LeftBottomPlace(character,0, yPosition));
		boolean coPlace2 = components[theRow][theCol].contains(RightBottomPlace(character,0, yPosition));
	
		return chPlace1 || chPlace2 || coPlace1 || coPlace2;
	}
	
	public boolean wasSideBottom(int theRow, int theCol) {
		
		float yPosition = character.getVys() + character.getSy();
		
		boolean chPlace1 = character.contains(RightBottomPlace(theRow,theCol, 0, yPosition));
		boolean chPlace2 = character.contains(LeftBottomPlace(theRow,theCol, 0, yPosition));
		
		boolean coPlace1 = components[theRow][theCol].contains(RightTopPlace(character, 0, -yPosition));
		boolean coPlace2 = components[theRow][theCol].contains(LeftTopPlace(character,0, -yPosition));
	
		return chPlace1 || chPlace2 || coPlace1 || coPlace2;
	}
	
	
	public Point LeftTopPlace(int theRow, int theCol, float xPosition, float yPosition) {
		int x = (int) (components[theRow][theCol].x + xPosition);
		int y = (int) (components[theRow][theCol].y + yPosition);
		return new Point(x,y);
	}

	public Point LeftTopPlace(ImageMovement sprite, float xPosition, float yPosition) {
		
		int x = (int) (sprite.x + xPosition);
		int y = (int) (sprite.y + yPosition);
		return new Point(x,y);
	}
	
	public Point RightTopPlace(int theRow, int theCol, float xPosition, float yPosition) {
		Point place = new Point();
		place.x = (int) (components[theRow][theCol].x + components[theRow][theCol].width + xPosition);
		place.y = (int) (components[theRow][theCol].y + yPosition);
		
		return place;
	}

	public Point RightTopPlace(ImageMovement sprite, float xPosition, float yPosition) {
		
		Point place = new Point();
		place.x = (int) (sprite.x + sprite.width + xPosition);
		place.y = (int) (sprite.y + yPosition);
		
		return place;

	}
	
	public Point LeftBottomPlace(int theRow, int theCol, float xPosition, float yPosition) {
		
		Point place = new Point();
		place.x = (int) (components[theRow][theCol].x + xPosition);
		place.y = (int) (components[theRow][theCol].y + components[theRow][theCol].height + yPosition);
		
		return place;
	}

	public Point LeftBottomPlace(ImageMovement sprite, float xPosition, float yPosition) {
		Point place = new Point();
		place.x = (int) (sprite.x + xPosition);
		place.y = (int) (sprite.y + sprite.height + yPosition);
		
		return place;

	}
	
	public Point RightBottomPlace(int theRow, int theCol, float xPosition, float yPosition) {
			
			Point place = new Point();
			place.x = (int) (components[theRow][theCol].x + components[theRow][theCol].width + xPosition);
			place.y = (int) (components[theRow][theCol].y + components[theRow][theCol].height + yPosition);
			
			return place;
		}

	public Point RightBottomPlace(ImageMovement sprite, float xPosition, float yPosition) {
		Point place = new Point();
		place.x = (int) (sprite.x + sprite.height + xPosition);
		place.y = (int) (sprite.y + sprite.height + yPosition);
		
		return place;

	}

	//--------------------------------------------------------
	//--------------- for the File Parse 
	
	private ArrayList<String> getContentFile(String fileTitle) {
		ArrayList<String> context = new ArrayList<>();
		File file = new File(fileTitle);
		
		try (Scanner scanner = new Scanner(file)) {
			String line; 
			while (scanner.hasNext()) {
				
				line = scanner.nextLine();
				if(!line.startsWith("#")) {
					context.add(line);
				}
				
			}
		} catch (FileNotFoundException g) {
			System.out.println(g.getMessage());
		}
		return context; 
	}
	
	private int getLargestLine(ArrayList<String> context) {
		int largest = 0;
		int teeemp; 
		for(int i = 0; i < context.size(); i++) {
			teeemp = context.get(i).length();
			
			if(largest < teeemp) largest = teeemp;
		}
		return largest;
	}
	
	//-------------------------------- to load the methods 

	
	private void addComponents(Hashtable<Character, ImageMovement> assets) {

		int w = 0; 
		int h = 0; 
		
		String lineOfLetters;
		char items; 
		
		for (int theRow = 0; theRow < components.length; theRow++) {
			
			lineOfLetters = worldCode.get(theRow);
			for(int theCol = 0; theCol < lineOfLetters.length(); theCol++) {
				
				items = lineOfLetters.charAt(theCol);
				ImageMovement sprite = assets.get(items);
				
				if(sprite != null) {
					
					if(wasCharacter(items) ) {
						components[theRow][theCol] = sprite;
						character = sprite;
					}
					
					else if(wasComponent(items)) components[theRow][theCol] = sprite.getDuplicate();
					w += sprite.width;
					h += sprite.height;
				} else {
					 System.out.println("error " + items + "The HashTable does not exist");
				}
			}
		}
		
		componentsWidthAvg = w/components[0].length; 
		componentsHeightAvg = h/components[0].length; 
		
		worldHeight = componentsWidthAvg*components.length;
		worldWidth = componentsHeightAvg*components[0].length;
		
		addAreaToComponents(); 
		
	}
	
	private void addAreaToComponents() {
		for(int theRow = 0; theRow < components.length; theRow++ ) {
			for(int theCol = 0; theCol < components[theRow].length; theCol++) {
				
				if(!wasVacant(theRow,theCol )) {
					components[theRow][theCol].x = (int) getForX(theRow,theCol);
					
					components[theRow][theCol].y = (int) getForY(theRow,theCol);
					
					if(wasCharacter(theRow,theCol) ) {
						
						float characterWidth = components[theRow][theCol].width;
						components[theRow][theCol].boundaryRight = (Show.w/2-characterWidth);
						components[theRow][theCol].boundaryLeft = 0; 
						components[theRow][theCol].boundaryUp = 0; 
						components[theRow][theCol].boundaryDown = getForY(theRow,theCol);
					}
				}
			}
		}
	}
	//------------------------------ to implement the world methods 
	private int getBeginningTheCol() {
		return Math.max(0,Math.abs(position.y)/componentsHeightAvg);
	}
	
	private int getBeginningTheRow() {
		return Math.max(0,Math.abs(position.x)/componentsWidthAvg);
	}

	private int getFinishingTheRow() {
		return components.length;
	}
	
	private int getFinishingTheCol() {
		return components[0].length;
	}
	
	private int getTheRow(int i) {
		return (i*componentsHeightAvg+position.y);
	}
	
	private int getTheCol(int j) {
		return (j*componentsWidthAvg+position.x);
	}
	
	private boolean wasBeginOfMap() {
		return position.x + character.getXPosition() >= 0;
	}
	
	private boolean wasFinishedOfMap() {
		int worldWidth = components[0].length*componentsWidthAvg;
		int currentWidth = (int)(abs(position.x) + character.getXPosition() + Show.w);
		
		return currentWidth >= worldWidth;
	}
	private int getWorldWidth() {
		return components[0].length*componentsWidthAvg;
	}

	//---------------------------------- to implement character methods 
	
	private int getCharacterTheRow() {
		float hPosition = 0;
		if(character.height > componentsHeightAvg)
			hPosition = character.height-componentsHeightAvg;
		
		return Math.round((character.y+hPosition)/componentsHeightAvg);
	}
	
	private int getCharacterTheCol() {
		float wPosition = 0;
		if(character.width > componentsWidthAvg)
			wPosition = character.width-componentsWidthAvg;
		
		return Math.round((character.x+wPosition)/componentsWidthAvg);
	}
	
	private boolean wasCharacterCenterScreen() {
		return (character.x + character.getXPosition()) >= (Show.w/2 - character.width) &&
				(character.x + character.getXPosition()) <= Show.w/2;
	}
	
	
	private boolean wasCharacterFinishedScreen() {
		return (character.x + character.getXPosition()) >= (Show.w - character.width);
	}
	
	
	private boolean wasCharacterBeginScreen() {
		return (character.getX()-character.getXPosition()) < 0;
	}
	
	// to implement the helpers 
	
	private boolean wasName(char code) {
		return Character.isAlphabetic(code) && Character.isUpperCase(code);
	}
	
	private boolean wasName(int theRow, int theCol) {
		return wasName(components[theRow][theCol].getCharId());
	}


	private boolean wasCharacter(char code) {
		return Character.isDigit(code);
	}
	
	private boolean wasCharacter(int theRow, int theCol) {
		return wasCorrect(theRow,theCol) && !wasVacant(theRow,theCol) && wasCharacter(components[theRow][theCol].getCharId());
	}
	
	private boolean wasStrengthUp(char code) {
		return Character.isAlphabetic(code) && Character.isLowerCase(code);
	}
	
	private boolean wasStrengthUp(int theRow, int theCol) {
		return wasStrengthUp(components[theRow][theCol].getCharId());
	}
	
	private boolean wasAvatar(char code) {
		return !wasName(code) && !wasCharacter(code) && !wasStrengthUp(code) && !Character.isSpaceChar(code);
	}
	
	private boolean wasAvatar(int theRow, int theCol) {
		return wasAvatar(components[theRow][theCol].getCharId());
	}

	private boolean wasComponent(char code) {
				return wasName(code) || wasStrengthUp(code) || wasCharacter(code) || wasAvatar(code) ;
	}
	
	public int getWorldHeight() {
		return worldHeight;
	}

	public float getForY(int theRow, int theCol) {
		return (theRow * componentsHeightAvg - hPosition(theRow,theCol));
	}


	public  float getForX(int theRow, int theCol) {
		return (theCol * componentsWidthAvg-wPosition(theRow,theCol));
	}
	
	public float hPosition(int theRow, int theCol) {
		int hPosition = 0;
		if(components[theRow][theCol].height > componentsHeightAvg)
			hPosition = (int) components[theRow][theCol].height-componentsHeightAvg; 
		return hPosition; 
	}

	private float wPosition(int theRow, int theCol) {
		int wPosition = 0;
		if(components[theRow][theCol].height > componentsWidthAvg)
			wPosition = (int)components[theRow][theCol].width-componentsWidthAvg; 
		
		return wPosition;
	}
	
	private boolean wasCorrect(int theRow, int theCol) {
		
		return theRow >= 0 && theRow < components.length && theCol>= 0 && theCol < components[0].length;
	}
	
	private boolean wasVacant(int theRow, int theCol) {
		return components[theRow][theCol] == null;
	}
	
	public boolean live(int theRow, int theCol) {
		
		return wasCorrect(theRow, theCol) && !wasVacant(theRow, theCol);
	}
	
	public boolean worldPrimary() {
		for(int theRow = 0; theRow < components.length; theRow++) {
			for(int theCol = 0; theCol < components[0].length; theCol++) {
				
				if(!wasVacant(theRow,theCol))
					if(components[theRow][theCol].getX() < 0 || components[theRow][theCol].getY() <0)
						return false; 
			}
		}
		return true; 
	
	}
	
	private void printComponents() {
		for(int i = 0; i < components.length; i++ ) {
			for(int j = 0; j < components[i].length; j++) {
				if(components[i][j] != null) {
					if(components[i][j] == character)
				
					System.out.print("&");
				else
					System.out.print("*");
				
	
				} else {
					System.out.print(" ");
				} 
			}
			System.out.println();
		}
	}
}

class Match{
	int theRow; 
	int theCol;
		
	Match() {
		theRow = -1; 
		theCol = -1; 
	}
		
	Match(int R, int C) {
		theRow = R;
		theCol = C; 
	}
		
	public Match(Match random) {
		this.theRow = random.theRow;
		this.theCol = random.theCol;
	}
		
	public boolean wasVacant() {
		return theRow == -1 && theCol == -1; 
	}
		
	public String toTheString() {
		return "R: " + theRow + " C:" + theCol + "\n";
	}
}
