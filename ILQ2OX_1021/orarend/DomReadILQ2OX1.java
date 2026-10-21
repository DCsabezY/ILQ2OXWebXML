package ILQ2OX_1021.orarend;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomReadILQ2OX1 {
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
                
        //Ebből a fájlból olvas
        File xmlFile = new File("ILQ2OX_1021/orarend/ILQ2OX_orarend.xml"); 

        //DocumentBuilderFactoryból megkapjuk a DocumentBuilder-t
        DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();

        //A DocumentBuilder a DocumentBuilderFactory típusú objektum new DocumentBuilder() metódusával jön létre
        DocumentBuilder dBuilder = Factory.newDocumentBuilder();

        //A parse() metódus elemzi és betölti az XML fájlt a doc objektumba
        Document doc = dBuilder.parse(xmlFile);

        //A dokumentum normalizálása segíti a helyes eredmények elérését
        doc.getDocumentElement().normalize();

        //Kiíratjuk a dokumentum gyökérelemét
        System.out.println("Gyökérelem: " + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("Ora");

        for (int i = 0; i < nList.getLength(); i++) {
            
            Node nNode = nList.item(i);

            System.out.println("\nAktuális elem: " + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                
                Element elem = (Element) nNode;

                String id = elem.getAttribute("id");
                String type = elem.getAttribute("típus");

                Node node1 = elem.getElementsByTagName("targy").item(0);
                String targy = node1.getTextContent();

                Element idopont = (Element) elem.getElementsByTagName("idopont").item(0);
                String nap = idopont.getElementsByTagName("nap").item(0).getTextContent();
                String tol = idopont.getElementsByTagName("tol").item(0).getTextContent();
                String ig = idopont.getElementsByTagName("ig").item(0).getTextContent();

                Node node2 = elem.getElementsByTagName("helyszin").item(0);
                String place = node2.getTextContent();

                Node node3 = elem.getElementsByTagName("oktato").item(0);
                String teacher = node3.getTextContent();

                Node node4 = elem.getElementsByTagName("szak").item(0);
                String course = node4.getTextContent();

                System.out.println("Óra ID: " + id);
                System.out.println("Típus: " + type);
                System.out.println("Tárgy: " + targy);
                System.out.println("Időpont");
                System.out.println("    Nap: " + nap);
                System.out.println("    Tól: " + tol);
                System.out.println("    Ig: " + ig);
                System.out.println("Helyszín: " + place);
                System.out.println("Oktató: " + teacher);
                System.out.println("Szak: " + course);
            }
        }
    }
}
