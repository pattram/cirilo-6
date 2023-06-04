package org.emile.client.dialog.core;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;
import org.emile.cirilo.fedora.FedoraConnector;

public class ImageViewer {
	
	private static Logger log = Logger.getLogger(ImageViewer.class);

	private FedoraConnector connector;
	
	public ImageViewer(FedoraConnector connector) {
		this.connector = connector;
	}
	
	public CImagePanel get(String mimetype, Dimension panelsize, String pid, String dsid) {
				
		CImagePanel panel = null;
		BufferedImage bufferedImage = null;
		InputStream ins;
		
        try {      
        	if (mimetype.contains("/tiff")) {
        		ImageReader reader = null;
        		Iterator<ImageReader> iteratorIO = null;
        		int pages = 0;
        		try {
        			ins = new ByteArrayInputStream(connector.stubGetDatastream(pid, dsid));
        			ImageInputStream imageInputStream = ImageIO.createImageInputStream(ins);
        			if (imageInputStream != null && imageInputStream.length() != 0) {
        				iteratorIO = ImageIO.getImageReaders(imageInputStream);
        				if (iteratorIO != null && iteratorIO.hasNext()) {
        					reader = iteratorIO.next();

        					reader.setInput(imageInputStream);
        					pages = reader.getNumImages(true);
        					bufferedImage = reader.read(pages-1);
        				}
        			}
        		} catch (Exception q) {}
        	} else {
            	ins = new ByteArrayInputStream(connector.stubGetDatastream(pid, dsid));
            	bufferedImage = ImageIO.read(ins);
        	}
        	panel = createPanel(bufferedImage, panelsize);
        } catch (Exception e) {}
			
		if (panel == null ) {
			try {
				ins = new ByteArrayInputStream(connector.stubGetDatastream(pid, "THUMBNAIL"));
				bufferedImage = ImageIO.read(ins);
				panel = createPanel(bufferedImage, panelsize);
		    } catch (Exception e) {}
		}
	
		return panel;
	}

	private CImagePanel createPanel (BufferedImage bim, Dimension panelsize) {
		
		CImagePanel panel = null;
		
		try {
			
			int iHeight = bim.getHeight();
			int iWidth = bim.getWidth();
	
			int height = (int) panelsize.getHeight();
			int width = (int) panelsize.getWidth();
	
			if (iHeight > height) {
				width = (int)((double) iWidth * ((double)height/(double)iHeight));
			} else {
				width = iWidth;
				height = iHeight;
			}
			panel = new CImagePanel(bim, height, width);
		} catch (Exception qe) {qe.printStackTrace(); }
		
		return panel;
		
	}
	
}
