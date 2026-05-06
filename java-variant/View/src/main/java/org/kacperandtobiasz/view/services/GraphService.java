package org.kacperandtobiasz.view.services;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GraphService {
    private final List<BarChart> resultBarCharts = new ArrayList<>();
    private final List<ScatterChart<Number, Number>> resultScatterCharts = new ArrayList<>();
    private int histogramBinCount = 10;

    private Signal resultSignal;

    public void addResultBarChart(BarChart barChart) {
        if (barChart == null)
            throw new NullPointerException("barChart is null");
        if (!resultBarCharts.contains(barChart)) {
            resultBarCharts.add(barChart);
        }
        drawBarChart(resultSignal, barChart);
    }

    public void addResultScatterChart(ScatterChart<Number, Number> scatterChart) {
        if (scatterChart == null)
            throw new NullPointerException("scatterChart is null");
        if (!resultScatterCharts.contains(scatterChart)) {
            resultScatterCharts.add(scatterChart);
        }
        drawScatterChart(resultSignal, scatterChart);
    }

    public void setHistogramBinCount(int histogramBinCount) {
        if (histogramBinCount < 1)
            histogramBinCount = 1;
        this.histogramBinCount = histogramBinCount;

        drawResultSignalGraphs(resultSignal);
    }

    public int getHistogramBinCount() {
        return histogramBinCount;
    }

    public void drawResultSignalGraphs(Signal signal) {
        resultSignal = signal;
        for (ScatterChart<Number, Number> chart : resultScatterCharts) {
            drawScatterChart(resultSignal, chart);
        }

        for (BarChart chart : resultBarCharts) {
            drawBarChart(resultSignal, chart);
        }
    }

    public void drawScatterChart(Signal signal, ScatterChart<Number, Number> scatterChart){
        if (signal == null)
            throw new NullPointerException("Attempted to draw null signal, aborting.");
        if (!signal.isSampled()) {
            Logger.getGlobal().warning("Attempted to draw unsampled signal, aborting.");
            return;
        }
        drawScatterChart(signal.getDiscreteSignal(), scatterChart);
    }

    public void drawScatterChart(DiscreteSignal ds, ScatterChart<Number, Number> scatterChart) {
        XYChart.Series<Number, Number> scatterSeries = new XYChart.Series<>();

        scatterChart.getData().clear();
        scatterChart.getData().add(scatterSeries);

        for (int i = 0; i < ds.getSampleCount(); i++) {
            scatterSeries.getData().add(
                    new XYChart.Data<>(
                            ds.getTimeAtIndex(i),
                            ds.getSample(i)
                    )
            );
        }
    }

    private record MinMax(double min, double max) {}

    private MinMax findMinMax(DiscreteSignal ds) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < ds.getSampleCount(); i++) {
            double val = ds.getSample(i);
            if (val < min) min = val;
            if (val > max) max = val;
        }
        return new MinMax(min, max);
    }

    public void drawBarChart(Signal signal, BarChart barChart){
        if (signal == null)
            throw new NullPointerException("Attempted to draw null signal, aborting.");
        if (!signal.isSampled()) {
            Logger.getGlobal().warning("Attempted to draw unsampled signal, aborting.");
            return;
        }
        drawBarChart(signal.getDiscreteSignal(), barChart);
    }

    public void drawBarChart(DiscreteSignal discreteSignal, BarChart barChart){
        barChart.getData().clear();

        if (discreteSignal.getSampleCount() == 0) {
            return;
        }

        MinMax minMax = findMinMax(discreteSignal);

        int[] counts = calculateCountsPerBin(discreteSignal, minMax);
        String[] labels = generateBinLabels(minMax);

        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        for (int i = 0; i < histogramBinCount; i++) {
            barSeries.getData().add(new XYChart.Data<>(labels[i], counts[i]));
        }
        barChart.getData().add(barSeries);
    }

    private double calculateBinWidth(MinMax minMax) {
        double range = minMax.max() - minMax.min();
        if (range <= 0) {
            return 1.0;
        }
        return range / histogramBinCount;
    }

    private String[] generateBinLabels(MinMax minMax) {
        String[] labels = new String[histogramBinCount];

        double binWidth = calculateBinWidth(minMax);

        for (int i = 0; i < histogramBinCount; i++) {
            double binStart = minMax.min() + i * binWidth;
            double binEnd = minMax.min() + (i + 1) * binWidth;
            labels[i] = String.format("%.2f-%.2f", binStart, binEnd);
        }
        return labels;
    }

    private int[] calculateCountsPerBin(DiscreteSignal ds, MinMax minMax) {
        int[] counts = new int[histogramBinCount];
        for (int i = 0; i < ds.getSampleCount(); i++) {
            double value = ds.getSample(i);
            int binIndex = calculateBinIndex(value, minMax);

            counts[binIndex]++;
        }
        return counts;
    }

    private int calculateBinIndex(double value, MinMax minMax) {
        double binWidth = calculateBinWidth(minMax);
        if (binWidth <= 0) {
            return 0;
        }
        if (value <= minMax.min()) {
            return 0;
        }
        if (value >= minMax.max()) {
            return histogramBinCount - 1;
        }
        int binIndex = (int) ((value - minMax.min()) / binWidth);
        if (binIndex < 0) {
            return 0;
        }
        if (binIndex >= histogramBinCount) {
            return histogramBinCount - 1;
        }
        return binIndex;
    }
}
