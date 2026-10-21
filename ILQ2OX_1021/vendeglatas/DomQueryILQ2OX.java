package ILQ2OX_1021.vendeglatas;
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

public class DomQueryILQ2OX {
    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {

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

        //a.) Azok a szakácsok, akiknek a végzettségei között van szakközépiskola
        System.out.println("a.) Szakközépiskolai végzettségű szakácsok:");
        NodeList nList = doc.getElementsByTagName("szakacs");
        int talalat = 0;

        for (int i = 0; i < nList.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode;

                //Több végzettség lehet, ezért ciklussal nézzük végig
                NodeList vegzettsegList = elem.getElementsByTagName("vegzettseg");
                boolean vanSzakkozep = false;
                for (int j = 0; j < vegzettsegList.getLength(); j++) {
                    if (vegzettsegList.item(j).getTextContent().equalsIgnoreCase("Szakközépiskola")) {
                        vanSzakkozep = true;
                    }
                }

                if (vanSzakkozep) {
                    String szkod = elem.getAttribute("szkod");
                    String nev = elem.getElementsByTagName("nev").item(0).getTextContent();
                    String reszleg = elem.getElementsByTagName("reszleg").item(0).getTextContent();

                    //Formázva kiíratjuk a lekért adatokat
                    System.out.println("\nSzakacs azonosító: " + szkod);
                    System.out.println("Szakacs neve: " + nev);
                    System.out.println("Szakacs részlege: " + reszleg);
                    talalat++;
                }
            }
        }
        if (talalat == 0) {
            System.out.println("Nincs ilyen szakács.");
        }

        //b.) Azok az éttermek, amelyek öt csillagosak
        System.out.println("\nb.) Öt csillagos éttermek:");
        NodeList nList2 = doc.getElementsByTagName("etterem");
        talalat = 0;

        for (int i = 0; i < nList2.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode2 = nList2.item(i);

            if (nNode2.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode2;

                String csillag = elem.getElementsByTagName("csillag").item(0).getTextContent();

                if (csillag.equals("5")) {
                    String ekod = elem.getAttribute("ekod");
                    String nev = elem.getElementsByTagName("nev").item(0).getTextContent();
                    String city = elem.getElementsByTagName("varos").item(0).getTextContent();
                    String street = elem.getElementsByTagName("utca").item(0).getTextContent();
                    String houseNumber = elem.getElementsByTagName("hazszam").item(0).getTextContent();

                    //A cím gyerekelemeinek tartalmát összefűzzük
                    String adr = city + ", " + street + " utca " + houseNumber + ".";

                    //Formázva kiíratjuk a lekért adatokat
                    System.out.println("\nÉtterem ID: " + ekod);
                    System.out.println("Étterem neve: " + nev);
                    System.out.println("Étterem címe: " + adr);
                    System.out.println("Étterem csillagok száma: " + csillag);
                    talalat++;
                }
            }
        }
        if (talalat == 0) {
            System.out.println("Nincs ilyen étterem.");
        }

        //c.) Azok a gyakornokok, akik be vannak osztva délutánra
        System.out.println("\nc.) Délutánra beosztott gyakornokok:");
        NodeList nList3 = doc.getElementsByTagName("gyakornok");
        talalat = 0;

        for (int i = 0; i < nList3.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode3 = nList3.item(i);

            if (nNode3.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode3;

                //Több műszak lehet, ezért ciklussal nézzük végig
                NodeList muszakList = elem.getElementsByTagName("muszak");
                boolean vanDelutan = false;
                for (int j = 0; j < muszakList.getLength(); j++) {
                    if (muszakList.item(j).getTextContent().equalsIgnoreCase("Délután")) {
                        vanDelutan = true;
                    }
                }

                if (vanDelutan) {
                    String gykod = elem.getAttribute("gykod");
                    String eid = elem.getAttribute("e_gy");
                    String nev = elem.getElementsByTagName("nev").item(0).getTextContent();

                    //Formázva kiíratjuk a lekért adatokat
                    System.out.println("\nGyakornok azonosító: " + gykod);
                    System.out.println("Ez a gyakornok a " + eid + " azonosítójú étteremben dolgozik.");
                    System.out.println("Gyakornok neve: " + nev);
                    talalat++;
                }
            }
        }
        if (talalat == 0) {
            System.out.println("Nincs ilyen gyakornok.");
        }
    }
}
