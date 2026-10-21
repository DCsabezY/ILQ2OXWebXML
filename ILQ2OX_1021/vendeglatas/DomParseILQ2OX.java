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

public class DomParseILQ2OX {
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

        //Kiíratjuk a dokumentum gyökérelemét
        System.out.println("Gyökérelem: " + doc.getDocumentElement().getNodeName());

        //A getElementsByTagName() metódus segítségével megkapjuk az etterem elem Nodelist-jét
        NodeList nList = doc.getElementsByTagName("etterem");

        //Végigmegyünk rajta egy for ciklussal
        for (int i = 0; i < nList.getLength(); i++) {
            
            //Lekérjük a lista aktuális elemét
            Node nNode = nList.item(i);

            System.out.println("\nAktuális elem: " + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode;

                //Lekérjük az aktuális elem attribútumait
                String ekod = elem.getAttribute("ekod");

                //Lekérjük az aktuális elem gyerekelemeinek tartalmát
                Node node1 = elem.getElementsByTagName("nev").item(0);
                String nev = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("varos").item(0);
                String city = node2.getTextContent();

                Node node3 = elem.getElementsByTagName("utca").item(0);
                String street = node3.getTextContent();

                Node node4 = elem.getElementsByTagName("hazszam").item(0);
                String houseNumber = node4.getTextContent();
                
                Node node6 = elem.getElementsByTagName("csillag").item(0);
                String stars = node6.getTextContent();

                //A cím gyerekelemeinek tartalmát összefűzzük

                String adr = city + ", " + street + " utca " + houseNumber + ".";

                //Formázva kiíratjuk a lekért adatokat
                System.out.println("Étterem ID: " + ekod);
                System.out.println("Étterem neve: " + nev);
                System.out.println("Étterem címe: " + adr);
                System.out.println("Étterem csillagok száma: " + stars);
            }
        }

        //Főszakács nodeList
        NodeList nList2 = doc.getElementsByTagName("foszakacs");

        for (int i = 0; i < nList2.getLength(); i++) {
            
            //Lekérjük a lista aktuális elemét
            Node nNode2 = nList2.item(i);

            System.out.println("\nAktuális elem: " + nNode2.getNodeName());
            if (nNode2.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode2;

                //Lekérjük az aktuális elem attribútumait
                String fkod = elem.getAttribute("fkod");
                String eid = elem.getAttribute("e_f");

                String work = "Ez a főszakacs a " +eid + "azonosítójú étteremben dolgozik.";

                //Lekérjük az aktuális elem gyerekelemeinek tartalmát
                Node node1 = elem.getElementsByTagName("nev").item(0);
                String nev = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("eletkor").item(0);
                String age = node2.getTextContent();

                Node node3;
                String edu = "";

                for (int j = 0; j < elem.getElementsByTagName("vegzettseg").getLength(); j++) {
                    node3 = elem.getElementsByTagName("vegzettseg").item(j);
                    if (j == elem.getElementsByTagName("vegzettseg").getLength()-1) {
                        edu += node3.getTextContent();
                    }else {
                        edu += node3.getTextContent() + ", ";
                    }
                }

                //Formázva kiíratjuk a lekért adatokat
                System.out.println("Főszakacs azonosító: " + fkod);
                System.out.println(work);
                System.out.println("Főszakacs neve: " + nev);
                System.out.println("Főszakacs életkora: " + age);
                System.out.println("Főszakacs végzettsége: " + edu);
            }
        }

        //Szakács nodeList
        NodeList nList3 = doc.getElementsByTagName("szakacs");

        for (int i = 0; i < nList3.getLength(); i++) {
            
            //Lekérjük a lista aktuális elemét
            Node nNode3 = nList3.item(i);

            System.out.println("\nAktuális elem: " + nNode3.getNodeName());
            if (nNode3.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode3;

                //Lekérjük az aktuális elem attribútumait
                String szkod = elem.getAttribute("szkod");
                String eid = elem.getAttribute("e_sz");

                String work = "Ez a szakacs a " +eid + " azonosítójú étteremben dolgozik.";

                //Lekérjük az aktuális elem gyerekelemeinek tartalmát
                Node node1 = elem.getElementsByTagName("nev").item(0);
                String nev = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("reszleg").item(0);
                String deparment = node2.getTextContent();

                Node node3;
                String edu = "";

                for (int j = 0; j < elem.getElementsByTagName("vegzettseg").getLength(); j++) {
                    node3 = elem.getElementsByTagName("vegzettseg").item(j);
                    if (j == elem.getElementsByTagName("vegzettseg").getLength()-1) {
                        edu += node3.getTextContent();
                    }else {
                        edu += node3.getTextContent() + ", ";
                    }
                }

                //Formázva kiíratjuk a lekért adatokat
                System.out.println("Szakacs azonosító: " + szkod);
                System.out.println(work);
                System.out.println("Szakacs neve: " + nev);
                System.out.println("Szakacs részlege: " + deparment);
                System.out.println("Szakacs végzettsége: " + edu);
            }
        }

        //Gyakornok nodeList
        NodeList nList4 = doc.getElementsByTagName("gyakornok");

        for (int i = 0; i < nList4.getLength(); i++) {
            
            //Lekérjük a lista aktuális elemét
            Node nNode4 = nList4.item(i);

            System.out.println("\nAktuális elem: " + nNode4.getNodeName());
            if (nNode4.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode4;

                //Lekérjük az aktuális elem attribútumait
                String gykod = elem.getAttribute("gykod");
                String eid = elem.getAttribute("e_gy");

                String work = "Ez a gyakornok a " +eid + " azonosítójú étteremben dolgozik.";

                //Lekérjük az aktuális elem gyerekelemeinek tartalmát
                Node node1 = elem.getElementsByTagName("nev").item(0);
                String nev = node1.getTextContent();

                //A gyakorlat összetett elem: először őt kérjük le, majd az ő gyerekeit
                Element gyakorlat = (Element) elem.getElementsByTagName("gyakorlat").item(0);
                String kezdete = gyakorlat.getElementsByTagName("kezdete").item(0).getTextContent();
                String idotartama = gyakorlat.getElementsByTagName("idotartama").item(0).getTextContent();

                //Több műszak lehet, ezért ciklus kell
                NodeList muszakList = elem.getElementsByTagName("muszak");
                String muszak = "";
                for (int j = 0; j < muszakList.getLength(); j++) {
                    muszak += muszakList.item(j).getTextContent();
                    if (j < muszakList.getLength() - 1) {
                        muszak += ", ";
                    }
                }

                System.out.println("Gyakornok azonosító: " + gykod);
                System.out.println(work);
                System.out.println("Gyakornok neve: " + nev);
                System.out.println("Gyakorlat kezdete: " + kezdete);
                System.out.println("Gyakorlat időtartama: " + idotartama);
                System.out.println("Műszak(ok): " + muszak);
            }
        }

        //Vendég nodeList
        NodeList nList5 = doc.getElementsByTagName("vendeg");

        for (int i = 0; i < nList5.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode5 = nList5.item(i);

            System.out.println("\nAktuális elem: " + nNode5.getNodeName());
            if (nNode5.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode5;

                //Lekérjük az aktuális elem attribútumait
                String vkod = elem.getAttribute("vkod");

                //Lekérjük az aktuális elem gyerekelemeinek tartalmát
                Node node1 = elem.getElementsByTagName("nev").item(0);
                String nev = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("eletkor").item(0);
                String age = node2.getTextContent();

                Node node3 = elem.getElementsByTagName("varos").item(0);
                String city = node3.getTextContent();

                Node node4 = elem.getElementsByTagName("utca").item(0);
                String street = node4.getTextContent();

                Node node5 = elem.getElementsByTagName("hazszam").item(0);
                String houseNumber = node5.getTextContent();

                //A cím gyerekelemeinek tartalmát összefűzzük
                String adr = city + ", " + street + " utca " + houseNumber + ".";

                //Formázva kiíratjuk a lekért adatokat
                System.out.println("Vendég azonosító: " + vkod);
                System.out.println("Vendég neve: " + nev);
                System.out.println("Vendég életkora: " + age);
                System.out.println("Vendég címe: " + adr);
            }
        }

        //Rendelés nodeList
        NodeList nList6 = doc.getElementsByTagName("rendeles");

        for (int i = 0; i < nList6.getLength(); i++) {

            //Lekérjük a lista aktuális elemét
            Node nNode6 = nList6.item(i);

            System.out.println("\nAktuális elem: " + nNode6.getNodeName());
            if (nNode6.getNodeType() == Node.ELEMENT_NODE) {

                //Az aktuális elem Elementté kovertálása
                Element elem = (Element) nNode6;

                //Lekérjük az aktuális elem attribútumait
                String vid = elem.getAttribute("e_v_v");
                String eid = elem.getAttribute("e_v_e");

                String order = "Ezt a rendelést a " + vid + " azonosítójú vendég adta le a " + eid + " azonosítójú étteremben.";

                //Lekérjük az aktuális elem gyerekelemeinek tartalmát
                Node node1 = elem.getElementsByTagName("osszeg").item(0);
                String sum = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("etel").item(0);
                String food = node2.getTextContent();

                //Formázva kiíratjuk a lekért adatokat
                System.out.println(order);
                System.out.println("Rendelés összege: " + sum + " Ft");
                System.out.println("Rendelt étel: " + food);
            }
        }
    }
}
