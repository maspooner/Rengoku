package org.spooner.java.TileGame;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MovementHandler extends AbstractMovement implements KeyListener{
	//members
	private int xOffset;
	private int yOffset;
	private boolean isUp;
	private boolean isDown;
	private boolean isLeft;
	private boolean isRight;
	//constructors
	public MovementHandler(){
		//NOTE: start \/ is null so board can set position
		super(null, TileConstants.PLAYER_SPEED);
		//TODO
		isUp=false;
		isDown=false;
		isLeft=false;
		isRight=false;
	}
	//methods
	public void setXOffset(int xOffset){this.xOffset=xOffset;}
	public void setYOffset(int yOffset){this.yOffset=yOffset;}
	public int getXOffset(){return xOffset;}
	public int getYOffset(){return yOffset;}
	@Override
	public Direction getDirection(){
		//favors up/down over left/right
		if(isUp) return Direction.UP;
		if(isDown) return Direction.DOWN;
		if(isLeft) return Direction.LEFT;
		if(isRight) return Direction.RIGHT;
		return Direction.NONE;
	}
//	public void move(Board board, Point hitUpLeft, Point hitDownRight){
//		//TODO split up method?
//		//TODO can up/down against wall, can't left/right
//		Point center=TileConstants.getCenter();
//		int tWidth=board.getWidth()*TileConstants.TILE_SIZE;
//		int tHeight=board.getHeight()*TileConstants.TILE_SIZE;
//		int move=getSpeed();
//		if(isUp){
//			//check the top left and right points of contact for tile colision [speed] above
//			// - move for up movement
//			int yRawContact=hitUpLeft.y-move-yOffset;
//			//left at UpLeft x, UpLeft y
//			Tile checkLeft=board.getTileAt(hitUpLeft.x-xOffset, yRawContact);
//			//right at DownRight x, UpLeft y
//			Tile checkRight=board.getTileAt(hitDownRight.x-xOffset, yRawContact);
//			//if tiles not tangible
//			if(!checkLeft.isTangible() || !checkRight.isTangible()){
//				//get exact position of tile
//				int yFineContact=((int) yRawContact/TileConstants.TILE_SIZE)*TileConstants.TILE_SIZE;
//				//add a tile size for testing bottom of tile
//				yFineContact+=TileConstants.TILE_SIZE;
//				//affect move (no adjusting needed)
//				move=hitUpLeft.y-yFineContact-yOffset;
//			}
//			moveUp(center.y, move);
//		}
//		if(isDown){
//			//check bottom left and right points of contact for colision [speed] bellow
//			// + for down movement
//			int yRawContact=hitDownRight.y+move-yOffset;
//			//left at UpLeft x, DownRight y
//			Tile checkLeft=board.getTileAt(hitUpLeft.x-xOffset, yRawContact);
//			//right at DownRight x, DownRight y
//			Tile checkRight=board.getTileAt(hitDownRight.x-xOffset, yRawContact);
//			//check tangilbility
//			if(!checkLeft.isTangible() || !checkRight.isTangible()){
//				//get exact position of tile
//				int yFineContact=((int) yRawContact/TileConstants.TILE_SIZE)*TileConstants.TILE_SIZE;
//				//no addition for testing top of tile
//				//affect move -1 so no glitching
//				move=yFineContact-hitDownRight.y+yOffset-1;
//			}
//			moveDown(center.y, tHeight, move);
//		}
//		if(isLeft){
//			//check left up and down points of contact for colision [speed] to the left
//			// - for left movement
//			int xRawContact=hitUpLeft.x-move-xOffset;
//			//up at UpLeft x, UpLeft y
//			Tile checkUp=board.getTileAt(xRawContact, hitUpLeft.y-yOffset);
//			//down at UpLeft x, DownRight y
//			Tile checkDown=board.getTileAt(xRawContact, hitDownRight.y-yOffset);
//			//check tangilbility
//			if(!checkUp.isTangible() || !checkDown.isTangible()){
//				//get exact position of tile
//				int xFineContact=((int) xRawContact/TileConstants.TILE_SIZE)*TileConstants.TILE_SIZE;
//				//add a tile size for testing right of tile
//				xFineContact+=TileConstants.TILE_SIZE;
//				//affect move (no adjusting needed)
//				move=hitUpLeft.x-xFineContact-xOffset;
//			}
//			moveLeft(center.x, move);
//		}
//		if(isRight){
//			//check right up and down points of contact for colision [speed] to the right
//			// + for right movement
//			int xRawContact=hitDownRight.x+move-xOffset;
//			//up at DownRight x, UpLeft y
//			Tile checkUp=board.getTileAt(xRawContact, hitUpLeft.y-yOffset);
//			//down at DownRight x, DownRight y
//			Tile checkDown=board.getTileAt(xRawContact, hitDownRight.y-yOffset);
//			//check tangilbility
//			if(!checkUp.isTangible() || !checkDown.isTangible()){
//				//get exact position of tile
//				int xFineContact=((int) xRawContact/TileConstants.TILE_SIZE)*TileConstants.TILE_SIZE;
//				//no addition for testing right of tile
//				//affect move -1 so no glitching
//				move=xFineContact-hitDownRight.x+xOffset-1;
//			}
//			moveRight(center.x, tWidth, move);
//		}
//	}
	private void moveUp(int yCenter, int yMove){
		//if at a wall
		if(yMove==0) return;
		int stopPosition=0;
		//if reached up side, and background not stationary
		if(yMove-yOffset<0 && yOffset!=stopPosition){
			//adjust yMove to accommodate
			yMove+=yOffset;
			//make y offset stop
			yOffset=stopPosition;
			//use leftover movement to move y (negated)
			incrementY(-yMove);
		}
		//if centered
		else if(getY()==yCenter){
			//offset background, do not move
			yOffset+=yMove;
		}
		//if would pass center, and background not stationary
		else if(getY()-yMove<yCenter && yOffset!=stopPosition){
			//adjust yMove to accommodate
			yMove-=yCenter-getY();
			//make y the center
			setY(yCenter);
			//use leftover movement to offset background
			yOffset+=yMove;
		}
		else{
			//do not adjust offset, just move (negated)
			incrementY(-yMove);
		}
	}
	private void moveDown(int yCenter, int tHeight, int yMove){
		//if at a wall
		if(yMove==0) return;
		int stopPosition=TileConstants.FRAME_HEIGHT-tHeight;
		//if reached down side, and background not stationary
		if(TileConstants.FRAME_HEIGHT-yOffset>tHeight && yOffset!=stopPosition){
			//adjust yMove to accommodate
			yMove+=stopPosition-yOffset;
			//make y offset stop
			yOffset=stopPosition;
			//use leftover movement to move y
			incrementY(yMove);
		}
		//if centered
		else if(getY()==yCenter){
			//offset background, do not move
			yOffset-=yMove;
		}
		//if would pass center, and background not stationary
		else if(yMove+getY()>yCenter && yOffset!=stopPosition){
			//adjust yMove to accommodate
			yMove-=yCenter-getY();
			//make y the center
			setY(yCenter);
			//use leftover movement to offset background
			yOffset-=yMove;
		}
		else{
			//do not adjust offset, just move
			incrementY(yMove);
		}
	}
	private void moveLeft(int xCenter, int xMove){
		//if at a wall
		if(xMove==0) return;
		int stopPosition=0;
		//if reached left side, and background not stationary
		if(xMove-xOffset<0 && xOffset!=stopPosition){
			//adjust xMove to accommodate
			xMove+=xOffset;
			//make x offset stop
			xOffset=stopPosition;
			//use leftover movement to move x (negated)
			incrementX(-xMove);
		}
		//if centered
		else if(getX()==xCenter){
			//offset background, do not move
			xOffset+=xMove;
		}
		//if would pass center, and background not stationary
		else if(getX()-xMove<xCenter && xOffset!=stopPosition){
			//adjust xMove to accommodate
			xMove-=xCenter-getX();
			//make x the center
			setX(xCenter);
			//use leftover movement to offset background
			xOffset+=xMove;
		}
		else{
			//do not adjust offset, just move (negated)
			incrementX(-xMove);
		}
	}
	private void moveRight(int xCenter, int tWidth, int xMove){
		//if at a wall
		if(xMove==0) return;
		int stopPosition=TileConstants.FRAME_WIDTH-tWidth;
		//if reached right side, and background not stationary
		if(TileConstants.FRAME_WIDTH-xOffset>tWidth && xOffset!=stopPosition){
			//adjust xMove to accommodate
			xMove-=stopPosition-xOffset;
			//make x offset stop
			xOffset=stopPosition;
			//use leftover movement to move x
			incrementX(xMove);
		}
		//if centered
		else if(getX()==xCenter){
			//offset background, do not move
			xOffset-=xMove;
		}
		//if would pass center, and background not stationary
		else if(xMove+getX()>xCenter && xOffset!=stopPosition){
			//adjust xMove to accommodate
			xMove-=xCenter-getX();
			//make x the center
			setX(xCenter);
			//use leftover movement to offset background
			xOffset-=xMove;
		}
		else{
			//do not adjust offset, just move
			incrementX(xMove);
		}
	}
	public void keyPressed(KeyEvent ke) {
		if(ke.getKeyCode()==KeyEvent.VK_UP){
			isUp=true;
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN){
			isDown=true;
		}
		if(ke.getKeyCode()==KeyEvent.VK_LEFT){
			isLeft=true;
		}
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT){
			isRight=true;
		}
	}
	public void keyReleased(KeyEvent ke) {
		if(ke.getKeyCode()==KeyEvent.VK_UP){
			isUp=false;
		}
		if(ke.getKeyCode()==KeyEvent.VK_DOWN){
			isDown=false;
		}
		if(ke.getKeyCode()==KeyEvent.VK_LEFT){
			isLeft=false;
		}
		if(ke.getKeyCode()==KeyEvent.VK_RIGHT){
			isRight=false;
		}
	}
	public void keyTyped(KeyEvent ke) {}
}
