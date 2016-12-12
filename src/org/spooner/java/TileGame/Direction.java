package org.spooner.java.TileGame;

public enum Direction {
	//enum
	UP(0),
	DOWN(1),
	LEFT(2),
	RIGHT(3),
	NONE(-1);
	//members
	private int id;
	//constructors
	Direction(int id){
		this.id=id;
	}
	//methods
	public int toIndex() { return id; }
	public boolean isHorizontal(){
		return equals(LEFT) || equals(RIGHT);
	}
	public static Direction getDirection(int id){
		for(Direction d : values()){
			if(d.id==id)
				return d;
		}
		return NONE;
	}
	public Direction opposite(){
		Direction oppo = Direction.NONE;
		switch(this){
			case UP: oppo = DOWN; break;
			case DOWN: oppo = UP; break;
			case LEFT: oppo = RIGHT; break;
			case RIGHT: oppo = LEFT; break;
			default: break;
		}
		return oppo;
	}
}
