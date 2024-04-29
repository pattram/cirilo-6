package org.emile.client.dialog.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.bounce.text.LineNumberMargin;
import org.bounce.text.ScrollableEditorPanel;
import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLFoldingMargin;
import org.bounce.text.xml.XMLStyleConstants;
import org.emile.cirilo.Namespaces;
import org.emile.cirilo.fedora.FedoraConnector;
import org.emile.cirilo.utils.XMLUtils;
import org.emile.client.Common;
import org.emile.client.ServiceNames;
import org.emile.client.dialog.ObjectEditorDialog;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import voodoosoft.jroots.core.CPropertyService;
import voodoosoft.jroots.core.CServiceProvider;

public class XMLEditor {
	
	private static Logger log = Logger.getLogger(XMLEditor.class);

	private FedoraConnector connector;
	private ObjectEditorDialog parent;
	private ResourceBundle res;
	private JEditorPane editor;
	private XMLEditorKit kit;
	private String dsid;
	private String mimetype;
	private SAXBuilder builder;
	private Format format;
	private XMLOutputter outputter;
	private boolean updated;
	
	public XMLEditor(FedoraConnector connector, ObjectEditorDialog parent) {
		
		this.connector = connector;
		this.parent = parent;
				
		kit = new XMLEditorKit();
		kit.setAutoIndentation(true);
		kit.setTagCompletion(true); 
		kit.setStyle(XMLStyleConstants.ATTRIBUTE_NAME, new Color(255, 0, 0), Font.BOLD);
		
		builder = new SAXBuilder();
		
	    format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		
		outputter = new XMLOutputter(format);

	}
		
	public JPanel get(String pid, String dsid, String mimetype) {
		
		try {
			
			this.updated = false;
			this.dsid = dsid;
			this.mimetype = mimetype;
			
			if (mimetype.contains("application/pdf")) {
				try {
					PDDocument pdd = PDDocument.load(connector.stubGetDatastream(pid, dsid));
					PDDocumentCatalog catalog = pdd.getDocumentCatalog();
					PDMetadata metadata = catalog.getMetadata();
					PDFTextStripperByArea stripper = new PDFTextStripperByArea();
					PDFTextStripper tStripper = new PDFTextStripper();
					Document doc;
					Element xmp;
					Element rdf;
			
					stripper.setSortByPosition(true);

					if (metadata != null) {				
						InputStream is = metadata.createInputStream();
						doc = builder.build(is);
				
						XPath xpath = XPath.newInstance("//x:xmpmeta");
						xpath.addNamespace(Namespaces.xmlns_x);
					
						xmp = ((Element)((Element)xpath.selectSingleNode(doc)).clone());
						rdf = xmp.getChild("RDF", Namespaces.xmlns_rdf);
										
						rdf.addContent(getDescription(tStripper.getText(pdd)));
						is.close();				
					} else {			
						xmp = new Element ("xmpmeta", Namespaces.xmlns_x);
						rdf = new Element ("RDF", Namespaces.xmlns_rdf);
						xmp.addContent(rdf);
						rdf.addContent(getDescription(tStripper.getText(pdd)));
					}
			

					for (Object o : rdf.getChildren("Description", Namespaces.xmlns_rdf)) {
						((Element)o).removeAttribute("about", Namespaces.xmlns_rdf);	
					}
				
					return (createViewer(outputter.outputString(xmp).getBytes(), mimetype));
					
				} catch (Exception q) {
					return (createViewer("".getBytes(), mimetype));				
				}
			} else {
				return (createViewer(connector.stubGetDatastream(pid, dsid), mimetype));
			}
		} catch (Exception e) {
			log.error(e);
		}

		return null;
	}

	public JPanel createViewer(byte[] stream, String mimetype) {

		JPanel frame = new JPanel(new BorderLayout());
		
		try {
			
			dispose();
						
			res = (ResourceBundle) CServiceProvider.getService(ServiceNames.RESOURCES);
			CPropertyService props = (CPropertyService) CServiceProvider.getService(ServiceNames.PROPERTIES);

			editor = new JEditorPane();
			
			kit.setTagCompletion(mimetype.toLowerCase().contains("xml")); 
			
			editor.setEditorKit(kit);     
			editor.read(new ByteArrayInputStream(stream), null);
			editor.setFont(new Font("Courier", Font.PLAIN, 12));
			
			editor.getDocument().putProperty(XMLEditorKit.ERROR_HIGHLIGHTING_ATTRIBUTE, new Boolean(true));
		    editor.getDocument().putProperty(PlainDocument.tabSizeAttribute, new Integer(4));

		    editor.getDocument().addDocumentListener(new XMLDocumentListener());
		    
			ScrollableEditorPanel editorPanel = new ScrollableEditorPanel(editor);
			JScrollPane scroller = new JScrollPane(editorPanel);
		
			JMenuBar menu = new JMenuBar();
			JMenu file = new JMenu(res.getString("file"));
			menu.add(file);
					
			JMenuItem save = new JMenuItem(res.getString("save"));
			save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
			save.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleSave(ae);
        		}
        	});

			file.add(save);

			JMenu source = new JMenu(res.getString("source"));
			menu.add(source);
			
			JMenuItem format = new JMenuItem(res.getString("format"));
			format.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
			format.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent ae) {
        			handleFormat(ae);
        		}
        	});
	
			source.add(format);
			
			frame.add(menu, BorderLayout.NORTH);
			
			file.setEnabled(!mimetype.contains("application/pdf"));
			source.setEnabled(mimetype.contains("xml"));
			
			JPanel header = new JPanel(new BorderLayout());
			header.add(new XMLFoldingMargin(editor), BorderLayout.EAST);
			header.add(new LineNumberMargin(editor), BorderLayout.WEST);
			scroller.setRowHeaderView(header);
			frame.add(scroller, BorderLayout.CENTER);		
				
			Font font = editor.getFont();
			editor.setFont(new javax.swing.plaf.FontUIResource(font.getFontName(), 
					font.getStyle(), 
					new Integer(props.getProperty("user", "FontSize"))));
						
			scroller.updateUI();
	
		} catch (Exception e) {
			log.error(e);
		}
		
		return frame;
    
	}
	
	public void handleSave(ActionEvent ae) {
		try {
			if (JOptionPane.showConfirmDialog(null,Common.msgFormat(res.getString("asksave"), dsid), Common.MAIN_WINDOW_HEADER, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {					
				handleSave();		
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public void handleSave() {
		try {
			CSwingWorker loader = new CSwingWorker(this.parent, dsid + "||||" + mimetype + "::::" + editor.getText());
			loader.execute();
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public boolean isUpdated() {
		return updated;
	}
	
	public void resetUpdated() {
		updated = false;
	}
	
	public void handleFormat(ActionEvent ae) {
		try {
			Document document = XMLUtils.createDocumentFromByteArray(editor.getText().getBytes());
			editor.setText(outputter.outputString(document));
			editor.setCaretPosition(1);
		} catch (Exception e) {
			log.error(e);
		}
	}

    public void dispose() {
		try {
			parent.handleUpdated();
 			editor = null;
		} catch (Exception e) {
			log.error(e);
		}
	}
    
    public String getDsid() {
    	return dsid;
    }
    
    private Element getDescription (String fulltext) {
    
    	Element description = null;
		try {
			description = new Element("Description", Namespaces.xmlns_rdf);
			description.addNamespaceDeclaration(Namespaces.xmlns_dcterms);
			Element dcterms = new Element("description", Namespaces.xmlns_dcterms);
			dcterms.setText(fulltext);
			description.addContent(dcterms);
 		} catch (Exception e) {
			log.error(e);
		}
		return description;
	}
    
    private class XMLDocumentListener implements DocumentListener {
     
        public void insertUpdate(DocumentEvent e) {
            updated = true;
            parent.setApply(updated);
        }
        public void removeUpdate(DocumentEvent e) {
        	 updated = true;
             parent.setApply(updated);
      }
        public void changedUpdate(DocumentEvent e) {
        	 updated = true;
             parent.setApply(updated);
      }
        
    }

}
