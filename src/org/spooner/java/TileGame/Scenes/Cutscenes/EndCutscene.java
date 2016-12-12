package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.Flags;
import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileGame;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Menus.AbstractMenu;
import org.spooner.java.TileGame.Menus.Position;
import org.spooner.java.TileGame.Speech.Conversation;

public class EndCutscene extends Cutscene{
	private static final int ORB_DELAY = 201;
	private static final int FRAG_DELAY = 231;
	//members
	private boolean isGood;
	private SimpleProp room;
	private CutsceneTextMenu menu;
	private Conversation[] conversations;
	private boolean showingWhite;
	private Actor emere;
	private Actor ebisu;
	private SimpleProp[] orbs;
	private int orbTimer;
	private CircularPath[] orbCircles;
	private SimpleProp[] frags;
	private int fragTimer;
	private CircularPath[] fragCircles;
	private Path[] orbPaths;
	private Path[] fragPaths;
	private int wHeight;
	private boolean text;
	//constructors
	public EndCutscene(){
		Flags f = Flags.getFlags();
		//good if has all the fragments
		isGood = f.hasFlag("item", 30) && f.hasFlag("item", 31) && f.hasFlag("item", 32);
		showingWhite = false;
		wHeight = TileConstants.FRAME_HEIGHT;
		emere = new Actor("Emere", 200, 570, Direction.LEFT);
		ebisu = new Actor("Ebisu", 120, 570, Direction.RIGHT);
		orbs = new SimpleProp[]{ new SimpleProp("orbs/3", emere.getX(), emere.getY()), new SimpleProp("orbs/2", emere.getX(), emere.getY()), 
				new SimpleProp("orbs/1", emere.getX(), emere.getY()), new SimpleProp("orbs/4", emere.getX(), emere.getY()), new SimpleProp("orbs/0", emere.getX(), emere.getY()) };
		for(SimpleProp o : orbs)
			o.setDrawing(false);
		orbCircles = new CircularPath[5];
		for(int i = 0; i < orbCircles.length; i++){
			orbCircles[i] = new CircularPath(350, TileConstants.FRAME_CENTER.x - 125, TileConstants.FRAME_CENTER.y, 135, true);
		}
		frags = new SimpleProp[]{ new SimpleProp("alpha", 0, 0), new SimpleProp("beta", 0, 0), new SimpleProp("gamma", 0, 0) };
		for(SimpleProp r : frags)
			r.setDrawing(false);
		fragCircles = new CircularPath[3];
		for(int i = 0; i < fragCircles.length; i++){
			fragCircles[i] = new CircularPath(125, TileConstants.FRAME_CENTER.x + emere.getWidth(), TileConstants.FRAME_CENTER.y, 135, true);
		}
		fragTimer = 0;
		orbPaths = new Path[5];
		for(int i = 0; i < orbPaths.length; i++){
			if(isGood){
				orbPaths[i] = new Path(new Point[]{ new Point(594, 414) }, false);
			}
			else{
				orbPaths[i] = new Path(new Point[]{ offScreen() }, false);
			}
		}
		fragPaths = new Path[3];
		for(int i = 0; i < fragPaths.length; i++){
			if(isGood){
				fragPaths[i] = new Path(new Point[]{ new Point(594, 414) }, false);
			}
			else{
				fragPaths[i] = new Path(new Point[]{ offScreen() }, false);
			}
		}
		room = new SimpleProp("room", 0, 0);
		menu = new CutsceneTextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT);
		menu.setYPosition(Position.BOTTOM);
		conversations = TileIO.parseConversations("con_END.xml");
		orbTimer = 0;
		text = false;
		TileAudio.playMusic("truth");
	}
	//methods
	private Point offScreen(){
		int x = TileGame.getRandomBoolean() ? TileGame.getRandomInt(80) + TileConstants.FRAME_WIDTH + 10 : - TileGame.getRandomInt(80) - 100;
		int y = TileGame.getRandomBoolean() ? TileGame.getRandomInt(80) + TileConstants.FRAME_HEIGHT + 10 : - TileGame.getRandomInt(80) - 100;
		return new Point(x, y);
	}
	@Override
	public void draw(Graphics g, InputTracker input) {
		//black background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
		if(text){
			g.setFont(TileConstants.MENU_FONT.deriveFont(67.0f));
			g.setColor(Color.WHITE);
			g.drawString("The End", 400, 310);
			g.drawString("Thanks for playing!", 300, 420);
		}
		if(showingWhite){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, TileConstants.FRAME_WIDTH, wHeight);
		}
		translate(g);
		room.draw(g);
		emere.draw(g);
		ebisu.draw(g);
		for(SimpleProp o : orbs)
			o.draw(g);
		for(SimpleProp f : frags)
			f.draw(g);
		translateBack(g);
		menu.draw(g);
	}
	@Override
	public void tick(InputTracker input) {
		boolean done = true;
		//the list of actions
		switch(getCurrAction()){
		case 0: done = delay(60); break;
		//move at x2 speed to center
		case 1: done = emere.moveTo(TileConstants.FRAME_CENTER.x + 50, TileConstants.FRAME_CENTER.y, Direction.LEFT);
				done &= ebisu.moveTo(TileConstants.FRAME_CENTER.x - 50, TileConstants.FRAME_CENTER.y, Direction.RIGHT);
				done &= emere.moveTo(TileConstants.FRAME_CENTER.x + 50, TileConstants.FRAME_CENTER.y, Direction.LEFT);
				done &= ebisu.moveTo(TileConstants.FRAME_CENTER.x - 50, TileConstants.FRAME_CENTER.y, Direction.RIGHT);
				break;
		case 2: done = delay(60); break;
		case 3: done = room.scaleTo(0.001, 0.001, 1.5);
				done &= room.scaleTo(0.001, 0.001, 1.5);
				done &= room.moveTo(TileConstants.FRAME_CENTER.x, TileConstants.FRAME_CENTER.y);
				done &= room.moveTo(TileConstants.FRAME_CENTER.x, TileConstants.FRAME_CENTER.y);
				break;
		case 4: emere.turn(Direction.DOWN); room.setDrawing(false); break;
		case 5: done = ebisu.moveTo(200, ebisu.getY(), Direction.RIGHT); break;
		case 6: emere.turn(Direction.LEFT); break;
		case 7: done = panTo(-150, 0); break;
		case 8: menu.open(conversations[0]); break;
		case 9: done = delay(60); break;
		case 10: menu.begin(); break;
		case 11: done = !menu.tick(input, null); break;
		case 12: menu.end(); break;
		case 13: done = delay(30); break;
		case 14: showingWhite = true; break;
		case 15: done = delay(30); break;
		case 16:
			for(SimpleProp o : orbs){
				o.setDrawing(true);
				o.setX(emere.getX());
				o.setY(emere.getY() + 32);
			}
			for(int i = 0; i < frags.length; i++){
				SimpleProp f = frags[i];
				if(Flags.getFlags().hasFlag("item", 30 + i))
					f.setDrawing(true);
				f.setX(emere.getX());
				f.setY(emere.getY() + 32);
			}
			break;
		case 17: showingWhite = false; break;
		case 18: done = delay(30); break;
		case 19: done = orbCircle(input); done &= orbCircle(input); fragCircle(input); break;
		case 20: done = spread(); break;
		case 21: emere.turn(Direction.UP); break;
		case 22: emere.turn(Direction.DOWN); break;
		case 23: emere.turn(Direction.LEFT); break;
		case 24: emere.turn(Direction.DOWN); break;
		case 25: emere.turn(Direction.UP); break;
		case 26: emere.turn(Direction.RIGHT); break;
		case 27: emere.turn(Direction.LEFT); break;
		case 28: done = delay(20); break;
		case 29: done = emere.scaleTo(0.2, 0.2, 0.1); break;
		case 30: done = ebisu.scaleTo(0.2, 0.2, 0.1); break;
		case 31: showingWhite = true; break;
		case 32: done = delay(80); break;
		case 33: showingWhite = false; break;
		case 34: done = delay(30); break;
		case 35: showingWhite = true; break;
		case 36: done = delay(20); break;
		case 37: showingWhite = false; break;
		case 38: emere.setDrawing(false); break;
		case 39: showingWhite = true; break;
		case 40: done = delay(20); break;
		case 41: showingWhite = false; break;
		case 42: done = delay(20); break;
		case 43: showingWhite = true; break;
		case 44: done = delay(15); break;
		case 45: ebisu.setDrawing(false); break;
		case 46: showingWhite = false; break;
		case 47: showingWhite = true; break;
		case 48: TileAudio.playMusic("sunlight");
		case 49: text = true; break;
		case 50: done = wHeight <= 0; wHeight -= 3;  if(wHeight < 0) wHeight = 0; break;
		case 51: done = delay(400); break;
		default: finish(); break;
		}
		//move to next action if done
		if(done)
			nextAction();
	}
	private boolean spread(){
		boolean allThere = true;
		for(int i = 0; i < orbPaths.length; i++){
			if(allThere){
				allThere = orbs[i].followPath(orbPaths[i]);
				allThere &= orbs[i].followPath(orbPaths[i]);
				allThere &= orbs[i].followPath(orbPaths[i]);
			}
			else{
				orbs[i].followPath(orbPaths[i]);
				orbs[i].followPath(orbPaths[i]);
				orbs[i].followPath(orbPaths[i]);
			}
			if(orbs[i].getX() == orbPaths[i].get(0).x && orbs[i].getY() == orbPaths[i].get(0).y){
				if(orbs[i].scaleTo(0.01, 0.01, 2))
					orbs[i].setDrawing(false);
			}
		}
		for(int i = 0; i < fragPaths.length; i++){
			if(allThere){
				allThere = frags[i].followPath(fragPaths[i]);
				allThere &= frags[i].followPath(fragPaths[i]);
				allThere &= frags[i].followPath(fragPaths[i]);
			}
			else{
				frags[i].followPath(fragPaths[i]);
				frags[i].followPath(fragPaths[i]);
				frags[i].followPath(fragPaths[i]);
			}
			if(frags[i].getX() == fragPaths[i].get(0).x && frags[i].getY() == fragPaths[i].get(0).y){
				if(frags[i].scaleTo(0.01, 0.01, 2))
					frags[i].setDrawing(false);
			}
		}
		boolean gone = true;
		for(SimpleProp o : orbs)
			if(gone)
				gone = !o.isDrawing();
		for(SimpleProp s : frags)
			if(gone)
				gone = !s.isDrawing();
		return allThere && gone;
	}
	private void fragCircle(InputTracker input){
		frags[0].followPath(fragCircles[0]);
		if(fragTimer > FRAG_DELAY){
			frags[1].followPath(fragCircles[1]);
		}
		if(fragTimer > 2 * FRAG_DELAY){
			frags[2].followPath(fragCircles[2]);
		}
		fragTimer++;
	}
	private boolean orbCircle(InputTracker input){
		boolean done = false;
		orbs[0].followPath(orbCircles[0]);
		orbs[0].followPath(orbCircles[0]);
		if(orbTimer > ORB_DELAY){
			orbs[1].followPath(orbCircles[1]);
			orbs[1].followPath(orbCircles[1]);
		}
		if(orbTimer > 2 * ORB_DELAY){
			orbs[2].followPath(orbCircles[2]);
			orbs[2].followPath(orbCircles[2]);
		}
		//menu commands
		if(orbTimer > 4 * ORB_DELAY + 3){
			done = true;
		}
		else if(orbTimer > 4 * ORB_DELAY + 2){
			menu.end();
		}
		else if(orbTimer > 4 * ORB_DELAY + 1){
			//stall timer if menu still open
			if(menu.tick(input, null)){
				orbTimer--;
			}
		}
		else if(orbTimer > 4 * ORB_DELAY){
			menu.begin();
		}
		else if(orbTimer > 3 * ORB_DELAY){
			menu.open(conversations[isGood ? 2 : 1]);
		}
		if(orbTimer > 3 * ORB_DELAY){
			orbs[3].followPath(orbCircles[3]);
			orbs[3].followPath(orbCircles[3]);
		}
		if(orbTimer > 4 * ORB_DELAY){
			orbs[4].followPath(orbCircles[4]);
			orbs[4].followPath(orbCircles[4]);
		}
		orbTimer++;
		return done;
	}
	@Override
	public GameState endState(){
		return GameState.TITLE;
	}
}
