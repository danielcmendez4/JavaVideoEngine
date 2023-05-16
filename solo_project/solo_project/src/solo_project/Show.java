package solo_project;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public abstract class Show extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static volatile boolean runnable;
	private static Thread videoThread; 
	private static BufferStrategy buffer; 
	
	private static GraphicsDevice gDevice; 
	protected static DisplayMode  showMode; 
	
	public static int w;
	public static int h;
	
	protected String name; 
	protected Color surround; 
	public Canvas can; 
	
	public static InputKeyboard key; 
	
	public void begin() {
		videoThread = new Thread(this);
		videoThread.start();
	}
	
	public void run() {
		
		runnable = true; 
		
		int fps = 60; 
		double eventPerTick = 1.0E9/fps;
		double delta = 0; 
		
		long currentEvent = System.nanoTime();
		long lastEvent = currentEvent; 
		double nanosPerFrame; 
		
		long event = 0;
		long ticker = 0;
		
		while(runnable) {
			currentEvent = System.nanoTime();
			nanosPerFrame = currentEvent-lastEvent;
			
			delta += nanosPerFrame/eventPerTick;
			event += nanosPerFrame;
			lastEvent = currentEvent; 
			
			if(delta >= 1) {
				actionInput();
				upgradeObject();
				provideFrame();
				ticker++;
				delta--;
			}
			
			if(event >= 1.0E9 ) {
				System.out.println("Thicker: " + ticker);
				ticker = 0; 
				event = 0; 
			}
		}
	}
	
	
	protected abstract void actionInput(); 
	
	protected abstract void upgradeObject();
	
	protected void provideFrame() {
		do {
			do  {
				Graphics e = null; 
				try {
					e = buffer.getDrawGraphics();
					e.clearRect(0, 0, getWidth(), getHeight()); // ???
					sketch(e);
				} finally {
					if (e != null) {
						e.dispose();
					}
				}
			} while (buffer.contentsRestored());
			buffer.show();
		} while (buffer.contentsLost());
	}

	protected abstract void sketch(Graphics e);
	
	protected void makeSizeWindow() {
		
		can = new Canvas();
		can.setSize(w, h);
		can.setBackground(surround);
		can.setIgnoreRepaint(true);
		
		key = new InputKeyboard();
		can.addKeyListener((KeyListener) key);
		
		getContentPane().add(can);
		setTitle(name);
		setIgnoreRepaint(true);
		
		pack();
		
		setLocationRelativeTo(null);
		setVisible(true);
		
		can.createBufferStrategy(2);
		buffer = can.getBufferStrategy();
		can.requestFocus();
	}
	
	protected void makeCompleteWindow() {
		uploadCompleteScreen(); 
		
		key = new InputKeyboard(); // InputKeyboard.Java 
		addKeyListener((KeyListener) key); 
		
		onQuit(); 
	}

	

	private void uploadCompleteScreen() {
		setIgnoreRepaint(true);
        setUndecorated(true);
        setBackground(surround); 
        
        GraphicsEnvironment environment = 
        		GraphicsEnvironment.getLocalGraphicsEnvironment();
        
        gDevice = environment.getDefaultScreenDevice();
        showMode = getAccessibleShow()[0]; 
        
        if (!gDevice.isFullScreenSupported() ) {
        	System.err.println("ERROR: INVALID OR CORRUPT FULL SCREEN!!!!!");
        	System.exit(0);
        }
        
        gDevice.setFullScreenWindow(this);
        gDevice.setDisplayMode(showMode);
	}
	
	private void onQuit() {
		addKeyListener(new KeyAdapter() {
			
			@SuppressWarnings("unused")
			public void boardPressed(KeyEvent g) {
				if ( g.getKeyCode() == KeyEvent.VK_ESCAPE ) {
					turnOff();
				}
			}
		});
		
	}
	
	protected void turnOff() {
		try {
			runnable = false; 
			videoThread.join();
			
			gDevice.setDisplayMode(getNowDisplayMode() );
			gDevice.setFullScreenWindow(null);
		} catch ( InterruptedException g ) {
			System.out.println(g.getMessage()); 
		}
		System.exit(0);
	}
	
	

	protected void quitSizedScreen() {
		try {
			runnable = false;
			videoThread.join(); 
		} catch( InterruptedException g) {
			System.out.println(g.getMessage());
		}
		System.exit( 0 );
	}
	
	public DisplayMode getNowDisplayMode() {
		GraphicsEnvironment environment = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		return environment.getDefaultScreenDevice().getDisplayMode();
	}

	public DisplayMode[] getAccessibleShow() {
		return gDevice.getDisplayModes();
	}
	
	public void setShowMode(DisplayMode mode) {
		showMode = mode; 
	}

}
