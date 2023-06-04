package org.emile.client.dialog.core;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class CImagePanel extends JPanel {
	
	 private BufferedImage image;	
	 private int height;
	 private int width;
	
	 public CImagePanel(BufferedImage image, double width, double height) {
		 this.image = image;
		 this.width = (int)(width);
		 this.height = (int)(height);
	 }
	 
	 @Override
	 public void paintComponent(Graphics g) {
		 super.paintComponent(g);
		 g.drawImage(image, 0, 0, height, width, this);
	 }
}

