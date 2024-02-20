package fr.sts.codesporte;

public class CodeItem {
    private String descriptionPorte;
    private String code;
    private double longitude;
    private double latitude;

    public CodeItem(String descriptionPorte, String code, double longitude, double latitude) {
        this.descriptionPorte = descriptionPorte;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters
    public String getDescriptionPorte() {
        return descriptionPorte;
    }

    public String getCode() {
        return code;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    // Setters
    public void setDescriptionPorte(String descriptionPorte) {
        this.descriptionPorte = descriptionPorte;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}