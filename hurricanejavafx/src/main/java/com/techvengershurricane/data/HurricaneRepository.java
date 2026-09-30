package com.techvengershurricane.data;

import com.techvengershurricane.model.HurricaneEvent;
import java.nio.file.Path;

public class HurricaneRepository extends JsonCrudRepository<HurricaneEvent> {
    public HurricaneRepository(Path jsonDirectory) {
        super(jsonDirectory.resolve("hurricanes.json"), HurricaneEvent[].class);
    }
}
