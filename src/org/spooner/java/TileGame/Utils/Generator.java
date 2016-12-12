package org.spooner.java.TileGame.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.spooner.java.TileGame.Direction;
import org.spooner.java.TileGame.InventoryItem;
import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileGame;
import org.spooner.java.TileGame.TileIO;
import org.spooner.java.TileGame.TileSet;
import org.spooner.java.TileGame.Scenes.Board;
import org.spooner.java.TileGame.Scenes.Tile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Generator {
	private static final String DIRECTORY = "F:\\Documents\\TESTOR\\";
	private static final String DONE_DIRECTORY = DIRECTORY + "\\DONE\\";
	private static final String BOARD_DIRECTORY = "F:\\Documents\\workspace\\TileGame\\assets\\boards\\";
	private static final int TILESET_WIDTH = 10;
	
	public static void main(String[] args) throws IOException{
		TileGame.setTest(true);
		Scanner scan = new Scanner(System.in);
		System.out.println("do what? (i = inventory, o = orbs, t = tileset, m = make image, s = master script, b = make board, c = count tiles)");
		String resp = scan.next();
		if(resp.equals("i")){
			writeInventory(scan, TileConstants.INVENTORY_FILE);
		}
		else if(resp.equals("o")){
			writeInventory(scan, TileConstants.ORB_FILE);
		}
		else if(resp.equals("t")){
			createTileset(scan);
		}
		else if(resp.equals("m")){
			imageify(scan);
		}
		else if(resp.equals("s")){
			masterScript();
		}
		else if(resp.equals("b")){
			boardify(scan);
		}
		else if(resp.equals("c")){
			countTiles();
		}
		else{
			System.out.println("Command unrecognised.");
		}
		scan.close();
	}
	private static void writeInventory(Scanner scan, String file){
		System.out.println("Entries: ");
		int entries = scan.nextInt();
		scan.nextLine();
		InventoryItem[] items = new InventoryItem[entries];
		for(int i = 0; i < items.length; i++){
			System.out.println("Name: ");
			String name = scan.nextLine();
			System.out.println("Desc: ");
			String desc = scan.nextLine();
			items[i] = new InventoryItem(name, desc);
		}
		boolean success = true;
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(items);
			oos.close();
		}
		catch(Exception e){
			//any error means failed
			e.printStackTrace();
			success = false;
		}
		System.out.println(success);
	}
	private static void createTileset(Scanner scan) throws IOException{
		System.out.println("source?");
		String source = scan.next();
		ImageIO.write(findTileSet(source), "png", new File(DONE_DIRECTORY + "\\" + source));
	}
	private static BufferedImage findTileSet(String source) throws IOException{
		ArrayList<SourceTile> uniqueTiles = new ArrayList<SourceTile>();
		BufferedImage inImage = ImageIO.read(new File(DIRECTORY + source));
		//not the right size
		if(inImage.getWidth() % TileConstants.ORG_TILE_SIZE != 0 || inImage.getHeight() % TileConstants.ORG_TILE_SIZE != 0){
			throw new IOException("Not right tile size ( " + TileConstants.ORG_TILE_SIZE + " )");
		}
		else{
			//for every tile
			for(int i = 0; i < inImage.getWidth(); i += TileConstants.ORG_TILE_SIZE){
				for(int j = 0; j < inImage.getHeight(); j += TileConstants.ORG_TILE_SIZE){
					int[][] rgbMatrix = new int[TileConstants.ORG_TILE_SIZE][TileConstants.ORG_TILE_SIZE];
					//for every pixel in the tile
					for(int k = 0; k < TileConstants.ORG_TILE_SIZE; k++){
						for(int l = 0; l < TileConstants.ORG_TILE_SIZE; l++){
							//find the rgb and record it in the matrix
							rgbMatrix[k][l] = inImage.getRGB(i + k, j + l);
						}
					}
					//construct a new tile
					SourceTile t = new SourceTile(rgbMatrix);
					boolean matches = false;
					//for every one already in the list
					for(int h = 0; h < uniqueTiles.size() && !matches; h++){
						//check if it is equal
						if(t.equals(uniqueTiles.get(h))){
							matches = true;
						}
					}
					//if it is a new tile, add it
					if(!matches){
						uniqueTiles.add(t);
					}
				}
			}
			return generateImage(uniqueTiles);
		}
	}
	private static BufferedImage generateImage(ArrayList<SourceTile> tiles) throws IOException{
		//sort the tiles by average RGB
		Collections.sort(tiles);
		final int WIDTH = TILESET_WIDTH * TileConstants.ORG_TILE_SIZE;
		//find the height needed (+1 for extra room)
		int h = tiles.size() / TILESET_WIDTH + 1;
		final int HEIGHT = h * TileConstants.ORG_TILE_SIZE;
		BufferedImage outImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = outImage.createGraphics();
		Iterator<SourceTile> iter = tiles.iterator();
		//for every tile, across then down
		for(int i = 0; i < HEIGHT && iter.hasNext(); i += TileConstants.ORG_TILE_SIZE){
			for(int j = 0; j < WIDTH && iter.hasNext(); j += TileConstants.ORG_TILE_SIZE){
				//for every coord in tile
				SourceTile t = iter.next();
				for(int k = 0; k < TileConstants.ORG_TILE_SIZE; k++){
					for(int l = 0; l < TileConstants.ORG_TILE_SIZE; l++){
						//find the color
						g2.setColor(new Color(t.getRGB(k, l)));
						//paint a pixel at the location
						g2.fillRect(j + k, i + l, 1, 1);
					}
				}
			}
		}
		g2.dispose();
		return outImage;
	}
	private static void imageify(Scanner scan) throws IOException{
		System.out.println("source?");
		String source = scan.next();
		//load the board
		Board b = TileIO.parseBoard(source, "0", Direction.NONE);
		//create an image of the size
		BufferedImage outImage = new BufferedImage(b.getWidth() / TileConstants.TILE_SCALE, 
				b.getHeight() / TileConstants.TILE_SCALE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = outImage.createGraphics();
		for(int i = 0; i < b.getTileWidth(); i++){
			for(int j = 0; j < b.getTileHeight(); j++){
				//get the image of the current tile with the tileset
				BufferedImage tile = TileSet.currentSet().getIcon(b.getTileAt(i, j).getID());
				//draw it
				g2.drawImage(tile, i * tile.getWidth(), j * tile.getHeight(), null);
				//draw foreground if there
				if(b.getTileAt(i, j).hasForeground()){
					BufferedImage fore = TileSet.foregroundSet().getIcon(b.getTileAt(i, j).getForeID());
					//draw it
					g2.drawImage(fore, i * fore.getWidth(), j * fore.getHeight(), null);
				}
			}
		}
		g2.dispose();
		//remove .xml, and .png
		String name = source.substring(0, source.length() - 3) + ".png";
		//write to file
		ImageIO.write(outImage, "png", new File(DONE_DIRECTORY + name));
	}
	private static void masterScript(){
		File dir = new File(TileConstants.CONVERSATION_PATH);
		String output = "";
		for(File f : dir.listFiles()){
			if(f.isDirectory()){
				for(File l : f.listFiles()){
					Document doc = TileIO.getDocument(l.getAbsolutePath());
					output += l.getName() + "\n==============================\n";
					NodeList nl = doc.getElementsByTagName("line");
					for(int i = 0; i < nl.getLength(); i++){
						output += nl.item(i).getAttributes().getNamedItem("talk") + ": \t" + nl.item(i).getTextContent() + "\n";
					}
				}
			}
			else{
				Document doc = TileIO.getDocument(f.getAbsolutePath());
				output += f.getName() + "\n==============================\n";
				NodeList nl = doc.getElementsByTagName("line");
				for(int i = 0; i < nl.getLength(); i++){
					output += nl.item(i).getAttributes().getNamedItem("talk") + ": \t" + nl.item(i).getTextContent() + "\n";
				}
			}
		}
		int previous = output.indexOf("\n");
		int nCount = previous == -1 ? 0 : 1;
		while(previous != -1){
			previous = output.indexOf("\n", previous + 1);
			nCount++;
		}
		System.out.println("Number of lines: " + nCount);
		System.out.println("Number of pages: " + (nCount / 54.0));
		try{
			FileOutputStream fos = new FileOutputStream(new File(DONE_DIRECTORY + "masterScript.txt"));
			fos.write(output.getBytes());
			fos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void countTiles(){
		int count = 0;
		for(File f : new File(BOARD_DIRECTORY).listFiles()){
			Board b = TileIO.parseBoard(f.getName(), "0", Direction.UP);
			count += b.getTileHeight() * b.getTileWidth();
		}
		System.out.println("Number of tiles: " + count);
	}
	private static void boardify(Scanner scan){
		System.out.println("source?");
		String source = scan.next();
		//remove .png, and add .xml
		String name = source.substring(0, source.length() - 3) + ".xml";
		System.out.println("tileset?");
		int tileset = scan.nextInt();
		TileSet.changeSet(tileset);
		BufferedImage image = TileIO.loadImage(DIRECTORY + source);
		System.out.println(image.getHeight());
		Tile[][] tiles = new Tile[image.getWidth() / TileConstants.ORG_TILE_SIZE][image.getHeight() / TileConstants.ORG_TILE_SIZE];
		for(int i = 0; i < image.getWidth(); i += TileConstants.ORG_TILE_SIZE){
			for(int j = 0; j < image.getHeight(); j += TileConstants.ORG_TILE_SIZE){
				int id = -1;
				for(int k = 0; k < TileSet.currentSet().numIcons() && id == -1; k++){
					if(equalTiles(TileSet.currentSet().getIcon(k), image.getSubimage(i, j, TileConstants.ORG_TILE_SIZE, TileConstants.ORG_TILE_SIZE)))
						id = k;
				}
				tiles[i / TileConstants.ORG_TILE_SIZE][j / TileConstants.ORG_TILE_SIZE] = new Tile(id, new boolean[]{false, false, false, false}, -1);
			}
		}
		writeXML(tiles, new File(DONE_DIRECTORY + name));
	}
	private static boolean equalTiles(BufferedImage b1, BufferedImage b2){
		boolean equal = true;
		int i = 0;
		while(equal && i < TileConstants.ORG_TILE_SIZE){
			int j = 0;
			while(equal && j < TileConstants.ORG_TILE_SIZE){
				equal = b1.getRGB(i, j) == b2.getRGB(i, j);
				j++;
			}
			i++;
		}
		return equal;
	}
	private static void writeXML(Tile[][] tiles, File path){
		try{
			//create a new document
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			//create and append the root element
			doc.appendChild(doc.createElement("board"));
			//append a text node to make it look nicer
			doc.getDocumentElement().appendChild(newLine(doc));
			//build the tiles onto the document
			buildTiles(doc, tiles);
			//create and append the tileset
			Element tileSet = doc.createElement("tile_set");
			tileSet.appendChild(doc.createTextNode(Integer.toString(TileSet.currentSet().getID())));
			doc.getDocumentElement().appendChild(tileSet);
			doc.getDocumentElement().appendChild(newLine(doc));
			//create the source object and stream result
			DOMSource source = new DOMSource(doc);
			StreamResult fileResult = new StreamResult(path);
			//use a transformer to write to file
			TransformerFactory.newInstance().newTransformer().transform(source, fileResult);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void buildTiles(Document doc, Tile[][] tiles){
		Element root = doc.getDocumentElement();
		//for each line
		for(int i = 0; i < tiles.length; i++){
			//create and append a child to the root
			Element line = doc.createElement("line");
			root.appendChild(line);
			//for each tile
			for(int j = 0; j < tiles[i].length; j++){
				Element tileElement = doc.createElement("tile");
				//append the text node as the id
				tileElement.appendChild(doc.createTextNode(Integer.toString(tiles[i][j].getID())));
				line.appendChild(tileElement);
			}
			//append a text node to make it look nicer
			root.appendChild(newLine(doc));
		}
	}
	private static Text newLine(Document doc){
		return doc.createTextNode("\n");
	}
}
