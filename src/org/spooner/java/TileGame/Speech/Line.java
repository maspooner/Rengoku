package org.spooner.java.TileGame.Speech;

public class Line implements Speakable{
	//members
	private String line;
	private String speaker;
	private boolean isSaid;
	//constructors
	public Line(String speaker, String line){
		this.line = line;
		this.speaker = speaker;
		this.isSaid = false;
	}
	//methods
	public String getLine(){ return line; }
	public String getSpeaker(){ return speaker; }
	@Override
	public Speakable next() {
		isSaid = true;
		return this;
	}
	@Override
	public boolean hasNext() { return !isSaid; }
	@Override
	public void reset() {
		isSaid = false;
	}
	@Override
	public void revert() {
		isSaid = false;
	}
	@Override
	public void begin() { }
}
