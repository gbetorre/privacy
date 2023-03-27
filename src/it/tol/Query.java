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

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * <p>Query &egrave; l'interfaccia pubblic contenente tutte le query della
 * web-application &nbsp;<code>Processi on Line (prol)</code>, tranne quelle
 * composte dinamicamente da metodi implementati, di cui comunque dichiara
 * l'interfaccia.</p>
 * <p>Definisce inoltre alcune costanti di utilit&agrave;.</p>
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
    public static final String GET_TRATTAMENTI =
            "SELECT " +
            "       T.codice            AS \"codice\"" +
            "   ,   T.nome              AS \"nome\"" +
            "   ,   T.descrizione       AS \"informativa\"" +
            "   ,   T.ordinale          AS \"ordinale\"" +
            "   FROM trattamento T" +
            "       INNER JOIN rilevazione R ON T.id_rilevazione = R.id" +
            "   WHERE R.id = ?" +
            "       AND T.id_stato = 1" +
            "   ORDER BY codice";
    
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
