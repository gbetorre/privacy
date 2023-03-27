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

package it.tol.command;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.tol.ConfigManager;
import it.tol.Constants;
import it.tol.DBWrapper;
import it.tol.Main;
import it.tol.Query;
import it.tol.Utils;
import it.tol.bean.CodeBean;
import it.tol.bean.ItemBean;
import it.tol.bean.PersonBean;
import it.tol.exception.AttributoNonValorizzatoException;
import it.tol.exception.CommandException;
import it.tol.exception.WebStorageException;


/**
 * <p><code>HomeCommand.java</code><br />
 * Gestisce la root dell'applicazione.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class HomeCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = -4437906730411178543L;
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    /* friendly */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": "; //$NON-NLS-1$
    /* $NON-NLS-1$ silence a warning that Eclipse emits when it encounters
     * string literals.
     * The idea is that UI messages should not be embedded as string literals,
     * but rather sourced from a resource file
     * (so that they can be translated, proofed, etc).*/
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Pagina a cui la command reindirizza per mostrare la form di login
     */
    private static final String nomeFileElenco = "/jsp/login.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare le scelte iniziali
     */
    private static final String nomeFileLanding = "/jsp/landing.jsp";
    /**
     * DataBound.
     */
    private static DBWrapper db;
    /**
     * Nome del database su cui insiste l'applicazione
     */
    private static String dbName = null;
    /**
     * Ultima rilevazione
     */
    private static CodeBean lastSurvey;


    /**
     * Crea una nuova istanza di HomeCommand
     */
    public HomeCommand() {
        /*;*/   // It doesn't anything
    }


    /**
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
	 *
	 * @param voceMenu la VoceMenuBean pari alla Command presente.
	 * @throws it.tol.exception.CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
     */
    @Override
    public void init(ItemBean voceMenu) throws CommandException {
        this.setId(voceMenu.getId());
        this.setNome(voceMenu.getNome());
        this.setLabelWeb(voceMenu.getLabelWeb());
        this.setNomeClasse(voceMenu.getNomeClasse());
        this.setPaginaJsp(voceMenu.getPaginaJsp());
        this.setInformativa(voceMenu.getInformativa());
        if (this.getPaginaJsp() == null) {
          String msg = FOR_NAME + "La voce menu' " + this.getNome() + " non ha il campo paginaJsp. Impossibile visualizzare i risultati.\n";
          throw new CommandException(msg);
        }
        try {
            // Attiva la connessione al database
            db = new DBWrapper();
            // Recupera l'ultima rilevazione
            lastSurvey = db.getSurvey(GET_ALL_BY_CLAUSE, GET_ALL_BY_CLAUSE);
        }
        catch (WebStorageException wse) {
            String msg = FOR_NAME + "Non e\' possibile avere una connessione al database.\n" + wse.getMessage();
            throw new CommandException(msg, wse);
        }
        catch (Exception e) {
            String msg = FOR_NAME + "Problemi nel caricare gli stati.\n" + e.getMessage();
            throw new CommandException(msg, e);
        }
    }


    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     *
     * @param req HttpServletRequest contenente parametri e attributi, e in cui settare attributi
     * @throws CommandException incapsula qualunque genere di eccezione che si possa verificare in qualunque punto del programma
     */
    @Override
    public void execute(HttpServletRequest req)
                 throws CommandException {
        /* ******************************************************************** *
         *                    Dichiarazioni e inizializzazioni                  *
         * ******************************************************************** */
        // Utente loggato
        PersonBean user = null;
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara un messaggio di errore
        String error = null;
        
        String parameters = this.getParameters(req, MIME_TYPE_TEXT);
        /* ******************************************************************** *
         *                 Recupero dei parametri di navigazione                *
         * ******************************************************************** */
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        // Non controlla qui se l'utente è già loggato perché questa command deve rispondere anche PRIMA del login
        /* ******************************************************************** *
         *             Rami in cui occorre che l'utente sia loggato             *
         * ******************************************************************** */
        try {
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH) && ConfigManager.getSurveys().containsKey(codeSur)) {
                // In questo punto la sessione deve esistere e l'utente deve esserne loggato
                try {
                    user = getLoggedUser(req);
                } catch (CommandException ce) {
                    String msg = "Si e\' tentato di accedere a una funzione senza essere loggati, o a sessione scaduta.\n";
                    LOG.severe("Problema a livello di autenticazione: " + msg + ce.getMessage());
                    throw (ce);
                }
                // Se l'utente è loggato ed esiste il parametro di rilevazione, mostra la pagina di landing
                fileJspT = nomeFileLanding;
            } else {
                if (isLoggedUser(req)) {
                    error = "Funzione non trovata.";
                }
                fileJspT = nomeFileElenco;
            }
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + ": Si e\' verificato un problema in una conversione di tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + cce.getMessage(), cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema non meglio specificato.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *                          Recupera i parametri                        *
         * ******************************************************************** */
        /* Imposta una variabile di applicazione, se non è già stata valorizzata (singleton).
         * Il contenuto in sé della variabile è stato sicuramente creato, altrimenti
         * non sarebbe stato possibile arrivare a questo punto del codice,
         * ma, se questa è la prima richiesta che viene fatta all'applicazione
         * (e siamo quindi in presenza dell'"handicap del primo uomo")
         * non è detto che la variabile stessa sia stata memorizzata a livello
         * di application scope. Ci serve a questo livello per controllare,
         * in tutte le pagine dell'applicazione, che stiamo puntando al db giusto.  
         * ATTENZIONE: crea una variabile di applicazione                       */
        dbName = (String) req.getServletContext().getAttribute("dbName");
        if (dbName == null || dbName.isEmpty()) {
            // Uso la stessa stringa perché, se non valorizzata in application, non sarà mai empty ma sarà null
            dbName = ConfigManager.getDbName();
            // Attenzione: crea una variabile di APPLICAZIONE
            req.getServletContext().setAttribute("db", dbName);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
        if (error != null) {
            req.setAttribute("error", true);
            req.setAttribute("msg", error);
        }
    }


    /**
     * <p>Restituisce l'utente loggato, se lo trova nella sessione utente,
     * altrimenti lancia un'eccezione.</p>
     *
     * @param req HttpServletRequest contenente la sessione e i suoi attributi
     * @return <code>PersonBean</code> - l'utente loggatosi correntemente
     * @throws CommandException se si verifica un problema nel recupero della sessione o dei suoi attributi
     */
    public static PersonBean getLoggedUser(HttpServletRequest req)
                                    throws CommandException {
        // Utente loggato
        PersonBean user = null;
        /* ******************************************************************** *
         *                         Recupera la Sessione                         *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                String msg = FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n";
                LOG.severe(msg + "Sessione non trovata!\n");
                throw new CommandException();
            }
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                String msg = FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n";
                LOG.severe(msg + "Attributo \'utente\' non trovato in sessione!\n");
                throw new CommandException(msg);
            }
            return user;
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + ": Si e\' verificato un problema in una conversione di tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + cce.getMessage(), cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Restituisce true se l'utente &egrave; loggato e se lo trova nella sessione utente,
     * altrimenti restituisce false.</p>
     *
     * @param req HttpServletRequest contenente la sessione e i suoi attributi
     * @return <code>boolean</code> - flag di utente trovato in sessione
     * @throws CommandException se si verifica un problema di puntamento
     */
    public static boolean isLoggedUser(HttpServletRequest req)
                                throws CommandException {
        // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
        try {
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                return false;
            }
            PersonBean user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                return false;
            }
            return true;
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /* ************************************************************************ *
     * Metodi di generazione di liste di voci (per MENU,submenu,breadcrumbs...) *
     * ************************************************************************ */

    /**
     * <p>Restituisce una String che rappresenta un url da impostare in una
     * voce di menu, il cui padre viene passato come argomento, come
     * analogamente la web application seguente la root ed un eventuale
     * parametro aggiuntivo.</p>
     *
     * @param appName nome della web application, seguente la root, per la corretta generazione dell'url
     * @param title voce di livello immediatamente superiore alla voce per la quale si vuol generare l'url
     * @param part eventuale valore del parametro 'p' della Querystring
     * @param surCode identificativo della rilevazione corrente oppure della rilevazione di default in caso di accesso utente a piu' di una rilevazione
     * @return <code>String</code> - url ben formato e valido, da applicare a una voce di menu
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    private static String makeUrl(String appName,
                                  ItemBean title,
                                  String part,
                                  String surCode)
                           throws CommandException {
        String entParam = SLASH + QM + ConfigManager.getEntToken() + EQ;
        StringBuffer url = new StringBuffer(appName);
        try {
            url.append(entParam).append(title.getNome());
            if (part != null) {
                url.append("&p=").append(part);
            }
            url.append("&r=").append(surCode);
            return String.valueOf(url);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema di natura non identificata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }


    /**
     * <p>Restituisce una struttura vettoriale <cite>(with insertion order)</cite>
     * contenente le breadcrumbs lasciate dall'utente nel percorso seguito fino
     * alla richiesta corrente.</p>
     * <p>Non si pu&ograve; passare direttamente la richiesta e lasciare che
     * il metodo si arrangi a recuperare i parametri (con getParameterNames)
     * perch&eacute; la richiesta conosciuta da questa classe non corrisponde
     * alla richiesta del chiamante. In linea teorica, si potrebbe effettuare
     * questa operazione, modificando questo metodo in modo che accetti la
     * richiesta come argomento, e passandola come parametro; tuttavia, 
     * la richiesta &egrave; un oggetto oneroso, e soprattutto
     * creerebbe confusione in una Command che &egrave;
     * creata a sua volta per gestire richieste...<br />
     * <small>NOTA: In questa classe vi sono metodi che accettano come parametro
     * una richiesta, ispezionandone e restituendone i valori, ma si tratta di
     * metodi di debug, utilizzati dal programmatore finalizzati appunto ad
     * ispezionare lo stato della richiesta, non metodi da utilizzare per
     * generare output: quindi non &egrave; la stessa cosa.</small></p>
     *
     * @param appName    nome della web application, seguente la root
     * @param pageParams la queryString contenente tutti i parametri di navigazione
     * @param extraInfo  parametro facoltativo; se significativo, permette di specificare una foglia ad hoc; in tal caso viene aggiunto il link alla ex-foglia
     * @return <code>LinkedList&lt;ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine di inserimento, che contiene le voci seguite dall'utente nella navigazione fino alla richiesta corrente
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    public static LinkedList<ItemBean> makeBreadCrumbs(String appName,
                                                       String pageParams,
                                                       String extraInfo)
                                                throws CommandException {
        int prime = 13;                 // per ottimizzare
        // Ottiene l'elenco delle Command
        Vector<ItemBean> classiCommand = ConfigManager.getClassiCommand();
        // Genera l'etichetta per nodo radice
        final String homeLbl = Utils.capitalize(COMMAND_HOME);
        // Dichiara la struttura per la lista di voci da usare per generare le breadcrumbs
        AbstractList<ItemBean> nav = new LinkedList<>();
        // Variabili di appoggio
        String codeSurvey, tokenSurvey = null;
        // Dictonary contenente i soli valori del token 'p' permessi
        LinkedHashMap<String, String> allowedParams = new LinkedHashMap<>(prime);
        // Lista dei token che NON devono generare breadcrumbs ("token vietati")
        LinkedList<String> deniedTokens = new LinkedList<>();
        // Aggiunge un tot di token vietati, caricati dinamicamente
        String deniedPattern1 = "sliv";
        String deniedPattern2 = "pliv";
        for (int i = 1; i <= 4; i++) {
            String patternToDeny1 = new String(deniedPattern1 + i);
            String patternToDeny2 = new String(deniedPattern2 + i);
            deniedTokens.add(patternToDeny1);
            deniedTokens.add(patternToDeny2);
        }
        // Aggiunge le esclusioni per data e ora ed eventuali altri parametri (p.es. id) che non devono essere marcati
        /*deniedTokens.add("d");
        deniedTokens.add("t");
        deniedTokens.add("idO");
        // Aggiunge i valori del token 'p' che devono generare breadcrumb associandoli a un'etichetta da mostrare in breadcrumb
        allowedParams.put(PART_SEARCH,          "Ricerca");
        allowedParams.put(PART_SELECT_STR,      "Scelta Struttura");
        allowedParams.put(PART_SELECT_QST,      "Quesiti");
        allowedParams.put(PART_CONFIRM_QST,     "Riepilogo");
        allowedParams.put(PART_SELECT_QSS,      "Interviste");
        allowedParams.put(PART_RESUME_QST,      "Risposte");
        allowedParams.put(PART_OUTPUT,          "Output");*/
        try {
            // Tokenizza la querystring in base all'ampersand
            String[] tokens = pageParams.split(AMPERSAND);
            // Prepara la lista dei parametri da esporre nelle breadcrumbs
            Map<String, String> tokensAsMap = new LinkedHashMap<>(prime);
            // Esamina ogni token
            for (int i = 0; i < tokens.length; i++) {
                // Ottiene la coppia: 'parametro=valore'
                String couple = tokens[i];
                // Estrae il solo parametro
                String paramName = couple.substring(NOTHING, couple.indexOf(EQ));
                // Estrae il solo valore
                String paramValue = couple.substring(couple.indexOf(EQ));
                // Test: il token trovato NON rientra in quelli da escludere? 
                if (!deniedTokens.contains(paramName)) {
                    // Allora il token genererà una breadcrumb
                    tokensAsMap.put(paramName, paramValue);
                    // Le variabili locali non servono più...
                    couple = paramName = paramValue = null;
                }
            }
            // Recupera il codice rilevazione
            codeSurvey = tokensAsMap.get(PARAM_SURVEY).substring(SUB_MENU);
            // Controllo sull'input (il codice rilevazione deve essere valido!)
            if (!ConfigManager.getSurveys().containsKey(codeSurvey)) {
                // "Se non dispone di un codice rilevazione, gliene verrà assegnato uno d'ufficio"...
                tokenSurvey = PARAM_SURVEY + EQ + ConfigManager.getSurveyList().get(MAIN_MENU).getNome();
            } else {
                // Se dispone di un codice rilevazione valido, verrà usato quello
                tokenSurvey = PARAM_SURVEY + tokensAsMap.get(PARAM_SURVEY);
            }
            // Il link alla home è fisso
            final String homeLnk = appName + ROOT_QM + ConfigManager.getEntToken() + EQ + COMMAND_HOME + AMPERSAND + tokenSurvey;
            // Crea un oggetto per incapsulare il link della root
            ItemBean root = new ItemBean(appName, homeLbl, homeLnk, MAIN_MENU);
            // Aggiunge la root alle breadcrumbs
            nav.add(root);
            // Scorre tutti i token calcolati per generare le corrispettive breadcrumbs
            for (java.util.Map.Entry<String, String> entry : tokensAsMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String labelWeb = null;
                if (key.equals(ConfigManager.getEntToken())) {
                    for (ItemBean command : classiCommand) {
                        if (command.getNome().equals((value.substring(SUB_MENU)))) {
                            labelWeb = command.getLabelWeb();
                            break;
                        }
                    }
                } else if (allowedParams.containsKey(value.substring(SUB_MENU))) {
                    labelWeb = allowedParams.get(value.substring(SUB_MENU));
                }
                String url = appName + ROOT_QM + key + value + AMPERSAND + tokenSurvey;
                if (!url.equals(homeLnk)) {
                    ItemBean item = new ItemBean(key, labelWeb, url, SUB_MENU);
                    if (!key.equals(PARAM_SURVEY)) {
                        nav.add(item);
                    }
                }
            }
            if (extraInfo != null) {
                nav.add(new ItemBean(extraInfo, extraInfo, extraInfo, SUB_MENU));
            }
            return (LinkedList<ItemBean>) nav;
        } catch (PatternSyntaxException pse) {
            String msg = FOR_NAME + "Si e\' verificato un problema di parsing della queryString.\n";
            LOG.severe(msg);
            throw new CommandException(msg + pse.getMessage(), pse);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema di natura non identificata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Prende in input una struttura di breadcrumbs gi&agrave; formata 
     * (serie di link corenti con il percorso seguito dall'utente fino 
     * alla richiesta corrente) e restituisce una struttura di breadcrumbs 
     * basata su quella ottenuta come parametro ma avente,
     * come ultima foglia, un'etichetta passata come parametro, se questo
     * &egrave; significativo, dando anche la possibilit&agrave; di eliminare 
     * un numero di foglie a piacere, specificato tramite un parametro: <dl>
     * <dt>0</dt><dd> =&gt; non toglie nulla,</dd>
     * <dt>1</dt><dd> =&gt; toglie l'ultima foglia,</dd> 
     * <dt>2</dt><dd> =&gt; toglie l'ultima e la penultima foglia</dd>
     * </dl>etc.</p>
     * 
     * @param nav       lista di breadcrumbs preesistente
     * @param items     numero di foglie da potare (opzionale)
     * @param extraInfo etichetta da aggiungere come ultima foglia (opzionale)
     * @return <code>LinkedList&lt;ItemBean&gt;</code> - struttura vettoriale, rispettante l'ordine di inserimento, rimaneggiata
     * @throws CommandException se si verifica un problema nell'accesso a qualche parametro o in qualche altro puntamento
     */
    public static LinkedList<ItemBean> makeBreadCrumbs(LinkedList<ItemBean> nav,
                                                       int items,
                                                       String extraInfo)
                                                throws CommandException {
        // Pota le foglie di Lorien
        if (items > NOTHING) {
            for (int i = 0; i < items; i++) {
                nav.removeLast();
            }
        }
        // Aggiunge una foglia di Valinor
        if (extraInfo != null && !extraInfo.equals(VOID_STRING)) {
            nav.add(new ItemBean(extraInfo, extraInfo, extraInfo, SUB_MENU));
        }
        // Restituisce l'albero potato e/o rimaneggiato
        return nav;
    }


    /* ************************************************************************ *
     *                             Metodi di debug                              *
     * ************************************************************************ */

    /**
     * <p>Restituisce i nomi e i valori degli attributi presenti in Request
     * in un dato momento e in un dato contesto, rappresentati dallo
     * stato del chiamante.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * quali attributi sono presenti in Request onde evitare duplicazioni
     * o ridondanze.</p>
     * </p>
     * Ad esempio, richiamando questo metodo dal ramo "didattica" del sito web
     * di ateneo, metodo <code>requestByPage</code>
     * e.g.: <pre>req.setAttribute("reqAttr", getAttributes(req));</pre>
     * e richiamandolo dalla pagina relativa, con la semplice:
     * <pre>${reqAttr}</pre>
     * si ottiene:
     * <pre style="border:solid gray;border-width:2px;padding:8px;">
     * <strong>dipartimento</strong> = it.univr.di.uol.bean.DipartimentoBean@518dd094
     * <strong>mO</strong> = {it.univr.di.uol.bean.SegnalibroBean@1ef0921d=[it.univr.di.uol.MenuVerticale@5ab38d6b, it.univr.di.uol.MenuVerticale@42099a52], it.univr.di.uol.bean.SegnalibroBean@4408bdc9=[it.univr.di.uol.MenuVerticale@4729f5d], it.univr.di.uol.bean.SegnalibroBean@19e3fa04=[it.univr.di.uol.MenuVerticale@13c94f3], it.univr.di.uol.bean.SegnalibroBean@463329e3=[it.univr.di.uol.MenuVerticale@3056de27]}
     * <strong>lingue</strong> = it.univr.di.uol.Lingue@3578ce60
     * <strong>FirstLanguage</strong> = it
     * <strong>flagsUrl</strong> = ent=home&page=didattica
     * <strong>SecondLanguage</strong> = en
     * <strong>logoFileDoc</strong> = [[it.univr.di.uol.bean.FileDocBean@5b11bbf9]]
     * <strong>currentYear</strong> = 2015
     * </pre></p>
     *
     * @param req HttpServletRequest contenente gli attributi che si vogliono conoscere
     * @return un unico oggetto contenente tutti i valori e i nomi degli attributi settati in request nel momento in cui lo chiede il chiamante
     */
    public static String getAttributes(HttpServletRequest req) {
        Enumeration<String> attributes = req.getAttributeNames();
        StringBuffer attributesName = new StringBuffer("<pre>");
        while (attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();
            attributesName.append("<strong><u>");
            attributesName.append(attributeName);
            attributesName.append("</u></strong>");
            attributesName.append(" = ");
            attributesName.append(req.getAttribute(attributeName));
            attributesName.append("<br />");
        }
        attributesName.append("</pre>");
        return String.valueOf(attributesName);
    }


    /**
     * <p>Restituisce i nomi e i valori dei parametri presenti in Request
     * in un dato momento e in un dato contesto, rappresentati dallo
     * stato del chiamante.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * quali parametri sono presenti in Request onde evitare duplicazioni
     * e/o ridondanze.</p>
     * <p>Esempi di richiamo:
     * String par = HomeCommand.getParameters(req, MIME_TYPE_HTML);
     * String par = HomeCommand.getParameters(req, MIME_TYPE_TEXT);
     * </p>
     * @param req HttpServletRequest contenente i parametri che si vogliono conoscere
     * @param mime argomento specificante il formato dell'output desiderato
     * @return un unico oggetto contenente tutti i valori e i nomi dei parametri settati in request nel momento in cui lo chiede il chiamante
     */
    public static String getParameters(HttpServletRequest req,
                                       String mime) {
        Enumeration<String> parameters = req.getParameterNames();
        StringBuffer parametersName = new StringBuffer();
        if (mime.equals(MIME_TYPE_HTML)) {
            parametersName.append("<pre>");
            while (parameters.hasMoreElements()) {
                String parameterName = parameters.nextElement();
                parametersName.append("<strong><u>");
                parametersName.append(parameterName);
                parametersName.append("</u></strong>");
                parametersName.append(" = ");
                parametersName.append(req.getParameter(parameterName));
                parametersName.append("<br />");
            }
            parametersName.append("</pre>");
        } else if (mime.equals(MIME_TYPE_TEXT)) {
            while (parameters.hasMoreElements()) {
                String parameterName = parameters.nextElement();
                parametersName.append(parameterName);
                parametersName.append(" = ");
                parametersName.append(req.getParameter(parameterName));
                parametersName.append("\n");
            }
        }
        return String.valueOf(parametersName);
    }


    /**
     * <p>Restituisce <code>true</code> se un nome di un parametro,
     * il cui valore viene passato come argomento del metodo, esiste
     * tra i parametri della HttpServletRequest; <code>false</code>
     * altrimenti.</p>
     * <p>Pu&ograve; essere utilizzato per verificare rapidamente
     * se un dato parametro sia stato passato in Request.</p>
     *
     * @param req HttpServletRequest contenente i parametri che si vogliono conoscere
     * @param paramName argomento specificante il nome del parametro cercato
     * @return un unico oggetto contenente tutti i valori e i nomi dei parametri settati in request nel momento in cui lo chiede il chiamante
     */
    public static boolean isParameter(HttpServletRequest req,
                                      String paramName) {
        Enumeration<String> parameters = req.getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameterName = parameters.nextElement();
            if (parameterName.equalsIgnoreCase(paramName)) {
                return true;
            }
        }
        return false;
    }

    /* ************************************************************************ *
     *                    Getters sulle variabili di classe                     *
     * ************************************************************************ */

    /**
     * <p>Restituisce la rilevazione con data pi&uacute; recente.</p>
     *
     * @return <code>LinkedList&lt;CodeBean&gt;</code> - una lista ordinata di tutti i possibili valori con cui puo' essere descritta la complessita' di un elemento
     * @throws AttributoNonValorizzatoException se l'id del CodeBean che rappresenta l'ultima rilevazione non e' stato valorizzato (p.es. per un difetto della query)
     */
    public static CodeBean getLastSurvey()
                                  throws AttributoNonValorizzatoException {
        return new CodeBean(lastSurvey);
    }

}
