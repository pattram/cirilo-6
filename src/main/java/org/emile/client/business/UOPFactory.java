package org.emile.client.business;

import org.apache.commons.lang3.StringUtils;
import org.emile.client.ServiceNames;
import org.emile.cm4f.models.UploadOptions;

import voodoosoft.jroots.core.CServiceName;
import voodoosoft.jroots.core.CServiceProvider;

public class UOPFactory {
	
	
	private CServiceName service_name;
	private UploadOptions UOP = null;
	private String extension;	

	public UOPFactory(String model) {
				
		switch (model) {
		case "CUBE":
			service_name = ServiceNames.CUBE_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "GML":
			service_name = ServiceNames.GML_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "LIDO":
			service_name = ServiceNames.LIDO_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "MEI":
			service_name = ServiceNames.MEI_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "METS":
			service_name = ServiceNames.METS_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "Ontology":
			service_name = ServiceNames.ONTOLOGY_UPLOADOPTIONS;
			this.extension = "*.rdf;*.xml;*.ttl";
			break;
		case "RDF":
			service_name = ServiceNames.RDF_UPLOADOPTIONS;
			this.extension = "*.rdf;*.xml;*.ttl";
			break;
		case "RDO":
			service_name = ServiceNames.RDO_UPLOADOPTIONS;
			this.extension = "*.rdf;*.xml";
			break;
		case "RTI":
			service_name = ServiceNames.RTI_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "SKOS":
			service_name = ServiceNames.SKOS_UPLOADOPTIONS;
			this.extension = "*.rdf;*.xml;*.ttl";
			break;
		case "Spectral":
			service_name = ServiceNames.SPECTRAL_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		case "TEI":
			service_name = ServiceNames.TEI_UPLOADOPTIONS;
			this.extension = "*.xml";
			break;
		}
		try {
			UOP = (UploadOptions) CServiceProvider.getService(service_name);		
		} catch (Exception e) {}
				
	}
	
	public UploadOptions getUOP() {
		return this.UOP;
	}

	public String getExtension() {
		return this.extension;
	}
}
