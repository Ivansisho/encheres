/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.encheres.encheres;

/**
 *
 * @author ivane
 */

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@Route(value = "")
@PageTitle("Encheres")
public class VuePrincipale {
    
    private SessionInfo sessionInfo;

    private HorizontalLayout entete;
    private VerticalLayout mainContent;
    
    public void setEntete(Component c) {
        this.entete.removeAll();
        this.entete.add(c);
    }

    public void setMainContent(Component c) {
        this.mainContent.removeAll();
        this.mainContent.add(c);
    }
    
    public VuePrincipale() {
        this.sessionInfo = new SessionInfo();
        this.entete = new HorizontalLayout();
        this.entete.setWidthFull();

        this.mainContent = new VerticalLayout();
        this.mainContent.setWidthFull();
        this.mainContent.setHeightFull();
    }
    
}
