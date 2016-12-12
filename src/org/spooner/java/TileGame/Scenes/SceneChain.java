package org.spooner.java.TileGame.Scenes;

import java.awt.Graphics;

import org.spooner.java.TileGame.GameState;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Transition;

public class SceneChain implements DrawableScene{
	//members
	private DrawableScene[] scenes;
	private String[] musics;
	private GameState followUp;
	private Transition sceneTrans;
	private int current;
	private boolean done;
	//constructors
	public SceneChain(DrawableScene[] scenes, String[] musics, GameState followUp){
		this.scenes = scenes;
		this.musics = musics;
		this.followUp = followUp;
		sceneTrans = new Transition(TileConstants.FADE_SPEED);
		current = 0;
		done = false;
		//play any music
		if(musics[current] != null){
			TileAudio.playMusic(musics[current]);
		}
		else{
			TileAudio.stopMusic();
		}
	}
	//methods
	@Override
	public void onBegin(){
		//do nothing
	}
	@Override
	public void draw(Graphics g, InputTracker input) {
		scenes[current].draw(g, input);
		//draw the transition
		sceneTrans.draw(g);
	}
	@Override
	public void tick(InputTracker input){
		scenes[current].tick(input);
		if(scenes[current].isDone()){
			//if just finished fading out
			if(sceneTrans.justOut()){
				//go to the next scene
				if(current + 1 < scenes.length){
					current++;
					//play any music
					if(musics[current] != null){
						TileAudio.playMusic(musics[current]);
					}
					else{
						TileAudio.stopMusic();
					}
				}
				else{
					//done if through with all scenes
					done = true;
				}
				//clear any unwanted input
				input.clearActions();
				if(!done){
					//begin transtion in
					sceneTrans.activate(true);
				}
			}
			//if not active, waiting for transition out
			else if(!sceneTrans.isActive()){
				//begin transtion out
				sceneTrans.activate(false);
				//perform the active scene's clean up
				scenes[current].onEnd();
			}
		}
		else{
			//tick it if there is no transition
			if(!sceneTrans.isActive()){
				//if didn't just fade in
				if(!sceneTrans.justIn()){
					//tick normally
					scenes[current].tick(input);
				}
				else{
					//run pre-setup on scene
					scenes[current].onBegin();
				}
			}
		}
	}
	@Override
	public boolean isDone(){
		return done;
	}
	@Override
	public void onEnd(){
		//do nothing
	}
	@Override
	public GameState endState() {
		return followUp;
	}
}
