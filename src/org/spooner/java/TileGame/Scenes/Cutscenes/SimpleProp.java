package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;

public class SimpleProp extends Prop{
	//members
	private BufferedImage frame;
	//constructors
	protected SimpleProp(String spriteAlias, int x, int y){
		super(x, y);
		frame = TileIO.loadImage(TileConstants.SPRITE_PATH + spriteAlias + ".png");
	}
	//methods
	@Override
	protected BufferedImage getFrame(){ return frame; }
	@Override
	protected int getWidth() {
		return (int) (frame.getWidth() * TileConstants.TILE_SCALE * getSX());
	}
	@Override
	protected int getHeight() {
		return (int) (frame.getHeight() * TileConstants.TILE_SCALE * getSY());
	}
}
