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


import javax.servlet.http.HttpServletRequest;

import it.tol.bean.ItemBean;
import it.tol.exception.CommandException;

/**
 * Command è una semplice interfaccia per realizzare un'applicazione
 * web servlet-centrica.
 *
 * Nell'approccio servlet-centrico esiste una servlet centrale che
 * gestisce la logica dell'applicazione. Ogni azione del web è
 * realizzata da una classe specifica e l'interazione tra la
 * servlet-centrale e le classi avviene tramite questa
 * interfaccia. Questo approccio è anche detto 'command pattern'.
 *
 * @author Roberto Posenato
 */
public interface Command {

    /**
     * Esegue tutte le operazioni necessarie per recuperare le informazioni
     * per la visualizzazione. Tutte le informazioni devono essere inserite
     * nella sessione o in request.
     *
     * @param req HttpServletRequest
     * @throws CommandException incapsula tutte le possibili eccezioni incontrate dalle classi implementanti
     */
    public void execute(HttpServletRequest req)
    throws CommandException;


    /**
     * init supplies a constructor that can't be used
     * directly. Remember that almost of the implementing classes are
     * instancied by the Class.newInstance().
     *
     * @param voce a ItemBean containing al the useful information for instantiation
     * @throws CommandException incapsulate each exception occurred in init by implementing classes
     */
    public void init(ItemBean voce)
    throws CommandException;
}
