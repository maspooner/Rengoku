package org.spooner.java.TileGame.Speech;

public interface Speakable {
	public abstract Speakable next();
	public abstract boolean hasNext();
	public abstract void reset();
	public abstract void revert();
	public abstract void begin();
}
