package in.kpis.afoozo.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class NotificationBean implements Serializable {

    private String title;
    private ArrayList<MenuListBean> list;

    public NotificationBean(String title, ArrayList<MenuListBean> list) {
        this.title = title;
        this.list = list;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MenuListBean> getList() {
        return list;
    }

    public void setList(ArrayList<MenuListBean> list) {
        this.list = list;
    }
}
