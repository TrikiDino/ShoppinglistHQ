package com.web.shoppinglisthq;

public class TblShoppingMemo {
    private String product;
    private int quantity;
    private String einh;
    private long id;
    private boolean checked;
    private double preis;
    private String warenGruppe;
    private String vonWo;

    public TblShoppingMemo(String product, int quantity, String einh, long id, boolean checked, double preis, String warenGruppe, String vonWo) {
        this.product = product;
        this.quantity = quantity;
        this.einh = einh;
        this.id = id;
        this.checked = checked;
        this.preis = preis;
        this.warenGruppe = warenGruppe;
        this.vonWo = vonWo;
    }

    public TblShoppingMemo(String product, int quantity, long id, boolean checked) {
        this.product = product;
        this.quantity = quantity;
        this.id = id;
        this.checked = checked;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEinh() {
        return einh;
    }

    public void setEinh(String einh) {
        this.einh = einh;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public String getWarenGruppe() {
        return warenGruppe;
    }

    public void setWarenGruppe(String warenGruppe) {
        this.warenGruppe = warenGruppe;
    }

    public String getVonWo() {
        return vonWo;
    }

    public void setVonWo(String vonWo) {
        this.vonWo = vonWo;
    }

    @Override
    public String toString() {

        //return quantity + " " + einh + " " + product + " von " + vonWo + " Preis:" + preis;
        return quantity==0?product:String.format("%3d %5s %20s - %4.2f€", quantity, einh==null?"":einh,
                    product==null?"":product, preis);
    }

    // Konstanten für DataSource
    public static final String TABLE_NAME = "shopping_list";

    public enum COLUMN {
        _ID("_id"),
        PRODUCT("product"),
        EINH("einh"),
        QUANTITY("quantity"),
        CHECKED("checked"),
        PREIS("preis"),
        WARENGRUPPE("warenGruppe"),
        VONWO("vonWo");

        private String name;

        COLUMN(String name) { this.name = name; }

        public String bez(){ return name; }
    }
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" + COLUMN._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN.PRODUCT + " TEXT NOT NULL, " +
                    COLUMN.QUANTITY + " INTEGER NOT NULL, " +
                    COLUMN.EINH + " STRING DEFAULT 'Stck', " +
                    COLUMN.CHECKED + " BOOLEAN DEFAULT 0, " +
                    COLUMN.PREIS + " DOUBLE DEFAULT 0, " +
                    COLUMN.WARENGRUPPE + " TEXT DEFAULT 'Sonstiges', " +
                    COLUMN.VONWO + " TEXT DEFAULT 'Sonstige');";

    public static final String SQL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String SQL_DEL = "DELETE FROM " + TABLE_NAME;

}
