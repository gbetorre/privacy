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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
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
     * Il metodo length() di String restituisce un valore espresso in 
     * Unicode Code Units, cio&egrave; in colonne, non in pixel. 
     * Per comparare questo valore restituito con un valore in pixel, 
     * bisogna moltiplicare il valore restituito per un fattore fisso, 
     * derivante dal rapporto tra pixel e Unicode Code Units.
     * Quindi per calcolare una larghezza in Unicode Code Units
     * bisogna dividere width in pixel per 7.5 mentre per 
     * ottenere una larghezza in pixel bisogna moltiplicare 
     * String.length() per 7.5 
     */
    private static final double UNICODE_CODE_UNITS_FACTOR = 7.5;
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
     * Margine sinistro pagina pdf
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
     * Restituisce una nuova coordinata a partire da una vecchia coordinata 
     * ed un incremento, entrambi passati come parametri.
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
     * in un oggetto di tipo Graphics2D, che fornisce controllo fine su una serie 
     * di aspetti, come geometria, coordinate, gestione del colore e impaginazione 
     * del testo, il tutto finalizzato al rendering2-dimensionale di forme, testo 
     * e immagini; tale oggetto viene passato come parametro e manipolato per 
     * riferimento. 
     * La pagina di un documento PDF pu&ograve; essere vista, infatti, come un 
     * piano (della geometria/algebra lineare) e in questo senso bisogna calcolare 
     * le coordinate x e y per ogni posizionamento di testo si voglia fare 
     * nella pagina stessa.
     * 
     * @param graph2D   l'oggetto Graphics2D in cui impostare la stampa
     * @param line      la String corrispondente alla riga di testo da stampare
     * @param x1        la coordinata orizzontale in cui iniziare il posizionamento orizzontale della String
     * @param y1        la coordinata verticale relativa all'ultimo posizionamento prima del corrente; se non ci sono stampe precedenti, corrisponde all'inizializzazione della coordinata y
     * @param increment l'incremento cui sottoporre la coordinata verticale (y)
     * @return <code>int</code> - il valore della coordinata y incrementata dell'incremento i: corrisponde al valore della coordinata verticale in base alla quale è stata stampata la stringa String
     */
    public static int println(Graphics2D graph2D, 
                              String line, 
                              int x1, 
                              int y1, 
                              int increment) {
        int y2 = y1 + increment;
        graph2D.drawString(line, x1, y2);
        return y2;
    }

    
    /**
     * Stampa un'area di testo ricevuta sotto forma di parametro di tipo String,
     * in un oggetto di tipo TextLayout, tramite un oggetto di tipo Graphics2D
     * passato come parametro e manipolato per riferimento.
     * 
     * @param graph2D   l'oggetto Graphics2D da usare per la stampa
     * @param text      la String corrispondente all'area di testo da stampare
     * @param x1        la coordinata orizzontale in cui iniziare il posizionamento orizzontale della String
     * @param y1        la coordinata verticale relativa all'ultimo posizionamento prima del corrente; se non ci sono stampe precedenti, corrisponde all'inizializzazione della coordinata y
     * @param increment l'incremento cui sottoporre la coordinata verticale (y)
     * @param width     la larghezza dell'area stampabile, in pixel
     * @return <code>int</code> - il valore della coordinata y incrementata dell'incremento i: corrisponde al valore della coordinata verticale in base alla quale è stata stampata la stringa String
     */
    public static void print(Graphics2D graph2D, 
                            String text, 
                            int x1, 
                            int y1, 
                            int increment, 
                            int width) {
        // Incapsula la coordinata verticale
        float y2 = y1;
        // Imposta l'anti-alias
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Recupera il font
        Font font = graph2D.getFont();
        // Crea una stringa con attributi di formattazione
        AttributedString as = new AttributedString(text);
        // Imposta il font
        as.addAttribute(TextAttribute.FONT, font);
        // Imposta l'allineamento del testo
        as.addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
        // Iteratore per testo e relativi attributi
        AttributedCharacterIterator aci = as.getIterator();
        // Misuratore di testo
        FontRenderContext frc = graph2D.getFontRenderContext();
        // Misuratore di linea
        LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc); 
        // Produce l'output
        while (lbm.getPosition() < aci.getEndIndex()) { 
            TextLayout textLayout = lbm.nextLayout(width); 
            //y2 += textLayout.getAscent();
            TextLayout justifiedLayout = textLayout.getJustifiedLayout(width);
            y2 += increment;
            justifiedLayout.draw(graph2D, x1, y2); 
            y2 += textLayout.getDescent() + textLayout.getLeading();  
        }
    }
    
    
    /**
     * Stampa un'area di testo ricevuta sotto forma di parametro di tipo String,
     * in un oggetto di tipo TextLayout, tramite un oggetto di tipo Graphics2D
     * passato come parametro e manipolato per riferimento.
     * 
     * @param graph2D   l'oggetto Graphics2D da usare per la stampa
     * @param text      la String corrispondente all'area di testo da stampare
     * @param x1        la coordinata orizzontale in cui iniziare il posizionamento orizzontale della String
     * @param y1        la coordinata verticale relativa all'ultimo posizionamento prima del corrente; se non ci sono stampe precedenti, corrisponde all'inizializzazione della coordinata y
     * @param increment l'incremento cui sottoporre la coordinata verticale (y)
     * @param width     la larghezza dell'area stampabile, in pixel
     * @return <code>int</code> - il valore della coordinata y incrementata dell'incremento i: corrisponde al valore della coordinata verticale in base alla quale è stata stampata la stringa String
     */
    public static String print(Graphics2D graph2D, 
                            String text, 
                            int x1, 
                            int y1, 
                            int increment, 
                            int width,
                            int boundary) {
        // Variabili locali
        String textIn = null;
        String textOut = null;
        // Incapsula la coordinata verticale
        float y2 = y1; 
        // Recupera la lunghezza del testo
        int length = text.length();
        // Se il testo è troppo lungo per stare nella pagina, ne prende una parte
        if (length > boundary) {
            textIn = truncateTo(text, boundary);
            textOut = truncateFrom(text, textIn.length());
        } else {
            textIn = text;
        }
        // Imposta l'anti-alias
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Recupera il font
        Font font = graph2D.getFont();
        // Crea una stringa con attributi di formattazione
        AttributedString as = new AttributedString(textIn);
        // Imposta il font
        as.addAttribute(TextAttribute.FONT, font);
        // Imposta l'allineamento del testo
        as.addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
        // Iteratore per testo e relativi attributi
        AttributedCharacterIterator aci = as.getIterator();
        // Misuratore di testo
        FontRenderContext frc = graph2D.getFontRenderContext();
        // Misuratore di linea
        LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc); 
        // Produce l'output
        while (lbm.getPosition() < aci.getEndIndex()) { 
            TextLayout textLayout = lbm.nextLayout(width); 
            //y2 += textLayout.getAscent();
            TextLayout justifiedLayout = textLayout.getJustifiedLayout(width);
            y2 += increment;
            justifiedLayout.draw(graph2D, x1, y2); 
            y2 += textLayout.getDescent() + textLayout.getLeading();  
        }  
        return textOut;
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
                          .replaceAll("<li style=\"text-align: justify;\">", " ●")
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
    
    
    public static String truncateTo(final String content, 
                                    final int lastIndex) {
        String result = content.substring(NOTHING, lastIndex);
        if (content.charAt(lastIndex) != BLANK_SPACE) {
            return result.substring(NOTHING, result.lastIndexOf(BLANK_STRING));
        }
        return result;
    }
    
    
    public static String truncateFrom(final String content, 
                                      final int fromIndex) {
        int start = fromIndex;
        String result = content.substring(fromIndex);
        while (content.charAt(start) != BLANK_SPACE) {
            start--;
            result = result.substring(start, content.length());
        }
        return result.trim();
    }
    
}
