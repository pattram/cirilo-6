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

import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CHint;
import org.emile.client.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.*;

import org.apache.log4j.Logger;

public class GuiReplaceDialog extends CGuiComposite {
	
	private static Logger log = Logger.getLogger(GuiReplaceDialog.class);
	
	protected Container container;

	protected JTabbedPane jtpPane;
	protected JEditorPane jLogView;
	
	protected JProgressBar jpbProgessBar;
		
	protected JLabel jlMarkAsLanguageResource;
	protected JComboBox<String> jcbsMarkAsLanguageResource;
	protected JLabel jlRefreshObjectFromSource;
	protected JComboBox<String> jcbsRefreshObjectFromSource;
	protected JComboBox<String> jcbGroup;
	protected JComboBox<String> jcbsGroup;

	protected JTextField jtfStylesheet;
	protected JButton jbStylesheet;	
	protected JComboBox<String> jcbDatastreams;
	protected JComboBox<String> jcbsTransformations;

	protected JList<String> jtRels;
	protected JComboBox<String> jcbsRels;
	protected JList<String> jtNonRels;

	protected JButton jbRelAdd;
	protected JButton jbRelRemove;
	protected JButton jbRelFind;
	protected JButton jbRelFindAdd;
	protected JButton jbRelFindDel;
	protected JComboBox<String> jcbRelFind;

	protected JTextArea jtaQuery;
	protected JComboBox<String> jcbsQuery;

	protected JComboBox<String> jcbVersionableDatastreams;
	protected JComboBox<String> jcbsVersionableDatastreams;
	protected JComboBox<String> jcbContentModels;

	protected Box Box00;
	protected JLabel jlXsl00;
	protected JTextField jtfPipeline00;
	protected JComboBox<String> jcbsPipeline00;
	protected JButton jbChoosePipeline00;
	
	protected Box Box01;
	protected JLabel jlXsl01;
	protected JTextField jtfPipeline01;
	protected JComboBox<String> jcbsPipeline01;
	protected JButton jbChoosePipeline01;
	
	protected Box Box02;
	protected JLabel jlXsl02;
	protected JTextField jtfPipeline02;
	protected JComboBox<String> jcbsPipeline02;
	protected JButton jbChoosePipeline02;

	protected Box Box03;
	protected JLabel jlXsl03;
	protected JTextField jtfPipeline03;
	protected JComboBox<String> jcbsPipeline03;
	protected JButton jbChoosePipeline03;
	
	protected Box Box04;
	protected JLabel jlXsl04;
	protected JTextField jtfPipeline04;
	protected JComboBox<String> jcbsPipeline04;
	protected JButton jbChoosePipeline04;
	
	protected Box Box05;
	protected JLabel jlXsl05;
	protected JTextField jtfPipeline05;
	protected JComboBox<String> jcbsPipeline05;
	protected JButton jbChoosePipeline05;
	
	protected Box Box06;
	protected JLabel jlXsl06;
	protected JTextField jtfPipeline06;
	protected JComboBox<String> jcbsPipeline06;
	protected JButton jbChoosePipeline06;
	
	protected Box Box07;
	protected JLabel jlXsl07;
	protected JTextField jtfPipeline07;
	protected JComboBox<String> jcbsPipeline07;
	protected JButton jbChoosePipeline07;
	
	protected Box Box08;
	protected JLabel jlXsl08;
	protected JTextField jtfPipeline08;
	protected JComboBox<String> jcbsPipeline08;
	protected JButton jbChoosePipeline08;
	
	protected Box Box09;
	protected JLabel jlXsl09;
	protected JTextField jtfPipeline09;
	protected JComboBox<String> jcbsPipeline09;
	protected JButton jbChoosePipeline09;
	
	protected Box Box10;
	protected JLabel jlXsl10;
	protected JTextField jtfPipeline10;
	protected JComboBox<String> jcbsPipeline10;
	protected JButton jbChoosePipeline10;
	
	protected Box Box11;
	protected JLabel jlXsl11;
	protected JTextField jtfPipeline11;
	protected JComboBox<String> jcbsPipeline11;
	protected JButton jbChoosePipeline11;
	
	protected Box Box12;
	protected JLabel jlXsl12;
	protected JTextField jtfPipeline12;
	protected JComboBox<String> jcbsPipeline12;
	protected JButton jbChoosePipeline12;
		
	protected Box Box13;
	protected JLabel jlXsl13;
	protected JTextField jtfPipeline13;
	protected JComboBox<String> jcbsPipeline13;
	protected JButton jbChoosePipeline13;
		
	protected Box Box14;
	protected JLabel jlXsl14;
	protected JTextField jtfPipeline14;
	protected JComboBox<String> jcbsPipeline14;
	protected JButton jbChoosePipeline14;
		
	protected Box Box15;
	protected JLabel jlXsl15;
	protected JTextField jtfPipeline15;
	protected JComboBox<String> jcbsPipeline15;
	protected JButton jbChoosePipeline15;

	protected Box Box16;
	protected JLabel jlXsl16;
	protected JTextField jtfPipeline16;
	protected JComboBox<String> jcbsPipeline16;
	protected JButton jbChoosePipeline16;
	
	protected Box Box17;
	protected JLabel jlXsl17;
	protected JTextField jtfPipeline17;
	protected JComboBox<String> jcbsPipeline17;
	protected JButton jbChoosePipeline17;
	
	protected Box Box18;
	protected JLabel jlXsl18;
	protected JTextField jtfPipeline18;
	protected JComboBox<String> jcbsPipeline18;
	protected JButton jbChoosePipeline18;
	
	protected Box Box19;
	protected JLabel jlXsl19;
	protected JTextField jtfPipeline19;
	protected JComboBox<String> jcbsPipeline19;
	protected JButton jbChoosePipeline19;
	
	protected Dimension button_dim;

	protected JButton jbApply;
	protected JButton jbClose;
	protected JButton jbConfigure;


	public GuiReplaceDialog () {
		
		super("GuiReplaceDialog ");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(),e);	
		}
	}

		
	protected void setup() {

		setWidgetName(jtpPane, "jtpPane");
		setWidgetName(jLogView, "jLogView");
		
		setWidgetName(jpbProgessBar, "jpbProgessBar");

		setWidgetName(jcbsMarkAsLanguageResource, "jcbsMarkAsLanguageResource");
		setWidgetName(jcbsRefreshObjectFromSource, "jcbsRefreshObjectFromSource");
		setWidgetName(jcbGroup, "jcbGroup");
		setWidgetName(jcbsGroup, "jcbsGroup");

		setWidgetName(jtfStylesheet, "jtfStylesheet");
		setWidgetName(jbStylesheet, "jbStylesheet");
		setWidgetName(jcbDatastreams, "jcbDatastreams");
		setWidgetName(jcbsTransformations, "jcbsTransformations");
					
		setWidgetName(jtRels, "jtRels");
		setWidgetName(jcbsRels, "jcbsRels");
		setWidgetName(jtNonRels, "jtNonRels");
		setWidgetName(jbRelAdd, "jbRelAdd");
		setWidgetName(jbRelRemove, "jbRelRemove");
		setWidgetName(jbRelFind, "jbRelFind");
		setWidgetName(jcbRelFind, "jcbRelFind");
		setWidgetName(jbRelFindAdd, "jbRelFindAdd");
		setWidgetName(jbRelFindDel, "jbRelFindDel");

		setWidgetName(jtaQuery, "jtaQuery");
		setWidgetName(jcbsQuery, "jcbsQuery");
		
		setWidgetName(jcbVersionableDatastreams, "jcbVersionableDatastreams");
		setWidgetName(jcbsVersionableDatastreams, "jcbsVersionableDatastreams");
		setWidgetName(jcbContentModels, "jcbContentModels");

		setWidgetName(Box00, "Box00");
		setWidgetName(jlXsl00, "jlXsl00");
		setWidgetName(jcbsPipeline00, "jcbsPipeline00");
		setWidgetName(jtfPipeline00, "jtfPipeline00");
		setWidgetName(jbChoosePipeline00, "jbChoosePipeline00");

		setWidgetName(Box01, "Box01");
		setWidgetName(jlXsl01, "jlXsl01");
		setWidgetName(jcbsPipeline01, "jcbsPipeline01");
		setWidgetName(jtfPipeline01, "jtfPipeline01");
		setWidgetName(jbChoosePipeline01, "jbChoosePipeline01");

		setWidgetName(Box02, "Box02");
		setWidgetName(jlXsl02, "jlXsl02");
		setWidgetName(jcbsPipeline02, "jcbsPipeline02");
		setWidgetName(jtfPipeline02, "jtfPipeline02");
		setWidgetName(jbChoosePipeline02, "jbChoosePipeline02");

		setWidgetName(Box03, "Box03");
		setWidgetName(jlXsl03, "jlXsl03");
		setWidgetName(jcbsPipeline03, "jcbsPipeline03");
		setWidgetName(jtfPipeline03, "jtfPipeline03");
		setWidgetName(jbChoosePipeline03, "jbChoosePipeline03");
		
		setWidgetName(Box04, "Box04");
		setWidgetName(jlXsl04, "jlXsl04");
		setWidgetName(jcbsPipeline04, "jcbsPipeline04");
		setWidgetName(jtfPipeline04, "jtfPipeline04");
		setWidgetName(jbChoosePipeline04, "jbChoosePipeline04");
		
		setWidgetName(Box05, "Box05");
		setWidgetName(jlXsl05, "jlXsl05");
		setWidgetName(jcbsPipeline05, "jcbsPipeline05");
		setWidgetName(jtfPipeline05, "jtfPipeline05");
		setWidgetName(jbChoosePipeline05, "jbChoosePipeline05");
		
		setWidgetName(Box06, "Box06");
		setWidgetName(jlXsl06, "jlXsl06");
		setWidgetName(jcbsPipeline06, "jcbsPipeline06");
		setWidgetName(jtfPipeline06, "jtfPipeline06");
		setWidgetName(jbChoosePipeline06, "jbChoosePipeline06");
		
		setWidgetName(Box07, "Box07");
		setWidgetName(jlXsl07, "jlXsl07");
		setWidgetName(jcbsPipeline07, "jcbsPipeline07");
		setWidgetName(jtfPipeline07, "jtfPipeline07");
		setWidgetName(jbChoosePipeline07, "jbChoosePipeline07");
		
		setWidgetName(Box08, "Box08");
		setWidgetName(jlXsl08, "jlXsl08");
		setWidgetName(jcbsPipeline08, "jcbsPipeline08");
		setWidgetName(jtfPipeline08, "jtfPipeline08");
		setWidgetName(jbChoosePipeline08, "jbChoosePipeline08");
		
		setWidgetName(Box09, "Box09");
		setWidgetName(jlXsl09, "jlXsl09");
		setWidgetName(jcbsPipeline09, "jcbsPipeline09");
		setWidgetName(jtfPipeline09, "jtfPipeline09");
		setWidgetName(jbChoosePipeline09, "jbChoosePipeline09");
		
		setWidgetName(Box10, "Box10");
		setWidgetName(jlXsl10, "jlXsl10");
		setWidgetName(jcbsPipeline10, "jcbsPipeline10");
		setWidgetName(jtfPipeline10, "jtfPipeline10");
		setWidgetName(jbChoosePipeline10, "jbChoosePipeline10");
		
		setWidgetName(Box11, "Box11");
		setWidgetName(jlXsl11, "jlXsl11");
		setWidgetName(jcbsPipeline11, "jcbsPipeline11");
		setWidgetName(jtfPipeline11, "jtfPipeline11");
		setWidgetName(jbChoosePipeline11, "jbChoosePipeline11");
		
		setWidgetName(Box12, "Box12");
		setWidgetName(jlXsl12, "jlXsl12");
		setWidgetName(jcbsPipeline12, "jcbsPipeline12");
		setWidgetName(jtfPipeline12, "jtfPipeline12");
		setWidgetName(jbChoosePipeline12, "jbChoosePipeline12");
		
		setWidgetName(Box13, "Box13");
		setWidgetName(jlXsl13, "jlXsl13");
		setWidgetName(jcbsPipeline13, "jcbsPipeline13");
		setWidgetName(jtfPipeline13, "jtfPipeline13");
		setWidgetName(jbChoosePipeline13, "jbChoosePipeline13");
		
		setWidgetName(Box14, "Box14");
		setWidgetName(jlXsl14, "jlXsl14");
		setWidgetName(jcbsPipeline14, "jcbsPipeline14");
		setWidgetName(jtfPipeline14, "jtfPipeline14");
		setWidgetName(jbChoosePipeline14, "jbChoosePipeline14");
		
		setWidgetName(Box15, "Box15");
		setWidgetName(jlXsl15, "jlXsl15");
		setWidgetName(jcbsPipeline15, "jcbsPipeline15");
		setWidgetName(jtfPipeline15, "jtfPipeline15");
		setWidgetName(jbChoosePipeline15, "jbChoosePipeline15");

		setWidgetName(Box16, "Box16");
		setWidgetName(jlXsl16, "jlXsl16");
		setWidgetName(jcbsPipeline16, "jcbsPipeline16");
		setWidgetName(jtfPipeline16, "jtfPipeline16");
		setWidgetName(jbChoosePipeline16, "jbChoosePipeline16");
		
		setWidgetName(Box17, "Box17");
		setWidgetName(jlXsl17, "jlXsl17");
		setWidgetName(jcbsPipeline17, "jcbsPipeline17");
		setWidgetName(jtfPipeline17, "jtfPipeline17");
		setWidgetName(jbChoosePipeline17, "jbChoosePipeline17");
		
		setWidgetName(Box18, "Box18");
		setWidgetName(jlXsl18, "jlXsl18");
		setWidgetName(jcbsPipeline18, "jcbsPipeline18");
		setWidgetName(jtfPipeline18, "jtfPipeline18");
		setWidgetName(jbChoosePipeline18, "jbChoosePipeline18");
		
		setWidgetName(Box19, "Box19");
		setWidgetName(jlXsl19, "jlXsl19");
		setWidgetName(jcbsPipeline19, "jcbsPipeline19");
		setWidgetName(jtfPipeline19, "jtfPipeline19");
		setWidgetName(jbChoosePipeline19, "jbChoosePipeline19");

		setWidgetName(jbApply, "jbApply");
		setWidgetName(jbClose, "jbClose");
		
		setWidgetName( jbConfigure, "jbConfigure" );
		
	}

	private void jbInit() throws Exception {
		
		try {
		ResourceBundle res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
		
		int pipeline_label_width = new Integer(props.getProperty("user", "FontSize"))*16;
		
		Object[] SET = { "-", res.getString("set"), res.getString("unset") };
		Object[] APPLY = { "-", res.getString("apply") };
		Object[] ADD = { "-", res.getString("add"), res.getString("replace"), res.getString("remove") };
		Object[] NEW = { "-", res.getString("apply") };
		Object[] REPLACE = { "-", res.getString("replace"), res.getString("remove") };
						
		container = new Container();	
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));

	    jtpPane = new JTabbedPane();  
	    
		jLogView = new JEditorPane();
		JScrollPane scrPane = new JScrollPane(jLogView);

		jpbProgessBar = new JProgressBar();	
		jpbProgessBar.setVisible(false);
		jpbProgessBar.setIndeterminate(true);
		jpbProgessBar.setStringPainted(true);
	    
	    jlMarkAsLanguageResource = new JLabel(res.getString("langresource"));
	    jcbsMarkAsLanguageResource = new JComboBox(SET);
	    jcbsMarkAsLanguageResource.setPreferredSize(new Dimension(75, jcbsMarkAsLanguageResource.getPreferredSize().height));
	    jlRefreshObjectFromSource = new JLabel(res.getString("objectfromsource"));
	    jcbsRefreshObjectFromSource = new JComboBox(APPLY);
	    jcbsRefreshObjectFromSource.setPreferredSize(new Dimension(75, jcbsRefreshObjectFromSource.getPreferredSize().height));
		jcbGroup = new JComboBox();
		jcbsGroup = new JComboBox(APPLY);
		jcbsGroup.setPreferredSize(new Dimension(75, jcbsGroup.getPreferredSize().height));

		jtfStylesheet = new JTextField();
		jtfStylesheet.setPreferredSize(new Dimension(600, jtfStylesheet.getPreferredSize().height));
		
		int button_width = (int) Math.round(new Integer(props.getProperty("user", "FontSize"))*2.7);
		button_dim = new Dimension(Math.max(button_width, 32), jtfStylesheet.getPreferredSize().height+2);

		jbStylesheet = Utils.createOpenButton(button_dim);
		
		jcbsTransformations = new JComboBox(APPLY);	
		jcbDatastreams = new JComboBox();
		
		jtRels = new JList(new DefaultListModel());
		jtRels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jcbsRels = new JComboBox(ADD);
		jcbsRels.setPreferredSize(new Dimension(75, jcbsRels.getPreferredSize().height));

		jtNonRels = new JList(new DefaultListModel());
		jtNonRels.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);	
		
		jbRelAdd = new JButton(res.getString("add"));
		jbRelAdd.setToolTipText(res.getString("TTcontextadd"));
		jbRelRemove = new JButton(res.getString("remove"));
		jbRelRemove.setToolTipText(res.getString("TTcontextdel"));
		jcbRelFind = new JComboBox();  
		jcbRelFind.setPreferredSize(new Dimension(500, jcbRelFind.getPreferredSize().height));
		jbRelFind = new JButton(res.getString("search").replaceAll("[. ]", ""));
		jbRelFind.setToolTipText(res.getString("TTsearchtermfind"));
		jbRelFindAdd = new JButton("+");
		jbRelFindAdd.setToolTipText(res.getString("TTsearchtermadd"));		
		jbRelFindDel = new JButton("-");
		jbRelFindDel.setToolTipText(res.getString("TTsearchtermdel"));

		jtaQuery = new JTextArea();
		jcbsQuery = new JComboBox(APPLY);
		
		jcbVersionableDatastreams = new JComboBox<String>();
		jcbsVersionableDatastreams =  new JComboBox(NEW);
		jcbsVersionableDatastreams.setPreferredSize(new Dimension(75, jcbsVersionableDatastreams.getPreferredSize().height));
		jcbVersionableDatastreams = new JComboBox();
		jcbContentModels = new JComboBox<String>();
		
		Box00 = Box.createHorizontalBox();
		jlXsl00 =  new JLabel("");
		jlXsl00.setPreferredSize(new Dimension(pipeline_label_width, jlXsl00.getPreferredSize().height));
		jcbsPipeline00 = new JComboBox(REPLACE);
		jtfPipeline00 = new JTextField();
		jtfPipeline00.setPreferredSize(new Dimension(600, jtfPipeline00.getPreferredSize().height));		
		jbChoosePipeline00 = Utils.createOpenButton(button_dim);

		Box00.add(jlXsl00);
		Box00.add(jcbsPipeline00);
		Box00.add(new JLabel(" "));
		Box00.add(jtfPipeline00);
		Box00.add(jbChoosePipeline00);
		
		Box01 = Box.createHorizontalBox();
		jlXsl01 = new JLabel("");
		jlXsl01.setPreferredSize(new Dimension(pipeline_label_width, jlXsl01.getPreferredSize().height));
		jcbsPipeline01 = new JComboBox(REPLACE);
		jtfPipeline01 = new JTextField();
		jtfPipeline01.setPreferredSize(new Dimension(600, jcbsPipeline01.getPreferredSize().height));
		jbChoosePipeline01 = Utils.createOpenButton(button_dim);

		Box01.add(jlXsl01);
		Box01.add(jcbsPipeline01);
		Box01.add(new JLabel(" "));
		Box01.add(jtfPipeline01);
		Box01.add(jbChoosePipeline01);
			
		Box02 = Box.createHorizontalBox();
		jlXsl02 = new JLabel("");
		jlXsl02.setPreferredSize(new Dimension(pipeline_label_width, jlXsl02.getPreferredSize().height));
		jcbsPipeline02 = new JComboBox(REPLACE);
		jtfPipeline02 = new JTextField();
		jtfPipeline02.setPreferredSize(new Dimension(600, jtfPipeline02.getPreferredSize().height));
		jbChoosePipeline02 = Utils.createOpenButton(button_dim);	
	
		Box02.add(jlXsl02);
		Box02.add(jcbsPipeline02);
		Box02.add(new JLabel(" "));
		Box02.add(jtfPipeline02);
		Box02.add(jbChoosePipeline02);
	
		Box03 = Box.createHorizontalBox();
		jlXsl03 = new JLabel("");
		jlXsl03.setPreferredSize(new Dimension(pipeline_label_width, jlXsl03.getPreferredSize().height));
		jcbsPipeline03 = new JComboBox(REPLACE);
		jtfPipeline03 = new JTextField();
		jtfPipeline03.setPreferredSize(new Dimension(600, jtfPipeline03.getPreferredSize().height));
		jbChoosePipeline03 = Utils.createOpenButton(button_dim);
	
		Box03.add(jlXsl03);
		Box03.add(jcbsPipeline03);
		Box03.add(new JLabel(" "));
		Box03.add(jtfPipeline03);
		Box03.add(jbChoosePipeline03);
	
		Box04 = Box.createHorizontalBox();
		jlXsl04 = new JLabel("");
		jlXsl04.setPreferredSize(new Dimension(pipeline_label_width, jlXsl04.getPreferredSize().height));
		jcbsPipeline04 = new JComboBox(REPLACE);
		jtfPipeline04 = new JTextField();
		jtfPipeline04.setPreferredSize(new Dimension(600, jtfPipeline04.getPreferredSize().height));
		jbChoosePipeline04 = Utils.createOpenButton(button_dim);
	
		Box04.add(jlXsl04);
		Box04.add(jcbsPipeline04);
		Box04.add(new JLabel(" "));
		Box04.add(jtfPipeline04);
		Box04.add(jbChoosePipeline04);
	
		Box05 = Box.createHorizontalBox();
		jlXsl05 = new JLabel("");
		jlXsl05.setPreferredSize(new Dimension(pipeline_label_width, jlXsl05.getPreferredSize().height));
		jcbsPipeline05 = new JComboBox(REPLACE);
		jtfPipeline05 = new JTextField();
		jtfPipeline05.setPreferredSize(new Dimension(600, jtfPipeline05.getPreferredSize().height));
		jbChoosePipeline05 = Utils.createOpenButton(button_dim);
	
		Box05.add(jlXsl05);
		Box05.add(jcbsPipeline05);
		Box05.add(new JLabel(" "));
		Box05.add(jtfPipeline05);
		Box05.add(jbChoosePipeline05);
	
		Box06 = Box.createHorizontalBox();
		jlXsl06 = new JLabel("");
		jlXsl06.setPreferredSize(new Dimension(pipeline_label_width, jlXsl06.getPreferredSize().height));
		jcbsPipeline06 = new JComboBox(REPLACE);
		jtfPipeline06 = new JTextField();
		jtfPipeline06.setPreferredSize(new Dimension(600, jtfPipeline06.getPreferredSize().height));
		jbChoosePipeline06 = Utils.createOpenButton(button_dim);
	
		Box06.add(jlXsl06);
		Box06.add(jcbsPipeline06);
		Box06.add(new JLabel(" "));
		Box06.add(jtfPipeline06);
		Box06.add(jbChoosePipeline06);
	
		Box07 = Box.createHorizontalBox();
		jlXsl07 = new JLabel("");
		jlXsl07.setPreferredSize(new Dimension(pipeline_label_width, jlXsl07.getPreferredSize().height));
		jcbsPipeline07 = new JComboBox(REPLACE);
		jtfPipeline07 = new JTextField();
		jtfPipeline07.setPreferredSize(new Dimension(600, jtfPipeline07.getPreferredSize().height));
		jbChoosePipeline07 = Utils.createOpenButton(button_dim);
	
		Box07.add(jlXsl07);
		Box07.add(jcbsPipeline07);
		Box07.add(new JLabel(" "));
		Box07.add(jtfPipeline07);
		Box07.add(jbChoosePipeline07);
	
		Box08 = Box.createHorizontalBox();
		jlXsl08 = new JLabel("");
		jlXsl08.setPreferredSize(new Dimension(pipeline_label_width, jlXsl08.getPreferredSize().height));
		jcbsPipeline08 = new JComboBox(REPLACE);
		jtfPipeline08 = new JTextField();
		jtfPipeline08.setPreferredSize(new Dimension(600, jtfPipeline08.getPreferredSize().height));
		jbChoosePipeline08 = Utils.createOpenButton(button_dim);
	
		Box08.add(jlXsl08);
		Box08.add(jcbsPipeline08);
		Box08.add(new JLabel(" "));
		Box08.add(jtfPipeline08);
		Box08.add(jbChoosePipeline08);
	
		Box09 = Box.createHorizontalBox();
		jlXsl09 = new JLabel("");
		jlXsl09.setPreferredSize(new Dimension(pipeline_label_width, jlXsl09.getPreferredSize().height));
		jcbsPipeline09 = new JComboBox(REPLACE);
		jtfPipeline09 = new JTextField();
		jtfPipeline09.setPreferredSize(new Dimension(600, jtfPipeline09.getPreferredSize().height));
		jbChoosePipeline09 = Utils.createOpenButton(button_dim);
	
		Box09.add(jlXsl09);
		Box09.add(jcbsPipeline09);
		Box09.add(new JLabel(" "));
		Box09.add(jtfPipeline09);
		Box09.add(jbChoosePipeline09);
	
		Box10 = Box.createHorizontalBox();
		jlXsl10 = new JLabel("");
		jlXsl10.setPreferredSize(new Dimension(pipeline_label_width, jlXsl10.getPreferredSize().height));
		jcbsPipeline10 = new JComboBox(REPLACE);
		jtfPipeline10 = new JTextField();
		jtfPipeline10.setPreferredSize(new Dimension(600, jtfPipeline10.getPreferredSize().height));
		jbChoosePipeline10 = Utils.createOpenButton(button_dim);
	
		Box10.add(jlXsl10);
		Box10.add(jcbsPipeline10);
		Box10.add(new JLabel(" "));
		Box10.add(jtfPipeline10);
		Box10.add(jbChoosePipeline10);
	
		Box11 = Box.createHorizontalBox();
		jlXsl11 = new JLabel("");
		jlXsl11.setPreferredSize(new Dimension(pipeline_label_width, jlXsl11.getPreferredSize().height));
		jcbsPipeline11 = new JComboBox(REPLACE);
		jtfPipeline11 = new JTextField();
		jtfPipeline11.setPreferredSize(new Dimension(600, jtfPipeline11.getPreferredSize().height));
		jbChoosePipeline11 = Utils.createOpenButton(button_dim);
	
		Box11.add(jlXsl11);
		Box11.add(jcbsPipeline11);
		Box11.add(new JLabel(" "));
		Box11.add(jtfPipeline11);
		Box11.add(jbChoosePipeline11);
	
		Box12 = Box.createHorizontalBox();
		jlXsl12 = new JLabel("");
		jlXsl12.setPreferredSize(new Dimension(pipeline_label_width, jlXsl12.getPreferredSize().height));
		jcbsPipeline12 = new JComboBox(REPLACE);
		jtfPipeline12 = new JTextField();
		jtfPipeline12.setPreferredSize(new Dimension(600, jtfPipeline12.getPreferredSize().height));
		jbChoosePipeline12 = Utils.createOpenButton(button_dim);
	
		Box12.add(jlXsl12);
		Box12.add(jcbsPipeline12);
		Box12.add(new JLabel(" "));
		Box12.add(jtfPipeline12);
		Box12.add(jbChoosePipeline12);
	
		Box13 = Box.createHorizontalBox();
		jlXsl13 = new JLabel("");
		jlXsl13.setPreferredSize(new Dimension(pipeline_label_width, jlXsl13.getPreferredSize().height));
		jcbsPipeline13 = new JComboBox(REPLACE);
		jtfPipeline13 = new JTextField();
		jtfPipeline13.setPreferredSize(new Dimension(600, jtfPipeline13.getPreferredSize().height));
		jbChoosePipeline13 = Utils.createOpenButton(button_dim);
	
		Box13.add(jlXsl13);
		Box13.add(jcbsPipeline13);
		Box13.add(new JLabel(" "));
		Box13.add(jtfPipeline13);
		Box13.add(jbChoosePipeline13);
	
		Box14 = Box.createHorizontalBox();
		jlXsl14 = new JLabel("");
		jlXsl14.setPreferredSize(new Dimension(pipeline_label_width, jlXsl14.getPreferredSize().height));
		jcbsPipeline14 = new JComboBox(REPLACE);
		jtfPipeline14 = new JTextField();
		jtfPipeline14.setPreferredSize(new Dimension(600, jtfPipeline14.getPreferredSize().height));
		jbChoosePipeline14 = Utils.createOpenButton(button_dim);
	
		Box14.add(jlXsl14);
		Box14.add(jcbsPipeline14);
		Box14.add(new JLabel(" "));
		Box14.add(jtfPipeline14);
		Box14.add(jbChoosePipeline14);
	
		Box15 = Box.createHorizontalBox();
		jlXsl15 = new JLabel("");
		jlXsl15.setPreferredSize(new Dimension(pipeline_label_width, jlXsl15.getPreferredSize().height));
		jcbsPipeline15 = new JComboBox(REPLACE);
		jtfPipeline15 = new JTextField();
		jtfPipeline15.setPreferredSize(new Dimension(600, jtfPipeline15.getPreferredSize().height));
		jbChoosePipeline15 = Utils.createOpenButton(button_dim);
	
		Box15.add(jlXsl15);
		Box15.add(jcbsPipeline15);
		Box15.add(new JLabel(" "));
		Box15.add(jtfPipeline15);
		Box15.add(jbChoosePipeline15);
			
		Box16 = Box.createHorizontalBox();
		jlXsl16 = new JLabel("");
		jlXsl16.setPreferredSize(new Dimension(pipeline_label_width, jlXsl16.getPreferredSize().height));
		jcbsPipeline16 = new JComboBox(REPLACE);
		jtfPipeline16 = new JTextField();
		jtfPipeline16.setPreferredSize(new Dimension(600, jtfPipeline16.getPreferredSize().height));
		jbChoosePipeline16 = Utils.createOpenButton(button_dim);
	
		Box16.add(jlXsl16);
		Box16.add(jcbsPipeline16);
		Box16.add(new JLabel(" "));
		Box16.add(jtfPipeline16);
		Box16.add(jbChoosePipeline16);

		Box17 = Box.createHorizontalBox();
		jlXsl17 = new JLabel("");
		jlXsl17.setPreferredSize(new Dimension(pipeline_label_width, jlXsl17.getPreferredSize().height));
		jcbsPipeline17 = new JComboBox(REPLACE);
		jtfPipeline17 = new JTextField();
		jtfPipeline17.setPreferredSize(new Dimension(600, jtfPipeline17.getPreferredSize().height));
		jbChoosePipeline17 = Utils.createOpenButton(button_dim);
	
		Box17.add(jlXsl17);
		Box17.add(jcbsPipeline17);
		Box17.add(new JLabel(" "));
		Box17.add(jtfPipeline17);
		Box17.add(jbChoosePipeline17);

		Box18 = Box.createHorizontalBox();
		jlXsl18 = new JLabel("");
		jlXsl18.setPreferredSize(new Dimension(pipeline_label_width, jlXsl18.getPreferredSize().height));
		jcbsPipeline18 = new JComboBox(REPLACE);
		jtfPipeline18 = new JTextField();
		jtfPipeline18.setPreferredSize(new Dimension(600, jtfPipeline18.getPreferredSize().height));
		jbChoosePipeline18 = Utils.createOpenButton(button_dim);
	
		Box18.add(jlXsl18);
		Box18.add(jcbsPipeline18);
		Box18.add(new JLabel(" "));
		Box18.add(jtfPipeline18);
		Box18.add(jbChoosePipeline18);

		Box19 = Box.createHorizontalBox();
		jlXsl19 = new JLabel("");
		jlXsl19.setPreferredSize(new Dimension(pipeline_label_width, jlXsl19.getPreferredSize().height));
		jcbsPipeline19 = new JComboBox(REPLACE);
		jtfPipeline19 = new JTextField();
		jtfPipeline19.setPreferredSize(new Dimension(600, jtfPipeline19.getPreferredSize().height));
		jbChoosePipeline19 = Utils.createOpenButton(button_dim);
	
		Box19.add(jlXsl19);
		Box19.add(jcbsPipeline19);
		Box19.add(new JLabel(" "));
		Box19.add(jtfPipeline19);
		Box19.add(jbChoosePipeline19);
		
		jbApply = new JButton(res.getString("apply"));
		jbClose = new JButton(res.getString("close"));				
		jbConfigure = new JButton( res.getString("configure") );


		JPanel sd = new JPanel();
		sd.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		sd.setBorder(BorderFactory.createTitledBorder(res.getString("setdc")+": "));	
	
		Container t1 = new Container();
		t1.setLayout(new net.miginfocom.swing.MigLayout(""));
		
		JPanel ex = new JPanel();
		ex.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		ex.setBorder(BorderFactory.createTitledBorder(res.getString("executexslt")+": "));	

	    ex.add(jcbsTransformations);
	    ex.add(new JLabel(" "));
	    ex.add(jcbDatastreams);
	    ex.add(new JLabel(" "));
		Box c2  = Box.createHorizontalBox();
		c2.add(new JLabel("XSLT: "));
		c2.add(jtfStylesheet);
        c2.add(jbStylesheet);
        ex.add(c2);
        
        t1.add(ex);
 		
		JPanel cm = new JPanel();
		cm.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		cm.setBorder(BorderFactory.createTitledBorder(res.getString("setcontext")+": "));	

		Container t2 = new Container();
		t2.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		
		cm.add(new JLabel(res.getString("selected")+":"), "wrap 2");
		cm.add(new JScrollPane(jtRels), "height 100:200:400, growx, wrap 5");
		Box c3  = Box.createHorizontalBox();
		c3.add(jcbsRels);
		c3.add(new JLabel(" "));
		c3.add(jbRelRemove);
		cm.add( c3, "gapleft push, wrap 10" );
		cm.add(new JScrollPane(jtNonRels), "height 100:200:400, growx, wrap 5" );
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
		cm.add(c4);
		
		t2.add(cm);
		
		JPanel co = new JPanel();
		co.setPreferredSize(new Dimension(600, co.getPreferredSize().height));
		co.setLayout(new net.miginfocom.swing.MigLayout(""));
		co.setBorder(BorderFactory.createTitledBorder(res.getString("changeowner")+": "));	

		JPanel sp = new JPanel();
		sp.setPreferredSize(new Dimension(600, sp.getPreferredSize().height));
		sp.setLayout(new net.miginfocom.swing.MigLayout(""));
		sp.setBorder(BorderFactory.createTitledBorder(res.getString("setprop")+": "));	

		JPanel cv = new JPanel();
		cv.setPreferredSize(new Dimension(600, cv.getPreferredSize().height));
		cv.setLayout(new net.miginfocom.swing.MigLayout(""));
		cv.setBorder(BorderFactory.createTitledBorder(res.getString("perfop")+": "));	

		JPanel lg = new JPanel();
		lg.setPreferredSize(new Dimension(600, lg.getPreferredSize().height));
		lg.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		lg.setBorder(BorderFactory.createTitledBorder(res.getString("actlog")));	

		Container t3 = new Container();
		t3.setLayout(new net.miginfocom.swing.MigLayout(""));
		
		co.add(new JLabel(res.getString("owner") + ": "));
		co.add(jcbsGroup);
		co.add(new JLabel(" "));
		co.add(jcbGroup);

		sp.add(jcbsMarkAsLanguageResource );
		sp.add(new JLabel(" "));
		sp.add(jlMarkAsLanguageResource, "grow, wrap 10");
				
		cv.add(new JLabel(res.getString("createversion")));
		cv.add(new JLabel(" "));
		cv.add(jcbsVersionableDatastreams);
		cv.add(new JLabel(res.getString("named")));
		cv.add(jcbVersionableDatastreams,"grow, wrap 5");
		cv.add(jlRefreshObjectFromSource);
		cv.add(new JLabel(" "));
		cv.add(jcbsRefreshObjectFromSource, "grow, wrap 10");
	
		lg.add( scrPane, "height 250:400:600, growx, wrap 10");

		t3.add(co, "wrap 5");
		t3.add(sp, "wrap 5");
		t3.add(cv, "wrap 20");
		t3.add(lg, "wrap 5");
			
		Container t4 = new Container();
		t4.setLayout(new net.miginfocom.swing.MigLayout("fillx",""));
		
		JPanel sq = new JPanel();
		sq.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		sq.setBorder(BorderFactory.createTitledBorder(res.getString("subsquery")+": "));	

		sq.add(new JScrollPane(jtaQuery), "height 100:200:500, growx, wrap 5");
		sq.add(jcbsQuery);
		
		t4.add(sq, "height 100:200:500, growx, wrap 5");

		CHint hint0 = new CHint();
		hint0.setText(res.getString("HTreplself"));;
		t4.add(hint0);
		
		Container t5 = new Container();
		t5.setLayout(new net.miginfocom.swing.MigLayout("fillx"));

		JPanel hd = new JPanel();
		JPanel ip = new JPanel();
		JPanel dp = new JPanel();
		JPanel mp = new JPanel();

		hd.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		hd.setBorder(BorderFactory.createTitledBorder(res.getString("setpipelines")+": "));	
		
		Box bhd =  Box.createHorizontalBox();
		bhd.add(jcbContentModels);
		bhd.add(new JLabel("  "));
		bhd.add(jbConfigure);
		bhd.add(new JLabel("  "));

		CHint hint1 = new CHint();
		hint1.setText(res.getString("HTonlyselcm"));;
		bhd.add(hint1);

		hd.add(bhd, "wrap 5");
		
		t5.add(hd, "wrap 5");

		ip.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		ip.setBorder(BorderFactory.createTitledBorder(res.getString("ingestpipelines")+": "));		
				
		ip.add( Box00, "wrap 2");
		ip.add( Box01, "wrap 2");
		ip.add( Box02, "wrap 2");
		ip.add( Box03, "wrap 2");
		ip.add( Box04, "wrap 2");
		
		hd.add(ip, "wrap 2");
		
		dp.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		dp.setBorder(BorderFactory.createTitledBorder(res.getString("disspipelines")+": "));	

		dp.add( Box05, "wrap 2");
		dp.add( Box06, "wrap 2");
		dp.add( Box07, "wrap 2" );
		dp.add( Box08, "wrap 2" );
		dp.add( Box09, "wrap 2" );
		dp.add( Box10, "wrap 2" );
		dp.add( Box11, "wrap 2" );
		dp.add( Box12, "wrap 2" );
		dp.add( Box13, "wrap 2" );
		dp.add( Box14, "wrap 2" );
	
		hd.add(dp, "wrap 2");
		
		mp.setLayout(new net.miginfocom.swing.MigLayout("fillx"));
		mp.setBorder(BorderFactory.createTitledBorder(res.getString("metdatapipelines")+": "));	
		
		mp.add( Box15, "wrap 2" );
		mp.add( Box16, "wrap 2" );
		mp.add( Box17, "wrap 2" );
		mp.add( Box18, "wrap 2" );
		mp.add( Box19, "wrap 2" );
		
		hd.add(mp, "wrap 2");

		jtpPane.addTab(res.getString("general"), t3);
		jtpPane.addTab(res.getString("transformations"), t1);
		jtpPane.addTab(res.getString("pipelines"), t5);
		jtpPane.addTab(res.getString("relations"), t2);
		jtpPane.addTab(res.getString("queries"), t4);
		
		
		JScrollPane scrollPane = new JScrollPane(jtpPane);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		container.add(scrollPane, "grow, wrap 5");	
		
		Box bb =  Box.createHorizontalBox();
		bb.add(jpbProgessBar);
		bb.add(new JLabel("  "));
		bb.add(jbApply);
		bb.add(new JLabel(" "));
		bb.add(jbClose);
		container.add(bb, "gapleft push, wrap 5" );
		} catch (Exception e) {}

	}

}
