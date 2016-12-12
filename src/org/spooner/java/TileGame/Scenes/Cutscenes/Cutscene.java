package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Graphics;

import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.Scenes.DrawableScene;

public abstract class Cutscene implements DrawableScene{
	//members
	private int waitLimit;
	private int waitBuff;
	private int currAction;
	private int panX;
	private int panY;
	private boolean done;
	//constructors
	public Cutscene(){
		waitLimit = -1;
		waitBuff = 0;
		currAction = 0;
		panX = 0;
		panY = 0;
		done = false;
	}
	//methods
	protected boolean isWaiting(){ return waitLimit > -1; }
	protected void finish(){ done = true; }
	protected int getCurrAction(){ return currAction; }
	protected void nextAction(){ currAction++; }
	protected boolean delay(int ticks){
		//already waiting
		if(isWaiting()){
			//up the count
			waitBuff++;
			//done waiting
			if(waitBuff >= waitLimit){
				waitLimit = -1;
				waitBuff = 0;
				return true;
			}
		}
		else{
			waitLimit = ticks;
		}
		return false;
	}
	protected boolean panTo(int nx, int ny){
		if(panX != nx)
			panX += nx > panX ? 1 : -1;
		if(panY != ny)
			panY += ny < panY ? -1 : 1;
		if(panX == nx && panY == ny){
			return true;
		}
		return false;
	}
	protected void translate(Graphics g){
		//translate based on pan
		g.translate(- panX, - panY);
	}
	protected void translateBack(Graphics g){
		//translate based on pan
		g.translate(panX, panY);
	}
	@Override
	public boolean isDone() {
		//done if done with last action
		return done;
	}
	@Override
	public void onBegin() {
		//do nothing
	}
	@Override
	public void onEnd() {
		//do nothing
	}
	@Override
	public GameState endState() {
		//does nothing
		return null;
	}
}
