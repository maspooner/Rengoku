package org.spooner.java.TileGame.Utils;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.spooner.java.TileGame.TileConstants;
import org.spooner.java.TileGame.TileGame;


public class SpriteMod {
	private static final String DIRECTORY = "F:\\Documents\\TESTOR\\";
	private static final String DONE_DIRECTORY = DIRECTORY + "\\DONE\\";
	private static final int PLAYER_WIDTH = TileConstants.PLAYER_WIDTH * 2;
	private static final int PLAYER_HEIGHT = TileConstants.PLAYER_HEIGHT * 2;
	
	public static void main(String[] args) throws IOException{
		TileGame.setTest(true);
		Scanner scan = new Scanner(System.in);
		System.out.println("folder name? ");
		String dirName = scan.next();
		System.out.println("do what? (t for transperency, a for animation, c for color)");
		String ans = scan.next();
		if(ans.equals("t")){
			File dirFile = new File(DIRECTORY + dirName);
			if(dirFile.exists()){
				alphafyDirectory(dirName);
			}
			else{
				System.out.println("directory not found");
			}
		}
		else if(ans.equals("a")){
			System.out.println("default? (y/n)");
			String upDir, downDir, leftDir;
			upDir = downDir = leftDir = dirName + "/";
			String rightDir = dirName + "/right", moveDir = dirName + "/Move", stopDir = dirName + "/Stop";
			if(scan.next().equals("n")){
				System.out.println("up name?");
				upDir += scan.next();
				System.out.println("down name?");
				downDir += scan.next();
				System.out.println("left name?");
				leftDir += scan.next();
			}
			else{
				upDir += "up";
				downDir += "down";
				leftDir += "left";
			}
			//animate the move directories
			animateDirectory(upDir, moveDir + "/0");
			animateDirectory(downDir, moveDir + "/1");
			animateDirectory(leftDir, moveDir + "/2");
			//create a right directory
			flipDirectory(leftDir, rightDir);
			animateDirectory(rightDir, moveDir + "/3");
			//create the stop animations
			createStops(moveDir, stopDir);
		}
		else if(ans.equals("c")){
			System.out.println("num colors? ");
			int numColors = scan.nextInt();
			int[] modColors = new int[numColors];
			int[] newColors = new int[numColors];
			for(int i = 0; i < modColors.length; i++){
				System.out.println("red? ");
				int r = scan.nextInt();
				System.out.println("green? ");
				int g = scan.nextInt();
				System.out.println("blue? ");
				int b = scan.nextInt();
				modColors[i] = RGBtoARGB(new int[]{r, g, b});
				System.out.println("new red? ");
				int nr = scan.nextInt();
				System.out.println("new green? ");
				int ng = scan.nextInt();
				System.out.println("new blue? ");
				int nb = scan.nextInt();
				newColors[i] = RGBtoARGB(new int[]{nr, ng, nb});
			}
			colorizeDirectory(dirName, modColors, newColors);
		}
		else{
			System.out.println("command unrecognized");
		}
		scan.close();
	}
	private static void createStops(String moveDir, String stopDir) throws IOException{
		for(int i = 0; i < 4; i++){
			//grab the first image from the done pile
			File from = new File(DONE_DIRECTORY + moveDir + "/" + i + "/0.png");
			//where the done files go
			File to = new File(DONE_DIRECTORY + stopDir + "/" + i + "/0.png");
			//mark path if not there
			if(!to.getParentFile().exists())
				to.getParentFile().mkdirs();
			//delete if already there
			if(to.exists())
				to.delete();
			//copy
			Files.copy(from.toPath(), to.toPath());
		}
	}
	private static void animateDirectory(String homeDir, String newDir) throws IOException{
		//where the done files go
		File doneDir = new File(DONE_DIRECTORY + newDir + "\\");
		if(!doneDir.exists())
			 doneDir.mkdirs();
		File fromDir = new File(DIRECTORY + homeDir + "\\");
		int i = 0;
		for(File pic : fromDir.listFiles()){
			//resize and alphafy
			BufferedImage fixedImage = makeTransperent(resizeImage(pic));
			ImageIO.write(fixedImage, "png", new File(doneDir.getAbsolutePath() + "\\" + i + ".png"));
			i++;
		}
	}
	private static void flipDirectory(String source, String newDir) throws IOException{
		//where the done files go
		File doneDir = new File(DIRECTORY + newDir + "\\");
		if(!doneDir.exists())
			 doneDir.mkdirs();
		File fromDir = new File(DIRECTORY + source + "\\");
		int i = 0;
		for(File f : fromDir.listFiles()){
			if(f.isDirectory()){
				flipDirectory(source + "\\" + f.getName(), newDir + "\\" + f.getName());
			}
			else{
				//flip and write
				BufferedImage inImage = ImageIO.read(f);
				BufferedImage flippedImage = flipImage(inImage);
				ImageIO.write(flippedImage, "png", new File(doneDir.getAbsolutePath() + "\\" + i + ".png"));
				i++;
			}
		}
	}
	private static void colorizeDirectory(String homeDir, int[] modColors, int[] newColors) throws IOException{
		//where the done files go
		File doneDir = new File(DONE_DIRECTORY + homeDir + "\\");
		if(!doneDir.exists())
			 doneDir.mkdirs();
		File fromDir = new File(DIRECTORY + homeDir + "\\");
		int i = 0;
		for(File f : fromDir.listFiles()){
			if(f.isDirectory()){
				colorizeDirectory(homeDir + "\\" + f.getName(), modColors, newColors);
			}
			else{
				//colorize
				RGBImageFilter rgbFilter = new RGBImageFilter(){
					@Override
					public int filterRGB(int x, int y, int rgb){
						boolean found = false;
						for(int i = 0; i < modColors.length && !found; i++){
							if(rgb == modColors[i]){
								rgb = newColors[i];
								found = true;
							}
						}
						return rgb;
					}
				};
				BufferedImage fixedImage = filter(ImageIO.read(f), rgbFilter);
				ImageIO.write(fixedImage, "png", new File(doneDir.getAbsolutePath() + "\\" + i + ".png"));
				i++;
			}
		}
	}
	private static BufferedImage flipImage(BufferedImage inImage){
		//flip x values to negative
		AffineTransform transform = AffineTransform.getScaleInstance(-1, 1);
		//move back onto visable field
		transform.translate(-inImage.getWidth(), 0);
		//convert it to argb type
		BufferedImage newImage = new BufferedImage(inImage.getWidth(), inImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		g2.drawImage(inImage, transform, null);
		g2.dispose();
		return newImage;
	}
	private static BufferedImage resizeImage(File imageFile) throws IOException{
		BufferedImage image = ImageIO.read(imageFile);
		//not to size
		if(image.getHeight() != PLAYER_HEIGHT || image.getWidth() != PLAYER_WIDTH){
			BufferedImage fixed = new BufferedImage(PLAYER_WIDTH, PLAYER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = fixed.createGraphics();
			g2.drawImage(image, 0, 0, null);
			g2.dispose();
			//set the image
			image = fixed;
		}
		return image;
	}
	private static void alphafyDirectory(String homeDir) throws IOException{
		//where the done files go
		File doneDir = new File(DONE_DIRECTORY + homeDir + "\\");
		if(!doneDir.exists())
			 doneDir.mkdirs();
		File fromDir = new File(DIRECTORY + homeDir + "\\");
		for(File f : fromDir.listFiles()){
			if(f.isDirectory()){
				alphafyDirectory(homeDir + "\\" + f.getName());
			}
			else{
				//write a png
				BufferedImage inImage = ImageIO.read(f);
				ImageIO.write(makeTransperent(inImage), "png", new File(doneDir.getAbsolutePath() + "\\" + f.getName()));
			}
		}
	}
	private static int RGBtoARGB(int[] rgb){
		int r = rgb[0] << 16; //push it to the first position (over 16 / 4 = 4 bytes)
		int g = rgb[1] << 8; //push it to the second position (over 8 / 4 = 2 bytes)
		int b = rgb[2]; //no push
		//convert to one argb with an alpha mask of opaque
		return 0xFF000000 | r | g | b;
	}
	private static BufferedImage filter(BufferedImage inImage, RGBImageFilter filter){
		//filter it into a new image
		Image outImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(inImage.getSource(), filter));
		//create a blank image to put the new image onto
		BufferedImage buffOut = new BufferedImage(outImage.getWidth(null), outImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = buffOut.createGraphics();
		g2.drawImage(outImage, 0, 0, null);
		g2.dispose();
		return buffOut;
	}
	private static BufferedImage makeTransperent(BufferedImage inImage) throws IOException{
		//store all four in order
		int[] argbBack = new int[4];
		//read the background argb
		argbBack = inImage.getData().getPixel(0, 0, argbBack);
		final int background = RGBtoARGB(argbBack);
		//create a filter to filter out the background
		RGBImageFilter rgbFilter = new RGBImageFilter() {
			@Override
			public int filterRGB(int x, int y, int argb) {
				if(argb == background){
					//set the alpha component to 0 with a bit mask
					argb &= 0x00FFFFFF;
				}
				return argb;
			}
		};
		return filter(inImage, rgbFilter);
	}
}
