package ILQ2OX_1021.vendeglatas;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomModifyILQ2OX {
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, TransformerException {

        //Ebből a fájlból olvas
        File xmlFile = new File("vendeglatas/ILQ2OX_XML.xml");

        //DocumentBuilderFactoryból megkapjuk a DocumentBuilder-t
        DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();

        //A DocumentBuilder a DocumentBuilderFactory típusú objektum new DocumentBuilder() metódusával jön létre
        DocumentBuilder dBuilder = Factory.newDocumentBuilder();

        //A parse() metódus elemzi és betölti az XML fájlt a doc objektumba
        Document doc = dBuilder.parse(xmlFile);

        //A dokumentum normalizálása segíti a helyes eredmények elérését
        doc.getDocumentElement().normalize();

        //a.) A v1 azonosítójú vendég nev és eletkor elemének módosítása
        NodeList nList = doc.getElementsByTagName("vendeg");

        for (int i = 0; i < nList.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode;

                //Csak a v1 azonosítójú vendéget módosítjuk
                if (elem.getAttribute("vkod").equals("v1")) {

                    //Lekérjük a gyerekelemeket és átírjuk a tartalmukat
                    Node node1 = elem.getElementsByTagName("nev").item(0);
                    node1.setTextContent("Kiss Anna");

                    Node node2 = elem.getElementsByTagName("eletkor").item(0);
                    node2.setTextContent("30");
                }
            }
        }

        //b.) A gyakornok e_gy attribútumának módosítása e3 értékre
        NodeList nList2 = doc.getElementsByTagName("gyakornok");

        for (int i = 0; i < nList2.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode2 = nList2.item(i);

            if (nNode2.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode2;

                //Csak a gy1 azonosítójú gyakornokot módosítjuk
                if (elem.getAttribute("gykod").equals("gy1")) {
                    elem.setAttribute("e_gy", "e3");
                }
            }
        }

        //c.) Az összes rendeles elem törlése
        NodeList nList3 = doc.getElementsByTagName("rendeles");

        //A NodeList élő lista, törléskor rövidül, ezért hátulról haladunk
        for (int i = nList3.getLength() - 1; i >= 0; i--) {

            //Lekérjük a lista aktuális elemét
            Node nNode3 = nList3.item(i);

            //A szülőelemből töröljük az aktuális elemet
            nNode3.getParentNode().removeChild(nNode3);
        }

        //Az üres (csak whitespace) szövegcsomópontok törlése, hogy a kiírás szépen formázott legyen
        removeWhitespaceNodes(doc.getDocumentElement());

        //A módosított XML kiíratása a konzolra Transformer segítségével
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        //Kódolás és behúzás beállítása
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult consoleResult = new StreamResult(System.out);
        transformer.transform(source, consoleResult);
    }

    //Rekurzívan végigmegy az elem gyerekein és törli a csak whitespace-t tartalmazó szövegcsomópontokat
    private static void removeWhitespaceNodes(Node node) {
        NodeList children = node.getChildNodes();

        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }
}
