package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.Flags;
import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.InventoryItem;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Menus.AbstractMenu;
import org.spooner.java.TileGame.Menus.Position;
import org.spooner.java.TileGame.Menus.TextMenu;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.BoardChangeEvent;
import org.spooner.java.TileGame.Speech.Conversation;

public class OrbCutscene extends Cutscene{
	//members
	private SimpleProp orb;
	private int id;
	private BufferedImage background;
	private Actor emere;
	private CircularPath orbPath;
	private BoardChangeEvent backBoard;
	private TextMenu menu;
	private CutsceneTextMenu cutMenu;
	private Conversation[] conversations;
	private boolean showingWhite;
	private int circleCount;
	private int trans;
	private boolean showMenu;
	//constructors
	public OrbCutscene(int id, Board b){
		this.id = id;
		background = toImage(b);
		backBoard = b.toEvent();
		conversations = TileIO.parseConversations("con_ORBS.xml");
		emere = new Actor("Emere", b.getPlayerX(), b.getPlayerY(), Direction.DOWN);
		orb = new SimpleProp("orbs/" + id, emere.getX(), emere.getY() - 60);
		orbPath = new CircularPath(120, emere.getX(), emere.getY(), 90, false);
		orb.setDrawing(false);
		orb.setSX(0.01);
		orb.setSY(0.01);
		showingWhite = false;
		circleCount = 0;
		menu = new TextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT, true);
		menu.setYPosition(Position.BOTTOM);
		cutMenu = new CutsceneTextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT);
		cutMenu.setYPosition(Position.BOTTOM);
		TileAudio.playMusic("truth");
		trans = 0;
		showMenu = false;
	}
	//methods
	private BufferedImage toImage(Board b){
		BufferedImage bi = new BufferedImage(TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		b.draw(g, null);
		return bi;
	}
	@Override
	public void tick(InputTracker input){
		boolean done = true;
		//the list of actions
		switch(getCurrAction()){
			case 0: done = delay(100); break;
			case 1: showingWhite = true; trans = 60; break;
			case 2: done = delay(10); break;
			case 3: showingWhite = false; break;
			case 4: done = delay(100); break;
			case 5: showingWhite = true; trans = 110; break;
			case 6: done = delay(20); break;
			case 7: showingWhite = false; break;
			case 8: done = delay(10); break;
			case 9: showingWhite = true; trans = 150; break;
			case 10: done = delay(20); break;
			case 11: showingWhite = false; break;
			case 12: done = delay(100); break;
			case 13: orb.setDrawing(true); break;
			case 14: done = speedCircle(); break;
			case 15: done = delay(20); break;
			case 16: showingWhite = true; break;
			case 17: done = delay(20); break;
			case 18: orb.setDrawing(false); break;
			case 19: done = delay(18); break;
			case 20: orb.setDrawing(true); break;
			case 21: done = delay(16); break;
			case 22: orb.setDrawing(false); break;
			case 23: done = delay(14); break;
			case 24: orb.setDrawing(true); break;
			case 25: done = delay(12); break;
			case 26: orb.setDrawing(false); break;
			case 27: done = delay(10); break;
			case 28: orb.setDrawing(true); break;
			case 29: done = delay(8); break;
			case 30: orb.setDrawing(false); break;
			case 31: done = delay(6); break;
			case 32: orb.setDrawing(true); break;
			case 33: done = delay(4); break;
			case 34: orb.setDrawing(false); break;
			case 35: done = delay(70); break;
			case 36: showingWhite = false; break;
			case 37: done = delay(50); break;
			case 38: showMenu = true; menu.setText("You have been blessed with the power of the " + InventoryItem.getOrbs()[id].getName() + " Orb!"); break;
			case 39: done = !menu.tick(input, null); break;
			case 40: showMenu = false; break;
			//open the conversations in order based on how many orbs have
			case 41: cutMenu.open(conversations[Flags.getFlags().ownedOrbs().length - 1]); break;
			case 42: done = delay(40); break;
			case 43: cutMenu.begin(); break;
			case 44: done = !cutMenu.tick(input, null); break;
			case 45: cutMenu.end(); break;
			case 46: trans--; done = trans == 0; break;
			case 47: done = delay(40); break;
			default: finish(); break;
		}
		if(done)
			nextAction();
	}
	@Override
	public void draw(Graphics g, InputTracker input){
		g.drawImage(background, 0, 0, null);
		g.setColor(new Color(0, 0, 0, trans));
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
		if(showingWhite){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
		}
		emere.draw(g);
		orb.draw(g);
		if(showMenu)
			menu.draw(g);
		cutMenu.draw(g);
	}
	private boolean speedCircle(){
		if(orb.getSX() != 1.0 || orb.getSY() != 1.0){
			orb.scaleTo(1.0, 1.0, 0.004);
		}
		int cycles = circleCount < 1 ? 3 : (circleCount < 3 ? 5 : (circleCount < 6 ? 6 : 7));
		boolean doneCycle = false;
		for(int i = 0; i < cycles && !doneCycle; i++){
			doneCycle = orb.followPath(orbPath);
		}
		if(doneCycle){
			circleCount++;
			orbPath = new CircularPath(circleCount < 3 ? 90 : (circleCount < 6 ? 65 : 30), emere.getX(), emere.getY(), 90, false);
		}
		else{
			double ratio = (double) orbPath.index() / orbPath.points();
			if(ratio > .88 || ratio < .12) emere.turn(Direction.UP);
			else if(ratio <= .88 && ratio > .63) emere.turn(Direction.LEFT);
			else if(ratio <= .63 && ratio > .36) emere.turn(Direction.DOWN);
			else emere.turn(Direction.RIGHT);
		}
		return circleCount == 13;
	}
	public BoardChangeEvent getBackBoard(){ return backBoard; }
	@Override
	public GameState endState() {
		return GameState.BOARD;
	}
}
