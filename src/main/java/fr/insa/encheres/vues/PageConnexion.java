/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.encheres.vues;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import fr.insa.encheres.encheres.VuePrincipale;
import fr.insa.encheres.bdd.GestionBdD;
import fr.insa.encheres.model.Utilisateur;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author ivane
 */
public class PageConnexion extends VerticalLayout {
    
    private VuePrincipale main;
    
    private TextField vnom;
    private TextField vid;
    private TextField vprenom;
    private TextField vemail;
    private TextField vcodepostal;
    private PasswordField vpass;
    private Button vbLogin;
    
    public PageConnexion(VuePrincipale main) {
        this.main = main;
        this.vnom = new TextField("Nom");
        this.vpass = new PasswordField("Mot de passe");
        this.vbLogin = new Button("Se connecter");
        this.add(this.vnom,this.vpass,this.vbLogin);
        this.vbLogin.addClickListener((event) -> {
            this.doLogin();
        });
    }
    
    public void doLogin() {
        String nom = this.vnom.getValue();
        String pass = this.vpass.getValue();
        int id = 1;
        String prenom = this.vprenom.getValue();
        String email = this.vemail.getValue();
        String codepostal = this.vcodepostal.getValue();
        try {
            Connection con = this.main.getSessionInfo().getConBdD();
            Optional<Utilisateur> user = GestionBdD.login( con, id, nom, prenom, email, pass, codepostal);
            if(user.isEmpty()) {
                Notification.show("Utilisateur ou pass invalide");
            } else {
                this.main.getSessionInfo().setCurUser(user);
                this.main.setEntete(new EnteteAfterConnexion(this.main));
                this.main.setMainContent(new PrincipalAfterConnexion(this.main));
            }
        } catch (SQLException ex) {
            Notification.show("Problème interne : " + ex.getLocalizedMessage());
        }        
    }
}
