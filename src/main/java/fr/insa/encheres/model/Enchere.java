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
public class Enchere {
    
    private final int id;
    private Timestamp quand;
    private int montant;
    private int de;
    private int sur;
    
    public Enchere(int id, Timestamp quand, int montant, int de, int sur) {
        this.id = id;
        this.quand = quand;
        this.montant = montant;
        this.de = de;
        this.sur = sur;
    }

    public int getId() {
        return id;
    }

    public Timestamp getQuand() {
        return quand;
    }

    public int getMontant() {
        return montant;
    }

    public int getDe() {
        return de;
    }

    public int getSur() {
        return sur;
    }

    public void setQuand(Timestamp quand) {
        this.quand = quand;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public void setSur(int sur) {
        this.sur = sur;
    }
    
}
