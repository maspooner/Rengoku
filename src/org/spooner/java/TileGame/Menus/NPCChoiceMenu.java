package org.spooner.java.TileGame.Menus;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Entities.NPC;
import org.spooner.java.TileGame.Scenes.Board;

public class NPCChoiceMenu extends AbstractMenu{
	//members
	private int currentSelection;
	private Direction lastDirection;
	private String[] commands;
	private NPC invoker;
	//constructors
	protected NPCChoiceMenu(int width, int height){
		super(width, height);
		currentSelection = -1;
		lastDirection = Direction.NONE;
		invoker = null;
		commands = null;
	}
	//methods
	public void open(NPC invoker, String[] commands){
		this.invoker = invoker;
		this.commands = commands;
		currentSelection = 0;
	}
	@Override
	public void drawText(Graphics g, int ox, int oy, int w, int h){
		//commands must be present
		if(commands != null){
			//find out the stats of the string to display
			FontMetrics fm = g.getFontMetrics();
			int strHeight = fm.getHeight();
			//draw name
			g.drawString(TileConstants.PLAYER_NAME + ":", ox, oy + strHeight);
			//for every command
			for(int u = 0; u < commands.length; u++){
				//draw the string on the new line
				g.drawString(commands[u], ox, oy + strHeight * (u+2));
			}
			//draw the arrow
			final BufferedImage A = TileConstants.SELECTION_ARROW;
			g.drawImage(A, ox - A.getWidth() - TileConstants.CURSOR_BUFFER, oy + strHeight * (currentSelection + 2) - A.getHeight(), null);
		}
	}
	@Override
	public boolean tick(InputTracker input, Board currentBoard){
		boolean open = true;
		Direction currDir = input.calculateDirection();
		//prevent insane scrolling
		if(!currDir.equals(lastDirection)){
			//index of last choice
			int lastIndex = commands.length - 1;
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
				TileAudio.playSound("select");
			}
			lastDirection = currDir;
		}
		//if interacting
		if(input.consumeInteract()){
			//make the choice
			invoker.nextChoice().makeChoice(currentSelection);
			//reset selection
			currentSelection = -1;
			invoker = null;
			commands = null;
			//close window
			open = false;
		}
		else if(input.consumeCancel() || input.consumeHubbing()){
			TileAudio.playSound("buzz");
		}
		return open;
	}
}
