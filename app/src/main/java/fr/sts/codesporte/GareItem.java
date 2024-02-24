package fr.sts.codesporte;

import java.util.List;

public class GareItem {
    private String name;
    private List<CodeItem> codes; // Liste pour stocker les portes
    private double latitude;
    private double longitude;

    // Constructeur
    public GareItem(String name, List<CodeItem> codes, double latitude, double longitude) {
        this.name = name;
        this.codes = codes; // Initialisez la liste des codes ici
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters
    public String getName() { return name; }
    public List<CodeItem> getCodes() { return codes; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    // Vous pourriez également ajouter des méthodes pour ajouter ou supprimer des codes
    public void addCode(CodeItem code) {
        this.codes.add(code);
    }
}