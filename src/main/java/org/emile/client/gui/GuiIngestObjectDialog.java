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

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite;

import java.awt.*;

import org.emile.client.CiriloFrame;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CDublinCoreFieldListener;
import org.emile.client.dialog.core.CHint;
import org.emile.client.utils.Utils;

import java.util.ResourceBundle;

import javax.swing.*;

import org.apache.log4j.Logger;


public class GuiIngestObjectDialog extends CGuiComposite {
	
	private static Logger log = Logger.getLogger(GuiCreateObjectDialog.class);
	
	protected Container container;

	protected JProgressBar jpbProgessBar;
	
	protected JEditorPane jLogView;

	protected JTextField jtfIdentifier;
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
	protected JRadioButton jrbFilesystem;
	protected JRadioButton jrbSpreadsheet;
	protected Box jbxFilesystem;
	protected Box jbxSpreadsheet;
	protected JTextField jtfIngestDir;
	protected JButton jbIngestDir;
	protected JTextField jtfTemplate;
	protected JButton jbTemplate;
	protected JTextField jtfSpreadsheet;
	protected JButton jbSpreadsheet;
	protected JButton jbConfigure;
	protected JCheckBox jcbStrict;
		
	protected JComboBox<String> jcbContentModel;
	protected JComboBox<String> jcbGroup;
	protected JComboBox<String> jcbContextPrototype;

	protected Dimension button_dim;
	
	protected JButton jbApply;
	protected JButton jbClose;
	protected JButton jbReset;
	protected CHint jbHint;
	protected CHint jbHintStrict;
	
	public GuiIngestObjectDialog() {

		super( "GuiIngestObjectDialog" );

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
		
		setWidgetName( jpbProgessBar, "jpbProgessBar" );
		setWidgetName( jLogView, "jLogView" );
		setWidgetName( jbxFilesystem, "jbxFilesystem" );
		setWidgetName( jbxSpreadsheet, "jbxSpreadsheet" );
		setWidgetName( jrbFilesystem, "jrbFilesystem" );
		setWidgetName( jrbSpreadsheet, "jrbSpreadsheet" );
		setWidgetName( jtfIngestDir, "jtfIngestDir" );
		setWidgetName( jbIngestDir, "jbIngestDir" );
		setWidgetName( jtfTemplate, "jtfTemplate" );
		setWidgetName( jbTemplate, "jbTemplate" );
		setWidgetName( jtfSpreadsheet, "jtfSpreadsheet" );
		setWidgetName( jbSpreadsheet, "jbSpreadsheet" );	
		setWidgetName( jcbContentModel, "jcbContentModel" );
		setWidgetName( jcbGroup, "jcbGroup" );
		setWidgetName( jtfTitle, "jtfTitle" );
		setWidgetName( jtfSubject, "jtfSubject" );
		setWidgetName( jtfDescription, "jtfDescription" );
		setWidgetName( jtfCreator, "jtfCreator" );
		setWidgetName( jtfPublisher, "jtfPublisher" );
		setWidgetName( jtfContributor, "jtfContributor" );
		setWidgetName( jcbContextPrototype, "jcbContextPrototype" );
		setWidgetName( jtfDate, "jtfDate" );
		setWidgetName( jtfType, "jtfType" );
		setWidgetName( jtfFormat, "jtfFormat" );
		setWidgetName( jtfSource, "jtfSource" );
		setWidgetName( jtfLanguage, "jtfLanguage" );
		setWidgetName( jtfRelation, "jtfRelation" );
		setWidgetName( jtfCoverage, "jtfCoverage" );
		setWidgetName( jtfRights, "jtfRights" );
		setWidgetName( jbConfigure, "jbConfigure" );
		setWidgetName( jbHint, "jbHint" );
		setWidgetName( jbApply, "jbApply" );
		setWidgetName( jbClose, "jbClose" );
		setWidgetName( jbReset, "jbReset" );
		setWidgetName( jcbStrict, "jcbStrict" );
		
	}

	private void jbInit() throws Exception {

		
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
	
		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("insets 10","[][grow]",""));
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);	
		jpbProgessBar.setStringPainted(true);
	
		jLogView = new JEditorPane();
		JScrollPane scrPane = new JScrollPane(jLogView);

		jbxFilesystem  = Box.createHorizontalBox();
		jbxSpreadsheet  = Box.createHorizontalBox();
		
		jtfIngestDir = new JTextField();
		
		int button_width = (int) Math.round(new Integer(props.getProperty("user", "FontSize"))*2.7);
		button_dim = new Dimension(Math.max(button_width, 32), jtfIngestDir.getPreferredSize().height+2);
		
		jbIngestDir = Utils.createOpenButton(button_dim);
		
		jbxFilesystem.add(new JLabel(res.getString("directory")+": "));
		jbxFilesystem.add(jtfIngestDir);
		jbxFilesystem.add(jbIngestDir);
			
		jtfTemplate = new JTextField();
		jbTemplate = Utils.createOpenButton(button_dim);

		jtfSpreadsheet = new JTextField();
		jbSpreadsheet = Utils.createOpenButton(button_dim);
		
		jbxSpreadsheet.add(new JLabel(res.getString("spreadsheet")+": "));
		jbxSpreadsheet.add(jtfSpreadsheet);
		jbxSpreadsheet.add(jbSpreadsheet);
		jbxSpreadsheet.add(new JLabel(" "+res.getString("template")+": "));
		jbxSpreadsheet.add(jtfTemplate);
		jbxSpreadsheet.add(jbTemplate);
		
		ButtonGroup bg = new ButtonGroup();
		jrbFilesystem = new JRadioButton(res.getString("filesystem"));
		jrbSpreadsheet = new JRadioButton(res.getString("spreadsheet"));
		bg.add(jrbFilesystem);
		bg.add(jrbSpreadsheet);
		
		jcbGroup = new JComboBox();
		jcbGroup.setBackground( Color.YELLOW );

		jcbContextPrototype = new JComboBox();
		
		jcbContentModel = new JComboBox();
		jcbContentModel.setBackground( Color.YELLOW );
		
		jcbStrict = new JCheckBox(res.getString("strictmode"));
		jcbStrict.setSelected(true); 
				
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
		jbHintStrict = new CHint(res.getString("HTstrict"));
		
		jbConfigure = new JButton( res.getString("configure") );

		jbApply = new JButton( res.getString("apply") );

		jbClose = new JButton( res.getString("close") );
		
		jbReset = new JButton( res.getString("reset") );

		container.add(new JLabel(res.getString("ingestfrom")+":"));
		Box c0  = Box.createHorizontalBox();
		c0.add( jrbFilesystem );
		c0.add( jrbSpreadsheet );	
		c0.add( new JLabel ("  "+res.getString("contentmodel")+": "));
		c0.add( jcbContentModel );
		c0.add( new JLabel ("  "+res.getString("group")+": "));
		c0.add( jcbGroup,"wrap 5" );	
	    container.add(c0,"grow, wrap 5");
    
	    container.add(new JLabel(""));
	    Box cx  = Box.createHorizontalBox();
		cx.add(jbxFilesystem);
		cx.add(jbxSpreadsheet);
		container.add(cx,"grow, wrap 5");
		jbxSpreadsheet.setVisible(false);

	    container.add(new JLabel(""));
	    Box cy  = Box.createHorizontalBox();
	    cy.add(new JLabel(res.getString("createcontext")+": "));
	    cy.add(jcbContextPrototype);
	    cy.add(new JLabel("  "));
	    cy.add(jcbStrict);
	    container.add(cy, "grow, wrap 5");
	    

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
		container.add( jtfRights, "grow, wrap 15" );
			
		Box c3  = Box.createHorizontalBox();
		c3.add(jpbProgessBar);
		c3.add( new JLabel( " " ) );
		c3.add( jbApply );
		c3.add( new JLabel( " " ) );
		c3.add( jbReset );
		c3.add( new JLabel( " " ) );
		c3.add( jbConfigure );
		c3.add( new JLabel( " " ) );
		c3.add( jbClose );
		
		container.add(new JLabel(""));
		container.add(new JLabel(res.getString("actlog")), "wrap 1");
		container.add(new JLabel(""));
	   	container.add( scrPane, "height 100:1000:3000, growx, wrap 10");

		container.add(new JLabel(""));
		container.add (c3, "gapleft push, wrap 5");
		
		container.add(new JLabel());
		container.add(jbHintStrict, "wrap 5");
		container.add(new JLabel());
		container.add(jbHint, "wrap 15");

		
	}

}

