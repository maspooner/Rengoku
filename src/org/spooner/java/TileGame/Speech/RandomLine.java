package org.spooner.java.TileGame.Speech;

public class RandomLine implements Speakable{
	//members
	private Conversation[] randomCons;
	private int randomIndex;
	//constructors
	public RandomLine(Conversation[] randomCons){
		this.randomCons = randomCons;
		randomize();
	}
	//methods
	private void randomize(){
		randomIndex = (int) (Math.random() * randomCons.length);
	}
	private void nextIndex(){
		randomIndex++;
		if(randomIndex == randomCons.length) randomIndex = 0;
	}
	@Override
	public void reset(){
		randomCons[randomIndex].reset();
		nextIndex();
	}
	@Override
	public void revert() {
		randomCons[randomIndex].revert();
	}
	@Override
	public Speakable next(){
		return randomCons[randomIndex].next();
	}
	@Override
	public boolean hasNext(){ return randomCons[randomIndex].hasNext(); }
	@Override
	public void begin(){
		randomCons[randomIndex].begin();
	}
}
