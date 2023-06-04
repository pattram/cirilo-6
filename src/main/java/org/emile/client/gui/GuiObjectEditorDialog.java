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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URI;

import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CHyperlink;
import org.emile.client.utils.Utils;

import java.util.ResourceBundle;

import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiObjectEditorDialog extends CGuiComposite {
	
	private static Logger log = Logger.getLogger(GuiObjectEditorDialog.class);
	
	protected Container container;

	protected JTabbedPane tpPane;
	
	protected JProgressBar jpbProgessBar;
	
	protected Dimension button_dim;

	protected JTable jtDatastreams;	
	protected JTable jtPipelines;	
	protected JButton jbApply;
	protected JButton jbClose;

	protected Box Location;
	protected JTextField jtfLocation;

	
	protected JList<String> jtRels;
	protected JList<String> jtNonRels;
	protected JButton jbRelAdd;
	protected JButton jbRelRemove;
	protected JButton jbRelFind;
	protected JButton jbRelFindAdd;
	protected JButton jbRelFindDel;
	protected JComboBox<String> jcbRelFind;
	
	protected CHyperlink JlContentModel;
	protected CHyperlink JlPath;
	protected JButton jbDSAdd;
	protected JButton jbDSNew;
	protected JButton jbDSPurge;
	protected JButton jbDSExport;
	protected JButton jbDSVersioning;
	
	protected Box Box99;
	protected JComboBox<String> jcbRJobs;
	protected JButton jbRun;

	protected Box Box00;
	protected JLabel jlXsl00;
	protected JTextField jtfPipeline00;
	protected JButton jbChoosePipeline00;
	
	protected Box Box01;
	protected JLabel jlXsl01;
	protected JTextField jtfPipeline01;
	protected JButton jbChoosePipeline01;
	
	protected Box Box02;
	protected JLabel jlXsl02;
	protected JTextField jtfPipeline02;
	protected JButton jbChoosePipeline02;

	protected Box Box03;
	protected JLabel jlXsl03;
	protected JTextField jtfPipeline03;
	protected JButton jbChoosePipeline03;
	
	protected Box Box04;
	protected JLabel jlXsl04;
	protected JTextField jtfPipeline04;
	protected JButton jbChoosePipeline04;
	
	protected Box Box05;
	protected JLabel jlXsl05;
	protected JTextField jtfPipeline05;
	protected JButton jbChoosePipeline05;
	
	protected Box Box06;
	protected JLabel jlXsl06;
	protected JTextField jtfPipeline06;
	protected JButton jbChoosePipeline06;
	
	protected Box Box07;
	protected JLabel jlXsl07;
	protected JTextField jtfPipeline07;
	protected JButton jbChoosePipeline07;
	
	protected Box Box08;
	protected JLabel jlXsl08;
	protected JTextField jtfPipeline08;
	protected JButton jbChoosePipeline08;
	
	protected Box Box09;
	protected JLabel jlXsl09;
	protected JTextField jtfPipeline09;
	protected JButton jbChoosePipeline09;
	
	protected Box Box10;
	protected JLabel jlXsl10;
	protected JTextField jtfPipeline10;
	protected JButton jbChoosePipeline10;
	
	protected Box Box11;
	protected JLabel jlXsl11;
	protected JTextField jtfPipeline11;
	protected JButton jbChoosePipeline11;
	
	protected Box Box12;
	protected JLabel jlXsl12;
	protected JTextField jtfPipeline12;
	protected JButton jbChoosePipeline12;
		
	protected Box Box13;
	protected JLabel jlXsl13;
	protected JTextField jtfPipeline13;
	protected JButton jbChoosePipeline13;
		
	protected Box Box14;
	protected JLabel jlXsl14;
	protected JTextField jtfPipeline14;
	protected JButton jbChoosePipeline14;
		
	protected Box Box15;
	protected JLabel jlXsl15;
	protected JTextField jtfPipeline15;
	protected JButton jbChoosePipeline15;
	
	protected Box Box16;
	protected JLabel jlXsl16;
	protected JTextField jtfPipeline16;
	protected JButton jbChoosePipeline16;
	
	protected Box Box17;
	protected JLabel jlXsl17;
	protected JTextField jtfPipeline17;
	protected JButton jbChoosePipeline17;
	
	protected Box Box18;
	protected JLabel jlXsl18;
	protected JTextField jtfPipeline18;
	protected JButton jbChoosePipeline18;
	
	protected Box Box19;
	protected JLabel jlXsl19;
	protected JTextField jtfPipeline19;
	protected JButton jbChoosePipeline19;
	
	protected JPanel jpDSPanel;
	
	protected Box jbNewDS;
	protected JTextField jtfDSID;
	protected JComboBox<String> jcbMimeTypes;
	protected JButton jbDSOK;
	
	protected JButton jbConfigure;


	public GuiObjectEditorDialog () {
		
		super("GuiObjectEditorDialog ");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
		}
	}


	/**
	 *  Description of the Method
	 */
	protected void setup() {

		setWidgetName(jtDatastreams, "jtDatastreams");

		setWidgetName(jpbProgessBar, "jpbProgessBar");

		setWidgetName(tpPane, "tpPane");
		
		setWidgetName(Location, "Location");
		setWidgetName(jtfLocation, "jtfLocation");
			
		setWidgetName(JlContentModel, "JlContentModel");
		setWidgetName(JlPath, "JlPath");
		
		setWidgetName(jtRels, "jtRels");
		setWidgetName(jtNonRels, "jtNonRels");
		setWidgetName(jbRelAdd, "jbRelAdd");
		setWidgetName(jbRelRemove, "jbRelRemove");
		setWidgetName(jbRelFind, "jbRelFind");
		setWidgetName(jcbRelFind, "jcbRelFind");
		setWidgetName(jbRelFindAdd, "jbRelFindAdd");
		setWidgetName(jbRelFindDel, "jbRelFindDel");
		
		setWidgetName(jbDSAdd, "jbDSAdd");
		setWidgetName(jbDSNew, "jbDSNew");
		setWidgetName(jbDSPurge, "jbDSPurge");
		setWidgetName(jbDSExport, "jbDSExport");
		setWidgetName(jbDSVersioning, "jbDSVersioning");
		setWidgetName(jpDSPanel, "jpDSPanel");
		
		setWidgetName(jbNewDS, "jbNewDS");
		setWidgetName(jtfDSID, "jtfDSID");
		setWidgetName(jcbMimeTypes, "jcbMimeTypes");
		setWidgetName(jbDSOK, "jbDSOK");

		setWidgetName(Box99, "Box99");
		setWidgetName(jcbRJobs, "jcbRJobs");
		setWidgetName(jbRun, "jbRun");

		setWidgetName(Box00, "Box00");
		setWidgetName(jlXsl00, "jlXsl00");
		setWidgetName(jtfPipeline00, "jtfPipeline00");
		setWidgetName(jbChoosePipeline00, "jbChoosePipeline00");

		setWidgetName(Box01, "Box01");
		setWidgetName(jlXsl01, "jlXsl01");
		setWidgetName(jtfPipeline01, "jtfPipeline01");
		setWidgetName(jbChoosePipeline01, "jbChoosePipeline01");

		setWidgetName(Box02, "Box02");
		setWidgetName(jlXsl02, "jlXsl02");
		setWidgetName(jtfPipeline02, "jtfPipeline02");
		setWidgetName(jbChoosePipeline02, "jbChoosePipeline02");

		setWidgetName(Box03, "Box03");
		setWidgetName(jlXsl03, "jlXsl03");
		setWidgetName(jtfPipeline03, "jtfPipeline03");
		setWidgetName(jbChoosePipeline03, "jbChoosePipeline03");
		
		setWidgetName(Box04, "Box04");
		setWidgetName(jlXsl04, "jlXsl04");
		setWidgetName(jtfPipeline04, "jtfPipeline04");
		setWidgetName(jbChoosePipeline04, "jbChoosePipeline04");
		
		setWidgetName(Box05, "Box05");
		setWidgetName(jlXsl05, "jlXsl05");
		setWidgetName(jtfPipeline05, "jtfPipeline05");
		setWidgetName(jbChoosePipeline05, "jbChoosePipeline05");
		
		setWidgetName(Box06, "Box06");
		setWidgetName(jlXsl06, "jlXsl06");
		setWidgetName(jtfPipeline06, "jtfPipeline06");
		setWidgetName(jbChoosePipeline06, "jbChoosePipeline06");
		
		setWidgetName(Box07, "Box07");
		setWidgetName(jlXsl07, "jlXsl07");
		setWidgetName(jtfPipeline07, "jtfPipeline07");
		setWidgetName(jbChoosePipeline07, "jbChoosePipeline07");
		
		setWidgetName(Box08, "Box08");
		setWidgetName(jlXsl08, "jlXsl08");
		setWidgetName(jtfPipeline08, "jtfPipeline08");
		setWidgetName(jbChoosePipeline08, "jbChoosePipeline08");
		
		setWidgetName(Box09, "Box09");
		setWidgetName(jlXsl09, "jlXsl09");
		setWidgetName(jtfPipeline09, "jtfPipeline09");
		setWidgetName(jbChoosePipeline09, "jbChoosePipeline09");
		
		setWidgetName(Box10, "Box10");
		setWidgetName(jlXsl10, "jlXsl10");
		setWidgetName(jtfPipeline10, "jtfPipeline10");
		setWidgetName(jbChoosePipeline10, "jbChoosePipeline10");
		
		setWidgetName(Box11, "Box11");
		setWidgetName(jlXsl11, "jlXsl11");
		setWidgetName(jtfPipeline11, "jtfPipeline11");
		setWidgetName(jbChoosePipeline11, "jbChoosePipeline11");
		
		setWidgetName(Box12, "Box12");
		setWidgetName(jlXsl12, "jlXsl12");
		setWidgetName(jtfPipeline12, "jtfPipeline12");
		setWidgetName(jbChoosePipeline12, "jbChoosePipeline12");
		
		setWidgetName(Box13, "Box13");
		setWidgetName(jlXsl13, "jlXsl13");
		setWidgetName(jtfPipeline13, "jtfPipeline13");
		setWidgetName(jbChoosePipeline13, "jbChoosePipeline13");
		
		setWidgetName(Box14, "Box14");
		setWidgetName(jlXsl14, "jlXsl14");
		setWidgetName(jtfPipeline14, "jtfPipeline14");
		setWidgetName(jbChoosePipeline14, "jbChoosePipeline14");
		
		setWidgetName(Box15, "Box15");
		setWidgetName(jlXsl15, "jlXsl15");
		setWidgetName(jtfPipeline15, "jtfPipeline15");
		setWidgetName(jbChoosePipeline15, "jbChoosePipeline15");
		
		setWidgetName(Box16, "Box16");
		setWidgetName(jlXsl16, "jlXsl16");
		setWidgetName(jtfPipeline16, "jtfPipeline16");
		setWidgetName(jbChoosePipeline16, "jbChoosePipeline16");
		
		setWidgetName(Box17, "Box17");
		setWidgetName(jlXsl17, "jlXsl17");
		setWidgetName(jtfPipeline17, "jtfPipeline17");
		setWidgetName(jbChoosePipeline17, "jbChoosePipeline17");
		
		setWidgetName(Box18, "Box18");
		setWidgetName(jlXsl18, "jlXsl18");
		setWidgetName(jtfPipeline18, "jtfPipeline18");
		setWidgetName(jbChoosePipeline18, "jbChoosePipeline18");
		
		setWidgetName(Box19, "Box19");
		setWidgetName(jlXsl19, "jlXsl19");
		setWidgetName(jtfPipeline19, "jtfPipeline19");
		setWidgetName(jbChoosePipeline19, "jbChoosePipeline19");
		
		setWidgetName(jbApply, "jbApply");
		setWidgetName(jbClose, "jbClose");

		setWidgetName( jbConfigure, "jbConfigure" );
		
	}

	private void jbInit() throws Exception {
		
	    
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		
		int pipeline_label_width = new Integer(props.getProperty("user", "FontSize"))*16;

		container = new Container();	
		container.setLayout(new net.miginfocom.swing.MigLayout("fillx"));

	    tpPane = new JTabbedPane();
	    
	    jpDSPanel = new JPanel();
	    jpDSPanel.setLayout(new BorderLayout());
	    
		jtDatastreams = new JTable();

		jbClose = new JButton(res.getString("close"));
			
		JlContentModel = new CHyperlink();
		JlContentModel.setURI(new URI("https://localhost/doc/userguide/rcontentmodels/#tei"));
		JlContentModel.setText("TEI");
		JlPath = new CHyperlink();

		jbDSAdd = new JButton(res.getString("update"));
		jbDSAdd.setToolTipText(res.getString("TTobjectadd"));
		jbDSNew = new JButton(res.getString("new"));
		jbDSNew.setToolTipText(res.getString("TTobjectnew"));
		jbDSPurge = new JButton(res.getString("delete"));		
		jbDSPurge.setToolTipText(res.getString("TTobjectpurge"));
		jbDSExport = new JButton(res.getString("export").replaceAll("[.]", ""));
		jbDSExport.setToolTipText(res.getString("TTobjectexport"));
		jbDSVersioning = new JButton(res.getString("versioning"));
		jbDSVersioning.setToolTipText(res.getString("TTobjectver"));

		jtfDSID = new JTextField();
		jtfDSID.setPreferredSize(new Dimension(120, jtfDSID.getPreferredSize().height));
		jtfDSID.addKeyListener(new TextKeyListener());
	
		jcbMimeTypes = new JComboBox(Common.MIMETYPES.toArray());
		jbDSOK = new JButton("OK");
		jbDSOK.setEnabled(false);
		
		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);
		
		jtRels = new JList(new DefaultListModel());
		jtRels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		jtNonRels = new JList(new DefaultListModel());
		jtNonRels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		jbRelAdd = new JButton(res.getString("add"));
		jbRelAdd.setToolTipText(res.getString("TTcontextadd"));
		jbRelRemove = new JButton(res.getString("remove"));
		jbRelRemove.setToolTipText(res.getString("TTcontextdel"));
		jbApply = new JButton(res.getString("apply"));
		jcbRelFind = new JComboBox();  
		jcbRelFind.setPreferredSize(new Dimension(650, jcbRelFind.getPreferredSize().height));
		jbRelFind = new JButton(res.getString("search").replaceAll("[. ]", ""));
		jbRelFind.setToolTipText(res.getString("TTsearchtermfind"));
		jbRelFindAdd = new JButton("+");
		jbRelFindAdd.setToolTipText(res.getString("TTsearchtermadd"));		
		jbRelFindDel = new JButton("-");
		jbRelFindDel.setToolTipText(res.getString("TTsearchtermdel"));

		
		jtfLocation = new JTextField();
		Location = Box.createHorizontalBox();
		Location.add(new Label(res.getString("isshownat")+": "));
		Location.add(jtfLocation);
		
		Box99 = Box.createHorizontalBox();
		jcbRJobs = new JComboBox<String>();
		jbRun = new JButton(res.getString("run"));

		Box99.add(jbRun);
		Box99.add(jcbRJobs);
		
		Box00 = Box.createHorizontalBox();
		jlXsl00 = new JLabel("");
		jlXsl00.setPreferredSize(new Dimension(pipeline_label_width, jlXsl00.getPreferredSize().height));
		jtfPipeline00 = new JTextField();
		jtfPipeline00.setPreferredSize(new Dimension(650, jtfPipeline00.getPreferredSize().height));
		
		int button_width = (int) Math.round(new Integer(props.getProperty("user", "FontSize"))*2.7);
		button_dim = new Dimension(Math.max(button_width, 32), jtfPipeline00.getPreferredSize().height+2);
		
		jbChoosePipeline00 = Utils.createOpenButton(button_dim);

		Box00.add(jlXsl00);
		Box00.add(jtfPipeline00);
		Box00.add(jbChoosePipeline00);
		
		Box01 = Box.createHorizontalBox();
		jlXsl01 = new JLabel("");
		jlXsl01.setPreferredSize(new Dimension(pipeline_label_width, jlXsl01.getPreferredSize().height));
		jtfPipeline01 = new JTextField();
		jtfPipeline01.setPreferredSize(new Dimension(650, jtfPipeline01.getPreferredSize().height));
		jbChoosePipeline01 = Utils.createOpenButton(button_dim);

		Box01.add(jlXsl01);
		Box01.add(jtfPipeline01);
		Box01.add(jbChoosePipeline01);
			
		Box02 = Box.createHorizontalBox();
		jlXsl02 = new JLabel("");
		jlXsl02.setPreferredSize(new Dimension(pipeline_label_width, jlXsl02.getPreferredSize().height));
		jtfPipeline02 = new JTextField();
		jtfPipeline02.setPreferredSize(new Dimension(650, jtfPipeline02.getPreferredSize().height));
		jbChoosePipeline02 = Utils.createOpenButton(button_dim);

	
		Box02.add(jlXsl02);
		Box02.add(jtfPipeline02);
		Box02.add(jbChoosePipeline02);
	
		Box03 = Box.createHorizontalBox();
		jlXsl03 = new JLabel("");
		jlXsl03.setPreferredSize(new Dimension(pipeline_label_width, jlXsl03.getPreferredSize().height));
		jtfPipeline03 = new JTextField();
		jtfPipeline03.setPreferredSize(new Dimension(650, jtfPipeline03.getPreferredSize().height));
		jbChoosePipeline03 = Utils.createOpenButton(button_dim);
	
		Box03.add(jlXsl03);
		Box03.add(jtfPipeline03);
		Box03.add(jbChoosePipeline03);
	
		Box04 = Box.createHorizontalBox();
		jlXsl04 = new JLabel("");
		jlXsl04.setPreferredSize(new Dimension(pipeline_label_width, jlXsl04.getPreferredSize().height));
		jtfPipeline04 = new JTextField();
		jtfPipeline04.setPreferredSize(new Dimension(650, jtfPipeline04.getPreferredSize().height));
		jbChoosePipeline04 = Utils.createOpenButton(button_dim);

	
		Box04.add(jlXsl04);
		Box04.add(jtfPipeline04);
		Box04.add(jbChoosePipeline04);
	
		Box05 = Box.createHorizontalBox();
		jlXsl05 = new JLabel("");
		jlXsl05.setPreferredSize(new Dimension(pipeline_label_width, jlXsl05.getPreferredSize().height));
		jtfPipeline05 = new JTextField();
		jtfPipeline05.setPreferredSize(new Dimension(650, jtfPipeline05.getPreferredSize().height));
		jbChoosePipeline05 = Utils.createOpenButton(button_dim);

	
		Box05.add(jlXsl05);
		Box05.add(jtfPipeline05);
		Box05.add(jbChoosePipeline05);
	
		Box06 = Box.createHorizontalBox();
		jlXsl06 = new JLabel("");
		jlXsl06.setPreferredSize(new Dimension(pipeline_label_width, jlXsl06.getPreferredSize().height));
		jtfPipeline06 = new JTextField();
		jtfPipeline06.setPreferredSize(new Dimension(650, jtfPipeline06.getPreferredSize().height));
		jbChoosePipeline06 = Utils.createOpenButton(button_dim);

	
		Box06.add(jlXsl06);
		Box06.add(jtfPipeline06);
		Box06.add(jbChoosePipeline06);
	
		Box07 = Box.createHorizontalBox();
		jlXsl07 = new JLabel("");
		jlXsl07.setPreferredSize(new Dimension(pipeline_label_width, jlXsl07.getPreferredSize().height));
		jtfPipeline07 = new JTextField();
		jtfPipeline07.setPreferredSize(new Dimension(650, jtfPipeline07.getPreferredSize().height));
		jbChoosePipeline07 = Utils.createOpenButton(button_dim);

	
		Box07.add(jlXsl07);
		Box07.add(jtfPipeline07);
		Box07.add(jbChoosePipeline07);
	
		Box08 = Box.createHorizontalBox();
		jlXsl08 = new JLabel("");
		jlXsl08.setPreferredSize(new Dimension(pipeline_label_width, jlXsl08.getPreferredSize().height));
		jtfPipeline08 = new JTextField();
		jtfPipeline08.setPreferredSize(new Dimension(650, jtfPipeline08.getPreferredSize().height));
		jbChoosePipeline08 = Utils.createOpenButton(button_dim);

	
		Box08.add(jlXsl08);
		Box08.add(jtfPipeline08);
		Box08.add(jbChoosePipeline08);
	
		Box09 = Box.createHorizontalBox();
		jlXsl09 = new JLabel("");
		jlXsl09.setPreferredSize(new Dimension(pipeline_label_width, jlXsl09.getPreferredSize().height));
		jtfPipeline09 = new JTextField();
		jtfPipeline09.setPreferredSize(new Dimension(650, jtfPipeline09.getPreferredSize().height));
		jbChoosePipeline09 = Utils.createOpenButton(button_dim);

	
		Box09.add(jlXsl09);
		Box09.add(jtfPipeline09);
		Box09.add(jbChoosePipeline09);
	
		Box10 = Box.createHorizontalBox();
		jlXsl10 = new JLabel("");
		jlXsl10.setPreferredSize(new Dimension(pipeline_label_width, jlXsl10.getPreferredSize().height));
		jtfPipeline10 = new JTextField();
		jtfPipeline10.setPreferredSize(new Dimension(650, jtfPipeline10.getPreferredSize().height));
		jbChoosePipeline10 = Utils.createOpenButton(button_dim);

	
		Box10.add(jlXsl10);
		Box10.add(jtfPipeline10);
		Box10.add(jbChoosePipeline10);
	
		Box11 = Box.createHorizontalBox();
		jlXsl11 = new JLabel("");
		jlXsl11.setPreferredSize(new Dimension(pipeline_label_width, jlXsl11.getPreferredSize().height));
		jtfPipeline11 = new JTextField();
		jtfPipeline11.setPreferredSize(new Dimension(650, jtfPipeline11.getPreferredSize().height));
		jbChoosePipeline11 = Utils.createOpenButton(button_dim);

	
		Box11.add(jlXsl11);
		Box11.add(jtfPipeline11);
		Box11.add(jbChoosePipeline11);
	
		Box12 = Box.createHorizontalBox();
		jlXsl12 = new JLabel("");
		jlXsl12.setPreferredSize(new Dimension(pipeline_label_width, jlXsl12.getPreferredSize().height));
		jtfPipeline12 = new JTextField();
		jtfPipeline12.setPreferredSize(new Dimension(650, jtfPipeline12.getPreferredSize().height));
		jbChoosePipeline12 = Utils.createOpenButton(button_dim);

		Box12.add(jlXsl12);
		Box12.add(jtfPipeline12);
		Box12.add(jbChoosePipeline12);
	
		Box13 = Box.createHorizontalBox();
		jlXsl13 = new JLabel("");
		jlXsl13.setPreferredSize(new Dimension(pipeline_label_width, jlXsl13.getPreferredSize().height));
		jtfPipeline13 = new JTextField();
		jtfPipeline13.setPreferredSize(new Dimension(650, jtfPipeline13.getPreferredSize().height));
		jbChoosePipeline13 = Utils.createOpenButton(button_dim);

	
		Box13.add(jlXsl13);
		Box13.add(jtfPipeline13);
		Box13.add(jbChoosePipeline13);
	
		Box14 = Box.createHorizontalBox();
		jlXsl14 = new JLabel("");
		jlXsl14.setPreferredSize(new Dimension(pipeline_label_width, jlXsl14.getPreferredSize().height));
		jtfPipeline14 = new JTextField();
		jtfPipeline14.setPreferredSize(new Dimension(650, jtfPipeline14.getPreferredSize().height));
		jbChoosePipeline14 = Utils.createOpenButton(button_dim);

	
		Box14.add(jlXsl14);
		Box14.add(jtfPipeline14);
		Box14.add(jbChoosePipeline14);
	
		Box15 = Box.createHorizontalBox();
		jlXsl15 = new JLabel("");
		jlXsl15.setPreferredSize(new Dimension(pipeline_label_width, jlXsl15.getPreferredSize().height));
		jtfPipeline15 = new JTextField();
		jtfPipeline15.setPreferredSize(new Dimension(650, jtfPipeline15.getPreferredSize().height));
		jbChoosePipeline15 = Utils.createOpenButton(button_dim);
	
		Box15.add(jlXsl15);
		Box15.add(jtfPipeline15);
		Box15.add(jbChoosePipeline15);
		
		Box16 = Box.createHorizontalBox();
		jlXsl16 = new JLabel("");
		jlXsl16.setPreferredSize(new Dimension(pipeline_label_width, jlXsl16.getPreferredSize().height));
		jtfPipeline16 = new JTextField();
		jtfPipeline16.setPreferredSize(new Dimension(650, jtfPipeline16.getPreferredSize().height));
		jbChoosePipeline16 = Utils.createOpenButton(button_dim);
	
		Box16.add(jlXsl16);
		Box16.add(jtfPipeline16);
		Box16.add(jbChoosePipeline16);

		Box17 = Box.createHorizontalBox();
		jlXsl17 = new JLabel("");
		jlXsl17.setPreferredSize(new Dimension(pipeline_label_width, jlXsl17.getPreferredSize().height));
		jtfPipeline17 = new JTextField();
		jtfPipeline17.setPreferredSize(new Dimension(650, jtfPipeline17.getPreferredSize().height));
		jbChoosePipeline17 = Utils.createOpenButton(button_dim);
	
		Box17.add(jlXsl17);
		Box17.add(jtfPipeline17);
		Box17.add(jbChoosePipeline17);

		Box18 = Box.createHorizontalBox();
		jlXsl18 = new JLabel("");
		jlXsl18.setPreferredSize(new Dimension(pipeline_label_width, jlXsl18.getPreferredSize().height));
		jtfPipeline18 = new JTextField();
		jtfPipeline18.setPreferredSize(new Dimension(650, jtfPipeline18.getPreferredSize().height));
		jbChoosePipeline18 = Utils.createOpenButton(button_dim);
	
		Box18.add(jlXsl18);
		Box18.add(jtfPipeline18);
		Box18.add(jbChoosePipeline18);

		Box19 = Box.createHorizontalBox();
		jlXsl19 = new JLabel("");
		jlXsl19.setPreferredSize(new Dimension(pipeline_label_width, jlXsl19.getPreferredSize().height));
		jtfPipeline19 = new JTextField();
		jtfPipeline19.setPreferredSize(new Dimension(650, jtfPipeline19.getPreferredSize().height));
		jbChoosePipeline19 = Utils.createOpenButton(button_dim);
	
		Box19.add(jlXsl19);
		Box19.add(jtfPipeline19);
		Box19.add(jbChoosePipeline19);

		jbConfigure = new JButton( res.getString("configure") );

		
		Box c0  = Box.createHorizontalBox();		
		c0.add(new JLabel(res.getString("contentmodel")+": "));
		c0.add(JlContentModel);
		c0.add(new JLabel(" - "));
		c0.add(JlPath);
		c0.add(new JLabel("  "));
		c0.add(jbConfigure);
		c0.add(new JLabel("        "));
		c0.add(Location);

		
		Container t1 = new Container();
		t1.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		t1.add(c0, "growx, wrap 10");
	
		t1.add(new JScrollPane(jtDatastreams), "height 100:200:600, growx, wrap 5");
		
		jbNewDS = Box.createHorizontalBox();
		jbNewDS.add(new JLabel(res.getString("dsid")+": "));
		jbNewDS.add(jtfDSID);
		jbNewDS.add(new JLabel(" "));
		jbNewDS.add(jcbMimeTypes);
		jbNewDS.add(new JLabel(" "));
		jbNewDS.add(jbDSOK);
		jbNewDS.add(new JLabel("       "));
		jbNewDS.setVisible(false);
			
		Box c1  = Box.createHorizontalBox();		
		c1.add(Box99);
		c1.add(jbDSAdd);
		c1.add(new JLabel(" "));
		c1.add(jbDSNew);
		c1.add(new JLabel(" "));
		c1.add(jbDSExport);
		c1.add(new JLabel(" "));
		c1.add(jbDSVersioning);
		c1.add(new JLabel(" "));
		c1.add(jbDSPurge);
		c1.add(new JLabel(" "));
		t1.add(jbNewDS);
		
		Box c2  = Box.createHorizontalBox();	
		c2.add(jbNewDS);
		c2.add(c1);
				
		t1.add( c2, "wrap 10");	
		t1.add(jpDSPanel, "height 100:200:400, growx, wrap 5");

		JPanel re = new JPanel();
		re.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		re.setBorder(BorderFactory.createTitledBorder(res.getString("contextmemberships")+": "));		

		Container t2 = new Container();
		t2.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		
		re.add(new JLabel(res.getString("appears")+":"), "wrap 2");
		re.add(new JScrollPane(jtRels), "height 100:200:400, growx, wrap 5");
		Box c3  = Box.createHorizontalBox();
		c3.add(jbRelRemove);
		re.add( c3, "gapleft push, wrap 10" );
		re.add(new JScrollPane(jtNonRels), "height 100:200:400, growx, wrap 5" );
		Box c4  = Box.createHorizontalBox();
		c4.add(new JLabel(res.getString("ldotsearch")));
		c4.add(jcbRelFind);
		c4.add(new JLabel(" "));
		c4.add(jbRelFind);
		c4.add(new JLabel(" "));
		c4.add(jbRelFindAdd);
		c4.add(new JLabel(" "));
		c4.add(jbRelFindDel);
		c4.add(new JLabel("  "));
		c4.add(jbRelAdd);
		re.add( c4, "wrap 10" );	
		
		t2.add(re);
		
		JPanel ip = new JPanel();
		JPanel dp = new JPanel();
		JPanel mp = new JPanel();
		
		Container t3 = new Container();
		t3.setLayout(new net.miginfocom.swing.MigLayout("fillx"));

		ip.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		ip.setBorder(BorderFactory.createTitledBorder(res.getString("ingestpipelines")+": "));		
				
		ip.add( Box00, "wrap 2");
		ip.add( Box01, "wrap 2");
		ip.add( Box02, "wrap 2");
		ip.add( Box03, "wrap 2");
		ip.add( Box04, "wrap 2");
		
		t3.add(ip, "wrap 5");
		
		dp.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		dp.setBorder(BorderFactory.createTitledBorder(res.getString("disspipelines")+": "));	

		dp.add( Box05, "wrap 2" );
		dp.add( Box06, "wrap 2" );
		dp.add( Box07, "wrap 2" );
		dp.add( Box08, "wrap 2" );
		dp.add( Box09, "wrap 2" );
		dp.add( Box10, "wrap 2" );
		dp.add( Box11, "wrap 2" );
		dp.add( Box12, "wrap 2" );
		dp.add( Box13, "wrap 2" );
		dp.add( Box14, "wrap 2" );

		t3.add(dp, "wrap 5");
		
		mp.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		mp.setBorder(BorderFactory.createTitledBorder(res.getString("metdatapipelines")+": "));	
		
		mp.add( Box15, "wrap 2" );
		mp.add( Box16, "wrap 2" );
		mp.add( Box17, "wrap 2" );
		mp.add( Box18, "wrap 2" );
		mp.add( Box19, "wrap 2" );

		t3.add(mp, "wrap 5");

		tpPane.addTab(res.getString("datastreams"), t1);
		tpPane.addTab(res.getString("relations"), t2);
		tpPane.addTab(res.getString("pipelines"), t3);
	
		JScrollPane scrollPane = new JScrollPane(tpPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
	            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		container.add(scrollPane, "grow, wrap 10");		
			
		Box c5  = Box.createHorizontalBox();
		c5.add(jpbProgessBar);
		c5.add(new JLabel("    "));
		c5.add(jbApply);
		c5.add(new JLabel(" "));
		c5.add(jbClose);
		
		container.add(c5, "gapleft push, wrap 10" );

	}

	class TextKeyListener implements KeyListener {
		
		public void keyTyped( KeyEvent e ) {
			char ch = e.getKeyChar();

			if ( ch >= 'A' && ch <= 'Z' ) {
				return;
			}
			if ( ch >= 'a' && ch <= 'z' ) {
				e.setKeyChar( (char) ( ch - 32 ) );
				return;
			}
			if ( ch >= '0' && ch <= '9' ) {
				return;
			}
			if ( ch == '_') {
				return;
			}
			if ( ch == '-' ) {
				return;
			}
			if ( ch == '.' ) {
				return;
			}
			Toolkit.getDefaultToolkit().beep();
			e.consume();

		}

		public void keyPressed( KeyEvent e ) {}

		public void keyReleased( KeyEvent e ) {}
		
	}

}

