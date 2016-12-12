package org.spooner.java.TileGame.Speech;

public class ChoiceLine implements Speakable{
	//members
	private Conversation[] choiceCons;
	private int choiceIndex;
	//constructors
	public ChoiceLine(Conversation[] choiceCons){
		this.choiceCons = choiceCons;
		choiceIndex = -1;
	}
	//methods
	public void makeChoice(int i){
		choiceIndex = i;
		//begin the conversation
		choiceCons[choiceIndex].begin();
	}
	public String[] getChoices(){
		String[] choices = new String[choiceCons.length];
		for(int i = 0; i < choices.length; i++){
			//first choices should all be lines
			choices[i] = ((Line) choiceCons[i].peekNext()).getLine();
		}
		return choices;
	}
	@Override
	public Speakable next(){
		if(choiceIndex == -1) return this;
		return choiceCons[choiceIndex].next();
	}
	@Override
	public boolean hasNext() {
		if(choiceIndex == -1) return true;
		return choiceCons[choiceIndex].hasNext();
	}
	@Override
	public void reset() {
		if(choiceIndex != -1){
			//reset all so others get original text back
			for(Conversation c : choiceCons)
				c.reset();
			choiceIndex = -1;
		}
	}
	@Override
	public void revert() {
		if(choiceIndex != -1)
			choiceCons[choiceIndex].revert();
	}
	@Override
	public void begin(){
		//nothing, wait for choice
	}
}
