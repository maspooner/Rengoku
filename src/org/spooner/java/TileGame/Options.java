package org.spooner.java.TileGame;

import java.io.Serializable;

public class Options implements Serializable{
	//members
	private static final long serialVersionUID = 2093783856063852650L;
	//don't serialize options
	private static transient Options options;
	static{
		options = TileIO.loadOptions();
	}
	private boolean isMusicMuted;
	private boolean isSoundMuted;
	private int musicVolume;
	private int soundVolume;
	private int textSpeed;
	private boolean showJoy;
	private int wait;
	//constructors
	protected Options(){
		isMusicMuted = false;
		isSoundMuted = false;
		musicVolume = TileConstants.MUSIC_VOLUME;
		soundVolume = TileConstants.SOUND_VOLUME;
		textSpeed = 2;
		showJoy = true;
		wait = TileConstants.DEFAULT_WAIT;
	}
	//methods
	public boolean isMusicMuted(){ return isMusicMuted; }
	public boolean isSoundMuted(){ return isSoundMuted; }
	public int getMusicVolume(){ return musicVolume; }
	public int getSoundVolume(){ return soundVolume; }
	public int getTextSpeed(boolean isFast){ return isFast ? textSpeed * TileConstants.FAST_MENU_RATIO : textSpeed; }
	public boolean showJoy(){ return showJoy; }
	public int getWait(){ return wait; }
	public void toggleMusicMuted(){ isMusicMuted = !isMusicMuted; }
	public void toggleSoundMuted(){ isSoundMuted = !isSoundMuted; }
	public void increaseMusicVolume(){
		musicVolume += 10;
		//loop back around
		if(musicVolume > 100)
			musicVolume = 10;
	}
	public void increaseSoundVolume(){
		soundVolume += 10;
		//loop back around
		if(soundVolume > 100)
			soundVolume = 10;
	}
	public void increaseTextSpeed(){
		textSpeed++;
		//loop back around
		if(textSpeed > 3)
			textSpeed = 1;
	}
	public void increaseWait(){
		wait += 2;
		if(wait > 20)
			wait = 0;
	}
	public void toggleShowJoy(){ showJoy = !showJoy; }
	public static Options getOptions(){ return options; }
}
