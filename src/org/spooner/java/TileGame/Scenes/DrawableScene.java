package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;

import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;

public interface DrawableScene {
	//methods
	public abstract void onBegin();
	public abstract void draw(Graphics g, InputTracker input);
	public abstract void tick(InputTracker input);
	public abstract boolean isDone();
	public abstract void onEnd();
	public abstract GameState endState();
}
