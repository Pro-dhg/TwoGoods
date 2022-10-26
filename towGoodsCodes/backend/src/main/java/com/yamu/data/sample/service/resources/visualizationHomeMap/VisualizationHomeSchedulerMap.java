package com.yamu.data.sample.service.resources.visualizationHomeMap;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VisualizationHomeSchedulerMap {

    public static Map<String, List> homeSchedulerMap = new HashMap<String, List>();

    public Map<String, List> initRegion(String type,List list){
        homeSchedulerMap.put(type,list);
        return  homeSchedulerMap;
    }
    public Map<String, List> getAllRegionMap(){
        return getRegionMap();
    }

    public static Map<String, List> getRegionMap() {
        return homeSchedulerMap;
    }

    public static void setRegionMap(Map<String, List> homeSchedulerMap) {
        VisualizationHomeSchedulerMap.homeSchedulerMap = homeSchedulerMap;
    }

    public void removeAll(){
        homeSchedulerMap.clear();
    }
}
