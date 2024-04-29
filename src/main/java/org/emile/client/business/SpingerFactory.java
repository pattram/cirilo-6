package org.emile.client.business;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.emile.cirilo.exceptions.FedoraConnectionException;
import org.emile.cirilo.exceptions.KeycloakServerNotFoundException;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cm4f.models.UploadOptions;

public class SpingerFactory {
	
	private static Logger log = Logger.getLogger(SpingerFactory.class);


	private FedoraConnector connector = new FedoraConnector();
	private String group;
	private String prototype;
	private String context;
	private boolean verbose;
	
	public SpingerFactory (boolean verbose) {
		this.verbose = verbose;
	}
	
	public boolean connect (String host, String user, String passwd, String group, String prototype, String context) { 
	
		try {
			
			try {		
				FedoraConnector.stubGetAuthorizationServer("https", host);
				this.group = group;
				this.prototype = prototype;
				this.context = context;
			} catch (KeycloakServerNotFoundException e) {
				return false;
			} catch (FedoraConnectionException e) {
				return false;
			}
			
			connector.stubOpenConnection("https", host, user, passwd); 
			
		} catch (Exception e) {		
			return false;
		}
		
		return true;
		
	}
	
	public void ingest(String path) {

		UploadOptions uop = new UOPFactory("Spectral").getUOP();
		
	    String retval= new IngestFactory(null, this.connector, null, uop.get()).run(null, null, group, path, null, getPrototype(prototype, "o:prototype.spectral"), getPrototype(context, "o:prototype.context"), true, verbose);
	   
	    String created = StringUtils.substringBetween(retval, ":", ":");
		String updated = StringUtils.substringAfterLast(retval, ":");
		
		log.info("Summary: "+created + " objects were created and "+updated+" updated");

	}	

	private String getPrototype(String prototype, String defaults) {
		try {
			if (prototype != null && connector.stubExist(prototype, null) == 200) return prototype;
		} catch (Exception e) {}
		return defaults;
	}
}
