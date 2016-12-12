package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;

import org.spooner.java.TileGame.Animation;
import org.spooner.java.TileGame.Flags;
import org.spooner.java.TileGame.TileConstants;

public class ItemTile extends Tile{
	//members
	private int after;
	private int item;
	private int fire;
	private Animation sparkleAni;
	//constructors
	public ItemTile(int id, int after, int item, int fire){
		//auto blocked with no foreground
		super(id, new boolean[]{ true, true, true, true }, -1);
		this.after = after;
		this.item = item;
		this.fire = fire;
		sparkleAni = new Animation(TileConstants.SPARKLE_ANIMATION);
	}
	//methods
	public int getItem(){ return item; }
	@Override
	public void draw(Graphics g, int x, int y, int viewx, int viewy){
		super.draw(g, x, y, viewx, viewy);
		//draw the sparkle
		g.drawImage(sparkleAni.nextFrame(), x * TileConstants.TILE_SIZE - viewx, y * TileConstants.TILE_SIZE - viewy,
				TileConstants.TILE_SIZE, TileConstants.TILE_SIZE, null);
	}
	public boolean canRecieve(){
		//can only get item if meets after flag and doesn't have the item already
		return Flags.getFlags().hasFlag("after", after) && !Flags.getFlags().hasFlag("after", fire);
	}
	public void onInteract(){
		//give the item and make sure never fired again
		Flags.getFlags().fireFlag("get", item);
		Flags.getFlags().fireFlag("trigger", fire);
	}
	public Tile replacement(){
		return new Tile(getID(), new boolean[]{ true, true, true, true }, getForeID());
	}
}
