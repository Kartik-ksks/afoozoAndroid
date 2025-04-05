package in.kpis.afoozo.bean;

public class CartItemBean {

    private String Title;
    private int count;

    public CartItemBean(String title, int count) {
        Title = title;
        this.count = count;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
