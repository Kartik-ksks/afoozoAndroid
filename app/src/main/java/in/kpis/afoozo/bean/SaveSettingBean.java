package in.kpis.afoozo.bean;

import java.util.ArrayList;

public class SaveSettingBean {

    private ArrayList<NotiSettingBean> notificationList;

    public ArrayList<NotiSettingBean> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(ArrayList<NotiSettingBean> notificationList) {
        this.notificationList = notificationList;
    }
}
