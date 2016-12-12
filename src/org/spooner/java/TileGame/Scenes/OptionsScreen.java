package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;

import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.InventoryItem;
import org.spooner.java.TileGame.Options;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Menus.ChoiceMenu;
import org.spooner.java.TileGame.Menus.Position;

public class OptionsScreen implements DrawableScene{
	//members
	private ChoiceMenu menu;
	private InventoryItem[] items;
	private boolean done;
	private BouncingBackground bb;
	//constructors
	public OptionsScreen(){
		items = new InventoryItem[]{
			new InventoryItem(Options.getOptions().isMusicMuted() ? "Unmute the music" : "Mute the music"),
			new InventoryItem(Options.getOptions().isSoundMuted() ? "Unmute the sound" : "Mute the sound"),
			new InventoryItem("Music Volume: " + Options.getOptions().getMusicVolume() + "%"),
			new InventoryItem("Sound Volume: " + Options.getOptions().getSoundVolume() + "%"),
			new InventoryItem("Text Speed: " + Options.getOptions().getTextSpeed(false)),
			new InventoryItem(Options.getOptions().showJoy() ? "Running JoyToKey on startup." : "Not Running JoyToKey on startup."),
			new InventoryItem("Game Speed (Advanced): " + Options.getOptions().getWait()),
			new InventoryItem("Back")
		};
		menu = new ChoiceMenu(items, TileConstants.OPTIONS_MENU_WIDTH, TileConstants.OPTIONS_MENU_HEIGHT, false, false);
		menu.setXPosition(Position.MIDDLE);
		menu.setYPosition(Position.MIDDLE);
		bb = new BouncingBackground("optBack", TileConstants.BOUNCING_SCALE, TileConstants.BOUNCING_SPEED);
		done = false;
	}
	//methods
	@Override
	public void onBegin() { }
	@Override
	public void draw(Graphics g, InputTracker input){
		bb.draw(g);
		menu.draw(g);
	}
	@Override
	public void tick(InputTracker input){
		bb.tick();
		if(input.consumeCancel()){
			done = true;
		}
		//selected something
		else if(!menu.tick(input, null)){
			switch(menu.getCurrentSelection()){
			case 0:
				//toggle muting music
				Options.getOptions().toggleMusicMuted();
				//update the item
				items[0] = new InventoryItem(Options.getOptions().isMusicMuted() ? "Unmute the music" : "Mute the music");
				break;
			case 1:
				//toggle muting sound
				Options.getOptions().toggleSoundMuted();
				//update the item
				items[1] = new InventoryItem(Options.getOptions().isSoundMuted() ? "Unmute the sound" : "Mute the sound");
				break;
			case 2:
				//increase the music volume
				Options.getOptions().increaseMusicVolume();
				//update the item
				items[2] = new InventoryItem("Music Volume: " + Options.getOptions().getMusicVolume() + "%");
				break;
			case 3:
				//increase sound volume
				Options.getOptions().increaseSoundVolume();
				//update the item
				items[3] = new InventoryItem("Sound Volume: " + Options.getOptions().getSoundVolume() + "%");
				break;
			case 4:
				//increase text speed
				Options.getOptions().increaseTextSpeed();
				//update the item
				items[4] = new InventoryItem("Text Speed: " + Options.getOptions().getTextSpeed(false));
				break;
			case 5:
				//toggle joy
				Options.getOptions().toggleShowJoy();
				//update the item
				items[5] = new InventoryItem(Options.getOptions().showJoy() ? "Running JoyToKey on startup." : "Not Running JoyToKey on startup.");
				break;
			case 6:
				//increase wait
				Options.getOptions().increaseWait();
				//update the item
				items[6] = new InventoryItem("Game Speed (Advanced): " + Options.getOptions().getWait());
				break;
			case 7:
				//returning
				done = true;
				break;
			}
			TileAudio.applyOptions();
		}
	}
	@Override
	public boolean isDone() {
		//done if pressed back
		return done;
	}
	@Override
	public void onEnd() {
		//save options
		TileIO.saveOptions();
	}
	@Override
	public GameState endState(){
		return GameState.TITLE;
	}
}
