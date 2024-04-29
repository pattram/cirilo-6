package org.emile.client.business;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;

import org.emile.cirilo.Namespaces;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.json.JSONObject;
import org.apache.log4j.Logger;

public class EDM {

	private static Logger log = Logger.getLogger(EDM.class);

	private FedoraConnector connector;
	private Document edm;
	private Format format;
	private XMLOutputter outputter;
	private Document persons;
	private Document places;

	public EDM(FedoraConnector connector) {		
		try {

			this.connector = connector;
			
			format = Format.getRawFormat();
			format.setEncoding("UTF-8");
			outputter = new XMLOutputter(format);
			
      	    try { persons = XMLUtils.createDocumentFromURL(connector.getHostname()+"/cirilo:settings/PERSONS"); } catch (Exception q) {persons = null;}
      	    try { places = XMLUtils.createDocumentFromURL(connector.getHostname()+"/cirilo:settings/PLACES"); } catch (Exception q) {places = null;}
  
			// WebService.setUserName(account);

		} catch (Exception e) {
		  	log.error(e);		  			
		}
	}

	public void set(Document edm) {		
		normalize(edm);
	}

    
 	public byte[] get() {		
       return this.outputter.outputString(this.edm).getBytes();
	}
	
    public void save() {  
    	try {
       		if (persons != null)  connector.stubModifyDatastream("cirilo:settings", "PERSONS", outputter.outputString(persons).getBytes(), "text/xml", null);	
       		if (places != null)  connector.stubModifyDatastream("cirilo:settings", "PLACES", outputter.outputString(places).getBytes(), "text/xml", null);	
    	} catch (Exception e) {}	
    }	

	private void normalize(Document edm) {
		this.edm = edm;
 		normalizePersons();
 		normalizePlaces();	
	}
	
	private void normalizePersons() {
		
		String stream = null;
		
		if (persons == null) return;
		/*
		try {
			XPath xpath = XPath.newInstance("//edm:Agent[contains(@rdf:about,'/gnd/')]");
			xpath.addNamespace(Namespaces.xmlns_edm);
			xpath.addNamespace(Namespaces.xmlns_skos);
			xpath.addNamespace(Namespaces.xmlns_rdf);
			xpath.addNamespace(Namespaces.xmlns_rdaGr2);
			
			List nodes = (List) xpath.selectNodes( this.edm );

			if (nodes.size() > 0) {
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					try {
						Element e = (Element) iter.next();
						String preferredName = null;
						String id = e.getAttributeValue("about",Namespaces.xmlns_rdf);
						XPath qpath = XPath.newInstance("//person[@xml:id='"+id+"']");
						qpath.addNamespace(Namespaces.xmlns_xml);
	    				Element person = (Element) qpath.selectSingleNode( persons );
                        if (person == null) {
        		    		char[] buff = new char[1024];
                        	int n;
        		    		StringWriter sw = new StringWriter();
        		    		
        		    		URL url = new URL("http://hub.culturegraph.org/entityfacts/"+id.substring(id.indexOf("gnd/")+4));
        		    		try {
        		    			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        		    			try {
        		    				while ((n = br.read(buff)) != -1) { sw.write(buff, 0, n); }
        		    				stream = sw.toString();
        		    			} catch (Exception io) {}
        		    			finally {
        		    				sw.close();
        		    				br.close();
        		    			}
                                JSONObject json = new JSONObject(stream);
                                preferredName = json.getString("preferredName");
                                                         
        		    		} catch (Exception p) {}        		    		
                        	person = new Element ("person");                            
                        	person.setAttribute("id", id, Namespaces.xmlns_xml);
                        	Element name = new Element ("name");
                        	name.setAttribute("lang","de",Namespaces.xmlns_xml);
                        	name.setText(preferredName);
                        	person.addContent(name);
                        	persons.getRootElement().addContent(person);                        	
                        } else {
                        	preferredName = person.getChildText("name");	
                        }
                        e.getChild("prefLabel", Namespaces.xmlns_skos).setText(preferredName);
                        
					} catch (Exception q) {log.debug(q.getLocalizedMessage(),q);}
				}
			}	
			
		} catch (Exception e) {}	
		*/
	}
    
	private void normalizePlaces() {

		if (places == null) return;
		
		try {
		/*
			XPath xpath = XPath.newInstance("//edm:Place[contains(@rdf:about,'geonames.org')]");
			xpath.addNamespace(Namespaces.xmlns_edm);
			xpath.addNamespace(Namespaces.xmlns_skos);
			xpath.addNamespace(Namespaces.xmlns_rdf);
			xpath.addNamespace(Namespaces.xmlns_wgs84_pos);

			List nodes = (List) xpath.selectNodes( this.edm );

			if (nodes.size() > 0) {
				for (Iterator iter = nodes.iterator(); iter.hasNext();) {
					try {
						Element e = (Element) iter.next();
						String id = e.getAttributeValue("about",Namespaces.xmlns_rdf).replaceAll("www\\.","") ;
						log.debug("Resolving "+id+" against geonames.org"); 
						XPath qpath = XPath.newInstance("//place[@xml:id='"+id+"']");
						qpath.addNamespace(Namespaces.xmlns_xml);
						Element place = (Element) qpath.selectSingleNode( places );
						if (place == null) {
                        	place = new Element ("place");
                        	place.setAttribute("id", id, Namespaces.xmlns_xml);
                        	Element name = new Element ("name");
        					Document toponym  = builder.build(id+"/about.rdf");
    						XPath tpath = XPath.newInstance("//gn:*[@xml:lang='de']");
    						tpath.addNamespace(Namespaces.xmlns_gn);
    						Element alt = (Element) tpath.selectSingleNode( toponym );
    						if (alt == null) {
    							tpath = XPath.newInstance("//gn:name");
        						tpath.addNamespace(Namespaces.xmlns_gn);
        						alt = (Element) tpath.selectSingleNode( toponym );
    						} else {
    							name.setAttribute("lang","de",Namespaces.xmlns_xml);
    						}
    						if (alt != null) {
    							name.setText(alt.getTextNormalize());    							
    						} else {
    							name.setText(e.getChildText("prefLabel",Namespaces.xmlns_skos));
    						}    						
                        	place.addContent(name);
                        	places.getRootElement().addContent(place);
						} 
                    	e.getChild("prefLabel", Namespaces.xmlns_skos).setText(place.getChild("name").getText());
                    	
    					Toponym toponym = WebService.get(new Integer(id.substring(id.indexOf("org/") + 4)).intValue(), null, null);
    					e.getChild("lat",Namespaces.xmlns_wgs84_pos).setText(new Double(toponym.getLatitude()).toString());
    					e.getChild("long",Namespaces.xmlns_wgs84_pos).setText(new Double(toponym.getLongitude()).toString());
    					log.debug("lat: "+new Double(toponym.getLatitude()).toString()+" long: "+new Double(toponym.getLongitude()).toString());
 
                    	
					} catch (Exception q) {
						log.error(q.getMessage());
					}
				}
			}	
   */
		
		} catch (Exception e) {}	
		
	 }

 }
