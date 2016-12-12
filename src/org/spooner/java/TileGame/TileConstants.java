package org.spooner.java.TileGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.spooner.java.TileGame.Scenes.BoardChangeEvent;

public abstract class TileConstants {
	//members
	public static final String PLAYER_NAME = "Emere";
	public static final int NUM_ORBS = 5;
	public static final int MAX_FLAGS = 100;
	
	public static final int PLAYER_HEIGHT = 24;
	public static final int PLAYER_WIDTH = 16;
	public static final int ORG_TILE_SIZE = 16;
	public static final int TILE_SCALE = 4;
	public static final int TILE_SIZE = ORG_TILE_SIZE * TILE_SCALE;
	public static final int BACKGROUND_SCALE = 2;
	
	public static final int KEY_UP = KeyEvent.VK_UP;
	public static final int KEY_DOWN = KeyEvent.VK_DOWN;
	public static final int KEY_LEFT = KeyEvent.VK_LEFT;
	public static final int KEY_RIGHT = KeyEvent.VK_RIGHT;
	public static final int KEY_INTERACT = KeyEvent.VK_Z;
	public static final int KEY_RUN = KeyEvent.VK_X;
	public static final int KEY_HUB = KeyEvent.VK_C;
	public static final int KEY_CANCEL = KEY_RUN;
	public static final int KEY_SOUND = KeyEvent.VK_0;
	
	public static final int TICK_RATE = 17;
	public static final int ANIMATION_RATE = 6;
	public static final int FAST_ANIMATION_RATE = 3;
//	public static final int PROCESSOR_WAIT = 14; 
	public static final int DEFAULT_WAIT = 14; //number of milliseconds to wait before doing tick and draw
	public static final int MUSIC_BUFFER = 700; //700B
	public static final int SOUND_BUFFER = 4000; //about 4 kB
	public static final int MUSIC_VOLUME = 70;
	public static final int SOUND_VOLUME = 80;
	
	public static final int MOVEMENT_BUFFER = 60;
	public static final int CHANCE_TO_NOTHING = 70;//out of 100
	public static final int CHANCE_TO_TURN = 65;//out of 100
	
	private static final String ASSET_PATH = "assets/";
	public static final String TILE_SET_PATH = ASSET_PATH + "tilesets/";
	public static final String BOARD_PATH = ASSET_PATH + "boards/";
	public static final String SPRITE_PATH = ASSET_PATH + "sprites/";
	public static final String PERSON_SPRITE_PATH = SPRITE_PATH + "person/";
	public static final String BACKGROUNDS_PATH = ASSET_PATH + "backgrounds/";
	public static final String FLAGS_FILE = "./gameData.bin";
	public static final String OPTIONS_FILE = "./options.bin";
	public static final String INVENTORY_FILE = ASSET_PATH + "inventoryStrings.bin";
	public static final String ORB_FILE = ASSET_PATH + "orbStrings.bin";
	public static final String ICON_FILE = SPRITE_PATH + "icon.png";
	private static final String FONT_FILE = ASSET_PATH + "fantasy font.ttf";
	public static final String FOREGROUND_PATH = "foregrounds/";
	public static final String CONVERSATION_PATH = ASSET_PATH + "converses/";
	public static final String MUSIC_PATH = ASSET_PATH + "music/";
	public static final String SOUND_PATH = ASSET_PATH + "sounds/";
	public static final String TITLE_FILE = SPRITE_PATH + "title.png";
	public static final String SPARKLE_ANIMATION = SPRITE_PATH + "sparkle/";
	public static final String JOY_PATH = "joy/JoyToKey.exe";
	public static final String[] BACKGROUND_PATHS = new String[]{ "mourilyan.png", "nulato.png", "fujita.png", "transition1.png", "transition2.png" };
	
	public static final int FRAME_WIDTH = TILE_SIZE * 17;
	public static final int FRAME_HEIGHT = TILE_SIZE * 12;
	public static final Point FRAME_CENTER = new Point(FRAME_WIDTH / 2, FRAME_HEIGHT / 2);
	
	public static final Color MENU_COLOR = Color.BLUE;
	public static final Color MENU_BORDER_COLOR = Color.WHITE;
	public static final Color MENU_TEXT_COLOR = Color.WHITE;
	public static final Font MENU_FONT = TileIO.loadFont(FONT_FILE, Font.PLAIN, 32);
	
	public static final int MENU_ARC_WIDTH = 15;
	public static final int MENU_X_ADJUSTMENT = 20;
	public static final int MENU_Y_ADJUSTMENT = 10;
	public static final int MENU_BORDER_WIDTH = 2;
	public static final int MENU_X_TEXT_OFFSET = 25;
	public static final int MENU_Y_TEXT_OFFSET = 10;
	
	public static final int NPC_MENU_HEIGHT = 150;
	public static final int MAIN_MENU_WIDTH = 240;
	public static final int MAIN_MENU_HEIGHT = 210;
	public static final int TEXT_MENU_HEIGHT = 80;
	public static final int LIST_MENU_WIDTH = 300;
	public static final int DESC_MENU_HEIGHT = 160;
	public static final int NAMETAG_MENU_WIDTH = 350;
	public static final int NAMETAG_MENU_HEIGHT = TEXT_MENU_HEIGHT;
	public static final int HUB_MENU_WIDTH = LIST_MENU_WIDTH;
	public static final int HUB_MENU_HEIGHT = MAIN_MENU_HEIGHT;
	public static final int OPTIONS_MENU_WIDTH = FRAME_WIDTH / 2;
	public static final int OPTIONS_MENU_HEIGHT = FRAME_HEIGHT / 2;
	
	public static final int PLAYER_SPEED = 4;
	public static final int FAST_PLAYER_SPEED = 6;
	public static final int FAST_BOARD_SPEED = 6;
	public static final int FAST_FAST_BOARD_SPEED = 9;
	public static final int NPC_SPEED = 2;
	public static final float FADE_SPEED = 0.9f;
	public static final float OUT_RATIO= 0.1f; //how much of a difference the fade out should be from the fade in
	public static final int MENU_BUFFER = 2;
	public static final int FAST_MENU_RATIO = 3;
	public static final int CURSOR_BUFFER = 5;
	public static final int SKIP_REDUCTION = 4;
	public static final int PANNING_BUFFER = 50;
	public static final float MUSIC_FADE = 1.5f;
	public static final int NAMETAG_BUFFER = 30;
	public static final int BOUNCING_SPEED = 2;
	public static final int BOUNCING_SCALE = 6;
	
	public static final String DID_SAVE_TEXT = "The game was saved.";
	public static final String DIDNT_SAVE_TEXT = "The game could not be saved.";
	public static final String SAVE_CONFLICT_TEXT = "You already have a saved game. If you save in this new file, it WILL override your other save file. Keep that in mind.";
	public static final BoardChangeEvent FIRST_BOARD = new BoardChangeEvent("M_yours_up.xml", 0, Direction.DOWN);

	public static final InventoryItem[] MAIN_MENU_OPTIONS = new InventoryItem[]{
		new InventoryItem("Load Game"),
		new InventoryItem("New Game"),
		new InventoryItem("How to Play"),
		new InventoryItem("Options"),
		new InventoryItem("About"),
		new InventoryItem("Quit")
	};
	public static final InventoryItem[] HUB_MENU_OPTIONS = new InventoryItem[]{
		new InventoryItem("Items"),
		new InventoryItem("Orbs"),
		new InventoryItem("Save"),
		new InventoryItem("Main Menu")
	};
	public static final Animation ARROW_ANIMATION = new Animation(SPRITE_PATH + "sideArrow/");
	public static final BufferedImage SELECTION_ARROW = TileIO.loadImage(SPRITE_PATH + "selectArrow.png");
}
