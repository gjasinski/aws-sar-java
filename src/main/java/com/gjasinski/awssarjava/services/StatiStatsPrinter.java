package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.repositories.ExecutionResultRepository;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionRepository;
import com.gjasinski.awssarjava.repositories.TestExecutionRepository;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class StatiStatsPrinter {
    @Autowired
    private SarFunctionMainRepository mainRepository;
    @Autowired
    private SarSubfunctionRepository sarSubFunctionRepository;
    @Autowired
    private ExecutionResultRepository executionResultRepository;
    @Autowired
    private TestExecutionRepository testExecutionRepository;

    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);

//    @PostConstruct
    public void showGraphs() throws IOException {

        String directory = "C:\\thesis\\" + LocalDate.now();
        new File(directory).mkdir();
        deploymentCountWithinBin(directory);
        languagePieChart(directory);
        return;

    }

    private void languagePieChart(String directory) throws IOException {
        HashMap<String, Integer> runtimeCounter = new HashMap<>();
        List<SarSubFunction> all = sarSubFunctionRepository.findAll();
        all.forEach(f -> {
            runtimeCounter.put(f.getRuntime(), runtimeCounter.getOrDefault(f.getRuntime(), 0) + 1);
        });


        DefaultPieDataset dataset = new DefaultPieDataset( );
   /*     dataset.addValue(0, "Research May 2019", "ruby2.7");
        dataset.addValue(206, "Research May 2019", "python");
        dataset.addValue(0, "Research May 2019", "provided");
        dataset.addValue(108, "Research May 2019", "nodejs");
        dataset.addValue(10, "Research May 2019", "java");
        dataset.addValue(10, "Research May 2019", "go");
        dataset.addValue(0, "Research May 2019", "dotnetcore");

        dataset.addValue(3, "Research August 2021", "ruby2.7");
        dataset.addValue(501, "Research August 2021", "python");
        dataset.addValue(9, "Research August 2021", "provided");
        dataset.addValue(687, "Research August 2021", "nodejs");
        dataset.addValue(17, "Research August 2021", "java");
        dataset.addValue(32, "Research August 2021", "go");
        dataset.addValue(1, "Research August 2021", "dotnetcore");*/
        runtimeCounter.keySet().stream().filter(Objects::nonNull).forEach(k -> dataset.setValue(k, runtimeCounter.get(k)));


        JFreeChart chart = ChartFactory.createPieChart("AWS SAR function runtime",
                dataset, false, true, false);
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        ((PiePlot) chart.getPlot()).setLabelGenerator(gen);

/*

        //liczby na słupku
        CategoryItemRenderer renderer = ((CategoryPlot) chart.getPlot()).getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_CENTER);
        renderer.setDefaultPositiveItemLabelPosition(position);
*/


        int width = 775;
        int height = 480;
        ChartUtils.saveChartAsJPEG(new File(directory + "\\06_languagePieChart.png"), chart, width, height);
    }

    private void deploymentCountWithinBin(String directory) throws IOException {
        List<SarFunctionMain> all = mainRepository.findAll();

        int _0 = 0;
        int _0_10 = 0;
        int _10_100 = 0;
        int _100_1000 = 0;
        int _1000_10000 = 0;
        int _10000_100000 = 0;
        for (int i = 0; i < all.size(); i++) {
            SarFunctionMain f = all.get(i);
            if (f.getDeploymentCount() == 0) {
                _0++;
                continue;
            }
            if (f.getDeploymentCount() < 10) {
                _0_10++;
                continue;
            }
            if (f.getDeploymentCount() < 100) {
                _10_100++;
                continue;
            }
            if (f.getDeploymentCount() < 1_000) {
                _100_1000++;
                continue;
            }
            if (f.getDeploymentCount() < 10_000) {
                _1000_10000++;
                continue;
            }
            _10000_100000++;

        }


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(_0, "0", "0");
        dataset.addValue(_0_10, "1-9", "1-9");
        dataset.addValue(_10_100, "10-99", "10-99");
        dataset.addValue(_100_1000, "100-999", "100-999");
        dataset.addValue(_1000_10000, "1 000-9 999", "1 000-9 999)");
        dataset.addValue(_10000_100000, "10 000-99 999", "10 000-99 999");

        JFreeChart barChart = ChartFactory.createStackedBarChart("AWS SAR functions deployment count",
                "Deployments", "Number of functions",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);


        //liczby na słupku
        CategoryItemRenderer renderer = ((CategoryPlot) barChart.getPlot()).getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_CENTER);
        renderer.setDefaultPositiveItemLabelPosition(position);


        int width = 775;
        int height = 480;
        ChartUtils.saveChartAsJPEG(new File(directory + "\\05_deployment_histogram.png"), barChart, width, height);
    }
}