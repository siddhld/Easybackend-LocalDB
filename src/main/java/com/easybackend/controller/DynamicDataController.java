package com.easybackend.controller;

import com.easybackend.model.DynamicDataDto;
import com.easybackend.model.EndpointData;
import com.easybackend.service.DynamicDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping("/api")
public class DynamicDataController {
    private final DynamicDataService service;
    private final ObjectMapper objectMapper;

    @Autowired
    public DynamicDataController(ObjectMapper objectMapper, DynamicDataService service) {
        this.objectMapper = objectMapper;
        this.service = service;
    }

    @PostMapping(value = "/{uniqueKey}")
    public DynamicDataDto createData(@PathVariable String uniqueKey,
                                     @RequestBody(required = false) String jsonBody,
                                     @RequestParam MultiValueMap<String, String> allParams,
                                     HttpServletRequest request) {

        Map<String, Object> data = new HashMap<>();
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            data = parseJsonData(jsonBody);
        } else {
            data = parseFormData(allParams);
        }

        return service.saveData(uniqueKey, data);
    }

    @PostMapping("/endpoint")
    public EndpointData saveEndpointKey(@RequestBody EndpointData endpointData) {
        final String keyId = (endpointData.getKeyId() == null || endpointData.getKeyId().isEmpty() || endpointData.getKeyId().isBlank()) ? "id" : endpointData.getKeyId();
        return service.saveEndpointKey(endpointData.getUniqueKey(), keyId);
    }

    @GetMapping("/{uniqueKey}")
    public List<DynamicDataDto> getData(@PathVariable String uniqueKey) {
        return service.getData(uniqueKey);
    }

    @GetMapping("/{uniqueKey}/{id}")
    public DynamicDataDto getDataById(@PathVariable String uniqueKey, @PathVariable String id) {
        System.err.println();
        return service.getDataById(uniqueKey, id);
    }

    @PutMapping("/{uniqueKey}/{id}")
    public DynamicDataDto updateData(@PathVariable String uniqueKey,
                                     @PathVariable String id,
                                     @RequestBody(required = false) String jsonBody,
                                     @RequestParam MultiValueMap<String, String> allParams,
                                     HttpServletRequest request) {

        Map<String, Object> data = new HashMap<>();
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            data = parseJsonData(jsonBody);
        } else {
            data = parseFormData(allParams);
        }

        return service.updateData(uniqueKey, id, data);
    }

    @PatchMapping("/{uniqueKey}/{id}")
    public DynamicDataDto patchData(@PathVariable String uniqueKey,
                                    @PathVariable String id,
                                    @RequestBody(required = false) String jsonBody,
                                    @RequestParam MultiValueMap<String, String> allParams,
                                    HttpServletRequest request) {

        Map<String, Object> data = new HashMap<>();
        String contentType = request.getContentType();

        if (contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
            data = parseJsonData(jsonBody);
        } else {
            data = parseFormData(allParams);
        }

        return service.patchUpdateData(uniqueKey, id, data);
    }

    @DeleteMapping("/{uniqueKey}/{id}")
    public String deleteData(@PathVariable String uniqueKey, @PathVariable String id) {
        return service.deleteData(uniqueKey, id);
    }

    @DeleteMapping("/{uniqueKey}")
    public String deleteAllData(@PathVariable String uniqueKey) {
        return service.deleteAllData(uniqueKey);
    }

    @GetMapping("/generateUniqueKey")
    public String generateUniqueKey() {
        return service.generateUniqueKey();
    }

    private Map<String, Object> parseJsonData(String jsonBody) {
        if (jsonBody == null || jsonBody.isEmpty()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(jsonBody, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new HttpMessageNotReadableException("Invalid JSON format: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> parseFormData(MultiValueMap<String, String> allParams) {
        Map<String, Object> data = new HashMap<>();
        allParams.forEach((key, values) -> {
            if (values.size() == 1) {
                data.put(key, values.get(0));
            } else if (values.size() > 1) {
                data.put(key, values);
            }
        });
        return data;
    }
}
