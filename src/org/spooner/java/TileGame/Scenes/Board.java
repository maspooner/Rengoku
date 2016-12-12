package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;
import java.awt.Point;
import org.spooner.java.MRL.Sorter;
import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.Flags;
import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.Entities.Entity;
import org.spooner.java.TileGame.Entities.EntityComparator;
import org.spooner.java.TileGame.Entities.NPC;
import org.spooner.java.TileGame.Entities.Player;
import org.spooner.java.TileGame.Menus.AbstractMenu;
import org.spooner.java.TileGame.Menus.ChoiceMenu;
import org.spooner.java.TileGame.Menus.ListMenu;
import org.spooner.java.TileGame.Menus.NPCTextMenu;
import org.spooner.java.TileGame.Menus.Position;
import org.spooner.java.TileGame.Menus.TextMenu;

public class Board implements DrawableScene{
	//members
	private String name;
	private Tile[][] tiles;
	private boolean isFast;
	private Point[] foregroundIndexes;
	private Player player;
	private Entity[] entities;
	private EntityComparator entComp;
	private AbstractMenu currentMenu;
	private ChoiceMenu hubMenu;
	private TextMenu textMenu;
	private NPCTextMenu npcMenu;
	private ListMenu inventoryMenu;
	private ListMenu orbMenu;
	private TextMenu nametagMenu;
	private BoardChangeEvent changeEvent;
	private BoardState state;
	private org.spooner.java.MRL.Transition nametagTrans;
	private int nametagCount;
	private boolean isQuit;
	private boolean isChildMenu;
	private int cutID;
	//constructors
	public Board(String name, TextMenu nametagMenu, Tile[][] tiles, NPC[] npcs, Point playerStart,
			Direction orgFace, String musicName, boolean isFast){
		this.name = name;
		this.tiles = tiles;
		this.isFast = isFast;
		foregroundIndexes = findForegrounds();
		player = new Player(playerStart, getWidth(), getHeight(), orgFace);
		//start a list of entities, +1 for player room
		entities = new Entity[npcs.length + 1];
		//add npcs to array
		for(int i = 0; i < npcs.length; i++){
			entities[i] = npcs[i];
		}
		//add player to array
		entities[entities.length - 1] = player;
		entComp = new EntityComparator();
		hubMenu = new ChoiceMenu(TileConstants.HUB_MENU_OPTIONS, TileConstants.HUB_MENU_WIDTH, TileConstants.HUB_MENU_HEIGHT, true, true);
		hubMenu.setYPosition(Position.BOTTOM);
		textMenu = new TextMenu(AbstractMenu.FULL, TileConstants.TEXT_MENU_HEIGHT, true);
		npcMenu = new NPCTextMenu(AbstractMenu.FULL, TileConstants.NPC_MENU_HEIGHT, textMenu);
		//set the menu position
		npcMenu.setYPosition(Position.BOTTOM);
		inventoryMenu = new ListMenu("Inventory", null, TileConstants.LIST_MENU_WIDTH,
				AbstractMenu.FULL, AbstractMenu.FULL, TileConstants.DESC_MENU_HEIGHT);
		orbMenu = new ListMenu("Orbs", null, TileConstants.LIST_MENU_WIDTH,
				AbstractMenu.FULL, AbstractMenu.FULL, TileConstants.DESC_MENU_HEIGHT);
		this.nametagMenu = nametagMenu;
		nametagCount = 0;
		currentMenu = null;
		changeEvent = null;
		state = BoardState.NO_MOVEMENT;
		nametagTrans = new org.spooner.java.MRL.Transition(TileConstants.FADE_SPEED);
		isQuit = false;
		//occupy all npc tiles
		for(NPC n : npcs){
			if(n != null){
				if(getTileAt(n.getTileX(), n.getTileY()).getOccupent() == null)
					getTileAt(n.getTileX(), n.getTileY()).fill(n);
			}
		}
		//play music if present
		if(musicName != null){
			//play if not playing
			if(!(musicName + ".wav").equals(TileAudio.getPlayingMusic())){
				TileAudio.playMusic(musicName);
				//try again if failed
				if(!TileAudio.playingMusic()) TileAudio.playMusic(musicName);
			}
		}
		isChildMenu = true;
		cutID = -1;
	}
	//methods
	public BoardChangeEvent getChangeEvent(){
		return changeEvent;
	}
	public BoardChangeEvent toEvent(){
		//R is special syntax
		return new BoardChangeEvent(name, "R" + player.getTileX() + " " + player.getTileY(), player.getLastDirection());
	}
	public void fireChange(BoardChangeEvent changeEvent){
		this.changeEvent = changeEvent;
	}
	public void activateItem(int i, int j, String item, InputTracker input){
		input.clearActions();
		ItemTile it = (ItemTile) tiles[i][j];
		//replace the item tile with a regular tile
		tiles[i][j] = it.replacement();
		//go into a menu that's not a child
		isChildMenu = false;
		state = BoardState.IN_MENU;
		textMenu.setText("You recieved a(n) " + item + "!");
		currentMenu = textMenu; 
	}
	public boolean isFast(){ return isFast; }
	public int getTileWidth(){ return tiles.length; }
	public int getTileHeight(){ return tiles[0].length; }
	public int getWidth(){ return getTileWidth() * TileConstants.TILE_SIZE; }
	public int getHeight(){ return getTileHeight() * TileConstants.TILE_SIZE; }
	public Tile getTileAt(int x, int y){return tiles[x][y];}
	public int getPlayerX(){ return player.getRealX(); }
	public int getPlayerY(){ return player.getRealY() - player.getHeight(); }
	public boolean isInBounds(int x, int y){
		boolean inBounds = false;
		if(x >= 0 && x < getTileWidth()){
			if(y >= 0 && y < getTileHeight()){
				inBounds = true;
			}
		}
		return inBounds;
	}
	public void showNPCMenu(NPC invoker, InputTracker input){
		input.clearActions();
		//set the invoker
		npcMenu.setInvoker(invoker);
		//align based on player position
		Position align = Position.BOTTOM;
		//on bottom, show on top
		if(player.getRealY() > TileConstants.FRAME_CENTER.y)
			align = Position.TOP;
		npcMenu.setYPosition(align);
		//be in menu
		state = BoardState.IN_MENU;
		currentMenu = npcMenu;
	}
	private Point[] findForegrounds(){
		//count the foreground tiles
		int count = 0;
		for(Tile[] ta : tiles){
			for(Tile t : ta){
				if(t.hasForeground()) count++;
			}
		}
		Point[] forePoints = new Point[count];
		int pointIndex = 0;
		//find foregrounds
		for(int i = 0; i < tiles.length; i++){
			for(int j = 0; j < tiles[i].length; j++){
				//found a foreground
				if(tiles[i][j].hasForeground()){
					//add to array
					forePoints[pointIndex] = new Point(i, j);
					pointIndex++;
				}
			}
		}
		return forePoints;
	}
	private Point findXBounds(){
		//start centered on player
		int highx = player.getTileX();
		int lowx = player.getTileX();
		//the tile width of the board
		final int W = TileConstants.FRAME_WIDTH / TileConstants.TILE_SIZE;
		//if player is centered
		if(player.getRealX() == TileConstants.FRAME_CENTER.x){
			//go evenly in both directions (+2 for when moving)
			lowx -= W / 2 + 2;
			highx += W / 2 + 2;
		}
		//player is on left
		else if(player.getRealX() < TileConstants.FRAME_CENTER.x){
			lowx = 0;
			highx = W;
		}
		//player is on right
		else{
			lowx = tiles.length - 2 - W;
			highx = tiles.length;
		}
		//if went out of bounds transitioning
		if(lowx < 0) lowx = 0;
		if(highx > tiles.length) highx = tiles.length;
		return new Point(lowx, highx);
	}
	private Point findYBounds(){
		//start centered on player
		int highy = player.getTileY();
		int lowy = player.getTileY();
		//the tile height of the board
		final int H = TileConstants.FRAME_HEIGHT / TileConstants.TILE_SIZE;
		//if player is centered
		if(player.getRealY() == TileConstants.FRAME_CENTER.y){
			//go evenly in both directions (+2 for when moving)
			lowy -= H / 2 + 2;
			highy += H / 2 + 2;
		}
		//player is on top
		else if(player.getRealY() < TileConstants.FRAME_CENTER.y){
			lowy = 0;
			highy = H;
		}
		//player is on bottom
		else{
			lowy = tiles[0].length - 2 - H;
			highy = tiles[0].length;
		}
		//if went out of bounds transitioning
		if(lowy < 0) lowy = 0;
		if(highy > tiles[0].length) highy = tiles[0].length;
		return new Point(lowy, highy);
	}
	@Override
	public void draw(Graphics g, InputTracker input){
		//draw black background
		g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT);
		int viewx = player.getXOffset();
		int viewy = player.getYOffset();
		//find the boundries of where to draw tiles so that not all of them are drawn every time
		Point xBounds = findXBounds();
		Point yBounds = findYBounds();
		//draw each background tile with offset
		for(int i = xBounds.x; i < xBounds.y; i++){
			for(int j = yBounds.x; j < yBounds.y; j++){
				//draw the tile
				tiles[i][j].draw(g, i, j, viewx, viewy);
			}
		}
		//put the entities in order based on their y value
		Sorter.insertionSort(entities, entComp);
		//draw the entities in the right order
		for(Entity e : entities){
			//entity must be present
			if(e != null){
				//if the player
				if(e instanceof Player){
					((Player) e).draw(input, g, state);
				}
				else{
					((NPC) e).draw(g, viewx, viewy, state);
				}
			}
		}
		//draw the foreground
		for(Point p : foregroundIndexes){
			tiles[p.x][p.y].drawForeground(g, p.x, p.y, viewx, viewy);
		}
		//only if in menu
		if(state.equals(BoardState.IN_MENU)){
			currentMenu.draw(g);
		}
		//if has nametag and it's not outside
		if(nametagMenu != null && !nametagTrans.reachedOut()){
			nametagMenu.draw(g);
		}
	}
	@Override
	public void tick(InputTracker input){
		//test state
		switch(state){
			case MOVEMENT:
				if(input.consumeHubbing()){
					//clear actions
					input.clearActions();
					//show the menu
					currentMenu = hubMenu;
					boolean isRight = player.getRealX() > TileConstants.FRAME_CENTER.x;
					//put the menu on the opposite side of the player
					hubMenu.setXPosition(isRight ? Position.LEFT : Position.RIGHT);
					state = BoardState.IN_MENU;
				}
				else{
					//allow entities to tick
					player.tick(input, this);
					for(Entity e : entities){
						//entity must be present
						if(e != null && e instanceof NPC)
							((NPC) e).tick(this);
					}
				}
				break;
			case IN_MENU:
				//allow menu to tick
				boolean open = currentMenu.tick(input, this);
				//if closing and a child menu
				if(!open && isChildMenu){
					//closed hub menu
					if(currentMenu == hubMenu){
						switch(hubMenu.getCurrentSelection()){
						//items
						case 0:
							//set the list to the owned items when opened
							inventoryMenu.setItems(Flags.getFlags().ownedItems());
							currentMenu = inventoryMenu;
						break;
						//orbs
						case 1:
							//set the list to the owned orbs when opened
							orbMenu.setItems(Flags.getFlags().ownedOrbs());
							currentMenu = orbMenu;
						break;
						//save
						case 2:
							boolean saved = TileIO.saveFlags();
							textMenu.setText(saved ? TileConstants.DID_SAVE_TEXT : TileConstants.DIDNT_SAVE_TEXT);
							currentMenu = textMenu;
							break;
						//quit
						case 3:
							//start quitting
							state = BoardState.QUIT;
							break;
						//user canceled
						default:
							input.clearActions();
							//begin movement
							state = BoardState.MOVEMENT;
							break;
						}
					}
					//closed other menu besides talking one
					else if(currentMenu != npcMenu){
						input.clearActions();
						//return to hub
						currentMenu = hubMenu;
					}
					else{
						//handle cutscene
						if(npcMenu.getInvoker().cutscene() >= 0){
							cutID = npcMenu.getInvoker().cutscene();
						}
						//if reloading board
						else if(npcMenu.getInvoker().reloadsBoard()){
							//change to this board
							changeEvent = toEvent();
						}
						//stopped talking, resume movement
						state = BoardState.MOVEMENT;
					}
				}
				//not a child menu
				else if(!open && !isChildMenu){
					//begin movement
					state = BoardState.MOVEMENT;
					isChildMenu = true;
				}
				break;
			case QUIT:
				isQuit = true;
				break;
			default: break;
		}
		//has a menu AND active or reached on screen
		if(nametagMenu != null && (nametagTrans.isActive() || nametagTrans.reachedIn())){
			tickNametag();
		}
	}
	public int getCutscene(){ return cutID; }
	private void tickNametag(){
		if(nametagTrans.isActive()){
			if(nametagTrans.isFadingIn()){
				nametagTrans.fadeIn();
			}
			else{
				nametagTrans.fadeOut();
			}
		}
		else{
			//on screen
			if(nametagTrans.justIn()){
				//start counter
				nametagCount++;
			}
			//waiting
			else if(nametagCount > 0){
				nametagCount++;
				if(nametagCount > TileConstants.NAMETAG_BUFFER){
					//go out
					nametagTrans.activate(false);
				}
			}
		}
		//move the menu
		//use the lerp for the height of the menu
		int yNew = (int) (nametagTrans.getAlpha() * TileConstants.NAMETAG_MENU_HEIGHT) - TileConstants.NAMETAG_MENU_HEIGHT;
		nametagMenu.setYAlign(yNew);
	}
	@Override
	public boolean isDone(){
		//stop if change event is there or quitting or there's a cutscene
		return changeEvent != null || isQuit || cutID >= 0;
	}
	@Override
	public GameState endState(){
		//next state is a board or quitting or cutscene
		return isQuit ? GameState.TITLE : (cutID == 6 ? GameState.END_CUT : (cutID >= 0 ? GameState.ORB_CUT : GameState.BOARD));
	}
	@Override
	public void onBegin(){
		//start movement
		state = BoardState.MOVEMENT;
		//display nametag menu
		nametagTrans.activate(true);
	}
	@Override
	public void onEnd() {
		//set the state to no movement
		state = BoardState.NO_MOVEMENT;
	}
}
