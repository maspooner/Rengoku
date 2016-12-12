package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;

public class BouncingBackground {
	//members
	private int scale;
	private int speed;
	private int panX;
	private int panY;
	private int endX;
	private int endY;
	private boolean down;
	private BufferedImage background;
	//constructors
	protected BouncingBackground(String path, int scale, int speed){
		this.speed = speed;
		this.scale = scale;
		panX = 0;
		panY = 0;
		background = TileIO.loadImage(TileConstants.SPRITE_PATH + path + ".png");
		endX = getWidth() - TileConstants.FRAME_WIDTH;
		endY = getHeight() - TileConstants.FRAME_HEIGHT;
		down = true;
	}
	//methods
	private int getWidth(){
		return (int) (background.getWidth() * scale);
	}
	private int getHeight(){
		return (int) (background.getHeight() * scale);
	}
	protected void draw(Graphics g){
		g.drawImage(background, - panX, - panY, getWidth(), getHeight(), null);
	}
	protected void tick(){
		if(down){
			//move the pan [speed] pixels based on scale
			panX += speed;
			panY += speed;
			//if overshot, compensate
			if(panX > endX) panX = endX;
			if(panY > endY) panY = endY;
			//at end, reverse
			if(panX == endX && panY == endY){
				down = false;
			}
		}
		else{
			//move the pan [speed] pixels based on scale
			panX -= speed;
			panY -= speed;
			//if overshot, compensate
			if(panX < 0) panX = 0;
			if(panY < 0) panY = 0;
			//at end, reverse
			if(panX == 0 && panY == 0){
				down = true;
			}
		}
	}
}
