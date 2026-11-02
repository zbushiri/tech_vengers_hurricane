package com.model;

import static org.junit.Assert.*;

import org.junit.Test;

import model.SupplyItem;

public class SupplyItemTest {

    @Test
    public void constructorStoresSupplyDetails() {
        SupplyItem item = new SupplyItem("Tarps", 20);

        assertEquals("Constructor should store the supply item name",
            "Tarps", item.getName()
        );
        assertEquals("Constructor should store the supply the quantity",
            20, item.getQuantity()
        );
    }
    
}
