package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.TileConstants;

public abstract class Prop {
	//members
	private boolean drawing;
	private int x;
	private int y;
	private int dx;
	private int dy;
	private double sx;
	private double sy;
	//constructors
	protected Prop(int x, int y){
		this.x = x;
		this.y = y;
		this.sx = 1.0;
		this.sy = 1.0;
		this.dx = -1;
		this.dy = -1;
		this.drawing = true;
	}
	//methods
	protected void draw(Graphics g){
		//only if visible
		if(drawing){
			//draw the stop animation if no destination
			g.drawImage(getFrame(), x, y, getWidth(), getHeight(), null);
		}
	}
	protected boolean atDestination(){ return dx == -1 && dy == -1; }
	protected int getX(){ return x; }
	protected int getY(){ return y; }
	protected double getSX(){ return sx; }
	protected double getSY(){ return sy; }
	protected void setX(int x) { this.x = x; }
	protected void setY(int y){ this.y = y; }
	protected void setSX(double sx){ this.sx = sx; }
	protected void setSY(double sy){ this.sy = sy; }
	protected abstract BufferedImage getFrame();
	protected int getWidth(){
		return (int) (TileConstants.PLAYER_WIDTH * TileConstants.TILE_SCALE * sx);
	}
	protected int getHeight(){
		return (int) (TileConstants.PLAYER_HEIGHT * TileConstants.TILE_SCALE * sy);
	}
	protected void setDrawing(boolean drawing){ this.drawing = drawing; }
	protected boolean isDrawing(){ return drawing; }
	protected boolean moveTo(int nx, int ny){
		boolean there = false;
		//update destination
		dx = nx;
		dy = ny;
		//if either x or y is not at the destination, move there
		if(x != dx)
			x += dx > x ? 1 : -1;
		if(y != dy)
			y += dy < y ? -1 : 1;
		//there if both match up
		there = dx == x && dy == y;
		//if there, reset destinations and animation
		if(there){
			dx = -1;
			dy = -1;
		}
		return there;
	}
	protected boolean scaleTo(double nx, double ny, double rate){
		//new scale larger than old? increase by scale
		if(nx > sx){
			sx += rate * nx;
			if(sx > nx) sx = nx;
		}
		else{
			sx -= rate * nx;
			if(sx < nx) sx = nx;
		}
		if(ny > sy){
			sy += rate * ny;
			if(sy > ny) sy = ny;
		}
		else{
			sy -= rate * ny;
			if(sy < ny) sy = ny;
		}
		//there if scales are equal
		return sx == nx && sy == ny;
	}
	protected boolean followPath(Path p){
		Point current = p.currDest();
		//finished moving to destination
		if(moveTo(current.x, current.y)){
			//go to next point in path
			if(p.hasNext()){
				p.next();
			}
			else{
				//done with all of path
				return true;
			}
		}
		return false;
	}
	
}
