package org.spooner.java.TileGame.Utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Installer extends JFrame implements MouseListener{
	//members
	private static final int FRAME_WIDTH = 500;
	private static final int FRAME_HEIGHT = 500;
	private static final Font FONT = new Font("serif", Font.BOLD, 22);
	private BufferedImage wizard;
	private JFileChooser chooser;
	private String line1, line2;
	private Object lock;
	private boolean isTest;
	//constructors
	private Installer(boolean isTest){
		this.isTest = isTest;
		lock = new Object();
		wizard = loadImage("wizard.png");
		line1 = "Welcome, my child. I hear you";
		line2 = "require my services.";
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("Rengoku Wizard");
		setIconImage(loadImage("wizIcon.png"));
		setResizable(false);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(size.width / 2 - FRAME_WIDTH / 2, size.height / 2 - FRAME_HEIGHT / 2);
		setVisible(true);
		addMouseListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		step();
	}
	//methods
	@Override
	public void paint(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(wizard, 0, 0, FRAME_WIDTH, FRAME_HEIGHT, null);
		g2.setColor(Color.CYAN);
		g2.setFont(FONT);
		g2.drawString(line1, 50, 400);
		g2.drawString(line2, 50, 420);
		g2.drawString("[Click to continue]", 150, 480);
	}
	private void step(){
		waitForClick();
		changeLines("Well, it's nothing a wizard", "like me can't do.");
		waitForClick();
		changeLines("Alright, I'm going to magic up", "the folder with the game files");
		waitForClick();
		changeLines("into a folder of your choice.", "Get excited.");
		waitForClick();
		String destPath = getInstallDir();
		changeLines("All set?", "Excellent.");
		waitForClick();
		changeLines("One... Two... Three...", "BADABOOSH!");
		waitForClick();
		changeLines("Please wait...", "");
		doInstall(destPath);
		changeLines("It is done.", "Enjoy your game!");
		waitForClick();
		changeLines("Wizard, away!", "");
		waitForClick();
		System.exit(0);
	}
	private void changeLines(String line1, String line2){
		this.line1 = line1;
		this.line2 = line2;
		repaint();
	}
	private String getInstallDir(){
		int id = -1;
		do{
			id = chooser.showOpenDialog(this);
		}while(id != JFileChooser.APPROVE_OPTION);
		return chooser.getSelectedFile().getAbsolutePath();
	}
	private void doInstall(String destPath){
		try{
			File progDir = new File(destPath + "/Rengoku");
			if(!progDir.exists())
				progDir.mkdir();
			copy("Rengoku.jar", progDir.getAbsolutePath());
			copy("joy", progDir.getAbsolutePath());
			copy("icoIcon.ico", progDir.getAbsolutePath());
			//create shortcut
			String sLink = destPath + "/Rengoku.lnk";
			System.out.println("\n\n\n");
			String sIcon = progDir.getAbsolutePath() + "/icoIcon.ico";
			String sTarget = progDir.getAbsolutePath() + "/Rengoku.jar";
			String sWork = progDir.getAbsolutePath();
			Runtime.getRuntime().exec("Shortcut.exe /F:" + sLink + " /A:C /I:" + sIcon + " /T:" + sTarget + " /W:" + sWork);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	private void copy(String file, String destPath) throws IOException{
		File sourceFile = new File(file);
		Path source = Paths.get(file).toAbsolutePath();
		System.out.println(source);
		Path dest = Paths.get(destPath + "/" + file);
		System.out.println(dest);
		Files.copy(source, dest);
		if(sourceFile.isDirectory()){
			for(File f : sourceFile.listFiles()){
				copy(sourceFile.getName() + "/" + f.getName(), destPath);
			}
		}
	}
	private void waitForClick(){
		try{
			synchronized(lock){
				lock.wait();
			}
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
			System.exit(-1);
		}
	}
	private BufferedImage loadImage(String path){
		try {
			return isTest ? ImageIO.read(new File(path)) : ImageIO.read(ClassLoader.class.getResourceAsStream("/" + path));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	public static void main(String[] args){
		new Installer(args.length > 0);
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent me) {
		synchronized(lock){
			lock.notify();
		}
	}
}
