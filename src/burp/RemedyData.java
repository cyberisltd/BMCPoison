package burp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class RemedyData {
	
	private String rData;
	private Document doc;
	
	public RemedyData(String rData) {
		this.rData = rData;
	}
	
	public void generate() {
		try {
			
			String data = rData;
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// Create the XML structure
			doc = docBuilder.newDocument();
			
			// Get the function name
			String funcName = data.split("/")[1];
			Element rootElement = doc.createElement("function");
			rootElement.setAttribute("name", funcName);
			doc.appendChild(rootElement);
			
			data = data.replaceFirst("&.+=.+$", "").replaceFirst("^[0-9]+/[A-Za-z]+/", "");
			//BurpExtender.stdout.println(data);
			
			Integer argnum = 0;
			
			while (data.length() > 0) {
				argnum++;
				// Loop through the data, splitting elements until all the data has been processed
				Integer slashIndex = data.indexOf("/");
				if (!(slashIndex > 0)) {
					// Something possibly went wrong
					BurpExtender.stdout.println(String.format("Something went wrong when processing the Remedy data. The remaining data is: %s",data));
					return;
				}
				Integer size = Integer.parseInt(data.substring(0, slashIndex));
				data = data.substring(slashIndex + 1);
				String value = data.substring(0,size);
				data = data.substring(size);
				if (value.chars().filter(ch -> ch == '/').count() > 1) {
					// This is an array
					slashIndex = value.indexOf("/");
					Integer len = Integer.parseInt(value.substring(0,slashIndex));
					value = value.substring(slashIndex + 1);
					Element array = doc.createElement("argument");
					array.setAttribute("type", "array");
					array.setAttribute("length", Integer.toString(len));
					array.setAttribute("id",Integer.toString(argnum));
					for (Integer i = 0; i < len; i++) {
						// Create each array item
						slashIndex = value.indexOf("/");
						//BurpExtender.stdout.println(value);
						//BurpExtender.stdout.println(Integer.toString(slashIndex));
						size = Integer.parseInt(value.substring(0,slashIndex));
						value = value.substring(slashIndex + 1);
						String ivalue = value.substring(0,size);
						value = value.substring(size);
						Element item = doc.createElement("item");
						item.appendChild(doc.createTextNode(ivalue));
						item.setAttribute("id", String.format("%s.%s", Integer.toString(argnum), Integer.toString(i+1)));
						array.appendChild(item);
					}
					rootElement.appendChild(array);
				} else {
					Element item = doc.createElement("argument");
					item.setAttribute("type", "single");
					item.appendChild(doc.createTextNode(value));
					item.setAttribute("id",Integer.toString(argnum));
					rootElement.appendChild(item);
				}
			}
			
			/*TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(BurpExtender.stdout);
			t.transform(source, result);*/
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} /*catch (TransformerException tce) {
			tce.printStackTrace();
		}*/
	}
	
	public DefaultMutableTreeNode getTree() {
		// Get the root element
		Element function = doc.getDocumentElement();
		String funcName = function.getAttribute("name");
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(funcName);
		NodeList args = function.getElementsByTagName("argument");
		for (Integer i = 0; i < args.getLength(); i++) {
			DefaultMutableTreeNode arg;
			NamedNodeMap attribs = args.item(i).getAttributes();
			if(attribs.getNamedItem("type").getTextContent() == "array") {
				// TODO: Deal with arrays
				PoisonTreeNode nodeData = new PoisonTreeNode(args.item(i).getTextContent(), attribs.getNamedItem("type").getTextContent(), attribs.getNamedItem("id").getTextContent());
				arg = new DefaultMutableTreeNode(nodeData);
			} else {
				PoisonTreeNode nodeData = new PoisonTreeNode(args.item(i).getTextContent(), attribs.getNamedItem("type").getTextContent(), attribs.getNamedItem("id").getTextContent());
				arg = new DefaultMutableTreeNode(nodeData);
			}
			root.add(arg);
		}
		return root;
	}
}
