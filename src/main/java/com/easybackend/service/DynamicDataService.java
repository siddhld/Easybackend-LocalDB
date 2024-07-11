package com.easybackend.service;

import com.easybackend.model.DynamicDataDto;
import com.easybackend.model.EndpointData;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

public interface DynamicDataService {
    public DynamicDataDto saveData(String uniqueKey, Map<String, Object> data);

    public List<DynamicDataDto> getData(String uniqueKey);

    public DynamicDataDto getDataById(String uniqueKey, String id);

    public DynamicDataDto updateData(String uniqueKey, String id, Map<String, Object> data);

    public DynamicDataDto patchUpdateData(String uniqueKey, String id, Map<String, Object> fields);

    public String deleteData(String uniqueKey, String id);

    public String deleteAllData(@PathVariable String uniqueKey);

    public String generateUniqueKey();

    public String generateNextId(String uniqueKey, String keyId);

    public EndpointData saveEndpointKey(String uniqueKey, String keyId);
}
