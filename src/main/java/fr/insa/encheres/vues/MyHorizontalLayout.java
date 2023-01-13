/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.encheres.vues;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

/**
 *
 * @author ivane
 */
public class MyHorizontalLayout extends HorizontalLayout {

    public static String CSS_COLOR = "blue";

    public MyHorizontalLayout(Component... children) {
        super(children);
        if (ConfigGenerale.ENCADRE_LAYOUT) {
            Paragraph nom = new Paragraph(this.getClass().getSimpleName());
            nom.getStyle().set("font-family", "Monospace")
                    .set("font-size", "1.2em").set("color", CSS_COLOR)
                    .set("font-style", "Arial");
            this.addComponentAsFirst(nom);
            this.getStyle().set("border", "2px dotted "+CSS_COLOR);

        }
    }

}
