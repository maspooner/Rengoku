package org.spooner.java.TileGame.Speech;

import java.util.HashMap;

import org.spooner.java.TileGame.Flags;
import org.spooner.java.TileGame.InventoryItem;

public class Conversation implements Speakable{
	//members
	private Speakable[] speaks;
	private int currentSpeak;
	private HashMap<String, int[]> attributes;
	//constructors
	public Conversation(Speakable[] speaks){
		this(speaks, null);
	}
	public Conversation(Speakable[] speaks, HashMap<String, int[]> attributes){
		this.speaks = speaks;
		this.attributes = attributes;
		currentSpeak = 0;
	}
	//methods
	@Override
	public void reset(){
		currentSpeak = 0;
		for(Speakable s : speaks){
			s.reset();
		}
	}
	@Override
	public void revert(){
		currentSpeak--;
	}
	public String[] itemsGotten(){ 
		//amount is however many of get orb there are
		int count = 0;
		if(attributes != null){
			if(attributes.containsKey("get"))
				count += attributes.get("get").length;
		}
		//assign them
		String[] items = new String[count];
		if(attributes != null){
			int i = 0;
			//for both, if present, go through all values there and add the names to the array
			if(attributes.containsKey("get")){
				for(int j : attributes.get("get")){
					items[i] = InventoryItem.getItems()[j].getName();
					i++;
				}
			}
		}
		return items;
	}
	public int cutscene(){
		if(attributes != null && attributes.containsKey("orb")){
			return attributes.get("orb")[0];
		}
		return -1;
	}
	public boolean reloadsBoard(){
		return attributes != null && attributes.containsKey("reload");
	}
	public boolean canBegin(){
		boolean canBegin = true;
		//only if attributes
		if(attributes != null){
			for(String k : attributes.keySet()){
				//only if can still begin
				if(canBegin){
					int[] valArr = attributes.get(k);
					//for every number at the flag, check it
					for(int i = 0; i < valArr.length && canBegin; i++){
						int v = valArr[i];
						//can begin if has the flag
						canBegin = Flags.getFlags().hasFlag(k, v);
					}
				}
			}
		}
		return canBegin;
	}
	@Override
	public void begin(){
		//only if attributes
		if(attributes != null){
			for(String k : attributes.keySet()){
				//for every number at the flag, check it
				for(int v : attributes.get(k)){
					//fire every flag
					Flags.getFlags().fireFlag(k, v);
				}
			}
		}
	}
	public Speakable peekNext(){
		//while the current speech is done
		while(!speaks[currentSpeak].hasNext()){
			//increment currentSpeech
			currentSpeak++;
		}
		//the current speech
		Speakable speak = speaks[currentSpeak];
		//if speak is a not a line
		if(!(speak instanceof Line)){
			//set speak to the next line
			speak = speak.next();
		}
		//revert to original state
		speak.revert();
		return speak;
	}
	@Override
	public Speakable next(){
		//while the current speech is done
		while(!speaks[currentSpeak].hasNext()){
			//increment currentSpeech
			currentSpeak++;
		}
		//the current speech
		Speakable speak = speaks[currentSpeak].next();
		speak.begin();
		return speak;
	}
	@Override
	public boolean hasNext(){
		boolean hasNext = false;
		//while in range and does not have next
		while(currentSpeak < speaks.length && !hasNext){
			//if the speak has a next, is true
			if(speaks[currentSpeak].hasNext()){
				hasNext = true;
			}
			//move to next
			currentSpeak++;
		}
		if(hasNext){
			//decrement once for next call
			revert();
		}
		return hasNext;
	}
}
