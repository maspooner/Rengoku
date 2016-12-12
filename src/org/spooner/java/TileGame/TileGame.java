package org.spooner.java.TileGame;

import java.io.IOException;

/**
 * TileGame
 * Made by Matt Spooner
 * 
 * Date created: Mar 24, 2014
 * Last modified: May 20, 2015
 * 
 * New Concepts:
 * - custom buffering
 * - scrolling
 * - multiple inheritance
 * - using enums
 * - AI
 * - interfaces
 * - recursion
 * - serialization
 * - memory analyzation
 * - using my library
 */
public abstract class TileGame{
	//members
	private static final String TITLE = "Rengoku ";
	private static final String VERSION = "ver: 1.00";
	private static boolean isTest = true;
	//methods
	public static void main(String[] args) throws IOException{
		//make sure audio knows which read to use
		TileAudio.setResourceRead();
		if(Options.getOptions().showJoy()){
			//open up joy to key
			final Process joy = Runtime.getRuntime().exec(TileConstants.JOY_PATH);
			//make sure to close it on program end
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
				@Override
				public void run() {
					joy.destroy();
				}
			}));
		}
		//startup the frame
		new TileFrame(TITLE + VERSION);
		System.out.println(Runtime.getRuntime().freeMemory());
		System.out.println(Runtime.getRuntime().totalMemory());
		//free memory
		//used memory
		// Run 1: 39948120
		//166797312 - 159 MB
		// Run 2 (transform used) 13010256
		//28127232
		// Run 3 (game logic update) 15813424
		//30654464
		// Run 4 (only store 1 buff image) 2714112
		//16252928 - 15.5 MB
		// Run 5 (load tilesets one at a time) 12426696
		//16252928
		//------------------------
		//Run 1: 12920632
		//30896128 - 29.5 MB
		//Run 2 (getting rid of costly constants i.e. scene chains) 7257968
		//16252928 - 15.5 MB
	}
	public static boolean getRandomBoolean(){
		return getRandomInt(2) == 1;
	}
	public static int getRandomInt(int max){
		return (int) (Math.random() * max);
	}
	public static boolean isTest(){ return isTest; }
	public static void setTest(boolean isTest){ TileGame.isTest = isTest; }
}
