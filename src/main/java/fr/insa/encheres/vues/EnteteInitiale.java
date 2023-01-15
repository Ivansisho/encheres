/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.encheres.vues;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import java.util.Arrays;
import java.util.List;
import fr.insa.encheres.encheres.VuePrincipale;
/**
 *
 * @author ivane
 */
public class EnteteInitiale extends MyHorizontalLayout {
    
    private VuePrincipale main;
    private Button connection;
    private Button nouveau;
    private Button message;

    private static class AlignmentOption {
        private final String label;
        private final FlexComponent.Alignment alignment;

        public AlignmentOption(String label,
                FlexComponent.Alignment alignment) {
            this.label = label;
            this.alignment = alignment;
        }

        public FlexComponent.Alignment getAlignment() {
            return alignment;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public EnteteInitiale(VuePrincipale main) {
        
        VerticalLayout layout = new VerticalLayout();
        layout.add(this.connection = new Button("Se connecter"));
        this.connection.getStyle().set("fontWeight", "bold");
        this.connection.getStyle().set("font-size", "2em");
        layout.add(this.nouveau = new Button("Nouvel utilisateur"));
        this.nouveau.getStyle().set("fontWeight", "bold");
        this.nouveau.getStyle().set("font-size", "2em");
        layout.add(this.message = new Button("Surprise"));
        this.message.getStyle().set("fontWeight", "bold");
        this.message.getStyle().set("font-size", "2em");

        
        List<AlignmentOption> options = Arrays.asList(
                new AlignmentOption("Stretch",
                        FlexComponent.Alignment.STRETCH));

        RadioButtonGroup<AlignmentOption> radioGroup = new RadioButtonGroup<>();
        radioGroup.setLabel("Horizontal alignment");
        radioGroup.setItems(options);
        radioGroup.setValue(options.get(0));
        radioGroup.addValueChangeListener(e -> {
            FlexComponent.Alignment alignment = e.getValue().getAlignment();
            layout.setAlignItems(alignment);
        });

        this.add(new H1("Soyez tous les bienvenus dans ce magnifique programme de vente aux enchères"));
        this.add(layout, radioGroup);
        
        this.nouveau.addClickListener((t) -> {
            this.main.setMainContent(new NouvelUtilisateur(this.main));
        });
        this.connection.addClickListener((t) -> {
            this.main.setMainContent(new PageConnexion(this.main));
        });
        this.message.addClickListener((event) -> {
            Notification.show("Merci d'avoir cliqué sur le bouton surprise, "
                    + "malheureusement pas de surprise aujourd'hui. "
                    + "Vous aurez peut-être plus de chance la prochaine fois :D ");
        });
    }

}

