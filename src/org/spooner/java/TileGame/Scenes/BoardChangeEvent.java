package org.spooner.java.TileGame.Scenes;

import org.spooner.java.TileGame.Direction;

public class BoardChangeEvent {
	//members
	private String board;
	private String startID;
	private Direction face;
	//constructor
	public BoardChangeEvent(String board, String startID, Direction face){
		this.board = board;
		this.startID = startID;
		this.face = face;
	}
	public BoardChangeEvent(String board, int startID, Direction face){
		this(board, Integer.toString(startID), face);
	}
	protected BoardChangeEvent(BoardChangeTile changeTile){
		this(changeTile.getBoard(), changeTile.getStartID(), changeTile.getFace());
	}
	protected BoardChangeEvent(BoardChangeEvent event){
		this(event.board, event.startID, event.face);
	}
	public String getBoard(){ return board; }
	public String getStartID(){ return startID; }
	public Direction getFace(){ return face; }
}
