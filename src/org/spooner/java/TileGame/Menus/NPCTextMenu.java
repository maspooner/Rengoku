package org.spooner.java.TileGame.Menus;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.spooner.java.TileGame.Animation;
import org.spooner.java.TileGame.InputTracker;
import org.spooner.java.TileGame.Options;
import org.spooner.java.TileGame.TileAudio;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.Entities.NPC;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Speech.Line;

public class NPCTextMenu extends AbstractMenu{
	//members
	private String[] words;
	private Point[] wordCoords;
	private NPC invoker;
	private boolean advance;
	private NPCChoiceMenu choiceMenu;
	private TextMenu itemMenu;
	private String speakerText;
	private boolean isFast;
	private int currentIndex;
	private String[] leftoverWords;
	private int currentBuffer;
	private boolean showingChoice;
	private boolean showingItem;
	private int currentItem;
	//constructors
	public NPCTextMenu(int width, int height, TextMenu textMenu){
		super(width, height);
		words = null;
		wordCoords = null;
		speakerText = "FREE TEXT";
		invoker = null;
		advance = false;
		isFast = false;
		currentIndex = 0;
		leftoverWords = null;
		currentBuffer = 0;
		currentItem = 0;
		choiceMenu = new NPCChoiceMenu(width, height);
		//use board's text menu as an item menu
		itemMenu = textMenu;
		showingChoice = false;
		showingItem = false;
	}
	//methods
	public void setInvoker(NPC invoker){
		this.invoker = invoker;
		//find a conversation
		invoker.findConversation();
		currentIndex = 0;
	}
	public void setWords(String text){ words = splitText(text); }
	public String[] getWords(){ return words; }
	private String[] splitText(String text){
		//parse the text into words
		return text.split(" ");
	}
	@Override
	public void setXPosition(Position xPos){
		super.setXPosition(xPos);
		choiceMenu.setXPosition(xPos);
	}
	@Override
	public void setYPosition(Position yPos){
		super.setYPosition(yPos);
		choiceMenu.setYPosition(yPos);
	}
	@Override
	public void draw(Graphics g) {
		//item menu open
		if(showingItem){
			itemMenu.draw(g);
		}
		//choice menu open
		else if(showingChoice){
			//only draw it
			choiceMenu.draw(g);
		}
		else{
			//draw this
			super.draw(g);
		}
	}
	public void advance(){ advance = true; }
	private void findWordCoords(FontMetrics fm, int w, int h){
		int wordCount = 0;
		wordCoords = new Point[words.length];
		//find out the stats of the string to display
		int strHeight = fm.getHeight();
		int spaceWidth = fm.stringWidth(" ");
		boolean stillRoom = true;
		//width starts after speaker text
		int lineWidth = fm.stringWidth(speakerText);
		//line height starts at string height for first line
		int lineHeight = strHeight;
		int u;
		//for every word
		for(u = 0; u < words.length && stillRoom; u++){
			String word = words[u];
			int tempWidth = 0;
			//for every character
			for(int i = 0; i < word.length() && stillRoom; i++){
				//grab 1 character
				String ch = word.substring(i, i + 1);
				//find its draw length
				int len = fm.stringWidth(ch);
				//add its length
				tempWidth += len;
			}
			//where x can fit to: if has another line, w, else w minus the cursor area
			int toFitX = lineHeight + strHeight <= h ? w : w - TileConstants.ARROW_ANIMATION.getWidth();
			if(tempWidth + lineWidth <= toFitX){
				//the current line width
				int xPos = lineWidth;
				//add the width and a space
				lineWidth += tempWidth + spaceWidth;
				//record the string coords starting at the beginning of the string
				wordCoords[u] = new Point(xPos, lineHeight);
				wordCount++;
			}
			else{
				//start a new line if possible
				if(lineHeight + strHeight <= h){
					//increment
					lineHeight += strHeight;
					//reset counter
					lineWidth = 0;
					//add the width and a space
					lineWidth += tempWidth + spaceWidth;
					//record the string on the new line
					wordCoords[u] = new Point(0, lineHeight);
					wordCount++;
				}
				else{
					//no more room on the panel
					stillRoom = false;
				}
			}
		}
		//ran out of space
		if(!stillRoom){
			//get the words that were not displayed
			String[] newWords = new String[words.length-u+1];
			//select the ones not displayed from the words
			System.arraycopy(words, u-1, newWords, 0, newWords.length);
			//set the leftover words as the new words
			leftoverWords = newWords;
			//shorten array if need be
			if(wordCount < words.length){
				Point[] newCoords = new Point[wordCount];
				for(int i = 0; i < wordCount; i++){
					newCoords[i] = wordCoords[i];
				}
				wordCoords = newCoords;
			}
		}
	}
	@Override
	public void drawText(Graphics g, int ox, int oy, int w, int h){
		//has words, draw them
		if(words != null){
			//find out the stats of the string to display
			FontMetrics fm = g.getFontMetrics();
			int strHeight = fm.getHeight();
			//draw speaker text
			g.drawString(speakerText, ox, oy + strHeight);
			//if no coords set
			if(wordCoords == null)
				findWordCoords(fm, w, h);
			int i = 0, j = 0, currChar = 0;
			//for every word
			for(i = 0; i < wordCoords.length; i++){
				int currX = wordCoords[i].x;
				int currY = wordCoords[i].y;
				//for every letter up to the current index
				for(j = 0; j < words[i].length() && currChar < currentIndex; j++){
					//grab 1 character
					String ch = words[i].substring(j, j + 1);
					//find its draw length
					int len = fm.stringWidth(ch);
					//draw it
					g.drawString(ch, ox + currX, oy + currY);
					//increment x position
					currX += len;
					currChar++;
				}
			}
			//if went through everything
			if(i == wordCoords.length && j == words[wordCoords.length - 1].length()){
				//draw arrow at a normal speed
				final Animation A = TileConstants.ARROW_ANIMATION;
				g.drawImage(A.nextFrame(), ox + w - A.getWidth(), oy + h - A.getHeight(), null);
				//only if user advancing
				if(advance){
					//reset fields
					wordCoords = null;
					isFast = false;
					currentIndex = 0;
					//there are leftovers
					if(leftoverWords != null){
						words = leftoverWords;
						leftoverWords = null;
					}
					else{//no leftovers
						words = null;
					}
					advance = false;
				}
			}
			else{
				//if advancing but not done
				if(advance){
					//speed up
					isFast = true;
					advance = false;
				}
				//speed to go if fast
				int speed = Options.getOptions().getTextSpeed(isFast);
				//play the blip if not already
				if(!TileAudio.playingSound()) TileAudio.playSound("textBlip");
				//at the buffer
				if(currentBuffer >= TileConstants.MENU_BUFFER){
					//move to the next letter
					currentIndex += speed;
					//reset the buffer
					currentBuffer = 0;
				}
				else{
					currentBuffer++;
				}
			}
		}
	}
	@Override
	public boolean tick(InputTracker input, Board currentBoard){
		boolean stillOpen = true;
		//tick item
		if(showingItem){
			//still showing item if still open
			showingItem = itemMenu.tick(input, currentBoard);
			//done showing items, close
			if(!showingItem){
				//still items to display
				if(currentItem < invoker.itemsGotten().length){
					displayItem();
				}
				else{
					//close window
					stillOpen = false;
					//reset invoker
					invoker.reset();
					currentItem = 0;
				}
			}
		}
		//tick choice
		else if(showingChoice){
			//still showing choice if still open
			showingChoice = choiceMenu.tick(input, currentBoard);
		}
		//tick regular
		else{
			stillOpen = tick(input);
		}
		return stillOpen;
	}
	public NPC getInvoker(){ return invoker; }
	private boolean tick(InputTracker input){
		boolean stillOpen = true;
		if(input.consumeCancel() || input.consumeHubbing()){
			TileAudio.playSound("buzz");
		}
		//if interacting
		if(input.consumeInteract()){
			//set advancing
			advance = true;
		}
		//nothing left, close the window
		if(words == null && !invoker.hasNext()){
			//show the item menu if got items
			if(invoker.itemsGotten().length > 0){
				displayItem();
			}
			else{
				//close menu
				stillOpen = false;
				//reset invoker
				invoker.reset();
				currentItem = 0;
			}
		}
		//has another line to display
		else if(words == null && invoker.hasNext()){
			//a line is next
			if(invoker.hasNextLine()){
				Line next = invoker.nextLine();
				setWords(next.getLine());
				//set who is speaking (p = playerName, n = invokerName, other = custom name)
				setSpeakerText(next.getSpeaker().equals("p") ? TileConstants.PLAYER_NAME : (next.getSpeaker().equals("n") ? invoker.getName() : next.getSpeaker()));
			}
			//choice is next
			else if(invoker.hasNextChoice()){
				//open the choice menu
				choiceMenu.open(invoker, invoker.nextChoice().getChoices());
				showingChoice = true;
			}
		}
		return stillOpen;
	}
	public void setSpeakerText(String name){
		speakerText = name + ":  ";
	}
	private void displayItem(){
		showingItem = true;
		itemMenu.setText("You recieved a(n) " + invoker.itemsGotten()[currentItem] + "!");
		currentItem++;
	}
}
