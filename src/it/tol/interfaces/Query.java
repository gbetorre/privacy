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

package it.tol.interfaces;

import java.io.Serializable;


/**
 * <p>Query &egrave; l'interfaccia pubblic contenente tutte le query della
 * web-application &nbsp;<code>Trattamenti on Line (tol)</code>, tranne quelle
 * composte dinamicamente da metodi implementati, di cui comunque dichiara
 * l'interfaccia.</p>
 *
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public interface Query extends Serializable {

    /* ********************************************************************** *
     *                          1. Query comuni                               *
     * ********************************************************************** */
    
    /**
     * <p>Estrae le classi Command previste per la/le applicazione/i.</p>
     */
    public static final String LOOKUP_COMMAND =
            "SELECT " +
            "       id                  AS \"id\"" +
            "   ,   nome                AS \"nomeReale\"" +
            "   ,   nome                AS \"nomeClasse\"" +
            "   ,   token               AS \"nome\"" +
            "   ,   labelweb            AS \"labelWeb\"" +
            "   ,   jsp                 AS \"paginaJsp\"" +
            "   ,   informativa         AS \"informativa\"" +
            "  FROM command";

    /**
     * <p>Estrae l'id massimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MAX_ID =
            "SELECT " +
            "       MAX(id)             AS \"max\"" +
            "   FROM ";

    /**
     * <p>Estrae l'id minimo da una tabella definita nel chiamante</p>
     */
    public static final String SELECT_MIN_ID =
            "SELECT " +
            "       MIN(id)             AS \"min\"" +
            "   FROM ";
    
    /**
     * <p>Estrae l'utente con username e password passati come parametri.</p>
     */
    public static final String GET_USR =
            "SELECT " +
            "       U.id                AS \"usrId\"" +
            "   ,   P.id                AS \"id\"" +
            "   ,   P.nome              AS \"nome\"" +
            "   ,   P.cognome           AS \"cognome\"" +
            "   ,   P.sesso             AS \"sesso\"" +
            "   ,   P.data_nascita      AS \"dataNascita\"" +
            "   ,   P.codice_fiscale    AS \"codiceFiscale\"" +
            "   ,   P.email             AS \"email\"" +
            "   ,   P.cittadinanza      AS \"cittadinanza\"" +
            "   ,   P.note              AS \"note\"" +
            "   FROM usr U" +
            "       INNER JOIN persona P ON P.id = U.id_persona" +
            "   WHERE   login = ?" +
            "       AND (( passwd IS NULL OR passwd = ? ) " +
            "           AND ( passwdform IS NULL OR passwdform = ? ))";

    /**
     * <p>Estrae il ruolo di una persona
     * avente login passato come parametro,
     * assumendo che sulla login ci sia un vincolo di UNIQUE.</p>
     */
    public static final String GET_RUOLOUTENTE =
            "SELECT " +
            "       RA.id               AS \"id\"" +
            "   ,   RA.nome             AS \"nome\"" +
            "   FROM ruolo_applicativo RA " +
            "       INNER JOIN usr U on RA.id = U.id_ruolo" +
            "   WHERE U.login = ?";

    /**
     * <p>Estrae identificativo tupla ultimo accesso, se esiste
     * per l'utente il cui username viene passato come parametro.</p>
     */
    public static final String GET_ACCESSLOG_BY_LOGIN =
            "SELECT " +
            "       A.id                AS  \"id\"" +
            "   FROM access_log A " +
            "   WHERE A.login = ? ";

    /**
     * <p>Estrae la password criptata e il seme dell'utente,
     * identificato tramite username, passato come parametro.</p>
     */
    public static final String GET_ENCRYPTEDPASSWORD =
            "SELECT " +
            "       U.passwdform        AS \"nome\"" +
            "   ,   U.salt              AS \"informativa\"" +
            "   FROM usr U" +
            "   WHERE U.login = ?";

    /**
     * <p>Estrae:
     * <dl><dt>se viene passato l'id rilevazione sul primo e secondo parametro
     * <dd>i dati di una specifica rilevazione, avente id passato come parametro,</dd><br>
     * <p><em>oppure</em></p>
     * <dt>se viene passato -1 sul primo e secondo parametro</dt>
     * <dd>i dati dell'ultima rilevazione che ha trovato,
     * in base all'ordine di data_rilevazione</dd></dl></p>
     */
    public static final String GET_SURVEY =
            "SELECT " +
            "       R.id                AS \"id\"" +
            "   ,   R.codice            AS \"nome\"" +
            "   ,   R.nome              AS \"informativa\"" +
            "   ,   R.ordinale          AS \"ordinale\"" +
            "   FROM rilevazione R" +
            "   WHERE (R.id = ? OR -1 = ?)" +
            "       AND R.chiusa = true" +
            "   ORDER BY data_rilevazione DESC";
    
    /* ********************************************************************** *
     *     2. Query di Selezione specifiche di TOL (Trattamenti On Line)      *
     * ********************************************************************** */

    /**
     * Seleziona l'elenco dei trattamenti di dati in stato attivo e collegati 
     * ad una rilevazione il cui identificativo viene passato come parametro.
     */
    public static final String GET_TRATTAMENTI =
            "SELECT " +
            "       T.codice                    AS \"codice\"" +
            "   ,   T.nome                      AS \"nome\"" +
            "   ,   T.descrizione               AS \"informativa\"" +
            "   ,   T.ordinale                  AS \"ordinale\"" +
            "   FROM trattamento T" +
            "       INNER JOIN rilevazione R ON T.id_rilevazione = R.id" +
            "   WHERE R.id = ?" +
            "       AND T.id_stato = 1" +
            "   ORDER BY codice";
    
    /**
     * Seleziona i dettagli di uno specifico trattamento dati, 
     * in uno stato determinato oppure in qualunque stato 
     * (a seconda dei parametri passati sul primo e sul secondo argomento della clausola); 
     * il codice identificativo del trattamento viene passato 
     * come parametro e il trattamento stesso risulta collegato ad una rilevazione, 
     * il cui identificativo viene passato come parametro.
     */
    public static final String GET_TRATTAMENTO =
            "SELECT " +
            "       T.nome                      AS \"nome\"" +
            "   ,   T.note                      AS \"informativa\"" +
            "   ,   T.ordinale                  AS \"ordinale\"" +
            "   ,   T.codice                    AS \"codice\"" +
            "   ,   T.descrizione               AS \"descrizione\"" +
            "   ,   T.finalita                  AS \"finalita\"" +
            "   ,   T.termini_ultimi            AS \"terminiUltimi\"" +
            "   ,   T.extra_info                AS \"extraInfo\"" +
            "   ,   T.dati_personali            AS \"datiPersonali\"" +
            "   ,   T.dati_sanitari             AS \"datiSanitari\"" +
            "   ,   T.dati_orientamentosex      AS \"datiOrientamentoSex\"" +
            "   ,   T.dati_etnia_relig_app      AS \"datiEtniaReligApp\"" +
            "   ,   T.dati_minore_eta           AS \"datiMinoreEta\"" +
            "   ,   T.dati_genetici             AS \"datiGenetici\"" +
            "   ,   T.dati_biometrici           AS \"datiBiometrici\"" +
            "   ,   T.dati_giudiziari           AS \"datiGiudiziari\"" +
            "   ,   T.dati_ubicazione           AS \"datiUbicazione\"" +
            "   ,   T.dati_pseudonimizzati      AS \"datiPseudonimizzati\"" +
            "   ,   T.dati_anonimizzati         AS \"datiAnonimizzati\"" +
            "   ,   T.data_ultima_modifica      AS \"dataUltimaModifica\"" +
            "   ,   T.ora_ultima_modifica       AS \"oraUltimaModifica\"" +
            "   ,   T.id_usr_ultima_modifica    AS \"autoreUltimaModifica\"" +
            "   ,   T.id_tipo_trattamento       AS \"idTipo\"" +
            "   ,   T.id_stato                  AS \"idStato\"" +
            "   FROM trattamento T" +
            "       INNER JOIN rilevazione R ON T.id_rilevazione = R.id" +
            "   WHERE T.codice = ?" +
            "       AND R.id = ?" +           
            "       AND (T.id_stato = ? OR -1 = ?)";
    
    /**
     * Seleziona ulteriori informazioni relative a un trattamento dati, 
     * in uno stato determinato oppure in qualunque stato 
     * (a seconda dei parametri passati sul primo e sul secondo argomento della clausola); 
     * collegato ad una rilevazione, 
     * il cui identificativo viene passato come parametro.
     */
    public static final String GET_EXTRAINFO_TRATTAMENTO =
            "SELECT " +
            "       T.misure_sicurezza          AS \"extraInfo1\"" +
            "   ,   T.luoghi_custodia           AS \"extraInfo2\"" +
            "   ,   T.destinatari               AS \"extraInfo3\"" +
            "   FROM trattamento T" +
            "       INNER JOIN rilevazione R ON T.id_rilevazione = R.id" +
            "   WHERE T.codice = ?" +
            "       AND R.id = ?" +           
            "       AND (T.id_stato = ? OR -1 = ?)";
    
    /**
     * Seleziona un elenco di attivit&agrave; di trattamento dati, associate: 
     * ad uno specifico trattamento dati:
     * il cui codice identificativo viene passato come parametro, 
     * che si trova in uno stato specificato tramite parametro,
     * e collegato ad una rilevazione avente identificativo passato come parametro.
     */
    public static final String GET_ATTIVITA_TRATTAMENTO =
            "SELECT " +
            "       A.nome                      AS \"nome\"" +
            "   ,   A.ordinale                  AS \"ordinale\"" +
            "   ,   A.codice                    AS \"codice\"" +
            "   ,   A.descrizione               AS \"descrizione\"" +
            "   ,   A.datainizio                AS \"dataInizio\"" +
            "   ,   A.datafine                  AS \"dataFine\"" +
            "   ,   A.data_ultima_modifica      AS \"dataUltimaModifica\"" +
            "   ,   A.ora_ultima_modifica       AS \"oraUltimaModifica\"" +
            "   ,   A.id_usr_ultima_modifica    AS \"autoreUltimaModifica\"" +
            "   FROM attivita A" +
            "       INNER JOIN attivita_trattamento AT ON AT.cod_attivita = A.codice" +
            "       INNER JOIN trattamento T ON AT.cod_trattamento = T.codice" +
            "       INNER JOIN rilevazione R ON A.id_rilevazione = R.id" +
            "   WHERE T.codice = ?" +
            "       AND R.id = ?" +           
            "       AND (T.id_stato = ? OR -1 = ?)";
    
    /**
     * Seleziona l'elenco degli interessati collegati ad uno specifico trattamento di dati: 
     * il cui codice identificativo viene passato come parametro, 
     * che si trova in uno stato specificato tramite parametro,
     * e collegato ad una rilevazione avente identificativo passato come parametro.
     */
    public static final String GET_INTERESSATI_TRATTAMENTO =
            "SELECT " +
            "       I.id                        AS \"id\"" +
            "   ,   I.nome                      AS \"nome\"" +
            "   ,   I.descrizione               AS \"informativa\"" +
            "   ,   I.ordinale                  AS \"ordinale\"" +
            "   FROM interessati I" +
            "       INNER JOIN interessati_trattamento IT ON IT.id_interessati = I.id" +
            "       INNER JOIN trattamento T ON IT.cod_trattamento = T.codice" +
            "       INNER JOIN rilevazione R ON IT.id_rilevazione = R.id" +
            "   WHERE T.codice = ?" +
            "       AND R.id = ?" +
            "       AND (T.id_stato = ? OR -1 = ?)" +
            "   ORDER BY I.nome";
    
    /**
     * Seleziona un elenco di basi giuridiche associate  
     * ad uno specifico trattamento dati:
     * il cui codice identificativo viene passato come parametro, 
     * che si trova in uno stato specificato tramite parametro,
     * e collegato ad una rilevazione avente identificativo passato come parametro.
     */
    public static final String GET_BASI_GIURIDICHE_TRATTAMENTO =
            "SELECT " +
            "       BG.id                       AS \"id\"" +
            "   ,   BG.nome                     AS \"nome\"" +
            "   ,   BG.descrizione              AS \"descrizione\"" +
            "   ,   BG.ordinale                 AS \"ordinale\"" +
            "   ,   BG.tipo_base                AS \"codice\"" +
            "   ,   BGT.note                    AS \"informativa\"" +
            "   FROM base_giuridica BG" +
            "       INNER JOIN base_giuridica_trattamento BGT ON BGT.id_base_giuridica = BG.id" +
            "       INNER JOIN trattamento T ON BGT.cod_trattamento = T.codice" +
            "       INNER JOIN rilevazione R ON BG.id_rilevazione = R.id" +
            "   WHERE T.codice = ?" +
            "       AND R.id = ?" +           
            "       AND (T.id_stato = ? OR -1 = ?)";
    
    /**
     * Seleziona un elenco di banche dati associate  
     * ad uno specifico trattamento dati
     * il cui codice identificativo viene passato come parametro, 
     * che si trova in uno stato specificato tramite parametro,
     * e collegato ad una rilevazione avente identificativo passato come parametro.<br>
     * A ciascun trattamento possono essere associate diverse banche dati;
     * a ciascuna banca dati corrisponde un raggruppamento di:<ul>
     * <li>un solo database</li>
     * <li>una o pi&uacute; misure di sicurezza</li>
     * <li>uno o pi&uacute; luoghi di custodia</li>
     * </ul>
     */
    public static final String GET_BANCHE_DATI_TRATTAMENTO =
            "SELECT " +
            "       BD.id                       AS \"id\"" +
            "   ,   BD.nome                     AS \"nome\"" +
            "   ,   BD.descrizione              AS \"descrizione\"" +
            "   ,   BD.ordinale                 AS \"ordinale\"" +
            "   ,   DB.nome                     AS \"codice\"" +
            "   ,   DB.descrizione              AS \"informativa\"" +
            "   ,   DB.id_tipo_database         AS \"livello\"" +
            "   ,   TD.nome                     AS \"tipo\"" +
            "   ,   BD.data_ultima_modifica     AS \"dataUltimaModifica\"" +
            "   ,   BD.ora_ultima_modifica      AS \"oraUltimaModifica\"" +
            "   ,   BD.id_usr_ultima_modifica   AS \"autoreUltimaModifica\"" +
            "   FROM banca_dati BD" +
            "       INNER JOIN database DB ON BD.id_database = DB.id" +
            "       INNER JOIN tipo_database TD ON DB.id_tipo_database = TD.id" +
            "       INNER JOIN banca_dati_trattamento BDT ON BDT.id_banca_dati = BD.id" +
            "       INNER JOIN trattamento T ON BDT.cod_trattamento = T.codice" +
            "       INNER JOIN rilevazione R ON BD.id_rilevazione = R.id" +
            "   WHERE T.codice = ?" +
            "       AND R.id = ?" +           
            "       AND (T.id_stato = ? OR -1 = ?)";
    
    /* ********************************************************************** *
     *                        3. Query di inserimento                         *
     * ********************************************************************** */
    
    /**
     * <p>Query per inserimento di ultimo accesso al sistema.</p>
     */
    public static final String INSERT_ACCESSLOG_BY_USER =
            "INSERT INTO access_log" +
            "   (   id" +
            "   ,   login" +
            "   ,   data_ultimo_accesso" +
            "   ,   ora_ultimo_accesso )" +
            "   VALUES (? " +          // id
            "   ,       ? " +          // login
            "   ,       ? " +          // dataultimoaccesso
            "   ,       ?)" ;          // oraultimoaccesso
    
    /* ********************************************************************** *
     *                       4. Query di aggiornamento                        *
     * ********************************************************************** */
    
    /**
     * <p>Query per aggiornamento di ultimo accesso al sistema.</p>
     */
    public static final String UPDATE_ACCESSLOG_BY_USER =
            "UPDATE access_log" +
            "   SET login  = ?" +
            "   ,   data_ultimo_accesso = ?" +
            "   ,   ora_ultimo_accesso = ?" +
            "   WHERE id = ? ";
    
    /* ********************************************************************** *
     *                        5. Query di eliminazione                        *
     * ********************************************************************** */

}
