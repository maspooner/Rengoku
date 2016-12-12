package org.spooner.java.TileGame;

import java.awt.Point;

public abstract class AbstractMovement {
	//members
	private int x;
	private int y;
	private final int SPEED;
	//constructors
	public AbstractMovement(Point pos, final int speed){
		if(pos!=null){
			x=pos.x;
			y=pos.y;
		}
		SPEED=speed;
	}
	//methods
	public abstract Direction getDirection();
	public int getX(){return x;}
	public int getY(){return y;}
	public final int getSpeed(){return SPEED;}
	public void setX(int x){this.x=x;}
	public void setY(int y){this.y=y;}
	public void incrementX(int increment){x+=increment;}
	public void incrementY(int increment){y+=increment;}
//	public boolean isCollision(Entity x, Entity y){
//		//TODO gets stuck sometimes in hit box
//		//Entity 1 (mover) variables start with X
//		//Entity 2 (collider) variables start with Y
//		Point XUL=x.getUpLeftHitBox();
//		Point XDR=x.getDownRightHitBox();
//		Point YUL=y.getUpLeftHitBox();
//		Point YDR=y.getDownRightHitBox();
//		int xMove=0;
//		int yMove=0;
//		//direction of mover
//		switch(getDirection()){
//			case UP:
//				yMove= -SPEED;
//				break;
//			case DOWN:
//				yMove= SPEED;
//				break;
//			case LEFT:
//				xMove= -SPEED;
//				break;
//			case RIGHT:
//				xMove= SPEED;
//				break;
//			case NONE: return false;
//		}
//		//initialize corners of hit boxes
//		int xUp, xLeft, xDown, xRight, yUp, yLeft, yDown, yRight;
//		if(x instanceof Player){
//			//player is affected by offset
//			Player p=(Player) x;
//			xUp=XUL.y-p.getYOffset();
//			xLeft=XUL.x-p.getXOffset();
//			xDown=XDR.y-p.getYOffset();
//			xRight=XDR.x-p.getXOffset();
//			//npc is not affected by offset
//			yUp=YUL.y;
//			yLeft=YUL.x;
//			yDown=YDR.y;
//			yRight=YDR.x;
//		}
//		else{
//			//npc is not affected by offset
//			xUp=XUL.y;
//			xLeft=XUL.x;
//			xDown=XDR.y;
//			xRight=XDR.x;
//			//player is affected by offset
//			//make player hit box wider so NPC (bigger) doesn't pass over the Player (smaller)
//			//TODO test with horizontal
//			Player p=(Player) y;
//			yUp=YUL.y-p.getYOffset();
//			yLeft=YUL.x-p.getXOffset()-TileConstants.PLAYER_HIT_SPACING;
//			yDown=YDR.y-p.getYOffset();
//			yRight=YDR.x-p.getXOffset()+TileConstants.PLAYER_HIT_SPACING;
//		}
//		System.out.println("Entity 1: "+x.getClass().getSimpleName()+" Left "+xLeft+" Right "+xRight+" Up "+xUp+" Down "+xDown);
//		System.out.println("Entity 2: "+y.getClass().getSimpleName()+" Left "+yLeft+" Right "+yRight+" Up "+yUp+" Down "+yDown);
//		//mover Left OR mover Right is in collider hit box
//		boolean isXAligned=(yRight>=xLeft+xMove && xLeft+xMove>=yLeft) || (yRight>=xRight+xMove && xRight+xMove>=yLeft);
//		//mover up OR mover down is in collider hit box
//		boolean isYAligned=(yDown>=xUp+xMove && xUp+xMove>=yUp) || (yDown>=xDown+yMove && xDown+yMove>=yUp);
//		System.out.println("x align: "+isXAligned);
//		System.out.println("y align: "+isYAligned);
//		return isXAligned && isYAligned;
//	}
}
