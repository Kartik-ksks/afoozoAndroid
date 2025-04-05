package in.kpis.afoozo.bean;

public class MenuTabBean {

    private boolean isSelected;
    private String title;

    public MenuTabBean(boolean isSelected, String title) {
        this.isSelected = isSelected;
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
