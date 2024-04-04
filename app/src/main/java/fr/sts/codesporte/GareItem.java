package fr.sts.codesporte;

import java.util.List;

public class GareItem {

    private String id;
    private String nom;
    private List<PorteItem> porteList; // Liste des portes associées à la gare
    private double longitude;
    private double latitude;

    // Constructeur de GareItem
    public GareItem(String nom, List<PorteItem> porteList, double longitude, double latitude) {
        this.nom = nom;
        this.porteList = porteList;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public GareItem(String id,String nom, List<PorteItem> porteList, double longitude, double latitude) {
        this.id = id;
        this.nom = nom;
        this.porteList = porteList;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<PorteItem> getPorteList() {
        return porteList;
    }

    public void setPorteList(List<PorteItem> porteList) {
        this.porteList = porteList;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}