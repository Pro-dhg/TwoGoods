package com.yamu.data.sample.service.resources.thread;

import com.yamu.data.sample.service.resources.entity.po.VisualizationHomeData;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeDataVO;
import com.yamu.data.sample.service.resources.mapper.VisualizationHomeMapper;

import java.util.concurrent.CountDownLatch;

public class NowDataThread implements Runnable{

    private VisualizationHomeDataVO visualizationHomeDataVO;
    private CountDownLatch latch;
    private VisualizationHomeData data;
    private VisualizationHomeMapper mapper;
    private String queryTable;
    private String dataType;
    private Boolean isToday;
    private Boolean isYoy;
    private String nowEndTime;

    public NowDataThread(VisualizationHomeDataVO visualizationHomeDataVO, CountDownLatch latch,
                         VisualizationHomeMapper mapper, String queryTable, String dataType, Boolean isToday, Boolean isYoy, String nowEndTime){
        this.visualizationHomeDataVO = visualizationHomeDataVO;
        this.latch=latch;
        this.mapper = mapper;
        this.queryTable = queryTable;
        this.dataType = dataType;
        this.isToday = isToday;
        this.isYoy = isYoy;
        this.nowEndTime = nowEndTime;
    }

    public void run(){
        if(isToday){
            if(isYoy){
                if("recursionData".equals(dataType)){
                    data = mapper.queryRecursionParseTrendTodayYoy(visualizationHomeDataVO,nowEndTime);
                }else if("mobileData".equals(dataType)){
                    data = mapper.queryParseTotalCntMobileTodayYoy(visualizationHomeDataVO,nowEndTime);
                }else if("wlanData".equals(dataType)){
                    data = mapper.queryParseTotalCntWlanTodayYoy(visualizationHomeDataVO,nowEndTime);
                }else if("topNData".equals(dataType)){
                    data = mapper.queryTopNParseTotalCntTodayYoy(visualizationHomeDataVO,nowEndTime);
                }else{
                    data = mapper.queryParseTotalCntTodayYoy(visualizationHomeDataVO,nowEndTime);
                }
            }else{
                if("recursionData".equals(dataType)){
                    data = mapper.queryRecursionParseTrendToday(visualizationHomeDataVO);
                }else if("mobileData".equals(dataType)){
                    data = mapper.queryParseTotalCntMobileToday(visualizationHomeDataVO);
                }else if("wlanData".equals(dataType)){
                    data = mapper.queryParseTotalCntWlanToday(visualizationHomeDataVO);
                }else if("topNData".equals(dataType)){
                    data = mapper.queryTopNParseTotalCntToday(visualizationHomeDataVO);
                }else{
                    data = mapper.queryParseTotalCntToday(visualizationHomeDataVO);
                }
            }
        }else{
            if("recursionData".equals(dataType)){
                data = mapper.queryRecursionParseTrend(visualizationHomeDataVO,queryTable);
            }else if("mobileData".equals(dataType)){
                data = mapper.queryParseTotalCntMobile(visualizationHomeDataVO,queryTable);
            }else if("wlanData".equals(dataType)){
                data = mapper.queryParseTotalCntWlan(visualizationHomeDataVO,queryTable);
            }else if("topNData".equals(dataType)){
                data = mapper.queryTopNParseTotalCnt(visualizationHomeDataVO,queryTable);
            }else{
                data = mapper.queryParseTotalCnt(visualizationHomeDataVO,queryTable);
            }
        }
        latch.countDown();
    }

    public VisualizationHomeData getData(){
        return data;
    }

}
