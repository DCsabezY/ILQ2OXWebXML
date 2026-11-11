package ilq2ox.domparse.hu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ILQ2OX_DOMRead {

    public static void main(String[] args)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {

        // A beolvasandó XML fájl (az útvonal a repó gyökeréhez képest értendő, onnan futtatjuk)
        File xmlFile = new File("ILQ2OX_XMLTask/ILQ2OX_XML.xml");

        // A gyár (factory) hozza létre a feldolgozót, a beállításokat ezen lehet megadni
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // A DocumentBuilder végzi a tényleges elemzést
        DocumentBuilder builder = factory.newDocumentBuilder();

        // A fájl beolvasása és elemzése: az eredmény a teljes DOM-fa a memóriában
        Document doc = builder.parse(xmlFile);

        // Normalizálás: az egymás melletti szöveges csomópontokat összevonja.
        // A behúzásból maradt (csak szóközt/sortörést tartalmazó) szöveges csomópontok megmaradnak,
        // ezeket mentés előtt a removeWhitespaceNodes() törli.
        doc.getDocumentElement().normalize();

        // A gyökérelem lekérése és kiírása
        Element root = doc.getDocumentElement();
        System.out.println("Gyökérelem: " + root.getNodeName());

        // ---------------- Mérlegjegyek ----------------
        // A getElementsByTagName() a teljes dokumentumból visszaadja az adott nevű elemeket
        NodeList merlegjegyList = doc.getElementsByTagName("Merlegjegy");

        for (int i = 0; i < merlegjegyList.getLength(); i++) {

            // A lista aktuális eleme
            Node node = merlegjegyList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());

            // A getElementsByTagName() csak elemeket ad vissza, ez az ellenőrzés
            // biztonsági célú: garantálja, hogy az (Element) típuskényszerítés nem hibázhat
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                // A Node-ot Element-té alakítjuk, hogy elérjük az attribútumait
                Element elem = (Element) node;

                // Az elsődleges és az idegen kulcsok attribútumként szerepelnek
                String merlegjegyId = elem.getAttribute("Merlegjegy_ID");
                String termenyId = elem.getAttribute("Termeny_ID");
                String vevoId = elem.getAttribute("Vevo_ID");
                String gazdaId = elem.getAttribute("Gazda_ID");

                // A gyerekelemek szöveges tartalma
                String rendszam = getText(elem, "J_rendszam");
                String brutto = getText(elem, "Brutto_mazsa");
                String ures = getText(elem, "Ures_mazsa");
                String netto = getText(elem, "Netto_mazsa");
                String irany = getText(elem, "Irany");
                String datum = getText(elem, "Datum");

                // Kiírás blokk formában
                System.out.println("Mérlegjegy azonosító: " + merlegjegyId);
                System.out.println("Termény azonosító: " + termenyId);
                System.out.println("Vevő azonosító: " + vevoId);
                System.out.println("Gazda azonosító: " + gazdaId);
                System.out.println("Jármű rendszáma: " + rendszam);
                System.out.println("Bruttó tömeg: " + brutto + " mázsa");
                System.out.println("Üres tömeg: " + ures + " mázsa");
                System.out.println("Nettó tömeg: " + netto + " mázsa");
                System.out.println("Irány: " + irany);
                System.out.println("Dátum: " + datum);
            }
        }

        // ---------------- Gazdák ----------------
        NodeList gazdaList = doc.getElementsByTagName("Gazda");

        for (int i = 0; i < gazdaList.getLength(); i++) {

            Node node = gazdaList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) node;

                String gazdaId = elem.getAttribute("Gazda_ID");
                String adoszam = getText(elem, "Adoszam");
                String nev = getText(elem, "Nev");
                String lakcim = getText(elem, "Lakcim");

                System.out.println("Gazda azonosító: " + gazdaId);
                System.out.println("Adószám: " + adoszam);
                System.out.println("Név: " + nev);
                System.out.println("Lakcím: " + lakcim);

                // Az Elerhetoseg összetett elem, külön metódus írja ki
                printElerhetoseg(elem);
            }
        }

        // ---------------- Vevők ----------------
        NodeList vevoList = doc.getElementsByTagName("Vevo");

        for (int i = 0; i < vevoList.getLength(); i++) {

            Node node = vevoList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) node;

                String vevoId = elem.getAttribute("Vevo_ID");
                String nev = getText(elem, "Nev");
                String szallitasiCim = getText(elem, "SzallitasiCim");

                System.out.println("Vevő azonosító: " + vevoId);
                System.out.println("Név: " + nev);
                printElerhetoseg(elem);
                System.out.println("Szállítási cím: " + szallitasiCim);
            }
        }

        // ---------------- Raktárak ----------------
        NodeList raktarList = doc.getElementsByTagName("Raktar");

        for (int i = 0; i < raktarList.getLength(); i++) {

            Node node = raktarList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) node;

                String raktarId = elem.getAttribute("Raktar_ID");
                String nev = getText(elem, "Nev");
                String cim = getText(elem, "Cim");
                String kapacitas = getText(elem, "Kapacitas_mazsa");

                System.out.println("Raktár azonosító: " + raktarId);
                System.out.println("Név: " + nev);
                System.out.println("Cím: " + cim);
                System.out.println("Kapacitás: " + kapacitas + " mázsa");
            }
        }

        // ---------------- Készletmozgások ----------------
        NodeList mozgasList = doc.getElementsByTagName("KeszletMozgas");

        for (int i = 0; i < mozgasList.getLength(); i++) {

            Node node = mozgasList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) node;

                String mozgasId = elem.getAttribute("Mozgas_ID");
                String raktarId = elem.getAttribute("Raktar_ID");
                String merlegjegyId = elem.getAttribute("Merlegjegy_ID");
                String termenyId = elem.getAttribute("Termeny_ID");

                String irany = getText(elem, "Irany");
                String datum = getText(elem, "Datum");
                String mennyiseg = getText(elem, "Mennyiseg_mazsa");

                System.out.println("Mozgás azonosító: " + mozgasId);
                System.out.println("Raktár azonosító: " + raktarId);
                System.out.println("Mérlegjegy azonosító: " + merlegjegyId);
                System.out.println("Termény azonosító: " + termenyId);
                System.out.println("Irány: " + irany);
                System.out.println("Dátum: " + datum);
                System.out.println("Mennyiség: " + mennyiseg + " mázsa");
            }
        }

        // ---------------- Termények ----------------
        NodeList termenyList = doc.getElementsByTagName("Termeny");

        for (int i = 0; i < termenyList.getLength(); i++) {

            Node node = termenyList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) node;

                String termenyId = elem.getAttribute("Termeny_ID");
                String nev = getText(elem, "Nev");
                String fajta = getText(elem, "Fajta");

                System.out.println("Termény azonosító: " + termenyId);
                System.out.println("Név: " + nev);
                System.out.println("Fajta: " + fajta);
            }
        }

        // ---------------- Minőségvizsgálatok ----------------
        NodeList vizsgalatList = doc.getElementsByTagName("MinosegVizsgalat");

        for (int i = 0; i < vizsgalatList.getLength(); i++) {

            Node node = vizsgalatList.item(i);

            System.out.println("\nAktuális elem: " + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element elem = (Element) node;

                String vizsgalatId = elem.getAttribute("Vizsgalat_ID");
                String merlegjegyId = elem.getAttribute("Merlegjegy_ID");

                String nedvesseg = getText(elem, "Nedvesseg_szazalek");
                String idegenAnyag = getText(elem, "Idegen_anyag_szazalek");
                String datum = getText(elem, "Datum");
                String laborans = getText(elem, "Laborans_neve");
                String osztaly = getText(elem, "Minoseg_osztaly");

                System.out.println("Vizsgálat azonosító: " + vizsgalatId);
                System.out.println("Mérlegjegy azonosító: " + merlegjegyId);
                System.out.println("Nedvesség: " + nedvesseg + " %");
                System.out.println("Idegen anyag: " + idegenAnyag + " %");
                System.out.println("Dátum: " + datum);
                System.out.println("Laboráns neve: " + laborans);
                System.out.println("Minőségi osztály: " + osztaly);
            }
        }

        // ---------------- Mentés fájlba ----------------
        // A beolvasott DOM-fát a Transformer segítségével új XML fájlba írjuk
        saveToFile(doc, "ILQ2OX_XMLTask/ILQ2OX_XML_out.xml");
    }

    // Visszaadja az elem adott nevű első gyerekelemének szöveges tartalmát, a széleiről levágott
    // szóközökkel (az XSD whiteSpace=collapse miatt pl. " 200 " is érvényes érték lehet).
    // Ha nincs ilyen gyerekelem, üres szöveget ad vissza (így nem kapunk NullPointerException-t).
    // Nem private: a DOMQuery és a DOMModify is ezt használja (ugyanabban a csomagban vannak).
    public static String getText(Element elem, String tagName) {
        Node node = elem.getElementsByTagName(tagName).item(0);
        return node != null ? node.getTextContent().trim() : "";
    }

    // Kiírja egy Gazda vagy Vevo elem Elerhetoseg gyerekelemének tartalmát
    private static void printElerhetoseg(Element szulo) {

        // Az összetett elemet először Element-ként kérjük le, utána az ő gyerekeit olvassuk
        Element elerhetoseg = (Element) szulo.getElementsByTagName("Elerhetoseg").item(0);
        if (elerhetoseg == null) {
            return;
        }

        System.out.println("Telefonszám: " + getText(elerhetoseg, "TelefonSzam"));
        System.out.println("Faxszám: " + getText(elerhetoseg, "FaxSzam"));

        // Az EmailCim többértékű tulajdonság, ezért ciklussal fűzzük össze az értékeket
        NodeList emailList = elerhetoseg.getElementsByTagName("EmailCim");
        String emailek = "";
        for (int j = 0; j < emailList.getLength(); j++) {
            emailek += emailList.item(j).getTextContent();
            if (j < emailList.getLength() - 1) {
                emailek += ", ";
            }
        }
        System.out.println("E-mail cím(ek): " + emailek);
    }

    // Rekurzívan törli a csak szóközt/sortörést tartalmazó szöveges csomópontokat.
    // Ezek az eredeti fájl behúzásából maradtak; ha bent hagynánk őket, az INDENT
    // beállítás mellett a kimenetben üres sorok jelennének meg.
    private static void removeWhitespaceNodes(Node node) {
        NodeList children = node.getChildNodes();

        // Visszafelé haladunk, mert törléskor a lista indexei elcsúsznak
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes(child);
            }
        }
    }

    // A teljes dokumentumot kiírja a megadott nevű fájlba
    private static void saveToFile(Document doc, String fileName) throws TransformerException, IOException {

        // Az eredeti behúzás eltávolítása, hogy a Transformer újra egységesen behúzhassa
        removeWhitespaceNodes(doc.getDocumentElement());

        // A Transformer a DOM-fát (forrás) szöveges XML-lé alakítja (eredmény)
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Kimeneti beállítás: behúzás 4 szóközzel.
        // A kódolást (UTF-8) itt nem kell megadni: Writer használatakor azt
        // a lent létrehozott OutputStreamWriter határozza meg.
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // Az XML-deklarációt kézzel írjuk ki, mert a Transformer nem tesz utána sortörést
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // A try-with-resources a végén automatikusan lezárja a fájlt
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            // A sorvég a rendszerét követi (Windowson \r\n), ahogy a Transformer is
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());

            // A dokumentum legfelső szintű csomópontjait (megjegyzés + gyökérelem) egyenként
            // írjuk ki, így külön sorba kerülnek.
            // Forrás (DOMSource): a DOM-csomópont; eredmény (StreamResult): a fájlba író writer.
            NodeList topLevel = doc.getChildNodes();
            for (int i = 0; i < topLevel.getLength(); i++) {
                transformer.transform(new DOMSource(topLevel.item(i)), new StreamResult(writer));
            }
        }

        System.out.println("\nA dokumentum mentve: " + fileName);
    }
}
