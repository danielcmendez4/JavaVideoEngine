package solo_project;

import java.awt.event.KeyEvent;

public class InputKeyboard {

	private boolean[] board;
	private int[] pulled; 
	
	public InputKeyboard() {
		board = new boolean[256];
		pulled = new int[256];
	}
	
	public boolean boardDown(int boardCode) {
		return pulled[boardCode] > 0;
	}
	
	public boolean boardDownOnce(int boardCode) {
		return pulled[boardCode] == 1;
	}
	
	public synchronized void pull() {
		for (int i = 0; i < board.length; ++i) {
			if(board[i]) 
				pulled[i]++;
			else
				pulled[i] = 0; 
		}
	}
	
	public synchronized void boardPressed(KeyEvent g) {
		int boardCode = g.getKeyCode();
		if (boardCode >= 0 && boardCode < board.length)
			board[boardCode] = true;
		
	}
	
	public synchronized void boardReleased(KeyEvent g) {
		int boardCode = g.getKeyCode();
		if (boardCode >= 0 && boardCode < board.length)
			board[boardCode] = false;
		
	}
	
	public void boardTyped(KeyEvent argument) {
		
	}
}