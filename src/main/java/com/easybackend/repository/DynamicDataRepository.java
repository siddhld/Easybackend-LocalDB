package com.easybackend.repository;

import com.easybackend.model.DynamicData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DynamicDataRepository extends MongoRepository<DynamicData, String> {
    Optional<List<DynamicData>> findByUniqueKey(String uniqueKey);
    List<DynamicData> findAllByUniqueKey(String uniqueKey);
    List<DynamicData> findAllByCreatedAtBefore(LocalDate date);
    @Query("{ 'uniqueKey': ?0, 'data.?1': ?2 }")
    Optional<DynamicData> findByUniqueKeyAndDataId(String uniqueKey, String dataIdKey, String dataIdValue);
    Integer deleteAllByUniqueKey(String uniqueKey);

}