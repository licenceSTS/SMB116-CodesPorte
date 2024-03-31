package fr.sts.codesporte;

public class PorteItem {

    private String id;
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

    public PorteItem(String id, String description, String code, double longitude, double latitude) {
        this.id = id;
        this.description = description;
        this.code = code;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PorteItem porteItem = (PorteItem) o;
        return id != null && id.equals(porteItem.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PorteItem{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
