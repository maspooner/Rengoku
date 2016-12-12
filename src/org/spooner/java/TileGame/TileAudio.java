package org.spooner.java.TileGame;

import org.spooner.java.MRL.AudioPlayer;
import org.spooner.java.MRL.FadePlayer;

public abstract class TileAudio{
	//members
	private static FadePlayer musicPlayer;
	private static AudioPlayer soundPlayer;
	//constructors
	static{
		musicPlayer = new FadePlayer(TileConstants.MUSIC_FADE, TileConstants.MUSIC_BUFFER);
		soundPlayer = new AudioPlayer(TileConstants.SOUND_BUFFER);
		musicPlayer.setLoop(true);
		soundPlayer.setLoop(false);
		//set up options
		applyOptions();
	}
	//methods
	public static void applyOptions(){
		//if changing muted status
		if(Options.getOptions().isMusicMuted() == !musicPlayer.isMuted())
			musicPlayer.mute(Options.getOptions().isMusicMuted());
		if(Options.getOptions().isSoundMuted() == !soundPlayer.isMuted())
			soundPlayer.mute(Options.getOptions().isSoundMuted());
		//change volumes
		musicPlayer.setVolume(Options.getOptions().getMusicVolume());
		soundPlayer.setVolume(Options.getOptions().getSoundVolume());
	}
	public static void setResourceRead(){
		//make sure knows correct play method
		musicPlayer.setResourceRead(!TileGame.isTest());
		soundPlayer.setResourceRead(!TileGame.isTest());
	}
	public static void playMusic(String path){
		musicPlayer.stop();
		musicPlayer.play(TileConstants.MUSIC_PATH + path + ".wav");
	}
	public static void playSound(String path){
		soundPlayer.play(TileConstants.SOUND_PATH + path + ".wav");
	}
	public static void mute(boolean isMuted){
		musicPlayer.mute(isMuted);
	}
	public static void stopMusic(){
		musicPlayer.stop();
	}
	public static void stopSound(){
		soundPlayer.stop();
	}
	public static String getPlayingMusic(){
		if(musicPlayer.isPlaying()){
			String file = musicPlayer.getAudioLocation();
			int pos = file.lastIndexOf('/') + 1;
			//get just the file name
			return file.substring(pos);
		}
		return null;
	}
	public static boolean playingMusic(){
		return musicPlayer.isPlaying();
	}
	public static boolean playingSound(){
		return soundPlayer.isPlaying();
	}
	public static String getPlayingSound(){
		if(soundPlayer.isPlaying()){
			String file = soundPlayer.getAudioLocation();
			int pos = file.lastIndexOf('/') + 1;
			//get just the file name
			return file.substring(pos);
		}
		return null;
	}
}
