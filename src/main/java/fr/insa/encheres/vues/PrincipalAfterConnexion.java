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

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import fr.insa.encheres.encheres.VuePrincipale;

/**
 *
 * @author ivane
 */
public class PrincipalAfterConnexion extends MyVerticalLayout {

    private VuePrincipale main;

    private Tab encheres;
    private Tabs allTabs;

    private MyVerticalLayout tabContent;

    public PrincipalAfterConnexion(VuePrincipale main) {
        this.main = main;

        this.encheres = new Tab("Encheres de "
                + this.main.getSessionInfo().getUserName());
        this.allTabs.addSelectedChangeListener(event
                -> setTabContent(event.getSelectedTab())
        );
        this.tabContent = new MyVerticalLayout();
        this.tabContent.setHeightFull();
        this.tabContent.setWidthFull();
        this.tabContent.getStyle().set("border", "1px solid black");
        this.add(this.allTabs, this.tabContent);
        this.allTabs.setSelectedTab(this.encheres);
        this.tabContent.add(new PanneauEncheres(this.main));
    }

    public void setTabContent(Tab curTab) {
        this.tabContent.removeAll();
        if (curTab == this.encheres) {
            this.tabContent.add(new PanneauEncheres(this.main));
        }

    }
}
