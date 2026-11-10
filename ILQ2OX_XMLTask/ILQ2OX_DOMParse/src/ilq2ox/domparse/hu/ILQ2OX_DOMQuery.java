package ilq2ox.domparse.hu;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

// A getText() segédmetódus az ILQ2OX_DOMRead osztályban van, statikus importtal hívjuk
import static ilq2ox.domparse.hu.ILQ2OX_DOMRead.getText;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ILQ2OX_DOMQuery {
    // kb. 14% fölött a gabona már nem tárolható biztonságosan, szárítani kell
    private static final double NEDVESSEG_HATAR = 14.0; 

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {

        // A beolvasandó XML fájl
        File xmlFile = new File("ILQ2OX_XMLTask/ILQ2OX_XML.xml");

        // A gyár (factory) hozza létre a feldolgozót, a beállításokat ezen lehet megadni
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // A DocumentBuilder végzi a tényleges elemzést
        DocumentBuilder builder = factory.newDocumentBuilder();

        // A fájl beolvasása és elemzése: az eredmény a teljes DOM-fa a memóriában
        Document doc = builder.parse(xmlFile);

        // Normalizálás: az egymás melletti szöveges csomópontokat összevonja.
        doc.getDocumentElement().normalize();

        // Beérkező szállítások kiírása
        System.out.println("a.) Beérkező szállítások:");
        beerkezoSzallitasok(doc);

        // Gazdák és vevők e-mail címei (többértékű elem)
        System.out.println("\nb.) Gazdák és vevők e-mail címei:");
        elerhetosegek(doc, "Gazda");
        elerhetosegek(doc, "Vevo");

        // Beérkező gyenge minőségű tételek gazdája (összekapcsolás)
        System.out.println("\nc.) Beérkező gyenge minőségű tételek gazdája:");
        minosegVizsgalat(doc);

        // Raktáronkénti aktuális készlet (összesítés)
        System.out.println("\nd.) Raktáronkénti aktuális készlet:");
        raktaronkentiKeszlet(doc);
    }

    // Keresés az XML-ben egy elemre, amelynek adott attribútuma adott értékű
    private static Element keresID(Document doc, String tagName, String attrName, String ertek) {
        NodeList nList = doc.getElementsByTagName(tagName);

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;
                if (elem.getAttribute(attrName).equals(ertek)) {
                    return elem;
                }
            }
        }
        return null; // Ha nincs találat
    }

    // Beérkező szállítások kiírása
    private static void beerkezoSzallitasok(Document doc) {
        NodeList nList = doc.getElementsByTagName("Merlegjegy");

        for (int i = 0; i < nList.getLength(); i++) {

            // Lekérjük a lista aktuális elemét
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                // Az aktuális elem Elementté konvertálása
                Element elem = (Element) nNode;

                String irany = getText(elem, "Irany");

                if (irany.equals("Be")) {
                    String rendszam = getText(elem, "J_rendszam");
                    String datum = getText(elem, "Datum");
                    // Csak kiírjuk, számolni nem kell vele, ezért szövegként hagyjuk
                    String mazsa = getText(elem, "Netto_mazsa");
                    System.out.println("Rendszám: " + rendszam + ", Dátum: " + datum + ", Nettó tömeg: " + mazsa + " mázsa");
                }
            }
        }
    }

    // A gazdák és vevők e-mail címeinek kiírása
    private static void elerhetosegek(Document doc, String tipus) {
        NodeList nList = doc.getElementsByTagName(tipus);

        for (int i = 0; i < nList.getLength(); i++) {

            // Lekérjük a lista aktuális elemét
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                // Az aktuális elem Elementté konvertálása
                Element elem = (Element) nNode;

                String nev = getText(elem, "Nev");
                System.out.println("\n" + tipus + " neve: " + nev);
                NodeList emailList = elem.getElementsByTagName("EmailCim"); 
                for (int j = 0; j < emailList.getLength(); j++) {
                    Node emailNode = emailList.item(j);
                    if (emailNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element emailElem = (Element) emailNode;
                        String emailCim = emailElem.getTextContent();
                        System.out.println("Email cím: " + emailCim);
                    }
                }
            }
        }
    }

    // Gyenge minőségű tételek gazdájának kiírása
    private static void minosegVizsgalat(Document doc) {
        NodeList nList = doc.getElementsByTagName("MinosegVizsgalat");

        for (int i = 0; i < nList.getLength(); i++) {

            // Lekérjük a lista aktuális elemét
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                // Az aktuális elem Elementté konvertálása
                Element elem = (Element) nNode;

                double nedvesseg = Double.parseDouble(getText(elem, "Nedvesseg_szazalek"));
                // Megnézzük, hogy a nedvesség meghaladja-e a határértéket
                if (nedvesseg > NEDVESSEG_HATAR) {
                    // Ha igen, akkor a mérlegjegy azonosítóját változóba mentjük
                    String merlegjegyID = elem.getAttribute("Merlegjegy_ID");
                    // 1. lépés: a vizsgálat idegen kulcsával megkeressük a mérlegjegyet
                    Element merlegjegyElem = keresID(doc, "Merlegjegy", "Merlegjegy_ID", merlegjegyID);
                    if (merlegjegyElem == null) {
                        System.out.println("Hiba: Nem található a mérlegjegy ID: " + merlegjegyID);
                        continue;
                    }
                    // Csak a beérkező (Be) szállítás gazdája felelős a minőségért;
                    // kiszállításnál (Ki) a tétel már a telepről megy a vevőhöz
                    if (!getText(merlegjegyElem, "Irany").equals("Be")) {
                        continue;
                    }
                    // 2. lépés: a mérlegjegy Gazda_ID-jével megkeressük a gazdát
                    String gazdaID = merlegjegyElem.getAttribute("Gazda_ID");
                    Element gazdaElem = keresID(doc, "Gazda", "Gazda_ID", gazdaID);
                    if (gazdaElem == null) {
                        System.out.println("Hiba: Nem található a gazda ID: " + gazdaID);
                        continue;
                    }
                    // Lekérjük a rendszámot a mérlegjegy eleméből
                    String rendszam = getText(merlegjegyElem, "J_rendszam");
                    // Lekérjük a gazda nevét a gazda eleméből
                    String gazdaNev = getText(gazdaElem, "Nev");
                    // Kiíratjuk a gyenge minőségű tétel adatait
                    System.out.println("Gyenge minőségű tétel: Merlegjegy_ID=" + merlegjegyID + ", Rendszám: " + rendszam + ", Gazda neve: " + gazdaNev + ", Nedvesség: " + nedvesseg + "%");
                }
            }
        }
    }

    // Raktáronkénti aktuális készlet kiírása
    private static void raktaronkentiKeszlet(Document doc) {
        // Lekérjük a 2 listát
        NodeList raktarList = doc.getElementsByTagName("Raktar");
        NodeList mozgasList = doc.getElementsByTagName("KeszletMozgas");

        for (int i = 0; i < raktarList.getLength(); i++) {
            Node raktarNode = raktarList.item(i);
            if (raktarNode.getNodeType() == Node.ELEMENT_NODE) {
                Element raktarElem = (Element) raktarNode;
                String raktarID = raktarElem.getAttribute("Raktar_ID");
                String raktarNev = getText(raktarElem, "Nev");
                int kapacitas = Integer.parseInt(getText(raktarElem, "Kapacitas_mazsa"));

                // A belső ciklus előtt hozzuk létre az aktuális készlet változót.
                int aktualisKeszlet = 0;

                // Belső ciklus: az összes mozgás közül az ehhez a raktárhoz tartozókat összegezzük
                for (int j = 0; j < mozgasList.getLength(); j++) {
                    Node mozgasNode = mozgasList.item(j);
                    if (mozgasNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element mozgasElem = (Element) mozgasNode;
                        String mozgasRaktarID = mozgasElem.getAttribute("Raktar_ID");
                        if (mozgasRaktarID.equals(raktarID)) {
                            int mennyiseg = Integer.parseInt(getText(mozgasElem, "Mennyiseg_mazsa"));
                            String irany = getText(mozgasElem, "Irany");
                            if (irany.equals("Be")) {
                                aktualisKeszlet += mennyiseg;
                            } else if (irany.equals("Ki")) {
                                aktualisKeszlet -= mennyiseg;
                            } else {
                                // Ismeretlen irány: nem számoljuk bele, de jelezzük
                                System.out.println("Figyelmeztetés: ismeretlen irány (" + irany + ") a(z) "
                                        + mozgasElem.getAttribute("Mozgas_ID") + " mozgásnál");
                            }
                        }
                    }
                }

                // 0 kapacitásnál nem osztunk (az XSD szerint a 0 is érvényes érték)
                String telitettseg;
                if (kapacitas > 0) {
                    // Double kaszt, hogy ne egész osztás legyen; a Locale.ROOT miatt
                    // mindig tizedespontot ír ki, magyar beállítású gépen is
                    telitettseg = String.format(Locale.ROOT, "%.2f", (double) aktualisKeszlet / kapacitas * 100) + "%";
                } else {
                    telitettseg = "nem számolható (0 kapacitás)";
                }

                System.out.println("Raktár: " + raktarNev + ", Aktuális készlet: " + aktualisKeszlet + " / " + kapacitas + ", Telítettség: " + telitettseg);
            }
        }
    }
}