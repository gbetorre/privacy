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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.ParameterParser;

import it.tol.ConfigManager;
import it.tol.Constants;
import it.tol.DBWrapper;
import it.tol.Data;
import it.tol.Utils;
import it.tol.bean.CodeBean;
import it.tol.bean.DepartmentBean;
import it.tol.bean.ItemBean;
import it.tol.bean.PersonBean;
import it.tol.bean.ProcessBean;
import it.tol.exception.AttributoNonValorizzatoException;
import it.tol.exception.CommandException;
import it.tol.exception.WebStorageException;


/**
 * <p><code>DepartmentCommand.java</code><br>
 * Implementa la logica per la gestione delle strutture organizzative.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DepartmentCommand extends ItemBean implements Command, Constants {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 7816862687031662585L;
    /**
     *  Nome di questa classe
     *  (utilizzato per contestualizzare i messaggi di errore)
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * Log per debug in produzione
     */
    protected static Logger LOG = Logger.getLogger(ConfigManager.class.getName());
    /**
     * Pagina di default della Command
     */
    private static final String nomeFileElenco = "/jsp/stElenco.jsp";
    /**
     * Nome del file json della Command (dipende dalla pagina di default)
     */
    private String nomeFileJson = nomeFileElenco.substring(nomeFileElenco.lastIndexOf('/'), nomeFileElenco.indexOf(DOT));
    /**
     * Struttura contenente le pagina a cui la command fa riferimento per mostrare tutti gli attributi del progetto
     */
    private static final HashMap<String, String> nomeFile = new HashMap<>();
    /**
     *  Processo di dato id
     */
    ProcessBean runtimeProcess = null;


    /**
     * Crea una nuova istanza di questa Command
     */
    public DepartmentCommand() {
        ;   // It doesn't anything
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
        // Carica la hashmap contenente le pagine da includere in funzione dei parametri sulla querystring
        //nomeFile.put(PART_MACROPROCESS, nomeFileElenco);
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
         *            Crea e inizializza le variabili locali comuni             *
         * ******************************************************************** */
        // Databound
        DBWrapper db = null;
        // Parser per la gestione assistita dei parametri di input
        ParameterParser parser = new ParameterParser(req);
        // Utente loggato
        PersonBean user = null;
        // Recupera o inizializza 'codice rilevazione' (Survey)
        String codeSur = parser.getStringParameter("r", DASH);
        // Recupera o inizializza 'id struttura'
        //int idSt = parser.getIntParameter("id", -1);
        // Recupera o inizializza 'tipo parte'
        //String part = parser.getStringParameter("p", DASH);
        // Dichiara la pagina a cui reindirizzare
        String fileJspT = null;
        // Dichiara elenco di processi
        //AbstractList<ProcessBean> m = new ArrayList<>();
        // Prepara un oggetto contenente i parametri opzionali per i nodi
        ItemBean options = new ItemBean(VOID_STRING,  VOID_STRING,  VOID_STRING, VOID_STRING, STR_PFX, NOTHING);
        /* ******************************************************************** *
         *      Instanzia nuova classe WebStorage per il recupero dei dati      *
         * ******************************************************************** */
        try {
            db = new DBWrapper();
        } catch (WebStorageException wse) {
            throw new CommandException(FOR_NAME + "Non e\' disponibile un collegamento al database\n." + wse.getMessage(), wse);
        }
        /* ******************************************************************** *
         *                         Recupera la Sessione                         *
         * ******************************************************************** */
        try {
            // Recupera la sessione creata e valorizzata per riferimento nella req dal metodo authenticate
            HttpSession ses = req.getSession(IF_EXISTS_DONOT_CREATE_NEW);
            user = (PersonBean) ses.getAttribute("usr");
            if (user == null) {
                throw new CommandException(FOR_NAME + "Attenzione: controllare di essere autenticati nell\'applicazione!\n");
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
         *                   Decide il valore della pagina                      *
         * ******************************************************************** */
        try {
            // Il parametro di navigazione 'rilevazione' è obbligatorio
            if (!codeSur.equals(DASH)) {
                /* Il parametro di navigazione 'p' permette di addentrarsi nelle funzioni
                if (nomeFile.containsKey(part)) {
                    // Viene richiesta la visualizzazione di una struttura specifica
                    if (idSt > NOTHING) {
                        // TODO
                        // Qui va l'eventuale codice per la preparazione info pagina di dettaglio struttura
                        // (nel caso, ricordarsi di recuperare i parametri 'p' ed 'st', commentati più sopra)
                    } else {
                        m = db.getMacroBySurvey(user, codeSur);
                    }
                    fileJspT = nomeFile.get(part);
                } else {     */
                // Viene richiesta la visualizzazione dell'organigramma
                //ArrayList<DepartmentBean> structs = retrieveStructures(codeSur, user, db);
                // Genera il file json contenente le informazioni strutturate
                //printJson(req, structs, nomeFileJson, options);
                // Imposta la jsp
                fileJspT = nomeFileElenco;
              /*}*/
            } else {    // Manca il codice rilevazione!
                String msg = FOR_NAME + "Impossibile recuperare il codice della rilevazione.\n";
                LOG.severe(msg + "E\' possibile che qualcuno abbia alterato il codice rilevazione nell\'URL della pagina.\n");
                throw new CommandException(msg);
            }
        } catch (CommandException ce) {
            String msg = FOR_NAME + "Probabile problema nel recupero di valori dal db.\n";
            LOG.severe(msg);
            throw new CommandException(msg + ce.getMessage(), ce);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Si e\' verificato un problema di puntamento a null.\n";
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
        // Imposta la Pagina JSP di forwarding
        req.setAttribute("fileJsp", fileJspT);
    }

}
