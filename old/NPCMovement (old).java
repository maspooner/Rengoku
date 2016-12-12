package org.spooner.java.TileGame;

import java.awt.Point;

public abstract class NPCMovement {
	//members
	private int x;
	private int y;
	private int bufferTicks;
	private Point startPoint;
	private Point limitPoint;
	private Point destination;
	private Direction direction;
	//constructors
	public NPCMovement(Point startPoint, Point limitPoint){
		this.x = startPoint.x;
		this.y = startPoint.y;
		this.startPoint=startPoint;
		this.limitPoint=limitPoint;
		destination=null;
		direction=Direction.NONE;
		bufferTicks=0;
	}
	//methods
	public abstract Direction calculateDirection();
	public abstract Point calculateDestination();
	public void tick(){
		//TODO add check to make sure does not turn to direction already facing
		//no destination
		if(destination==null){
			//if is moving with no destination, it is turning
			if(!direction.equals(Direction.NONE)){
				//stop moving
				direction=Direction.NONE;
				System.out.println("STOPPED MOVING");
			}
			else{
				//count the tick
				bufferTicks++;
				//if buffer has been reached
				if(bufferTicks>=TileConstants.MOVEMENT_BUFFER){
					bufferTicks=0;
					//TODO uncomment
					destination=calculateDestination();
					direction=calculateDirection();
//					//test for turning 1<=num<=100
//					int action=getRandomInt(100)+1;
//					//if turning (favored)
//					if(action<=TileConstants.CHANCE_TO_TURN){
//						//get random direction
//						int dir=getRandomInt(4);
//						direction=Direction.getDirection(dir);
//						System.out.println("TURNED");
//					}
//					else{
//						//new random number, test for moving 1<=num<=100
//						action=getRandomInt(100-1)+1;
//						//if moving
//						if(action<=TileConstants.CHANCE_TO_MOVE){
//							destination=calculateDestination();
//							System.out.println(getDestination().x);
//							System.out.println(getDestination().y);
//							direction=calculateDirection();
//							System.out.println("DESTINATION SET (dir ="+getDirection());
//						}
//						//else, do nothing
//					}
				}
				//else, do nothing
			}
		}
		//has a destination to move to
		else{
			
			move();
			//if reached destination
			if(destination.x==x && destination.y==y)
				destination=null;
		}
	}
	public Direction getDirection(){return direction;}
	private int getRandomInt(int max){return (int) (Math.random() * max);}//TODO works?
	public boolean getRandomBoolean(){return getRandomInt(2) == 1;} //TODO works?
	public Point getDestination(){return destination;}
	public Point getStart(){return startPoint;}
	public Point getLimit(){return limitPoint;}
	public int getX(){ return x; }
	public int getY(){ return y; }
	public void move(){
		int speed = TileConstants.NPC_SPEED;
		switch(getDirection()){
		case UP:
			//negated speed
			y -= speed;
			//if overshot
			if(y<destination.y)
				y = destination.y;
			break;
		case DOWN:
			y += speed;
			//if overshot
			if(y>destination.y)
				y = destination.y;
			break;
		case LEFT:
			//negated speed
			x -= speed;
			//if overshot
			if(x<destination.x)
				x = destination.x;
			break;
		case RIGHT:
			x += speed;
			//if overshot
			if(x>destination.x)
				x = destination.x;
			break;
		case NONE:
			//TODO
			break;
		}
	}
}
