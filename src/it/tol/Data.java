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

package it.tol;

import java.awt.print.PageFormat;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;
import com.qoppa.pdfWriter.PDFDocument;

import it.tol.bean.PersonBean;
import it.tol.bean.ProcessingBean;
import it.tol.command.RegisterCommand;
import it.tol.exception.AttributoNonValorizzatoException;
import it.tol.exception.CommandException;
import it.tol.interfaces.Constants;
import it.tol.utils.generator.DocumentGenerator;
import it.tol.wrapper.DBWrapper;
import it.tol.wrapper.DocWrapper;


/**
 * <p><code>Data</code> &egrave; la servlet della web-application rol
 * che pu&ograve; essere utilizzata in pi&uacute; contesti:<ol>
 * <li>su una richiesta sincrona: per produrre output con contentType differenti da
 * 'text/html'</li>
 * <li>su una richiesta asincrona: per ottenere tuple da mostrare asincronamente
 * nelle pagine</li></ol></p>
 * <p>Nel primo caso, questa servlet fa a meno, legittimamente, del design (View),
 * in quanto l'output prodotto consiste in pure tuple prive di presentazione
 * (potenzialmente: fileset CSV, formati XML, dati con o senza metadati, RDF,
 * JSON, <cite>and so on</cite>).<br />
 * <p>
 * Questa servlet estrae l'azione dall'URL, ne verifica la
 * correttezza, quindi in base al valore del parametro <code>'entToken'</code> ricevuto
 * (qui chiamato 'qToken' per motivi storici, ma non importa)
 * richiama le varie Command che devono eseguire i comandi specifici.
 * Infine, recupera l'output dai metodi delle Command stesse richiamati
 * e li restituisce a sua volta al cliente sotto forma non necessariamente di outputstream
 * 'text/html' (come nel funzionamento standard delle applicazioni web),
 * quanto sotto forma di file nel formato richiesto (.csv, .xml, ecc.),
 * oppure passando il nome di una risorsa da richiamare in modo asincrono
 * per mostrare le tuple estratte, e passate in Request.<br />
 * In caso di richieste di output non di tipo text/html,
 * elabora anche un nome univoco per ogni file generato, basandosi sul
 * timestamp dell'estrazione/richiesta.
 * </p>
 * <p>
 * La classe che realizza l'azione deve implementare l'interfaccia
 * Command e, dopo aver eseguito le azioni necessarie, restituire
 * un set di risultati che dovr&agrave; essere utilizzato per
 * visualizzare i dati all'interno dei files serviti, ai quali
 * sarà fatto un forward.
 * </p>
 * L'azione presente nell'URL deve avere il seguente formato:
 * <pre>&lt;entToken&gt;=&lt;nome&gt;</pre>
 * dove 'nome' è il valore del parametro 'entToken' che identifica
 * l'azione da compiere al fine di generare i record.<br />
 * Oltre al parametro <code>'entToken'</code> possono essere presenti anche
 * eventuali altri parametri, ma essi non hanno interesse nel contesto
 * della presente classe, venendo incapsulati nella HttpServletRequest
 * e quindi inoltrati alla classe Command che deve fare il lavoro di
 * estrazione. Normalmente, tali altri parametri possono essere presenti
 * sotto forma di parametri sulla querystring, ma anche direttamente
 * settati nella request; ci&ograve; non interessa alcunch&eacute; ai fini
 * del funzionamento della presente classe.
 * </p>
 * <p>
 * Altre modalit&agrave; di generazione di output differenti da 'text/html'
 * (chiamate a pagine .jsp che incorporano la logica di preparazione del CSV,
 * chiamate a pagina .jsp che si occupano di presentare il metadato...)
 * vanno assolutamente evitate in favore dell'uso di questa servlet.
 * </p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class Data extends HttpServlet implements Constants {

    /**
     * La serializzazione necessita della dichiarazione
     * di una costante di tipo long identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -7053908837630394953L;
    /**
     * Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore)
     */
    private static final String FOR_NAME = "\n" + Logger.getLogger(Data.class.getName()) + ": ";
    /**
     * Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.
     */
    private static Logger log = Logger.getLogger(Data.class.getName());
    /**
     * Serve per inizializzare i rendirizzamenti con il servletToken
     */
    private ServletContext servletContext;
    /**
     * Parametro della query string identificante una Command.
     */
    private String qToken;
    /**
     * Parametro della query string per richiedere un certo formato di output.
     */
    private String format;


    /**
     * Inizializza (staticamente) le variabili globali e i parametri di inizializzazione.
     *
     * @param config la configurazione usata dal servlet container per passare informazioni alla servlet <strong>durante l'inizializzazione</strong>
     * @throws ServletException una eccezione che puo' essere sollevata quando la servlet incontra difficolta'
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        /*
         *  Inizializzazione da superclasse
         */
        super.init(config);
        /*
         *  Inizializzazione del servletToken
         */
        servletContext = getServletContext();
    }


    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("javadoc")
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res)
                    throws ServletException, IOException {
        // La pagina della servlet e' sganciata dal template, anzi ne costituisce un frammento
        String fileJsp = null;
        // Recupera valore di ent (servito da un ConfigManager esterno alla Data)
        qToken = req.getParameter(ConfigManager.getEntToken());
        // Recupera il formato dell'output, se specificato
        format = req.getParameter(ConfigManager.getOutToken());
        // Struttura da restituire in Request
        AbstractList<?> list = null;
        // Message
        log.info("===> Log su servlet Data. <===");
        // Decodifica la richiesta
        try {
            // Verifica se deve servire un output PDF
            if (format != null && !format.isEmpty() && format.equalsIgnoreCase(PDF)) {
                // Recupero elementi in base alla richiesta
                list = retrieve(req, qToken);
                // Passaggio in request per uso delle lista
                req.setAttribute("lista", list);
                // Genera il file PDF
                generatePDF(req, res);
                // Ha finito
                return;
            }
        } catch (CommandException ce) {
            throw new ServletException(FOR_NAME + "Problema nel service di Data.\n" + ce.getMessage(), ce);
        }
        // Forworda la richiesta, se è questo il caso
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(fileJsp);
        dispatcher.forward(req, res);
    }


    /**
     * <p>Restituisce un elenco generico di elementi relativi a una richiesta specifica.</p>
     *
     * @param req HttpServletRequest contenente i parametri per contestualizzare l'estrazione
     * @param qToken il token della commmand in base al quale bisogna preparare la lista di elementi
     * @return <code>ArrayList&lt;?&gt; - lista contenente gli elementi trovati
     * @throws CommandException se si verifica un problema nel recupero dei dati o in qualche puntamento
     */
    private static ArrayList<?> retrieve(HttpServletRequest req,
                                         String qToken)
                                  throws CommandException {
        // Dichiara generico elenco di elementi da restituire
        ArrayList<?> list = null;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametro per identificare il trattamento 
        String codeT = parser.getStringParameter("idT", VOID_STRING);
        // Recupera o inizializza parametro per identificare la rilevazione
        String codeSur = parser.getStringParameter("r", VOID_STRING);
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
        PersonBean user = (PersonBean) ses.getAttribute("usr");
        if (user == null) {
            throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
        }
        // Gestisce la richiesta
        try {
            ArrayList<ProcessingBean> vT = new ArrayList<>();
            // Istanzia nuovo Databound
            DBWrapper db = new DBWrapper();
            // Recupera il trattamento dati            
            if (!codeT.equals(VOID_STRING)) {
                ProcessingBean t = RegisterCommand.retrieve(user, codeT, STATE_ACTIVE, ConfigManager.getSurvey(codeSur), db);
                vT.add(t);
            // Recupera tutti i trattamenti
            } else {
                vT = RegisterCommand.retrieve(user, STATE_ACTIVE, ConfigManager.getSurvey(codeSur), db);
            }
            list = vT;
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        }
        return list;
    }


    /**
     * <p>Restituisce una mappa contenente elenchi di elementi generici 
     * (input, fasi, output...) estratti in base alla richiesta ricevuta
     * e indicizzati per una chiave convenzionale, definita nelle costanti.</p>
     *
     * @param req HttpServletRequest contenente i parametri per contestualizzare l'estrazione
     * @param qToken il token della commmand in base al quale bisogna preparare la lista di elementi
     * @param pToken il token relativo alla parte di gestione da effettuare
     * @return <code>HashMap&lt;String,ArrayList&lt;?&gt;&gt; - dictionary contenente le liste di elementi desiderati, indicizzati per una chiave convenzionale
     * @throws CommandException se si verifica un problema nella WebStorage (DBWrapper), nella Command interpellata, o in qualche puntamento
     */
    private static HashMap<String, ArrayList<?>> retrieve(HttpServletRequest req,
                                                          String qToken,
                                                          String pToken)
                                                   throws CommandException {
        // Dichiara generico elenco di elementi da restituire
        HashMap<String,ArrayList<?>> list = null;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametro per identificare la pagina
        String part = parser.getStringParameter("p", VOID_STRING);
        // Recupera o inizializza parametro per identificare la rilevazione
        String codeSurvey = parser.getStringParameter("r", VOID_STRING);
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
        PersonBean user = (PersonBean) ses.getAttribute("usr");
        if (user == null) {
            throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
        }
        // Gestisce la richiesta
        try {
            // Istanzia nuovo Databound
            DBWrapper db = new DBWrapper();

        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n" + e.getLocalizedMessage();
            log.severe(msg);
            throw new CommandException(msg);
        }
        return list;
    }


    /**
     * <p>Genera un nome univoco a partire da un prefisso dato come parametro.</p>
     *
     * @param label il prefisso che costituira' una parte del nome del file generato
     * @return <code>String</code> - il nome univoco generato
     */
    private static String makeFilename(String label) {
        // Crea un nome univoco per il file che andrà ad essere generato
        Calendar now = Calendar.getInstance();
        String fileName = label + UNDERSCORE +
                          new Integer(now.get(Calendar.YEAR)).toString() + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.MONTH) + 1)) + HYPHEN +
                          String.format("%02d", new Integer(now.get(Calendar.DAY_OF_MONTH))) + UNDERSCORE +
                          String.format("%02d", new Integer(now.get(Calendar.HOUR_OF_DAY))) +
                          String.format("%02d", new Integer(now.get(Calendar.MINUTE))) +
                          String.format("%02d", new Integer(now.get(Calendar.SECOND)));
        return fileName;
    }
    
    
    private void generatePDF (HttpServletRequest req, HttpServletResponse res) 
                       throws IOException, ServletException {
        if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_REGISTER)) {
            try {
                // Recupera lista di trattamenti da stampare
                ArrayList<ProcessingBean> list = (ArrayList<ProcessingBean>) req.getAttribute("lista");
                // Genera un nome univoco per il file che verrà servito
                String label = null;
                // O è presente un solo trattamento
                if (list.size() == ELEMENT_LEV_1) {
                    label = list.get(MAIN_MENU).getCodice();
                // O è presente una lista di trattamenti
                } else if (list.size() > ELEMENT_LEV_1) {
                    label = Constants.TREATMENTS;
                // La terza situazione è un errore
                } else {
                    String msg = FOR_NAME + "La Servlet Data non ha trovato trattamenti.\n";
                    log.severe(msg);
                    throw new IOException(msg);
                }
                String fileName = makeFilename(label);
                // Configura il response per il browser
                res.setContentType(MIME_TYPE_PDF);
                // Configura il characterEncoding
                res.setCharacterEncoding("UTF-8");
                // Configura l'header
                res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + PDF);
                // Stampa il file sullo standard output
                pdfPrintf(req, res);
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di attributi obbligatori contestualmente alla generazione del pdf.\n" + anve.getMessage();
                log.severe(msg);
                throw new IOException(msg);
            } catch (Exception e) {
                String msg = FOR_NAME + "Problema in un generatePDF di Data" + e.getMessage();
                log.severe(msg);
                throw new ServletException(msg);
            }
        } else {
            String msg = FOR_NAME + "La Servlet Data non accetta la stringa passata come valore di 'ent': " + req.getParameter(ConfigManager.getEntToken());
            log.severe(msg + "Tentativo di indirizzare alla Servlet Data una richiesta non gestita. Hacking test?\n");
            throw new IOException(msg);
        }
    }
    

    /**
     * <p>Gestisce la generazione dell&apos;output in formato di uno stream CSV 
     * che sar&agrave; recepito come tale dal browser e trattato di conseguenza 
     * (normalmente con il download).</p>
     * <p>Usa altri metodi, interni, per ottenere il nome del file, che dev&apos;essere un
     * nome univoco, e per la stampa vera e propria nel PrintWriter.</p>
     * <p>Un&apos;avvertenza importante riguarda il formato del character encoding!
     * Il db di processi anticorruttivi &egrave; codificato in UTF&ndash;8;
     * pertanto nell'implementazione potrebbe sembrare ovvio che
     * il characterEncoding migliore da impostare sia il medesimo, cosa che si fa
     * con la seguente istruzione:
     * <pre>res.setCharacterEncoding("UTF-8");</pre>
     * Tuttavia, le estrazioni sono destinate ad essere visualizzate
     * attraverso fogli di calcolo che, per impostazione predefinita,
     * assumono che il charset dei dati sia il latin1, non UTF-8,
     * perlomeno per la nostra utenza e per la maggior parte
     * delle piattaforme, per cui i dati, se espressi in formato UTF-8,
     * risultano codificati in maniera imprecisa, perch&eacute;, come al solito,
     * quando un dato UTF-8 viene codificato in latin1
     * (quest'ultimo anche identificato come: l1, csISOLatin1, iso-ir-100,
     * IBM819, CP819, o &ndash; ultimo ma non ultimo &ndash; ISO-8859-1)
     * i caratteri che escono al di fuori dei primi 128 caratteri,
     * che sono comuni (in quanto UTF-8 usa un solo byte per
     * codificare i primi 128 caratteri) non vengono visualizzati
     * correttamente ma vengono espressi con caratteri che in ASCII sono
     * non corrispondenti.</p>
     *
     * @param req HttpServletRequest da passare al metodo di stampa
     * @param res HttpServletResponse per impostarvi i valori che la predispongono a servire csv anziche' html
     * @param qToken token della commmand in base al quale bisogna preparare la lista di elementi
     * @throws ServletException eccezione eventualmente proveniente dalla fprinf, da propagare
     * @throws IOException  eccezione eventualmente proveniente dalla fprinf, da propagare
     */
    private static void generateCSV(HttpServletRequest req, HttpServletResponse res, String qToken)
                             throws ServletException, IOException {
        // Genera un nome univoco per il file che verrà servito
        String fileName = makeFilename(qToken);
        // Configura il response per il browser
        res.setContentType(MIME_TYPE_CSV);
        // Configura il characterEncoding
        res.setCharacterEncoding("UTF-8");
        // Configura l'header
        res.setHeader("Content-Disposition","attachment;filename=" + fileName + DOT + CSV);
        // Stampa il file sullo standard output
        csvPrintf(req, res);
    }
    
    
    /**
     * <p>Genera il contenuto dello stream, che questa classe tratta
     * sotto forma di file, che viene trasmesso sulla risposta in output,
     * a seconda del valore di <code>'q'</code> che riceve in input.</p>
     * <p>
     * Storicamente, in programmazione <code> C, C++ </code> e affini,
     * le funzioni che scrivono sull'outputstream si chiamano tutte
     * <code>printf</code>, precedute da vari prefissi a seconda di
     * quello che scrivono e di dove lo scrivono.<br />
     * <code>fprintf</code> &egrave; la funzione della libreria C che
     * invia output formattati allo stream, identificato con un puntatore
     * a un oggetto FILE passato come argomento
     * (<small>per approfondire,
     * <a href="http://www.tutorialspoint.com/c_standard_library/c_function_fprintf.htm">
     * v. p.es. qui</a></small>).<br />
     * Qui per analogia, pi&uacute; che altro nella forma di una "dotta"
     * citazione (per l'ambito informatico) il metodo della Data che
     * scrive il contenuto vero e proprio del file che viene passato
     * al client, viene chiamato allo stesso modo di questa "storica" funzione,
     * ma il contesto degli oggetti e degli argomenti
     * &egrave; ovviamente molto diverso.</p>
     *
     * @param req la HttpServletRequest contenente il valore di 'ent' e gli altri parametri necessari a formattare opportunamente l'output
     * @param res la HttpServletResponse utilizzata per ottenere il 'Writer' su cui stampare il contenuto, cioe' il file stesso
     * @throws ServletException   java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
     * @throws IOException        java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
    @SuppressWarnings("unchecked")
    private static void pdfPrintf(HttpServletRequest req, HttpServletResponse res)
                           throws ServletException, IOException {
        // Genera l'oggetto per il servlet output stream
        ServletOutputStream out = res.getOutputStream();
        // Recupera lista di trattamenti da stampare
        ArrayList<ProcessingBean> list = (ArrayList<ProcessingBean>) req.getAttribute("lista");
        /* **************************************************************** *
         *     Gestione elaborazione contenuto PDF per trattamenti dati     *
         * **************************************************************** */
        if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_REGISTER)) {
            try {
                // Genera il documento PDF
                PDFDocument pdfDoc = DocumentGenerator.getPDFDocument();
                // Ottiene il formato della pagina
                PageFormat pf = DocumentGenerator.getPageFormat();
                // Se sta stampando il registro bisogna aggiungere le intestazioni
                if (list.size() > ELEMENT_LEV_1) {
                    // 0. Frontespizio
                    DocWrapper.makeFrontPage(pf, pdfDoc);
                }
                // Dettagli trattamento/i
                DocWrapper.makePages(pf, pdfDoc, list);
                // Save the document to the servlet output stream. This goes directly to the browser
                pdfDoc.saveDocument(out);
    
            } catch (Exception e) {
                log.severe(FOR_NAME + "Problema in un fprintf di Data" + e.getMessage());
                out.println(e.getMessage());
            }
        } else {
            String msg = FOR_NAME + "La Servlet Data non accetta la stringa passata come valore di 'ent': " + req.getParameter(ConfigManager.getEntToken());
            log.severe(msg + "Tentativo di indirizzare alla Servlet Data una richiesta non gestita. Hacking test?\n");
            throw new IOException(msg);
        }
        // Close the server output stream
        out.close();
    }
    
    
    /**
     * <p>Genera il contenuto dello stream, che questa classe tratta
     * sotto forma di file, che viene trasmesso sulla risposta in output,
     * a seconda del valore di <code>'q'</code> che riceve in input.</p>
     * <p>
     * Storicamente, in programmazione <code> C, C++ </code> e affini,
     * le funzioni che scrivono sull'outputstream si chiamano tutte
     * <code>printf</code>, precedute da vari prefissi a seconda di
     * quello che scrivono e di dove lo scrivono.<br />
     * <code>fprintf</code> &egrave; la funzione della libreria C che
     * invia output formattati allo stream, identificato con un puntatore
     * a un oggetto FILE passato come argomento
     * (<small>per approfondire,
     * <a href="http://www.tutorialspoint.com/c_standard_library/c_function_fprintf.htm">
     * v. p.es. qui</a></small>).<br />
     *
     * @param req la HttpServletRequest contenente il valore di 'ent' e gli altri parametri necessari a formattare opportunamente l'output
     * @param res la HttpServletResponse utilizzata per ottenere il 'Writer' su cui stampare il contenuto, cioe' il file stesso
     * @return <code>int</code> - un valore intero restituito per motivi storici.
     *                            Tradizionalmente, tutte le funzioni della famiglia x-printf restituiscono un intero,
     *                            che vale il numero dei caratteri scritti - qui il numero delle righe scritte - in caso di successo
     *                            oppure -1 (o un altro numero negativo) in caso di fallimento
     * @throws ServletException   java.lang.Throwable.Exception.ServletException che viene sollevata se manca un parametro di configurazione considerato obbligatorio o per via di qualche altro problema di puntamento
     * @throws IOException        java.io.IOException che viene sollevata se si verifica un puntamento a null o in genere nei casi in cui nella gestione del flusso informativo di questo metodo si verifica un problema
     */
    @SuppressWarnings("unchecked")
    private static int csvPrintf(HttpServletRequest req, HttpServletResponse res)
                          throws ServletException, IOException {
        // Genera l'oggetto per lo standard output
        PrintWriter out = res.getWriter();
        // Tradizionalmente, ogni funzione della famiglia x-printf restituisce un intero
        int success = DEFAULT_ID;
        // Ottiene i parametri della richiesta
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza parametro per identificare la pagina
        String part = parser.getStringParameter("p", VOID_STRING);
        /* **************************************************************** *
         *  Gestione elaborazione contenuto CSV per interviste con risposte *
         * **************************************************************** */
        if (req.getParameter(ConfigManager.getEntToken()).equalsIgnoreCase(COMMAND_REGISTER)) {
            /* ************************************************************ *
             *    Generazione contenuto files CSV di tutte le interviste    *
             * ************************************************************ */
        }
        else {
            String msg = FOR_NAME + "La Servlet Data non accetta la stringa passata come valore di 'ent': " + req.getParameter(ConfigManager.getEntToken());
            log.severe(msg + "Tentativo di indirizzare alla Servlet Data una richiesta non gestita. Hacking test?\n");
            throw new IOException(msg);
        }
        return success;
    }
    
}
