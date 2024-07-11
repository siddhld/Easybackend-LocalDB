package com.easybackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Document(collection = "dynamic_data")
public class DynamicData {
    @Id
    private String id;
    private String uniqueKey;
    private LocalDate createdAt;
    private Map<String, Object> data = new LinkedHashMap<>();

}
