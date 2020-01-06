package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product {
    private ObservableList<Part> associatedParts;
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    public Product() {
    }

    public Product(int id, String name, double price, int stock, int min, int max) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.min = min;
        this.max = max;
    }

    public ObservableList<Part> getAllAssociatedParts() {
        return associatedParts;
    }

    public void addAssociatedPart(Part associatedPart) {
        if (associatedParts == null) {
            associatedParts = FXCollections.observableArrayList();
        }
        associatedParts.add(associatedPart);
    }

    public boolean deleteAssociatedPart(Part selectedAspart) {
        if (!associatedParts.contains(selectedAspart)) {
            return false;
        }

        associatedParts.remove(selectedAspart);
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}


//Product
//- associatedParts:ObservableList<Part>
// - id : int
//- name : String
//- price : double
//- stock : int
// - min : int
//- max : int
//+ Product(id : int, name : String, price : double, stock : int, min : int, max : int)
//+ setId(id:int):void
//+ setName(name:String):void
//+ setPrice(price:double):void
//+ setStock(stock:int):void
//+ setMin(min:int):void
//+ setMax(max:int):void
//+ setPrice(max:int):void
//+ getId():int
//+ getName():String
//+ getPrice():double
//+ getStock():int
//+ getMin():int
//+ getMax():int
//+ addAssociatedPart(part:Part):void
//+ deleteAssociatedPart(selectedAspart:Part):boolean
//+ getAllAssociatedParts():ObservableList<Part>