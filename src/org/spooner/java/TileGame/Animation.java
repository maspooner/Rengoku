package org.spooner.java.TileGame;

import java.awt.image.BufferedImage;

public class Animation {
	//members
	private BufferedImage[] frames;
	private int currentFrame;
	private int numFrames;
	private int currentCycles;
	//constructors
	public Animation(String path){
		reset();
		loadAnimation(path);
	}
	//methods
	public void reset(){
		currentFrame = 0;
		currentCycles = 0;
	}
	public BufferedImage nextFrame(){
		return nextFrame(TileConstants.ANIMATION_RATE);
	}
	public BufferedImage nextFrame(int rate){
		//if not one frame
		if(numFrames != 1){
			//if should switch animation
			if(currentCycles >= rate){
				currentCycles = 0;
				currentFrame++;
				//reset cycle if needed
				if(currentFrame == numFrames)
					currentFrame = 0;
			}
			else{
				//add a cycle
				currentCycles++;
			}
		}
		return frames[currentFrame];
	}
	public void loadAnimation(String path){
		frames = TileIO.loadAnimation(path);
		numFrames = frames.length;
	}
	public int getWidth(){ return frames[0].getWidth(); }
	public int getHeight(){ return frames[0].getHeight(); }
}
