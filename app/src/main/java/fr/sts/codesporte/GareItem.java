package fr.sts.codesporte;

public class GareItem {
    private String name;
    private int codeCount;

    public GareItem(String name, int codeCount) {
        this.name = name;
        this.codeCount = codeCount;
    }

    public String getName() {
        return name;
    }

    public int getCodeCount() {
        return codeCount;
    }
}

