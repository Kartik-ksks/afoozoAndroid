package in.kpis.afoozo.bean;

import java.util.ArrayList;

public class BillToCompanyHistoryBean {
    private int OrderCount;
    private double amount;
    private double remainingamount;
    private ArrayList<OrdersBean> list;

    public int getOrderCount() {
        return OrderCount;
    }

    public void setOrderCount(int orderCount) {
        OrderCount = orderCount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRemainingamount() {
        return remainingamount;
    }

    public void setRemainingamount(double remainingamount) {
        this.remainingamount = remainingamount;
    }

    public ArrayList<OrdersBean> getList() {
        return list;
    }

    public void setList(ArrayList<OrdersBean> list) {
        this.list = list;
    }
}
