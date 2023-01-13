/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.insa.encheres.model;

import java.sql.Timestamp;

/**
 *
 * @author ivane
 */
public class Objet {
    
    private final int id;
    private String titre;
    private String description;
    private Timestamp debut;
    private Timestamp fin;
    private int prixbase;
    private int proposepar;
    private int categorie;
    
    public Objet(int id, String titre, String description, Timestamp debut, Timestamp fin, int prixbase, int proposepar, int categorie) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.debut = debut;
        this.fin = fin;
        this.prixbase = prixbase;
        this.proposepar = proposepar;
        this.categorie = categorie;
        
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDebut(Timestamp debut) {
        this.debut = debut;
    }

    public void setFin(Timestamp fin) {
        this.fin = fin;
    }

    public void setPrixbase(int prixbase) {
        this.prixbase = prixbase;
    }

    public void setProposepar(int proposepar) {
        this.proposepar = proposepar;
    }

    public void setCategorie(int categorie) {
        this.categorie = categorie;
    }

    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getDebut() {
        return debut;
    }

    public Timestamp getFin() {
        return fin;
    }

    public int getPrixbase() {
        return prixbase;
    }

    public int getProposepar() {
        return proposepar;
    }

    public int getCategorie() {
        return categorie;
    }
    
}
