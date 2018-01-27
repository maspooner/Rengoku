package org.spooner.java.TileGame;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.spooner.java.TileGame.Entities.LinearNPC;
import org.spooner.java.TileGame.Entities.NPC;
import org.spooner.java.TileGame.Entities.PlanarNPC;
import org.spooner.java.TileGame.Entities.StationaryNPC;
import org.spooner.java.TileGame.Menus.TextMenu;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.BoardChangeTile;
import org.spooner.java.TileGame.Scenes.ItemTile;
import org.spooner.java.TileGame.Scenes.Tile;
import org.spooner.java.TileGame.Speech.ChoiceLine;
import org.spooner.java.TileGame.Speech.Conversation;
import org.spooner.java.TileGame.Speech.Line;
import org.spooner.java.TileGame.Speech.RandomLine;
import org.spooner.java.TileGame.Speech.Speakable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TileIO {
	//methods
	public static String[] listFiles(String dir){
		//regular file list
		if(TileGame.isTest()){
			return new File(dir).list();
		}
		else{
			//in a jar file
			ArrayList<String> files = new ArrayList<String>();
			try{
				//find this class
				URL jarURL = TileIO.class.getClassLoader().getResource(TileIO.class.getName().replace(".", "/") + ".class");
				//find the location of the jar file by editting its path
				String jarLoc = jarURL.getPath().substring(5, jarURL.getPath().indexOf('!'));
				JarFile jar = new JarFile(jarLoc.replaceAll("%20", " "));
				//find all the files in the jar file
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements()){
					//search through and find a name
					String name = entries.nextElement().getName();
					//if the name isn't the directory and it's in the directory
					if(!name.equals(dir) && name.startsWith(dir)){
						//grab the file name and add it
						files.add(name.substring(name.lastIndexOf('/') + 1, name.length()));
					}
				}
				jar.close();
			}
			catch(Exception e){
				e.printStackTrace();
				System.exit(-1);
			}
			return files.toArray(new String[files.size()]);
		}
	}
	private static InputStream getStream(String path){
		return ClassLoader.class.getResourceAsStream("/" + path);
	}
	public static Font loadFont(String path, int style, int size){
		try{
			Font f = TileGame.isTest() ? Font.createFont(Font.TRUETYPE_FONT, new File(path)) : Font.createFont(Font.TRUETYPE_FONT, getStream(path));
			return f.deriveFont(style, size);
		}
		catch (FontFormatException | IOException e){
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	public static BufferedImage loadImage(String path){
		try {
			return TileGame.isTest() ? ImageIO.read(new File(path)) : ImageIO.read(getStream(path));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	public static Board parseBoard(String xmlPath, String startID, Direction face){
		//load xml
		Document doc = getDocument(TileConstants.BOARD_PATH + xmlPath);
		//get rid of text white space
		doc.getDocumentElement().normalize();
		//load tiles with the document
		Tile[][] ta = parseTiles(doc);
		//node containing tile set
		Node set = doc.getElementsByTagName("tile_set").item(0);
		//change tileset
		TileSet.changeSet(Integer.parseInt(set.getTextContent()));
		//load music if present
		String musicName = null;
		if(doc.getElementsByTagName("music").getLength() > 0){
			Element music = (Element) doc.getElementsByTagName("music").item(0);
			musicName = music.getTextContent();
		}
		//load nametag if there
		TextMenu nametagMenu = null;
		if(doc.getElementsByTagName("nametag").getLength() > 0){
			//a menu without an arrow
			nametagMenu = new TextMenu(TileConstants.NAMETAG_MENU_WIDTH, TileConstants.NAMETAG_MENU_HEIGHT, false);
			//set the text to the nametag
			nametagMenu.setText(doc.getElementsByTagName("nametag").item(0).getTextContent());
		}
		//load fast tag if there
		boolean isFast = doc.getElementsByTagName("fast").getLength() > 0;
		//load start point (R means special reload location)
		Point start = startID.startsWith("R") ? parseReload(startID) : parsePoint(doc.getDocumentElement(), "start_pos", Integer.parseInt(startID));
		//load NPCs
		NodeList npcNodes = doc.getElementsByTagName("NPC");
		NPC[] npcs = new NPC[npcNodes.getLength()];
		for(int i = 0; i < npcNodes.getLength(); i++){
			npcs[i] = parseNPC((Element) npcNodes.item(i));
		}
		return new Board(xmlPath, nametagMenu, ta, npcs, start, face, musicName, isFast);
	}
	private static Point parseReload(String reload){
		//chop off R
		String rest = reload.substring(reload.indexOf("R") + 1);
		String[] pieces = rest.split(" ");
		return new Point(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]));
	}
	public static Document getDocument(String xmlPath){
		Document doc=null;
		try {
			DocumentBuilder dcb = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = TileGame.isTest() ? dcb.parse(new File(xmlPath)) : dcb.parse(getStream(xmlPath));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
		return doc;
	}
	private static Tile[][] parseTiles(Document doc){
		//get a list of the lines
		NodeList lines = doc.getElementsByTagName("line");
		//find the number of columns by looking at the first line and counting the number of tile tags
		int columns = ((Element) lines.item(0)).getElementsByTagName("tile").getLength();
		//construct the tile matrix
		Tile[][] tileMatrix = new Tile[lines.getLength()][columns];
		//for every line
		for(int i = 0; i < lines.getLength(); i++){
			//find the line element
			Element lineElement = (Element) lines.item(i);
			//get a list of the tiles
			NodeList tiles = lineElement.getElementsByTagName("tile");
			//for every tile
			for(int j = 0; j < tiles.getLength(); j++){
				//parse the tile element
				tileMatrix[i][j] = parseTile((Element) tiles.item(j));
			}
		}
		return tileMatrix;
	}
	private static NPC parseNPC(Element npcElement){
		NPC npc = null;
		//only create NPC if flags say so
		if(NPCisPresent(npcElement)){
			//load name
			String name = npcElement.getElementsByTagName("name").item(0).getTextContent();
			//load sprite alias
			NodeList sAlias = npcElement.getElementsByTagName("s_alias");
			String spriteAlias = sAlias.getLength() == 0 ? name : sAlias.item(0).getTextContent();
			//if no alias, use the name
			if(spriteAlias.isEmpty()) spriteAlias = name;
			//load converse alias
			NodeList cAlias = npcElement.getElementsByTagName("c_alias");
			String conAlias = cAlias.getLength() == 0 ? name : cAlias.item(0).getTextContent();
			//if no alias, use the name
			if(conAlias.isEmpty()) conAlias = name;
			//load conversation location
			String conLocation = "con_" + conAlias + ".xml";
			//load conversations
			Conversation[] converses = parseConversations(conLocation);
			//load starting point
			Point start = parsePoint(npcElement, "start_pos");
			//load limit point
			Point limit = parsePoint(npcElement, "limit_point");
			//decide which type of movement it has
			//both points are the same
			if(start.equals(limit)){
				npc = new StationaryNPC(name, spriteAlias, converses, start, limit);
			}
			//one point is the same
			else if(start.x == limit.x || start.y == limit.y){
				npc = new LinearNPC(name, spriteAlias, converses, start, limit);
			}
			//no points similar
			else{
				npc = new PlanarNPC(name, spriteAlias, converses, start, limit);
			}
		}
		return npc;
	}
	private static boolean NPCisPresent(Element npcElement){
		NodeList presNodes = npcElement.getElementsByTagName("pres");
		//has a pres node, possible not to be present
		if(presNodes.getLength() > 0){
			boolean isPresent = true;
			for(int p = 0; p < presNodes.getLength(); p++){
				Node pres = presNodes.item(p);
				NamedNodeMap nnm = pres.getAttributes();
				//go through flag types until not hasAll
				boolean hasAll = true;
				for(int i = 0; i < nnm.getLength() && hasAll; i++){
					Node n = nnm.item(i);
					String[] parts = n.getTextContent().split(",");
					for(int j = 0; j < parts.length && hasAll; j++){
						hasAll = Flags.getFlags().hasFlag(n.getNodeName(), Integer.parseInt(parts[j]));
					}
				}
				//npc present if not all flags are present
				boolean present = !hasAll;
				//no special message? normal, else inverted
				isPresent &= presNodes.item(p).getTextContent().isEmpty() ? present : !present;
			}
			return isPresent;
		}
		//default is present
		return true;
	}
	public static Conversation[] parseConversations(String conLocation){
		//load xml
		Document doc = getDocument(TileConstants.CONVERSATION_PATH + conLocation);
		//get rid of text white space
		doc.getDocumentElement().normalize();
		int numCons = doc.getElementsByTagName("converse").getLength();
		Conversation[] converses = new Conversation[numCons];
		for(int i = 0; i < numCons; i++){
			converses[i] = parseConversation(doc.getElementsByTagName("converse").item(i));
		}
		return converses;
	}
	private static int[] parseAttribute(String attr){
		//split by comma
		String[] strIDs = attr.split(",");
		int[] ids = new int[strIDs.length];
		//fill the array with ids
		for(int i = 0; i < ids.length; i++){
			ids[i] = Integer.parseInt(strIDs[i]);
		}
		return ids;
	}
	private static Conversation parseConversation(Node conNode){
		//attributes
		HashMap<String, int[]> attributes = null;
		if(conNode.hasAttributes()){
			NamedNodeMap attriMap = conNode.getAttributes();
			attributes = new HashMap<String, int[]>(1);
			for(int i = 0; i < attriMap.getLength(); i++){
				//name, ids
				attributes.put(attriMap.item(i).getNodeName(),
						parseAttribute(attriMap.item(i).getTextContent()));
			}
		}
		//actual children
		NodeList children = conNode.getChildNodes();
		ArrayList<Speakable> speakList = new ArrayList<Speakable>();
		for(int i = 0; i < children.getLength(); i++){
			Node n = children.item(i);
			if(n.getNodeName().equals("line")){
				String whoSpeaks = n.getAttributes().getNamedItem("talk").getTextContent();
				speakList.add(new Line(whoSpeaks, n.getTextContent()));
			}
			else if(n.getNodeName().equals("randoms")){
				speakList.add(parseSpeakable(n, true));
			}
			else if(n.getNodeName().equals("choices")){
				speakList.add(parseSpeakable(n, false));
			}
		}
		//make the list into an array
		Speakable[] speaks = new Speakable[speakList.size()];
		speaks = speakList.toArray(speaks);
		//conversation with/without attributes
		return new Conversation(speaks, attributes);
	}
	private static Speakable parseSpeakable(Node n, boolean isRandom){
		String tag = isRandom ? "random" : "choice";
		NodeList children = n.getChildNodes();
		int subCount = 0;
		//count number of special tags
		for(int j = 0; j < children.getLength(); j++){
			if(children.item(j).getNodeName().equals(tag)) subCount++;
		}
		Conversation[] subCons = new Conversation[subCount];
		int currSub = 0;
		//fill array
		for(int k = 0; k < children.getLength() && currSub < subCount; k++){
			Node child = children.item(k);
			if(child.getNodeName().equals(tag)){
				//recursion!
				subCons[currSub] = parseConversation(child);
				currSub++;
			}
		}
		return isRandom ? new RandomLine(subCons) : new ChoiceLine(subCons);
	}
	private static Point parsePoint(Element parent, String tagName){
		return parsePoint(parent, tagName, 0);
	}
	private static Point parsePoint(Element parent, String tagName, int startID){
		//get node that houses x&y
		Element child = (Element) parent.getElementsByTagName(tagName).item(startID);
		//get x&y
		return new Point(Integer.parseInt(child.getElementsByTagName("x").item(0).getTextContent()), 
				Integer.parseInt(child.getElementsByTagName("y").item(0).getTextContent()));
	}
	private static Tile parseTile(Element tileElement){
		Tile tile = null;
		//tile id attribute
		int tileID = Integer.parseInt(tileElement.getTextContent());
		//blocked attribute, false array if not present
		boolean[] blocked = parseBlocked(tileElement.getAttribute("blocked"));
		//board id attribute
		String boardID = tileElement.getAttribute("bid");
		//board id attribute
		String after = tileElement.getAttribute("after");
		//foreground attribute
		String foreground = tileElement.getAttribute("fore");
		//default is no foreground
		int foreID = -1;
		//has foreground
		if(!foreground.isEmpty()){
			foreID = Integer.parseInt(foreground);
		}
		//if has board id
		if(!boardID.isEmpty()){
			//board start id attribute there
			int startID = Integer.parseInt(tileElement.getAttribute("spid"));
			//face attribute is there
			int face = Integer.parseInt(tileElement.getAttribute("face"));
			//NOTE: discards any blocked info
			tile = new BoardChangeTile(tileID, boardID, foreID, startID, Direction.getDirection(face));
		}
		//is an item tile
		else if(!after.isEmpty()){
			//item attribute there
			int item = Integer.parseInt(tileElement.getAttribute("item"));
			//fire attribute is there
			int fire = Integer.parseInt(tileElement.getAttribute("fire"));
			//NOTE: discards any blocked info
			ItemTile it = new ItemTile(tileID, Integer.parseInt(after), item, fire);
			//if can recieve the item, use that tile, else, use the replacement (normal tile)
			tile = it.canRecieve() ? it : it.replacement();
		}
		else{
			//normal tile
			tile = new Tile(tileID, blocked, foreID);
		}
		return tile;
	}
	private static boolean[] parseBlocked(String blockedString){
		boolean[] blocked = new boolean[4];
		for(int i = 0; i < blocked.length && !blockedString.isEmpty(); i++){
			//side blocked if a c at the index
			blocked[i] = blockedString.charAt(i) == 'b';
		}
		return blocked;
	}
	public static BufferedImage[] loadAnimation(String path){
		BufferedImage[] bia;
		String[] images = listFiles(path);
		bia = new BufferedImage[images.length];
		//for every image
		for(int i = 0; i < images.length; i++){
			bia[i] = loadImage(path + images[i]);
		}
		return bia;
	}
	public static Flags loadFlags(){
		Flags flags;
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TileConstants.FLAGS_FILE));
			flags = (Flags) ois.readObject();
			ois.close();
		}
		catch (Exception e){
			//any error means new flags
			flags = new Flags();
		}
		return flags;
	}
	public static InventoryItem[] loadInventory(){
		InventoryItem[] items = null;
		try{
			ObjectInputStream ois = new ObjectInputStream(TileGame.isTest() ? 
					new FileInputStream(TileConstants.INVENTORY_FILE) : getStream(TileConstants.INVENTORY_FILE));
			items = (InventoryItem[]) ois.readObject();
			ois.close();
		}
		catch (Exception e){
			//any error means fatal error
			e.printStackTrace();
			System.exit(-1);
		}
		return items;
	}
	public static InventoryItem[] loadOrbs(){
		InventoryItem[] orbs = null;
		try{
			ObjectInputStream ois = new ObjectInputStream(TileGame.isTest() ? 
					new FileInputStream(TileConstants.ORB_FILE) : getStream(TileConstants.ORB_FILE));
			orbs = (InventoryItem[]) ois.readObject();
			ois.close();
		}
		catch (Exception e){
			//any error means fatal error
			e.printStackTrace();
			System.exit(-1);
		}
		return orbs;
	}
	public static Options loadOptions(){
		Options options;
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TileConstants.OPTIONS_FILE));
			options = (Options) ois.readObject();
			ois.close();
		}
		catch (Exception e){
			//any error means new options
			options = new Options();
		}
		return options;
	}
	public static boolean saveFlags(){
		boolean success = true;
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TileConstants.FLAGS_FILE));
			//flags hold saved game now
			Flags.getFlags().decay();
			oos.writeObject(Flags.getFlags());
			oos.close();
		}
		catch(Exception e){
			//any error means failed
			success = false;
		}
		return success;
	}
	public static boolean saveOptions(){
		boolean success = true;
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TileConstants.OPTIONS_FILE));
			oos.writeObject(Options.getOptions());
			oos.close();
		}
		catch(Exception e){
			//any error means failed
			success = false;
		}
		return success;
	}
}
