package com.yamu.data.sample.service.rpt;

import com.yamu.data.sample.service.resources.ResourcesApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author getiejun
 * @date 2021/10/20
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourcesApplication.class)
@WebAppConfiguration
public class ResourceWebsiteTopNDetailControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();//建议使用这种
    }

    @Test
    public void findDomainNetOut() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/domainNetOut/v1") //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON) //数据的格式
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .param("websiteAppName", "QQ")
                        .param("offset", "0")
                        .param("limit", "10")
                        .param("startTime", "2021-11-01 13:59:57")
                        .param("endTime", "2021-11-01 14:09:57")
                        .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainNetOutDetail() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/domainNetOutDetail/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("websiteAppName", "QQ")
                .param("offset", "0")
                .param("limit", "10")
                .param("startTime", "2021-11-01 13:59:57")
                .param("endTime", "2021-11-01 14:09:57")
                .param("queryType", "1min")
                .param("domainName", "appservice.qq.com")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findIspOfDomainNetOut() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/domainNetOutIsp/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("websiteAppName", "QQ")
                .param("startTime", "2021-11-01 13:59:57")
                .param("endTime", "2021-11-01 14:09:57")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void download() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/download/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("offset", "0")
                .param("limit", "10")
                .param("startTime", "2021-11-01 13:59:57")
                .param("endTime", "2021-11-01 14:09:57")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findWithinProvinceDomainRank() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/rankTrend/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("websiteAppName", "QQ")
                .param("offset", "0")
                .param("limit", "10")
                .param("netInRateMin", "10")
                .param("netInRateMax", "90")
                .param("startTime", "2021-11-01 13:59:57")
                .param("endTime", "2021-11-01 14:09:57")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }


    @Test
    public void findTrendList() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/tableList/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("websiteAppName", "QQ")
                .param("offset", "0")
                .param("limit", "10")
                .param("netInRateMin", "10")
                .param("netInRateMax", "90")
                .param("startTime", "2021-11-01 13:59:57")
                .param("endTime", "2021-11-01 14:09:57")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }


    @Test
    public void findRateReport() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/websiteTopNDetail/rateReport/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("websiteAppName", "QQ")
                .param("offset", "0")
                .param("limit", "10")
                .param("netInRateMin", "10")
                .param("netInRateMax", "90")
                .param("startTime", "2021-11-01 13:59:57")
                .param("endTime", "2021-11-01 14:09:57")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

}
