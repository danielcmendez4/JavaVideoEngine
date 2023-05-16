package solo_project;

import java.awt.Graphics;

public abstract class TheState {
	
	private static TheState nowState = null;
	
	public static void setTheState(TheState st) {
		nowState = st;
	}
	
	public static TheState getTheState() {
		return nowState; 
		
	}
	
	public abstract void upgrade();
	
	public abstract void actionInput(); 
	
	public abstract void sketch(Graphics e); 
}
