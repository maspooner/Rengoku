package org.spooner.java.TileGame.Scenes;

import org.spooner.java.TileGame.Direction;

public class BoardChangeTile extends Tile{
	//members
	private String board;
	private int startID;
	private Direction face;
	//constructor
	public BoardChangeTile(int id, String board, int foreID, int startID, Direction face){
		super(id, new boolean[]{false, false, false, false}, foreID);
		this.board = board;
		this.startID = startID;
		this.face = face;
	}
	//methods
	public String getBoard(){ return board; }
	public int getStartID(){ return startID; }
	public Direction getFace(){ return face; }
	@Override
	public BoardChangeEvent onFill() {
		//fire change board event
		return new BoardChangeEvent(this);
	}
}
