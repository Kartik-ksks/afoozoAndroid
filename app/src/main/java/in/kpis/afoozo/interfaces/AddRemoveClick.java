package in.kpis.afoozo.interfaces;

public interface AddRemoveClick {

    public void addClick(boolean isSubcategory, int catPosition, int subCatPosition, int itemPosition);

    public void removeClick(boolean isSubcategory, int catPosition, int subCatPosition, int itemPosition);

    public void editClick(boolean isSubcategory, int catPosition, int subCatPosition, int itemPosition);

    public void stickyClick(int position);

    public void scrollCallback();

}
