package service.io;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import data.service.ScholarDataService;

public class XMLIOService implements FileIOService {

	@Override
	public void populateFromFile(ScholarDataService sds, String filePath) {
		try {

			File file = new File(filePath);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			doc.getDocumentElement().normalize();

			System.out.println("Reading root element :" + doc.getDocumentElement().getNodeName());

			NodeList courseList = doc.getElementsByTagName("course");

			System.out.println("----------------------------");

			for (int i = 0; i < courseList.getLength(); i++) {

				Node nNode = courseList.item(i);

				System.out.println("\nReading: " + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					System.out.println("Staff id : " + eElement.getAttribute("id"));
					System.out.println(
							"First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
					System.out.println(
							"Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
					System.out.println(
							"Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
					System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void write(String path) {
		// TODO Auto-generated method stub

	}

}
