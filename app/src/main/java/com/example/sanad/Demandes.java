package com.example.sanad;

public class Demandes {
    String nom;
    String num;

    public  Demandes(String nom , String num){
        this.nom = nom;
        this.num = num;
    }

    public String getNom() {
        return nom;
    }

    public String getNum() {
        return num;
    }
}
