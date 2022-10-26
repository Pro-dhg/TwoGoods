package com.yamu.data.sample.service.resources.common.utils.pdf;

import com.yamu.data.sample.service.resources.ResourcesApplication;
import com.yamu.data.sample.service.resources.service.ResourceDownloadReportService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourcesApplication.class)
@WebAppConfiguration
class PDFGeneratorTest {

    public static final String path = "D:\\wuxuehai\\pdf";

    @Autowired
    private ResourceDownloadReportService service;

    /*
    @Test
    void generatePdf() throws DocumentException, IOException , Exception{
        String fullPath = path + File.separator + UUID.randomUUID() + ".pdf";
        ResourceWebsiteReport websiteReport = new ResourceWebsiteReport();

        websiteReport.setEndTime("2021-11-19 16:52:37");
        websiteReport.setStartTime("2021-11-19 16:42:37");
        websiteReport.setDomainNameTrendChart("0");
        websiteReport.setWebsiteTopNTrendChart("0");
        websiteReport.setDomainNameTopNTrendChart("0");
        websiteReport.setWebsiteTopNTypeTrendChart("0");
        websiteReport.setAnswerDistributionTrendChart("0");
        websiteReport.setOperatorTrendChart("0");
        websiteReport.setWebsite("0");
        websiteReport.setWebsiteType("0");
        websiteReport.setWebsiteDomainName("0");
        websiteReport.setCompany("0");
        websiteReport.setCdnDomainName("0");
        websiteReport.setCdnManufacturer("0");

        //service.download(websiteReport, null);
    }
    */
}