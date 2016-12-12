package org.spooner.java.TileGame;

import java.awt.image.BufferedImage;

public class TileSet {
	//members
	private static TileSet loadedSet = null;
	private static TileSet foregroundSet = null;
	private BufferedImage[] icons;
	private int id;
	//constructors
	private TileSet(String path, int id){
		this.id = id;
		//load the tileset image
		BufferedImage source = TileIO.loadImage(TileConstants.TILE_SET_PATH + path);
		int width = source.getWidth();
		int height = source.getHeight();
		//if not an accurate tile size
		if(width % TileConstants.ORG_TILE_SIZE != 0 || height % TileConstants.ORG_TILE_SIZE != 0){
			System.err.println("WARNING-Wrong source size");
			System.exit(-1);
		}
		//find tile dimensions
		int twidth = width / TileConstants.ORG_TILE_SIZE;
		int theight = height/TileConstants.ORG_TILE_SIZE;
		//create the array
		icons = new BufferedImage[twidth*theight];
		//create the icons
		createIcons(source);
	}
	//methods
	public int numIcons(){ return icons.length; }
	public int getID(){ return id; }
	public BufferedImage getIcon(int id){
		return icons[id];
	}
	private void createIcons(BufferedImage source){
		final int size = TileConstants.ORG_TILE_SIZE;
		int width = source.getWidth();
		int col = 0;
		int row = 0;
		for(int i=0;i<icons.length;i++){
			//crop out tiles
			icons[i] = source.getSubimage(col * size, row * size, size, size);
			//increment col
			col++;
			//if at end of row
			if(col * size == width){
				row++;
				col = 0;
			}
		}
	}
	//static methods
	public static TileSet currentSet(){ return loadedSet; }
	public static TileSet foregroundSet(){ return foregroundSet; }
	public static void changeSet(int newID){
		//no set loaded or different id
		if(loadedSet == null || newID != loadedSet.id){
			//change tile set
			loadedSet = new TileSet(newID + ".png", newID);
			foregroundSet = new TileSet(TileConstants.FOREGROUND_PATH + newID + ".png", newID);
		}
	}
}
