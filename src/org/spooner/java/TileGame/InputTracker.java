package org.spooner.java.TileGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputTracker implements KeyListener{
	//members
	private static final int NUM_SOUNDS = 9;
	private boolean isUp;
	private boolean isDown;
	private boolean isLeft;
	private boolean isRight;
	private boolean isInteracting;
	private boolean isRunning;
	private boolean isHubbing;
	private boolean isCanceling;
	//constructor
	protected InputTracker() {
		clear();
	}
	//methods
	public void clear(){
		isUp = false;
		isDown = false;
		isLeft = false;
		isRight = false;
		isRunning = false;
		isHubbing = false;
		isCanceling = false;
		clearActions();
	}
	public void clearActions(){
		//only ones that shouldn't be opened on begin
		isInteracting = false;
		isHubbing = false;
		isCanceling = false;
	}
	public Direction calculateDirection(){
		//favors up/down over left/right
		if(isUp) return Direction.UP;
		if(isDown) return Direction.DOWN;
		if(isLeft) return Direction.LEFT;
		if(isRight) return Direction.RIGHT;
		return Direction.NONE;
	}
	public boolean consumeInteract(){
		boolean interacted = isInteracting;
		//if interacted
		if(interacted){
			//run interact ONCE
			isInteracting = false;
		}
		return interacted;
	}
	public boolean consumeHubbing(){
		boolean hub = isHubbing;
		//if opening inventory
		if(hub){
			//run hub ONCE
			isHubbing = false;
		}
		return hub;
	}
	public boolean consumeCancel(){
		boolean can = isCanceling;
		//if opening inventory
		if(can){
			//run cancel ONCE
			isCanceling = false;
		}
		return can;
	}
	public boolean isRunning(){ return isRunning; }
	@Override
	public void keyPressed(KeyEvent ke) {
		if(ke.getKeyCode()==TileConstants.KEY_UP){
			isUp=true;
		}
		if(ke.getKeyCode()==TileConstants.KEY_DOWN){
			isDown=true;
		}
		if(ke.getKeyCode()==TileConstants.KEY_LEFT){
			isLeft=true;
		}
		if(ke.getKeyCode()==TileConstants.KEY_RIGHT){
			isRight=true;
		}
		if(ke.getKeyCode()==TileConstants.KEY_RUN){
			isRunning = true;
		}
	}
	@Override
	public void keyReleased(KeyEvent ke) {
		if(ke.getKeyCode()==TileConstants.KEY_UP){
			isUp = false;
		}
		if(ke.getKeyCode()==TileConstants.KEY_DOWN){
			isDown = false;
		}
		if(ke.getKeyCode()==TileConstants.KEY_LEFT){
			isLeft = false;
		}
		if(ke.getKeyCode()==TileConstants.KEY_RIGHT){
			isRight = false;
		}
		if(ke.getKeyCode()==TileConstants.KEY_RUN){
			isRunning = false;
		}
		if(ke.getKeyCode()==TileConstants.KEY_INTERACT){
			isInteracting = true;
		}
		if(ke.getKeyCode()==TileConstants.KEY_HUB){
			isHubbing = true;
		}
		if(ke.getKeyCode()==TileConstants.KEY_CANCEL){
			isCanceling = true;
		}
		//sound board
		if(ke.getKeyCode() == KeyEvent.VK_1){
			playSound(1);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_2){
			playSound(2);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_3){
			playSound(3);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_4){
			playSound(4);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_5){
			playSound(5);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_6){
			playSound(6);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_7){
			playSound(7);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_8){
			playSound(8);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_9){
			playSound(9);
		}
		else if(ke.getKeyCode() == KeyEvent.VK_0){
			playSound(TileGame.getRandomInt(NUM_SOUNDS) + 1);
		}
	}
	@Override
	public void keyTyped(KeyEvent ke) {}
	private void playSound(int i){
		switch(i){
		case 1: TileAudio.playSound("nyan"); break;
		case 2: TileAudio.playSound("nyan3"); break;
		case 3: TileAudio.playSound("tea"); break;
		case 4: TileAudio.playSound("ghost"); break;
		case 5: TileAudio.playSound("yeah"); break;
		case 6: TileAudio.playSound("calculus"); break;
		case 7: TileAudio.playSound("frozen"); break;
		case 8: TileAudio.playSound("pac"); break;
		default: TileAudio.playSound("ech"); break;
		}
	}
}
