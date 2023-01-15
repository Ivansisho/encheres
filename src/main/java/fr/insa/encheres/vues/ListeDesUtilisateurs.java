/*
    Copyright 2000- Francois de Bertrand de Beuvron

    This file is part of CoursBeuvron.

    CoursBeuvron is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CoursBeuvron is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.encheres.vues;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.insa.encheres.encheres.VuePrincipale;
import fr.insa.encheres.bdd.GestionBdD;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivane
 */
public class ListeDesUtilisateurs extends VerticalLayout {
    
    private VuePrincipale main;
    
    private UtilisateurGrid vgAllUsers;

    public ListeDesUtilisateurs(VuePrincipale main) {
        this.main = main;
        this.add(new H3("Liste de tous les utilisateurs"));
        this.vgAllUsers = new UtilisateurGrid();
        this.add(this.vgAllUsers);
    }
}
