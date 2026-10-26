package model;

/**
 * A named animal accompanying a household; pets do not use human beds.
 * @param name pet name
 * @param species kind of animal, for example dog
 * @author Team Tech-vengers
 */
public record Pet(String name, String species) {
    /**
     * Creates a named pet.
     * @param name nonblank pet name
     * @param species nonblank animal species
     */
    public Pet {
        if (name == null || name.isBlank() || species == null || species.isBlank()) {
            throw new IllegalArgumentException("A pet needs a name and species.");
        }
    }
}
