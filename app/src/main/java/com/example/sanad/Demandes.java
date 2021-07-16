package com.example.sanad;

public class Demandes {
    String nom;
    String id;
    boolean statut;

    public Demandes(){

    }

    public Demandes(String nom , String id){
        this.nom = nom;
        this.id = id;
        this.statut = false;
    }

    public String getNom() {
        return nom;
    }

    public String getNum() {
        return id;
    }

    public boolean getStatut() { return statut; }
}
