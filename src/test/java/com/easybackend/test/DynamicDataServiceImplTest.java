package com.easybackend.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.easybackend.model.DynamicData;
import com.easybackend.model.DynamicDataDto;
import com.easybackend.model.EndpointData;
import com.easybackend.repository.DynamicDataRepository;
import com.easybackend.repository.EndpointDataRepository;
import com.easybackend.service.DynamicDataServiceImpl;

    public class DynamicDataServiceImplTest {

        @InjectMocks
        private DynamicDataServiceImpl dynamicDataService;

        @Mock
        private DynamicDataRepository repository;

        @Mock
        private EndpointDataRepository endpointDataRepo;

        @BeforeEach
        public void setup() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testSaveData() {
            String uniqueKey = "abc123";
            Map<String, Object> data = new HashMap<>();
            data.put("name", "John Doe");
            data.put("age", 30);

            DynamicData savedData = new DynamicData();
            savedData.setUniqueKey(uniqueKey);
            savedData.setData(data);
            savedData.setCreatedAt(LocalDate.now());

            when(endpointDataRepo.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(new EndpointData(uniqueKey, "id")));
            when(repository.save(any(DynamicData.class))).thenReturn(savedData);

            DynamicDataDto result = dynamicDataService.saveData(uniqueKey, data);

            assertNotNull(result);
            assertEquals("John Doe", result.getData().get("name"));
            assertEquals(30, result.getData().get("age"));

            verify(repository).save(any(DynamicData.class));
            verify(endpointDataRepo).save(any(EndpointData.class));
        }

        @Test
        public void testGetData() {
            String uniqueKey = "abc123";
            List<DynamicData> dataList = new ArrayList<>();
            DynamicData data1 = new DynamicData();
            data1.setUniqueKey(uniqueKey);
            data1.setData(Collections.singletonMap("name", "John Doe"));
            dataList.add(data1);

            when(repository.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(dataList));

            List<DynamicDataDto> result = dynamicDataService.getData(uniqueKey);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("John Doe", result.get(0).getData().get("name"));

            verify(repository).findByUniqueKey(uniqueKey);
        }

        @Test
        public void testGetDataById() {
            String uniqueKey = "abc123";
            String id = "1";
            DynamicData data = new DynamicData();
            data.setUniqueKey(uniqueKey);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", id);
            dataMap.put("name", "John Doe");
            data.setData(dataMap);

            when(endpointDataRepo.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(new EndpointData(uniqueKey, "id")));
            when(repository.findByUniqueKeyAndDataId(uniqueKey, "id", id)).thenReturn(Optional.of(data));

            DynamicDataDto result = dynamicDataService.getDataById(uniqueKey, id);

            assertNotNull(result);
            assertEquals("John Doe", result.getData().get("name"));

            verify(repository).findByUniqueKeyAndDataId(uniqueKey, "id", id);
        }

        @Test
        public void testUpdateData() {
            String uniqueKey = "abc123";
            String id = "1";
            Map<String, Object> newData = new HashMap<>();
            newData.put("name", "Jane Doe");
            newData.put("age", 31);

            DynamicData existingData = new DynamicData();
            existingData.setUniqueKey(uniqueKey);
            Map<String, Object> existingDataMap = new HashMap<>();
            existingDataMap.put("id", id);
            existingDataMap.put("name", "John Doe");
            existingData.setData(existingDataMap);

            when(endpointDataRepo.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(new EndpointData(uniqueKey, "id")));
            when(repository.findByUniqueKeyAndDataId(uniqueKey, "id", id)).thenReturn(Optional.of(existingData));
            when(repository.save(any(DynamicData.class))).thenReturn(existingData);

            DynamicDataDto result = dynamicDataService.updateData(uniqueKey, id, newData);

            assertNotNull(result);
            assertEquals("Jane Doe", result.getData().get("name"));
            assertEquals(31, result.getData().get("age"));

            verify(repository).save(any(DynamicData.class));
        }

        @Test
        public void testPatchUpdateData() {
            String uniqueKey = "abc123";
            String id = "1";
            Map<String, Object> fields = new HashMap<>();
            fields.put("age", 32);

            DynamicData existingData = new DynamicData();
            existingData.setUniqueKey(uniqueKey);
            Map<String, Object> existingDataMap = new HashMap<>();
            existingDataMap.put("id", id);
            existingDataMap.put("name", "John Doe");
            existingDataMap.put("age", 30);
            existingData.setData(existingDataMap);

            when(endpointDataRepo.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(new EndpointData(uniqueKey, "id")));
            when(repository.findByUniqueKeyAndDataId(uniqueKey, "id", id)).thenReturn(Optional.of(existingData));
            when(repository.save(any(DynamicData.class))).thenReturn(existingData);

            DynamicDataDto result = dynamicDataService.patchUpdateData(uniqueKey, id, fields);

            assertNotNull(result);
            assertEquals("John Doe", result.getData().get("name"));
            assertEquals(32, result.getData().get("age"));

            verify(repository).save(any(DynamicData.class));
        }
}
