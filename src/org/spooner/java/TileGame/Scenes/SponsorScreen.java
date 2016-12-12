package org.spooner.java.TileGame.Scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Animation;
import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileConstants;

public class SponsorScreen implements DrawableScene{
	//members
	private Color background;
	private Animation sponsorAni;
	private int playTicks;
	private int currentTick;
	private int animeRate;
	private boolean isFast;
	//constructors
	public SponsorScreen(Color background, Animation sponserAni, int playTicks, int animeRate){
		this.background = background;
		this.sponsorAni = sponserAni;
		this.playTicks = playTicks;
		this.animeRate = animeRate;
		currentTick = 0;
		isFast = false;
	}
	//methods
	@Override
	public void draw(Graphics g, InputTracker input){
		//draw background
		g.setColor(background);
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
		//draw the animation in the center
		BufferedImage frame = sponsorAni.nextFrame(animeRate);
		g.drawImage(frame, TileConstants.FRAME_WIDTH / 2 - frame.getWidth() / 2,
				TileConstants.FRAME_HEIGHT / 2 - frame.getHeight() / 2, null);
	}
	@Override
	public void tick(InputTracker input){
		//wanting to go fast and not going fast
		if(input.consumeInteract() && !isFast){
			//go fast by cutting play ticks
			isFast = true;
			playTicks /= TileConstants.SKIP_REDUCTION;
		}
		currentTick++;
	}
	@Override
	public void onBegin() {
		//nothing special
	}
	@Override
	public void onEnd() {
		//nothing special
	}
	@Override
	public boolean isDone() {
		//done if past buffer
		return currentTick >= playTicks;
	}
	@Override
	public GameState endState() {
		//they don't lead into anything
		return null;
	}
}
