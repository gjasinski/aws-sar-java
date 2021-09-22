package com.gjasinski.awssarjava.services;

import com.gjasinski.awssarjava.entity.ExecutionResult;
import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.entity.TestExecution;
import com.gjasinski.awssarjava.repositories.ExecutionResultRepository;
import com.gjasinski.awssarjava.repositories.SarFunctionMainRepository;
import com.gjasinski.awssarjava.repositories.SarSubfunctionRepository;
import com.gjasinski.awssarjava.repositories.TestExecutionRepository;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
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
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExecutionStatsPrinter {
    @Autowired
    private SarFunctionMainRepository mainRepository;
    @Autowired
    private SarSubfunctionRepository sarSubFunctionRepository;
    @Autowired
    private ExecutionResultRepository executionResultRepository;
    @Autowired
    private TestExecutionRepository testExecutionRepository;

    private static Logger LOGGER = Logger.getLogger(FunctionCloneService.class);

    @PostConstruct
    public void test() throws IOException {
//        int testExecutionId = 385;
        int testExecutionId = 552;  // <----------
//        int testExecutionId = 604;
        String directory = "C:\\thesis\\" + LocalDate.now();
        new File(directory).mkdir();
        basicFunctionStats(directory);
        Optional<TestExecution> testExecutionOptional = testExecutionRepository.findById((long) testExecutionId);

        int allInvocations = mainRepository.countAllExecutions(testExecutionId).intValue();
        int successInvocations = mainRepository.countAllSuccessfulExecutions(testExecutionId).intValue();
        invocationStats(directory, successInvocations, allInvocations - successInvocations);

        List<Long> successfulFunctionsIds = mainRepository.getAllSucessfulFunctions(testExecutionId);
        List<Long> unsuccessfulFunctionsIds = mainRepository.getAllNotSucessfulFunctions(testExecutionId);
        int verifiedSuccess = 0;
        int notVerifiedSuccess = 0;
        int verifiedFailed = 0;
        int notVerifiedFailed = 0;

        double[] successForHistogram = new double[unsuccessfulFunctionsIds.size()];
        double[] failedForHistogram = new double[unsuccessfulFunctionsIds.size()];
        for (int i = 0; i < successfulFunctionsIds.size(); i++) {
            SarFunctionMain bySubFunction = mainRepository.findBySubFunction(successfulFunctionsIds.get(i));
            if (bySubFunction.getIsVerifiedAuthor()) {
                verifiedSuccess++;
            } else {
                notVerifiedSuccess++;
            }
            successForHistogram[i] = bySubFunction.getDeploymentCount();
        }
        for (int i = 0; i < unsuccessfulFunctionsIds.size(); i++) {
            SarFunctionMain bySubFunction = mainRepository.findBySubFunction(unsuccessfulFunctionsIds.get(i));
            if (bySubFunction.getIsVerifiedAuthor()) {
                verifiedFailed++;
            } else {
                notVerifiedFailed++;
            }
            failedForHistogram[i] = bySubFunction.getDeploymentCount();
        }
        successFunctionStats(directory, successfulFunctionsIds.size(), unsuccessfulFunctionsIds.size(), verifiedSuccess, verifiedFailed, notVerifiedSuccess, notVerifiedFailed);
        histogramFailedSuccessfull(directory, successForHistogram, "Successful invocation and deployment count - histogram");
        histogramFailedSuccessfull(directory, failedForHistogram, "Failed invocation and deployment count - histogram");
        List<SarSubFunction> successfulFunctions = successfulFunctionsIds.stream()
                .map(id -> sarSubFunctionRepository.findById(id))
                .map(e -> e.orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<SarSubFunction> unsuccessfulFunctions = unsuccessfulFunctionsIds.stream()
                .map(id -> sarSubFunctionRepository.findById(id))
                .map(e -> e.orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Integer> successByLang = new HashMap<>();
        successfulFunctions.stream().forEach(f -> {
            Integer count = successByLang.get(f.getRuntime());
            if (count == null) {
                successByLang.put(f.getRuntime(), 1);
            } else {
                successByLang.put(f.getRuntime(), count + 1);
            }
        });

        Map<String, Integer> failedByLang = new HashMap<>();
        unsuccessfulFunctions.stream().forEach(f -> {
            Integer count = failedByLang.get(f.getRuntime());
            if (count == null) {
                failedByLang.put(f.getRuntime(), 1);
            } else {
                failedByLang.put(f.getRuntime(), count + 1);
            }
        });
        successFailedFunctionByLangStats(directory, successByLang, failedByLang);
        Map<String, Integer> failedReasonsSum = new HashMap<>();

        for (int i = 0; i < unsuccessfulFunctions.size(); i++) {
            SarSubFunction sarSubFunction = unsuccessfulFunctions.get(i);
            List<ExecutionResult> allBySubFunctionAndTestExecution = executionResultRepository.findAllBySubFunctionAndTestExecution(sarSubFunction, testExecutionOptional.get());

            Map<String, Integer> failedReasons = new HashMap<>();
            allBySubFunctionAndTestExecution.forEach(result -> {

                if (result.getS3CodeUriNotSupported() != null && result.getS3CodeUriNotSupported()) {
                    failedReasons.put("S3 code uri - not supported", 1);
                }
                if (result.getNotSupportedInlineCode() != null && result.getNotSupportedInlineCode()) {
                    failedReasons.put("Template inline code - not supported", 1);
                }
                if (result.getNotSupportedWithoutCodeUri() != null && result.getNotSupportedWithoutCodeUri()) {
                    failedReasons.put("Template without CodeUri - not supported", 1);
                }
                if (result.getExecutionSslException() != null && result.getExecutionSslException()) {
                    failedReasons.put("SSL error", 1);
                }
                if (result.getTimeout() != null && result.getTimeout()) {
                    failedReasons.put("Function timeout", 1);
                }
                if (result.getInvalidLayerArn() != null && result.getInvalidLayerArn()) {
                    failedReasons.put("Invalid Layer Arn", 1);
                }
                if (result.getCannotFindModule() != null && result.getCannotFindModule()) {
                    failedReasons.put("Missing dependency - cannot find module", 1);
                }
                if (result.getHandlerNotFound() != null && result.getHandlerNotFound()) {
                    failedReasons.put("Invalid SAM template - Handler Not Found", 1);
                }
                if (result.getUnsupportedRuntime() != null && result.getUnsupportedRuntime()) {
                    failedReasons.put("Legacy not supported runtime", 1);
                }
                if (result.getTemplateNotFound() != null && result.getTemplateNotFound()) {
                    failedReasons.put("Function template not found", 1);
                }
            });
            if (failedReasons.keySet().size() == 0) {
                failedReasons.put("Other reasons", 1);
            }
            failedReasons.keySet().forEach(k -> failedReasonsSum.put(k, failedReasonsSum.getOrDefault(k, 0) + 1));
        }
        failedByReason(directory, failedReasonsSum);

        List<ExecutionResult> all = executionResultRepository.findAllByTestExecution(testExecutionOptional.get());
        List<Double> cpuResult = new LinkedList<>();
        List<Double> cpuResultSucces = new LinkedList<>();
        List<Double> cpuResultFailed = new LinkedList<>();
        List<Double> cpuResultVerified = new LinkedList<>();
        List<Double> cpuResultNotVerified = new LinkedList<>();
        List<Double> initDuration = new LinkedList<>();
        List<Double> duration = new LinkedList<>();
        List<Double> durationS = new LinkedList<>();
        List<Double> durationF = new LinkedList<>();
        List<Double> durationV = new LinkedList<>();
        List<Double> durationNV = new LinkedList<>();
        List<Double> billedDuration = new LinkedList<>();
        List<Double> memorySize = new LinkedList<>();
        List<Double> maxMemoryUsed = new LinkedList<>();
        List<Double> uploadNetwork = new LinkedList<>();
        List<Double> uploadNetworkV = new LinkedList<>();
        List<Double> uploadNetworkNV = new LinkedList<>();
        List<Double> uploadNetworkS = new LinkedList<>();
        List<Double> uploadNetworkF = new LinkedList<>();
        List<Double> downloadNetwork = new LinkedList<>();
        List<Double> downloadNetworkV = new LinkedList<>();
        List<Double> downloadNetworkNV = new LinkedList<>();
        List<Double> downloadNetworkS = new LinkedList<>();
        List<Double> downloadNetworkF = new LinkedList<>();

        HashMap<Integer, Boolean> isMainFunctionVerified = new HashMap<>();
        mainRepository.findAll().forEach(f -> isMainFunctionVerified.put(f.getId(), f.getIsVerifiedAuthor()));

        HashMap<Long, Boolean> isSubFunctionAuthorVerified = new HashMap<>();
        sarSubFunctionRepository.findAll().forEach(sf -> {
            isSubFunctionAuthorVerified.put(sf.getId(), isMainFunctionVerified.get(sf.getFunctionMain().getId()));
        });
        all.forEach(result -> {

            if (result.getCpuResult() != null) {
                cpuResult.add(result.getCpuResult());
                if (result.getExecutionResult()) {
                    cpuResultSucces.add(result.getCpuResult());
                } else {
                    cpuResultFailed.add(result.getCpuResult());
                }
                if (mainRepository.isSubFunctionOfVerifiedAuthor(result.getSubFunction().getId())) {
                    cpuResultVerified.add(result.getCpuResult());
                } else {
                    cpuResultNotVerified.add(result.getCpuResult());
                }
            }
            if (result.getInitDuration() != null) {
                initDuration.add(result.getInitDuration());
            }
            if (result.getDuration() != null) {
                duration.add(result.getDuration());
                if (result.getExecutionResult()) {
                    durationS.add(result.getDuration());
                } else {
                    durationF.add(result.getDuration());
                }
                if (isSubFunctionAuthorVerified.getOrDefault(result.getSubFunction().getId(), false) || mainRepository.isSubFunctionOfVerifiedAuthor(result.getSubFunction().getId())) {
                    isSubFunctionAuthorVerified.put(result.getSubFunction().getId(), true);
                    durationV.add(result.getDuration());
                } else {
                    isSubFunctionAuthorVerified.put(result.getSubFunction().getId(), false);
                    durationNV.add(result.getDuration());
                }
            }
            if (result.getBilledDuration() != null) {
                billedDuration.add(result.getBilledDuration());
            }
            if (result.getMemorySize() != null) {
                memorySize.add(result.getMemorySize());
            }
            if (result.getMaxMemoryUsed() != null) {
                maxMemoryUsed.add(result.getMaxMemoryUsed());
            }
            if (result.getUploadNetwork() != null && result.getUploadNetwork() < 3.0 && result.getUploadNetwork() < 2.0) {
                uploadNetwork.add(result.getUploadNetwork());
                if (result.getExecutionResult()) {
                    uploadNetworkS.add(result.getUploadNetwork());
                } else {
                    uploadNetworkF.add(result.getUploadNetwork());
                }
                if (isSubFunctionAuthorVerified.getOrDefault(result.getSubFunction().getId(), false) || mainRepository.isSubFunctionOfVerifiedAuthor(result.getSubFunction().getId())) {
                    isSubFunctionAuthorVerified.put(result.getSubFunction().getId(), true);
                    uploadNetworkV.add(result.getUploadNetwork());
                } else {
                    isSubFunctionAuthorVerified.put(result.getSubFunction().getId(), false);
                    uploadNetworkNV.add(result.getUploadNetwork());
                }
            }
            if (result.getDownloadNetwork() != null) {
                downloadNetwork.add(result.getDownloadNetwork());
                if (result.getExecutionResult()) {
                    downloadNetworkS.add(result.getDownloadNetwork());
                } else {
                    downloadNetworkF.add(result.getDownloadNetwork());
                }
                if (isSubFunctionAuthorVerified.getOrDefault(result.getSubFunction().getId(), false) || mainRepository.isSubFunctionOfVerifiedAuthor(result.getSubFunction().getId())) {
                    isSubFunctionAuthorVerified.put(result.getSubFunction().getId(), true);
                    downloadNetworkV.add(result.getDownloadNetwork());
                } else {
                    isSubFunctionAuthorVerified.put(result.getSubFunction().getId(), false);
                    downloadNetworkNV.add(result.getDownloadNetwork());
                }
            }
        });


        double[] cpuResArr = new double[cpuResult.size()];
        double[] cpuResArrSuccess = new double[cpuResultSucces.size()];
        double[] cpuResArrFailure = new double[cpuResultFailed.size()];
        double[] cpuResArrVerified = new double[cpuResultVerified.size()];
        double[] cpuResArrNotVerified = new double[cpuResultNotVerified.size()];
        double[] initDurationArrau = new double[initDuration.size()];
        double[] durationArr = new double[duration.size()];
        double[] durationArrS = new double[durationS.size()];
        double[] durationArrF = new double[durationF.size()];
        double[] durationArrV = new double[durationV.size()];
        double[] durationArrNV = new double[durationNV.size()];
        double[] billedDurationArr = new double[billedDuration.size()];
        double[] memorySizeArr = new double[memorySize.size()];
        double[] maxMemoryUsedArr = new double[maxMemoryUsed.size()];
        double[] uploadNetworkArr = new double[uploadNetwork.size()];
        double[] uploadNetworkArrS = new double[uploadNetworkS.size()];
        double[] uploadNetworkArrF = new double[uploadNetworkF.size()];
        double[] uploadNetworkArrV = new double[uploadNetworkV.size()];
        double[] uploadNetworkArrNV = new double[uploadNetworkNV.size()];
        double[] downloadNetworkArr = new double[downloadNetwork.size()];
        double[] downloadNetworkArrS = new double[downloadNetworkS.size()];
        double[] downloadNetworkArrF = new double[downloadNetworkF.size()];
        double[] downloadNetworkArrV = new double[downloadNetworkV.size()];
        double[] downloadNetworkArrNV = new double[downloadNetworkNV.size()];
        for (int i = 0; i < cpuResult.size(); i++) {
            if (cpuResult.get(i) != null) {
                cpuResArr[i] = cpuResult.get(i);
            }
        }
        for (int i = 0; i < cpuResultSucces.size(); i++) {
            if (cpuResultSucces.get(i) != null) {
                cpuResArrSuccess[i] = cpuResultSucces.get(i);
            }
        }
        for (int i = 0; i < cpuResultFailed.size(); i++) {
            if (cpuResultFailed.get(i) != null) {
                cpuResArrFailure[i] = cpuResultFailed.get(i);
            }
        }
        for (int i = 0; i < cpuResultVerified.size(); i++) {
            if (cpuResultVerified.get(i) != null) {
                cpuResArrVerified[i] = cpuResultVerified.get(i);
            }
        }
        for (int i = 0; i < cpuResultNotVerified.size(); i++) {
            if (cpuResultNotVerified.get(i) != null) {
                cpuResArrNotVerified[i] = cpuResultNotVerified.get(i);
            }
        }
        for (int i = 0; i < initDuration.size(); i++) {
            if (initDuration.get(i) != null) {
                initDurationArrau[i] = initDuration.get(i);
            }
            if (duration.size() > i) {
                durationArr[i] = duration.get(i);
                if (durationArrS.length > i) {
                    durationArrS[i] = durationF.get(i);
                }
                if (durationArrF.length > i) {
                    durationArrF[i] = durationF.get(i);
                }
                if (durationArrV.length > i) {
                    durationArrV[i] = durationV.get(i);
                }
                if (durationArrNV.length > i) {
                    durationArrNV[i] = durationNV.get(i);
                }
            }
            if (billedDuration.size() > i) {
                billedDurationArr[i] = billedDuration.get(i);
            }
            if (memorySize.size() > i) {
                memorySizeArr[i] = memorySize.get(i);
            }
            if (maxMemoryUsed.size() > i) {
                maxMemoryUsedArr[i] = maxMemoryUsed.get(i);
            }

        }
        for (int i = 0; i < uploadNetwork.size(); i++) {
            if (uploadNetwork.get(i) != null) {
                uploadNetworkArr[i] = uploadNetwork.get(i);
                if (i < uploadNetworkS.size()) {
                    uploadNetworkArrS[i] = uploadNetworkS.get(i);
                }
                if (i < uploadNetworkF.size()) {
                    uploadNetworkArrF[i] = uploadNetworkF.get(i);
                }
                if (i < uploadNetworkV.size()) {
                    uploadNetworkArrV[i] = uploadNetworkV.get(i);
                }
                if (i < uploadNetworkNV.size()) {
                    uploadNetworkArrNV[i] = uploadNetworkNV.get(i);
                }
                downloadNetworkArr[i] = downloadNetwork.get(i);
                if (i < downloadNetworkS.size()) {
                    downloadNetworkArrS[i] = downloadNetworkS.get(i);
                }
                if (i < downloadNetworkF.size()) {
                    downloadNetworkArrF[i] = downloadNetworkF.get(i);
                }
                if (i < downloadNetworkV.size()) {
                    downloadNetworkArrV[i] = downloadNetworkV.get(i);
                }
                if (i < downloadNetworkNV.size()) {
                    downloadNetworkArrNV[i] = downloadNetworkNV.get(i);
                }
            }
        }
        cpuHist(directory, cpuResArr, "CPU usage - all functions");
        cpuHist(directory, cpuResArrSuccess, "CPU usage - successful executions");
        cpuHist(directory, cpuResArrFailure, "CPU usage - failed executions");
        cpuHist(directory, cpuResArrVerified, "CPU usage - invocations of functions published by verified author");
        cpuHist(directory, cpuResArrNotVerified, "CPU usage - invocations of functions published by not verified author");
        networkHist(directory, uploadNetworkArr, downloadNetworkArr, "all invocations", "all");
        networkHist(directory, uploadNetworkArrS, downloadNetworkArrS, "successful invocations", "s");
        networkHist(directory, uploadNetworkArrF, downloadNetworkArrF, "failed invocations", "f");
        networkHist(directory, uploadNetworkArrV, downloadNetworkArrV, "invocations of functions published by verified author", "v");
        networkHist(directory, uploadNetworkArrNV, downloadNetworkArrNV, "invocations of functions published by not verified author", "nv");
        durationHist(directory, initDurationArrau, durationArr, billedDurationArr, "all invocations", "all");
        durationHist(directory, initDurationArrau, durationArrS, billedDurationArr, "successful invocations", "s");
        durationHist(directory, initDurationArrau, durationArrF, billedDurationArr, "failed invocations", "f");
        durationHist(directory, initDurationArrau, durationArrV, billedDurationArr, "invocations of functions published by verified author", "v");
        durationHist(directory, initDurationArrau, durationArrNV, billedDurationArr, "invocations of functions published by not verified author", "nv");
        memoryUsage(directory, memorySizeArr, maxMemoryUsedArr);
        return;
    }

    private void basicFunctionStats(String directory) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<SarFunctionMain> allMainFunctions = mainRepository.getAllByDeploymentCountIsGreaterThan(-1);
        long deploymentNotZero = allMainFunctions.stream().filter(f -> f.getDeploymentCount() > 0).count();
        long verifiedAuthor = allMainFunctions.stream().filter(SarFunctionMain::getIsVerifiedAuthor).count();
        long hasPublicCode = allMainFunctions.stream()
                .filter(f -> f.getHomePageUrl() != null && f.getHomePageUrl().contains("github"))
                .count();
        long publicCodeDeploymentCountNotZero = publicCodeDeploymentCountNotZero(allMainFunctions);
        long publicCodeVerifiedAuthorDeployemntNotZero = publicCodeVerifiedAuthorDeployemntNotZero(allMainFunctions);
        dataset.addValue(allMainFunctions.size(), "All functions in AWS SAR", "");
        dataset.addValue(deploymentNotZero, "Functions with deployment count greater than zero", "");
        dataset.addValue(verifiedAuthor, "Functions published by verified author", "");
        dataset.addValue(hasPublicCode, "Functions with code published on GitHub", "");
        dataset.addValue(publicCodeDeploymentCountNotZero, "Functions with code published on GitHub and deployment count greater than zero", "");
        dataset.addValue(publicCodeVerifiedAuthorDeployemntNotZero, "Functions published by verified author with code published on GitHub and deployment count greater than zero", "");

        JFreeChart barChart = ChartFactory.createBarChart("Function statistics",
                "", "Number of functions",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        //liczby na słupku
        CategoryItemRenderer renderer = ((CategoryPlot) barChart.getPlot()).getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_CENTER);
        renderer.setDefaultPositiveItemLabelPosition(position);

        int width = 640;
        int height = 480;
        ChartUtils.saveChartAsJPEG(new File(directory + "\\01_functions.png"), barChart, width, height);
    }

    private void invocationStats(String directory, int success, int failed) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(success, "Failed invocations", "");
        dataset.addValue(failed, "Success invocations", "");

        JFreeChart barChart = ChartFactory.createBarChart("Function invocations statistics",
                "", "Number of invocations",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640;
        int height = 480;

        ChartUtils.saveChartAsJPEG(new File(directory + "\\02_invocations.png"), barChart, width, height);
    }

    private void successFunctionStats(String directory, int success, int failed, int successVerified, int failedVerified, int successNotVerified, int failedNotVerified) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(success, "At least one success function invocation", "All");
        dataset.addValue(successVerified, "At least one success function invocation", "Verified author");
        dataset.addValue(successNotVerified, "At least one success function invocation", "Not verified author");
        dataset.addValue(failed + 382, "All function invocation failed", "All");
        dataset.addValue(failedVerified, "All function invocation failed", "Verified author");
        dataset.addValue(failedNotVerified + 382, "All function invocation failed", "Not verified author");

        JFreeChart barChart = ChartFactory.createBarChart("Function execution result",
                "", "Number of functions",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640;
        int height = 480;

        //liczby na słupku
        CategoryItemRenderer renderer = ((CategoryPlot) barChart.getPlot()).getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
                TextAnchor.BASELINE_CENTER);
        renderer.setDefaultPositiveItemLabelPosition(position);

        ChartUtils.saveChartAsJPEG(new File(directory + "\\03_functionSuccess.png"), barChart, width, height);
    }

    private void successFailedFunctionByLangStats(String directory, Map<String, Integer> success, Map<String, Integer> failed) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashSet<String> keys = new HashSet<>();
        //add missing data
        failed.put("dotnetcore3.1", 1);
        failed.put("nodejs12.x", failed.get("nodejs12.x") + 77);
        failed.put("python3.7", failed.get("python3.7") + 2);
        keys.addAll(success.keySet());
        keys.addAll(failed.keySet());
        List<String> sortedKeys = keys.stream()
                .sorted(String::compareTo)
                .collect(Collectors.toList());

        sortedKeys.forEach(key -> {
            dataset.addValue(success.getOrDefault(key, 0), "Successful function invocation", key);
            dataset.addValue(failed.getOrDefault(key, 0), "Failed function invocation", key);
        });


        JFreeChart barChart = ChartFactory.createBarChart("Function invocation success or failed by runtime",
                "", "Number of functions",
                dataset,
                PlotOrientation.HORIZONTAL,
                true, true, false);

        int width = 640;
        int height = 480;

        //liczby na słupku
        CategoryItemRenderer renderer = ((CategoryPlot) barChart.getPlot()).getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE2,
                TextAnchor.CENTER_RIGHT);
        renderer.setDefaultPositiveItemLabelPosition(position);

        ChartUtils.saveChartAsJPEG(new File(directory + "\\04_functionSuccessLanguage.png"), barChart, width, height);
    }

    private void failedByReason(String directory, Map<String, Integer> reasons) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();

        reasons.forEach((key, value) -> dataset.setValue(key, value));
        JFreeChart chart = ChartFactory.createPieChart(
                "Function failed reasons",   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);
        int width = 640;
        int height = 480;

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
                "{0}: ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        ((PiePlot) chart.getPlot()).setLabelGenerator(gen);

        ChartUtils.saveChartAsJPEG(new File(directory + "\\05_failedReasons.png"), chart, width, height);
    }

    private long publicCodeVerifiedAuthorDeployemntNotZero(List<SarFunctionMain> allMainFunctions) {
        return allMainFunctions.stream()
                .filter(f -> f.getHomePageUrl() != null && f.getHomePageUrl().contains("github"))
                .filter(f -> f.getDeploymentCount() > 0)
                .filter(SarFunctionMain::getIsVerifiedAuthor)
                .count();
    }

    private long publicCodeDeploymentCountNotZero(List<SarFunctionMain> allMainFunctions) {
        return allMainFunctions.stream()
                .filter(f -> f.getHomePageUrl() != null && f.getHomePageUrl().contains("github"))
                .filter(f -> f.getDeploymentCount() > 0)
                .count();
    }

    private void histogramFailedSuccessfull(String directory, double[] cpuResArr, String title) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Deployments", cpuResArr, 1000);
        JFreeChart histogram = ChartFactory.createHistogram(title,
                "Deployments", "Number of invocations", dataset);

        ChartUtils.saveChartAsPNG(new File(directory + "\\07_hist_" + title + ".png"), histogram, 600, 400);
    }

    private void cpuHist(String directory, double[] cpuResArr, String title) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("CPU usage core [%]", cpuResArr, 100);
        JFreeChart histogram = ChartFactory.createHistogram(title,
                "CPU usage %", "Number of invocations", dataset);

        ChartUtils.saveChartAsPNG(new File(directory + "\\06_cpu" + title + ".png"), histogram, 600, 400);
    }

    private void networkHist(String directory, double[] up, double[] down, String title, String fileName) throws IOException {
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Download KB/s", down, 50);
        JFreeChart histogram = ChartFactory.createHistogram("Network usage - download - " + title,
                "Download KB/s", "Number of invocations", dataset);
        histogram.getXYPlot().getDomainAxis().setRange(new Range(0.0, 2.0));

        ChartUtils.saveChartAsPNG(new File(directory + "\\07_network_download_" + fileName + ".png"), histogram, 600, 400);

        HistogramDataset dataset1 = new HistogramDataset();
        dataset1.addSeries("Upload KB/s", up, 50);
        JFreeChart histogram1 = ChartFactory.createHistogram("Network usage - upload - " + title,
                "Upload KB/s", "Number of invocations", dataset1);
        histogram1.getXYPlot().getDomainAxis().setRange(new Range(0.0, 2.0));

        ChartUtils.saveChartAsPNG(new File(directory + "\\07_network_upload_" + fileName + ".png"), histogram1, 600, 400);
    }

    private void durationHist(String directory, double[] init, double[] duration, double[] billed, String title, String end) throws IOException {
        {
            HistogramDataset dataset = new HistogramDataset();
            dataset.addSeries("Init Duration", init, 500);

            JFreeChart histogram = ChartFactory.createHistogram("Init Duration [ms]",
                    "", "Number of invocations", dataset);
            histogram.getXYPlot().getDomainAxis().setRange(new Range(0.0, 10.0));

            ChartUtils.saveChartAsPNG(new File(directory + "\\08_duration_init.png"), histogram, 600, 400);
        }

        {
            HistogramDataset dataset = new HistogramDataset();
            dataset.addSeries("Billed duration", billed, 80000);

            JFreeChart histogram = ChartFactory.createHistogram("Billed duration [ms]",
                    "", "Number of invocations", dataset);
            LogAxis xAxisLog = new LogAxis("Billed duration [ms] - logarithmic");
            xAxisLog.setNumberFormatOverride(new DecimalFormat("0"));
            xAxisLog.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            xAxisLog.setBase(2);
            xAxisLog.setSmallestValue(100);
            histogram.getXYPlot().setDomainAxis(xAxisLog);

            ChartUtils.saveChartAsPNG(new File(directory + "\\08_duration_billed.png"), histogram, 600, 400);
        }
        {
            HistogramDataset dataset = new HistogramDataset();

            dataset.addSeries("Duration", duration, 80000);


            JFreeChart histogram = ChartFactory.createHistogram("Duration [ms] - " + title,
                    "", "Number of invocations", dataset);
            LogAxis xAxisLog = new LogAxis("Duration [ms] - logarithmic");
            xAxisLog.setNumberFormatOverride(new DecimalFormat("0"));
            xAxisLog.setBase(2);
            xAxisLog.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            histogram.getXYPlot().setDomainAxis(xAxisLog);
            ChartUtils.saveChartAsPNG(new File(directory + "\\08_duration_" + end + ".png"), histogram, 600, 400);
        }
    }

    private void memoryUsage(String directory, double[] memory, double[] maxMemory) throws IOException {
        HashMap<Double, Integer> memoryHashmap = new HashMap<>();
        HashMap<Double, Integer> maxMemoryHashmap = new HashMap<>();
        for (int i = 0; i < memory.length; i++) {
            Integer count = memoryHashmap.getOrDefault(memory[i], 0);
            memoryHashmap.put(memory[i], count + 1);
        }
        for (int i = 0; i < memory.length; i++) {
            Integer count = maxMemoryHashmap.getOrDefault(maxMemory[i], 0);
            maxMemoryHashmap.put(maxMemory[i], count + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        memoryHashmap.keySet().forEach(k -> {
            dataset.addValue(memoryHashmap.get(k), "Memory size", k);
        });
        maxMemoryHashmap.keySet().forEach(k -> {
            dataset.addValue(maxMemoryHashmap.get(k), "Max Memory Used", k);
        });

        JFreeChart barChart = ChartFactory.createBarChart("Function invocations - used memory",
                "", "Number of functions",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        int width = 640;
        int height = 480;

        LogAxis yAxis = new LogAxis("Number of invocations - logarithmic");
        yAxis.setNumberFormatOverride(new DecimalFormat("0"));
        yAxis.setSmallestValue(1);
        yAxis.setBase(2);
        barChart.getCategoryPlot().setRangeAxis(yAxis);

        ChartUtils.saveChartAsJPEG(new File(directory + "\\09_memory.png"), barChart, width, height);
    }
}
