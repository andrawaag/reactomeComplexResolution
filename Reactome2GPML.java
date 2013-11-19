import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.*;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.bridgedb.DataSource;
import org.pathvisio.model.ConverterException;
import org.pathvisio.model.DataNodeType;
import org.pathvisio.model.ObjectType;
import org.pathvisio.model.Pathway;
import org.pathvisio.model.PathwayElement;
import org.pathvisio.view.DefaultTemplates.ReactionTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Reactome2GPML {

	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws MalformedURLException 
	 * @throws XPathExpressionException 
	 * @throws ConverterException 
	 */
	public static void convertProtein(XPath xPath, NodeList reactomeNodes, Pathway gpmlPathway, int i) throws XPathExpressionException{
		PathwayElement reactomeDataNode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		reactomeDataNode.setMWidth(80.0);
		reactomeDataNode.setMHeight(20.0);
		String xpathExpression2 = "./Properties/displayName";
		String textLabel = xPath.compile(xpathExpression2).evaluate( reactomeNodes.item(i));
		reactomeDataNode.setTextLabel(textLabel);
		reactomeDataNode.setDataNodeType(DataNodeType.PROTEIN);
		reactomeDataNode.setDataSource(DataSource.getByFullName("Reactome"));
		reactomeDataNode.setGeneID(reactomeNodes.item(i).getAttributes().getNamedItem("reactomeId").getTextContent());
		reactomeDataNode.setMCenterX(Double.parseDouble(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[0]));
		reactomeDataNode.setMCenterY(Double.parseDouble(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[1]));
		System.out.println(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[0]);
		gpmlPathway.add(reactomeDataNode);	
		reactomeDataNode.setGeneratedGraphId();
		System.out.println(reactomeNodes.item(i).getNodeName());
	}
	
	public static void convertProtein(XPath xPath, NodeList reactomeNodes, Pathway gpmlPathway, int i, Double centerX, Double centerY) throws XPathExpressionException{
		PathwayElement reactomeDataNode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		reactomeDataNode.setMWidth(80.0);
		reactomeDataNode.setMHeight(20.0);
		String xpathExpression2 = "./Properties/displayName";
		String textLabel = xPath.compile(xpathExpression2).evaluate( reactomeNodes.item(i));
		reactomeDataNode.setTextLabel(textLabel);
		reactomeDataNode.setDataNodeType(DataNodeType.PROTEIN);
		reactomeDataNode.setDataSource(DataSource.getByFullName("Reactome"));
		reactomeDataNode.setGeneID(reactomeNodes.item(i).getAttributes().getNamedItem("reactomeId").getTextContent());
		reactomeDataNode.setMCenterX(centerX);
		reactomeDataNode.setMCenterY(centerY);
		System.out.println(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[0]);
		gpmlPathway.add(reactomeDataNode);	
		reactomeDataNode.setGeneratedGraphId();
		System.out.println(reactomeNodes.item(i).getNodeName());
	}
	
	public static void convertComplex(XPath xPath, NodeList reactomeNodes, Pathway gpmlPathway, int i, DocumentBuilderFactory dbf) throws XPathExpressionException, ParserConfigurationException, MalformedURLException, SAXException, IOException{
		PathwayElement reactomeDataNode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		reactomeDataNode.setMWidth(80.0);
		reactomeDataNode.setMHeight(20.0);
		String xpathExpression2 = "./Properties/displayName";
		String textLabel = xPath.compile(xpathExpression2).evaluate( reactomeNodes.item(i));
		reactomeDataNode.setTextLabel(textLabel);
		reactomeDataNode.setDataNodeType(DataNodeType.COMPLEX);
		Double centerX = Double.parseDouble(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[0]);
		Double centerY = Double.parseDouble(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[1]);
		reactomeDataNode.setMCenterX(centerX);
		reactomeDataNode.setMCenterY(centerY);
		System.out.println(centerX);
		gpmlPathway.add(reactomeDataNode);	
		reactomeDataNode.setGeneratedGraphId();
		String complexId = reactomeNodes.item(i).getAttributes().getNamedItem("reactomeId").getTextContent();
		String urlString =  "http://reactomews.oicr.on.ca:8080/ReactomeRESTfulAPI/RESTfulWS/queryById/Complex/"+complexId;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("Accept", "application/xml");	
		DocumentBuilder db = dbf.newDocumentBuilder();
		//System.out.println(url.openStream());
		Document reactomeComplex = db.parse(conn.getInputStream());		
		String xpathExpression = "/complex/hasComponent";
		NodeList reactomeComplexCompartmentNodes = (NodeList) xPath.compile(xpathExpression).evaluate(reactomeComplex, XPathConstants.NODESET);
		for (int a=0; a<reactomeComplexCompartmentNodes.getLength(); a++){
			convertComplex(xPath, reactomeComplexCompartmentNodes, gpmlPathway, a, dbf, centerX, centerY);
		}
		xpathExpression = "/entityWithAccessionedSequence/referenceEntity";
		NodeList reactomeComplexAccessionedSequenceNodes = (NodeList) xPath.compile(xpathExpression).evaluate(reactomeComplex, XPathConstants.NODESET);
		for (int b=0; b<reactomeComplexAccessionedSequenceNodes.getLength(); b++){
			convertProtein(xPath, reactomeComplexAccessionedSequenceNodes, gpmlPathway, b, centerX, centerY);
		}
	}
	
	public static void convertComplex(XPath xPath, NodeList reactomeNodes, Pathway gpmlPathway, int i, DocumentBuilderFactory dbf, Double centerX, Double centerY) throws XPathExpressionException, ParserConfigurationException, MalformedURLException, SAXException, IOException{
		PathwayElement reactomeDataNode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		reactomeDataNode.setMWidth(80.0);
		reactomeDataNode.setMHeight(20.0);
		String xpathExpression2 = "//stableIdentifier/displayName";
		String textLabel = xPath.compile(xpathExpression2).evaluate(reactomeNodes.item(i));
		System.out.println(textLabel);
		reactomeDataNode.setTextLabel(textLabel);
		reactomeDataNode.setDataNodeType(DataNodeType.COMPLEX);		
		reactomeDataNode.setMCenterX(centerX);
		reactomeDataNode.setMCenterY(centerY);
		System.out.println(centerX);
		gpmlPathway.add(reactomeDataNode);	
		reactomeDataNode.setGeneratedGraphId();
		String xpathExpression3 = "//stableIdentifier/dbId";
		String complexId = xPath.compile(xpathExpression3).evaluate(reactomeNodes.item(i));
		System.out.println(complexId);
		String urlString =  "http://reactomews.oicr.on.ca:8080/ReactomeRESTfulAPI/RESTfulWS/queryById/Complex/"+complexId;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("Accept", "application/xml");	
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document reactomeComplex = db.parse(conn.getInputStream());	
		String xpathExpression = "/complex/hasComponent";
		NodeList reactomeComplexCompartmentNodes = (NodeList) xPath.compile(xpathExpression).evaluate(reactomeComplex, XPathConstants.NODESET);
		for (int a=0; a<reactomeComplexCompartmentNodes.getLength(); a++){
			convertComplex(xPath, reactomeNodes, gpmlPathway, i, dbf, centerX, centerY);
		}
		xpathExpression = "/entityWithAccessionedSequence/referenceEntity";
		NodeList reactomeComplexAccessionedSequenceNodes = (NodeList) xPath.compile(xpathExpression).evaluate(reactomeComplex, XPathConstants.NODESET);
		for (int b=0; b<reactomeComplexAccessionedSequenceNodes.getLength(); b++){
			convertProtein(xPath, reactomeNodes, gpmlPathway, i, centerX, centerY);
		}
	}
	
	
	
	public static void convertChemical(XPath xPath, NodeList reactomeNodes, Pathway gpmlPathway, int i) throws XPathExpressionException{
		PathwayElement reactomeDataNode = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		reactomeDataNode.setMWidth(80.0);
		reactomeDataNode.setMHeight(20.0);
		String xpathExpression2 = "./Properties/displayName";
		String textLabel = xPath.compile(xpathExpression2).evaluate( reactomeNodes.item(i));
		reactomeDataNode.setTextLabel(textLabel);
		reactomeDataNode.setDataNodeType(DataNodeType.METABOLITE);
		reactomeDataNode.setMCenterX(Double.parseDouble(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[0]));
		reactomeDataNode.setMCenterY(Double.parseDouble(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[1]));
		System.out.println(reactomeNodes.item(i).getAttributes().getNamedItem("position").getTextContent().split(" ")[0]);
		gpmlPathway.add(reactomeDataNode);	
		reactomeDataNode.setGeneratedGraphId();
	}
	
	public static void main(String[] args) throws ParserConfigurationException, MalformedURLException, SAXException, IOException, XPathExpressionException, ConverterException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		XPath xPath =  XPathFactory.newInstance().newXPath();
		String url = "http://reactomews.oicr.on.ca:8080/ReactomeRESTfulAPI/RESTfulWS/pathwayDiagram/169911/XML";
		Document reactomePathway = db.parse(new URL(url).openStream());

		HashMap<String, String> reactomecomponent2graphid = new HashMap<String, String>();
		//Get Pathway Title
		String xpathExpression = "/Process/Properties/displayName";
		String reactomePathwayTitle = xPath.compile(xpathExpression).evaluate(reactomePathway);
		System.out.println(reactomePathwayTitle);

		xpathExpression = "/Process/Nodes";
		Node nodes = (Node) xPath.compile(xpathExpression).evaluate(reactomePathway, XPathConstants.NODE);
		//createPathway model
		NodeList reactomeNodes = nodes.getChildNodes();

		Pathway gpmlPathway = new Pathway();
		//gpmlPathway.getMappInfo().set
		for (int i = 0; i<reactomeNodes.getLength(); i++){
			
		//Proteins
			if (reactomeNodes.item(i).getNodeName().equals("org.gk.render.RenderableProtein")){
				convertProtein(xPath, reactomeNodes, gpmlPathway, i);
			}
			//Complex
			if (reactomeNodes.item(i).getNodeName().equals("org.gk.render.RenderableComplex")){
				convertComplex(xPath, reactomeNodes, gpmlPathway, i, dbf);
			}
			// Chemical
			if (reactomeNodes.item(i).getNodeName().equals("org.gk.render.RenderableComplex")){
				convertChemical(xPath, reactomeNodes, gpmlPathway, i);
			}
			
		}
		File tmp = new File("/tmp/test.gpml");
		gpmlPathway.writeToXml(tmp, true);

	}

}
