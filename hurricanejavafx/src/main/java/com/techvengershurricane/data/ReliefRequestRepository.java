package com.techvengershurricane.data;

import com.techvengershurricane.model.ReliefRequest;
import java.nio.file.Path;

public class ReliefRequestRepository extends JsonCrudRepository<ReliefRequest> {
    public ReliefRequestRepository(Path jsonDirectory) {
        super(jsonDirectory.resolve("requests.json"), ReliefRequest[].class);
    }
}
