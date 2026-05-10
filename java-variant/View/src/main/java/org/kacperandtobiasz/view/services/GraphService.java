package org.kacperandtobiasz.view.services;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import org.kacperandtobiasz.model.base.signal.DiscreteSignal;
import org.kacperandtobiasz.model.base.signal.Signal;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Logger;

public class GraphService {
    private final List<BarChart> resultBarCharts = new ArrayList<>();
    private final List<ScatterChart<Number, Number>> resultScatterCharts = new ArrayList<>();
    private final List<ScatterChart<Number, Number>> quantizationCharts = new ArrayList<>();
    private final List<ScatterChart<Number, Number>> reconstructionCharts = new ArrayList<>();
    private int histogramBinCount = 10;
    private final List<GraphDrawListener> graphDrawListeners = new ArrayList<>();

    private Signal resultSignal;

    public static final class GraphDrawEvent extends EventObject {
        private final Signal signal;

        public GraphDrawEvent(Object source, Signal signal) {
            super(source);
            this.signal = signal;
        }

        public Signal getSignal() {
            return signal;
        }
    }

    @FunctionalInterface
    public interface GraphDrawListener extends EventListener {
        void signalDrawn(GraphDrawEvent event);
    }

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

        notifySignalDrawn(signal);
    }

    public void addGraphDrawListener(GraphDrawListener listener) {
        if (listener != null) {
            graphDrawListeners.add(listener);
        }
    }

    public void removeGraphDrawListener(GraphDrawListener listener) {
        graphDrawListeners.remove(listener);
    }

    private void notifySignalDrawn(Signal signal) {
        GraphDrawEvent event = new GraphDrawEvent(this, signal);
        for (GraphDrawListener listener : graphDrawListeners) {
            listener.signalDrawn(event);
        }
    }

    private boolean isSignalDrawable(Signal signal){
        if (signal == null){
            Logger.getGlobal().warning("Attempted to draw null signal, aborting.");
            return false;
        }
        if (!signal.isSampled()) {
            Logger.getGlobal().warning("Attempted to draw unsampled signal, aborting.");
            return false;
        }
        return true;
    }

    public void drawScatterChart(Signal signal, ScatterChart<Number, Number> scatterChart){
        scatterChart.getData().clear();
        if(isSignalDrawable(signal))
            addDataToScatterChart(signal.getDiscreteSignal(), scatterChart);
    }

    public void addDataToScatterChart(DiscreteSignal ds, ScatterChart<Number, Number> scatterChart) {
        XYChart.Series<Number, Number> scatterSeries = new XYChart.Series<>();

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
        barChart.getData().clear();
        if(isSignalDrawable(signal))
            addDataToBarChart(signal.getDiscreteSignal(), barChart);
    }

    public void addDataToBarChart(DiscreteSignal discreteSignal, BarChart barChart){
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

    public void addQuantizationChart(ScatterChart<Number, Number> chart) {
        if (chart == null) throw new NullPointerException("chart is null");
        if (!quantizationCharts.contains(chart)) {
            quantizationCharts.add(chart);
        }
    }

    public void addReconstructionChart(ScatterChart<Number, Number> chart) {
        if (chart == null) throw new NullPointerException("chart is null");
        if (!reconstructionCharts.contains(chart)) {
            reconstructionCharts.add(chart);
        }
    }

    public void drawQuantizationCharts(DiscreteSignal original, DiscreteSignal quantized) {
        for (ScatterChart<Number, Number> chart : quantizationCharts) {
            chart.getData().clear();
            if (original != null) {
                addNamedSeriesToScatterChart(original, "Sygnał oryginalny", chart, 0);
            }
            if (quantized != null) {
                addNamedSeriesToScatterChart(quantized, "Sygnał skwantyzowany", chart, 0);
            }
        }
    }

    public void drawReconstructionCharts(DiscreteSignal quantized, DiscreteSignal reconstructed) {
        for (ScatterChart<Number, Number> chart : reconstructionCharts) {
            chart.getData().clear();
            if (quantized != null) {
                addNamedSeriesToScatterChart(quantized, "Sygnał skwantyzowany", chart, 0);
            }
            if (reconstructed != null) {
                addNamedSeriesToScatterChart(reconstructed, "Sygnał zrekonstruowany", chart, 2000);
            }
        }
    }

    private void addNamedSeriesToScatterChart(DiscreteSignal ds, String name,
                                              ScatterChart<Number, Number> chart, int maxPoints) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        chart.getData().add(series);

        int sampleCount = ds.getSampleCount();
        int step = 1;
        if (maxPoints > 0 && sampleCount > maxPoints) {
            step = sampleCount / maxPoints;
        }

        for (int i = 0; i < sampleCount; i += step) {
            series.getData().add(
                    new XYChart.Data<>(ds.getTimeAtIndex(i), ds.getSample(i))
            );
        }
    }
}

