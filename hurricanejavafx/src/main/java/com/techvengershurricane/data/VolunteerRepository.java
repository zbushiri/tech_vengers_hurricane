package com.techvengershurricane.data;

import com.techvengershurricane.model.Volunteer;
import java.nio.file.Path;

public class VolunteerRepository extends JsonCrudRepository<Volunteer> {
    public VolunteerRepository(Path jsonDirectory) {
        super(jsonDirectory.resolve("volunteers.json"), Volunteer[].class);
    }
}
