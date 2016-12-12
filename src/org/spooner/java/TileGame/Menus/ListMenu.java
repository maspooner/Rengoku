package org.spooner.java.TileGame.Menus;

import java.awt.FontMetrics;
import java.awt.Graphics;

import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.InventoryItem;
import org.spooner.java.TileGame.Scenes.Board;

public class ListMenu extends ChoiceMenu {
	//members
	private String title;
	private TextMenu descMenu;
	private boolean inMenu;
	//constructors
	public ListMenu(String title, InventoryItem[] items, int width, int height, int dWidth, int dHeight){
		super(items, width, height, true, true);
		this.title = title;
		//description menu has no arrow
		descMenu = new TextMenu(dWidth, dHeight, false);
		inMenu = false;
	}
	//methods
	@Override
	public void draw(Graphics g){
		//draw normally
		super.draw(g);
		//if description menu open, draw it
		if(inMenu){
			descMenu.draw(g);
		}
	}
	@Override
	public void drawText(Graphics g, int ox, int oy, int w, int h){
		//find out the stats of the string to display
		FontMetrics fm = g.getFontMetrics();
		int strHeight = fm.getHeight();
		int currentSelection = getCurrentSelection();
		//draw title
		g.drawString(title, ox, oy + strHeight);
		//how many entries can fit in the window (integer math to round down)
		int entryNum = (h / strHeight) - 2;
		//if further down on the list, move the list down, else, start at 0
		int firstEntry = currentSelection + 2 - entryNum >= 0 ? currentSelection + 2 - entryNum : 0;
		//starts one down for title room
		int lineHeight = strHeight * 2;
		//for every entry that can fit
		for(int u = firstEntry; u <= (entryNum + firstEntry) && u < numItems(); u++){
			//draw the string on the new line
			g.drawString(getItem(u).getName(), ox, oy + lineHeight);
			//add more height
			lineHeight += strHeight;
		}
		//draw the arrow
		int cursorY = strHeight * (currentSelection - firstEntry + 2);
		drawArrow(g, ox, oy + cursorY);
	}
	@Override
	public boolean tick(InputTracker input, Board currentBoard) {
		if(inMenu){
			//in menu if still open = true
			inMenu = descMenu.tick(input, currentBoard);
			//jump out (keeping menu open)
			return true;
		}
		else{
			//tick normally
			//return if super menu wants to stay open or now inMenu
			return super.tick(input, currentBoard) || inMenu;
		}
	}
	@Override
	protected void onInteract(){
		//only if has items
		if(numItems() > 0){
			inMenu = true;
			InventoryItem item = getItem(getCurrentSelection());
			//set the description text
			descMenu.setText(item.getName() + ":  " + item.getDesc());
		}
	}
}
