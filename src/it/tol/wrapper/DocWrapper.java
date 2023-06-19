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

package it.tol.wrapper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.qoppa.pdfWriter.PDFDocument;
import com.qoppa.pdfWriter.PDFPage;

import it.tol.Data;
import it.tol.bean.ActivityBean;
import it.tol.bean.CodeBean;
import it.tol.bean.ItemBean;
import it.tol.bean.ProcessBean;
import it.tol.bean.ProcessingBean;
import it.tol.exception.AttributoNonValorizzatoException;
import it.tol.exception.CommandException;
import it.tol.utils.generator.DocumentGenerator;


/**
 * Classe per creare contenuti in documenti in formato Portable Document Format.
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DocWrapper extends DocumentGenerator {
    
    /**
     * La serializzazione necessita dell'identificativo della versione seriale
     */
    private static final long serialVersionUID = -436181615623568181L;
    /**
     * Nome di questa classe
     * (utilizzato per contestualizzare i messaggi di errore)
     */
    private static final String FOR_NAME = "\n" + Logger.getLogger(Data.class.getName()) + ": ";
    /**
     * Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.
     */
    private static Logger log = Logger.getLogger(Data.class.getName());
    /**
     * Costante per immagine di intestazione
     */
    private static final String LOGO = "logo1.png";
    /**
     * Costante per la lunghezza del paragrafo
     */
    private static final int TEXT_LIMIT = 600;
    
    
    /**
     * Restituisce il logo codificato sotto forma di BufferedImage.
     * 
     * @return <code>BufferedImage</code> - Oggetto che descrive un'immagine con un buffer accessibile di dati relativi all'immagine raster
     * @throws IOException se si verifica un problema nell'accesso alla risorsa da codificare
     */
    public static BufferedImage getLogo() 
                                 throws IOException {
        return getImage(LOGO);
    }
    
    
    /**
     * Genera la stampa dell'immagine del logo in un Graphics2D passato come parametro
     * e manipolato per riferimento.
     * 
     * @param g             l'oggetto Graphics2D in cui impostare la stampa
     * @throws IOException  se si verifica un problema nell'accesso alla risorsa da codificare
     */
    protected static void printLogo(Graphics2D g) 
                             throws IOException {
        int xH = 10;                        // Coordinata orizzontale di inizio header
        int yH = 10;                        // Coordinata verticale di inizio header
        BufferedImage image = getLogo();    // Recupera il logo sotto forma di BufferedImage
        g.drawImage(image, xH, yH, 209, 75, null);  // Disegna il logo
    }
    
    
    /**
     * Genera una pagina corrispondente al frontespizio del documento pdf
     * e la aggiunge a un'istanza di un documento pdf, passata come parametro.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @throws IOException se si verifica un problema nel puntamento a una risorsa (immagine)
     */
    public static void makeFrontPage(PageFormat pf, 
                                     PDFDocument doc)
                              throws IOException {
        PDFPage front = doc.createPage(pf);
        // Create Graphics2D
        Graphics2D g2d = front.createGraphics(); 
        BufferedImage image = getLogo();
        g2d.drawImage(image, 10, 10, 209, 75, null);
        g2d.setFont(new Font ("Helvetica", Font.BOLD, 26));
        g2d.drawString("Registro delle attività di trattamento", x, 400);
        g2d.setFont(new Font ("Helvetica", Font.ITALIC, 18));
        g2d.drawString("(Regolamento UE 2016/679, art. 30)", 150, 430);
        g2d.setFont(new Font ("Helvetica", Font.PLAIN, 12));
        g2d.drawString("Registro delle attività di trattamento – Vers. 3.0 – 30.06.2023", x, 700);
        doc.addPage(front);
    }
    

    /**
     * Genera una pagina corrispondente alla prima pagina interna del documento pdf
     * passato come parametro, formattata sulla base del PageFormat passato come parametro.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @throws IOException se si verifica un problema nel puntamento a una risorsa (immagine)
     */
    public static void makeFirstPage(PageFormat pf, 
                                     PDFDocument doc)
                              throws IOException {
        int y = 140;
        int i = 20;
        int w = 12;
        int h = 12;
        PDFPage page = doc.createPage(pf);
        // Create Graphics2D
        Graphics2D g2d = page.createGraphics();
        // Content
        printLogo(g2d);
        g2d.setFont(new Font("Helvetica", Font.BOLD, 14));
        g2d.drawString("1. Ambito di applicazione", x, y);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        y = println(g2d, "L’art. 30 del Regolamento UE n. 2016/679 (a seguire: GDPR) prevede che le imprese od", x, y, i);
        y = println(g2d, "organizzazioni con un numero uguale o superiore a 250 dipendenti devono adottare e tenere", x, y, i);
        y = println(g2d, "aggiornato un", x, y, i);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        println(g2d, "Registro delle Attività di Trattamento.", 150, y, NOTHING);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        y = println(g2d, "Tale obbligo non si applica alle pubbliche amministrazioni, salvo che i trattamenti che esse", x, y, i);
        y = println(g2d, "effettuano possano presentare un rischio per i diritti e le libertà dell’interessato, non siano", x, y, i);
        y = println(g2d, "occasionali o includano categorie particolari di dati (GDPR, art. 9, par. 1: dati che rivelino", x, y, i);
        y = println(g2d, "l’origine razziale o etnica, le opinioni politiche, le convinzioni religiose o filosofiche,", x, y, i);
        y = println(g2d, "o l’appartenenza sindacale, dati genetici, dati biometrici intesi a identificare in modo univoco una", x, y, i);
        y = println(g2d, "persona fisica, dati relativi alla salute o alla vita sessuale o all’orientamento sessuale della", x, y, i);
        y = println(g2d, "persona) o dati personali relativi a condanne penali.", x, y, i);
        y = println(g2d, "Preso atto che l'Università di Verona:", x, y, i*2);
        // <ul>
        BufferedImage bullet = getImage("ico-check.png");
        // li
        g2d.drawImage(bullet, x, 390, w, h, null);
        int x1 = getCoordinate(x, i);
        y = println(g2d, "ha più di 250 dipendenti;", x1, y, i);
        // li
        g2d.drawImage(bullet, x, 410, w, h, null);
        y = println(g2d, "effettua trattamenti che presentano rischio per i diritti e le libertà dell’interessato;", x1, y, i);
        // li
        g2d.drawImage(bullet, x, 430, w, h, null);
        y = println(g2d, "effettua trattamenti che includono le categorie particolari di dati di cui al GDPR, art. 9, par. 1;", x1, y, i);
        // li
        g2d.drawImage(bullet, x, 450, w, h, null);
        y = println(g2d, "effettua trattamenti che riguardano dati personali relativi a condanne penali,", x1, y, i);
        // </ul>
        y = println(g2d, "l'Università è tenuta ad adottare un Registro delle Attività di Trattamento per fornire ai terzi", x, y, i*2);
        y = println(g2d, "evidenza dell’analisi dei Trattamenti effettuati, motivando le misure intraprese sulla scorta dei rischi", x, y, i);
        y = println(g2d, "gravanti su di essi, secondo il principio di Responsabilizzazione o ", x, y, i);
        g2d.setFont(new Font ("Arial", Font.ITALIC, 11));
        g2d.drawString("Accountability.", getCoordinate(x, 320), y);
        doc.addPage(page);
    }

    
    /**
     * Produce una pagina corrispondente alla seconda pagina interna del documento pdf
     * passato come parametro, formattata sulla base del PageFormat passato come parametro.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @throws IOException se si verifica un problema nel puntamento a una risorsa (immagine)
     */
    public static void makeSecondPage(PageFormat pf, 
                                      PDFDocument doc)
                               throws IOException {
        int y = 140;
        int i = 20;
        // Create a page in the document
        PDFPage page = doc.createPage(pf);
        // Create Graphics2D
        Graphics2D g2d = page.createGraphics();
        // Content
        printLogo(g2d);
        // Title
        g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
        g2d.drawString("2. Dati di Contatto", x, y);
        // Text
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        y = println(g2d, "In merito al trattamento dei dati effettuato come Titolare e/o come Responsabile, è necessario", x, y, i);
        y = println(g2d, "specificare i dati di contatto:", x, y, i);
        g2d.setFont(new Font ("Arial", Font.BOLD, 11));
        y = println(g2d, "GDPR, art. 30 1a) il nome e i dati di contatto del titolare del trattamento e, ove applicabile,", x, y, i);
        y = println(g2d, "del contitolare del trattamento, del rappresentante del titolare del trattamento e del", x, y, i);
        y = println(g2d, "responsabile della protezione dei dati;", x, y, i); 
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        y = println(g2d, "Qualora il titolare svolga trattamenti anche in qualità di Responsabile si applica:", x, y, i*2);
        g2d.setFont(new Font ("Arial", Font.BOLD, 11));
        y = println(g2d, "GDPR, art. 30 2a) il nome e i dati di contatto del responsabile o dei responsabili del", x, y, i);
        y = println(g2d, "trattamento, di ogni titolare del trattamento per conto del quale agisce il responsabile del", x, y, i);
        y = println(g2d, "trattamento, del rappresentante del titolare del trattamento o del responsabile del", x, y, i);
        y = println(g2d, "trattamento e, ove applicabile, del responsabile della protezione dei dati.", x, y, i);
        // Make a rectangle 
        g2d.drawRoundRect(x, 398, 450, 152, 2, 2);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(82, 400, 225, i);
        g2d.fillRect(82, 400, 225, i);
        g2d.setColor(Color.black);
        g2d.drawString("FIGURA", 178, 415);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(302, 400, 226, i);
        g2d.fillRect(302, 400, 226, i);
        g2d.setColor(Color.black);
        g2d.drawString("DATI DI CONTATTO", 324, 415);
        y = println(g2d, "Titolare del Trattamento", getCoordinate(x, i), y, i*4);
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        y = println(g2d, "Università degli Studi di Verona", getCoordinate(x, i*11), y, NOTHING);
        y = println(g2d, "Via dell’Artigliere n. 8", getCoordinate(x, i*11), y, i);
        g2d.setFont(new Font ("Arial", Font.ITALIC, 11));
        y = println(g2d, "Nota: contatti validi anche qualora", getCoordinate(x, i), y, i);
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        y = println(g2d, "CAP 37129   - Verona", getCoordinate(x, i*11), y, NOTHING);
        y = println(g2d, "C.F: 93009870234 – P.I. 0154104023", getCoordinate(x, i*11), y, i);
        g2d.setFont(new Font ("Arial", Font.ITALIC, 11));
        g2d.drawString("il titolare agisca come Responsabile", getCoordinate(x, i), y);
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        y = println(g2d, "Tel. 0458028777", getCoordinate(x, i*11), y, i);
        y = println(g2d, "E-mail: privacy@ateneo.univr.it", getCoordinate(x, i*11), y, i);
        y += i/2;
        g2d.drawRoundRect(x, y, 450, 155, 2, 2);
        g2d.setFont(new Font ("Arial", Font.BOLD, 11));
        y = println(g2d, "Responsabile della Protezione", getCoordinate(x, i), y, i);
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        g2d.drawString("GL CONSULTING S.R.L.", getCoordinate(x, i*11), y);
        g2d.setFont(new Font ("Arial", Font.BOLD, 11));
        y = println(g2d, "dei Dati (DPO)", getCoordinate(x, i), y, i);
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        g2d.drawString("Soggetto individuato quale referente", getCoordinate(x, i*11), y);
        y = println(g2d, " per il titolare:", getCoordinate(x, i*11), y, i);
        y = println(g2d, "Gianluca Lombardi", getCoordinate(x, i*11), y, i);
        y = println(g2d, "Tel. 0312242323 / 3482345012", getCoordinate(x, i*11), y, i);
        y = println(g2d, "E-mail: info@gianlucalombardi.com", getCoordinate(x, i*11), y, i);
        println(g2d, "PEC: gianluca.lombardi@ingpec.eu", getCoordinate(x, i*11), y, i);
        doc.addPage(page);
    }
    
    
    /**
     * Produce una pagina intermedia dinamica.
     * 
     * @param pf            formato della pagina
     * @param doc           istanza di documento PDF
     * @param sectionTitle  titolo della pagina intermedia
     * @param sectionName   qualifica identificante la tipologia di dati mostrati nelle pagine fino alla prossima pagina intermedia
     * @throws IOException  se si verifica un problema nel puntamento a una risorsa (immagine)
     */
    public static void makeIntermediatePage(PageFormat pf, 
                                            PDFDocument doc,
                                            String sectionTitle,
                                            String sectionName)
                                     throws IOException {
        // Create a page in the document
        PDFPage page = doc.createPage(pf);
        // Create Graphics2D
        Graphics2D g2d = page.createGraphics(); 
        printLogo(g2d);
        g2d.setFont(new Font ("Helvetica", Font.BOLD, 18));
        g2d.drawString(sectionTitle, x, 300);
        g2d.setFont(new Font ("Helvetica", Font.PLAIN, 14));
        g2d.drawString("L’organizzazione effettua i seguenti trattamenti di dati in qualità di:", x, 330);
        g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
        g2d.drawString(sectionName, x + 190, 360);
        doc.addPage(page);
    }
 
    
    /**
     * Produce l'intestazione della prima pagina di un trattamento.
     * 
     * @param g     l'oggetto Graphics2D in cui impostare la stampa
     * @param t     l'oggetto contenente i dati del trattamento
     * @throws IOException se si verifica un problema nel puntamento a una risorsa (immagine)
     * @throws AttributoNonValorizzatoException se si verifica un problema nel recupero di un attributo obbligatorio del bean
     */
    public static void makeHeaderProcessingPage(Graphics2D g,
                                                ProcessingBean t)
                                         throws IOException, 
                                                AttributoNonValorizzatoException {
        int y = 85;
        int s = 8;
        int width = 600;
        // Logo
        printLogo(g);
        // Draw a coloured rectangle
        g.setColor(Color.BLUE);
        g.fillRect(60, y, 500, 70);
        // Draw a round border to the rectangle
        g.setStroke(new BasicStroke(6));
        // Draw a string
        g.setFont(new Font ("Helvetica", Font.BOLD, 14));
        g.setColor(Color.white);
        y = println(g, "Codice" + BLANK_SPACE + COLON + BLANK_SPACE + t.getCodice(), x, y, s*3);
        y += s;
        String[] titleAsArray = wrapText(g, t.getNome(), width);
        for (int i = 0; i < titleAsArray.length; i++) {
            y = println(g, titleAsArray[i], x, y, s*2);
        }
    }
    
    
    /**
     * Produce l'intestazione di una pagina interna di un trattamento.
     * 
     * @param g     l'oggetto Graphics2D in cui impostare la stampa
     * @param t     l'oggetto contenente i dati del trattamento
     * @return <code>int</code> - il valore della coordinata verticale raggiunta
     * @throws IOException se si verifica un problema nel puntamento a una risorsa (immagine)
     * @throws AttributoNonValorizzatoException se si verifica un problema nel recupero di un attributo obbligatorio del bean
     */
    public static int makeHeaderInternalPage(Graphics2D g,
                                             ProcessingBean t)
                                      throws IOException, 
                                             AttributoNonValorizzatoException {
        int y = 85;
        int s = 8;
        int width = 450;
        // Logo
        printLogo(g);
        // Draw a coloured rectangle
        g.setColor(Color.BLUE);
        g.drawRoundRect(60, y, 500, 70, 2, 2);
        // Draw a round border to the rectangle
        g.setStroke(new BasicStroke(6));
        // Draw a string
        y = println(g, "Codice" + BLANK_SPACE + COLON + BLANK_SPACE + t.getCodice(), x, y, s*3);
        y += s;
        String[] titleAsArray = wrapText(g, t.getNome(), width);
        for (int i = 0; i < titleAsArray.length; i++) {
            y = println(g, titleAsArray[i], x, y, s*2);
        }
        return y;
    }
    
    
    /**
     * Genera la prima pagina di un trattamento passato come parametro.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @return <code>int</code> - l'indice corrente delle attivit&agrave; di trattamento stampate nella prima pagina, o il numero totale di esse se la stampa non ha ecceduto l'altezza di pagina
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static int makeProcessingPage(PageFormat pf, 
                                          PDFDocument doc,
                                          ProcessingBean t)
                                   throws CommandException {
        int y = 140;
        int s = 8;
        int pageHeight = 720;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics();
            // Make the heder of the first processing page
            makeHeaderProcessingPage(g2d, t);
            g2d.setColor(Color.black);
            // Descrizione sintetica del trattamento
            if (!t.getDescrizione().equals(VOID_STRING) && t.getDescrizione().length() < TEXT_LIMIT) {
                y = 180;
                g2d.drawString("Descrizione sintetica del trattamento", x, y);
                // Draw to the page
                String text = cleanHtml(t.getDescrizione());
                String[] textAsArray = wrapText(g2d, text, 700);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                for (int i = 0; i < textAsArray.length; i++) {
                    y = println(g2d, textAsArray[i], x, y, s*2);
                }
            }
            // Attività di trattamento
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            y += s*5;
            g2d.drawString("Attività di trattamento" + BLANK_SPACE + "(" + t.getAttivita().size() + ")", x, y);
            // Horizontal rule
            float[] dashingPattern1 = {1f, 1f};
            Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);
            g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
            int count = NOTHING;
            ArrayList<ActivityBean> vA = t.getAttivita();
            for (int i = 0; i < vA.size(); i++) {
                ActivityBean a = vA.get(i);
                String text = a.getNome();
                String[] textAsArray = wrapText(g2d, text, 540);
                y += s;
                g2d.setStroke(stroke1);
                g2d.drawLine(x, y, 550, y);
                for (int j = 0; j < textAsArray.length; j++) {
                    y += s*2;
                    if (y < pageHeight) {
                        g2d.drawString(textAsArray[j], x, y);
                    } else {
                        count = i;
                        break;
                    }
                }
                if (y > pageHeight) {
                    doc.addPage(page);
                    return count;
                }
            }
            // Add the page to the document
            doc.addPage(page);
            return count;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /**
     * Genera la descrizione di un trattamento, passato come parametro, in una pagina separata.
     * 
     * @param pf        formato della pagina
     * @param doc       istanza di documento PDF
     * @param t         oggetto contenente i dati del trattamento
     * @param content   testo da includere in pagine di descrizione del trattamento successive alla prima
     * @return <code>String</code> - il testo rimanente, se la descrizione del trattamento non sta in una sola pagina
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static String makeProcessingPage(PageFormat pf, 
                                             PDFDocument doc,
                                             ProcessingBean t,
                                             String content)
                                      throws CommandException {
        int y = 180;
        int s = 8;
        String leftOver = null; 
        int boundary = 2000;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics();
            // If this method is called, this isn't the first processing page
            makeHeaderInternalPage(g2d, t);
            g2d.setColor(Color.black);
            if (content == null) {
                // Descrizione sintetica del trattamento
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                g2d.drawString("Descrizione sintetica del trattamento", x, y);
                // Draw to the page
                String text = cleanHtml(t.getDescrizione());
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                leftOver = print(g2d, text, x, y, s*2, 460, boundary);
            } else {
                // Altrimenti, se invece riceve testo, deve stampare solo quello
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                print(g2d, content, x, y, s*2, 460);
            }
            // Add the page to the document
            doc.addPage(page);
            return leftOver;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /**
     * Assunto che si verifichi il caso in cui la lista delle attivit&agrave; 
     * fosse co&iacute; lunga da non stare in una pagina, aggiunge un'ulteriore pagina 
     * generata dal ciclo sulla lista delle attivit&agrave; del trattamento corrente, 
     * passato come parametro.
     * Sa da quale attivt&agrave; di trattamento partire perch&eacute; si basa sul
     * numero d'indice dell'attivit&agrave; stessa, passato come parametro. 
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @param count numero d'indice dell'attivita' da cui partire per scorrere le attivita'
     * @return <code>int</code> - l'indice corrente delle attivit&agrave; di trattamento stampate nella prima pagina, o il numero totale di esse se la stampa non ha ecceduto l'altezza di pagina
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static int makeActivitiesPage(PageFormat pf, 
                                          PDFDocument doc,
                                          ProcessingBean t,
                                          int count)
                                   throws CommandException {
        int y = 85;
        int s = 8;
        int pageHeight = 700;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the internal page
            y = makeHeaderInternalPage(g2d, t);
            // Draw to the page
            g2d.setColor(Color.black);
            y += s*4;
            // Riga orizzontale
            float[] dashingPattern1 = {1f, 1f};
            Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);
            int count2 = NOTHING;
            ArrayList<ActivityBean> vA = t.getAttivita();
            for (int i = count; i < vA.size(); i++) {
                ActivityBean a = vA.get(i);
                String text = a.getNome();
                String[] textAsArray = wrapText(g2d, text, 540);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                y += s;
                g2d.setStroke(stroke1);
                g2d.drawLine(x, y, 550, y);
                for (int j = 0; j < textAsArray.length; j++) {
                    y += s*2;
                    if (y < pageHeight) {
                        g2d.drawString(textAsArray[j], x, y);
                    } else {
                        count2 = i;
                        break;
                    }
                }
                if (y > pageHeight) {
                    doc.addPage(page);
                    return count2;
                }
            }
            // Add the page to the document
            doc.addPage(page);
            return count2;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
        
    
    /**
     * Genera la pagina delle basi giuridiche di un trattamento passato come parametro.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @return <code>int</code> - l'indice corrente delle basi giuridiche di trattamento stampate nella pagina, o il numero totale di esse se la stampa non ha ecceduto l'altezza di pagina
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static int makeLegalBasisPage(PageFormat pf, 
                                          PDFDocument doc,
                                          ProcessingBean t)
                                   throws CommandException {
        int y = 180;
        int s = 8;
        int pageHeight = 720;
        int count = NOTHING;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Draw a string
            g2d.setColor(Color.black);
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            g2d.drawString("Descrizione delle finalità perseguite", x, y);
            // Draw to the page
            String text = cleanHtml(t.getFinalita());
            String[] textAsArray = wrapText(g2d, text, 700);
            g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
            for (int i = 0; i < textAsArray.length; i++) {
                y = println(g2d, textAsArray[i], x, y, s*2);
            }
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            y = println(g2d, "Basi giuridiche" + BLANK_SPACE + "(" + t.getBasiGiuridiche().size() + ")", x, y, s*5);
            // Draw to the page
            float[] dashingPattern1 = {1f, 1f};
            Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);
            ArrayList<ActivityBean> vB = t.getBasiGiuridiche();
            for (int i = 0; i < vB.size(); i++) {
                ActivityBean base = vB.get(i);
                textAsArray = wrapText(g2d, base.getNome(), 540);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                y += s;
                g2d.setStroke(stroke1);
                g2d.drawLine(x, y, 550, y);
                for (int j = 0; j < textAsArray.length; j++) {
                    y += s*2;
                    if (y < pageHeight) {
                        g2d.drawString(textAsArray[j], x, y);
                    } else {
                        count = i;
                        break;
                    }
                }
                y += s*2;
                if (base.getCodice().equals("C")) {
                    g2d.drawString("(DATI COMUNI)", x, y);
                } else if (base.getCodice().equals("P")) {
                    g2d.drawString("(DATI PARTICOLARI)", x, y);
                }
                g2d.setFont(new Font ("Arial", Font.ITALIC, 11));
                y = println(g2d, base.getDescrizione(), x, y, s*2);
                if (base.getInformativa() != null && !base.getInformativa().equals(VOID_STRING)) {
                    String[] informativa = wrapText(g2d, base.getInformativa(), 450);
                    for (int j = 0; j < informativa.length; j++) {
                        y = println(g2d, informativa[j], x, y, s*2);
                    }
                }
            }
            // Add the page to the document
            doc.addPage(page);
            return count;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
 
    
    /**
     * Stampa un tipo di dato trattato e il relativo check.
     * 
     * @param g         l'oggetto Graphics2D in cui impostare la stampa
     * @param flag      valore boolean specificante se il tipo di dato viene trattato
     * @param stroke    forma per decorazioni
     * @param label     etichetta specificante il tipo di dato trattato
     * @param posY      coordinata verticale per il posizionamento delle stampe sul piano
     * @return <code>int</code> - il valore della coordinata verticale raggiunta a seguito delle stampe prodotte dal metodo
     * @throws NullPointerException se label e' null (implicita)
     */
    public static int printKindOfData(Graphics2D g,
                                      boolean flag,
                                      Stroke stroke,
                                      String label,
                                      int posY) {
        int y = posY;
        int s = 8;
        g.setStroke(stroke);
        g.drawLine(x, y, 550, y);
        y = println(g, label, x, y, s*2);
        String check = (flag ? "[X]" : "[ ]") ;
        g.drawString(check, 524, y);
        return y;
    }
    
    
    /**
     * Restituisce il numero di tipologie di dati trattati nel contesto
     * di uno specifico trattamento dati, passato come parametro.
     * 
     * @param t     il trattamento dati di cui contare le tipologie trattate 
     * @return <code>int</code> - il numero di tipologie dati trovate nel trattamento corrente
     */
    public static int countKindOfData(ProcessingBean t) {
        int check = t.isDatiPersonali() ? 1 : 0; 
        check += t.isDatiSanitari() ? 1 : 0; 
        check += t.isDatiOrientamentoSex() ? 1 : 0; 
        check += t.isDatiEtniaReligApp() ? 1 : 0; 
        check += t.isDatiMinoreEta() ? 1 : 0; 
        check += t.isDatiGenetici() ? 1 : 0;  
        check += t.isDatiBiometrici() ? 1 : 0; 
        check += t.isDatiGiudiziari() ? 1 : 0; 
        check += t.isDatiUbicazione() ? 1 : 0; 
        check += t.isDatiPseudonimizzati() ? 1 : 0; 
        check += t.isDatiAnonimizzati() ? 1 : 0; 
        return check;
    }
    
    
    /**
     * Genera la pagina di descrizione delle categorie di interessati e delle
     * categorie di dati personali di un trattamento passato come parametro.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @return <code>int</code> - l'indice corrente delle attivit&agrave; di trattamento stampate nella prima pagina, o il numero totale di esse se la stampa non ha ecceduto l'altezza di pagina
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static int makeKindOfDataPage(PageFormat pf, 
                                          PDFDocument doc,
                                          ProcessingBean t)
                                   throws CommandException {
        int y = 180;
        int s = 8;
        int count = NOTHING;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Draw a string            
            g2d.setColor(Color.black);
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            g2d.drawString("Descrizione delle categorie di interessati" + BLANK_SPACE + "(" + t.getInteressati().size() + ")", x, y);
            // Draw to the page
            float[] dashingPattern1 = {1f, 1f};
            Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);
            ArrayList<CodeBean> vSubj = t.getInteressati();
            for (int i = 0; i < vSubj.size(); i++) {
                CodeBean subj = vSubj.get(i);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                y += s;
                g2d.setStroke(stroke1);
                g2d.drawLine(x, y, 550, y);
                y = println(g2d, subj.getNome() + " (" + subj.getInformativa() + ")", x, y, s*2);
                //g2d.setFont(new Font ("Arial", Font.ITALIC, 11));
                //y = println("(" + subj.getInformativa() + ")", x, y, s*2, g2d);
            }
            // Draw a string
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            y = println(g2d, "Descrizione delle categorie di dati personali (" + countKindOfData(t) + ")", x, y, s*5);
            // Draw to the page
            g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            y = printKindOfData(g2d, t.isDatiPersonali(), stroke1, "Dati comuni", y + s);
            y = printKindOfData(g2d, t.isDatiSanitari(), stroke1, "Dati sanitari", y + s);
            y = printKindOfData(g2d, t.isDatiOrientamentoSex(), stroke1, "Dati relativi all\'orientamento sessuale", y + s);
            y = printKindOfData(g2d, t.isDatiEtniaReligApp(), stroke1, "Dati relativi ad etnia, religione o appartenenza associativa", y + s);
            y = printKindOfData(g2d, t.isDatiMinoreEta(), stroke1, "Dati relativi a soggetti minorenni", y + s);
            y = printKindOfData(g2d, t.isDatiGenetici(), stroke1, "Dati relativi ad aspetti genetici", y + s);            
            y = printKindOfData(g2d, t.isDatiBiometrici(), stroke1, "Dati biometrici", y + s);
            y = printKindOfData(g2d, t.isDatiGiudiziari(), stroke1, "Dati giudiziari", y + s);
            y = printKindOfData(g2d, t.isDatiUbicazione(), stroke1, "Dati relativi all\'ubicazione dei soggetti", y + s);
            y = printKindOfData(g2d, t.isDatiPseudonimizzati(), stroke1, "Dati pseudonimizzati", y + s);
            y = printKindOfData(g2d, t.isDatiAnonimizzati(), stroke1, "Dati anonimizzati", y + s);
            // Add the page to the document
            doc.addPage(page);
            return count;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
 
    
    /**
     * Genera la pagina di descrizione delle categorie di destinatari 
     * e dei termini ultimi per la cancellazione.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @return <code>int</code> - il valore raggiunto nella coordinata verticale (y)
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static boolean makeExpireTimePage(PageFormat pf, 
                                              PDFDocument doc,
                                              ProcessingBean t)
                                       throws CommandException {
        int y = 180;
        int s = 8;
        boolean alreadyPrinted = false;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Draw a string            
            g2d.setColor(Color.black);
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            g2d.drawString("Categorie di destinatari a cui i dati vengono comunicati:", x, y);
            String text = cleanHtml(t.getExtraInfos().getExtraInfo3());
            String[] destAsArray = text.split(String.valueOf(HYPHEN));
            g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
            float[] dashingPattern1 = {1f, 1f};
            Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);
            for (int i = 0; i < destAsArray.length; i++) {
                if (!destAsArray[i].equals(VOID_STRING)) {
                    y += s;
                    g2d.setStroke(stroke1);
                    g2d.drawLine(x, y, 550, y);
                    if (destAsArray[i].length() > 70) {
                        String[] splittedRow = wrapText(g2d, destAsArray[i], 450);
                        y = println(g2d, "►" + splittedRow[0], x, y, s*2);
                        for (int j = 1; j < splittedRow.length; j++) {
                            y = println(g2d, splittedRow[j], x, y, s*2);
                        }
                    } else {
                        y = println(g2d, "►" + destAsArray[i], x, y, s*2);
                    }
                }
            }
            // Draw a string
            if (y < TEXT_LIMIT) {
                alreadyPrinted = true;
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                y = println(g2d, "Termini ultimi previsti per la cancellazione:", x, y, s*5);
                String[] textAsArray = wrapText(g2d, cleanHtml(t.getTerminiUltimi()), 710);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                for (int i = 0; i < textAsArray.length; i++) {
                    y = println(g2d, textAsArray[i], x, y, s*2);
                }
            }
            // Add the page to the document
            doc.addPage(page);
            return alreadyPrinted;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /**
     * Genera la pagina di descrizione delle categorie di destinatari 
     * e dei termini ultimi per la cancellazione.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @return <code>int</code> - il valore raggiunto nella coordinata verticale (y)
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static int makeExpireTimeAsPage(PageFormat pf, 
                                            PDFDocument doc,
                                            ProcessingBean t)
                                     throws CommandException {
        int y = 180;
        int s = 8;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Draw a string            
            g2d.setColor(Color.black);
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            y = println(g2d, "Termini ultimi previsti per la cancellazione:", x, y, s);
            String[] textAsArray = wrapText(g2d, cleanHtml(t.getTerminiUltimi()), 710);
            g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
            for (int i = 0; i < textAsArray.length; i++) {
                y = println(g2d, textAsArray[i], x, y, s*2);
            }
            // Add the page to the document
            doc.addPage(page);
            return y;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }

    
    /**
     * Genera la pagina di descrizione delle misure di sicurezza tecniche ed organizzative.
     * 
     * @param pf        formato della pagina
     * @param doc       istanza di documento PDF
     * @param t         oggetto contenente i dati del trattamento
     * @param content   testo da mandare in stampa nella pagina [facoltativo]
     * @return <code>String</code> - testo avanzato, se la pagina non e' stata sufficiente a contenerlo
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static String makeSecMeasurePage(PageFormat pf, 
                                             PDFDocument doc,
                                             ProcessingBean t, 
                                             String content)
                                      throws CommandException {
        int y = 180;
        int s = 8;
        int boundary = 2000;
        String leftOver = null;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Set colour
            g2d.setColor(Color.black);
            // Se non riceve testo, deve stampare anche il titolo
            if (content == null) {
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                g2d.drawString("Descrizione generale delle misure di sicurezza tecniche ed organizzative:", x, y);
                String text = cleanHtml(t.getExtraInfos().getExtraInfo1());
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                leftOver = print(g2d, text, x, y, s*2, 460, boundary);
                //JLabel label = new JLabel();
                //JTextArea htmlTextArea = new JTextArea(10, 20);
                //htmlTextArea.setText("<html><u>some text</u></html>");
                //label.setText(htmlTextArea.getText());
                //g2d.translate(x, y);
                //label.setText("<html><u>some text</u></html>");
                    //the fontMetrics stringWidth and height can be replaced by
                    //getLabel().getPreferredSize() if needed
                    //label.paint(g2d);
                    //Graphics g = label.getGraphics();
                    //g.drawString("<html><u>some text</u></html>", x, y);
                    //g2d.translate(-x, -y);
            } else {
            // Altrimenti, se invece riceve testo, deve stampare solo quello
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                print(g2d, content, x, y, s*2, 460, boundary);
            }
            // Add the page to the document
            doc.addPage(page);
            return leftOver;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /*
     * Genera la pagina di descrizione del trattamento (p.es. i dettagli
     * dell'accordo di contitolarit&agrave;).
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     *
    private static String makeProcessingDescription(PageFormat pf, 
                                                    PDFDocument doc,
                                                    ProcessingBean t, 
                                                    String content)
                                                            throws CommandException {
        int y = 180;
        int s = 8;
        int boundary = 2000;
        String leftOver = null;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Set colour
            g2d.setColor(Color.black);
            // Se non riceve testo, deve stampare anche il titolo
            if (content == null) {
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                g2d.drawString("Descrizione generale delle misure di sicurezza tecniche ed organizzative:", x, y);
                String text = cleanHtml(t.getExtraInfos().getExtraInfo1());
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                leftOver = print(g2d, text, x, y, s*2, 460, boundary);
                //JLabel label = new JLabel();
                //JTextArea htmlTextArea = new JTextArea(10, 20);
                //htmlTextArea.setText("<html><u>some text</u></html>");
                //label.setText(htmlTextArea.getText());
                //g2d.translate(x, y);
                //label.setText("<html><u>some text</u></html>");
                    //the fontMetrics stringWidth and height can be replaced by
                    //getLabel().getPreferredSize() if needed
                    //label.paint(g2d);
                    //Graphics g = label.getGraphics();
                    //g.drawString("<html><u>some text</u></html>", x, y);
                    //g2d.translate(-x, -y);
            } else {
            // Altrimenti, se invece riceve testo, deve stampare solo quello
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                print(g2d, content, x, y, s*2, 460, boundary);
            }
            // Add the page to the document
            doc.addPage(page);
            return leftOver;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }*/

    
    /**
     * Genera la pagina dei luoghi di custodia e dei supporti di memorizzazione.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @param count numero di database di trattamento stampati nella pagina, oppure zero se la stampa non ha ecceduto l'altezza di pagina
     * @return <code>int[]</code> - array contenente sia l'indice corrente dei database di trattamento stampate nella pagina, sia la coordinata verticale
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static int[] makeDBLocationPage(PageFormat pf, 
                                            PDFDocument doc,
                                            ProcessingBean t,
                                            int count)
                                     throws CommandException {
        int y = 180;
        int s = 8;
        int pageHeight = 720;
        int dbIndex = NOTHING;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Draw a string
            g2d.setColor(Color.black);
            if (count == NOTHING) {
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                g2d.drawString("Luoghi di custodia dei supporti di memorizzazione:", x, y);
                // Draw to the page
                String text = cleanHtml(t.getExtraInfos().getExtraInfo2());
                String[] textAsArray = wrapText(g2d, text, 700);
                //String[] textAsArray = wrapText(g2d, t.getExtraInfos().getExtraInfo2(), 740);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                for (int i = 0; i < textAsArray.length; i++) {
                    y = println(g2d, textAsArray[i], x, y, s*2);
                }
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                y = println(g2d, "Database" + BLANK_SPACE + "(" + t.getBancheDati().size() + ")", x, y, s*5);
            }
            // Draw to the page
            float[] dashingPattern1 = {1f, 1f};
            Stroke stroke1 = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);
            ArrayList<ProcessBean> vDB = t.getBancheDati();
            for (int i = count; i < vDB.size(); i++) {
                ProcessBean db = vDB.get(i);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                y += s;
                g2d.setStroke(stroke1);
                g2d.drawLine(x, y, 550, y);
                y += s*2;
                if (y < pageHeight) {
                    g2d.drawString(db.getNome() + " (" + db.getTipo() + ")", x, y);
                } else {
                    dbIndex = i;
                    break;
                }
            }
            // Prepara info di ritorno
            int[] data = {dbIndex, y};
            // Extra info
            if (y < 600) {
                g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
                y = println(g2d, "Ulteriori informazioni:", x, y, s*5);
                String text = cleanHtml(t.getExtraInfo());
                String[] textAsArray = wrapText(g2d, text, 600);
                g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
                for (int i = 0; i < textAsArray.length; i++) {
                    y = println(g2d, textAsArray[i], x, y, s*2);
                }
            }
            // Add the page to the document
            doc.addPage(page);
            return data;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
 
    
    /**
     * Genera la pagina di descrizione di uleriori informazioni.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     oggetto contenente i dati del trattamento
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static void makeExtraInfosPage(PageFormat pf, 
                                           PDFDocument doc,
                                           ProcessingBean t)
                                    throws CommandException {
        int y = 180;
        int s = 8;
        try {
            // Create a page in the document
            PDFPage page = doc.createPage(pf);
            // Create Graphics2D
            Graphics2D g2d = page.createGraphics(); 
            // Make the heder of the page
            makeHeaderInternalPage(g2d, t);
            // Draw a string            
            g2d.setColor(Color.black);
            g2d.setFont(new Font ("Helvetica", Font.BOLD, 14));
            g2d.drawString("Ulteriori informazioni:", x, y);
            String text = cleanHtml(t.getExtraInfo());
            String[] textAsArray = wrapText(g2d, text, 600);
            g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
            for (int i = 0; i < textAsArray.length; i++) {
                y = println(g2d, textAsArray[i], x, y, s*2);
            }
            // Add the page to the document
            doc.addPage(page);
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero di un attributo obbligatorio del bean.\n" + anve.getMessage();
            log.severe(msg);
            throw new CommandException(msg, anve);
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema in un metodo per la generazione di pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /**
     * Produce l'ultima pagina del documento pdf passato come parametro, 
     * formattata sulla base del PageFormat passato come parametro,
     * contenente il riquadro per la firma del documento.
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @throws IOException se si verifica un problema nel puntamento a una risorsa (immagine)
     */
    public static void makeLastPage(PageFormat pf, 
                                    PDFDocument doc)
                             throws IOException {
        int y = 240;
        int i = 20;
        // Create a page in the document
        PDFPage page = doc.createPage(pf);
        // Create Graphics2D
        Graphics2D g2d = page.createGraphics();
        // Content
        printLogo(g2d);
        // Make a rectangle 
        g2d.drawRoundRect(x, y, 450, 155, 2, 2);
        g2d.setFont(new Font ("Arial", Font.BOLD, 11));
        y = println(g2d, "Il Rettore", getCoordinate(x, i*10), y, i);
        g2d.setFont(new Font ("Arial", Font.PLAIN, 11));
        g2d.drawString("___________________________________________", getCoordinate(x, i*5), getCoordinate(y, i*3));
        y = println(g2d, "Prof. Pier Francesco Nocini", getCoordinate(x, i*8), y, i*5);
        doc.addPage(page);
    }

    
    /**
     * Gestisce il flusso principale
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param list  lista di tutti i trattamenti da stampare
     * @throws AttributoNonValorizzatoException se un attributo obbligatorio di un trattamento non viene trovato dotato di valore significativo
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    public static void makePages(PageFormat pf,
                                 PDFDocument doc,
                                 ArrayList<ProcessingBean> list) 
                          throws AttributoNonValorizzatoException, 
                                 CommandException {
        ArrayList<ProcessingBean> vT = null;
        ArrayList<ProcessingBean> vR = null;
        try {
            // 1. Ambito di applicazione
            makeFirstPage(pf, doc);
            // 2. Dati di Contatto
            makeSecondPage(pf, doc);
            // Altre pagine
            if (list.size() > ELEMENT_LEV_1) {
                vT = split(list, TITOLARE);
                vR = split(list, RESPONSABILE);
                // Pagina introduttiva elenco trattamenti da titolare
                makeIntermediatePage(pf, doc, "3. Elenco dei trattamenti come Titolare", "Titolare");
                // Elenco trattamenti da titolare
                for (ProcessingBean t : vT) {
                    printPages(pf, doc, t);
                }
                // Pagina introduttiva elenco trattamenti da responsabile
                makeIntermediatePage(pf, doc, "4. Elenco dei trattamenti come Responsabile", "Responsabile");
                // Elenco trattamenti da responsabile
                for (ProcessingBean t : vR) {
                    printPages(pf, doc, t);
                }
                // Firmato
                makeLastPage(pf, doc);
            } else {
                // C'è un solo trattamento
                ProcessingBean t = list.get(0);
                printPages(pf, doc, t);
            }
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero del codice del trattamento nel metodo per la generazione della pagina pdf.\n" + anve.getMessage();
            log.severe(msg);
            throw anve;
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Problema nella generazione delle pagine pdf.\n" + ce.getMessage();
            log.severe(msg);
            throw ce;
        } catch (IOException ioe) {
            String msg = FOR_NAME + "Probabile problema nel puntamento a una risorsa esterna.\n" + ioe.getMessage();
            log.severe(msg);
            throw new CommandException(msg, ioe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Problema nel metodo per la generazione della pagina pdf.\n" + e.getMessage();
            log.severe(msg);
            throw new CommandException(msg, e);
        }
    }
    
    
    /**
     * Gestisce un sottoinsieme del flusso principale
     * 
     * @param pf    formato della pagina
     * @param doc   istanza di documento PDF
     * @param t     trattamento da stampare
     * @throws CommandException se si verifica un problema nel recupero di una risorsa, di valori o in qualche altro tipo di puntamento
     */
    private static void printPages(PageFormat pf,
                                   PDFDocument doc,
                                   ProcessingBean t) 
                            throws CommandException {
        int count = makeProcessingPage(pf, doc, t);
        if (count > NOTHING) {
            count = makeActivitiesPage(pf, doc, t, count);
            if (count > NOTHING) {
                makeActivitiesPage(pf, doc, t, count);
            }
        }
        makeLegalBasisPage(pf, doc, t);
        makeKindOfDataPage(pf, doc, t);
        boolean printed = makeExpireTimePage(pf, doc, t);
        if (!printed) {
            makeExpireTimeAsPage(pf, doc, t);
        }
        String left = makeSecMeasurePage(pf, doc, t, null);
        if (left != null) {
            makeSecMeasurePage(pf, doc, t, left);
        }
        int countArray[] = makeDBLocationPage(pf, doc, t, NOTHING);
        count = countArray[NOTHING];
        if (count > NOTHING) {
            countArray = makeDBLocationPage(pf, doc, t, count);
            count = countArray[NOTHING];
            if (count > NOTHING) {
                makeDBLocationPage(pf, doc, t, count);
            }
        }
        if (countArray[ELEMENT_LEV_1] >= 600) {
            makeExtraInfosPage(pf, doc, t);
        }
        if (t.getDescrizione().length() >= 600) {
            String cleanText = cleanHtml(t.getDescrizione());
            String furtherInfo = makeProcessingPage(pf, doc, t, null);
            if (furtherInfo != null) {
                makeProcessingPage(pf, doc, t, cleanText.substring(1995, 4000));
                if (cleanText.length() > 4000) {
                    makeProcessingPage(pf, doc, t, cleanText.substring(4000, cleanText.length()));
                }
            }
        }
    }
    
        
    public static ArrayList<ProcessingBean> split(ArrayList<ProcessingBean> list, String suffix) throws AttributoNonValorizzatoException {
        ArrayList<ProcessingBean> l = new ArrayList<>();
        try {
            for (ProcessingBean t : list) {
                if (t.getCodice().endsWith(suffix)) {
                    l.add(t);
                }
            }
            return l;
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Problema nel recupero del codice del trattamento nel metodo per la suddivisione in base al tipo.\n" + anve.getMessage();
            log.severe(msg);
            throw anve;
        }
    }
    
    
    public static void drawString(Graphics2D g, String s, int x, int y, int width)
    {
        // FontMetrics gives us information about the width,
        // height, etc. of the current Graphics object's Font.
        FontMetrics fm = g.getFontMetrics();

        int lineHeight = fm.getHeight();

        int curX = x;
        int curY = y;

        String[] words = s.split(" ");

        for (String word : words)
        {
            // Find out thw width of the word.
            int wordWidth = fm.stringWidth(word + " ");

            // If text exceeds the width, then move to next line.
            if (curX + wordWidth >= x + width)
            {
                curY += lineHeight;
                curX = x;
            }

            g.drawString(word, curX, curY);

            // Move over to the right for next word.
            curX += wordWidth;
        }
    }
    
    
    private static void drawString(Graphics2D graph2D, String text, float x, float y) {
        graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font serifFont = new Font("Serif", Font.PLAIN, 10);
        Font sansSerifFont = new Font("Monospaced", Font.PLAIN, 11);
        AttributedString as = new AttributedString(text);
        as.addAttribute(TextAttribute.FONT, serifFont);
        as.addAttribute(TextAttribute.FONT, sansSerifFont, 2, 5);
        as.addAttribute(TextAttribute.FOREGROUND, Color.red, 2, 5);
        graph2D.drawString(as.getIterator(), x, y);
    }
    
    
    private int drawString(Graphics2D g2d, String text, float x, float y, Font font, FontRenderContext frc)
    {
        TextLayout layout = new TextLayout(text, font, frc);
        Rectangle2D r2d = layout.getBounds();
        float xAdjusted = x - (float) r2d.getCenterX();
        layout.draw(g2d, xAdjusted, y);
        return (int) (layout.getAscent() + layout.getDescent());
    }
    
}
