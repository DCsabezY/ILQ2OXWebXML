# CLAUDE.md – ILQ2OXWebXML

Webes adatkezelő környezetek (Dr. Bednarik László), 2026 ősz. A repó a gyakorlatok anyagát (`ILQ2OX_MMDD/`) és a féléves beadandót (`ILQ2OX_XMLTask/`) tartalmazza.
Forrás: `BEADANDÓ/Feladatkiiras_Webes XML_2026 osz.pdf` és `BEADANDÓ/webes xml_jkminta_2026 osz.pdf`. Ha ez a fájl ellentmond nekik, a PDF-ek az irányadók.

## Rólam és a munkamód
- Deák Csaba, Neptun: ILQ2OX. Magyarul, tömören. Őszinte értékelést kérek, nem dicséretet.
- Commitolni én commitolok (GitHub Desktop).
- A bemutatáson el kell tudnom magyarázni a kódot, ezért:
  - **DOMRead:** együtt írjuk, és minden lépést elmagyarázol (`DocumentBuilder`, `NodeList`-bejárás, `Transformer`). A példákat a ILQ2OX_1021\vendeglatas mappába találod.
  - **DOMQuery, DOMModify:** én írom. Kérésre adsz tippet vagy egy kis részletet, de kész osztályt nem.
  - Kódjavításnál mondd el, mit és miért javítasz.
- Átnézés: egy-egy osztály kész állapotában `/code-review`, plusz egy ellenőrzés a lenti követelménylista alapján.

## Határidők
- Feltöltés GitHubra: 2026-12-01.
- Bemutatás: 2026-12-02 vagy 12-09 (a kiírásban elírásból 2025 szerepel).

## Szerkezet (`ILQ2OX_XMLTask/`)
| Fájl                                      | Állapot                                   |
| ----------------------------------------- | ----------------------------------------- |
| `ILQ2OX_ER.jpg`                           | kész; a szerkesztőfájl (.drawio) hiányzik |
| `ILQ2OX_XDM.drawio`                       | kész; a `ILQ2OX_XDM.jpg` export hiányzik  |
| `ILQ2OX_XML.xml`                          | kész (gyökér: `ILQ2OX_gabonatelep`)       |
| `ILQ2OX_XMLSchema.xsd`                    | kész                                      |
| `ILQ2OX_DOMParse/src/ilq2ox/domparse/hu/` | `ILQ2OX_DOMRead`: beolvasás kész; `ILQ2OX_DOMQuery`, `ILQ2OX_DOMModify`: üres |
| jegyzőkönyv (PDF)                         | nincs elkezdve                            |

- Téma: gabonatelep. Fő elemek: `Merlegjegy`, `Gazda`, `Vevo`, `Termeny`, `Raktar`, `KeszletMozgas`, `MinosegVizsgalat`. Kulcsok attribútumként (`*_ID`), az idegen kulcsok is.
- Package: `ilq2ox.domparse.hu` (a kiírás szerint). Osztálynevek: `ILQ2OX_DOMRead`, `ILQ2OX_DOMQuery`, `ILQ2OX_DOMModify`. Nincs `DOMWrite` osztály.
- Futtatás: Java 21 (Temurin), VS Code „DOMParse futtatása” konfiguráció. A `cwd` az `ILQ2OX_XMLTask`, ezért az XML relatív útvonala `ILQ2OX_XML.xml`.
- Minta a gyakorlatról: `ILQ2OX_1021/vendeglatas/` (DomParse, DomQuery, DomModify). Ebből a stílust lehet átvenni, a kódot nem.

## 2. feladat követelményei (JK-minta)
- **DOMRead:** a teljes dokumentum feldolgozása, kiírása a konzolra blokk formában, **és mentés fájlba**.
- **DOMQuery:** legalább 4 lekérdezés a példányokból, kiírás a konzolra. **XPath tilos**, csak DOM-bejárás (`getElementsByTagName`, `getAttribute`, `getChildNodes`).
- **DOMModify:** legalább 4 módosítás a példányokon, kiírás a konzolra.
- Mindhárom osztályban magyar megjegyzések kellenek, mert a jegyzőkönyvbe a „lényeges kód” megjegyzésekkel együtt kerül be.

## Jegyzőkönyv formai követelményei
Times New Roman vagy Arial. Hiperhivatkozott tartalomjegyzék. Címsor 1: 14 pt, Címsor 2: 12 pt, mindkettő félkövér és balra igazított. A szöveg sorkizárt, 1,5-es sorközzel. A képek számozva (`1. ábra: …`), és a szövegben hivatkozni kell rájuk. A kód szövegként kerül be, más betűtípussal, nem képként. A feladat leírása legalább fél oldal. Fejezetenként rövid leírás, kiemelt kódrészlet magyarázattal, és link a fájlra.
