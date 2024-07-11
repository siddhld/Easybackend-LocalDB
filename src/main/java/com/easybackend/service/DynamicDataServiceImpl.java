package com.easybackend.service;

import com.easybackend.exception.DataNotFoundException;
import com.easybackend.model.DynamicData;
import com.easybackend.model.DynamicDataDto;
import com.easybackend.model.EndpointData;
import com.easybackend.repository.DynamicDataRepository;
import com.easybackend.repository.EndpointDataRepository;
import com.easybackend.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicDataServiceImpl implements DynamicDataService {
    private final DynamicDataRepository repository;
    private final EndpointDataRepository endpointDataRepo;

    @Autowired
    public DynamicDataServiceImpl(DynamicDataRepository repository, EndpointDataRepository endpointDataRepo) {
        this.repository = repository;
        this.endpointDataRepo = endpointDataRepo;
    }

    @Transactional
    @CacheEvict(value = {"dynamicDataDto"}, key = "#uniqueKey")
    public DynamicDataDto saveData(String uniqueKey, Map<String, Object> data) {
        Utility.validateUniqueKey(uniqueKey);   // Validation
        Utility.validateData(data);   // Validation

        String keyId = getKeyId(uniqueKey);
        String nextId = generateNextId(uniqueKey, keyId);

        // This is for putting the keyId to the top
        Map<String, Object> tempData = new LinkedHashMap<>();
        tempData.put(keyId, nextId);
        tempData.putAll(data);
        tempData.put(keyId, nextId);

        DynamicData dynamicData = new DynamicData();
        dynamicData.setUniqueKey(uniqueKey);
        dynamicData.setData(tempData);
        dynamicData.setCreatedAt(LocalDate.now());
        try {
            repository.save(dynamicData);
            endpointDataRepo.save(new EndpointData(uniqueKey, keyId));
        } catch (DataIntegrityViolationException e) {
            throw new DataAccessException("Data conflict error", e) {
            };
        } catch (DataAccessException e) {
            throw new DataAccessException("Database access error", e) {
            };
        } catch (Exception e) {
            throw new DataAccessException("Unexpected error", e) {
            };
        }
        return new DynamicDataDto(dynamicData);
    }

    @Transactional
    public EndpointData saveEndpointKey(String uniqueKey, String keyId) {
        Utility.validateUniqueKey(uniqueKey);
        try {
            return endpointDataRepo.save(new EndpointData(uniqueKey, keyId));
        } catch (DataIntegrityViolationException e) {
            throw new DataAccessException("Data conflict error", e) {
            };
        } catch (DataAccessException e) {
            throw new DataAccessException("Database access error", e) {
            };
        } catch (Exception e) {
            throw new DataAccessException("Unexpected error", e) {
            };
        }
    }

    @Cacheable(value = "dynamicDataDto", key = "#uniqueKey")
    public List<DynamicDataDto> getData(String uniqueKey) {
        Utility.validateUniqueKey(uniqueKey);   // Validation
        System.err.println("Inside Get Data ---------------- ");
        Optional<List<DynamicData>> data = repository.findByUniqueKey(uniqueKey);

        Utility.checkForEmptyListData(data, "Data not found for the provided Endpoint."); // Validation

        return data.get().stream().map(DynamicDataDto::new).collect(Collectors.toList());
    }

    @Cacheable(value = "dynamicDataDtoById", key = "#uniqueKey + '-' + #id")
    public DynamicDataDto getDataById(String uniqueKey, String id) {
        Utility.validateUniqueKey(uniqueKey);   // Validation
        System.err.println("Inside Get Data By Id ---------------- ");
        String keyId = getKeyId(uniqueKey);

        Optional<DynamicData> data = repository.findByUniqueKeyAndDataId(uniqueKey, keyId, id);

        Utility.checkForEmptyData(data, "Data not found for the provided id.");   // Validation

        return new DynamicDataDto(data.get());
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = {"dynamicDataDto"}, key = "#uniqueKey"),
                    @CacheEvict(value = "dynamicDataDtoById", key = "#uniqueKey + '-' + #id")
            }
    )
    public DynamicDataDto updateData(String uniqueKey, String id, Map<String, Object> data) {
        Utility.validateUniqueKey(uniqueKey);   // Validation
        Utility.validateData(data);   // Validation

        String keyId = getKeyId(uniqueKey);

        Optional<DynamicData> existingData = repository.findByUniqueKeyAndDataId(uniqueKey, keyId, id);

        Utility.checkForEmptyData(existingData, "Data not found with the given id");   // Validation

        // This is for putting the keyId to the top
        Map<String, Object> updatedMap = new LinkedHashMap<>();
        updatedMap.put(keyId, id);
        updatedMap.putAll(data);
        updatedMap.put(keyId, id);

        existingData.get().setData(updatedMap);
        try {
            repository.save(existingData.get());
        } catch (DataAccessException e) {
            throw new DataAccessException("Database access error", e) {
            };
        } catch (Exception e) {
            throw new DataAccessException("Unexpected error", e) {
            };
        }
        return new DynamicDataDto(existingData.get());

    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = {"dynamicDataDto"}, key = "#uniqueKey"),
                    @CacheEvict(value = "dynamicDataDtoById", key = "#uniqueKey + '-' + #id")
            }
    )
    public DynamicDataDto patchUpdateData(String uniqueKey, String id, Map<String, Object> fields) {
        Utility.validateUniqueKey(uniqueKey);   // Validation
        Utility.validateData(fields);   // Validation

        String keyId = getKeyId(uniqueKey);

        Optional<DynamicData> existingData = repository.findByUniqueKeyAndDataId(uniqueKey, keyId, id);

        Utility.checkForEmptyData(existingData, "Data not found with the given id");   // Validation

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            // Checking if the key is ID then skipping it
            if (!entry.getKey().equals(keyId))
                existingData.get().getData().put(entry.getKey(), entry.getValue());
        }

        try {
            repository.save(existingData.get());
        } catch (DataAccessException e) {
            throw new DataAccessException("Database access error", e) {
            };
        } catch (Exception e) {
            throw new DataAccessException("Unexpected error", e) {
            };
        }
        return new DynamicDataDto(existingData.get());
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = {"dynamicDataDto"}, key = "#uniqueKey"),
                    @CacheEvict(value = "dynamicDataDtoById", key = "#uniqueKey + '-' + #id")
            }
    )
    public String deleteData(String uniqueKey, String id) {
        Utility.validateUniqueKey(uniqueKey);   // Validation

        String keyId = getKeyId(uniqueKey);

        Optional<DynamicData> data = repository.findByUniqueKeyAndDataId(uniqueKey, keyId, id);

        Utility.checkForEmptyData(data, "Data not found with the given id");   // Validation

        try {
            repository.delete(data.get());
        } catch (DataAccessException e) {
            throw new DataAccessException("Database access error", e) {
            };
        } catch (Exception e) {
            throw new DataAccessException("Unexpected error", e) {
            };
        }
        return "Data deleted Successfully";
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = {"dynamicDataDto"}, key = "#uniqueKey"),
                    @CacheEvict(value = "dynamicDataDtoById", key = "#uniqueKey + '-' + #id")
            }
    )
    public String deleteAllData(String uniqueKey) {
        Utility.validateUniqueKey(uniqueKey);   // Validation
        Optional<EndpointData> endpointData = endpointDataRepo.findByUniqueKey(uniqueKey);
        Utility.checkForEmptyEndpoint(endpointData, "Please Enter your valid unique Endpoint in Url");   // Validation

        try {
            Integer result = repository.deleteAllByUniqueKey(uniqueKey);

            if (result <= 0) {
                throw new DataNotFoundException("No Data Found With The Provided Endpoint");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("Database access error", e) {
            };
        }
        return "All Data deleted Successfully";
    }

    private String getKeyId(String uniqueKey) {
        Optional<EndpointData> endpointData = endpointDataRepo.findByUniqueKey(uniqueKey);

        Utility.checkForEmptyEndpoint(endpointData, "Please Enter your valid unique Endpoint in Url");   // Validation

        String keyId = endpointData.map(EndpointData::getKeyId).orElse("id");

        return keyId;
    }

    public String generateUniqueKey() {
        String uniqueKey;
        Optional<List<DynamicData>> data;
        do {
            uniqueKey = Utility.generateRandomString();
            data = repository.findByUniqueKey(uniqueKey);
        } while (data.isPresent() && !data.get().isEmpty());
        return uniqueKey;
    }

    @Transactional(readOnly = true)
    public String generateNextId(String uniqueKey, String keyId) {
        // Fetch all records with the given uniqueKey
        List<DynamicData> records = repository.findAllByUniqueKey(uniqueKey);

        if (records.isEmpty()) {
            return "1";
        }

        // Use Stream API to find the record with the highest keyId value
        Optional<DynamicData> lastRecordOpt = records.stream()
                .max(Comparator.comparingInt(r -> Integer.parseInt((String) r.getData().get(keyId))));

        if (lastRecordOpt.isPresent()) {
            String val = (String) lastRecordOpt.get().getData().get(keyId);
            try {
                return String.valueOf(Integer.parseInt(val) + 1);
            } catch (NumberFormatException e) {
                throw new DataNotFoundException("Please provide a valid value for Id");
            }
        } else {
            return "1";
        }
    }
}
