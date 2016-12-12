package org.spooner.java.TileGame;

import java.io.Serializable;

public class Flags implements Serializable{
	//members
	private static final long serialVersionUID = 7440547502245887312L;
	//no serializing the items / flags
	private static transient Flags flags;
	static{
		flags = TileIO.loadFlags();
	}
	private boolean[] hasItem;
	private boolean[] hasTalked;
	private boolean[] hasOrb;
	private boolean fresh;
	//constructors
	protected Flags(){
		//all false
		hasItem = new boolean[TileConstants.MAX_FLAGS];
		//start with allowance
		hasItem[24] = true;
		hasTalked = new boolean[TileConstants.MAX_FLAGS];
		hasOrb = new boolean[TileConstants.NUM_ORBS];
		//fresh means newly created
		fresh = true;
	}
	//methods
	public void decay(){
		fresh = false;
	}
	public boolean isFresh(){ return fresh; }
	public boolean hasFlag(String type, int id){
		boolean has = true;
		//if only after talked and has not talked
		if(type.equals("after") && !hasTalked[id]){
			has = false;
		}
		//if only after get item and does not have item
		else if(type.equals("item") && !hasItem[id]){
			has = false;
		}
		//if only after get orb and does not have orb
		else if(type.equals("aorb") && !hasOrb[id]){
			has = false;
		}
		return has;
	}
	public void fireFlag(String type, int id){
		//trigger talking
		if(type.equals("trigger")){
			hasTalked[id] = true;
		}
		//get item
		if(type.equals("get")){
			hasItem[id] = true;
		}
		//lose item
		if(type.equals("take")){
			hasItem[id] = false;
		}
		//get orb
		if(type.equals("orb")){
			//saves from orb 6 causing exception
			if(id < hasOrb.length)
				hasOrb[id] = true;
		}
	}
	public InventoryItem[] ownedItems(){
		//count how many owned
		int numOwned = 0;
		for(boolean b : hasItem){
			if(b) numOwned++;
		}
		InventoryItem[] all = InventoryItem.getItems();
		InventoryItem[] owned = new InventoryItem[numOwned];
		int addIndex = 0;
		//go through whole array or until owned array is full
		for(int i = 0; i < all.length && addIndex < numOwned; i++){
			//has the item
			if(hasItem[i]){
				//add it to owned
				owned[addIndex] = all[i];
				addIndex++;
			}
		}
		return owned;
	}
	public InventoryItem[] ownedOrbs(){
		//count how many owned
		int numOwned = 0;
		for(boolean b : hasOrb){
			if(b) numOwned++;
		}
		InventoryItem[] all = InventoryItem.getOrbs();
		InventoryItem[] owned = new InventoryItem[numOwned];
		int addIndex = 0;
		//go through whole array or until owned array is full
		for(int i = 0; i < all.length && addIndex < numOwned; i++){
			//has the orb
			if(hasOrb[i]){
				//add it to owned
				owned[addIndex] = all[i];
				addIndex++;
			}
		}
		return owned;
	}
	public static Flags getFlags(){ return flags; }
}
