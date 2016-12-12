package org.spooner.java.TileGame.Entities;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileGame;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.BoardState;
import org.spooner.java.TileGame.Speech.ChoiceLine;
import org.spooner.java.TileGame.Speech.Conversation;
import org.spooner.java.TileGame.Speech.Line;
import org.spooner.java.TileGame.Speech.Speakable;

public abstract class NPC extends Entity{
	//members
	private String name;
	private int bufferTicks;
	private Point destination;
	private Direction direction;
	private Point startPoint;
	private Point limitPoint;
	private Conversation[] conversations;
	private Conversation currCon;
	//constructors
	public NPC(String name, String spriteAlias, Conversation[] conversations, Point startPoint, Point limitPoint){
		//face down as default
		super(TileConstants.PERSON_SPRITE_PATH + spriteAlias + "/", startPoint, Direction.DOWN);
		this.name = name;
		this.conversations = conversations;
		this.currCon = null;
		this.destination = null;
		this.direction = Direction.NONE;
		this.bufferTicks = 0;
		this.startPoint = startPoint;
		this.limitPoint = limitPoint;
	}
	//methods
	public abstract Point calculateDestination();
	public abstract Direction calculateDirection(Point destination);
	public abstract boolean directionMove(Direction dir, Point dest, int speedDir);
	public String getName() { return name; }
	public Point getStart(){ return startPoint; }
	public Point getLimit(){ return limitPoint; }
	public void findConversation(){
		//start at bottom for most recent conversation
		boolean found = false;
		for(int i = conversations.length - 1; i >= 0 && !found; i--){
			if(conversations[i].canBegin()){
				conversations[i].begin();
				//set the current con
				currCon = conversations[i];
				found = true;
			}
		}
	}
	public ChoiceLine nextChoice(){
		Speakable speak = currCon.next();
		return (ChoiceLine) speak;
	}
	public void reset(){
		for(Conversation c : conversations)
			c.reset();
	}
	public boolean hasNextChoice(){
		Speakable speak = currCon.peekNext();
		//has next if instanceof choice line
		return speak instanceof ChoiceLine;
	}
	public boolean hasNextLine(){
		Speakable speak = currCon.peekNext();
		//has next if instanceof line
		return speak instanceof Line;
	}
	public Line nextLine(){
		Speakable speak = currCon.next();
		return (Line) speak;
	}
	public boolean hasNext(){
		boolean hasNext = false;
		if(currCon != null){
			hasNext = currCon.hasNext();
		}
		return hasNext;
	}
	public void onInteract(Board currentBoard, Player p, InputTracker input){
		//face the player
		face(p);
		//bring up the menu
		currentBoard.showNPCMenu(this, input);
	}
	public void draw(Graphics g, int viewx, int viewy, BoardState state){
		//offset due to the height of character
		int y = getRealY() - getHeight();
		//move in a direction only if movement is allowed
		Direction dir = state.equals(BoardState.MOVEMENT) ? direction : Direction.NONE;
		//note: views are subtracted to scroll in the opposite direction
		g.drawImage(getFrame(dir, state), getRealX() - viewx, 
				y - viewy, getWidth(), getHeight(), null);
	}
	private BufferedImage getFrame(Direction direction, BoardState state){ return getFrame(direction, state, false); }
	public void tick(Board currentBoard){
		//no destination
		if(destination == null){
			//if is moving with no destination, it is turning
			if(!direction.equals(Direction.NONE)){
				//stop moving
				direction = Direction.NONE;
			}
			else{
				//count the tick
				bufferTicks++;
				//if buffer has been reached
				if(bufferTicks >= TileConstants.MOVEMENT_BUFFER){
					//reset the buffer
					bufferTicks = 0;
					//test for action 1<=num<=100
					int action = TileGame.getRandomInt(100) + 1;
					if(action > TileConstants.CHANCE_TO_NOTHING){
						//new random number
						action = TileGame.getRandomInt(100) + 1;
						//if turning
						if(action <= TileConstants.CHANCE_TO_TURN){
							//get random direction
							int dir = TileGame.getRandomInt(4);
							Direction newDirection = Direction.getDirection(dir);
							//only change direction if it is not the same
							if(!newDirection.equals(direction)){
								direction = newDirection;
							}
						}
						//if moving
						else{
							//calculate a direction and destination
							destination = calculateDestination();
							direction = calculateDirection(destination);
							//if not in bounds
							if(!currentBoard.isInBounds(destination.x, destination.y)){
								//reset
								destination = null;
								direction = Direction.NONE;
							}
							//if not successfully moved
							else if(!move(direction, currentBoard, destination)){
								//reset
								destination = null;
								direction = Direction.NONE;
							}
						}
					}
				}
				//else, do nothing
			}
		}
		//has a destination to move to
		else{
			int speedDir = direction.equals(Direction.DOWN) || direction.equals(Direction.RIGHT) ? 1 : -1;
			//move in the direction and see if there
			boolean isThere = directionMove(direction, destination, speedDir);
			//if there, find new destination
			if(isThere)
				destination = null;
		}
	}
	public String[] itemsGotten(){
		return currCon.itemsGotten();
	}
	public int cutscene(){
		return currCon.cutscene();
	}
	public boolean reloadsBoard(){
		return currCon.reloadsBoard();
	}
	@Override
	public int getSpeed(boolean isRunning, boolean isFastBoard){
		return TileConstants.NPC_SPEED;
	}
	public int getSpeed(){ return getSpeed(false, false); }
}
