package org.spooner.java.TileGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.BoardChangeEvent;
import org.spooner.java.TileGame.Scenes.DrawableScene;
import org.spooner.java.TileGame.Scenes.OptionsScreen;
import org.spooner.java.TileGame.Scenes.SceneChain;
import org.spooner.java.TileGame.Scenes.SponsorScreen;
import org.spooner.java.TileGame.Scenes.StaticScreen;
import org.spooner.java.TileGame.Scenes.TitleScreen;
import org.spooner.java.TileGame.Scenes.Cutscenes.EndCutscene;
import org.spooner.java.TileGame.Scenes.Cutscenes.IntroCutscene;
import org.spooner.java.TileGame.Scenes.Cutscenes.OrbCutscene;
import org.spooner.java.TileGame.Scenes.Cutscenes.WakeCutscene;

@SuppressWarnings("serial")
public class TileFrame extends JFrame implements Runnable{
	//constants
	public static int RIGHT_INSET;
	public static int BOTTOM_INSET;
	private static final DrawableScene BUFFER_SCREEN = new DrawableScene(){
		public void tick(InputTracker input) {}
		public void onEnd() {}
		public void onBegin() {}
		@Override
		public boolean isDone() { return true; }
		@Override
		public GameState endState(){ return null; }
		@Override
		public void draw(Graphics g, InputTracker input) { g.fillRect(0, 0, TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT); }
	};
	//members
	private Thread renderThread;
	private DrawableScene currentScene;
	private InputTracker inputTracker;
	private Transition transition;
	private int xInset;
	private int yInset;
	//constructors
	public TileFrame(String title){
		transition = new Transition(TileConstants.FADE_SPEED);
		xInset = 0;
		yInset = 0;
		//size will be changed later to account for the insets
		setSize(new Dimension(TileConstants.FRAME_WIDTH, TileConstants.FRAME_HEIGHT));
		inputTracker = new InputTracker();
		addKeyListener(inputTracker);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIgnoreRepaint(true);
		setTitle(title);
		setResizable(false);
		setIconImage(TileIO.loadImage(TileConstants.ICON_FILE));
		Dimension wholeSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(wholeSize.width / 2 - TileConstants.FRAME_WIDTH / 2, wholeSize.height / 2 - TileConstants.FRAME_HEIGHT / 2);
		setVisible(true);
		
		currentScene = getOpeningChain();
		renderThread = new Thread(this);
		renderThread.start();
		//set constants
		RIGHT_INSET = getInsets().right;
		BOTTOM_INSET = getInsets().bottom;
		//fade in
		transition.activate(true);
		currentScene.onBegin();
	}
	//methods
	@Override
	public void paint(Graphics g) {/*do nothing*/}
	private void nextScene(){
		//test the end state
		switch(currentScene.endState()){
		case NEW_GAME:
			//show opening chain
			currentScene = getNewGameChain();
			break;
		case LOAD_GAME:
			//load the first board
			currentScene = loadBoard(TileConstants.FIRST_BOARD);
			break;
		case INSTRUCTION:
			currentScene = new StaticScreen("inBack", "instruct");
			break;
		case OPTIONS:
			//go to options
			currentScene = new OptionsScreen();
			break;
		case ABOUT:
			currentScene = new StaticScreen("aboutBack", "about");
			break;
		case ORB_CUT:
			//coming from board
			Board bo = (Board) currentScene;
			currentScene = new OrbCutscene(bo.getCutscene(), bo);
			break;
		case END_CUT:
			currentScene = new EndCutscene();
			break;
		case BOARD:
			BoardChangeEvent bce = null;
			if(currentScene instanceof Board){
				//coming from previous board -> currentScene is a board
				Board b = (Board) currentScene;
				bce = b.getChangeEvent();
			}
			else{
				//coming from orb cutscene
				OrbCutscene oc = (OrbCutscene) currentScene;
				bce = oc.getBackBoard();
			}
			//load the current scene's change event
			currentScene = loadBoard(bce);
			break;
		case TITLE:
			currentScene = new TitleScreen();
			break;
		case QUIT:
			//gracefully exit
			System.exit(0);
			break;
		}
	}
	private SceneChain getOpeningChain(){
		SponsorScreen java = new SponsorScreen(Color.WHITE, new Animation(TileConstants.SPRITE_PATH + "java/"), 800, TileConstants.FAST_ANIMATION_RATE);
		SponsorScreen spoon = new SponsorScreen(Color.BLACK, new Animation(TileConstants.SPRITE_PATH + "spoon/"), 1200, TileConstants.ANIMATION_RATE);
		return new SceneChain(new DrawableScene[]{ BUFFER_SCREEN, java, spoon },
				new String[]{ null, "primative", "angel" }, GameState.TITLE);
	}
	private SceneChain getNewGameChain(){
		SponsorScreen dedi = new SponsorScreen(Color.BLACK, new Animation(TileConstants.SPRITE_PATH + "dedi/"), 600, TileConstants.ANIMATION_RATE);
		return new SceneChain(new DrawableScene[]{ dedi, new IntroCutscene(), new WakeCutscene() }, new String[]{ null, "cathedral", "sunlight" }, GameState.LOAD_GAME);
	}
	private Board loadBoard(BoardChangeEvent event){
		return TileIO.parseBoard(event.getBoard(), event.getStartID(), event.getFace());
	}
	private void tick() {
		//scene still going
		if(!currentScene.isDone()){
			//tick it if there is no transition
			if(!transition.isActive()){
				//if didn't just fade in
				if(!transition.justIn()){
					//tick normally
					currentScene.tick(inputTracker);
				}
				else{
					//run pre-setup on scene
					currentScene.onBegin();
				}
			}
		}
		//scene transitioning
		else{
			//if just finished fading out
			if(transition.justOut()){
				//go to the next scene
				nextScene();
				
				//clear any unwanted input
				inputTracker.clearActions();
				//begin transtion in
				transition.activate(true);
			}
			//if not active, waiting for transition out
			else if(!transition.isActive()){
				//begin transtion out
				transition.activate(false);
				//perform the active scene's clean up
				currentScene.onEnd();
			}
		}
	}
	private void draw(){
		BufferStrategy bs = getBufferStrategy();
		if(bs==null){
			//double buffer
			createBufferStrategy(2);
		}
		else{
			do{
				Graphics g = null;
				try{
					g = bs.getDrawGraphics();
					//if no insets yet
					if(xInset == 0 || yInset == 0){
						//set them
						xInset = getInsets().left;
						yInset = getInsets().top;
						//add insets to view full width/height
						setSize(new Dimension(TileConstants.FRAME_WIDTH + xInset, TileConstants.FRAME_HEIGHT + yInset));
					}
					//translate the graphics to account for frame offsets
					g.translate(xInset, yInset);
					//draw the currentScene
					currentScene.draw(g, inputTracker);
					//draw the transition
					transition.draw(g);
				}
				finally{
					g.dispose();
				}
				bs.show();
			} while(bs.contentsLost());
		}
	}
	@Override
	public void run(){
		//last time is initialized to now
		long last = System.currentTimeMillis();
		short timer = 0;
		while(true){
			//wait to not overload processor
			try{
				Thread.sleep(Options.getOptions().getWait());
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
				System.exit(-1);
			}
			//find current time
			long now = System.currentTimeMillis();
			//find change in time
			timer += now - last;
			//if time to tick/draw
			if(timer >= TileConstants.TICK_RATE){
				//make timer under tick rate
				while(timer >= TileConstants.TICK_RATE)
					timer -= TileConstants.TICK_RATE;
				tick();
				draw();
			}
			//set last
			last = now;
		}
	}
}
