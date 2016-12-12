package org.spooner.java.TileGame.Menus;

import java.awt.Graphics;

import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileFrame;
import org.spooner.java.TileGame.Scenes.Board;

public abstract class AbstractMenu {
	//constants
	public static final int FULL = -1;
	//members
	private int xAlign;
	private int yAlign;
	private Position xPos;
	private Position yPos;
	private int width;
	private int height;
	//constructor
	protected AbstractMenu(int width, int height){
		xAlign = 0;
		yAlign = 0;
		xPos = Position.CUSTOM;
		yPos = Position.CUSTOM;
		this.width = width;
		this.height = height;
	}
	//methods
	public void draw(Graphics g){
		//calculate all parameters
		int x = calculateX(), y = calculateY();
		int w = calculateWidth(), h = calculateHeight();
		//draw the back most panel
		g.setColor(TileConstants.MENU_COLOR);
		g.fillRoundRect(x, y, w, h, TileConstants.MENU_ARC_WIDTH, TileConstants.MENU_ARC_WIDTH);
		//draw the border around the edge
		g.setColor(TileConstants.MENU_BORDER_COLOR);
		//offset relative x, y, w, h (dimensions are *2 for both sides)
		x += TileConstants.MENU_BORDER_WIDTH;
		y += TileConstants.MENU_BORDER_WIDTH;
		w -= TileConstants.MENU_BORDER_WIDTH * 2;
		h -= TileConstants.MENU_BORDER_WIDTH * 2;
		g.fillRoundRect(x, y, w, h, TileConstants.MENU_ARC_WIDTH, TileConstants.MENU_ARC_WIDTH);
		//do it again, but paint it background color to get the border effect
		g.setColor(TileConstants.MENU_COLOR);
		x += TileConstants.MENU_BORDER_WIDTH;
		y += TileConstants.MENU_BORDER_WIDTH;
		w -= TileConstants.MENU_BORDER_WIDTH * 2;
		h -= TileConstants.MENU_BORDER_WIDTH * 2;
		g.fillRoundRect(x, y, w, h, TileConstants.MENU_ARC_WIDTH, TileConstants.MENU_ARC_WIDTH);
		//set up for text
		g.setColor(TileConstants.MENU_TEXT_COLOR);
		g.setFont(TileConstants.MENU_FONT);
		//offset x y w h and find the pixel allowance of the two
		x += TileConstants.MENU_X_TEXT_OFFSET;
		y += TileConstants.MENU_Y_TEXT_OFFSET;
		w -= 2 * TileConstants.MENU_X_TEXT_OFFSET;
		h -= 2 * TileConstants.MENU_Y_TEXT_OFFSET;
		//draw the text
		drawText(g, x, y, w, h);
	}
	
	public abstract void drawText(Graphics g, int ox, int oy, int w, int h);
	public abstract boolean tick(InputTracker input, Board currentBoard);
	public void setXPosition(Position xPos){ this.xPos = xPos; }
	public void setYPosition(Position yPos){ this.yPos = yPos; }
	public void setXAlign(int xAlign){
		//becomes custom position
		xPos = Position.CUSTOM;
		this.xAlign = xAlign;
	}
	public void setYAlign(int yAlign){
		//becomes custom position
		yPos = Position.CUSTOM;
		this.yAlign = yAlign;
	}
	private int calculateX(){
		int x = 0;
		//if exact position
		if(xPos.equals(Position.CUSTOM)){
			x = xAlign;
		}
		//on the right of the screen
		else if(xPos.equals(Position.RIGHT)){
			//full width
			if(width == FULL){
				//same as left
				x = TileConstants.MENU_X_ADJUSTMENT;
			}
			else{
				//start from frame width and work left accounting for width, insets, etc
				x = TileConstants.FRAME_WIDTH - width - TileFrame.RIGHT_INSET - TileConstants.MENU_X_ADJUSTMENT;
			}
		}
		//on the left of the screen
		else if(xPos.equals(Position.LEFT)){
			//indent
			x = TileConstants.MENU_X_ADJUSTMENT;
		}
		else if(xPos.equals(Position.MIDDLE)){
			//go to the middle of frame and window
			x = TileConstants.MENU_X_ADJUSTMENT + TileConstants.FRAME_WIDTH / 2 - width / 2;
		}
		return x;
	}
	private int calculateY(){
		int y = 0;
		//if exact position
		if(yPos.equals(Position.CUSTOM)){
			y = yAlign;
		}
		//on the bottom of the screen
		else if(yPos.equals(Position.BOTTOM)){
			//full height
			if(height == FULL){
				//same as left
				y = TileConstants.MENU_Y_ADJUSTMENT;
			}
			else{
				//start from frame height and work up accounting for height, insets, etc
				y = TileConstants.FRAME_HEIGHT - height - TileFrame.BOTTOM_INSET - TileConstants.MENU_Y_ADJUSTMENT;
			}
		}
		//on the top of the screen
		else if(yPos.equals(Position.TOP)){
			//indent
			y = TileConstants.MENU_Y_ADJUSTMENT;
		}
		else if(yPos.equals(Position.MIDDLE)){
			//go to the middle of frame and window
			y = TileConstants.MENU_Y_ADJUSTMENT + TileConstants.FRAME_HEIGHT / 2 - height / 2;
		}
		return y;
	}
	private int calculateWidth(){
		//w is the width unless the width is full, then set to frame width accounting for rightInset (left already handled in translate)
		int w = width == FULL ? TileConstants.FRAME_WIDTH - TileFrame.RIGHT_INSET : width;
		//if a code AND full
		if(xAlign < 0 && width == FULL){
			//adjust the menu (*2 for both sides)
			w -= TileConstants.MENU_X_ADJUSTMENT*2;
		}
		return w;
	}
	private int calculateHeight(){
		//h is the height unless the height is full, then set to frame height accounting for bottomInset (top already handled in translate)
		int h = height == FULL ? TileConstants.FRAME_HEIGHT - TileFrame.BOTTOM_INSET : height;
		//if a code AND full
		if(yAlign < 0 && height == FULL){
			//adjust the menu (*2 for both sides)
			h -= TileConstants.MENU_Y_ADJUSTMENT*2;
		}
		return h;
	}
}
