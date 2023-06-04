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

package org.emile.client.dialog;

import voodoosoft.jroots.core.CServiceName;

public class DialogNames {

	public final static CServiceName LOGIN_DIALOG = new CServiceName("LoginDialog");
	
	public final static CServiceName[] REPOSITORY_DIALOG = {
			new CServiceName("RepositoryDialog_0"), 
			new CServiceName("RepositoryDialog_1"),
			new CServiceName("RepositoryDialog_2"),
			new CServiceName("RepositoryDialog_3"), 
			new CServiceName("RepositoryDialog_4"), 
			new CServiceName("RepositoryDialog_5"), 
	};
	
	public final static CServiceName OBJECTEDITOR_DIALOG = new CServiceName("ObjectEditorDialog");
	public final static CServiceName CREATEOBJECT_DIALOG = new CServiceName("CreateObjectDialog");
	public final static CServiceName INGESTOBJECT_DIALOG = new CServiceName("IngestObjectDialog");
	public final static CServiceName USERENVIRONMENT_DIALOG = new CServiceName("UserEnvironmentDialog");
	public final static CServiceName SEARCH_DIALOG = new CServiceName("SearchDialog");
	public final static CServiceName REPLACE_DIALOG = new CServiceName("ReplaceDialog");
	public final static CServiceName PUBLISH_DIALOG = new CServiceName("PublishDialog");
	public final static CServiceName PUBLISHGROUP_DIALOG = new CServiceName("PublishGroupDialog");
	public final static CServiceName PREFERENCES_DIALOG = new CServiceName("PreferencesDialog");
	public final static CServiceName LOG_DIALOG = new CServiceName("LogDialog");
	public final static CServiceName AGGREGATE_DIALOG = new CServiceName("AggregateDialog");
	public final static CServiceName XSL_DIALOG = new CServiceName("XSLDialog");
	public final static CServiceName REVIEW_DIALOG = new CServiceName("ReviewDialog");
	public final static CServiceName REORGANIZE_DIALOG = new CServiceName("ReorganizeDialog");
	public final static CServiceName HARVESTER_DIALOG = new CServiceName("HarvesterDialog");

}

