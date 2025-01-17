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

import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;

import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CDublinCoreFieldListener;
import org.emile.client.dialog.core.CHint;
import org.emile.client.dialog.core.CPIDFieldListener;

import java.util.ResourceBundle;

import javax.swing.*;

import org.apache.log4j.Logger;


public class GuiCreateObjectDialog extends CGuiComposite {
	
	private static Logger log = Logger.getLogger(GuiCreateObjectDialog.class);
	
	protected Container container;
	protected JTextField jtfIdentifier;
	protected JTextField jtfPID;
	protected JTextField jtfTitle;
	protected JTextField jtfSubject;
	protected JTextField jtfDescription;
	protected JTextField jtfCreator;
	protected JTextField jtfPublisher;
	protected JTextField jtfContributor;
	protected JTextField jtfDate;
	protected JTextField jtfType;
	protected JTextField jtfFormat;
	protected JTextField jtfSource;
	protected JTextField jtfLanguage;
	protected JTextField jtfRelation;
	protected JTextField jtfCoverage;
	protected JTextField jtfRights;
	protected JCheckBox jcbAllowEditingPIDField;
	protected JProgressBar jpbProgessBar;
	protected JComboBox<String> jcbContentModel;
	protected JComboBox<String> jcbNamespace;
	protected JComboBox<String> jcbGroup;
	protected CHint jbHint;

	protected JButton jbApply;
	protected JButton jbClose;
	protected JButton jbConfigure;
	protected JButton jbReset;

	public GuiCreateObjectDialog() {

		super( "GuiCreateObjectDialog" );

		try {
			jbInit();
			setRootComponent( container );
			setup();
		}
		catch ( Exception e ) {
			log.error(e);	
		}
	}

	protected void setup() {
		
		setWidgetName( jcbContentModel, "jcbContentModel" );
		setWidgetName( jcbNamespace, "jcbNamespace" );
		setWidgetName( jcbGroup, "jcbGroup" );
		setWidgetName( jtfPID, "jtfPID" );
		setWidgetName( jcbAllowEditingPIDField, "jcbAllowEditingPIDField" );
		setWidgetName( jtfTitle, "jtfTitle" );
		setWidgetName( jtfSubject, "jtfSubject" );
		setWidgetName( jtfDescription, "jtfDescription" );
		setWidgetName( jtfCreator, "jtfCreator" );
		setWidgetName( jtfPublisher, "jtfPublisher" );
		setWidgetName( jtfContributor, "jtfContributor" );
		setWidgetName( jtfDate, "jtfDate" );
		setWidgetName( jtfType, "jtfType" );
		setWidgetName( jtfFormat, "jtfFormat" );
		setWidgetName( jtfSource, "jtfSource" );
		setWidgetName( jtfLanguage, "jtfLanguage" );
		setWidgetName( jtfRelation, "jtfRelation" );
		setWidgetName( jtfCoverage, "jtfCoverage" );
		setWidgetName( jtfRights, "jtfRights" );
		setWidgetName( jbApply, "jbApply" );
		setWidgetName( jbClose, "jbClose" );
		setWidgetName( jbConfigure, "jbConfigure" );
		setWidgetName( jbReset, "jbReset" );
		setWidgetName( jbHint, "jbHint" );
		setWidgetName( jpbProgessBar, "jpbProgessBar" );
		
	}

	private void jbInit() throws Exception {

		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("insets 10","[][grow]",""));
		
		jcbAllowEditingPIDField = new JCheckBox( "PID:" );

		jtfPID = new JTextField();
		jtfPID.setBackground( new Color (238,238,238)  );
		jtfPID.setEnabled(false);
		jtfPID.addKeyListener( new CPIDFieldListener() );
		jtfPID.setPreferredSize(new Dimension(400, jtfPID.getPreferredSize().height));

		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);

		jcbGroup = new JComboBox();
		jcbGroup.setBackground( Color.YELLOW );

		jcbContentModel = new JComboBox();
		jcbContentModel.setBackground( Color.YELLOW );
		
		jcbNamespace = new JComboBox(Common.NAMESPACES);
		jcbNamespace.setBackground( Color.YELLOW );

		jtfTitle = new JTextField();
		jtfTitle.setBackground( Color.YELLOW );
		jtfTitle.addKeyListener( new CDublinCoreFieldListener() );

		jtfSubject = new JTextField();
		jtfSubject.addKeyListener( new CDublinCoreFieldListener() );

		jtfDescription = new JTextField();
		jtfDescription.addKeyListener( new CDublinCoreFieldListener() );

		jtfCreator = new JTextField();
		jtfCreator.addKeyListener( new CDublinCoreFieldListener() );

		jtfPublisher = new JTextField();
		jtfPublisher.addKeyListener( new CDublinCoreFieldListener() );

		jtfContributor = new JTextField();
		jtfContributor.addKeyListener( new CDublinCoreFieldListener() );

		jtfDate = new JTextField();
		jtfDate.addKeyListener( new CDublinCoreFieldListener() );

		jtfType = new JTextField();
		jtfType.addKeyListener( new CDublinCoreFieldListener() );

		jtfFormat = new JTextField();
		jtfFormat.addKeyListener( new CDublinCoreFieldListener() );

		jtfSource = new JTextField();
		jtfSource.addKeyListener( new CDublinCoreFieldListener() );

		jtfLanguage = new JTextField();
		jtfLanguage.addKeyListener( new CDublinCoreFieldListener() );

		jtfRelation = new JTextField();
		jtfRelation.addKeyListener( new CDublinCoreFieldListener() );

		jtfCoverage = new JTextField();
		jtfCoverage.addKeyListener( new CDublinCoreFieldListener() );

		jtfRights = new JTextField();
		jtfRights.addKeyListener( new CDublinCoreFieldListener() );

		jbHint = new CHint();

		jbApply = new JButton( res.getString("apply") );

		jbClose = new JButton( res.getString("close") );

		jbConfigure = new JButton( res.getString("configure") );

		jbReset = new JButton( res.getString("reset") );

		container.add(jcbAllowEditingPIDField);
		Box c0  = Box.createHorizontalBox();
		c0.add( jcbNamespace );
		c0.add( new JLabel (" "));
		c0.add( jtfPID );
		c0.add( new JLabel ("  "+res.getString("contentmodel")+": "));
		c0.add( jcbContentModel );
		c0.add( new JLabel ("  "+res.getString("group")+": "));
	    c0.add( jcbGroup );
        container.add(c0,"wrap 5");

        container.add( new JLabel( "dc:Title" ) );
	    container.add( jtfTitle, "grow, wrap 5" );
        container.add( new JLabel( "dc:Description" ) );
	    container.add( jtfDescription, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Subject" ) );
	    container.add( jtfSubject, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Creator" ) );
		container.add( jtfCreator, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Publisher" ) );
		container.add( jtfPublisher, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Contributor" ) );
		container.add( jtfContributor, "grow, wrap 5" );
	    
	    container.add( new JLabel( "dc:Language" ) );
		Box c1  = Box.createHorizontalBox();
		c1.add( jtfLanguage );
		c1.add( new JLabel( "  dc:Date " ) );
		c1.add( jtfDate );
        container.add(c1,"grow, wrap 5");
		
	    container.add( new JLabel( "dc:Type" ) );
		Box c2  = Box.createHorizontalBox();
		c2.add( jtfType );
		c2.add( new JLabel( "  dc:Format " ) );
		c2.add( jtfFormat );
        container.add(c2,"grow, wrap 5");
    
	    container.add( new JLabel( "dc:Source" ) );
		container.add( jtfSource, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Relation" ) );
		container.add( jtfRelation, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Coverage" ) );
		container.add( jtfCoverage, "grow, wrap 5" );
	    container.add( new JLabel( "dc:Rights" ) );
		container.add( jtfRights, "grow, wrap 5" );
		
	    container.add( new JLabel( "" ) );
		Box c3  = Box.createHorizontalBox();
		c3.add( jpbProgessBar );
		c3.add( new JLabel( " " ) );
		c3.add( jbApply );
		c3.add( new JLabel( " " ) );
		c3.add( jbReset );
		c3.add( new JLabel( " " ) );
		c3.add( jbConfigure );
		c3.add( new JLabel( " " ) );
		c3.add( jbClose );
		container.add (c3, "gapleft push, wrap 10");	
		
		container.add(new JLabel());
		container.add(jbHint, "wrap 15");


	}


}

