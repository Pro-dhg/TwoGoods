package com.yamu.data.sample.service.resources.thread;

import com.yamu.data.sample.service.resources.entity.po.ResourceDomainTopnDetail;
import com.yamu.data.sample.service.resources.mapper.ResourceDomainTopnDetailMapper;
import java.util.concurrent.CountDownLatch;

public class ResourceHomeKeyBusinessThread implements Runnable{

    private ResourceDomainTopnDetail domainTopNDetail;
    private CountDownLatch latch;
    private ResourceDomainTopnDetail data;
    private ResourceDomainTopnDetailMapper mapper;

    public ResourceHomeKeyBusinessThread(ResourceDomainTopnDetail domainTopNDetail, CountDownLatch latch,ResourceDomainTopnDetailMapper mapper){
        this.domainTopNDetail = domainTopNDetail;
        this.latch=latch;
        this.mapper = mapper;
    }

    public void run(){
        data = mapper.queryParseTotalCntRateByParam(domainTopNDetail);
        latch.countDown();
    }

    public ResourceDomainTopnDetail getData(){
        return data;
    }
}
