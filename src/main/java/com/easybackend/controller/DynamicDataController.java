package com.easybackend.controller;

import com.easybackend.model.DynamicDataDto;
import com.easybackend.model.EndpointData;
import com.easybackend.service.DynamicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000/")
@RequestMapping("/api")
public class DynamicDataController {
    private final DynamicDataService service;

    @Autowired
    public DynamicDataController(DynamicDataService service) {
        this.service = service;
    }

    @PostMapping("/{uniqueKey}")
    public DynamicDataDto createData(@PathVariable String uniqueKey, @RequestBody Map<String, Object> data) {
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
        return service.getDataById(uniqueKey, id);
    }

    @PutMapping("/{uniqueKey}/{id}")
    public DynamicDataDto updateData(@PathVariable String uniqueKey, @PathVariable String id, @RequestBody Map<String, Object> data) {
        return service.updateData(uniqueKey, id, data);
    }

    @PatchMapping("/{uniqueKey}/{id}")
    public DynamicDataDto patchData(@PathVariable String uniqueKey, @PathVariable String id, @RequestBody Map<String, Object> fields) {
        return service.patchUpdateData(uniqueKey, id, fields);
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

}
