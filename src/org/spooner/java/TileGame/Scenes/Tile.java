package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;
import java.awt.Image;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileSet;
import org.spooner.java.TileGame.Entities.Entity;

public class Tile {
	//members
	private int id;
	private boolean[] isSideBlocked;
	private Entity occupent;
	private int foreID;
	//constuctors
	public Tile(int id, boolean[] isSideBlocked, int foreID){
		this.id=id;
		this.isSideBlocked = isSideBlocked;
		this.foreID = foreID;
		occupent = null;
	}
	//methods
	public boolean hasForeground(){ return foreID >= 0; }
	public int getForeID(){ return foreID; }
	public int getID(){return id;}
	public boolean isSideBlocked(Direction d){ return isSideBlocked[d.toIndex()];}
	public void fill(Entity e){occupent = e;}
	public BoardChangeEvent onFill(){
		//no change
		return null;
	}
	public void vacate(){occupent = null;}
	public boolean isFilled(){return occupent != null;}
	public Entity getOccupent(){ return occupent; }
	public void draw(Graphics g, int x, int y, int viewx, int viewy){
		drawImage(g, TileSet.currentSet().getIcon(id), x, y, viewx, viewy);
	}
	public void drawForeground(Graphics g, int x, int y, int viewx, int viewy){
		drawImage(g, TileSet.foregroundSet().getIcon(foreID), x, y, viewx, viewy);
	}
	private void drawImage(Graphics g, Image i, int x, int y, int viewx, int viewy){
		//subtract view to scroll in opposite direction (view is inverted)
		g.drawImage(i, x * TileConstants.TILE_SIZE - viewx, 
				y * TileConstants.TILE_SIZE - viewy, TileConstants.TILE_SIZE, TileConstants.TILE_SIZE, null);
	}
}
