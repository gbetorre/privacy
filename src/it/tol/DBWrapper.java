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

package it.tol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import it.tol.bean.ActivityBean;
import it.tol.bean.BeanUtil;
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
 * <p><code>DBWrapper.java</code> &egrave; la classe che implementa
 * l'accesso ai database utilizzati dall'applicazione nonch&eacute;
 * l'esecuzione delle query e la gestione dei risultati restituiti,
 * che impacchetta in oggetti di tipo JavaBean e restituisce al chiamante.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class DBWrapper implements Query, Constants {

    /**
     * <p>La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.<br />
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera
     * automatica dalla JVM, e questo potrebbe portare a errori
     * riguardo alla serializzazione).</p>
     * <p><small>I moderni IDE, come Eclipse, permettono di assegnare
     * questa costante in due modi diversi:
     *  <ul>
     *  <li>o attraverso un valore di default assegnato a questa costante<br />
     *  (p.es. <code>private static final long serialVersionUID = 1L;</code>)
     *  </li>
     *  <li>o attraverso un valore calcolato tramite un algoritmo implementato
     *  internamente<br /> (p.es.
     *  <code>private static final long serialVersionUID = -8762739881448133461L;</code>)
     *  </li></small></p>
     */
    private static final long serialVersionUID = -926403476135499718L;
    /**
     * <p>Logger della classe per scrivere i messaggi di errore.
     * All logging goes through this logger.</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    protected static Logger LOG = Logger.getLogger(DBWrapper.class.getName());
    /**
     * <p>Nome di questa classe
     * (viene utilizzato per contestualizzare i messaggi di errore).</p>
     * <p>Non &egrave; privata ma Default (friendly) per essere visibile
     * negli oggetti ovverride implementati da questa classe.</p>
     */
    static final String FOR_NAME = "\n" + Logger.getLogger(new Throwable().getStackTrace()[0].getClassName()) + ": ";
    /**
     * <p>Connessione db postgres di tol.</p>
     */
    protected static DataSource tol_manager = null;
    /**
     * <p>Recupera da Servlet la stringa opportuna per il puntamento del DataSource.</p>
     */
    private static String contextDbName = ConfigManager.getDbName();


    /**
     * <p>Costruttore. Prepara le connessioni.</p>
     * <p>Viene usata l'interfaccia
     * <a href="https://docs.oracle.com/javase/7/docs/api/javax/sql/DataSource.html">DataSource</a>
     * per ottenere le connessioni.</p>
     * <p>Usa il pattern Singleton per evitare di aprire connessioni inutili
     * in caso di connessioni gi&agrave; aperte.</p>
     *
     * @throws WebStorageException in caso di mancata connessione al database per errore password o dbms down
     * @see DataSource
     */
    public DBWrapper() throws WebStorageException {
        if (tol_manager == null) {
            try {
                tol_manager = (DataSource) ((Context) new InitialContext()).lookup(contextDbName);
                if (tol_manager == null)
                    throw new WebStorageException(FOR_NAME + "La risorsa " + contextDbName + "non e\' disponibile. Verificare configurazione e collegamenti.\n");
            } catch (NamingException ne) {
                throw new WebStorageException(FOR_NAME + "Problema nel recuperare la risorsa jdbc/tol per problemi di naming: " + ne.getMessage());
            } catch (Exception e) {
                throw new WebStorageException(FOR_NAME + "Errore generico nel costruttore: " + e.getMessage(), e);
            }
        }
    }

    /* ********************************************************** *
     *               Metodi generali dell'applicazione            *
     * ********************************************************** */

    /**
     * <p>Restituisce un Vector di Command.</p>
     *
     * @return <code>Vector&lt;ItemBean&gt;</code> - lista di ItemBean rappresentanti ciascuno una Command dell'applicazione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public Vector<ItemBean> lookupCommand()
                                   throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        ItemBean cmd = null;
        Vector<ItemBean> commands = new Vector<>();
        try {
            con = tol_manager.getConnection();
            pst = con.prepareStatement(LOOKUP_COMMAND);
            pst.clearParameters();
            rs = pst.executeQuery();
            while (rs.next()) {
                cmd = new ItemBean();
                BeanUtil.populate(cmd, rs);
                commands.add(cmd);
            }
            return commands;
        } catch (SQLException sqle) {
            throw new WebStorageException(FOR_NAME + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = "Connessione al database in stato inconsistente!\nAttenzione: la connessione vale " + con + "\n";
                LOG.severe(msg);
                throw new WebStorageException(FOR_NAME + msg + npe.getMessage(), npe);
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage(), sqle);
            }
        }
    }


    /**
     * <p>Restituisce
     * <ul>
     * <li>il massimo valore del contatore identificativo di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il max(id)
     * @return <code>int</code> - un intero che rappresenta il massimo valore trovato, oppure zero se non sono stati trovati valori
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getMax(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = 0;
            String query = SELECT_MAX_ID + table;
            con = tol_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il max(id).\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce
     * <ul>
     * <li>il minimo valore del contatore identificativo di una
     * tabella il cui nome viene passato come argomento</li>
     * <li>oppure zero se nella tabella non sono presenti record.</li>
     * </ul></p>
     *
     * @param table nome della tabella di cui si vuol recuperare il min(id)
     * @return <code>int</code> - un intero che rappresenta il minimo valore trovato, oppure zero se non sono stati trovati valori
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "static-method", "null" })
    public int getMin(String table)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            int count = 0;
            String query = SELECT_MIN_ID + table;
            con = tol_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }  catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare il max(id).\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }
    

    /**
     * <p>Restituisce il primo valore trovato data una query 
     * passata come parametro</p>
     *
     * @param query da eseguire
     * @return <code>String</code> - stringa restituita
     * @throws WebStorageException se si verifica un problema nella query o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public String get(String query)
               throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String value = null;
        try {
            con = tol_manager.getConnection();
            pst = con.prepareStatement(query);
            pst.clearParameters();
            rs = pst.executeQuery();
            if (rs.next()) {
                value = rs.getString(1);
            }
            return value;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Impossibile recuperare un valore.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce un CodeBean contenente la password criptata e il seme
     * per poter verificare le credenziali inserite dall'utente.</p>
     *
     * @param username   username della persona che ha richiesto il login
     * @return <code>CodeBean</code> - CodeBean contenente la password criptata e il seme
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public CodeBean getEncryptedPassword(String username)
                                  throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean password = null;
        int nextInt = 0;
        try {
            con = tol_manager.getConnection();
            pst = con.prepareStatement(GET_ENCRYPTEDPASSWORD);
            pst.clearParameters();
            pst.setString(++nextInt, username);
            rs = pst.executeQuery();
            if (rs.next()) {
                password = new CodeBean();
                BeanUtil.populate(password, rs);
            }
            return password;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Restituisce un PersonBean rappresentante un utente loggato.</p>
     *
     * @param username  username della persona che ha eseguito il login
     * @param password  password della persona che ha eseguito il login
     * @return <code>PersonBean</code> - PersonBean rappresentante l'utente loggato
     * @throws it.tol.exception.WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     * @throws it.tol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e l'id della persona non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    @SuppressWarnings({ "null", "static-method" })
    public PersonBean getUser(String username,
                              String password)
                       throws WebStorageException, AttributoNonValorizzatoException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs, rs1 = null;
        PersonBean usr = null;
        int nextInt = 0;
        Vector<CodeBean> vRuoli = new Vector<>();
        try {
            con = tol_manager.getConnection();
            pst = con.prepareStatement(GET_USR);
            pst.clearParameters();
            pst.setString(++nextInt, username);
            pst.setString(++nextInt, password);
            pst.setString(++nextInt, password);
            rs = pst.executeQuery();
            if (rs.next()) {
                usr = new PersonBean();
                BeanUtil.populate(usr, rs);
                // Se ha trovato l'utente, ne cerca il ruolo
                pst = null;
                pst = con.prepareStatement(GET_RUOLOUTENTE);
                pst.clearParameters();
                pst.setString(1, username);
                rs1 = pst.executeQuery();
                while(rs1.next()) {
                    CodeBean ruolo = new CodeBean();
                    BeanUtil.populate(ruolo, rs1);
                    vRuoli.add(ruolo);
                }
                usr.setRuoli(vRuoli);
            }
            // Just tries to engage the Garbage Collector
            pst = null;
            // Get Out
            return usr;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto PersonBean non valorizzato; problema nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query dell\'utente.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Verifica se per l'utente loggato esiste una tupla che indica
     * un precedente login.
     * <ul>
     * <li>Se non esiste una tupla per l'utente loggato, la inserisce.</li>
     * <li>Se esiste una tupla per l'utente loggato, la aggiorna.</li>
     * </ul>
     * In questo modo, il metodo gestisce nella tabella degli accessi
     * sempre l'ultimo accesso e non quelli precedenti.</p>
     *
     * @param username      login dell'utente (username usato per accedere)
     * @throws WebStorageException se si verifica un problema SQL o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null" })
    public void manageAccess(String username)
                      throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean accessRow = null;
        int nextParam = NOTHING;
        try {
            // Ottiene la connessione
            con = tol_manager.getConnection();
            // Verifica se la login abbia già fatto un accesso
            pst = con.prepareStatement(GET_ACCESSLOG_BY_LOGIN);
            pst.clearParameters();
            pst.setString(++nextParam, username);
            rs = pst.executeQuery();
            if (rs.next()) {    // Esiste già un accesso: lo aggiorna
                accessRow = new CodeBean();
                BeanUtil.populate(accessRow, rs);
                pst = null;
                con.setAutoCommit(false);
                pst = con.prepareStatement(UPDATE_ACCESSLOG_BY_USER);
                pst.clearParameters();
                pst.setString(nextParam, username);
                // Campi automatici: ora ultimo accesso, data ultimo accesso
                pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate()))); // non accetta un GregorianCalendar né una data java.util.Date, ma java.sql.Date
                pst.setTime(++nextParam, Utils.getCurrentTime());   // non accetta una Stringa, ma un oggetto java.sql.Time
                pst.setInt(++nextParam, accessRow.getId());
                pst.executeUpdate();
                con.commit();
            } else {            // Non esiste un accesso: ne crea uno nuovo
                // Chiude e annulla il PreparedStatement rimasto inutilizzato
                pst.close();
                pst = null;
                // BEGIN;
                con.setAutoCommit(false);
                pst = con.prepareStatement(INSERT_ACCESSLOG_BY_USER);
                pst.clearParameters();
                int nextVal = getMax("access_log") + 1;
                pst.setInt(nextParam, nextVal);
                pst.setString(++nextParam, username);
                pst.setDate(++nextParam, Utils.convert(Utils.convert(Utils.getCurrentDate())));
                pst.setTime(++nextParam, Utils.getCurrentTime());
                pst.executeUpdate();
                // END;
                con.commit();
            }
            String msg = "Si e\' loggato l\'utente: " + username +
                         " in data:" + Utils.format(Utils.getCurrentDate()) +
                         " alle ore:" + Utils.getCurrentTime() +
                         ".\n";
            LOG.info(msg);
        } catch (AttributoNonValorizzatoException anve) {
            String msg = FOR_NAME + "Probabile problema nel recupero dell'id dell\'ultimo accesso\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + anve.getMessage(), anve);
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (NumberFormatException nfe) {
            String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + nfe.getMessage(), nfe);
        } catch (NullPointerException npe) {
            String msg = FOR_NAME + "Tupla non aggiornata correttamente; problema nella query che inserisce o in quella che aggiorna ultimo accesso al sistema.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + npe.getMessage(), npe);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }

    /* ************************************************************************ *
     *                            Metodi di SELEZIONE                           *
     * ************************************************************************ */
    
    /**
     * <p>Seleziona i dati della rilevazione.</p>
     * <p>A seconda del valore dei parameri ricevuti, assume i seguenti comportamenti:
     * <dl><dt>se viene passato l'id rilevazione sul primo e secondo parametro</dt>
     * <dd>restituisce i dati della specifica rilevazione, avente tale valore
     * come identificativo,</dd>
     * <dt>se viene passato un valore qualunque sul primo parametro e -1 sul secondo parametro</dt>
     * <dd>restituisce i dati dell'ultima rilevazione che ha trovato,
     * in base all'ordine di data_rilevazione</dd></dl></p>
     * <p>Ritorna un CodeBean rappresentante la rilevazione desiderata, oppure quella che si
     * trova in testa alla lista di rilevazioni ordinate per data rilevazione discendente.</p>
     *
     * @param idSurvey  intero rappresentante l'identificativo dell rilevazione desiderato (oppure un valore qualunque se non si cerca una specifica rilevazione)
     * @param getAll    intero rappresentante il valore convenzionale -1 se si desidera ottenere l'ultima rilevazione (o un valore qualunque se si cerca una specifica rilevazione)
     * @return <code>CodeBean</code> - CodeBean rappresentante la rilevazione rispondente ai criteri di selezione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public CodeBean getSurvey(int idSurvey,
                              int getAll)
                       throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean survey = null;
        try {
            con = tol_manager.getConnection();
            pst = con.prepareStatement(GET_SURVEY);
            pst.clearParameters();
            pst.setInt(1, idSurvey);
            pst.setInt(2, getAll);
            rs = pst.executeQuery();
            if (rs.next()) {
                survey = new CodeBean();
                BeanUtil.populate(survey, rs);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Get out
            return survey;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Oggetto CodeBean non valorizzato; problema nella query dell\'ultima rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query dell\'ultima rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }


    /**
     * <p>Seleziona i dati di una o pi&uacute; rilevazioni.</p>
     * <p>A seconda del valore dei parameri ricevuti, assume i seguenti comportamenti:
     * <dl><dt>se viene passato l'id rilevazione sul primo e secondo parametro</dt>
     * <dd>restituisce una struttura vettoriale contenente un oggetto valorizzato con
     * i dati della specifica rilevazione, avente tale valore come identificativo,</dd>
     * <dt>se viene passato un valore qualunque sul primo parametro e -1 sul secondo parametro</dt>
     * <dd>restituisce una struttura vettoriale contenente un oggetto valorizzato per
     * ogni rilevazione che ha trovato, in base all'ordine specificato nella query</dd></dl></p>
     * <p>Ritorna un ArrayList di CodeBean rappresentante la rilevazione desiderata,
     * oppure tutte le rilevazioni trovate, ordinate per ordine specificato nella query.</p>
     *
     * @param idSurvey  intero rappresentante l'identificativo dell rilevazione desiderato (oppure un valore qualunque se non si cerca una specifica rilevazione)
     * @param getAll    intero rappresentante il valore convenzionale -1 se si desidera ottenere l'ultima rilevazione (o un valore qualunque se si cerca una specifica rilevazione)
     * @return <code>ArrayList&lt;CodeBean&gt;</code> - ArrayList di CodeBean rappresentante la/le rilevazione/i rispondente/i ai criteri di selezione
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nell'accesso al db o in qualche tipo di puntamento
     */
    @SuppressWarnings({ "null", "static-method" })
    public ArrayList<CodeBean> getSurveys(int idSurvey,
                                          int getAll)
                                   throws WebStorageException {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        CodeBean survey = null;
        ArrayList<CodeBean> surveys = new ArrayList<>();
        try {
            con = tol_manager.getConnection();
            pst = con.prepareStatement(GET_SURVEY);
            pst.clearParameters();
            pst.setInt(1, idSurvey);
            pst.setInt(2, getAll);
            rs = pst.executeQuery();
            while (rs.next()) {
                survey = new CodeBean();
                BeanUtil.populate(survey, rs);
                surveys.add(survey);
            }
            // Tries (just tries) to engage the Garbage Collector
            pst = null;
            // Let's go away
            return surveys;
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Struttura di CodeBean non valorizzata; problema nella query dell\'ultima rilevazione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        } catch (ClassCastException cce) {
            String msg = FOR_NAME + "Problema in una conversione di tipi nella query delle rilevazioni.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + cce.getMessage(), cce);
        } finally {
            try {
                con.close();
            } catch (NullPointerException npe) {
                String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + npe.getMessage());
            } catch (SQLException sqle) {
                throw new WebStorageException(FOR_NAME + sqle.getMessage());
            }
        }
    }
    
    
    /**
     * <p>Restituisce l'elenco dei trattamenti dati personali
     * collegati ad una rilevazione il cui identificativo viene 
     * passato come parametro e che si trovano in stato attivo.</p>
     *
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - lista di trattamenti
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings("static-method")
    public ArrayList<ItemBean> getTrattamenti(PersonBean user,
                                              CodeBean survey)
                                       throws WebStorageException {
        try (Connection con = tol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs = null;
            int nextParam = NOTHING;
            ItemBean trattamento = null;
            ArrayList<ItemBean> trattamenti = new ArrayList<>();
            // TODO: Controllare se user è superuser
            try {
                pst = con.prepareStatement(GET_TRATTAMENTI);
                pst.clearParameters();                
                pst.setInt(++nextParam, survey.getId());
                rs = pst.executeQuery();
                while (rs.next()) {
                    trattamento = new ItemBean();
                    BeanUtil.populate(trattamento, rs);
                    trattamenti.add(trattamento);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return trattamenti;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio del bean; verificare identificativo della rilevazione.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    
    
    /**
     * <p>Restituisce uno specifico trattamento di dati personali
     * il cui identificativo viene passato come parametro,
     * collegato ad una rilevazione il cui identificativo viene 
     * passato come parametro e che si trova in uno stato determinato
     * oppure in tutti gli stati, a seconda dei valori passati in un oggetto
     * che il metodo accetta come argomento.</p>
     * // TODO: COMMENTO
     * @param user      oggetto rappresentante la persona loggata, di cui si vogliono verificare i diritti
     * @param idTrattamento 
     * @param stato 
     * @param survey    oggetto contenente i dati della rilevazione
     * @return <code>ArrayList&lt;ItemBean&gt;</code> - lista di trattamenti
     * @throws WebStorageException se si verifica un problema nell'esecuzione della query, nel recupero di attributi obbligatori non valorizzati o in qualche altro tipo di puntamento
     */
    @SuppressWarnings("static-method")
    public ProcessingBean getTrattamento(PersonBean user,
                                         String idTrattamento,
                                         ItemBean stato,
                                         CodeBean survey)
                                  throws WebStorageException {
        try (Connection con = tol_manager.getConnection()) {
            PreparedStatement pst = null;
            ResultSet rs, rs1, rs2 = null;
            int nextParam = NOTHING;
            ProcessingBean trattamento = null;
            //ItemBean extraInfo = null;
            //ActivityBean attivita = null;
            AbstractList<ActivityBean> vAttivita = new ArrayList<>();
            // TODO: Controllare se user è superuser
            try {
                pst = con.prepareStatement(GET_TRATTAMENTO);
                pst.clearParameters();
                pst.setString(++nextParam, idTrattamento);
                pst.setInt(++nextParam, survey.getId());
                pst.setInt(++nextParam, stato.getCod1());
                pst.setInt(++nextParam, stato.getCod2());
                rs = pst.executeQuery();
                if (rs.next()) {
                    trattamento = new ProcessingBean();
                    BeanUtil.populate(trattamento, rs);
                    // Recupera ulteriori informazioni relative al trattamento
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_EXTRAINFO_TRATTAMENTO);
                    pst.clearParameters();
                    pst.setString(++nextParam, idTrattamento);
                    pst.setInt(++nextParam, survey.getId());
                    pst.setInt(++nextParam, stato.getCod1());
                    pst.setInt(++nextParam, stato.getCod2());
                    rs1 = pst.executeQuery();
                    if (rs1.next()) {
                        ItemBean extraInfo = new ItemBean();
                        BeanUtil.populate(extraInfo, rs1);
                        trattamento.setExtraInfos(extraInfo);
                    }
                    // Ha trovato il trattamento: ne cerca le attività
                    nextParam = NOTHING;
                    pst = null;
                    pst = con.prepareStatement(GET_ATTIVITA);
                    pst.clearParameters();
                    pst.setString(++nextParam, idTrattamento);
                    pst.setInt(++nextParam, survey.getId());
                    pst.setInt(++nextParam, stato.getCod1());
                    pst.setInt(++nextParam, stato.getCod2());
                    rs2 = pst.executeQuery();
                    while (rs2.next()) {
                        ActivityBean attivita = new ActivityBean();
                        BeanUtil.populate(attivita, rs2);
                        vAttivita.add(attivita);
                    }
                    trattamento.setAttivita((ArrayList<ActivityBean>) vAttivita);
                }
                // Just tries to engage the Garbage Collector
                pst = null;
                // Get Out
                return trattamento;
            } catch (AttributoNonValorizzatoException anve) {
                String msg = FOR_NAME + "Si e\' verificato un problema nell\'accesso ad un attributo obbligatorio di un bean.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + anve.getMessage(), anve);
            } catch (SQLException sqle) {
                String msg = FOR_NAME + "Oggetto non valorizzato; problema nella query.\n";
                LOG.severe(msg);
                throw new WebStorageException(msg + sqle.getMessage(), sqle);
            } finally {
                try {
                    con.close();
                } catch (NullPointerException npe) {
                    String msg = FOR_NAME + "Ooops... problema nella chiusura della connessione.\n";
                    LOG.severe(msg);
                    throw new WebStorageException(msg + npe.getMessage());
                } catch (SQLException sqle) {
                    throw new WebStorageException(FOR_NAME + sqle.getMessage());
                }
            }
        } catch (SQLException sqle) {
            String msg = FOR_NAME + "Problema con la creazione della connessione.\n";
            LOG.severe(msg);
            throw new WebStorageException(msg + sqle.getMessage(), sqle);
        }
    }
    

    /* ************************************************************************ *
     *                           Metodi di INSERIMENTO                          *
     * ************************************************************************ */

    
    /* ************************************************************************ *
     *                          Metodi di AGGIORNAMENTO                         *
     * ************************************************************************ */



}
