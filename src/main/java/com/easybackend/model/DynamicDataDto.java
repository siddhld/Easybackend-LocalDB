package com.easybackend.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicDataDto {

    @JsonIgnore
    private Map<String, Object> data = new LinkedHashMap<>();
    public DynamicDataDto(DynamicData dynamicData){
        this.data = dynamicData.getData();
    }

    @JsonAnyGetter
    public Map<String, Object> getData(){
        return this.data;
    }

    @JsonAnySetter
    public void setData(String key, Object value){
        data.put(key, value);
    }
}
