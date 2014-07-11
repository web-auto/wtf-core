/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Base XML Parser.
 *
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseXMLParser {

  public BaseXMLParser(String xmlData) {
    // Helper class.
  }

  public static Document getDOMDocument(String xmlData) throws ParserConfigurationException,
      SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document domTree = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
    return domTree;
  }

  public static String getXmlStringFromDOMDocument(Document domTree) {
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = null;
    try {
      transformer = factory.newTransformer();
    } catch (TransformerConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    StringWriter writer = new StringWriter();
    Result result = new StreamResult(writer);
    Source source = new DOMSource(domTree);
    try {
      transformer.transform(source, result);
    } catch (TransformerException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return writer.toString();
  }

  public static NodeList exeXPath(Document domTree, String xpathString) {
    XPath xpath = XPathFactory.newInstance().newXPath();
    try {
      return (NodeList) xpath.evaluate(xpathString, domTree, XPathConstants.NODESET);
     // domTree.getElementsByTagName(tagname)
    } catch (XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public static void setText(NodeList nodeList, String text) {
    if (nodeList.getLength() > 0) {
      nodeList.item(0).setTextContent(text);
    }
  }

  public static String getText(NodeList nodeList) {
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent();
    }
    return "";
  }

  public static List<String> getTextList(NodeList nodeList) {
    if(nodeList.getLength()>0){
      List<String> result = new ArrayList<String>();
      int index=0;
      while (index < nodeList.getLength()) {
        result.add(nodeList.item(index).getTextContent());
        index++;
      }
      return result;
    }
	return null;
  }
  
  public static String getAttributeValue(NodeList nodeList, String attributeName) {
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getAttributes().getNamedItem(attributeName).getNodeValue();
    }
    return "";
  }

  public static Node getNode(NodeList nodeList) {
    if (nodeList.getLength() > 0) {
      return nodeList.item(0);
    }
    return null;
  }
}
