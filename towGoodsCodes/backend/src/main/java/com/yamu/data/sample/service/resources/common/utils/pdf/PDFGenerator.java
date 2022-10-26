package com.yamu.data.sample.service.resources.common.utils.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.events.IndexEvents;
import com.yamu.data.sample.service.resources.entity.po.ResourceReportPdf;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.VerticalAlignment;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.itextpdf.text.Image.getInstance;

/**
 * @author xh.wu
 * @date 2021/11/19
 */
public class PDFGenerator {

    public static com.itextpdf.text.Font font;

    static {
        font = new com.itextpdf.text.Font();
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            //baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\simfang.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            font = new com.itextpdf.text.Font(baseFont, 10);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean generatePdf(List<ResourceReportPdf> data, ServletOutputStream outputStream) throws IOException, DocumentException {
        if (data == null || data.size() == 0 || outputStream == null) {
            return false;
        }
        //章节序号
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        IndexEvents indexEvents = new IndexEvents();
        writer.setPageEvent(indexEvents);
        document.open();
        document.newPage();
        Chapter chapter = new Chapter(new Paragraph("资源分析报告", font), 1);
        chapter.add(new Paragraph("\n"));
        Section lastSection = null;
        for (int i = 0; i < data.size(); i++) {
            ResourceReportPdf report = data.get(i);
            switch (report.getReportPdfType()) {
                case LINECHART: {
                    writeLineChart(chapter, report.getTitleName(), (DefaultCategoryDataset) report.getData());
                    break;
                }
                case AREACHART: {
                    writeStackedAreaChart(document, chapter, report.getTitleName(), (DefaultCategoryDataset) report.getData(), i);
                    break;
                }

                case PIECHART: {
                    writePieChart(document, chapter, report.getTitleName(), (DefaultPieDataset) report.getData());
                    break;
                }

                case HISTOGRAM: {
                    if (StringUtils.isNotEmpty(report.getTitleName())) {
                        Section section = chapter.addSection(new Paragraph(report.getTitleName(), font));
                        //section.setIndentationLeft(10);
                        section.add(new Paragraph("\n"));
                        lastSection = section;
                    }
                    writeBarChart(lastSection, (DefaultCategoryDataset) report.getData());
                    if (i % 2 == 0) {
                        lastSection.add(new Paragraph("\n"));
                        lastSection.add(new Paragraph("\n"));
                        continue;
                    }
                    if (i % 2 == 1) {
                        lastSection.newPage();
                        continue;
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            if (i % 2 == 0) {
                chapter.add(new Paragraph("\n"));
                chapter.add(new Paragraph("\n"));
            }

            if (i % 2 == 1) {
                chapter.newPage();
            }
        }
        document.add(chapter);
        document.close();
        writer.close();
        return true;
    }
    private static void writeLineChart(Chapter chapter, String title, DefaultCategoryDataset dataset) throws IOException, BadElementException {
        Section section = chapter.addSection(new Paragraph(title, font));
        section.add(new Paragraph("\n"));
        JFreeChart lineChart = ChartUtil.lineChart(null, null, null, dataset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsJPEG(bos, lineChart, 850, 400);
        com.itextpdf.text.Image image = getInstance(bos.toByteArray());
        image.scalePercent(60);

        PdfPTable table =createTable();
        PdfPCell cell = createCell();
        cell.addElement(image);
        table.addCell(cell);
        section.add(table);
    }
    private static void writeStackedAreaChart(Document document, Chapter chapter, String title, DefaultCategoryDataset dataset, int index) throws DocumentException, IOException {
        if (!StringUtils.isEmpty(title)) {
            Section section = chapter.addSection(new Paragraph(title, font));
            //section.setIndentationLeft(10);
            section.add(new Paragraph("\n"));
            //document.add(section);
            JFreeChart stackedAreaChart = ChartUtil.stackedAreaChart(null, null, null, dataset);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ChartUtils.writeChartAsJPEG(bos, stackedAreaChart, 850, 400);
            com.itextpdf.text.Image image = getInstance(bos.toByteArray());
            image.scalePercent(60);

            PdfPTable table =createTable();
            PdfPCell cell = createCell();
            cell.addElement(image);
            table.addCell(cell);
            section.add(table);
            return;
        }

        JFreeChart stackedAreaChart = ChartUtil.stackedAreaChart(null, null, null, dataset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsJPEG(bos, stackedAreaChart, 700, 380);
        com.itextpdf.text.Image image = getInstance(bos.toByteArray());
        image.scalePercent(60);
        document.add(image);
    }

    private static void writeBarChart(Section section, DefaultCategoryDataset dataset) throws IOException, DocumentException {
        JFreeChart barChart = ChartUtil.barChart(null, null, null, dataset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsJPEG(bos, barChart, 850, dataset.getColumnCount() * 20 > 400 ? dataset.getColumnCount() * 20 : 400 );
        com.itextpdf.text.Image image = getInstance(bos.toByteArray());
        image.scalePercent(60);

        PdfPTable table =createTable();
        PdfPCell cell = createCell();
        cell.addElement(image);
        table.addCell(cell);
        section.add(table);
    }

    private static void writePieChart(Document document, Chapter chapter, String title, DefaultPieDataset dataset) throws DocumentException, IOException {
        if (!StringUtils.isEmpty(title)) {
            Section section = chapter.addSection(new Paragraph(title, font));
            //section.setIndentationLeft(10);
            section.add(new Paragraph("\n"));
            JFreeChart pieChart = ChartUtil.pieChart(null, dataset);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ChartUtils.writeChartAsJPEG(bos, pieChart, 850, 450);
            com.itextpdf.text.Image image = getInstance(bos.toByteArray());
            image.scalePercent(60);

            PdfPTable table =createTable();
            PdfPCell cell = createCell();
            cell.addElement(image);
            table.addCell(cell);
            section.add(table);
            return;
        }
        JFreeChart pieChart = ChartUtil.pieChart(null, dataset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsJPEG(bos, pieChart, 800, 500);
        com.itextpdf.text.Image image = getInstance(bos.toByteArray());
        image.scalePercent(60);
        document.add(image);
    }

    private static PdfPCell createCell() {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderWidth(0);
        cell.setPadding(20f);
        return cell;
    }

    private static PdfPTable createTable() {
        PdfPTable table = new PdfPTable(1);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setTotalWidth(560);
        table.setLockedWidth(true);
        table.getDefaultCell().setBorder(0);
        return table;
    }

    static class ChartUtil {
        private static final Color[] BAR_COLORS = new Color[]{
                new Color(51, 198, 247),
                new Color(192, 80, 77),
                new Color(155, 187, 89),
        };

        private static final Color[] LINE_COLORS = new Color[]{
                new Color(51, 198, 247),
                new Color(145,204,117),
                new Color(250,200,88),
                new Color(238,102,102),
                new Color(115,192,222),
                new Color(59,162,114),
                new Color(252,132,82),
                new Color(154,96,180),
                new Color(203,176,227),
                new Color(255,105,180),
        };

        private static final Color AREA_COLOR = new Color(	135,206,250);

        private static final Color[] PIE_COLORS = new Color[]{
                new Color(84,112,198),
                new Color(145,204,117),
                new Color(250,200,88),
                new Color(238,102,102),
                new Color(115,192,222),
                new Color(59,162,114),
                new Color(252,132,82),
                new Color(154,96,180),
                new Color(203,176,227),
                new Color(255,105,180),
                new Color(186,85,211)
        };


        private static StandardChartTheme initChartTheme() {
            StandardChartTheme currentTheme = new StandardChartTheme("JFree");
            // 横轴纵轴标题文字大小
            currentTheme.setLargeFont(new java.awt.Font("宋体", java.awt.Font.BOLD, 15));
            // 横轴纵轴数值文字大小
            currentTheme.setRegularFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 13));
            currentTheme.setExtraLargeFont(new java.awt.Font("宋体", java.awt.Font.BOLD, 20));
            // 背景颜色
            currentTheme.setPlotBackgroundPaint(new Color(255, 255, 204, 0));
            // 边框线条
            currentTheme.setPlotOutlinePaint(new Color(0, 0, 0, 0));
            // 网格线条
            currentTheme.setRangeGridlinePaint(new Color(78, 74, 74));
            return currentTheme;
        }

        /**
         * 线图
         */
        public static JFreeChart lineChart(String title, String categoryAxisLabel, String valueAxisLabel, DefaultCategoryDataset dataset) {
            ChartFactory.setChartTheme(initChartTheme());

            JFreeChart chart = ChartFactory.createLineChart(
                    title,
                    categoryAxisLabel,
                    valueAxisLabel,
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            // 折现点显示数值
            renderer.setDefaultItemLabelsVisible(false);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            // 更改线条颜色
            for (int i = 0; i < dataset.getRowKeys().size(); i++) {
                renderer.setSeriesPaint(i, LINE_COLORS[i]);
            }
            renderer.setDefaultShapesVisible(true);
            renderer.setDefaultStroke(new BasicStroke(5.0f));
            plot.setRangeGridlinesVisible(true);
            plot.setOutlineVisible(false);
            return chart;
        }

        /**
         * 柱状图
         */
        public static JFreeChart barChart(String title, String categoryAxisLabel, String valueAxisLabel, DefaultCategoryDataset dataset) {
            ChartFactory.setChartTheme(initChartTheme());

            JFreeChart chart = ChartFactory.createBarChart(
                    title,
                    categoryAxisLabel,
                    valueAxisLabel,
                    dataset,
                    PlotOrientation.HORIZONTAL,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            // 纯色显示
            renderer.setBarPainter(new StandardBarPainter());
            // 柱子上显示小数字
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
            /*ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT);

            renderer.setDefaultPositiveItemLabelPosition(position);
            renderer.setSeriesPositiveItemLabelPosition(0,position);*/
            //renderer.setItemLabelAnchorOffset(0d);
            // 设置柱子间隔
            renderer.setItemMargin(0.0);
            // 设置柱子颜色
            for (int i = 0; i < dataset.getRowKeys().size(); i++) {
                renderer.setSeriesPaint(i, BAR_COLORS[i]);
            }
            renderer.setMaximumBarWidth(5);
            plot.getDomainAxis().setVisible(true);
            plot.getDomainAxis().setAxisLineVisible(false);
            //横坐标占比
            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.2f);
            plot.getRangeAxis().setUpperMargin(0.1);
            plot.getRangeAxis().setAxisLineVisible(false);
            plot.getRangeAxis().setVisible(false);

            plot.setRangeGridlinesVisible(false);
            plot.setNoDataMessage("暂无数据");
            plot.setNoDataMessageFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 15));

            return chart;
        }

        /**
         * 饼图
         */
        public static JFreeChart pieChart(String title, DefaultPieDataset dataset) {
            ChartFactory.setChartTheme(initChartTheme());

            JFreeChart chart = ChartFactory.createPieChart(
                    title,
                    dataset,
                    true,
                    true,
                    false
            );
            PiePlot plot = (PiePlot) chart.getPlot();
            // 设置扇区颜色
            for (int i = 0; i < dataset.getKeys().size(); i++) {
                plot.setSectionPaint(dataset.getKey(i), PIE_COLORS[i % PIE_COLORS.length]);
            }
            // 设置扇区的线条颜色
            plot.setDefaultSectionOutlinePaint(new Color(255, 255, 255));
            // 设置扇区的线条大小
            plot.setDefaultSectionOutlineStroke(new BasicStroke(3));
            // 设置标签颜色
            plot.setLabelLinkPaint(new Color(255, 255, 255, 0));
            // 设置标签背景色
            plot.setLabelBackgroundPaint(new Color(255, 255, 255, 0));
            // 设置标签线条颜色
            plot.setLabelOutlinePaint(new Color(255, 255, 255, 0));
            // 设置标签阴影颜色
            plot.setLabelShadowPaint(new Color(255, 255, 255, 0));
            // 设置饼图阴影颜色
            plot.setShadowPaint(new Color(255, 255, 255, 0));
            plot.setLabelLinksVisible(false);
            plot.setLabelGenerator(null);
            // 添加标签数字百分比显示
            //plot.setLabelGenerator(new StandardPieSectionLabelGenerator(("{0}{2}"), NumberFormat.getNumberInstance(),new DecimalFormat("0.00%")));
            LegendTitle legend = chart.getLegend();
            legend.setPosition(RectangleEdge.BOTTOM);
            legend.setLegendItemGraphicPadding(new RectangleInsets(10d, 10d, 10d, 10d));
            //legend.setItemFont(new Font("宋体",Font.BOLD, 15));
            //legend.setMargin(0d, 0d, 0d, 100d);
            //legend.setVisible(false);
            legend.setVerticalAlignment(VerticalAlignment.CENTER);

            return chart;
        }

        public static JFreeChart stackedAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {
            JFreeChart chart = ChartFactory.createStackedAreaChart(
                    title,
                    categoryAxisLabel,
                    valueAxisLabel,
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            StackedAreaRenderer renderer = (StackedAreaRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, AREA_COLOR);
            /*plot.setDomainGridlinesVisible(false);
            plot.setDomainCrosshairVisible(false);
            plot.setRangeCrosshairVisible(false);
            plot.setRangeMinorGridlinesVisible(false);
            plot.setDrawSharedDomainAxis(false);
            plot.setRangeCrosshairValue(0d);*/
            plot.setOutlineVisible(false);

            plot.setBackgroundPaint(new Color(255, 255, 255));
            plot.setRangeGridlinesVisible(false);
            //chart.getLegend().setVisible(false);
            /*LegendTitle legendTitle = chart.getLegend();
            if (legendTitle != null) {
                legendTitle.setItemFont(new Font("宋体",Font.BOLD, 15));
            }*/

            return chart;
        }
    }
}