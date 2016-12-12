package org.spooner.java.TileGame.Menus;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.spooner.java.TileGame.Animation;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Scenes.Board;

public class TextMenu extends AbstractMenu{
	//members
	private String[] words;
	private Point[] wordCoords;
	private boolean arrow;
	//constructors
	public TextMenu(int width, int height, boolean arrow){
		super(width, height);
		words = null;
		wordCoords = null;
		this.arrow = arrow;
	}
	//methods
	public void setText(String text){
		//split into words
		this.words = text.split(" ");
		wordCoords = null;
	}
	private void positionWords(FontMetrics fm, int xStart, int w, int h){
		//initialize the array
		wordCoords = new Point[words.length];
		//find out the stats of the string to display
		int strHeight = fm.getHeight();
		int spaceWidth = fm.stringWidth(" ");
		//line height starts at string height for first line plus the starting position
		int lineHeight = strHeight;
		//width starts after starting point
		int lineWidth = xStart;
		//for every word
		for(int u = 0; u < words.length; u++){
			String word = words[u];
			int tempWidth = 0;
			//for every character
			for(int i = 0; i < word.length(); i++){
				//grab 1 character
				String ch = word.substring(i, i + 1);
				//find its draw length
				int len = fm.stringWidth(ch);
				//add its length
				tempWidth += len;
			}
			//if can fit on the line
			if(tempWidth + lineWidth + xStart <= w){
				//the current line width
				int xPos = lineWidth;
				//add the width and a space
				lineWidth += tempWidth + spaceWidth;
				//record the string position starting at the beginning of the string
				wordCoords[u] = new Point(xPos, lineHeight);
			}
			else{
				//start a new line no matter what
				//increment
				lineHeight += strHeight;
				//reset counter to start of x
				lineWidth = xStart;
				//add the width and a space
				lineWidth += tempWidth + spaceWidth;
				//record the string position starting at the beginning of the string
				wordCoords[u] = new Point(xStart, lineHeight);
			}
		}
	}
	@Override
	public void drawText(Graphics g, int ox, int oy, int w, int h){
		if(words != null){
			//initialize word positions if they are null
			if(wordCoords == null){
				//position the words at the start (no translation)
				positionWords(g.getFontMetrics(), ox, w, h);
			}
			//draw the words at the coords
			for(int i = 0; i < words.length; i++){
				//x coords stay same, y coords could be changing
				g.drawString(words[i], wordCoords[i].x, wordCoords[i].y + oy);
			}
			//only if arrow shown
			if(arrow){
				final Animation A = TileConstants.ARROW_ANIMATION;
				//draw the arrow at a normal speed
				g.drawImage(A.nextFrame(), ox + w - A.getWidth(), oy + h - A.getHeight(), null);
			}
		}
	}
	@Override
	public boolean tick(InputTracker input, Board currentBoard){
		//if canceled
		if(input.consumeHubbing()){
			TileAudio.playSound("buzz");
		}
		//remain open until press button
		return !input.consumeInteract() && !input.consumeCancel();
	}
}
