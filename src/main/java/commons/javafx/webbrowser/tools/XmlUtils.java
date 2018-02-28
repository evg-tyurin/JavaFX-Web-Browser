package commons.javafx.webbrowser.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;

/**
 * Набор методов для работы с XML-файлами, DOM-документами и XPath-выражениями. 
 * @author ET
 */
public class XmlUtils
{
	/**
	 * Извлекает текст из xml-тэга (элемента, ноды).
	 * @param node xml-тэг (нода)
	 * @return текст, содержащийся внутри тэга
	 */
	public static String getText(Node node)
	{
		String text = "";
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				text += child.getNodeValue() + " ";
			}
		}
		return text;
	}
	/**
	 * Прочитать XML-документ.
	 * @param xmlFile имя файла
	 * @return DOM-документ
	 * @throws Exception
	 */
	public static Document getDomDocument(File xmlFile) throws Exception
	{
		InputStream in = new URL("file:///"+xmlFile.getAbsolutePath()).openStream();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document xmlDoc = docBuilder.parse(in);
        in.close();
		return xmlDoc;
	}
	/**
	 * ЕТ: этот метод перенесен из Utils и он очень похож на {@link #getDomDocument(File)}
	 * @param file xml файл
	 * @return документ DOM
	 * @throws Exception
	 */
	public static Document getXML(File file) throws Exception {
		InputStream in = new FileInputStream(file);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document xml = docBuilder.parse(in);
        in.close();
		return xml;
	}
	/**
	 * Прочитать конфигурационный параметр.
	 * Примеры запросов:
	 * <li>/foo/bar/@id 	- выбрать значение атрибута
	 * <li>/foo/bar/text()  - выбрать текстовое значение
	 * @param xml DOM-документ
	 * @param xPath запрос XPath
	 * @return значение
	 * @throws Exception
	 */
	public static String getConfigParam(Document xml, String xPath) throws Exception {
		//        NodeList nodeList = XPathAPI.selectNodeList(xml, xPath);
		NodeList nodeList = selectNodeList(xml, xPath);
        if(nodeList.getLength()==0)
        	return null;
	    return nodeList.item(0).getNodeValue();
	}
	public static NodeList selectNodeList(Node xml, String xPath) throws Exception{
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile(xPath);
		NodeList nodeList = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
		return nodeList;
	}
    public static String xmlize(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&laquo;"); break;
                case '>': sb.append("&raquo;"); break;
                case '"': sb.append("&quot;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}