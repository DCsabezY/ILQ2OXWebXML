import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class XPath_ILQ2OX {
    
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document = documentBuilder.parse("ILQ2OX_1111\\XPath_ILQ2OX\\src\\student_ILQ2OX.xml");
            document.getDocumentElement().normalize();

            //az XPath elkészítése
            XPath xPath = XPathFactory.newInstance().newXPath();

            //Meg kell adni az elérési út kifejezést és a csomópont listát:
            String neptunkod = "/hallgatok/hallgato";
            
            System.out.println("\n Futás eredménye: ");

            NodeList neptunKod = (NodeList) xPath.compile(neptunkod).evaluate(document, XPathConstants.NODESET);

            BufferedWriter writer = new BufferedWriter(new FileWriter("ILQ2OX_1111\\\\XPath_ILQ2OX\\\\src\\\\student_ILQ2OX_1.xml"));

            writer.write("<?xml version=\"1.0\" encoding =\"UTF-8\"?>\n");
            writer.write("<hallgatok>\n");

            //A for ciklus segítségével a NodeList csomópontjain végig kell menni.
            for (int i = 0; i < neptunKod.getLength(); i++) {
                
                //kivesszük a NodeList-ből az aktuális elemet.
                Node node = neptunKod.item(i);

                System.out.println("\nAktuális elem: " + node.getNodeName());

                //Meg kell vizsgálni a csomópontot, tesztelni kell a subelemet
                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("hallgato")) {
                    //Node csomópontot elemmé konvertálja
                    Element element = (Element) node;

                    String id = element.getAttribute("id");
                    String keresztnev = element.getElementsByTagName("keresztnev").item(0).getTextContent();
                    String vezeteknev = element.getElementsByTagName("vezeteknev").item(0).getTextContent();
                    String foglalkozas = element.getElementsByTagName("foglalkozas").item(0).getTextContent();

                    System.out.println("Hallgató ID: " + id);
                    System.out.println("Hallgató keresztnevnév: " + keresztnev);
                    System.out.println("Hallgató vezetéknév: " + vezeteknev);
                    System.out.println("Hallgató foglalkozása: " + foglalkozas);

                    writer.write("  <hallgato id=\"" + id + "\">\n");
                    writer.write("      <keresztnev>" + keresztnev + "</keresztnev>\n");
                    writer.write("      <vezeteknev>" + vezeteknev + "</vezeteknev>\n");
                    writer.write("      <foglalkozas>" + foglalkozas + "</foglalkozas>\n");
                    writer.write("  </hallgato>\n");
                }
            }

            writer.write("</hallgatok>");
            writer.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }   catch (SAXException e) {
            e.printStackTrace();
        }   catch (IOException e) {
            e.printStackTrace();
        }   catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }
}
