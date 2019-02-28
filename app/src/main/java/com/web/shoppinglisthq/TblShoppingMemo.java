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
        return quantity + " x " + product ;
    }
}
