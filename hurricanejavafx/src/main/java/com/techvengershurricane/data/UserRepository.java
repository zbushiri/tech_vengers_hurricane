package com.techvengershurricane.data;

import com.techvengershurricane.model.User;
import java.nio.file.Path;

public class UserRepository extends JsonCrudRepository<User> {
    public UserRepository(Path jsonDirectory) {
        super(jsonDirectory.resolve("users.json"), User[].class);
    }
}
