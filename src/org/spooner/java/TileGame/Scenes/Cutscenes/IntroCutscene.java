package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Menus.AbstractMenu;
import org.spooner.java.TileGame.Menus.Position;
import org.spooner.java.TileGame.Speech.Conversation;

public class IntroCutscene extends Cutscene{
	//members
	private static final int ORB_DELAY = 171;
	private Actor ebisu;
	private Actor emere;
	private SimpleProp[] orbs;
	private SimpleProp mour;
	private Conversation[] conversations;
	private CutsceneTextMenu menu;
	private int orbTimer;
	private int skyTimer;
	private CircularPath[] orbCircles;
	private Path skyPath;
	private Path housePath;
	private Path[] orbEnds;
	private boolean[] flingingOff;
	//constructors
	public IntroCutscene(){
		ebisu = new Actor("Ebisu", TileConstants.FRAME_CENTER.x, TileConstants.FRAME_CENTER.y - 70, Direction.DOWN);
		ebisu.setY(ebisu.getY() - ebisu.getHeight());
		ebisu.setSX(0.01);
		ebisu.setSY(0.01);
		emere = new Actor("Emere", TileConstants.FRAME_WIDTH / 2, TileConstants.FRAME_HEIGHT + ebisu.getHeight(), Direction.UP);
		orbs = new SimpleProp[]{ new SimpleProp("orbs/3", 0, 300), new SimpleProp("orbs/2", 0, 300), 
				new SimpleProp("orbs/1", 0, 300), new SimpleProp("orbs/4", 0, 300), new SimpleProp("orbs/0", 0, 300) };
		mour = new SimpleProp("mourilyan", 0, 0);
//		mour.setDrawing(false);
		mour.setSX(0.001);
		mour.setSY(0.001);
		for(SimpleProp sp : orbs){
			sp.setSX(1.25);
			sp.setSY(1.25);
			sp.setDrawing(false);
			sp.setX(ebisu.getX());
			sp.setY(ebisu.getY() - sp.getHeight() - 50);
		}
		orbCircles = new CircularPath[5];
		for(int i = 0; i < orbCircles.length; i++){
			orbCircles[i] = new CircularPath(155, TileConstants.FRAME_CENTER.x, ebisu.getY() + ebisu.getHeight(), 90, true);
		}
		orbEnds = new Path[5];
		
		for(int i = 0; i < orbCircles.length; i++){
			orbEnds[i] = new Path(new Point[]{ new Point(TileConstants.FRAME_CENTER.x, -200) }, false);
		}
		conversations = TileIO.parseConversations("con_INTRO.xml");
		menu = new CutsceneTextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT);
		menu.setYPosition(Position.BOTTOM);
		skyPath = new Path(new Point[]{ new Point(-300, -1400), new Point(-1100, -1500), new Point(-1200, -2200), new Point(-1800, -3500),
				new Point(-700, -3000), new Point(-500, -2800) }, true);
		orbTimer = 0;
		skyTimer = 0;
		flingingOff = new boolean[orbs.length];
		housePath = new Path(new Point[]{ new Point(-700, -1900) }, false);
	}
	//methods
	@Override
	public void draw(Graphics g, InputTracker input) {
		//black background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
		mour.draw(g);
		//translate
		translate(g);
		ebisu.draw(g);
		emere.draw(g);
		for(SimpleProp sp : orbs)
			sp.draw(g);
		//translate back
		translateBack(g);
		menu.draw(g);
	}
	@Override
	public void tick(InputTracker input){
		boolean done = true;
		//the list of actions
		switch(getCurrAction()){
			case 0: done = delay(200); break;
			case 1: done = emere.moveTo(TileConstants.FRAME_CENTER.x, TileConstants.FRAME_CENTER.y + 70, Direction.UP); break;
			case 2: done = delay(100); break;
			case 3: done = emere.turn(Direction.LEFT); break;
			case 4: done = delay(50); break;
			case 5: done = emere.turn(Direction.RIGHT); break;
			case 6: done = delay(50); break;
			case 7: done = emere.turn(Direction.UP); break;
			case 8: done = delay(100); break;
			case 9: done = emere.turn(Direction.RIGHT); break;
			case 10: done = delay(100); break;
			case 11: done = emere.turn(Direction.UP); break;
			case 12: done = ebisu.scaleTo(1.0, 1.0, 0.002); break;
			case 13: done = delay(100); break;
			case 14: menu.open(conversations[0]); break;
			case 15: done = delay(100); break;
			case 16: menu.begin(); break;
			case 17: done = !menu.tick(input, null); break;
			case 18: menu.end(); break;
			case 19: done = delay(100); break;
			case 20: done = panTo(0, -80); break;
			case 21: done = delay(100); break;
			case 22: for(SimpleProp sp : orbs) sp.setDrawing(true); break;
			case 23: done = delay(100); break;
			case 24: done = orbCircle(input); break;
			case 25: mour.setDrawing(true); break;
			case 26: orbCircle(input); done = skyTour(input); break;
			case 27: done = zoomHouse(input); break;
			default: finish(); break;
		}
		//move to next action if done
		if(done)
			nextAction();
	}
	private boolean orbCircle(InputTracker input){
		boolean done = false;
		orbs[0].followPath(orbCircles[0]);
		if(orbTimer > ORB_DELAY){
			orbs[1].followPath(orbCircles[1]);
		}
		if(orbTimer > 2 * ORB_DELAY){
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
			menu.open(conversations[1]);
		}
		if(orbTimer > 3 * ORB_DELAY){
			orbs[3].followPath(orbCircles[3]);
		}
		if(orbTimer > 4 * ORB_DELAY){
			orbs[4].followPath(orbCircles[4]);
		}
		orbTimer++;
		return done;
	}
	private boolean skyTour(InputTracker input){
		boolean done = false;
		if(skyTimer > 205){
			menu.end();
			done = true;
		}
		else if(skyTimer > 201){
			//stall timer if menu still open
			if(menu.tick(input, null)){
				skyTimer--;
			}
		}
		else if(skyTimer > 200){
			menu.begin();
		}
		else if(skyTimer > 100){
			menu.open(conversations[2]);
		}
		if(skyTimer > 200){
			if(mour.getSX() != 1.0 || mour.getSY() != 1.0){
				mour.scaleTo(1.0, 1.0, 0.002);
			}
			else{
				mour.followPath(skyPath);
				mour.followPath(skyPath);
			}
		}
		skyTimer++;
		return done;
	}
	private boolean zoomHouse(InputTracker input){
		boolean orbsDone = true;
		for(int i = 0; i < orbs.length; i++){
			if(orbs[i].getX() == orbCircles[i].get(0).x && orbs[i].getY() == orbCircles[i].get(0).y){
				flingingOff[i] = true;
			}
			boolean orbDone;
			if(flingingOff[i]){
				orbs[i].followPath(orbEnds[i]);
				orbDone = orbs[i].followPath(orbEnds[i]);
			}
			else{
				orbDone = orbs[i].followPath(orbCircles[i]);
			}
			//so far done, but this orb is continuing
			if(orbsDone && !orbDone)
				orbsDone = false;
		}
		return mour.followPath(housePath) && orbsDone;
	}
}
