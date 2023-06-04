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


import java.awt.Container;
import java.awt.Dimension;
import java.util.ResourceBundle;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.gui.CGuiComposite; 

import javax.swing.*;

import org.apache.log4j.Logger;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.core.CHyperlink;

public class GuiPreferencesDialog extends CGuiComposite {

	private static Logger log = Logger.getLogger(GuiPreferencesDialog.class);
		
	protected ResourceBundle res;
	protected Container container;

	protected JCheckBox jcbCUBEExtractDCMI;
	protected JCheckBox jcbCUBEApplySemanticPolicy;
	protected JCheckBox jcbCUBEExecuteCustomization;
	protected JCheckBox jcbCUBEExecuteValidation;
	protected JCheckBox jcbCUBETriggerPipelines;
	protected JCheckBox jcbCUBEResolveSKOS;
	protected JCheckBox jcbCUBEResolvePlacenames;
	protected JCheckBox jcbCUBEAcceptIDSExclusively;
	protected CHyperlink jhlCUBESeeAlso;

	protected JCheckBox jcbGMLExtractDCMI;
	protected JCheckBox jcbGMLApplySemanticPolicy;
	protected JCheckBox jcbGMLExecuteCustomization;
	protected JCheckBox jcbGMLExecuteValidation;
	protected JCheckBox jcbGMLTriggerPipelines;
	protected CHyperlink jhlGMLSeeAlso;

	protected JCheckBox jcbLIDOExtractDCMI;
	protected JCheckBox jcbLIDOApplySemanticPolicy;
	protected JCheckBox jcbLIDOExecuteCustomization;
	protected JCheckBox jcbLIDOExecuteValidation;
	protected JCheckBox jcbLIDOTriggerPipelines;
	protected JCheckBox jcbLIDOResolveSKOS;
	protected JCheckBox jcbLIDOResolvePlacenames;
	protected JCheckBox jcbLIDOAcceptIDSExclusively;
	protected JCheckBox jcbLIDOUploadImages;
	protected JCheckBox jcbLIDOGeneratePyramidalTiff;
	protected CHyperlink jhlLIDOSeeAlso;

	protected JCheckBox jcbMEIExtractDCMI;
	protected JCheckBox jcbMEIApplySemanticPolicy;
	protected JCheckBox jcbMEIExecuteCustomization;
	protected JCheckBox jcbMEIExecuteValidation;
	protected JCheckBox jcbMEITriggerPipelines;
	protected CHyperlink jhlMEISeeAlso;

	protected JCheckBox jcbMETSExtractDCMI;
	protected JCheckBox jcbMETSExecuteValidation;
	protected CHyperlink jhlMETSSeeAlso;

	protected JCheckBox jcbOntologyExtractDCMI;
	protected JCheckBox jcbOntologyUploadImages;
	protected JCheckBox jcbOntologyGeneratePyramidalTiff;
	protected CHyperlink jhlOntologySeeAlso;
	
	protected JCheckBox jcbRDFExtractDCMI;
	protected JCheckBox jcbRDFUploadImages;
	protected JCheckBox jcbRDFGeneratePyramidalTiff;
	protected CHyperlink jhlRDFSeeAlso;
	
	protected JCheckBox jcbRDOExtractDCMI;
	protected CHyperlink jhlRDOSeeAlso;
	
	protected JCheckBox jcbRTIExtractDCMI;
	protected JCheckBox jcbRTIApplySemanticPolicy;
	protected JCheckBox jcbRTIExecuteCustomization;
	protected JCheckBox jcbRTITriggerPipelines;
	protected CHyperlink jhlRTISeeAlso;

	protected JCheckBox jcbSKOSSkosify;
	protected JCheckBox jcbSKOSExtractDCMI;
	protected CHyperlink jhlSKOSSeeAlso;
	
	protected JCheckBox jcbSpectralExtractDCMI;
	protected JCheckBox jcbSpectralApplySemanticPolicy;
	protected JCheckBox jcbSpectralExecuteValidation;
	protected JCheckBox jcbSpectralGeneratePyramidalTiff;
	protected CHyperlink jhlSpectralSeeAlso;
	
	protected JCheckBox jcbSTORYExtractDCMI;
	protected CHyperlink jhlSTORYSeeAlso;

	protected JCheckBox jcbTEIExtractDCMI;
	protected JCheckBox jcbTEIApplySemanticPolicy;
	protected JCheckBox jcbTEIExecuteCustomization;
	protected JCheckBox jcbTEIExecuteValidation;
	protected JCheckBox jcbTEITriggerPipelines;
	protected JCheckBox jcbTEIResolveSKOS;
	protected JCheckBox jcbTEIResolvePlacenames;
	protected JCheckBox jcbTEIAcceptIDSExclusively;
	protected JCheckBox jcbTEIUploadImages;
	protected JCheckBox jcbTEIGeneratePyramidalTiff;
	protected JCheckBox jcbTEICreateMETS;
	protected CHyperlink jhlTEISeeAlso;

	protected JSpinner jtfFontSize;
	protected JCheckBox jcbShowByDoubleClick;
	protected JCheckBox jcbTheCure;
	protected JTabbedPane tp;
	
	protected JButton jbApply;
	protected JButton jbClose;
	
	public GuiPreferencesDialog () {
		
		super("GuiPreferencesDialog ");

		try {
			jbInit();
			setRootComponent(container);
			setup();
		} catch (Exception e) {
			log.error(e);	
		}
	}

	protected void setup() {

		setWidgetName(jtfFontSize, "jtfFontSize");
		setWidgetName(jcbShowByDoubleClick, "jcbShowByDoubleClick");
		setWidgetName(jcbTheCure, "jcbTheCure");
		
		setWidgetName(jcbCUBEExtractDCMI, "jcbCUBEExtractDCMI");		
		setWidgetName(jcbCUBEApplySemanticPolicy, "jcbCUBEApplySemanticPolicy");		
		setWidgetName(jcbCUBEExecuteCustomization, "jcbCUBEExecuteCustomization");		
		setWidgetName(jcbCUBEExecuteValidation, "jcbCUBEExecuteValidation");		
		setWidgetName(jcbCUBETriggerPipelines, "jcbCUBETriggerPipelines");				
		setWidgetName(jcbCUBEResolveSKOS, "jcbCUBEResolveSKOS");				
		setWidgetName(jcbCUBEResolvePlacenames, "jcbCUBEResolvePlacenames");				
		setWidgetName(jcbCUBEAcceptIDSExclusively, "jcbCUBEAcceptIDSExclusively");				
		setWidgetName(jhlCUBESeeAlso, "jhlCUBESeeAlso");				
		
		setWidgetName(jcbGMLExtractDCMI, "jcbGMLExtractDCMI");		
		setWidgetName(jcbGMLApplySemanticPolicy, "jcbGMLApplySemanticPolicy");		
		setWidgetName(jcbGMLExecuteCustomization, "jcbGMLExecuteCustomization");		
		setWidgetName(jcbGMLExecuteValidation, "jcbGMLExecuteValidation");		
		setWidgetName(jcbGMLTriggerPipelines, "jcbGMLTriggerPipelines");				
		setWidgetName(jhlGMLSeeAlso, "jhlGMLSeeAlso");				

		setWidgetName(jcbLIDOExtractDCMI, "jcbLIDOExtractDCMI");		
		setWidgetName(jcbLIDOApplySemanticPolicy, "jcbLIDOApplySemanticPolicy");		
		setWidgetName(jcbLIDOExecuteCustomization, "jcbLIDOExecuteCustomization");		
		setWidgetName(jcbLIDOExecuteValidation, "jcbLIDOExecuteValidation");		
		setWidgetName(jcbLIDOTriggerPipelines, "jcbLIDOTriggerPipelines");				
		setWidgetName(jcbLIDOResolveSKOS, "jcbLIDOResolveSKOS");				
		setWidgetName(jcbLIDOResolvePlacenames, "jcbLIDOResolvePlacenames");				
		setWidgetName(jcbLIDOAcceptIDSExclusively, "jcbLIDOAcceptIDSExclusively");				
		setWidgetName(jcbLIDOUploadImages, "jcbLIDOUploadImages");				
		setWidgetName(jcbLIDOGeneratePyramidalTiff, "jcbLIDOGeneratePyramidalTiff");				
		setWidgetName(jhlLIDOSeeAlso, "jhlLIDOSeeAlso");				

		setWidgetName(jcbMEIExtractDCMI, "jcbMEIExtractDCMI");		
		setWidgetName(jcbMEIApplySemanticPolicy, "jcbMEIApplySemanticPolicy");		
		setWidgetName(jcbMEIExecuteCustomization, "jcbMEIExecuteCustomization");		
		setWidgetName(jcbMEIExecuteValidation, "jcbMEIExecuteValidation");		
		setWidgetName(jcbMEITriggerPipelines, "jcbMEITriggerPipelines");				
		setWidgetName(jhlMEISeeAlso, "jhlMEISeeAlso");				

		setWidgetName(jcbMETSExtractDCMI, "jcbMETSExtractDCMI");		
		setWidgetName(jcbMETSExecuteValidation, "jcbMETSExecuteValidation");		
		setWidgetName(jhlMETSSeeAlso, "jhlMETSSeeAlso");				

		setWidgetName(jcbOntologyExtractDCMI, "jcbOntologyExtractDCMI");		
		setWidgetName(jcbOntologyUploadImages, "jcbOntologyUploadImages");		
		setWidgetName(jcbOntologyGeneratePyramidalTiff, "jcbOntologyGeneratePyramidalTiff");		
		setWidgetName(jhlOntologySeeAlso, "jhlOntologySeeAlso");		
		
		setWidgetName(jcbRDFExtractDCMI, "jcbRDFExtractDCMI");		
		setWidgetName(jcbRDFUploadImages, "jcbRDFUploadImages");		
		setWidgetName(jcbRDFGeneratePyramidalTiff, "jcbRDFGeneratePyramidalTiff");		
		setWidgetName(jhlRDFSeeAlso, "jhlRDFSeeAlso");		
		
		setWidgetName(jcbRDOExtractDCMI, "jcbRDOExtractDCMI");		
		setWidgetName(jhlRDOSeeAlso, "jhlRDOSeeAlso");		
		
		setWidgetName(jcbRTIExtractDCMI, "jcbRTIExtractDCMI");		
		setWidgetName(jcbRTIApplySemanticPolicy, "jcbRTIApplySemanticPolicy");				
		setWidgetName(jcbRTIExecuteCustomization, "jcbRTIExecuteCustomization");				
		setWidgetName(jcbRTITriggerPipelines, "jcbRTITriggerPipelines");				
		setWidgetName(jhlRTISeeAlso, "jhlRTISeeAlso");				

		setWidgetName(jcbSKOSExtractDCMI, "jcbSKOSExtractDCMI");		
		setWidgetName(jcbSKOSSkosify, "jcbSKOSSkosify");		
		setWidgetName(jhlSKOSSeeAlso, "jhlSKOSSeeAlso");				

		setWidgetName(jcbSpectralExtractDCMI, "jcbSpectralExtractDCMI");		
		setWidgetName(jcbSpectralApplySemanticPolicy, "jcbSpectralApplySemanticPolicy");		
		setWidgetName(jcbSpectralExecuteValidation, "jcbSpectralExecuteValidation");		
		setWidgetName(jhlSpectralSeeAlso, "jhlSpectralSeeAlso");					
		setWidgetName(jcbSpectralGeneratePyramidalTiff, "jcbSpectralGeneratePyramidalTiff");				
		
		setWidgetName(jcbSTORYExtractDCMI, "jcbSTORYExtractDCMI");		
		setWidgetName(jhlSTORYSeeAlso, "jhlSTORYSeeAlso");				

		setWidgetName(jcbTEIExtractDCMI, "jcbTEIExtractDCMI");		
		setWidgetName(jcbTEIApplySemanticPolicy, "jcbTEIApplySemanticPolicy");		
		setWidgetName(jcbTEIExecuteCustomization, "jcbTEIExecuteCustomization");		
		setWidgetName(jcbTEITriggerPipelines, "jcbTEITriggerPipelines");				
		setWidgetName(jcbTEIExecuteValidation, "jcbTEIExecuteValidation");		
		setWidgetName(jcbTEIResolveSKOS, "jcbTEIResolveSKOS");				
		setWidgetName(jcbTEIResolvePlacenames, "jcbTEIResolvePlacenames");				
		setWidgetName(jcbTEIAcceptIDSExclusively, "jcbTEIAcceptIDSExclusively");				
		setWidgetName(jcbTEIUploadImages, "jcbTEIUploadImages");				
		setWidgetName(jcbTEIGeneratePyramidalTiff, "jcbTEIGeneratePyramidalTiff");				
		setWidgetName(jcbTEICreateMETS, "jcbTEICreateMETS");				
		setWidgetName(jhlTEISeeAlso, "jhlTEISeeAlso");				

		setWidgetName(tp, "tp");
		setWidgetName(jbApply, "jbApply");
		setWidgetName(jbClose, "jbClose");
	}

	private void jbInit() throws Exception {
		
		try {
		res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
		CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

		container = new Container();
		container.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
				
		jtfFontSize =  new JSpinner();
		jcbShowByDoubleClick = new JCheckBox(res.getString("showbydoubleclick"));
		jcbTheCure = new JCheckBox(res.getString("thecure"));
		jcbTheCure.setToolTipText(res.getString("TTTheCure"));
		
		jcbCUBEExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbCUBEApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbCUBEExecuteCustomization = new JCheckBox(res.getString("executecust"));
		jcbCUBEExecuteValidation = new JCheckBox(res.getString("executeval"));
		jcbCUBETriggerPipelines = new JCheckBox(res.getString("triggerpipelines"));
		jcbCUBEResolveSKOS = new JCheckBox(res.getString("resolveskos"));
		jcbCUBEResolvePlacenames = new JCheckBox(res.getString("resolvegeonames"));
		jcbCUBEAcceptIDSExclusively = new JCheckBox(res.getString("acceptexclusively"));
		jhlCUBESeeAlso = new CHyperlink();
		jhlCUBESeeAlso.setText(res.getString("seealso"));
		
		jcbGMLExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbGMLApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbGMLExecuteCustomization = new JCheckBox(res.getString("executecust"));
		jcbGMLExecuteValidation = new JCheckBox(res.getString("executeval"));
		jcbGMLTriggerPipelines = new JCheckBox(res.getString("triggerpipelines"));
		jhlGMLSeeAlso = new CHyperlink();
		jhlGMLSeeAlso.setText(res.getString("seealso"));

		jcbLIDOExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbLIDOApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbLIDOExecuteCustomization = new JCheckBox(res.getString("executecust"));
		jcbLIDOExecuteValidation = new JCheckBox(res.getString("executeval"));
		jcbLIDOTriggerPipelines = new JCheckBox(res.getString("triggerpipelines"));
		jcbLIDOResolveSKOS = new JCheckBox(res.getString("resolveskos"));
		jcbLIDOResolvePlacenames = new JCheckBox(res.getString("resolvegeonames"));
		jcbLIDOAcceptIDSExclusively = new JCheckBox(res.getString("acceptexclusively"));
		jcbLIDOUploadImages = new JCheckBox(res.getString("uploadimages"));
		jcbLIDOGeneratePyramidalTiff = new JCheckBox(res.getString("pyramidaltiff"));
		jhlLIDOSeeAlso = new CHyperlink();
		jhlLIDOSeeAlso.setText(res.getString("seealso"));

		jcbMEIExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbMEIApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbMEIExecuteCustomization = new JCheckBox(res.getString("executecust"));
		jcbMEIExecuteValidation = new JCheckBox(res.getString("executeval"));
		jcbMEITriggerPipelines = new JCheckBox(res.getString("triggerpipelines"));
		jhlMEISeeAlso = new CHyperlink();
		jhlMEISeeAlso.setText(res.getString("seealso"));

		jcbMETSExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbMETSExecuteValidation = new JCheckBox(res.getString("executeval"));
		jhlMETSSeeAlso = new CHyperlink();
		jhlMETSSeeAlso.setText(res.getString("seealso"));

		jcbOntologyExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbOntologyUploadImages = new JCheckBox(res.getString("uploadimages"));
		jcbOntologyGeneratePyramidalTiff = new JCheckBox(res.getString("pyramidaltiff"));
		jhlOntologySeeAlso = new CHyperlink();
		jhlOntologySeeAlso.setText(res.getString("seealso"));

		jcbRDFExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbRDFUploadImages = new JCheckBox(res.getString("uploadimages"));
		jcbRDFGeneratePyramidalTiff = new JCheckBox(res.getString("pyramidaltiff"));
		jhlRDFSeeAlso = new CHyperlink();
		jhlRDFSeeAlso.setText(res.getString("seealso"));
		
		jcbRDOExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jhlRDOSeeAlso = new CHyperlink();
		jhlRDOSeeAlso.setText(res.getString("seealso"));
		
		jcbRTIExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbRTIApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbRTIExecuteCustomization = new JCheckBox(res.getString("executecust"));
		jcbRTITriggerPipelines = new JCheckBox(res.getString("triggerpipelines"));
		jhlRTISeeAlso = new CHyperlink();
		jhlRTISeeAlso.setText(res.getString("seealso"));

		jcbSKOSExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbSKOSSkosify = new JCheckBox(res.getString("skosify"));
		jhlSKOSSeeAlso = new CHyperlink();
		jhlSKOSSeeAlso.setText(res.getString("seealso"));

		jcbSpectralExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbSpectralApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbSpectralExecuteValidation = new JCheckBox(res.getString("executeval"));
		jhlSpectralSeeAlso = new CHyperlink();
		jhlSpectralSeeAlso.setText(res.getString("seealso"));
		jcbSpectralGeneratePyramidalTiff = new JCheckBox(res.getString("pyramidaltiff"));

		jcbSTORYExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jhlSTORYSeeAlso = new CHyperlink();
		jhlSTORYSeeAlso.setText(res.getString("seealso"));

		jcbTEIExtractDCMI = new JCheckBox(res.getString("extractdcmi"));
		jcbTEIApplySemanticPolicy = new JCheckBox(res.getString("applysempolicy"));
		jcbTEIExecuteCustomization = new JCheckBox(res.getString("executecust"));
		jcbTEIExecuteValidation = new JCheckBox(res.getString("executeval"));
		jcbTEITriggerPipelines = new JCheckBox(res.getString("triggerpipelines"));
		jcbTEIResolveSKOS = new JCheckBox(res.getString("resolveskos"));
		jcbTEIResolvePlacenames = new JCheckBox(res.getString("resolvegeonames"));
		jcbTEIAcceptIDSExclusively = new JCheckBox(res.getString("acceptexclusively"));
		jcbTEIUploadImages = new JCheckBox(res.getString("uploadimages"));
		jcbTEIGeneratePyramidalTiff = new JCheckBox(res.getString("pyramidaltiff"));
		jcbTEICreateMETS = new JCheckBox(res.getString("createmets"));
		jhlTEISeeAlso = new CHyperlink();
		jhlTEISeeAlso.setText(res.getString("seealso"));
	
		jbApply = new JButton(res.getString("apply"));
		jbClose = new JButton(res.getString("close"));
		
		tp = new JTabbedPane();
		
		Container c0 = new Container();
		c0.setLayout(new net.miginfocom.swing.MigLayout("","[grow][grow]",""));
		Box b0  = Box.createHorizontalBox();
		b0.add( new JLabel(res.getString("fontsize")+": ") );
		b0.add( jtfFontSize);
		c0.add( b0,  "wrap 5");
		c0.add( jcbShowByDoubleClick, "wrap 5");
		c0.add( jcbTheCure);
		
		Container c1 = new Container();
		c1.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c1.add(jcbCUBEExtractDCMI, "wrap 5");
		c1.add(jcbCUBEApplySemanticPolicy, "wrap 5");
		c1.add(jcbCUBEExecuteCustomization, "wrap 5");
		c1.add(jcbCUBEExecuteValidation, "wrap 5");
		c1.add(jcbCUBETriggerPipelines, "wrap 5");	
		c1.add(jcbCUBEResolveSKOS, "wrap 5");	
		c1.add(jcbCUBEResolvePlacenames, "wrap 5");	
		c1.add(jcbCUBEAcceptIDSExclusively, "gapbefore 18px, wrap 15");			
		c1.add(jhlCUBESeeAlso, "wrap 5");			
		
		Container c2 = new Container();
		c2.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c2.add(jcbGMLExtractDCMI, "wrap 5");
		c2.add(jcbGMLApplySemanticPolicy, "wrap 5");
		c2.add(jcbGMLExecuteCustomization, "wrap 5");
		c2.add(jcbGMLExecuteValidation, "wrap 5");
		c2.add(jcbGMLTriggerPipelines, "wrap 10");	
		c2.add(jhlGMLSeeAlso, "wrap 5");			
	
		Container c3 = new Container();
		c3.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c3.add(jcbLIDOExtractDCMI, "wrap 5");
		c3.add(jcbLIDOApplySemanticPolicy, "wrap 5");
		c3.add(jcbLIDOExecuteCustomization, "wrap 5");
		c3.add(jcbLIDOExecuteValidation, "wrap 5");
		c3.add(jcbLIDOTriggerPipelines, "wrap 5");	
		c3.add(jcbLIDOResolveSKOS, "wrap 5");	
		c3.add(jcbLIDOResolvePlacenames, "wrap 5");	
		c3.add(jcbLIDOAcceptIDSExclusively, "gapbefore 18px, wrap 5");			
		c3.add(jcbLIDOUploadImages, "wrap 5");	
		c3.add(jcbLIDOGeneratePyramidalTiff,  "gapbefore 18px,wrap 5");	
		c3.add(jhlLIDOSeeAlso, "wrap 5");			

		Container c4 = new Container();
		c4.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c4.add(jcbMEIExtractDCMI, "wrap 5");
		c4.add(jcbMEIApplySemanticPolicy, "wrap 5");
		c4.add(jcbMEIExecuteCustomization, "wrap 5");
		c4.add(jcbMEIExecuteValidation, "wrap 5");
		c4.add(jcbMEITriggerPipelines, "wrap 10");	
		c4.add(jhlMEISeeAlso, "wrap 5");			

		Container c5 = new Container();
		c5.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c5.add(jcbMETSExtractDCMI, "wrap 5");
		c5.add(jcbMETSExecuteValidation, "wrap 5");
		c5.add(jhlMETSSeeAlso, "wrap 5");			

		Container c6 = new Container();
		c6.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c6.add(jcbOntologyExtractDCMI, "wrap 5");
		c6.add(jcbOntologyUploadImages, "wrap 5");
		c6.add(jcbOntologyGeneratePyramidalTiff,  "gapbefore 18px,wrap 5");
		c6.add(jhlOntologySeeAlso, "wrap 5");			

		Container c7 = new Container();
		c7.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c7.add(jcbRDFExtractDCMI, "wrap 5");
		c7.add(jcbRDFUploadImages, "wrap 5");
		c7.add(jcbRDFGeneratePyramidalTiff, "gapbefore 18px,wrap 5");
		c7.add(jhlRDFSeeAlso, "wrap 5");			


		Container c8 = new Container();
		c8.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c8.add(jcbRDOExtractDCMI, "wrap 5");
		c8.add(jhlRDOSeeAlso, "wrap 5");			

		Container c9 = new Container();
		c9.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c9.add(jcbRTIExtractDCMI, "wrap 5");
		c9.add(jcbRTIApplySemanticPolicy, "wrap 5");
		c9.add(jcbRTIExecuteCustomization, "wrap 5");
		c9.add(jcbRTITriggerPipelines, "wrap 5");	
		c9.add(jhlRTISeeAlso, "wrap 5");			

		Container c10 = new Container();
		c10.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c10.add(jcbSKOSExtractDCMI, "wrap 5");
		c10.add(jcbSKOSSkosify, "wrap 10");
		c10.add(jhlSKOSSeeAlso, "wrap 5");			

		Container c11 = new Container();
		c11.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c11.add(jcbSpectralExtractDCMI, "wrap 5");
		c11.add(jcbSpectralApplySemanticPolicy, "wrap 5");
		c11.add(jcbSpectralExecuteValidation, "wrap 5");
		c11.add(jcbSpectralGeneratePyramidalTiff, "wrap 10");	
		c11.add(jhlSpectralSeeAlso, "wrap 5");			

		Container c12 = new Container();
		c12.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c12.add(jcbSTORYExtractDCMI, "wrap 5");
		c12.add(jhlSTORYSeeAlso, "wrap 5");			

		Container c13 = new Container();
		c13.setLayout(new net.miginfocom.swing.MigLayout("","[grow]",""));
		c13.add(jcbTEIExtractDCMI, "wrap 5");
		c13.add(jcbTEIApplySemanticPolicy, "wrap 5");
		c13.add(jcbTEIExecuteCustomization, "wrap 5");
		c13.add(jcbTEIExecuteValidation, "wrap 5");
		c13.add(jcbTEITriggerPipelines, "wrap 5");	
		c13.add(jcbTEIResolveSKOS, "wrap 5");	
		c13.add(jcbTEIResolvePlacenames, "wrap 5");	
		c13.add(jcbTEIAcceptIDSExclusively, "gapbefore 18px,wrap 5");			
		c13.add(jcbTEIUploadImages, "wrap 5");	
		c13.add(jcbTEIGeneratePyramidalTiff,  "gapbefore 18px,wrap 5");	
		c13.add(jcbTEICreateMETS, "wrap 10");	
		c13.add(jhlTEISeeAlso, "wrap 5");			

		tp.addTab(res.getString("general"),     c0);
		tp.addTab(res.getString("cubeobjects"), c1);
		tp.addTab(res.getString("gmlobjects"),  c2);
		tp.addTab(res.getString("lidoobjects"), c3);
		tp.addTab(res.getString("meiobjects"),  c4);
		tp.addTab(res.getString("metsobjects"), c5);
		tp.addTab(res.getString("ontologyobjects"), c6);
		tp.addTab(res.getString("rdfobjects"), c7);
		tp.addTab(res.getString("rdoobjects"), c8);
		tp.addTab(res.getString("rtiobjects"), c9);
		tp.addTab(res.getString("skosobjects"), c10);
		tp.addTab(res.getString("spectralobjects"), c11);
		tp.addTab(res.getString("storyobjects"), c12);
		tp.addTab(res.getString("teiobjects"),  c13);
			
		container.add(tp, "grow, wrap 5");
		Box bb  = Box.createHorizontalBox();
		bb.add( jbApply );
		bb.add( new JLabel (" "));
		bb.add( jbClose );
		container.add( bb, "gapleft push, wrap 10" );
		
		} catch (Exception e ) { e.printStackTrace();}
	}

}
