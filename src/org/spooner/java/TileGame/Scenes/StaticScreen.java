package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;

public class StaticScreen implements DrawableScene{
	//members
	private BouncingBackground bb;
	private BufferedImage message;
	private boolean done;
	//constructors
	public StaticScreen(String back, String image){
		bb = new BouncingBackground(back, TileConstants.BOUNCING_SCALE, TileConstants.BOUNCING_SPEED);
		done = false;
		message = TileIO.loadImage(TileConstants.SPRITE_PATH + image +".png");
	}
	//methods
	@Override
	public void onBegin() { }
	@Override
	public void draw(Graphics g, InputTracker input) {
		bb.draw(g);
		g.drawImage(message, TileConstants.FRAME_WIDTH / 2 - message.getWidth() / 2, TileConstants.FRAME_HEIGHT / 2 - message.getHeight() / 2, null);
	}
	@Override
	public void tick(InputTracker input) {
		bb.tick();
		if(input.consumeHubbing()){
			TileAudio.playSound("buzz");
		}
		done = input.consumeInteract() || input.consumeCancel();
	}
	@Override
	public boolean isDone() {
		return done;
	}
	@Override
	public void onEnd() { }
	@Override
	public GameState endState() {
		return GameState.TITLE;
	}
}
