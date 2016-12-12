package org.spooner.java.TileGame;

import java.awt.image.BufferedImage;

public class DirectionalAnimation extends Animation{
	//members
	private Animation[] directionalFrames;
	//constructors
	public DirectionalAnimation(String path){
		super(path);
	}
	//methods
	public BufferedImage nextFrame(Direction direction, int rate){
		return directionalFrames[direction.ordinal()].nextFrame(rate);
	}
	@Override
	public void loadAnimation(String path){
		directionalFrames = new Animation[4];
		//iterate through up down left right
		for(int i = 0; i < 4; i++){
			String dirPath = path + i + "/";
			directionalFrames[i] = new Animation(dirPath);
		}
	}
	@Override
	public int getWidth() { return directionalFrames[0].getWidth(); }
	@Override
	public int getHeight() { return directionalFrames[0].getHeight(); }
}
