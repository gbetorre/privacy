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
 *   the Free Software Foundation; either version 3 of the License, or
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.tol.command.HomeCommand;
import it.tol.ConfigManager;
import it.tol.Constants;
import it.tol.DBWrapper;
import it.tol.Main;
import it.tol.bean.CodeBean;
import it.tol.bean.DepartmentBean;
import it.tol.bean.ItemBean;
import it.tol.bean.PersonBean;
import it.tol.bean.ProcessBean;
import it.tol.bean.ProcessingBean;
import it.tol.exception.AttributoNonValorizzatoException;
import it.tol.exception.CommandException;
import it.tol.exception.WebStorageException;


/** 
 * <p><code>RegisterCommand.java</code><br />
 * Implementa la logica per la gestione del registro dei trattamenti (TOL).</p>
 * 
 * <p>Created on Mon 27th of March, 2023 at 13:03:26</p>
 * 
 * @author <a href="mailto:giovanroberto.torre@univr.it">Giovanroberto Torre</a>
 */
public class RegisterCommand extends ItemBean implements Command, Constants {
    
    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale. 
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione). 
     */
    private static final long serialVersionUID = 6619103818860851921L;
    /**
     *  Nome di questa classe 
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * Pagina a cui la command reindirizza per mostrare la lista dei trattamenti
     */
    private static final String fileElenco = "/jsp/trElenco.jsp";
    /**
     * Pagina a cui la command reindirizza per mostrare il dettaglio di un trattamento
     */
    private static final String fileDettaglio = "/jsp/trTrattamento.jsp";
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutte le pagine gestite da questa Command
     */    
    private static final HashMap<String, String> pages = new HashMap<>();
    /**
     *  Tabella chiave/valore contenente il numero di quesiti per ogni rilevazione
     */
    private static ConcurrentHashMap<String, Integer> questionAmounts; 

    
    /** 
     * Crea una nuova istanza di WbsCommand 
     */
    public RegisterCommand() {
        /*;*/   // It doesn't anything
    }
  
    
    /** 
     * <p>Raccoglie i valori dell'oggetto ItemBean
     * e li passa a questa classe command.</p>
     *
     * @param voceMenu la VoceMenuBean pari alla Command presente.
     * @throws CommandException se l'attributo paginaJsp di questa command non e' stato valorizzato.
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
        // Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        pages.put(COMMAND_REGISTER,          fileElenco);
    }  
  
    
    /**
     * <p>Gestisce il flusso principale.</p>
     * <p>Prepara i bean.</p>
     * <p>Passa nella Request i valori che verranno utilizzati dall'applicazione.</p>
     * 
     * @param req la HttpServletRequest contenente la richiesta del client
     * @throws CommandException se si verifica un problema, tipicamente nell'accesso a campi non accessibili o in qualche altro tipo di puntamento 
     */
    @Override
    public void execute(HttpServletRequest req) 
                 throws CommandException {
        /* ******************************************************************** *
         *              Dichiara e inizializza variabili locali                 *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Utente loggato
        PersonBean user = null;
        // Trattamento specifico
        ProcessingBean treat = null;
        // Elenco dei trattamenti legati alla rilevazione
        ArrayList<ItemBean> treats = null;
        // Elenco strutture collegate alla rilevazione
        ArrayList<DepartmentBean> structs = null;
        // Elenco processi collegati alla rilevazione
        ArrayList<ProcessBean> macros = null;
        // Oggetto rilevazione
        CodeBean survey = null;
        // Tabella che conterrà i valori dei parametri passati dalle form
        HashMap<String, LinkedHashMap<String, String>> params = null;
        // Predispone le BreadCrumbs personalizzate per la Command corrente
        LinkedList<ItemBean> bC = null;
        // Variabile contenente l'indirizzo per la redirect da una chiamata POST a una chiamata GET
        String redirect = null;
        /* ******************************************************************** *
         *                    Recupera parametri e attributi                    *
         * ******************************************************************** */
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter(PARAM_SURVEY, DASH);
        // Flag di scrittura
        Boolean writeAsObject = (Boolean) req.getAttribute("w");
        boolean write = writeAsObject.booleanValue();
        // Recupera o inizializza 'tipo pagina'   
        String part = parser.getStringParameter("p", DASH);
        // Recupera o inizializza 'id trattamento'   
        String codeT = parser.getStringParameter("idT", DASH);
        /* ******************************************************************** *
         *      Instanzia nuova classe DBWrapper per il recupero dei dati       *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *                        Avoids the Garden Gate                        *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            if (ses == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
            }
            // Bisogna essere autenticati 
            user = (PersonBean) ses.getAttribute("usr");
            // Cioè bisogna che l'utente corrente abbia una sessione valida
            if (user == null) {
                throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n");
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
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null, probabilmente nel tentativo di recuperare l\'utente.\n";
            LOG.severe(msg);
            throw new CommandException("Attenzione: controllare di essere autenticati nell\'applicazione!\n" + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *                          Corpo del programma                         *
         * ******************************************************************** */
        // Decide il valore della pagina
        try {
            // Controllo sull'input
            if (!codeSur.equals(DASH)) {
                // Creazione della tabella che conterrà i valori dei parametri passati dalle form
                params = new HashMap<>();
                // Carica in ogni caso i parametri di navigazione
                loadParams(part, parser, params);
                // Recupera oggetto rilevazione a partire da suo attributo
                survey = ConfigManager.getSurvey(codeSur);
                // @PostMapping
                if (write) {

                // @GetMapping
                } else {
                    /* ************************************************ *
                     *                Manage Treatment Part             *
                     * ************************************************ */
                    if (pages.containsKey(part)) {
                        fileJspT = pages.get(part);
                    } else {
                        /* ************************************************ *
                         *             SELECT a Specific Treatment          *
                         * ************************************************ */
                        if (!codeT.equals(DASH)) {
                            // Recupera il trattamento dati
                            ItemBean stato = new ItemBean(GET_ALL_BY_CLAUSE, GET_ALL_BY_CLAUSE);
                            treat = db.getTrattamento(user, codeT, stato, survey);
                            // Ha bisogno di personalizzare le breadcrumbs
                            LinkedList<ItemBean> breadCrumbs = (LinkedList<ItemBean>) req.getAttribute("breadCrumbs");
                            bC = HomeCommand.makeBreadCrumbs(breadCrumbs, ELEMENT_LEV_1, "Trattamento Dati");
                            // Pagina
                            fileJspT = fileDettaglio;
                        } else {
                            /* ************************************************ *
                             *             SELECT List of Treatments            *
                             * ************************************************ */
                            treats = db.getTrattamenti(user, survey);
                            fileJspT = fileElenco;                            
                        }
                    }
                }
            } else {
                // Se siamo qui vuol dire che l'identificativo della rilevazione non è significativo, il che vuol dire che qualcuno ha pasticciato con l'URL
                HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
                ses.invalidate();
                String msg = FOR_NAME + "Qualcuno ha tentato di inserire un indirizzo nel browser avente un codice rilevazione non valido!.\n";
                LOG.severe(msg);
                throw new CommandException("Attenzione: indirizzo richiesto non valido!\n");
            }
        } catch (IllegalStateException ise) {
            String msg = FOR_NAME + "Impossibile redirigere l'output. Verificare se la risposta e\' stata gia\' committata.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ise.getMessage(), ise);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Si e\' verificato un problema in una conversione di tipo.\n";
            LOG.severe(msg);
            throw new CommandException(msg + cce.getMessage(), cce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n Attenzione: controllare di essere autenticati nell\'applicazione!\n";
            LOG.severe(msg);
            throw new CommandException(msg + npe.getMessage(), npe);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
        /* ******************************************************************** *
         *              Settaggi in request dei valori calcolati                *
         * ******************************************************************** */
        // Imposta nella request oggetto trattamento specifico
        if (treat != null) {
            req.setAttribute("trattamento", treat);
        }      
        // Imposta nella request elenco completo registro trattamenti
        if (treats != null) {
            req.setAttribute("registro", treats);
        }
        // Imposta nella request elenco completo strutture
        if (structs != null) {
            req.setAttribute("strutture", structs);
        }
        // Imposta nella request elenco completo processi
        if (macros != null) {
            req.setAttribute("processi", macros);
        }
        // Imposta l'eventuale indirizzo a cui redirigere
        if (redirect != null) {
            req.setAttribute("redirect", redirect);
        }
        // Imposta struttura contenente tutti i parametri di navigazione già estratti
        if (!params.isEmpty()) {
            req.setAttribute("params", params);
        }
        // Imposta nella request le breadcrumbs in caso siano state personalizzate
        if (bC != null) {
            req.removeAttribute("breadCrumbs");
            req.setAttribute("breadCrumbs", bC);
        }
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }
    
    
    /**
     * <p>Estrae l'elenco dei quesiti e, per ogni quesito figlio trovato,
     * lo valorizza con gli attributi aggiuntivi (tipo, formulazione, etc.)</p>
     * 
     * @param user          utente loggato
     * @param codeSurvey    codice testuale della rilevazione
     * @param idQ           identificativo del quesito 
     * @param getAll        flag specificante, se vale -1, che si vogliono recuperare tutte le strutture collegate a tutti i macro/processi
     * @param db            databound gia' istanziato
     * @return <code>ArrayList&lt;QuestionBean&gt;</code> - ArrayList di quesiti trovati completi di quesiti figli (quesiti "di cui")
     * @throws CommandException se si verifica un problema nella query o nell'estrazione, nel recupero di valori o in qualche altro tipo di puntamento
     *//*
    public static ArrayList<QuestionBean> retrieveQuestions(PersonBean user,
                                                            String codeSurvey,
                                                            int idQ,
                                                            int getAll,
                                                            DBWrapper db)
                                                     throws CommandException {
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean survey = ConfigManager.getSurvey(codeSurvey);
        // Dichiara la lista di quesiti finiti
        ArrayList<QuestionBean> richQuestions = new ArrayList<>();
        try {
            // Chiama il metodo (ricorsivo) del databound che estrae i quesiti valorizzati
            ArrayList<QuestionBean> questions = db.getQuestions(user, survey, idQ, getAll);
            // Cicla sui quesiti trovati
            for (QuestionBean current : questions) {
                // Per ogni quesito verifica se ha figli
                ArrayList<QuestionBean> childQuestions = current.getChildQuestions();
                // Se li ha:
                if (childQuestions != null) {
                    // Per ogni figlio (i figli hanno solo gli id)
                    for (int i = NOTHING; i < childQuestions.size(); i++) {
                        // Recupera il figlio
                        QuestionBean child = childQuestions.get(i);
                        // Lo arricchisce con il tipo e gli altri attributi
                        QuestionBean richChild = db.getQuestions(user, survey, child.getId(), child.getId()).get(NOTHING);
                        // Sostituisce il tipo "arricchito" al tipo "povero"
                        childQuestions.set(i, richChild);
                    }
                    current.setChildQuestions(childQuestions);
                }
                richQuestions.add(current);
            }
            return richQuestions;
        } catch (WebStorageException wse) {
            String msg = FOR_NAME + "Si e\' verificato un problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + wse.getMessage(), wse);
        } catch (Exception e) {
            String msg = FOR_NAME + "Si e\' verificato un problema.\n";
            LOG.severe(msg);
            throw new CommandException(msg + e.getMessage(), e);
        }
    }
    
    
    /**
     * <p>Valorizza per riferimento una mappa contenente tutti i valori 
     * parametrici riscontrati sulla richiesta.</p>
     * 
     * @param part          la sezione del sito corrente
     * @param parser        oggetto per la gestione assistita dei parametri di input, gia' pronto all'uso
     * @param formParams    mappa da valorizzare per riferimento (ByRef)
     * @throws CommandException se si verifica un problema nella gestione degli oggetti data o in qualche tipo di puntamento
     * @throws AttributoNonValorizzatoException se si fa riferimento a un attributo obbligatorio di bean che non viene trovato
     */
    public static void loadParams(String part, 
                                  ParameterParser parser,
                                  HashMap<String, LinkedHashMap<String, String>> formParams)
                           throws CommandException, 
                                  AttributoNonValorizzatoException {
        LinkedHashMap<String, String> struct = new LinkedHashMap<>();
        LinkedHashMap<String, String> proat = new LinkedHashMap<>();
        LinkedHashMap<String, String> answs = new LinkedHashMap<>();
        LinkedHashMap<String, String> survey = new LinkedHashMap<>();
        LinkedHashMap<String, String> quest = new LinkedHashMap<>();
        LinkedHashMap<String, String> risk = new LinkedHashMap<>();
        /* **************************************************** *
         *     Caricamento parametro di Codice Rilevazione      *
         * **************************************************** */      
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera l'oggetto rilevazione a partire dal suo codice
        CodeBean surveyAsBean = ConfigManager.getSurvey(codeSur);
        // Inserisce l'ìd della rilevazione come valore del parametro
        survey.put(PARAM_SURVEY, String.valueOf(surveyAsBean.getId()));
        // Aggiunge data e ora, se le trova
        survey.put("d", parser.getStringParameter("d", VOID_STRING));
        survey.put("t", parser.getStringParameter("t", VOID_STRING));
        // Aggiunge il tutto al dizionario dei parametri
        formParams.put(PARAM_SURVEY, survey);
        /* **************************************************** *
         *     Caricamento parametri di Scelta Struttura        *
         * **************************************************** */        
        struct.put("liv1",  parser.getStringParameter("sliv1", VOID_STRING));
        struct.put("liv2",  parser.getStringParameter("sliv2", VOID_STRING));
        struct.put("liv3",  parser.getStringParameter("sliv3", VOID_STRING));
        struct.put("liv4",  parser.getStringParameter("sliv4", VOID_STRING));
        formParams.put(PART_SELECT_STR, struct);
        /* **************************************************** *
         *      Caricamento parametri di Scelta Processo        *
         * **************************************************** */
        proat.put("liv1",    parser.getStringParameter("pliv1", VOID_STRING));
        proat.put("liv2",    parser.getStringParameter("pliv2", VOID_STRING));
        proat.put("liv3",    parser.getStringParameter("pliv3", VOID_STRING));
    }
    
}