package org.emile.client.utils;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringUtils;
import org.emile.cirilo.Namespaces;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;
import voodoosoft.jroots.dialog.CDialog;

public class Utils {
	
	private static XMLOutputter outputter = new XMLOutputter();
	
	public static void setRowSelection(JTable jtData) {
		try {
			CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);
			TableModel dm = jtData.getModel();

			if (dm.getRowCount() > 0 && !((String)dm.getValueAt(0, 1)).isEmpty()) {
				jtData.setRowSelectionInterval(0, 0);
			}
			
			jtData.setShowHorizontalLines(false);
			jtData.setRowHeight((int)(new Integer(props.getProperty("user", "FontSize"))*1.2));
			
		} catch (Exception e) {}
	}
	
	public static void hideColumnUUID(JTable jtData) {
		
		TableColumn column=jtData.getColumnModel().getColumn(0);
		column.setMinWidth(0);
		column.setMaxWidth(0);
		column.setWidth(0);
		column.setPreferredWidth(0);
		column.setResizable(false);
	}

    public static JButton createOpenButton(Dimension dim) {
    	
		JButton button = new JButton(dim.getWidth()< 36 ? Common.OPEN_BUTTON_SMALL : Common.OPEN_BUTTON_MEDIUM);	
		button.setPreferredSize(dim);
		button.setMinimumSize(dim);
		button.setMaximumSize(dim);
		
		return button;
		
    }
	
	public static byte[] getDcmiXML(Map<String,String> dcmi) {
		
		byte[] oai_xml = null;
		
		try {
			
			 Element oai_dc = new Element("dc", Namespaces.xmlns_oai_dc);
			 oai_dc.addNamespaceDeclaration(Namespaces.xmlns_dc);
			 oai_dc.addNamespaceDeclaration(Namespaces.xmlns_dcterms);
			 oai_dc.addNamespaceDeclaration(Namespaces.xmlns_dcmitype);
			 
			 Document xml = new Document(oai_dc);
			 
			 for (String key: dcmi.keySet()) {
				String value = dcmi.get(key);
				if (value != null) {
					Element field = new Element(key.toLowerCase(), Namespaces.xmlns_dc);
					field.setText(value);
					oai_dc.addContent(field);
				}
			 }
			 oai_xml = outputter.outputString(xml).getBytes();
			 
		} catch (Exception e) {}

		return oai_xml;
		
	}
	
	public static byte[] getDcmi(String pid, CDialog dialog) {
		
		byte[] stream = null;
		
		try {
			HashMap<String, String> dcmi = Common.DCMI_MAP;
			dcmi.put("Identifier",pid);
			for (String s: Common.DCMI) {
				String value = ((JTextField) dialog.getGuiComposite().getWidget("jtf"+s)).getText();
				if (s.equals("Title") && value.isEmpty()) value = Common.UNKNOWN_TITLE;
				if (!value.isEmpty()) {
					if (value.startsWith("#")) value = value.substring(1);
					dcmi.put(s, value);
				}
			}		
			stream = Utils.getDcmiXML(dcmi);
		} catch (Exception e) {}	
		
		return stream;

	}
	
	public static void setUIFont (javax.swing.plaf.FontUIResource font) {
		Enumeration keys = UIManager.getLookAndFeel().getDefaults().keys();
		while (keys.hasMoreElements() ) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource) UIManager.put(key, font);
		}
		
	}
	
	public static Vector getVector(int length) {		
		String[] row = new String[length];
		for (int i = 0; i < row.length; i++) row[i] = "";
		return new Vector<String>(Arrays.asList(row));	
	}
	
	public static String getDate() {
		String date = StringUtils.substringBefore(new Date().toString(), " CEST");	
		return date;
	}
	
	public static String rename (String s) {
		
		if (s.contains(":")) {
			
			if (s.matches("(cirilo:).*") ) { 
					s = s.replaceAll("Backbone","settings").replaceAll("cirilo:","");
				if (s.contains("."))  {
					s = getPath("cm4f",  "accounts", s.split("\\.")[1], "prototypes", s.split("\\.")[0]); 
				} else if (isLowerCase(s.charAt(0))) {
					s = s.contains("settings") ? getPath("cm4f", "defaults") : getPath("cm4f", "accounts", s, "defaults");
				} else {
					s = getPath("cm4f", "prototypes", s);
				}
			}
			
			s = s.toLowerCase().replaceAll("container:|collection:", "context:");
			
			if (s.matches("(query:|corpus:|context:).*") ) { 
		  		String uuid = UUID.nameUUIDFromBytes(s.replaceAll("\\W",".").getBytes()).toString().replaceAll("-","/");
		  		Matcher m = Pattern.compile("(context|query|corpus)[:]([a-z0-9]{1,})([.-]*)(.*)").matcher(s);
		  		if (m.find()) s = "aggregations/"+m.group(1)+"/"+m.group(2)+"/"+uuid;	
			} else if (s.matches("(o:prototype).*") ) { 	
				Matcher m = Pattern.compile("o:prototype\\.(.*)").matcher(s);
				if (m.find()) s = ("cm4f/prototypes/"+m.group(1)).replaceAll("\\W", "/");
		    } else if (s.matches("(o:).*") ) { 
		    	s = s.replaceAll("o:","objects/").replaceAll("\\W", "/");
	    		if (s.split("/").length == 2) s+="/radix";				
		    }  
			
		}
	
		return Common.REPO_ROOT+s;	
	 }	
	
	private static String getPath (String... args) {
		String s = "";
		for (String arg : args) {
			s+= (s.isEmpty() ? "" : "/") + rename(arg);
		}
		return s;
	}

    private static boolean isLowerCase(char c) {
			return c >= 'a' && c <= 'z';
	 }
}
