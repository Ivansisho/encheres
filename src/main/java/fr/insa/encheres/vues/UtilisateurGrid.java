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

import com.vaadin.flow.component.grid.Grid;
import fr.insa.encheres.encheres.VuePrincipale;
import fr.insa.encheres.model.Utilisateur;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author ivane
 */
public class UtilisateurGrid extends Grid<Utilisateur>{ 
    
    private List<Utilisateur> datas;
    
    public UtilisateurGrid() {
        
        Column<Utilisateur> cID = this.addColumn(Utilisateur::getId)
                .setHeader("ID");
        cID.setWidth("2em");
        Column<Utilisateur> cNom = this.addColumn(Utilisateur::getNom)
                .setHeader("Nom");
        cNom.setWidth("2em");
        Column<Utilisateur> cPrenom = this.addColumn(Utilisateur::getPrenom)
                .setHeader("Prenom");
        cPrenom.setWidth("2em");
        Column<Utilisateur> cEmail = this.addColumn(Utilisateur::getEmail)
                .setHeader("Email");
        cEmail.setWidth("2em");
        Column<Utilisateur> cPass = this.addColumn(Utilisateur::getPass)
                .setHeader("Pass");
        cPass.setWidth("2em");
        Column<Utilisateur> cCodepostal = this.addColumn(Utilisateur::getCodepostal)
                .setHeader("Codepostal");
        cCodepostal.setWidth("2em");
        cNom.setSortable(true);
       
         this.setItems(datas);
        // pour affichage compact pour transparents
        // this.setMaxHeight("12em");
    }
    
    public void addUtilisateurs(Collection<Utilisateur> toAdd) {
        this.datas.addAll(toAdd);
        this.setItems(this.datas);
    }
    
    public void removeUtilisateurs(Collection<Utilisateur> toRemove) {
        this.datas.removeAll(toRemove);
        this.setItems(this.datas);
    }

    /**
     * @return the datas
     */
    public List<Utilisateur> getDatas() {
        return datas;
    }
    


    
}
