package org.emile.client.dialog.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.emile.cirilo.utils.XMLUtils;
import org.jdom.Document;
import org.jdom.Element;

public class CConfigurationStore {
	
	private HashMap<String,ArrayList<String>> Pipelines = new HashMap<String,ArrayList<String>>();
	private HashMap<String,String> Parameters = new HashMap<String,String>();
	private Document default_pipelines;
	
	
	public CConfigurationStore(byte[] pipelineDescription, byte[] configurationDescription, byte[] default_pipelines) {
		
		Document pipeline_description = XMLUtils.createDocumentFromByteArray(pipelineDescription);
		
		List<Element> pipelines = XMLUtils.getChildren("/PipelineDescription/Pipelines", pipeline_description);
	
		for (int i= 0; i < pipelines.size(); i++) {
			
			Element pipeline = pipelines.get(i);
			
			ArrayList<String> pipes = new ArrayList<String>();
			
			pipes.add(pipeline.getChild("Ingest") != null ?  pipeline.getChildTextNormalize("Ingest").replaceAll(" ", "") : null);
			pipes.add(pipeline.getChild("Dissemination") != null ?  pipeline.getChildTextNormalize("Dissemination").replaceAll(" ", "")  : null);
			pipes.add(pipeline.getChild("Metadata") != null ?  pipeline.getChildTextNormalize("Metadata").replaceAll(" ", "")  : null);
			
			Pipelines.put(pipeline.getAttributeValue("model"), pipes);	
				
		}
		
		Document configuration_description = XMLUtils.createDocumentFromByteArray(configurationDescription);
		
		Parameters.put("TextMimetypes", configuration_description.getRootElement().getChildTextNormalize("TextMimetypes"));
		Parameters.put("ImageMimetypes", configuration_description.getRootElement().getChildTextNormalize("ImageMimetypes"));
		Parameters.put("VersionableDatastreams", configuration_description.getRootElement().getChildTextNormalize("VersionableDatastreams"));
		Parameters.put("TransformableDatastreams", configuration_description.getRootElement().getChildTextNormalize("TransformableDatastreams"));
		Parameters.put("IngestibleModels", configuration_description.getRootElement().getChildTextNormalize("IngestibleModels"));
		Parameters.put("SpreadsheetableModels", configuration_description.getRootElement().getChildTextNormalize("SpreadsheetableModels"));
		Parameters.put("SupportedMimetypes", configuration_description.getRootElement().getChildTextNormalize("SupportedMimetypes"));
		
		this.default_pipelines = XMLUtils.createDocumentFromByteArray(default_pipelines);
		
	}
	
	public ArrayList<String> getIngestPipelines(String model) {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> pipes = Pipelines.get(model);
		if (pipes.get(0) != null) list = new ArrayList<String>(Arrays.asList(pipes.get(0).split("[;,]")));
		return list;
	}

	public ArrayList<String> getDisseminationPipelines(String model) {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> pipes = Pipelines.get(model);
		if (pipes.get(1) != null) list = new ArrayList<String>(Arrays.asList(pipes.get(1).split("[;,]")));
		return list;
	}	
	
	public ArrayList<String> getMetadataPipelines(String model) {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> pipes = Pipelines.get(model);
		if (pipes.get(2) != null) list = new ArrayList<String>(Arrays.asList(pipes.get(2).split("[,;]")));
		return list;
	}
	
	public ArrayList<String> getAll(String model) {
		ArrayList<String> pipes = Pipelines.get(model);
		
		String all = (pipes.get(0) != null ? pipes.get(0) + ";" : "") +
				     (pipes.get(1) != null ? pipes.get(1) + ";" : "") + 
				     (pipes.get(2) != null ? pipes.get(2) + ";" : "");
		ArrayList<String> list  = new ArrayList<String>(Arrays.asList(all.split("[;,]")));
		Collections.sort(list);
		return list;
	}	
	
	public ArrayList<String> getParameters(String name) {
		ArrayList<String> list  = new ArrayList<String>(Arrays.asList(Parameters.get(name).split("[;,]")));
		Collections.sort(list);
		return list;
	}
	
	public Document getDefaultPipelines() {
		return default_pipelines;
	}

}
