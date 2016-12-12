package org.spooner.java.TileGame.Entities;

import java.awt.Point;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.TileGame;
import org.spooner.java.TileGame.Speech.Conversation;

public class StationaryNPC extends NPC{
	//constructors
	public StationaryNPC(String name, String spriteAlias, Conversation[] conversations, Point startPoint, Point limitPoint){
		super(name, spriteAlias, conversations, startPoint, limitPoint);
	}
	//methods
	@Override
	public Direction calculateDirection(Point destination){
		Direction dir = Direction.NONE;
		if(TileGame.getRandomBoolean()){
			dir = TileGame.getRandomBoolean() ? Direction.UP : Direction.DOWN;
		}
		else{
			dir = TileGame.getRandomBoolean() ? Direction.LEFT : Direction.RIGHT;
		}
		return dir;
	}
	@Override
	public Point calculateDestination(){
		//destination is current point
		return getStart();
	}
	@Override
	public boolean directionMove(Direction dir, Point dest, int speedDir){
		//always there
		return true;
	}
}
