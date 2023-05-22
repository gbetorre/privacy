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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFPage;

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
     * Imposta il formato della pagina
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
     * a partire dal suo nome, ricevuto come argomento
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
     * incremento, entrambi passati come parametri
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
     * 
     * @param content   la String da stampare
     * @param x         la coordinata orizzontale in cui iniziare il posizionamento orizzontale della String
     * @param y         la coordinata verticale relativa all'ultimo posizionamento prima del corrente; se non ci sono stampe precedenti, corrisponde all'inizializzazione della coordinata y
     * @param increment l'incremento cui sottoporre la coordinata verticale (y)
     * @param g         l'oggetto Graphics2D in cui impostare la stampa
     * @return <code>int</code> - il valore della coordinata y incrementata dell'incremento i: corrisponde al valore della coordinata verticale in base alla quale è stata stampata la stringa String
     */
    public static int println(String content, 
                              int x, 
                              int y, 
                              int increment, 
                              Graphics2D g) {
        int newY = y + increment;
        g.drawString(content, x, newY);
        return newY;
    }
    
    
    /**
     * Java wrap text in graphics2D
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

    
    //text = text.replaceAll("<br ?/?>", "\r\n");
    //text = text.replaceAll("<?/?strong>", VOID_STRING);
    public static String cleanHtml(String html) {
        String text = html.replaceAll("<p style=\"text-align: justify;\">", VOID_STRING)
                          .replaceAll("</p>", VOID_STRING)
                          .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", VOID_STRING)
                          .replaceAll("&ndash;", String.valueOf(HYPHEN))
                          .replaceAll("&rsquo;", String.valueOf(APOSTROPHE))
                          .replaceAll("&agrave;", "à")
                          .replaceAll("&egrave;", "è");
        return text;
    }
    
}
