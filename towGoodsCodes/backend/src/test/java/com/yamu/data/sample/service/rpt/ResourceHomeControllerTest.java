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
public class ResourceHomeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();//建议使用这种
    }

    @Test
    public void findDomainTopNType() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainTopNType/v1") //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON) //数据的格式
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .param("startTime", "2021-10-21 09:46:07")
                        .param("endTime", "2021-10-19 09:56:07")
                        .param("rankNumber", "10")
                        .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findWebsiteTopNType() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/websiteTopNType/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainTopN() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainTopN/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainNetRate() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainNetRate/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainParse() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainParse/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainParseRate() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainParseRate/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainSuccessRate() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainSuccessRate/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainNetInRate() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainNetInRate/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findDomainWithInRate() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainWithInRate/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findProvinceResourceMap() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/map/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
    }

    @Test
    public void domainDetailRate() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/domainDetailRate/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("startTime", "2021-10-21 09:46:07")
                .param("endTime", "2021-10-19 09:56:07")
                .param("rankNumber", "10")
                .param("queryType", "1min")
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

    @Test
    public void findParseRealtime() throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/service/resource/home/parseRealtime/v1") //请求的url,请求的方法是get
                .contentType(MediaType.APPLICATION_JSON) //数据的格式
                .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        System.out.println(responseString);
    }

}
