package com.easybackend.test;

import com.easybackend.controller.DynamicDataController;
import com.easybackend.model.DynamicDataDto;
import com.easybackend.model.EndpointData;
import com.easybackend.service.DynamicDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DynamicDataControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DynamicDataService dynamicDataService;

    @InjectMocks
    private DynamicDataController dynamicDataController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dynamicDataController).build();
    }

    @Test
    public void testCreateData() throws Exception {
        String uniqueKey = "abc123";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", 30);

        DynamicDataDto dto = new DynamicDataDto();
        dto.setData("id", "1");
        dto.setData("name", "John Doe");
        dto.setData("age", 30);

        when(dynamicDataService.saveData(eq(uniqueKey), anyMap())).thenReturn(dto);

        mockMvc.perform(post("/api/{uniqueKey}", uniqueKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"age\":30}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(30));

        verify(dynamicDataService).saveData(eq(uniqueKey), anyMap());
    }

    @Test
    public void testGetData() throws Exception {
        String uniqueKey = "abc123";
        List<DynamicDataDto> dtoList = new ArrayList<>();
        DynamicDataDto dto1 = new DynamicDataDto();
        dto1.setData("id", "1");
        dto1.setData("name", "John Doe");
        dtoList.add(dto1);

        when(dynamicDataService.getData(uniqueKey)).thenReturn(dtoList);

        mockMvc.perform(get("/api/{uniqueKey}", uniqueKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(dynamicDataService).getData(uniqueKey);
    }

    @Test
    public void testGetDataById() throws Exception {
        String uniqueKey = "abc123";
        String id = "1";
        DynamicDataDto dto = new DynamicDataDto();
        dto.setData("id", id);
        dto.setData("name", "John Doe");

        when(dynamicDataService.getDataById(uniqueKey, id)).thenReturn(dto);

        mockMvc.perform(get("/api/{uniqueKey}/{id}", uniqueKey, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(dynamicDataService).getDataById(uniqueKey, id);
    }

    @Test
    public void testUpdateData() throws Exception {
        String uniqueKey = "abc123";
        String id = "1";
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Jane Doe");
        data.put("age", 31);

        DynamicDataDto dto = new DynamicDataDto();
        dto.setData("id", id);
        dto.setData("name", "Jane Doe");
        dto.setData("age", 31);

        when(dynamicDataService.updateData(eq(uniqueKey), eq(id), anyMap())).thenReturn(dto);

        mockMvc.perform(put("/api/{uniqueKey}/{id}", uniqueKey, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jane Doe\",\"age\":31}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.age").value(31));

        verify(dynamicDataService).updateData(eq(uniqueKey), eq(id), anyMap());
    }

    @Test
    public void testPatchData() throws Exception {
        String uniqueKey = "abc123";
        String id = "1";
        Map<String, Object> fields = new HashMap<>();
        fields.put("age", 32);

        DynamicDataDto dto = new DynamicDataDto();
        dto.setData("id", id);
        dto.setData("name", "John Doe");
        dto.setData("age", 32);

        when(dynamicDataService.patchUpdateData(eq(uniqueKey), eq(id), anyMap())).thenReturn(dto);

        mockMvc.perform(patch("/api/{uniqueKey}/{id}", uniqueKey, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"age\":32}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.age").value(32));

        verify(dynamicDataService).patchUpdateData(eq(uniqueKey), eq(id), anyMap());
    }

    @Test
    public void testDeleteData() throws Exception {
        String uniqueKey = "abc123";
        String id = "1";

        when(dynamicDataService.deleteData(uniqueKey, id)).thenReturn("Data deleted Successfully");

        mockMvc.perform(delete("/api/{uniqueKey}/{id}", uniqueKey, id))
                .andExpect(status().isOk())
                .andExpect(content().string("Data deleted Successfully"));

        verify(dynamicDataService).deleteData(uniqueKey, id);
    }

    @Test
    public void testDeleteAllData() throws Exception {
        String uniqueKey = "abc123";

        when(dynamicDataService.deleteAllData(uniqueKey)).thenReturn("All Data deleted Successfully");

        mockMvc.perform(delete("/api/{uniqueKey}", uniqueKey))
                .andExpect(status().isOk())
                .andExpect(content().string("All Data deleted Successfully"));

        verify(dynamicDataService).deleteAllData(uniqueKey);
    }

    @Test
    public void testGenerateUniqueKey() throws Exception {
        when(dynamicDataService.generateUniqueKey()).thenReturn("xyz789");

        mockMvc.perform(get("/api/generateUniqueKey"))
                .andExpect(status().isOk())
                .andExpect(content().string("xyz789"));

        verify(dynamicDataService).generateUniqueKey();
    }

    @Test
    public void testSaveEndpointKey() throws Exception {
        String uniqueKey = "abc123";
        String keyId = "customId";
        EndpointData endpointData = new EndpointData(uniqueKey, keyId);

        when(dynamicDataService.saveEndpointKey(uniqueKey, keyId)).thenReturn(endpointData);

        mockMvc.perform(post("/api/endpoint")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"uniqueKey\":\"abc123\",\"keyId\":\"customId\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uniqueKey").value(uniqueKey))
                .andExpect(jsonPath("$.keyId").value(keyId));

        verify(dynamicDataService).saveEndpointKey(uniqueKey, keyId);
    }
}

