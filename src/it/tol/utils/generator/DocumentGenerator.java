/*
 *   Trattamenti On Line (tol): Applicazione web per la gestione del 
 *   registro delle attività di trattamento.
 *
 *   TOL:
 *   web application to manage and publish information about
 *   databases containing personal data, which are managed by subjects 
 *   belonging to the university or involving 
 *   the university as manager.
 *   Copyright (C) 2023 Giovanroberto Torre
 *   all right reserved
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA<br>
 *
 *   Giovanroberto Torre <gianroberto.torre@gmail.com>
 *   Universita' degli Studi di Verona
 *   Via Dell'Artigliere, 8
 *   37129 Verona (Italy)
 */

package it.tol.utils.generator;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import com.qoppa.pdfWriter.PDFDocument;

import it.tol.ConfigManager;
import it.tol.interfaces.Constants;


/**
 * Classe per generare documenti in formato Portable Document Format.
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DocumentGenerator implements Constants {

    /**
     * La serializzazione necessita dell'identificativo della versione seriale
     */
    private static final long serialVersionUID = 8277760460032134941L;
    /**
     * Larghezza pagina pdf
     */
    private static double paperWidth = 8.5;
    /**
     * Altezza pagina pdf
     */
    private static double paperHeight = 11.0;
    /**
     * Unit&agrave; di misura (in the PDF world, 1 point = 1/72 inch)
     */
    private static double paperUnit = 72.0;
    /**
     * Posizionamento orizzontale elementi da collocare in pagina pdf
     */
    protected static final int x = 80;
            

    /**
     * Restituisce un'istanza di PDFDocument, pronta per essere corredata
     * di pagine e testo.
     * 
     * @return <code>com.qoppa.pdfWriter.PDFDocument</code> - istanza di PDFDocument cui aggiungere pagine
     */
    public static PDFDocument getPDFDocument() {
        return new PDFDocument();
    }
    
    
    /**
     * Imposta il formato della pagina.
     * 
     * @return <code>PageFormat</code> - formato della pagina (altezza, larghezza, orientamento)
     */
    public static PageFormat getPageFormat() {
        Paper paper = new Paper();
        // Create a PageFormat of standard letter size with no margins
        paper.setSize(paperWidth * paperUnit, paperHeight * paperUnit);
        // Imageable area
        paper.setImageableArea(0, 0, paperWidth * paperUnit, paperHeight * paperUnit);
        // Create pageformat for the document
        PageFormat pf = new PageFormat();
        // Orientation
        pf.setOrientation(PageFormat.PORTRAIT);
        // Paper settings
        pf.setPaper(paper);
        // Create a document 
        return pf;
    }
    
    
    /**
     * Restituisce una qualunque immagine raster sotto forma di BufferedImage,
     * a partire dal suo nome, ricevuto come argomento.
     * 
     * @param imageName il nome dell'immagine raster da recuperare
     * @return <code>BufferedImage</code> - Oggetto che descrive un'immagine con un buffer accessibile di dati relativi all'immagine raster
     * @throws IOException se si verifica un problema nell'accesso alla risorsa da codificare
     */
    public static BufferedImage getImage(String imageName) 
                                  throws IOException {
        BufferedImage image = ImageIO.read(new File(ConfigManager.getDirImages() + imageName));
        return image;
    }
    
    
    /**
     * Restituisce una nuova coordinata a partire da una vecchia coordinata ed un 
     * incremento, entrambi passati come parametri.
     * 
     * @param coord     la coordinata originaria
     * @param increment l'incremento da sommare (algebricamente)
     * @return <code>int</code> - la coordinata calcolata a partire dalla vecchia coordinata e dall'incremento specificato
     */
    protected static int getCoordinate(int coord, 
                                       int increment) {
        return coord + increment;
    }

    
    /**
     * Stampa una riga di testo, ricevuta sotto forma di parametro di tipo String,
     * in un oggetto di tipo Graphics2D, che fornisce controllo fine su una serie di aspetti,
     * come geometria, coordinate, gestione del colore e impaginazione del testo, il tutto
     * finalizzato al rendering2-dimensionale di forme, testo e immagini; tale oggetto
     * viene passato come parametro e manipolato per riferimento. 
     * La pagina di un documento PDF pu&ograve; essere vista, infatti, come un piano 
     * (della geometria/algebra lineare) e in questo senso bisogna calcolare le coordinate
     * x e y per ogni posizionamento di testo si voglia fare nella pagina stessa.
     * @param graph2D   l'oggetto Graphics2D in cui impostare la stampa
     * @param content   la String da stampare
     * @param x1        la coordinata orizzontale in cui iniziare il posizionamento orizzontale della String
     * @param y1        la coordinata verticale relativa all'ultimo posizionamento prima del corrente; se non ci sono stampe precedenti, corrisponde all'inizializzazione della coordinata y
     * @param increment l'incremento cui sottoporre la coordinata verticale (y)
     * 
     * @return <code>int</code> - il valore della coordinata y incrementata dell'incremento i: corrisponde al valore della coordinata verticale in base alla quale è stata stampata la stringa String
     */
    public static int println(Graphics2D graph2D, 
                              String content, 
                              int x1, 
                              int y1, 
                              int increment) {
        int newY = y1 + increment;
        graph2D.drawString(content, x1, newY);
        return newY;
    }
    
    
    /**
     * Il metodo length() di String restituisce un valore espresso in unicode code units
     * Facciamo conto che la larghezza stampabile nella pagina sia di 80 colonne,
     * e quindi di 80 Unicode Code Units.
     * Siccome sappiamo che la larghezza stampabile è di 600 pixel,
     * ricaviamo il rapporto tra pixel e punti dividendo pixel / punti,
     * ovvero nel caso specifico 600 / 80 = 7.5
     * Quindi per calcolare la larghezza massima in Unicode Code Units
     * dobbiamo dividere width per 7.5 oppure moltiplicare content.length per 7.5 
     */
    public static int println(Graphics2D graph2D, 
                              String row, 
                              int x1, 
                              int y1, 
                              int increment, 
                              int width) {
        // Calcola la nuova ordinata (riferimento per la successiva riga)
        int y2 = y1 + increment;
        // Trasforma la larghezza dell'area stampabile (espressa in pixel) in Unicode Code Units
        double printableArea = (width / 7.5);
        // La stringa corrente corrisponde a una riga di testo: la misura (valore espresso in Unicode Code Units)
        double rowLength = row.length();
        // Cerca la fine della prima parola
        int position = row.indexOf(" ", NOTHING); int position2 = NOTHING;
        // Crea uno StringBuilder a partire dalla riga
        StringBuilder sb = new StringBuilder(row);
        
        String row3 = row;
        
        // Se la lunghezza della riga è minore della larghezza limite
        if (rowLength < printableArea) {
            
            // Aggiunge uno spazio extra dopo la parola
            sb.insert(position, " ");
            String row2 = sb.toString();
            // Ricalcola la lunghezza della riga modificata 
            rowLength = row2.length();
            // Misura un'altra volta la riga: se è ancora minore della larghezza
            while (rowLength < printableArea) {
                position += 3;
                // Cerca la fine della parola successiva
                position2 = row2.indexOf(" ", position);
                // Controlla il caso in cui il carattere non sia stato trovato
                if (position2 == DEFAULT_ID) {
                    break;
                }
                // Aggiunge uno spazio extra dopo la parola ‎successiva
                sb = new StringBuilder(row2);
                sb.insert(position2, "  ");
                row3 = sb.toString();
                // Se la stringa è finita esce
                if (position2 >= row.length()) {
                    break;
                }
                // Altrimenti prepara le variabili per rientrare
                position = position2;
                row2 = row3;
                // Misura un'altra volta la riga: etc. etc.
                rowLength = row3.length();
            }
            
        }
        
        // Agginge uno spazio extra dopo la seconda parola
        
        // ...e così via
        
       /*        StringBuffer content = new StringBuffer();
        int textRowWidth = textRow.length();
        
        if (textRowWidth*7 < width) {
            // Spezza la riga in parole
            String[] words = textRow.split(" ");
            for (int i = 0; i < words.length; i++) {
                content.append(words[i]).append(" ");
                if (String.valueOf(content).length()*7 < width) {
                    content.append("   ");
                }
                //textRow = String.valueOf(content);
                //textRowWidth = textRow.length();
                if (textRowWidth*7 > width)
                    break;
            }
             * StringTokenizer st = new StringTokenizer(textRow);
            for (int i = 1; i < st.countTokens() - 2; i++) {
               content.append(st.nextToken()).append(" ");
            }
        }*/
    
        graph2D.drawString(row3, x1, y2);
        return y2;
    }
    
    
    /**
     * Java wrap text in graphics2D.
     * 
     * @param graph2D       Grapics2D
     * @param textIn        text to wrap
     * @param textWidthArea horizontal width of the printable area
     * @return <code>String[]</code> - Array of String containig the wrapped text
     * @author Daniel Bigelow
     * @see <a href="https://danielbigelow.com/java-wraptext-in-graphics2d/">cfr</a>
     */
    public static String[] wrapText(Graphics2D graph2D, String textIn, int textWidthArea) {

        String[] myReturnString;

        int initialLength = (int) graph2D.getFontMetrics().getStringBounds(textIn, graph2D).getWidth();

        // If String is smaller than the area we have just return it;
        if (initialLength < textWidthArea) {
            myReturnString = new String[1];
            myReturnString[0] = textIn;
            return myReturnString;
        }

        ArrayList<String> returnLinesArray = new ArrayList<String>();
        StringBuilder stbld = new StringBuilder();
        int currentLineLength = 0;

        // for each of the words in textIn
        for (String st : textIn.split(" ")) {
            int currentwordlength = (int) graph2D.getFontMetrics().getStringBounds(st + " ", graph2D).getWidth();
            
            // is the current word shorter than the total text area?
            if (currentwordlength <= textWidthArea) {
                // if it fits add it to the next list
                if (currentLineLength + currentwordlength <= textWidthArea) {
                    stbld.append(st).append(" ");
                    currentLineLength += currentwordlength;
                } else {
                    
                    returnLinesArray.add(stbld.toString());
                    stbld = new StringBuilder();
                    stbld.append(st).append(" ");

                    currentLineLength = currentwordlength;
                }
            } else { 
                //our word is to long, lets break it up into two lines ( this is probably an edge case )
                char[] textInChars = st.toCharArray();
                // foreach char see if the next character will fit.
                for (char ch : textInChars) {
                    // get Character length
                    int charLength = graph2D.getFontMetrics().charWidth(ch);
                    //does the character fit in the next character spot?
                    if (currentLineLength + charLength <= textWidthArea) {
                        stbld.append(ch);
                        currentLineLength += charLength;
                    } else {
                        returnLinesArray.add(stbld.toString());
                        stbld = new StringBuilder();
                        stbld.append(ch);
                        currentLineLength = charLength;
                    }
                }
                //add space at end of the word
                stbld.append(" ");
            }

        }
        returnLinesArray.add(stbld.toString());
        myReturnString = new String[returnLinesArray.size()];
        int currentLine = 0;
        for (String st : returnLinesArray) {
            myReturnString[currentLine++] = st;
        }

        return myReturnString;
    }

    
    public static String cleanHtml(String html) {
        String text = html.replaceAll("<p style=\"text-align: justify;\">", VOID_STRING)
                          .replaceAll("</p>", VOID_STRING)
                          .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", VOID_STRING)
                          .replaceAll("&ndash;", String.valueOf(HYPHEN))
                          .replaceAll("&rsquo;", String.valueOf(APOSTROPHE))
                          .replaceAll("&ldquo;", "‟")
                          .replaceAll("&rdquo;", "”")
                          .replaceAll("&agrave;", "à")
                          .replaceAll("&egrave;", "è")
                          .replaceAll("&igrave;", "ì")
                          .replaceAll("&ograve;", "ò")
                          .replaceAll("&ugrave;", "ù");
        return text;
    }
    
}
