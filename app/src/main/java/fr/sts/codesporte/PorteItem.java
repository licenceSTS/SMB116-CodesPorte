package fr.sts.codesporte;

public class PorteItem {
    private String description;
    private String code;
    private double longitude;
    private double latitude;

    public PorteItem(String description, String code, double longitude, double latitude) {
        this.description = description;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters
    public String getDescription() {
        return description;
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
    public void setDescription(String description) {
        this.description = description;
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
