package org.spooner.java.TileGame;

import java.io.Serializable;

public class InventoryItem implements Serializable{
	//members
	private static final long serialVersionUID = -2722632943140765605L;
	private static transient InventoryItem[] items;
	private static transient InventoryItem[] orbs;
	static{
		items = TileIO.loadInventory();
		orbs = TileIO.loadOrbs();
	}
	private String name;
	private String desc;
	//constructors
	public InventoryItem(String name, String desc){
		this.name = name;
		this.desc = desc;
	}
	public InventoryItem(String name){
		this(name, null);
	}
	//methods
	public String getName(){ return name; }
	public String getDesc(){ return desc; }
	
	public static InventoryItem[] getItems(){ return items; }
	public static InventoryItem[] getOrbs(){ return orbs; }
}
