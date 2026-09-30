package com.techvengershurricane.data;

import com.techvengershurricane.model.Shelter;
import java.nio.file.Path;

public class ShelterRepository extends JsonCrudRepository<Shelter> {
    public ShelterRepository(Path jsonDirectory) {
        super(jsonDirectory.resolve("shelters.json"), Shelter[].class);
    }
}
