package in.kpis.afoozo.bean;

import java.io.Serializable;

public class EventsListBean implements Serializable {

    private String title;
    private String image;

    public EventsListBean(String title, String image) {
        this.title = title;
        this.image = image;
    }
}
