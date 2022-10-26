package com.yamu.data.sample.service.resources.visualizationHomeMap;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VisualizationHomeRamMap {

    public static Map<String, List> regionMap = new HashMap<String, List>();

    public Map<String, List> initRegion(String type,List list){
        regionMap.put(type,list);
        return  regionMap;
    }
    public Map<String, List> getAllRegionMap(){
        return getRegionMap();
    }

    public static Map<String, List> getRegionMap() {
        return regionMap;
    }

    public static void setRegionMap(Map<String, List> regionMap) {
        VisualizationHomeRamMap.regionMap = regionMap;
    }
}
