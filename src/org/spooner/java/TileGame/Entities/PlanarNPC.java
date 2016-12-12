package org.spooner.java.TileGame.Entities;

import java.awt.Point;

import org.spooner.java.TileGame.TileGame;
import org.spooner.java.TileGame.Speech.Conversation;

public class PlanarNPC extends LinearNPC{
	//constructors
	public PlanarNPC(String name, String spriteAlias, Conversation[] conversations, Point start, Point limit){
		super(name, spriteAlias, conversations, start, limit);
	}
	//methods
	@Override
	public Point calculateDestination(){
		Point start = getStart();
		Point limit = getLimit();
		int dx = getTileX();
		int dy = getTileY();
		//if vertical movement
		if(TileGame.getRandomBoolean()){ //only line changed from linear NPC!
			boolean isUp=false;
			//go up
			if(TileGame.getRandomBoolean()){
				//if start below limit
				int topBound = start.y > limit.y ? limit.y : start.y;
				//only go up if new y is below the boundary
				isUp = dy - 1 >= topBound;
			}
			//go down
			else{
				//if start below limit
				int bottomBound = start.y > limit.y ? start.y : limit.y;
				//only go down if new y is above the boundary
				isUp = !(dy + 1 <= bottomBound);
			}
			//move in the correct direction
			dy += isUp ? -1 : 1;
		}
		//horizontal
		else{
			boolean isLeft=false;
			//go left
			if(TileGame.getRandomBoolean()){
				//if start is right of limit
				int leftBound = start.x > limit.x ? limit.x : start.x;
				//only go left if new x is right of the boundary
				isLeft = dx - 1 >= leftBound;
			}
			//go down
			else{
				//if start is right of limit
				int rightBound = start.x > limit.x ? start.x : limit.x;
				//only go right if new y is left of the boundary
				isLeft = !(dx + 1 <= rightBound);
			}
			//move in the correct direction
			dx += isLeft ? -1 : 1;
		}
		return new Point(dx, dy);
	}
}
