/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.client.dialog.core;

import javax.swing.*;

import java.awt.*;
import java.beans.*;
import java.io.File;

public class CImagePreviewPanel extends JPanel implements PropertyChangeListener {

	static final long serialVersionUID = 0L;
	private int width, height;
	private ImageIcon icon;
	private Image image;
	private final static int ACCSIZE = 155;
	private Color bg;

	public CImagePreviewPanel() {
		setPreferredSize(new Dimension(ACCSIZE, -1));
		bg = getBackground();
	}

	public void propertyChange(PropertyChangeEvent e) {

		String propertyName = e.getPropertyName();

		if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File selection = (File) e.getNewValue();

			if (selection == null) {
				return;
			} 
							
			icon = new ImageIcon(selection.getAbsolutePath());
			image = icon.getImage();
			scaleImage();
			repaint();	
		}
	}

	private void scaleImage() {
		
		width = image.getWidth(this);
		height = image.getHeight(this);
		double ratio = 1.0;

		if (width >= height) {
			ratio = (double) (ACCSIZE - 5) / width;
			width = ACCSIZE - 5; height = (int) (height * ratio);
		} else {
			if (getHeight() > 150) {
				ratio = (double) (ACCSIZE - 5) / height;
				height = ACCSIZE - 5; width = (int) (width * ratio);
			} else {
				ratio = (double) getHeight() / height;
				height = getHeight(); width = (int) (width * ratio);
			}
		}

		image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}

	public void paintComponent(Graphics g) {

		g.setColor(bg);
		g.fillRect(0, 0, ACCSIZE, getHeight());
		g.drawImage(image, 5, 0, this);
	}

}
