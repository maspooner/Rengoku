package org.spooner.java.TileGame;

import java.awt.Color;
import java.awt.Graphics;

public class Transition extends org.spooner.java.MRL.Transition{
	private static final float START_IN = 1f;
	private static final float START_OUT = 0f;
	private static final float END_IN = 0f;
	private static final float END_OUT = 1f;
	//constructor
	public Transition(float fadeSpeed, float initial){
		super(fadeSpeed, initial);
	}
	public Transition(float fadeSpeed){
		//full transperent
		this(fadeSpeed, START_OUT);
	}
	//methods
	public void draw(Graphics g){
		//active transition
		if(isActive()){
			if(isFadingIn())
				//fade in or out
				fadeIn();
			else
				fadeOut();
		}
		//set the color to black with the transparency
		g.setColor(new Color(0f, 0f, 0f, getAlpha()));
		//draw a rectangle
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
	}
	//\/ these four overriden to do the reverse of parent
	@Override
	public void lerpIn(float dt){
		lerp(START_IN, END_IN, dt * getFadeSpeed());
	}
	@Override
	public void lerpOut(float dt) {
		lerp(START_OUT, END_OUT, dt * getFadeSpeed());
	}
	@Override
	public boolean reachedIn() {
		return getAlpha() <= END_IN;
	}
	@Override
	public boolean reachedOut() {
		return getAlpha() >= END_OUT;
	}
}
