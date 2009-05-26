package org.oboedit.gui.components.ontologyGeneration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

/**
 * 
 * @author Marcel Hanke
 * @author Goetz Fabian
 *
 */
public class HtmlToTextExtraction extends DataExtraction {
	private HTMLDocument doc;
	
	protected final static Logger logger = Logger.getLogger(HtmlToTextExtraction.class);
	
	/**
	 * Tries to extend an existing definition by looking up its source html content. If no extension could be found,
	 * the definition is left as it is.
	 * 
	 * @return true if extended, false otherwise
	 */
	public boolean definitionExtraction(CandidateDefinition definition)
	{
		if(definition == null || definition.getCachedURL() == null || definition.getCachedURL().isEmpty()) {
   			return false;	
   		}
		
		if(!load(definition)) {
			return false;
		}
		
		if(!parse(definition)) {
			return false;
		}
		
		return true;
	}

	private boolean load(CandidateDefinition definition) {
		if (definition == null || definition.getCachedURL() == null || definition.getCachedURL().isEmpty()) {
			return false;
		}

		URL url;

		try {
			if (definition.getCachedURL().get(0).endsWith("pdf") || definition.getCachedURL().get(0)
			    .endsWith("ppt") || definition.getCachedURL().get(0).endsWith("doc")) {
				logger.error("Definitions from files not in HTML are not extended.");
				return false;
			}

			url = new URL(definition.getCachedURL().get(0));

			HTMLEditorKit kit = new HTMLEditorKit();
			doc = (HTMLDocument) kit.createDefaultDocument();

			doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);

			URLConnection con = url.openConnection();
			con.setConnectTimeout(1000);

			con.connect();

			Reader HTMLReader = new InputStreamReader(con.getInputStream());
			try {
				kit.read(HTMLReader, doc, 0);
			} catch (BadLocationException e) {
				return false;
			}
			
			return true;
			} 
			catch (MalformedURLException e) {
				return false;
			}
			catch (SocketTimeoutException e) {
				logger.error("Timeout during connection.", e);
				return false;
			}
			catch (IOException e) {
				return false;
			}
	}
	
	private boolean parse(CandidateDefinition definition) {
		String def = definition.getDefinition();
		if (def.endsWith("...")) {
			def = def.substring(0, def.length()-3);
		}
		// filter out some special characters
		if (def.contains("[") || def.contains("]")) {
			StringTokenizer tokenizer = new StringTokenizer(def);
			String token;
			def = "";
			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken();
				if (token.contains("[")) {
					token = token.replace('[', ' ').trim();
				}
				else if (token.contains("]")) {
					token = token.replace(']', ' ').trim();
				}
				def = def + " " + token;
			}
		}
		ElementIterator iter = new ElementIterator(doc);
		Element elem;
		try {
			boolean run = true;
			while (run) {
				elem = iter.next();
				if (elem == null) {
					run = false;
					continue;
				}
	
				// we found the main content of the HTML document
				if (elem.getName().equals("content")) {
					String element;
					
						element = elem.getDocument().getText(0, elem.getDocument().getLength() - 1);
					
					int defLength = def.length();
	
					// extending definition if it contains def
					if (defLength > 2 && element.contains(def.subSequence(0, defLength - 2))) {
						int begin = element.indexOf(def.substring(0, defLength - 2));
	
						boolean extending = true;
						int i = begin + defLength - 2;
						while (extending) {
							i++;
	
							if (element.charAt(i) == '.') {
								extending = false;
								String newDef = element.subSequence(begin, i) + ".";
	
								if (def.equals(newDef)) {
									return false;
								}
								else {
									definition.setDefinition(newDef);
	
									// write html formatted definition
									super.generateHTMLFormattedDefinition(definition);
	
									return true;
								}
							}
						}
						break;
					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	@Override
	public String fileExtraction(File file) {
		// TODO Auto-generated method stub
		
		return null;
	}
}