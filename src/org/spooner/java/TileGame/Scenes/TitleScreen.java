package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Flags;
import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Transition;
import org.spooner.java.TileGame.Menus.AbstractMenu;
import org.spooner.java.TileGame.Menus.ChoiceMenu;
import org.spooner.java.TileGame.Menus.Position;
import org.spooner.java.TileGame.Menus.TextMenu;

public class TitleScreen implements DrawableScene{
	//members
	private String[] backgroundPaths;
	private BufferedImage title;
	private BufferedImage background;
	private int currentBackground;
	private int panX;
	private int panY;
	private int endX;
	private int endY;
	private boolean selectionMade;
	private ChoiceMenu menu;
	private Transition backTrans;
	private TextMenu warningMenu;
	private boolean warning;
	//constructors
	public TitleScreen(){
		backgroundPaths = TileIO.listFiles(TileConstants.BACKGROUNDS_PATH);
		currentBackground = 0;
		panX = 0;
		panY = 0;
		endX = 0;
		endY = 0;
		selectionMade = false;
		title = TileIO.loadImage(TileConstants.TITLE_FILE);
		nextBackground();
		menu = new ChoiceMenu(TileConstants.MAIN_MENU_OPTIONS, TileConstants.MAIN_MENU_WIDTH, TileConstants.MAIN_MENU_HEIGHT, false, false);
		menu.setXPosition(Position.MIDDLE);
		menu.setYPosition(Position.MIDDLE);
		//start black
		backTrans = new Transition(TileConstants.FADE_SPEED, 1f);
		warningMenu = new TextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT, true);
		warningMenu.setText(TileConstants.SAVE_CONFLICT_TEXT);
		warning = false;
		//play music if not already playing
		if(!"title.wav".equals(TileAudio.getPlayingMusic()))
			TileAudio.playMusic("title");
	}
	//methods
	private int backgroundWidth(){
		return (int) (TileConstants.BACKGROUND_SCALE * background.getWidth());
	}
	private int backgroundHeight(){
		return (int) (TileConstants.BACKGROUND_SCALE * background.getHeight());
	}
	private void nextBackground(){
		background = TileIO.loadImage(TileConstants.BACKGROUNDS_PATH + backgroundPaths[currentBackground]);
		//where the background ends (the acutal picture size - frame dimension)
		endX = backgroundWidth() - TileConstants.FRAME_WIDTH;
		endY = backgroundHeight() - TileConstants.FRAME_HEIGHT;
	}
	private void drawTitle(Graphics g){
		//center it
		int x = TileConstants.FRAME_WIDTH / 2 - title.getWidth() / 2;
		g.drawImage(title, x, 40, null);
	}
	@Override
	public void draw(Graphics g, InputTracker input) {
		//draw scrolling image
		g.drawImage(background, - panX, - panY, backgroundWidth(), backgroundHeight(), null);
		//draw transition
		backTrans.draw(g);
		//draw title
		drawTitle(g);
		//draw menu
		menu.draw(g);
		if(warning){
			warningMenu.draw(g);
		}
	}
	@Override
	public void tick(InputTracker input) {
		//move the pan [speed] pixels based on scale
		panX += TileConstants.BACKGROUND_SCALE / 2;
		//move the pan [speed] pixels based on scale
		panY += TileConstants.BACKGROUND_SCALE / 2;
		//if overshot, compensate
		if(panX > endX) panX = endX;
		if(panY > endY) panY = endY;
		//at end of the picture or just finished transitioning out
		if((panX == endX && panY == endY) || backTrans.justOut()){
			//reset and move to next background
			panX = 0;
			panY = 0;
			currentBackground++;
			if(currentBackground == backgroundPaths.length)
				currentBackground = 0;
			nextBackground();
			//fade in
			backTrans.activate(true);
		}
		//if should start fading out
		if(!backTrans.isActive() && (panX >= endX - TileConstants.PANNING_BUFFER || panY >= endY - TileConstants.PANNING_BUFFER)){
			backTrans.activate(false);
		}
		//tick menu
		if(warning){
			//close menu
			if(!warningMenu.tick(input, null)){
				warning = false;
				selectionMade = true;
			}
		}
		else{
			//mark a selection made if interacted
			if(!menu.tick(input, null)){
				selectionMade = true;
				//chose load game without a game to load
				if(menu.getCurrentSelection() == 0 && Flags.getFlags().isFresh()){
					TileAudio.playSound("buzz");
					selectionMade = false;
				}
				//chose new game but already game there
				if(menu.getCurrentSelection() == 1 && !Flags.getFlags().isFresh()){
					//show warning
					warning = true;
					selectionMade = false;
				}
			}
		}
	}
	@Override
	public boolean isDone() {
		//done if selection made
		return selectionMade;
	}
	@Override
	public void onBegin(){
		//fade background in after regular fade in
		backTrans.activate(true);
	}
	@Override
	public void onEnd(){
		//do nothing
	}
	@Override
	public GameState endState(){
		switch(menu.getCurrentSelection()){
			case 0: return GameState.LOAD_GAME;
			case 1: return GameState.NEW_GAME;
			case 2: return GameState.INSTRUCTION;
			case 3: return GameState.OPTIONS;
			case 4: return GameState.ABOUT;
			case 5: return GameState.QUIT;
			default: return null;
		}
	}
}
