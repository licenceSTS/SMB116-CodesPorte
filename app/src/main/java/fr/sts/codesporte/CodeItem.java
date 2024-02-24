package fr.sts.codesporte;

import java.util.ArrayList;
import java.util.List;

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

    public List<CodeItem> getCodes() {
        List<CodeItem> codes = new ArrayList<>();
        codes.add(this);
        return codes;
    }

    public void addCode(CodeItem code) {
        this.code = code.code;
        this.description = code.description;
        this.longitude = code.longitude;
        this.latitude = code.latitude;
    }

    public void removeCode(CodeItem code) {
        this.code = "";
        this.description = "";
        this.longitude = 0;
        this.latitude = 0;
    }

    public void updateCode(CodeItem code) {
        this.code = code.code;
        this.description = code.description;
        this.longitude = code.longitude;
        this.latitude = code.latitude;
    }

    public void setCodes(List<CodeItem> codes) {
        this.code = codes.get(0).code;
        this.description = codes.get(0).description;
        this.longitude = codes.get(0).longitude;
        this.latitude = codes.get(0).latitude;
    }

    public void removeCodes(List<CodeItem> codes) {
        this.code = "";
        this.description = "";
        this.longitude = 0;
        this.latitude = 0;
    }

    public void updateCodes(List<CodeItem> codes) {
        this.code = codes.get(0).code;
        this.description = codes.get(0).description;
        this.longitude = codes.get(0).longitude;
        this.latitude = codes.get(0).latitude;
    }
}