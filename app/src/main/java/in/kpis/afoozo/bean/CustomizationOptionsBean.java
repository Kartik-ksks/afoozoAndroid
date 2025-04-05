package in.kpis.afoozo.bean;

import java.io.Serializable;

public class CustomizationOptionsBean implements Serializable {

    /**
     * id : 1
     * name : a
     * price : 1.0
     */

    private boolean isAdded;
    private long id;
    private String name;
    private double price;

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
