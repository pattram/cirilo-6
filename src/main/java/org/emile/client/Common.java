package org.emile.client;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.commons.lang3.StringUtils;

public class Common {

	public static final String[] DCMI = { "Title", "Description", "Subject", "Creator", "Publisher", "Contributor",
			"Language", "Date", "Type", "Format", "Source", "Relation", "Coverage", "Rights" };

	
	public final static HashMap<String, String> DCMI_MAP = new HashMap<String, String>();
	static {
		DCMI_MAP.put("Title",             null);
		DCMI_MAP.put("Description",       null);
		DCMI_MAP.put("Identifier",        null);
		DCMI_MAP.put("Subject",           null);
		DCMI_MAP.put("Creator",           null);
		DCMI_MAP.put("Publisher",         null);
		DCMI_MAP.put("Contributor",       null);
		DCMI_MAP.put("Date",              null);
		DCMI_MAP.put("Type",              null);
		DCMI_MAP.put("Format",            null);
		DCMI_MAP.put("Source",            null);
		DCMI_MAP.put("Language",          null);
		DCMI_MAP.put("Relation",          null);
		DCMI_MAP.put("Coverage",          null);
		DCMI_MAP.put("Rights",            null);
	}
	
	public static enum Schemes {XMLSchema, RelaxNG, RelaxNGCompact};
	
	public static final String MAIN_WINDOW_HEADER = "Cirilo 6 Release 1.4.0.2";

	public static final String[] PROPERTIES = { "Fedora.Repositories" };

	public static final String[] QUERY = { null, null, null, "500", "false" };

	public static final String VALID_PID_REGEX = "(o|context|query|corpus):([a-z])([-.a-z0-9]){1,}";

	public static final Pattern VALID_PID_PATTERN = Pattern.compile(VALID_PID_REGEX);

	public static final ImageIcon OPEN_BUTTON_SMALL = new ImageIcon(Cirilo.class.getResource("open_button_small.png"));
	public static final ImageIcon OPEN_BUTTON_MEDIUM = new ImageIcon(Cirilo.class.getResource("open_button_medium.png"));
	public static final ImageIcon GAMS_BUTTON = new ImageIcon(Cirilo.class.getResource("gams_button.png"));

	public static final Object[] NAMESPACES = { "o:", "context:", "corpus:", "query:" };

	public static final String APACHE = "http://apache:82";

	public static final String THIS = "@this";

	public static final String UNKNOWN_TITLE = "Unknown";
	
	public static final String HDL_PREFIX  = "hdl:";
	
    public static final String  REPO_ROOT = "http://fcrepo4:8080/fcrepo/rest/";

    public static final String  PRIMARY_SOURCES = "|TEI_SOURCE|LIDO_SOURCE|MEI_SOURCE|ONTOLOGY|DESCRIPTION|GM_SOURCE|METS_SOURCE|";
    
    public static final String  COMPONENT_METADATA= "http://www.clarin.eu/cmd/1/profiles/clarin.eu:cr1:p_1562754657284";
   
    public static final String PYRAMIDAL = "-pyramidal";

	public final static ArrayList<String> IMAGE_EXTENSIONS = new ArrayList<String>();
	static {
		IMAGE_EXTENSIONS.add("jpg,jpeg,tif,tiff,gif,png");
		IMAGE_EXTENSIONS.add("image/tiff");
		IMAGE_EXTENSIONS.add("image/jpeg");
		IMAGE_EXTENSIONS.add("image/png");
		IMAGE_EXTENSIONS.add("image/gif");
	}
	
	public final static String NONDELETEABLEDATASTREAMS = "|DC|RELS-EXT|QR|THUMBNAIL|RDF_TRIPLES|GeoRDF_TRIPLES|"; // + Versionable datastreams from cirilo.properties	
	
	public final static ArrayList<String> MIMETYPES = new ArrayList<String>();
	static {
		MIMETYPES.add("text/xml");
		MIMETYPES.add("application/javascript");
		MIMETYPES.add("video/mp4");
		MIMETYPES.add("application/json");
		MIMETYPES.add("application/msword");
		MIMETYPES.add("application/octet-stream");
		MIMETYPES.add("application/pdf");
		MIMETYPES.add("application/rdf+xml");
		MIMETYPES.add("application/sparql-query");
		MIMETYPES.add("model/vnd.cnr-isti.nxz");
		MIMETYPES.add("application/vnd.ms-excel");
		MIMETYPES.add("application/vnd.ms-powerpoint");
		MIMETYPES.add("application/vnd.ms-powerpoint");
		MIMETYPES.add("application/vnd.oais.opendocument.text");
		MIMETYPES.add("application/vnd.oais.opendocument.presentation");
		MIMETYPES.add("application/vnd.oais.opendocument.spreadsheet");
		MIMETYPES.add("application/vnd.sun.xml.calc");
		MIMETYPES.add("application/vnd.sun.xml.impress");
		MIMETYPES.add("application/vnd.sun.xml.writer");
		MIMETYPES.add("application/xhtml+xml");
		MIMETYPES.add("application/x-shockwave-flash");
		MIMETYPES.add("application/zip");
		MIMETYPES.add("application/gzip");
		MIMETYPES.add("audio/mp3");
		MIMETYPES.add("audio/x-wave");
		MIMETYPES.add("image/gif");
		MIMETYPES.add("image/jpeg");
		MIMETYPES.add("image/png");
		MIMETYPES.add("image/tiff");
		MIMETYPES.add("text/css");
		MIMETYPES.add("text/html");
		MIMETYPES.add("text/plain");
		MIMETYPES.add("text/turtle");
		MIMETYPES.add("video/mpeg");
	};

	public static ResourceBundle getResourceBundle(String locale) {

		ResourceBundle res = null;

		Locale.setDefault(Locale.ENGLISH);
		res = ResourceBundle.getBundle("org.emile.client.Resources", Locale.getDefault());

		UIManager.put("OptionPane.yesButtonText", res.getString("yes"));
		UIManager.put("OptionPane.noButtonText", res.getString("no"));
		UIManager.put("OptionPane.cancelButtonText", res.getString("cancel"));

		UIManager.put("FileChooser.cancelButtonText", res.getString("cancel"));
		UIManager.put("FileChooser.openButtonText", res.getString("open").replaceAll("[.]", ""));
		UIManager.put("FileChooser.acceptAllFileFilterText", res.getString("allfiles"));
		UIManager.put("FileChooser.lookInLabelText", res.getString("lookin"));
		UIManager.put("FileChooser.cancelButtonToolTipText", "");
		UIManager.put("FileChooser.openButtonToolTipText", "");
		UIManager.put("FileChooser.filesOfTypeLabelText", res.getString("filetype"));
		UIManager.put("FileChooser.fileNameLabelText", res.getString("filename"));
		UIManager.put("FileChooser.listViewButtonToolTipText", "");
		UIManager.put("FileChooser.listViewButtonAccessibleName", res.getString("list"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "");
		UIManager.put("FileChooser.detailsViewButtonAccessibleName", res.getString("details"));
		UIManager.put("FileChooser.upFolderToolTipText", "");
		UIManager.put("FileChooser.upFolderAccessibleName", "..");
		UIManager.put("FileChooser.homeFolderToolTipText", "");
		UIManager.put("FileChooser.homeFolderAccessibleName", "/");
		UIManager.put("FileChooser.fileNameHeaderText", res.getString("name"));
		UIManager.put("FileChooser.fileSizeHeaderText", res.getString("size"));
		UIManager.put("FileChooser.fileTypeHeaderText", res.getString("type"));
		UIManager.put("FileChooser.fileDateHeaderText", res.getString("date"));
		UIManager.put("FileChooser.fileAttrHeaderText", res.getString("extension"));
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);

		return res;
	}

	public static String msgFormat(String... params) {

		MessageFormat msgFmt = new MessageFormat(params[0]);

		String[] args = new String[params.length - 1];
		for (int i = 0; i < params.length - 1; i++)
			args[i] = params[i + 1];

		return msgFmt.format(args);
	}
	
	public static String getDate() {
		String date = StringUtils.substringBefore(new Date().toString(), " CEST");	
		return date;
	}
	
}
