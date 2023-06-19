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

package it.tol.bean;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

import it.tol.exception.AttributoNonValorizzatoException;

/**
 * <p>Classe che serve a rappresentare trattamenti di dati</p>
 * <p>Un trattamento dati &egrave; un aggregatore di informazioni relative
 * alla natura dei dati trattati e ad una serie di informazioni accessorie; p.es.:
 * dove vengono memorizzati i dati; chi li gestisce; quali misure di sicurezza
 * sono attuate; etc.</p>
 * 
 * @author <a href="mailto:gianroberto.torre@gmail.com">Giovanroberto Torre</a>
 */
public class ProcessingBean extends CodeBean {

    /**
     * La serializzazione necessita di dichiarare una costante di tipo long
     * identificativa della versione seriale.
     * (Se questo dato non fosse inserito, verrebbe calcolato in maniera automatica
     * dalla JVM, e questo potrebbe portare a errori riguardo alla serializzazione).
     */
    private static final long serialVersionUID = 5105019633026559685L;
    /**     Codice del trattamento                                                          */
    private String codice;
    /**     Descrizione                                                                     */
    private String descrizione;   
    /**     Finalit&agrave; del trattamento                                                 */
    private String finalita;
    /**     Basi giuridiche del trattamento                                                 */
    private ArrayList<ActivityBean> basiGiuridiche;
    /**     Termini ultimi del trattamento                                                  */
    private String terminiUltimi;
    /**     Ulteriori informazioni                                                          */
    private String extraInfo;
    /**     Titolari del trattamento                                                        */
    private ArrayList<DepartmentBean> titolari;
    /**     Responsabile del trattamento                                                    */
    private DepartmentBean responsabile;
    /**     Attivit&agrave; del trattamento                                                 */
    private ArrayList<ActivityBean> attivita;
    /**     Categorie di interessati dal trattamento                                        */
    private ArrayList<CodeBean> interessati;
    /**     Categorie di destinatari del trattamento                                        */
    private ArrayList<DepartmentBean> destinatari;
    /**     Banche dati del trattamento                                                     */
    private ArrayList<ProcessBean> bancheDati;
    /**     Flag di trattamento dati personali                                              */
    private boolean datiPersonali;
    /**     Flag di trattamento dati sanitari                                               */
    private boolean datiSanitari; 
    /**     Flag di trattamento dati relativi all'orientamento sessuale                     */
    private boolean datiOrientamentoSex; 
    /**     Flag di trattamento dati relativi ad etnia, religione o appartenenza sindacale  */
    private boolean datiEtniaReligApp;
    /**     Flag di trattamento dati personali di soggetti minorenni                        */
    private boolean datiMinoreEta;
    /**     Flag di trattamento dati genetici                                               */
    private boolean datiGenetici;
    /**     Flag di trattamento dati biometrici                                             */
    private boolean datiBiometrici;
    /**     Flag di trattamento dati giudiziari                                             */
    private boolean datiGiudiziari;
    /**     Flag di trattamento dati relativi all'ubicazione                                */
    private boolean datiUbicazione;
    /**     Flag di trattamento dati pseudonimizzati                                        */
    private boolean datiPseudonimizzati;
    /**     Flag di trattamento dati anonimizzati                                           */
    private boolean datiAnonimizzati;
    /**     Data ultima modifica                                                            */
    private Date dataUltimaModifica;
    /**     Ora ultima modifica                                                             */
    private Time oraUltimaModifica;
    /**     Autore ultima modifica                                                          */
    private int autoreUltimaModifica;
    /**     Id del tipo del trattamento                                                     */
    private int idTipo;
    /**     Id dello stato del trattamento                                                  */
    private int idStato;
    /**     Ulteriori estremi del trattamento                                               */
    private ItemBean extraInfos;
    
    
    /**
     * <p>Override Costruttore di Default</p>
     * <p>Inizializza le variabili di classe a valori convenzionali</p>
     */
    public ProcessingBean() {
        super();
        codice = descrizione = finalita = terminiUltimi = extraInfo = null;
        titolari = null; 
        responsabile = null;
        attivita = basiGiuridiche = null;
        interessati = null;
        destinatari = null;
        bancheDati = null;
        datiPersonali = datiSanitari = datiOrientamentoSex = datiEtniaReligApp = datiMinoreEta = datiGenetici = datiBiometrici = datiGiudiziari = datiUbicazione = datiPseudonimizzati = datiAnonimizzati = false;
        dataUltimaModifica = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = idTipo = idStato = BEAN_DEFAULT_ID;
        extraInfos = null;
    }

    
    /**
     * <p>Costruttore per clonazione.</p>
     * <p>Inizializza i campi a valori presi da un altro bean &dash; se sono valorizzati
     * &dash;, altrimenti li inizializza a valori di default.</p>   
     *              
     * @see it.tol.bean.CodeBean#CodeBean(it.tol.bean.CodeBean)
     * @param o oggetto CodeBean i cui valori devono essere copiati
     * @throws AttributoNonValorizzatoException se l'identificativo dell'oggetto passato come argomento non e' valorizzato!
     */
    public ProcessingBean(CodeBean o) throws AttributoNonValorizzatoException {
        super(o);
        codice = descrizione = finalita = terminiUltimi = extraInfo = null;
        titolari = null; 
        responsabile = null;
        attivita = basiGiuridiche = null;
        interessati = null;
        destinatari = null;
        bancheDati = null;
        datiPersonali = datiSanitari = datiOrientamentoSex = datiEtniaReligApp = datiMinoreEta = datiGenetici = datiBiometrici = datiGiudiziari = datiUbicazione = datiPseudonimizzati = datiAnonimizzati = false;
        dataUltimaModifica = new Date(0);
        oraUltimaModifica = null;
        autoreUltimaModifica = idTipo = idStato = BEAN_DEFAULT_ID;
        extraInfos = null;
    }


    /**
     * @return the codice
     * @throws it.tol.exception.AttributoNonValorizzatoException  eccezione che viene sollevata se questo oggetto viene usato e codice non &egrave; stato valorizzato (&egrave; un dato obbligatorio)
     */
    public String getCodice() throws AttributoNonValorizzatoException {
        if (codice == null) {
            throw new AttributoNonValorizzatoException("Attributo codice non valorizzato!");
        }
        return codice;
    }

    /**
     * @param codice the codice to set
     */
    public void setCodice(String codice) {
        this.codice = codice;
    }

    
    /**
     * @return the descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @param note the descrizione to set
     */
    public void setDescrizione(String note) {
        this.descrizione = note;
    }
    

    /**
     * @return the finalita
     */
    public String getFinalita() {
        return finalita;
    }


    /**
     * @param finalita the finalita to set
     */
    public void setFinalita(String finalita) {
        this.finalita = finalita;
    }


    /**
     * @return the basiGiuridiche
     */
    public ArrayList<ActivityBean> getBasiGiuridiche() {
        return basiGiuridiche;
    }


    /**
     * @param basiGiuridiche the basiGiuridiche to set
     */
    public void setBasiGiuridiche(ArrayList<ActivityBean> basiGiuridiche) {
        this.basiGiuridiche = basiGiuridiche;
    }


    /**
     * @return the terminiUltimi
     */
    public String getTerminiUltimi() {
        return terminiUltimi;
    }


    /**
     * @param terminiUltimi the terminiUltimi to set
     */
    public void setTerminiUltimi(String terminiUltimi) {
        this.terminiUltimi = terminiUltimi;
    }


    /**
     * @return the extraInfo
     */
    public String getExtraInfo() {
        return extraInfo;
    }

    /**
     * @param extraInfo the extraInfo to set
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    
    /**
     * <p>Restituisce i titolari di un trattamento dati.
     * Un trattamento pu&ograve; avere uno o pi&uacute; titolari, che in tal caso
     * sono detti &quot;contitolari&quot;</p>
     * @return <code>ArrayList&lt;DepartmentBean&gt;</code> - titolari del trattamento
     */
    public ArrayList<DepartmentBean> getTitolari() {
        return titolari;
    }

    /**
     * <p>Imposta i titolari di un trattamento dati.</p>
     * @param titolari l'elenco dei titolari da impostare
     */
    public void setTitolari(ArrayList<DepartmentBean> titolari) {
        this.titolari = titolari;
    }


    /**
     * @return the responsabile
     */
    public DepartmentBean getResponsabile() {
        return responsabile;
    }

    /**
     * @param responsabile the responsabile to set
     */
    public void setResponsabile(DepartmentBean responsabile) {
        this.responsabile = responsabile;
    }


    /**
     * @return the attivita
     */
    public ArrayList<ActivityBean> getAttivita() {
        return attivita;
    }


    /**
     * @param attivita the attivita to set
     */
    public void setAttivita(ArrayList<ActivityBean> attivita) {
        this.attivita = attivita;
    }


    /**
     * @return the interessati
     */
    public ArrayList<CodeBean> getInteressati() {
        return interessati;
    }

    /**
     * @param interessati the interessati to set
     */
    public void setInteressati(ArrayList<CodeBean> interessati) {
        this.interessati = interessati;
    }


    /**
     * @return the destinatari
     */
    public ArrayList<DepartmentBean> getDestinatari() {
        return destinatari;
    }

    /**
     * @param destinatari the destinatari to set
     */
    public void setDestinatari(ArrayList<DepartmentBean> destinatari) {
        this.destinatari = destinatari;
    }


    /**
     * @return the bancheDati
     */
    public ArrayList<ProcessBean> getBancheDati() {
        return bancheDati;
    }


    /**
     * @param bancheDati the bancheDati to set
     */
    public void setBancheDati(ArrayList<ProcessBean> bancheDati) {
        this.bancheDati = bancheDati;
    }


    /**
     * @return the datiPersonali
     */
    public boolean isDatiPersonali() {
        return datiPersonali;
    }


    /**
     * @param datiPersonali the datiPersonali to set
     */
    public void setDatiPersonali(boolean datiPersonali) {
        this.datiPersonali = datiPersonali;
    }


    /**
     * @return the datiSanitari
     */
    public boolean isDatiSanitari() {
        return datiSanitari;
    }


    /**
     * @param datiSanitari the datiSanitari to set
     */
    public void setDatiSanitari(boolean datiSanitari) {
        this.datiSanitari = datiSanitari;
    }


    /**
     * @return the datiOrientamentoSex
     */
    public boolean isDatiOrientamentoSex() {
        return datiOrientamentoSex;
    }


    /**
     * @param datiOrientamentoSex the datiOrientamentoSex to set
     */
    public void setDatiOrientamentoSex(boolean datiOrientamentoSex) {
        this.datiOrientamentoSex = datiOrientamentoSex;
    }


    /**
     * @return the datiEtniaReligApp
     */
    public boolean isDatiEtniaReligApp() {
        return datiEtniaReligApp;
    }


    /**
     * @param datiEtniaReligApp the datiEtniaReligApp to set
     */
    public void setDatiEtniaReligApp(boolean datiEtniaReligApp) {
        this.datiEtniaReligApp = datiEtniaReligApp;
    }


    /**
     * @return the datiMinoreEta
     */
    public boolean isDatiMinoreEta() {
        return datiMinoreEta;
    }


    /**
     * @param datiMinoreEta the datiMinoreEta to set
     */
    public void setDatiMinoreEta(boolean datiMinoreEta) {
        this.datiMinoreEta = datiMinoreEta;
    }


    /**
     * @return the datiGenetici
     */
    public boolean isDatiGenetici() {
        return datiGenetici;
    }


    /**
     * @param datiGenetici the datiGenetici to set
     */
    public void setDatiGenetici(boolean datiGenetici) {
        this.datiGenetici = datiGenetici;
    }


    /**
     * @return the datiBiometrici
     */
    public boolean isDatiBiometrici() {
        return datiBiometrici;
    }


    /**
     * @param datiBiometrici the datiBiometrici to set
     */
    public void setDatiBiometrici(boolean datiBiometrici) {
        this.datiBiometrici = datiBiometrici;
    }


    /**
     * @return the datiGiudiziari
     */
    public boolean isDatiGiudiziari() {
        return datiGiudiziari;
    }


    /**
     * @param datiGiudiziari the datiGiudiziari to set
     */
    public void setDatiGiudiziari(boolean datiGiudiziari) {
        this.datiGiudiziari = datiGiudiziari;
    }


    /**
     * @return the datiUbicazione
     */
    public boolean isDatiUbicazione() {
        return datiUbicazione;
    }


    /**
     * @param datiUbicazione the datiUbicazione to set
     */
    public void setDatiUbicazione(boolean datiUbicazione) {
        this.datiUbicazione = datiUbicazione;
    }


    /**
     * @return the datiPseudonimizzati
     */
    public boolean isDatiPseudonimizzati() {
        return datiPseudonimizzati;
    }


    /**
     * @param datiPseudonimizzati the datiPseudonimizzati to set
     */
    public void setDatiPseudonimizzati(boolean datiPseudonimizzati) {
        this.datiPseudonimizzati = datiPseudonimizzati;
    }


    /**
     * @return the datiAnonimizzati
     */
    public boolean isDatiAnonimizzati() {
        return datiAnonimizzati;
    }


    /**
     * @param datiAnonimizzati the datiAnonimizzati to set
     */
    public void setDatiAnonimizzati(boolean datiAnonimizzati) {
        this.datiAnonimizzati = datiAnonimizzati;
    }

    /* *********************************************************** *
     *       Metodi getter e setter per data ultima modifica       *
     * *********************************************************** */
    /**
     * Restituisce la data dell'ultima modifica del trattamento
     *
     * @return <code>java.util.Date</code> - data dell'ultima modifica
     */
    public Date getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    /**
     * Imposta la data dell'ultima del trattamento
     *
     * @param dataUltimaModifica data ultima modifica da impostare
     */
    public void setDataUltimaModifica(Date dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }


    /* *********************************************************** *
     *       Metodi getter e setter per ora ultima modifica        *
     * *********************************************************** */
    /**
     * Restituisce l'ora dell'ultima modifica di un trattamento
     *
     * @return <code>java.sql.Time</code> - ora dell'ultima modifica
     */
    public Time getOraUltimaModifica() {
        return oraUltimaModifica;
    }

    /**
     * Imposta l'ora dell'ultima modifica di un trattamento
     *
     * @param oraUltimaModifica ora ultima modifica da impostare
     */
    public void setOraUltimaModifica(Time oraUltimaModifica) {
        this.oraUltimaModifica = oraUltimaModifica;
    }


    /* ************************************************************** *
     *       Metodi getter e setter per autore ultima modifica        *
     * ************************************************************** */
    /**
     * Restituisce l'autore dell'ultima modifica di un trattamento
     *
     * @return <code>int</code> - autore ultima modifica
     */
    public int getAutoreUltimaModifica() {
        return autoreUltimaModifica;
    }

    /**
     * Imposta l'autore dell'ultima modifica di un trattamento
     *
     * @param autoreUltimaModifica autore ultima modifica da impostare
     */
    public void setAutoreUltimaModifica(int autoreUltimaModifica) {
        this.autoreUltimaModifica = autoreUltimaModifica;
    }


    /**
     * @return the idTipo
     */
    public int getIdTipo() {
        return idTipo;
    }


    /**
     * @param idTipo the idTipo to set
     */
    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }


    /**
     * @return the idStato
     */
    public int getIdStato() {
        return idStato;
    }


    /**
     * @param idStato the idStato to set
     */
    public void setIdStato(int idStato) {
        this.idStato = idStato;
    }


    /**
     * @return the extraInfos
     */
    public ItemBean getExtraInfos() {
        return extraInfos;
    }


    /**
     * @param extraInfos the extraInfos to set
     */
    public void setExtraInfos(ItemBean extraInfos) {
        this.extraInfos = extraInfos;
    }
    
}
