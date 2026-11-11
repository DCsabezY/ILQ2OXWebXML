package ilq2ox.domparse.hu;

// A segédmetódusok a másik két osztályban vannak, statikus importtal hívjuk őket
import static ilq2ox.domparse.hu.ILQ2OX_DOMQuery.keresID;
import static ilq2ox.domparse.hu.ILQ2OX_DOMRead.getText;
import static ilq2ox.domparse.hu.ILQ2OX_DOMRead.removeWhitespaceNodes;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

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

public class ILQ2OX_DOMModify {

    public static void main(String[] args)
            throws SAXException, IOException, ParserConfigurationException, TransformerException {

        // A beolvasandó XML fájl (az útvonal a repó gyökeréhez képest értendő, onnan futtatjuk)
        File xmlFile = new File("ILQ2OX_XMLTask/ILQ2OX_XML.xml");

        // A gyár (factory) hozza létre a feldolgozót, a beállításokat ezen lehet megadni
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 
        // A DocumentBuilder végzi a tényleges elemzést
        DocumentBuilder builder = factory.newDocumentBuilder();

        // A fájl beolvasása és elemzése: az eredmény a teljes DOM-fa a memóriában.
        // Minden módosítás ezen a memóriabeli fán történik, az eredeti fájl változatlan marad.
        Document doc = builder.parse(xmlFile);

        // Normalizálás: az egymás melletti szöveges csomópontokat összevonja
        doc.getDocumentElement().normalize();

        // a.) Elem szöveges tartalmának módosítása
        System.out.println("a.) Az mj_02 mérlegjegy újramérése (új üres tömeg: 150 mázsa):");
        ujrameres(doc, "mj_02", 150);

        // b.) Új elem beszúrása
        System.out.println("\nb.) Új e-mail cím felvétele a g_01 gazdához:");
        ujEmail(doc, "g_01", "kovacs.janos@agrar.hu");

        // c.) Attribútum (idegen kulcs) módosítása
        System.out.println("\nc.) Az mj_03 kiszállítás vevőjének cseréje v_01-re:");
        vevoCsere(doc, "mj_03", "v_01");

        // d.) Elem törlése
        System.out.println("\nd.) A vevők faxszámának törlése:");
        faxTorles(doc);

        // A módosított dokumentum kiírása a konzolra
        System.out.println("\nA módosított dokumentum:");
        printDocument(doc);
    }

    // a.) A mérlegjegy üres tömegének átírása, a nettó tömeg újraszámolása,
    // és a mérlegjegyhez tartozó készletmozgás mennyiségének frissítése
    private static void ujrameres(Document doc, String merlegjegyId, int ujUres) {

        // A mérlegjegyet az elsődleges kulcsa (Merlegjegy_ID attribútum) alapján keressük meg
        Element merlegjegy = keresID(doc, "Merlegjegy", "Merlegjegy_ID", merlegjegyId);
        if (merlegjegy == null) {
            System.out.println("Hiba: nem található a mérlegjegy: " + merlegjegyId);
            return;
        }

        // A számoláshoz a szöveges tartalmat egész számmá alakítjuk
        int brutto = Integer.parseInt(getText(merlegjegy, "Brutto_mazsa"));
        String regiUres = getText(merlegjegy, "Ures_mazsa");
        String regiNetto = getText(merlegjegy, "Netto_mazsa");
        int ujNetto = brutto - ujUres;

        // Az XSD szerint a tömeg nem lehet negatív, ilyenkor nem módosítunk
        if (ujNetto < 0) {
            System.out.println("Hiba: az üres tömeg (" + ujUres + ") nagyobb a bruttónál (" + brutto + ")");
            return;
        }

        // A setTextContent() az elem teljes szöveges tartalmát lecseréli az új értékre
        merlegjegy.getElementsByTagName("Ures_mazsa").item(0).setTextContent(String.valueOf(ujUres));
        merlegjegy.getElementsByTagName("Netto_mazsa").item(0).setTextContent(String.valueOf(ujNetto));

        System.out.println("Mérlegjegy: " + merlegjegyId + ", Bruttó: " + brutto + " mázsa");
        System.out.println("Üres tömeg: " + regiUres + " -> " + ujUres + " mázsa");
        System.out.println("Nettó tömeg: " + regiNetto + " -> " + ujNetto + " mázsa");

        // A készletmozgás a Merlegjegy_ID idegen kulccsal hivatkozik a mérlegjegyre.
        // A mozgás mennyisége a nettó tömeg, ezért azt is frissíteni kell, különben ellentmondás lenne.
        NodeList mozgasList = doc.getElementsByTagName("KeszletMozgas");
        for (int i = 0; i < mozgasList.getLength(); i++) {
            Node node = mozgasList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element mozgas = (Element) node;
                if (mozgas.getAttribute("Merlegjegy_ID").equals(merlegjegyId)) {
                    String regiMennyiseg = getText(mozgas, "Mennyiseg_mazsa");
                    mozgas.getElementsByTagName("Mennyiseg_mazsa").item(0).setTextContent(String.valueOf(ujNetto));
                    System.out.println("Készletmozgás: " + mozgas.getAttribute("Mozgas_ID") + ", Mennyiség: "
                            + regiMennyiseg + " -> " + ujNetto + " mázsa");
                }
            }
        }
    }

    // b.) Új EmailCim elem hozzáadása egy gazda elérhetőségéhez (többértékű tulajdonság bővítése)
    private static void ujEmail(Document doc, String gazdaId, String email) {

        Element gazda = keresID(doc, "Gazda", "Gazda_ID", gazdaId);
        if (gazda == null) {
            System.out.println("Hiba: nem található a gazda: " + gazdaId);
            return;
        }

        // Az Elerhetoseg összetett elem, az e-mail címek ennek a gyerekei
        Element elerhetoseg = (Element) gazda.getElementsByTagName("Elerhetoseg").item(0);

        // Az új elemet a Document hozza létre; ekkor még nincs benne a fában
        Element ujElem = doc.createElement("EmailCim");
        ujElem.setTextContent(email);

        // Az appendChild() az utolsó gyerek után fűzi be az elemet. Az XSD-ben az EmailCim
        // a sorozat utolsó eleme, így a sorrend a beszúrás után is érvényes marad.
        elerhetoseg.appendChild(ujElem);

        System.out.println("Gazda: " + getText(gazda, "Nev") + " (" + gazdaId + ")");
        System.out.println("Új e-mail cím: " + email);
        System.out.println("E-mail címek száma: " + elerhetoseg.getElementsByTagName("EmailCim").getLength());
    }

    // c.) Egy mérlegjegy Vevo_ID idegen kulcsának átírása másik vevőre
    private static void vevoCsere(Document doc, String merlegjegyId, String ujVevoId) {

        Element merlegjegy = keresID(doc, "Merlegjegy", "Merlegjegy_ID", merlegjegyId);
        if (merlegjegy == null) {
            System.out.println("Hiba: nem található a mérlegjegy: " + merlegjegyId);
            return;
        }

        // Az idegen kulcs csak létező vevőre mutathat (az XSD keyref ezt ellenőrzi),
        // ezért módosítás előtt megnézzük, hogy van-e ilyen vevő
        Element ujVevo = keresID(doc, "Vevo", "Vevo_ID", ujVevoId);
        if (ujVevo == null) {
            System.out.println("Hiba: nem található a vevő: " + ujVevoId);
            return;
        }

        String regiVevoId = merlegjegy.getAttribute("Vevo_ID");

        // A setAttribute() a meglévő attribútum értékét felülírja (ha nem létezne, létrehozná)
        merlegjegy.setAttribute("Vevo_ID", ujVevoId);

        System.out.println("Mérlegjegy: " + merlegjegyId + ", Rendszám: " + getText(merlegjegy, "J_rendszam"));
        System.out.println("Vevo_ID: " + regiVevoId + " -> " + ujVevoId + " (" + getText(ujVevo, "Nev") + ")");
    }

    // d.) A FaxSzam elem törlése minden vevőtől (az XSD-ben minOccurs="0", így a törlés után is érvényes)
    private static void faxTorles(Document doc) {

        NodeList vevoList = doc.getElementsByTagName("Vevo");

        for (int i = 0; i < vevoList.getLength(); i++) {
            Node node = vevoList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element vevo = (Element) node;

                // A NodeList élő lista: törléskor rövidül, ezért hátulról haladunk
                NodeList faxList = vevo.getElementsByTagName("FaxSzam");
                for (int j = faxList.getLength() - 1; j >= 0; j--) {
                    Node fax = faxList.item(j);

                    // Egy elemet csak a szülője törölhet, ezért a getParentNode()-on hívjuk a removeChild()-ot
                    fax.getParentNode().removeChild(fax);
                    System.out.println("Vevő: " + getText(vevo, "Nev") + ", törölt faxszám: " + fax.getTextContent());
                }
            }
        }
    }

    // A teljes dokumentum kiírása a konzolra
    private static void printDocument(Document doc) throws TransformerException {

        // Az eredeti behúzás eltávolítása, hogy a Transformer újra egységesen behúzhassa
        // (így az újonnan beszúrt EmailCim is a helyes behúzással jelenik meg)
        removeWhitespaceNodes(doc.getDocumentElement());

        // A Transformer a DOM-fát (forrás) szöveges XML-lé alakítja (eredmény)
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Behúzás 4 szóközzel; az XML-deklarációt kézzel írjuk ki, mert a Transformer nem tesz utána sortörést
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // Először egy StringWriter-be írunk, és azt a println() írja ki: így a konzol saját
        // kódolását használja, és az ékezetes betűk helyesen jelennek meg
        StringWriter writer = new StringWriter();
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());

        // A legfelső szintű csomópontokat (megjegyzés + gyökérelem) egyenként írjuk ki,
        // így külön sorba kerülnek
        NodeList topLevel = doc.getChildNodes();
        for (int i = 0; i < topLevel.getLength(); i++) {
            transformer.transform(new DOMSource(topLevel.item(i)), new StreamResult(writer));
        }

        System.out.println(writer);
    }
}
