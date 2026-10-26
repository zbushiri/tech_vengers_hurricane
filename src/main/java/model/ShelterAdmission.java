package model;

import java.util.List;
import java.util.UUID;

/**
 * A saved household check-in, linked to the request to prevent double counting.
 * @param requestId relief request identifier
 * @param people named human occupants
 * @param pets accompanying animals
 * @author Team Tech-vengers
 */
public record ShelterAdmission(UUID requestId, List<Person> people, List<Pet> pets) {
    /**
     * Copies the household lists for a check-in record.
     * @param requestId relief request identifier
     * @param people human occupants
     * @param pets accompanying animals
     */
    public ShelterAdmission {
        if (requestId == null || people == null || people.isEmpty() || pets == null) {
            throw new IllegalArgumentException("An admission needs a request and its household.");
        }
        people = List.copyOf(people);
        pets = List.copyOf(pets);
    }
}
