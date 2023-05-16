package solo_project;

import java.awt.Graphics;
import java.util.Hashtable;

public class VideoState extends TheState {

	private ImageMovement character; 
	private MakingWorld map; 
	private String mapFile = "/res/worlds/world";
	
	public VideoState() {
		makeCharacter(); 
		
		try {
			System.out.println("Character h " + character.height);
			System.out.println("Character w " + character.width);
			Hashtable<Character, ImageMovement> assets = new Hashtable<>();
			getBlocks(assets);
			assets.put('0', character);
			
			map = new MakingWorld(mapFile, assets);
		} catch (Exception g) {
			g.printStackTrace();
		}
	}
	
	@Override
	public void actionInput() {
		map.actionInput();

	}
	
	@Override
	public void upgrade() {
		map.upgrade();
	}

	public void sketch(Graphics e) {
		map.sketch(e); 
	}
	
	private void makeCharacter() {
		String pathFile[] = {"/res/character/idle/Idle", "/res/character/run/Run"};
		int period[] = {3,3};
		character = new ImageMovement(pathFile, period,'0');
		character.setScale(0.1f, 0.2f);
	}
	
	
	private void getBlocks(Hashtable<Character,ImageMovement> world) {
		
		for(char i = 'A'; i<='O'; i++) {
			ImageMovement sprite = new ImageMovement("res/blocktiles/Blocktiles" + i,i);
			world.put(i, sprite);
		}
		
	}

}
