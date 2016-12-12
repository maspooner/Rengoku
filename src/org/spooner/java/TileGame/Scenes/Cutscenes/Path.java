package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Point;

public class Path {
	//members
	private Point[] points;
	private int curr;
	private boolean isLoop;
	//constructors
	protected Path(Point[] points, boolean isLoop){
		this.points = points;
		curr = 0;
		this.isLoop = isLoop;
	}
	//methods
	protected Point get(int i){ return points[i]; }
	protected void reset(){ curr = 0; }
	protected int points(){ return points.length; }
	protected int index(){ return curr; }
	protected Point currDest(){
		return points[curr];
	}
	protected boolean hasNext(){
		return isLoop || curr + 1 < points.length;
	}
	protected void next(){
		curr++;
		if(isLoop && curr == points.length)
			curr = 0;
	}
	
}
