package solo_project;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

public class VideoGame extends Show {
	
	
	private static final long serialVersionUID = 1L;
	public static TheState videoState; 
		
		
		public void begin() {
			w = 270;
			h = 122; 
			
			videoState = new VideoState(); 
			
			TheState.setTheState(videoState);
			
			makeSizeWindow();
			can.setBackground(Color.black);
			super.begin();
		}


		@Override
		protected void actionInput() {
			key.pull();
			if(TheState.getTheState() != null) {
				TheState.getTheState().actionInput();
			}
			
		}


		@Override
		protected void upgradeObject() {
			key.pull();
			if(TheState.getTheState() != null) {
				TheState.getTheState().upgrade();
			}
			
		}


		@Override
		protected void sketch(Graphics e) {
			if(TheState.getTheState() != null) {
				TheState.getTheState().sketch(e);
			}
		}
		
		public static void main(String[]args ) {
			final VideoGame videoGame = new VideoGame();
			
			SwingUtilities.invokeLater(() -> videoGame.begin());
			
			videoGame.addWindowListener(new WindowAdapter() {
				@SuppressWarnings("unused")
				public void closingWindow(WindowEvent g) {
					videoGame.quitSizedScreen();
				}
			});
		}	
}