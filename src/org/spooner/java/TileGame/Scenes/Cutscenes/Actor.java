package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Point;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.DirectionalAnimation;
import org.spooner.java.TileGame.TileConstants;

public class Actor extends Prop{
	//members
	private DirectionalAnimation moveAni;
	private DirectionalAnimation stopAni;
	private Direction dir;
	//constructors
	protected Actor(String spriteAlias, int x, int y, Direction dir){
		super(x, y);
		moveAni = new DirectionalAnimation(TileConstants.PERSON_SPRITE_PATH + spriteAlias + "/Move/");
		stopAni = new DirectionalAnimation(TileConstants.PERSON_SPRITE_PATH + spriteAlias + "/Stop/");
		this.dir = dir;
	}
	//methods
	@Override
	protected BufferedImage getFrame(){
		return atDestination() ? stopAni.nextFrame(dir, TileConstants.ANIMATION_RATE) : moveAni.nextFrame(dir, TileConstants.ANIMATION_RATE);
	}
	protected boolean turn(Direction dir){
		this.dir = dir;
		return true;
	}
	protected boolean moveTo(int nx, int ny, Direction dir){
		this.dir = dir;
		boolean there = super.moveTo(nx, ny);
		if(there) moveAni.reset();
		return there;
	}
	@Override
	protected boolean followPath(Path p){
		Point current = p.currDest();
		//finished moving to destination
		if(moveTo(current.x, current.y, getDirectionToward(current))){
			//go to next point in path
			if(p.hasNext()){
				p.next();
			}
			else{
				//done with all of path
				return true;
			}
		}
		return false;
	}
	private Direction getDirectionToward(Point p){
		//distance from destination to coords
		int diffX = p.x - getX();
		//y axis is inverted
		int diffY = getY() - p.y;
		double degs = Math.toDegrees(Math.atan2(diffY, diffX));
		//get everything positive
		if(degs < 0) degs += 360;
		//upper
		if(degs <= 135.0 && degs >= 45.0){
			return Direction.UP;
		}
		//left
		else if(degs > 135.0 && degs < 225.0){
			return Direction.LEFT;
		}
		//down
		else if(degs >= 225.0 && degs <= 315.0){
			return Direction.DOWN;
		}
		//right
		else{
			return Direction.RIGHT;
		}
	}
}
