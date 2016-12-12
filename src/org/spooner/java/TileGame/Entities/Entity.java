package org.spooner.java.TileGame.Entities;

import java.awt.Point;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.DirectionalAnimation;
import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.BoardState;
import org.spooner.java.TileGame.Scenes.Tile;

public abstract class Entity {
	//members
	private int height;
	private int width;
	private DirectionalAnimation moveAni;
	private DirectionalAnimation stopAni;
	private Direction lastDirection;
	private int xTile;
	private int yTile;
	private int xReal;
	private int yReal;
	//constructors
	public Entity(String animationPath, Point start, Direction orgFace){
		this.xTile = start.x;
		this.yTile = start.y;
		//the real positon of the character
		xReal = start.x * TileConstants.TILE_SIZE;
		//y is measured from the bottom of the character -> add tile size to compensate
		yReal = start.y * TileConstants.TILE_SIZE + TileConstants.TILE_SIZE;
		height = TileConstants.PLAYER_HEIGHT * TileConstants.TILE_SCALE;
		width = TileConstants.PLAYER_WIDTH * TileConstants.TILE_SCALE;
		moveAni = new DirectionalAnimation(animationPath + "Move/");
		stopAni = new DirectionalAnimation(animationPath + "Stop/");
		lastDirection = orgFace;
	}
	//methods
	public BufferedImage getFrame(Direction direction, BoardState state, boolean isFast){
		int rate = isFast ? TileConstants.FAST_ANIMATION_RATE : TileConstants.ANIMATION_RATE;
		if(direction.equals(Direction.NONE)){
			//not moving
			//reset moving animation
			moveAni.reset();
			return stopAni.nextFrame(lastDirection, rate);
		}
		else if(!state.equals(BoardState.IN_MENU)){
			//moving and not in menu
			//set last direction
			lastDirection = direction;
			return moveAni.nextFrame(direction, rate);
		}
		else{
			//default is not moving
			return stopAni.nextFrame(direction, rate);
		}
	}
	public boolean move(Direction d, Board currentBoard, Point destination){
		Tile from = currentBoard.getTileAt(xTile, yTile);
		Tile to = currentBoard.getTileAt(destination.x, destination.y);
		boolean moved = false;
		//what direction the entity is trying to enter the tile from
		Direction tileDir = d.opposite();
		//only if the side is accessible from both tiles and it is not filled already
		if(!to.isSideBlocked(tileDir) && !from.isSideBlocked(d) && !to.isFilled()){
			//fill the space
			to.fill(this);
			//empty the current space
			from.vacate();
			//move to the tile
			switch(d){
				case UP: yTile--; break;
				case DOWN: yTile++; break;
				case LEFT: xTile--; break;
				case RIGHT: xTile++; break;
				default: break;
			}
			//was a success
			moved = true;
		}
		return moved;
	}
	public void face(Entity e){
		lastDirection = e.lastDirection.opposite();
	}
	public abstract int getSpeed(boolean isRunning, boolean isFastBoard);
	public Direction getLastDirection(){ return lastDirection; }
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public int getTileX(){return xTile;}
	public int getTileY(){return yTile;}
	public int getRealX(){ return xReal; }
	public int getRealY(){ return yReal; }
	public void setRealX(int xReal) { this.xReal = xReal; }
	public void setRealY(int yReal) { this.yReal = yReal; }
	public void realXMove(int dir){ xReal += dir; }
	public void realYMove(int dir){ yReal += dir; }
}
