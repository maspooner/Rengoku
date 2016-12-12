package org.spooner.java.TileGame.Scenes.Cutscenes;

import java.awt.Graphics;

import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.Menus.NPCTextMenu;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Speech.Conversation;
import org.spooner.java.TileGame.Speech.Line;

public class CutsceneTextMenu extends NPCTextMenu{
	//members
	private Conversation con;
	//constructors
	protected CutsceneTextMenu(int width, int height){
		super(width, height, null);
		con = null;
	}
	//methods
	protected void open(Conversation con){
		this.con = con;
	}
	protected void begin(){
		nextLine();
	}
	private void nextLine(){
		//assume they are all lines
		Line l = (Line) con.next();
		setSpeakerText(l.getSpeaker());
		setWords(l.getLine());
	}
	protected void end(){
		con = null;
	}
	@Override
	public void draw(Graphics g){
		//only draw if conversation open
		if(con != null)
			super.draw(g);
	}
	@Override
	public boolean tick(InputTracker input, Board currentBoard) {
		//if interacting
		if(input.consumeInteract()){
			//set advancing
			advance();
		}
		if(getWords() == null){
			if(con.hasNext()){
				nextLine();
			}
			else{
				//all done
				return false;
			}
		}
		return true;
	}
}
