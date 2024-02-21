package fr.sts.codesporte;

public class CodeItem {
    private String description;
    private String code;
    private double longitude;
    private double latitude;

    public CodeItem(String description, String code, double longitude, double latitude) {
        this.description = description;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
    }

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
}