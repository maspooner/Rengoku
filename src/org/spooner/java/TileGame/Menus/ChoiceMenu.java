package org.spooner.java.TileGame.Menus;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.InventoryItem;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Scenes.Board;

public class ChoiceMenu extends AbstractMenu{
	//members
	private int currentSelection;
	private Direction lastDirection;
	private InventoryItem[] items;
	private boolean canCancel;
	private boolean canHub;
	//constructors
	public ChoiceMenu(InventoryItem[] items, int width, int height, boolean canCancel, boolean canHub){
		super(width, height);
		this.canHub = canHub;
		this.items = items;
		currentSelection = 0;
		lastDirection = Direction.NONE;
		this.canCancel = canCancel;
	}
	//methods
	protected void drawArrow(Graphics g, int x, int y){
		final BufferedImage A = TileConstants.SELECTION_ARROW;
		g.drawImage(A, x - A.getWidth() - TileConstants.CURSOR_BUFFER, y - A.getHeight(), null);
	}
	protected void onInteract(){
		//do nothing for this
	}
	public int getCurrentSelection(){ return currentSelection; }
	public void setItems(InventoryItem[] items){ this.items = items; }
	public InventoryItem getItem(int i){ return items[i]; }
	public int numItems(){ return items.length; }
	@Override
	public void drawText(Graphics g, int ox, int oy, int w, int h) {
		//find out the stats of the string to display
		FontMetrics fm = g.getFontMetrics();
		int strHeight = fm.getHeight();
		//for every entry
		for(int i = 0; i < items.length; i++){
			//draw the string on the new line
			g.drawString(items[i].getName(), ox, oy + strHeight * (i + 1));
		}
		//draw arrow at current selection
		drawArrow(g, ox, oy + strHeight * (currentSelection + 1));
	}
	@Override
	public boolean tick(InputTracker input, Board currentBoard) {
		//reset if canceled last time
		if(currentSelection == -1) currentSelection = 0;
		Direction currDir = input.calculateDirection();
		//prevent insane scrolling
		if(!currDir.equals(lastDirection)){
			//index of last entry
			int lastIndex = items.length == 0 ? 0 : items.length - 1;
			switch(currDir){
				case UP:
				case LEFT:
					//move back index
					currentSelection--;
					//make sure selection is in bounds
					if(currentSelection < 0){
						currentSelection = lastIndex;
					}
					break;
				case DOWN:
				case RIGHT:
					//move forward index
					currentSelection++;
					//make sure selection is in bounds
					if(currentSelection > lastIndex){
						currentSelection = 0;
					}
					break;
				default: break;
			}
			//play the select sound if moving
			if(!currDir.equals(Direction.NONE)){
				if(!"select.wav".equals(TileAudio.getPlayingSound())) TileAudio.playSound("select");
			}
			lastDirection = currDir;
		}
		//if interacting, do on interact
		if(input.consumeInteract()){
			onInteract();
			//close the window
			return false;
		}
		//if canceled
		if(input.consumeCancel()){
			if(canCancel){
				//flag choice
				currentSelection = -1;
				//close the widnow
				return false;
			}
			else{
				TileAudio.playSound("buzz");
			}
		}
		//if canceled
		if(input.consumeHubbing()){
			if(canHub){
				//flag choice
				currentSelection = -1;
				//close the widnow
				return false;
			}
			else{
				TileAudio.playSound("buzz");
			}
		}
		//stay open
		return true;
	}
}
