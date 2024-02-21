package fr.sts.codesporte;

import java.util.List;

public class GareItem {
    private String name;
    private List<CodeItem> codes;
    private double longitude;
    private double latitude;

    public GareItem(String name, List<CodeItem> codes, double longitude, double latitude) {
        this.name = name;
        this.codes = codes;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public int getCodeCount() {
        return codes.size();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public List<CodeItem> getCodes() {
        return codes;
    }
}