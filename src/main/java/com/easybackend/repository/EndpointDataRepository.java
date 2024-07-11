package com.easybackend.repository;

import com.easybackend.model.EndpointData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndpointDataRepository extends MongoRepository<EndpointData, String> {
    Optional<EndpointData> findByUniqueKey(String uniqueKey);
}