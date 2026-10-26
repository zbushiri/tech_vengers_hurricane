package model;

/**
 * A supply and how many of it there are, such as 20 tarps.
 * @author Team Tech-vengers
 */
public class SupplyItem {
    private String name;
    private int quantity;

    /**
     * Creates a supply item.
     * 
     * @param name what the supply is
     * @param quantity how many there are
     */
    public SupplyItem(String name, int quantity) {
        setName(name);
        setQuantity(quantity);
    }

    /**
     * Returns the name.
     * @return what the supply is
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name what the supply is
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the quantity.
     * @return how many there are
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     * @param quantity how many there are
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
