package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SonarResult;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SonarResultRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SonarGraphCreator {
    @Autowired
    SonarResultRepository repo;
    @Autowired
    SarFunctionMainRepository mainrepo;

//    @PostConstruct
    public void generate() throws IOException {
        String directory = "C:\\thesis\\" + LocalDate.now();
        new File(directory).mkdir();

        List<SonarResult> all = repo.findAll();
        List<SonarResult> codeSmellsList = all.stream()
                .filter(m -> m.getMetric().equals("code_smells"))
                .collect(Collectors.toList());
        Map<String, Integer> codeSmells = new HashMap<>();
        codeSmellsList.forEach(m -> {
            if (m.getValue() < 10) {
                String key = m.getValue() + "";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 20) {
                String key = "10_20";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 30) {
                String key = "20_30";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 40) {
                String key = "30_40";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 50) {
                String key = "40_50";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 60) {
                String key = "50_60";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 70) {
                String key = "60_70";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 80) {
                String key = "70_80";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 90) {
                String key = "80_90";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 100) {
                String key = "90_100";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else if (m.getValue() < 1_000) {
                String key = "100_1000";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            } else {
                String key = "1000_";
                codeSmells.put(key, codeSmells.getOrDefault(key, 0) + 1);
            }

        });
//        double[] codeSmellArr = list2Arr(codeSmellsList);
        codeSmellHist(directory, codeSmells);

        List<SonarResult> bugsList = all.stream().filter(m -> m.getMetric().equals("bugs")).collect(Collectors.toList());
        double[] bugsArr = list2Arr(bugsList);
        bugsHist(directory, bugsArr);

        List<SonarResult> vulnHotSpotsList = all.stream().filter(m -> m.getMetric().equals("security_hotspots")).collect(Collectors.toList());
        double[] vulnHotSpotArr = list2Arr(vulnHotSpotsList);
        securityHotspotsHist(directory, vulnHotSpotArr);

        List<SonarResult> vulnerabilitiesList = all.stream().filter(m -> m.getMetric().equals("vulnerabilities")).collect(Collectors.toList());
        double[] vulnerabilitiesArr = list2Arr(vulnerabilitiesList);
        vulnerabilitiesHist(directory, vulnerabilitiesArr);

        scatteredPlot(directory);
    }

    private void scatteredPlot(String directory) throws IOException {


        List<SarFunctionMain> all = mainrepo.findAll();
        XYSeries securityHotspots = new XYSeries("SecurityHotspots", false, true);
        XYSeries vulnerabilities = new XYSeries("Vulnerabilities", false, true);
        XYSeries bugs = new XYSeries("Bugs", false, true);
        XYSeries codeSmells = new XYSeries("Smell codes", false, true);
        all.forEach(f -> {
            if (f.getDeploymentCount() < 10_000){
                List<SonarResult> byFunctionMain = repo.findByFunctionMain(f);
//            if (f.getDeploymentCount() < 20_000) {
/*                Integer sum = 0;
                for (int i = 0; i < byFunctionMain.size(); i++) {
//                    if (byFunctionMain.get(i).getValue() > 0 && byFunctionMain.get(i).getValue() < 3000) {
                        sum += byFunctionMain.get(i).getValue();
//                    }
                }*/
                byFunctionMain.forEach(m -> {
                        if (m.getMetric().equals("code_smells") && m.getValue() < 2000) {
                            codeSmells.add(f.getDeploymentCount(), m.getValue());
                        }
                        if (m.getMetric().equals("bugs") && m.getValue() < 200) {
                            bugs.add(f.getDeploymentCount(), m.getValue());
                        }
                        if (m.getMetric().equals("vulnerabilities")) {
                            vulnerabilities.add(f.getDeploymentCount(), m.getValue());
                        }
                        if (m.getMetric().equals("security_hotspots")) {
                            securityHotspots.add(f.getDeploymentCount(), m.getValue());
                        }
                });
            /*if (f.getDeploymentCount() < 10_000 && sum < 2_000) {
                codeSmells.add(f.getDeploymentCount(), sum);
            }*/

//            }
            }
        });
        scatteredChart(directory, securityHotspots, "Function deployments and security hotsposts", "Security hotspots");
        scatteredChart(directory, vulnerabilities, "Function deployments and vulnerabilities", "Vulnerabilities");
        scatteredChart(directory, bugs, "Function deployments and bugs", "Bugs");
        scatteredChart(directory, codeSmells, "Function deployments and code smells", "Code smells");
/*        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(securityHotspots);
        dataset.addSeries(vulnerabilities);
        dataset.addSeries(bugs);
        dataset.addSeries(codeSmells);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Function deployments and number of issues",
                "Deployments", "Issues", dataset);

        // 5x5 red pixel circle
//        Shape shape  = new Ellipse2D.Double(0,0,5,5);
//        XYPlot xyPlot = (XYPlot) chart.getPlot();
//        XYItemRenderer renderer = xyPlot.getRenderer();
//        renderer.setDefaultShape(shape);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setDefaultShapesFilled(false);
        renderer.setUseFillPaint(false);
//        renderer.setDefaultShape(new Rectangle(1, 1));
//        renderer.setSeriesShape(0, new Rectangle(1, 1));
//        renderer.setSeriesShape(1, new Rectangle(1, 1));
//        renderer.setSeriesShape(2, new Rectangle(1, 1));
//        renderer.setSeriesShape(3, new Rectangle(1, 1));

        LogAxis domainAxis = new LogAxis("Deployments");
        domainAxis.setNumberFormatOverride(new DecimalFormat("0"));
        chart.getXYPlot().setDomainAxis(domainAxis);

        LogAxis ValueAxis = new LogAxis("Issues");
        ValueAxis.setNumberFormatOverride(new DecimalFormat("0"));
        chart.getXYPlot().setRangeAxis(ValueAxis);

        ChartUtils.saveChartAsPNG(new File(directory + "\\07_sonar_scatter.png"), chart, 600, 400);*/
    }

    private void scatteredChart(String directory, XYSeries series, String title, String yLabel) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot(title,
                "Deployments", yLabel, dataset);

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
        renderer.setDefaultShapesFilled(false);
        renderer.setUseFillPaint(false);
        Shape shape  = new Ellipse2D.Double(0,0,5,5);
        renderer.setSeriesShape(0, shape);


        ChartUtils.saveChartAsPNG(new File(directory + "\\07_sonar_scatter_" + yLabel + ".png"), chart, 600, 400);

/*        LogAxis domainAxis = new LogAxis("Deployments");
        domainAxis.setNumberFormatOverride(new DecimalFormat("0"));
        chart.getXYPlot().setDomainAxis(domainAxis);

        LogAxis ValueAxis = new LogAxis(yLabel);
        ValueAxis.setNumberFormatOverride(new DecimalFormat("0"));
        chart.getXYPlot().setRangeAxis(ValueAxis);

        ChartUtils.saveChartAsPNG(new File(directory + "\\07_sonar_scatter_" + yLabel + "log.png"), chart, 600, 400);*/
    }

    private double[] list2Arr(List<SonarResult> codeSmellsList) {
        double[] codeSmellArr = new double[codeSmellsList.size()];
        for (int i = 0; i < codeSmellsList.size(); i++) {
            if (codeSmellsList.get(i) != null) {
                codeSmellArr[i] = codeSmellsList.get(i).getValue();
            }
        }
        return codeSmellArr;
    }

    private void codeSmellHist(String directory, Map<String, Integer> codeSmells) throws IOException {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < 10; i++) {
            Integer value = codeSmells.get(i + "");
            dataset.addValue(value, "", i + "");
        }
        dataset.addValue(codeSmells.getOrDefault("10_20", 0), "", "10-19");
        dataset.addValue(codeSmells.getOrDefault("20_30", 0), "", "20-29");
        dataset.addValue(codeSmells.getOrDefault("30_40", 0), "", "30-39");
        dataset.addValue(codeSmells.getOrDefault("40_50", 0), "", "40-49");
        dataset.addValue(codeSmells.getOrDefault("50_60", 0), "", "50-59");
        dataset.addValue(codeSmells.getOrDefault("60_70", 0), "", "60-69");
        dataset.addValue(codeSmells.getOrDefault("70_80", 0), "", "70-79");
        dataset.addValue(codeSmells.getOrDefault("80_90", 0), "", "80-89");
        dataset.addValue(codeSmells.getOrDefault("90_100", 0), "", "90-99");
        dataset.addValue(codeSmells.getOrDefault("100_1000", 0), "", "100-999");
        dataset.addValue(codeSmells.getOrDefault("1000_", 0), "", "1000-");


        JFreeChart barChart = ChartFactory.createStackedBarChart("How many functions from AWS SAR have code smells",
                "Code smells", "Number of functions",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);


        //liczby na słupku
        CategoryItemRenderer renderer = ((CategoryPlot) barChart.getPlot()).getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_CENTER);
        CategoryAxis axis = barChart.getCategoryPlot().getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        renderer.setDefaultPositiveItemLabelPosition(position);
        ChartUtils.saveChartAsPNG(new File(directory + "\\01_code_smells.png"), barChart, 600, 400);

    }

/*
    private void codeSmellHist(String directory, double[] codeSmells) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Code smells", codeSmells, 200);
        JFreeChart histogram = ChartFactory.createHistogram("Code smells",
                "Number of code smells", "Number of functions", dataset);

        ChartUtils.saveChartAsPNG(new File(directory + "\\01_code_smells.png"), histogram, 600, 400);
    }
*/

    private void vulnerabilitiesHist(String directory, double[] codeSmells) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Vulnerabilities", codeSmells, 100);
        JFreeChart histogram = ChartFactory.createHistogram("Vulnerabilities",
                "Number of vulnerabilities", "Number of functions", dataset);

        ChartUtils.saveChartAsPNG(new File(directory + "\\02_Vulnerabilities.png"), histogram, 600, 400);
    }

    private void securityHotspotsHist(String directory, double[] codeSmells) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Security HotSpots", codeSmells, 100);
        JFreeChart histogram = ChartFactory.createHistogram("Security HotSpots",
                "Number of Security HotSpots", "Number of functions", dataset);

        ChartUtils.saveChartAsPNG(new File(directory + "\\03_Security HotSpots.png"), histogram, 600, 400);
    }


    private void bugsHist(String directory, double[] codeSmells) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Bugs", codeSmells, 100);
        JFreeChart histogram = ChartFactory.createHistogram("Bugs",
                "Number of bugs", "Number of functions", dataset);

        ChartUtils.saveChartAsPNG(new File(directory + "\\04_Bugs.png"), histogram, 600, 400);
    }

}
