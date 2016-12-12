package org.spooner.java.TileGame.Entities;

import java.awt.Point;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileGame;
import org.spooner.java.TileGame.Speech.Conversation;

public class LinearNPC extends NPC {
	//constructors
	public LinearNPC(String name, String spriteAlias, Conversation[] conversations, Point start, Point limit){
		super(name, spriteAlias, conversations, start, limit);
	}
	//methods
	@Override
	public boolean directionMove(Direction dir, Point dest, int speedDir){
		boolean isThere = false;
		//while not there and still moving
		for(int i = 0; i < getSpeed() && !isThere; i++){
			if(dir.isHorizontal()){
				realXMove(speedDir);
				//real position at correct tile
				isThere = getRealX() == dest.x * TileConstants.TILE_SIZE;
			}
			else{
				realYMove(speedDir);
				//real position at correct tile (add a tile size to measure from the bottom)
				isThere = getRealY() == dest.y * TileConstants.TILE_SIZE + TileConstants.TILE_SIZE;
			}
		}
		return isThere;
	}
	@Override
	public Direction calculateDirection(Point destination) {
		Direction dir = Direction.NONE;
		int x=getTileX();
		int y=getTileY();
		if(destination.y<y) dir = Direction.UP;
		if(destination.y>y) dir = Direction.DOWN;
		if(destination.x<x) dir = Direction.LEFT;
		if(destination.x>x) dir = Direction.RIGHT;
		return dir;
	}
	@Override
	public Point calculateDestination(){
		Point start = getStart();
		Point limit = getLimit();
		int dx=getTileX();
		int dy=getTileY();
		//if vertical movement
		if(start.x == limit.x){
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
