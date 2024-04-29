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

package org.emile.client;

import org.emile.client.dialog.*;

import voodoosoft.jroots.application.*;
import voodoosoft.jroots.core.*;
import voodoosoft.jroots.dialog.*;
import voodoosoft.jroots.gui.*;

import org.emile.client.ServiceNames;
import org.emile.client.dialog.DialogNames;

import java.text.DateFormat;

import javax.swing.*;

import java.util.*;


public class Setup {
		
	private Setup() { }

	public static void AccessManager() throws Exception {

		CDefaultAccessManager loAccMan;
	
		loAccMan = new CDefaultAccessManager(null);
		CServiceProvider.addService(loAccMan, ServiceNames.ACCESS_MANAGER);
	}

	public static void BusinessObjects() throws Exception {}

	public static void Dialogs(CGuiManager aoGuiMan) throws Exception {
		
		CDialogManager loDialogManager;
		CDialogCreator loModalDialogCreator;
		IDialogCreator loCreator;
		CiriloFrame loFrame;

		LoginDialog loLoginDialog;
		ReorganizerDialog loReorganizerDialog;
		ObjectEditorDialog loObjectEditorDialog;
		CreateObjectDialog loCreateObjectDialog;
		IngestObjectDialog loIngestObjectDialog;
		SearchDialog loSearchDialog;
		UserEnvironmentDialog loUserEnvironmentDialog;
		ReplaceDialog loReplaceDialog;
		XSLDialog loXSLDialog;
		LogDialog loLogDialog;
		HarvesterDialog loHarvesterDialog;
		PreferencesDialog loPreferencesDialog;
		AggregateDialog loAggregateDialog;
		PublishDialog loPublishDialog;
		PublishGroupDialog loPublishGroupDialog;
		ReviewDialog loReviewDialog;
		
		RepositoryDialog loRepositoryDialog_0;
		RepositoryDialog loRepositoryDialog_1;
		RepositoryDialog loRepositoryDialog_2;
		RepositoryDialog loRepositoryDialog_3;
		RepositoryDialog loRepositoryDialog_4;
		RepositoryDialog loRepositoryDialog_5;
		
		// dialog date format
		CDefaultGuiAdapter.setDateFormat(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH));
		CFormatTransformer.registerTransformer("DefaultDialogDate", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH));

		// dialog manager
		loDialogManager = new CDialogManager();
		CServiceProvider.addService(loDialogManager, ServiceNames.DIALOG_MANAGER);
		
		// parent for all dialogs
		loFrame = (CiriloFrame) CServiceProvider.getService(ServiceNames.MAIN_WINDOW);

		// creator for non modal dialogs
		loModalDialogCreator = new CDialogCreator(loFrame, true);

		// creator for internal frames
		loCreator = new CInternalFrameCreator(loFrame.getDesktopPane());

		// LoginDialog
		loLoginDialog = (LoginDialog) loModalDialogCreator.createDialog(LoginDialog.class, "LoginDialog", Common.MAIN_WINDOW_HEADER, DialogNames.LOGIN_DIALOG);
		loLoginDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loLoginDialog, DialogNames.LOGIN_DIALOG);

		// ReorganizeDialog
		loReorganizerDialog = (ReorganizerDialog) loModalDialogCreator.createDialog(ReorganizerDialog.class, "ReorganizerDialog", Common.MAIN_WINDOW_HEADER, DialogNames.REORGANIZE_DIALOG);
		loReorganizerDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loReorganizerDialog, DialogNames.REORGANIZE_DIALOG);

		// ObjectDialog
		loObjectEditorDialog = (ObjectEditorDialog) loModalDialogCreator.createDialog(ObjectEditorDialog.class, "ObjectEditorDialog", "", DialogNames.OBJECTEDITOR_DIALOG);
		loObjectEditorDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loObjectEditorDialog, DialogNames.OBJECTEDITOR_DIALOG);
		
		// CreateObjectDialog
		loCreateObjectDialog = (CreateObjectDialog) loModalDialogCreator.createDialog(CreateObjectDialog.class, "CreateObjectDialog", "", DialogNames.CREATEOBJECT_DIALOG);
		loCreateObjectDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loCreateObjectDialog, DialogNames.CREATEOBJECT_DIALOG);

		// IngestObjectDialog
		loIngestObjectDialog = (IngestObjectDialog) loModalDialogCreator.createDialog(IngestObjectDialog.class, "IngestObjectDialog", "", DialogNames.INGESTOBJECT_DIALOG);
		loIngestObjectDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loIngestObjectDialog, DialogNames.INGESTOBJECT_DIALOG);
		
		// HarvesterDialog
		loHarvesterDialog = (HarvesterDialog) loModalDialogCreator.createDialog(HarvesterDialog.class, "HarvesterDialog", "", DialogNames.HARVESTER_DIALOG);
		loHarvesterDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loHarvesterDialog, DialogNames.HARVESTER_DIALOG);

		// SearchDialog
		loSearchDialog = (SearchDialog) loModalDialogCreator.createDialog(SearchDialog.class, "SearchDialog", "", DialogNames.SEARCH_DIALOG);
		loSearchDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loSearchDialog, DialogNames.SEARCH_DIALOG);

		// ReplaceDialog
		loReplaceDialog = (ReplaceDialog) loModalDialogCreator.createDialog(ReplaceDialog.class, "ReplaceDialog", "", DialogNames.REPLACE_DIALOG);
		loReplaceDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loReplaceDialog, DialogNames.REPLACE_DIALOG);
		
		// UserEnvironmentDialog
		loUserEnvironmentDialog = (UserEnvironmentDialog) loModalDialogCreator.createDialog(UserEnvironmentDialog.class, "UserEnvironmentDialog", "", DialogNames.USERENVIRONMENT_DIALOG);
		loUserEnvironmentDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loUserEnvironmentDialog, DialogNames.USERENVIRONMENT_DIALOG);
		
		// PublishDialog
		loPublishDialog = (PublishDialog) loModalDialogCreator.createDialog(PublishDialog.class, "PublishDialog", "", DialogNames.PUBLISH_DIALOG);
		loPublishDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loPublishDialog, DialogNames.PUBLISH_DIALOG);
		
		// PublishGroupDialog
		loPublishGroupDialog = (PublishGroupDialog) loModalDialogCreator.createDialog(PublishGroupDialog.class, "PublishGroupDialog", "", DialogNames.PUBLISHGROUP_DIALOG);
		loPublishGroupDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loPublishGroupDialog, DialogNames.PUBLISHGROUP_DIALOG);
		
		// XSLDialog
		loXSLDialog = (XSLDialog) loModalDialogCreator.createDialog(XSLDialog.class, "XSLDialog", "", DialogNames.XSL_DIALOG);
		loXSLDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loXSLDialog, DialogNames.XSL_DIALOG);
		
		// LogDialog
		loLogDialog = (LogDialog) loModalDialogCreator.createDialog(LogDialog.class, "LogDialog", "", DialogNames.LOG_DIALOG);
		loLogDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loLogDialog, DialogNames.LOG_DIALOG);

		// ReviewDialog
		loReviewDialog = (ReviewDialog) loModalDialogCreator.createDialog(ReviewDialog.class, "ReviewDialog", "", DialogNames.REVIEW_DIALOG);
		loReviewDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loReviewDialog, DialogNames.REVIEW_DIALOG);

		// PreferencesDialog
		loPreferencesDialog = (PreferencesDialog) loModalDialogCreator.createDialog(PreferencesDialog.class, "PreferencesDialog", "", DialogNames.PREFERENCES_DIALOG);
		loPreferencesDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loPreferencesDialog, DialogNames.PREFERENCES_DIALOG);
		
		// AggregateDialog
		loAggregateDialog = (AggregateDialog) loModalDialogCreator.createDialog(AggregateDialog.class, "AggregateDialog", "", DialogNames.AGGREGATE_DIALOG);
		loAggregateDialog.setGuiManager(aoGuiMan);
		CServiceProvider.addService(loAggregateDialog, DialogNames.AGGREGATE_DIALOG);
		
		// RepositoryDialog
		loRepositoryDialog_0 = (RepositoryDialog) loCreator.createDialog(RepositoryDialog.class, "RepositoryDialog_0", "", DialogNames.REPOSITORY_DIALOG[0]);
		loRepositoryDialog_0.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loRepositoryDialog_0, true);
		CServiceProvider.addService(loRepositoryDialog_0, DialogNames.REPOSITORY_DIALOG[0]);
		loRepositoryDialog_1 = (RepositoryDialog) loCreator.createDialog(RepositoryDialog.class, "RepositoryDialog_1", "", DialogNames.REPOSITORY_DIALOG[1]);
		loRepositoryDialog_1.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loRepositoryDialog_1, true);
		CServiceProvider.addService(loRepositoryDialog_1, DialogNames.REPOSITORY_DIALOG[1]);
		loRepositoryDialog_2 = (RepositoryDialog) loCreator.createDialog(RepositoryDialog.class, "RepositoryDialog_2", "", DialogNames.REPOSITORY_DIALOG[2]);
		loRepositoryDialog_2.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loRepositoryDialog_2, true);
		CServiceProvider.addService(loRepositoryDialog_2, DialogNames.REPOSITORY_DIALOG[2]);
		loRepositoryDialog_3 = (RepositoryDialog) loCreator.createDialog(RepositoryDialog.class, "RepositoryDialog_3", "", DialogNames.REPOSITORY_DIALOG[3]);
		loRepositoryDialog_3.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loRepositoryDialog_3, true);
		CServiceProvider.addService(loRepositoryDialog_3, DialogNames.REPOSITORY_DIALOG[3]);
		loRepositoryDialog_4 = (RepositoryDialog) loCreator.createDialog(RepositoryDialog.class, "RepositoryDialog_4", "", DialogNames.REPOSITORY_DIALOG[4]);
		loRepositoryDialog_4.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loRepositoryDialog_4, true);
		CServiceProvider.addService(loRepositoryDialog_3, DialogNames.REPOSITORY_DIALOG[4]);
		loRepositoryDialog_5 = (RepositoryDialog) loCreator.createDialog(RepositoryDialog.class, "RepositoryDialog_5", "", DialogNames.REPOSITORY_DIALOG[5]);
		loRepositoryDialog_5.setGuiManager(aoGuiMan);
		loDialogManager.registerPrototype(loRepositoryDialog_5, true);
		CServiceProvider.addService(loRepositoryDialog_5, DialogNames.REPOSITORY_DIALOG[5]);
		
	}

	public static CGuiManager GUI()
		throws Exception {

		CGuiFactory loFactory;
		JFrame loFrame;
		CGuiManager loGuiMan;

		loGuiMan = new CGuiManager();

		// Create GUI factory
		loFactory = new CGuiFactory("javax.swing");
		loFactory.addDefaultBindings();

		// Build dialog gGUI
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiLoginDialog", "LoginDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiRepositoryDialog", "RepositoryDialog_0");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiRepositoryDialog", "RepositoryDialog_1");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiRepositoryDialog", "RepositoryDialog_2");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiRepositoryDialog", "RepositoryDialog_3");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiRepositoryDialog", "RepositoryDialog_4");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiRepositoryDialog", "RepositoryDialog_5");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiObjectEditorDialog", "ObjectEditorDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiCreateObjectDialog", "CreateObjectDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiIngestObjectDialog", "IngestObjectDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiSearchDialog", "SearchDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiReplaceDialog", "ReplaceDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiUserEnvironmentDialog", "UserEnvironmentDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiXSLDialog", "XSLDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiLogDialog", "LogDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiPublishDialog", "PublishDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiPublishGroupDialog", "PublishGroupDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiPreferencesDialog", "PreferencesDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiAggregateDialog", "AggregateDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiReviewDialog", "ReviewDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiReorganizerDialog", "ReorganizerDialog");
		loGuiMan.addGuiComposite("org.emile.client.gui.GuiHarvesterDialog", "HarvesterDialog");
	
		
		// Build menu GUI
		loGuiMan.addGuiComposite(loFactory.createGuiFromXML(Cirilo.class.getResourceAsStream("MenuBar.xml"), true), "FrameMenu");
		loGuiMan.addWidgetTree("FrameMenu", true, true);

		// Main window
		loFrame = new CiriloFrame(Common.MAIN_WINDOW_HEADER, loGuiMan);
		CServiceProvider.addService(loFrame, ServiceNames.MAIN_WINDOW);

		return loGuiMan;
	}
	
}

