package fr.sts.codesporte;

import java.util.List;

public class GareItem {
    private final String name;
    private final List<CodeItem> codes; // Liste pour stocker les portes
    private final double latitude;
    private final double longitude;

    // Constructeur
    public GareItem(String name, List<CodeItem> codes, double latitude, double longitude) {
        this.name = name;
        this.codes = codes;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters et Setters
    public String getName() { return name; }
    public List<CodeItem> getCodes() { return codes; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void addCode(CodeItem code) {
        this.codes.add(code);
    }
}