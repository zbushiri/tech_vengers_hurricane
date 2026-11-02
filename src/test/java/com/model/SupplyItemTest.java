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

    @Test
    public void supplyItemDetailsAsNegativeNumber() {
        SupplyItem item = new SupplyItem("Tarps", -5);
        assertEquals("Constructor should set negative number as 0",
            0, item.getQuantity()
        );
    }
    
    @Test
    public void missingSuplyName() {
        SupplyItem item = new SupplyItem(null, 5);
        assertEquals("Should not allow null item name", "", item.getName() 
    );
    }
    
}
