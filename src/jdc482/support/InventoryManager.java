package jdc482.support;

import jdc482.model.Inventory;

public class InventoryManager {
    private static final Inventory instance = new Inventory();

    private InventoryManager(){}

    public static Inventory shared(){
        return instance;
    }
}
