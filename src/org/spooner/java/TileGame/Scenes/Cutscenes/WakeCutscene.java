package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Color;
import java.awt.Graphics;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Menus.AbstractMenu;
import org.spooner.java.TileGame.Menus.Position;
import org.spooner.java.TileGame.Speech.Conversation;

public class WakeCutscene extends Cutscene{
	//members
	private int hBlack;
	private SimpleProp room;
	private CutsceneTextMenu menu;
	private Conversation[] conversations;
	private Actor emere;
	private SimpleProp covers;
	//constructors
	public WakeCutscene(){
		hBlack = TileConstants.FRAME_HEIGHT;
		room = new SimpleProp("room", 0, 0);
		menu = new CutsceneTextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT);
		menu.setYPosition(Position.BOTTOM);
		conversations = TileIO.parseConversations("con_INTRO.xml");
		emere = new Actor("Emere", 120, 450, Direction.DOWN);
		covers = new SimpleProp("cover", 120, 500);
	}
	//methods
	@Override
	public void draw(Graphics g, InputTracker input) {
		room.draw(g);
		emere.draw(g);
		covers.draw(g);
		//black background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, hBlack);
		menu.draw(g);
	}

	@Override
	public void tick(InputTracker input) {
		boolean done = true;
		switch(getCurrAction()){
			case 0: done = delay(100); break;
			case 1: menu.open(conversations[3]); break;
			case 2: done = delay(50); break;
			case 3: menu.begin(); break;
			case 4: done = !menu.tick(input, null); break;
			case 5: menu.end(); break;
			case 6: done = delay(200); break;
			case 7: hBlack -= 2; done = hBlack == 0; break;
			case 8: done = delay(100); break;
			case 9: done = emere.turn(Direction.RIGHT); break;
			case 10: done = delay(100); break;
			case 11: menu.open(conversations[4]); break;
			case 12: done = delay(50); break;
			case 13: menu.begin(); break;
			case 14: done = !menu.tick(input, null); break;
			case 15: menu.end(); break;
			case 16: done = delay(150); break;
			default: finish(); break;
		}
		if(done)
			nextAction();
	}

}
