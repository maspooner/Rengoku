package org.spooner.java.TileGame.Entities;

import java.awt.Graphics;
import java.awt.Point;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.InventoryItem;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.BoardChangeEvent;
import org.spooner.java.TileGame.Scenes.BoardState;
import org.spooner.java.TileGame.Scenes.ItemTile;
import org.spooner.java.TileGame.Scenes.Tile;

public class Player extends Entity{
	//members
	private Direction transitionDir;
	private int xOffset;
	private int yOffset;
	private boolean xLocked;
	private boolean yLocked;
	//constructors
	public Player(Point start, int bWidth, int bHeight, Direction orgFace){
		super(TileConstants.PERSON_SPRITE_PATH + "Emere/", start, orgFace);
		transitionDir = Direction.NONE;
		//if locked in the center position
		xLocked = false;
		yLocked = false;
		//the offset to be used by the board
		calibratePlayerPosition(bWidth, bHeight);
	}
	//methods
	private void calibratePlayerPosition(int bWidth, int bHeight){
		Point c = TileConstants.FRAME_CENTER;
		int rightEdge = bWidth - TileConstants.FRAME_WIDTH;
		int lowEdge = bHeight - TileConstants.FRAME_HEIGHT;
		//if real x is past the center of the frame
		if(getRealX() > c.x){
			//on right of screen past center
			if(getRealX() > rightEdge + c.x){
				//bring offset to edge
				xOffset = rightEdge;
				//bring player to its new position
				setRealX(getRealX() - rightEdge);
			}
			else{
				//set offset to what is left
				xOffset = getRealX() - c.x;
				//bring player to center
				setRealX(c.x);
			}
		}
		else{
			//no offset
			xOffset = 0;
			//real x is good
		}
		//if real y is past the center of the frame
		if(getRealY() > c.y){
			//on bottom of screen past center
			if(getRealY() > lowEdge + c.y){
				//bring offset to edge
				yOffset = lowEdge;
				//bring player to its new position
				setRealY(getRealY() - lowEdge);
			}
			else{
				//set offset to what is left
				yOffset = getRealY() - c.y;
				//bring player to center
				setRealY(c.y);
			}
		}
		else{
			//no offset
			yOffset = 0;
			//real y is good
		}
	}
	public void draw(InputTracker input, Graphics g, BoardState state){
		if(input == null) return;
		//offset due to the height of character
		int y = getRealY() - getHeight();
		//if transitioning AND in movement state, use the transition direction, else, use the user's input direction
		Direction dir = transitionDir.equals(Direction.NONE) && state.equals(BoardState.MOVEMENT)
				? input.calculateDirection() : transitionDir;
		g.drawImage(getFrame(dir, state, input.isRunning()), getRealX(), y, getWidth(), getHeight(), null);
	}
	public void tick(InputTracker input, Board currentBoard){
		Direction dir = input.calculateDirection();
		boolean interaction = input.consumeInteract();
		//transitioning to another tile
		if(!transitionDir.equals(Direction.NONE)){
			//down and right are pos
			int speedDir = transitionDir.equals(Direction.DOWN) || transitionDir.equals(Direction.RIGHT) ? 1 : -1;
			boolean isThere = false;
			//speed varies with running
			int speed = getSpeed(input.isRunning(), currentBoard.isFast());
			//move the actual player position
			for(int i = 0; i < speed && !isThere; i++){
				if(transitionDir.isHorizontal()){
					transitionX(speedDir, currentBoard.getWidth(), TileConstants.FRAME_CENTER.x);
					//at a tile intersection
					isThere = (getRealX() + xOffset) % TileConstants.TILE_SIZE == 0;
				}
				else{
					transitionY(speedDir, currentBoard.getHeight(), TileConstants.FRAME_CENTER.y);
					//at a tile intersection
					isThere = (getRealY() + yOffset) % TileConstants.TILE_SIZE == 0;
				}
			}
			if(isThere){
				//no longer transitioning
				transitionDir = Direction.NONE;
				//capture any events from filling the tile
				BoardChangeEvent event = currentBoard.getTileAt(getTileX(), getTileY()).onFill();
				//if event present
				if(event != null){
					//fire the change
					currentBoard.fireChange(event);
				}
			}
		}
		//providing input to interact
		else if(interaction){
			//calculate the spot of the interacting tile using the last direction
			Point inter = pointingTo(getLastDirection(), currentBoard);
			//look for item tile first
			boolean interacted = tileInteract(inter, currentBoard, input);
			if(!interacted){
				//try interacting
				interacted = entityInteract(inter, currentBoard, input);
			}
			//see if there is a possiblity two away
			if(!interacted){
				Point farInter = pointingTo(inter.x, inter.y, getLastDirection(), currentBoard);
				//try interacting
				entityInteract(farInter, currentBoard, input);
			}
		}
		//providing input to move
		else if(!dir.equals(Direction.NONE)){
			//calculate a tile destination using the input direction
			Point dest = pointingTo(dir, currentBoard);
			//if a valid point
			if(currentBoard.isInBounds(dest.x, dest.y)){
				//only if possible to move
				if(move(dir, currentBoard, dest)){
					//start transition
					transitionDir = dir;
				}
			}
		}
	}
	private boolean entityInteract(Point target, Board currentBoard, InputTracker input){
		boolean success = false;
		//if a valid point
		if(currentBoard.isInBounds(target.x, target.y)){
			Tile to = currentBoard.getTileAt(target.x, target.y);
			//if someone is there
			if(to.isFilled()){
				//interact
				((NPC) to.getOccupent()).onInteract(currentBoard, this, input);
				success = true;
			}
		}
		return success;
	}
	private boolean tileInteract(Point target, Board currentBoard, InputTracker input){
		boolean success = false;
		//if a valid point
		if(currentBoard.isInBounds(target.x, target.y)){
			Tile to = currentBoard.getTileAt(target.x, target.y);
			//if an item tile
			if(to instanceof ItemTile){
				ItemTile it = (ItemTile) to;
				//interact
				it.onInteract();
				currentBoard.activateItem(target.x, target.y, InventoryItem.getItems()[it.getItem()].getName(), input);
				success = true;
			}
		}
		return success;
	}
	private Point pointingTo(Direction dir, Board currentBoard){ return pointingTo(getTileX(), getTileY(), dir, currentBoard); }
	private Point pointingTo(int ox, int oy, Direction dir, Board currentBoard){
		//calculate a tile destination
		int dx = ox;
		int dy = oy;
		switch(dir){
			case UP: dy--; break;
			case DOWN: dy++; break;
			case LEFT: dx--; break;
			case RIGHT: dx++; break;
			case NONE: break;
		}
		return new Point(dx, dy);
	}
	private void transitionX(int speedDir, int bWidth, int cx){
		//only care if centered
		if(getRealX() == cx){
			//if moving right
			if(speedDir == 1){
				//locked if not at end of the board
				xLocked = xOffset != bWidth - TileConstants.FRAME_WIDTH;
			}
			//moving left
			else{
				//locked if not at the start of the board
				xLocked = xOffset != 0;
			}
		}
		//only if not locked in the x
		if(!xLocked){
			//move the sprite
			realXMove(speedDir);
		}
		else{
			//move the background
			xOffset += speedDir;
		}
	}
	private void transitionY(int speedDir, int bHeight, int cy){
		//only care if centered
		if(getRealY() == cy){
			//if moving down
			if(speedDir == 1){
				//locked if not at end of the board
				yLocked = yOffset != bHeight - TileConstants.FRAME_HEIGHT;
			}
			//moving up
			else{
				//locked if not at the start of the board
				yLocked = yOffset != 0;
			}
		}
		//only if not locked in the y
		if(!yLocked){
			//move the sprite
			realYMove(speedDir);
		}
		else{
			//move the background
			yOffset += speedDir;
		}
	}
	public int getXOffset(){return xOffset;}
	public int getYOffset(){return yOffset;}
	@Override
	public int getSpeed(boolean isRunning, boolean isFastBoard){
		if(isFastBoard){
			return isRunning ? TileConstants.FAST_FAST_BOARD_SPEED : TileConstants.FAST_BOARD_SPEED;
		}
		else{
			return isRunning ? TileConstants.FAST_PLAYER_SPEED : TileConstants.PLAYER_SPEED;
		}
	}
}
