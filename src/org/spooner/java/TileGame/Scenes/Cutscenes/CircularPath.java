package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Point;

public class CircularPath extends Path {
	//members
	private static final int START_POINTS = 120;
	//constructors
	protected CircularPath(int radius, int cx, int cy, int theta, boolean isLoop){
		super(calculatePoints(radius, cx, cy, theta), isLoop);
	}
	//methods
	private static Point[] calculatePoints(int radius, int cx, int cy, int theta){
		//y axis is flipped
		theta = 360 - theta;
		int degTraveled = 0;
		Point[] points = new Point[START_POINTS];
		int i = 0;
		do{
			//current angle
			int angle = theta + degTraveled;
			//convert to rectangular grid
			int dx = (int) (radius * Math.cos(Math.toRadians(angle)));
			int dy = (int) (radius * Math.sin(Math.toRadians(angle)));
			points[i] = new Point(dx + cx, dy + cy);
			i++;
			degTraveled += 360 / START_POINTS;
		}
		while(i < points.length);
		return points;
	}
}
