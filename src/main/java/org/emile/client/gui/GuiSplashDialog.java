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

package org.emile.client.gui;

import voodoosoft.jroots.core.gui.CGuiTools;

import java.awt.*;

import javax.swing.*;

import org.emile.client.Cirilo;

import org.apache.log4j.Logger;

public class GuiSplashDialog extends JWindow {
	
	private JPanel panel              = new JPanel();
	private JLabel image              = new JLabel();

	public GuiSplashDialog() {
			
		try {
			
			Init();

			image.setOpaque(false);
			image.setIcon(new ImageIcon(Cirilo.class.getResource("cirilo.png")));

			pack();
			
			CGuiTools.center(this);
			
		} catch (Exception e) {e.printStackTrace();}
	}


	public void dispose() {

		super.dispose();

	}


	private void Init() throws Exception {

		this.getContentPane().setLayout(new GridBagLayout());

		panel.setBorder(BorderFactory.createEtchedBorder());
		panel.setPreferredSize(new Dimension(605, 374));
		panel.setLayout(new GridBagLayout());

		image.setPreferredSize(new Dimension(600, 369));
		image.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(image, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));

		this.getContentPane().add(panel,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(9, 9, 6, 7), 0, 0));

	}

}

