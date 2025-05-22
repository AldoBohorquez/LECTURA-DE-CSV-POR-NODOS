package lasalle.oaxaca.edu.mx.procesadorcsvnodos.nodos;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Nodo para crear visualizaciones gráficas de los datos
 */
public class VisualizationNode extends ProcessingNode {
    private Map<String, Integer> frequencies;
    private Map<Integer, List<Double>> numericValues;
    private String chartTitle;
    private String chartType; // "pie", "bar", "histogram", "scatter"
    
    public VisualizationNode(String[] headers, int[] columns, String chartType, String chartTitle) {
        super(headers, columns);
        this.frequencies = new HashMap<>();
        this.numericValues = new HashMap<>();
        this.chartType = chartType;
        this.chartTitle = chartTitle;
        
        // Inicializar listas para valores numéricos
        for (int i = 0; i < columns.length; i++) {
            numericValues.put(i, new ArrayList<>());
        }
    }
    
    @Override
    public void process(List<String[]> records) {
        for (String[] record : records) {
            String[] extractedValues = extractColumns(record);
            
            // Para gráficas categóricas (pie, bar)
            if (chartType.equals("pie") || chartType.equals("bar")) {
                String key = generateKey(extractedValues);
                frequencies.put(key, frequencies.getOrDefault(key, 0) + 1);
            }
            
            // Para gráficas numéricas (histogram, scatter)
            if (chartType.equals("histogram") || chartType.equals("scatter")) {
                for (int i = 0; i < extractedValues.length; i++) {
                    try {
                        double value = Double.parseDouble(extractedValues[i]);
                        numericValues.get(i).add(value);
                    } catch (NumberFormatException e) {
                        // Ignorar valores no numéricos
                    }
                }
            }
        }
    }
    
    @Override
    public void showResults() {
        System.out.println("\n----- Generando Visualización: " + chartTitle + " -----");
        
        JFreeChart chart = createChart();
        if (chart != null) {
            displayChart(chart);
            saveChart(chart);
        }
    }
    
    private JFreeChart createChart() {
        switch (chartType) {
            case "pie":
                return createPieChart();
            case "bar":
                return createBarChart();
            case "histogram":
                return createHistogram();
            case "scatter":
                return createScatterPlot();
            default:
                System.out.println("Tipo de gráfica no reconocido: " + chartType);
                return null;
        }
    }
    
    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Limitar a los top 10 para mejor visualización
        frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> {
                dataset.setValue(entry.getKey(), entry.getValue());
            });
        
        return ChartFactory.createPieChart(
            chartTitle,
            dataset,
            true, true, false
        );
    }
    
    private JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(15) // Top 15 para barras
            .forEach(entry -> {
                dataset.addValue(entry.getValue(), "Frecuencia", entry.getKey());
            });
        
        return ChartFactory.createBarChart(
            chartTitle,
            "Categorías",
            "Frecuencia",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
    }
    
    private JFreeChart createHistogram() {
        if (numericValues.get(0).isEmpty()) {
            System.out.println("No hay datos numéricos para crear histograma");
            return null;
        }
        
        List<Double> values = numericValues.get(0);
        String columnName = headers[columns[0]];
        
        // Crear bins para el histograma
        double min = Collections.min(values);
        double max = Collections.max(values);
        int numBins = Math.min(20, (int)Math.sqrt(values.size())); // Regla empírica
        double binWidth = (max - min) / numBins;
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Crear contadores para cada bin
        Map<String, Integer> bins = new HashMap<>();
        for (double value : values) {
            int binIndex = (int)((value - min) / binWidth);
            if (binIndex >= numBins) binIndex = numBins - 1; // Para el valor máximo
            
            double binStart = min + binIndex * binWidth;
            double binEnd = binStart + binWidth;
            String binLabel = String.format("%.1f-%.1f", binStart, binEnd);
            
            bins.put(binLabel, bins.getOrDefault(binLabel, 0) + 1);
        }
        
        bins.forEach((binLabel, count) -> {
            dataset.addValue(count, columnName, binLabel);
        });
        
        return ChartFactory.createBarChart(
            chartTitle + " - Histograma de " + columnName,
            "Rangos de valores",
            "Frecuencia",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
    }
    
    private JFreeChart createScatterPlot() {
        if (columns.length < 2 || numericValues.get(0).isEmpty() || numericValues.get(1).isEmpty()) {
            System.out.println("Se necesitan al menos 2 columnas numéricas para scatter plot");
            return null;
        }
        
        XYSeries series = new XYSeries("Datos");
        List<Double> xValues = numericValues.get(0);
        List<Double> yValues = numericValues.get(1);
        
        int minSize = Math.min(xValues.size(), yValues.size());
        for (int i = 0; i < minSize; i++) {
            series.add(xValues.get(i), yValues.get(i));
        }
        
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        
        return ChartFactory.createScatterPlot(
            chartTitle,
            headers[columns[0]],
            headers[columns[1]],
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
        );
    }
    
    private void displayChart(JFreeChart chart) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(chartTitle);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 600));
            
            frame.add(chartPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("Gráfica mostrada en ventana: " + chartTitle);
        });
    }
    
    private void saveChart(JFreeChart chart) {
        try {
            String filename = chartTitle.replaceAll("[^a-zA-Z0-9]", "_") + ".png";
            File file = new File(filename);
            ChartUtils.saveChartAsPNG(file, chart, 800, 600);
            System.out.println("Gráfica guardada como: " + filename);
        } catch (IOException e) {
            System.out.println("Error al guardar la gráfica: " + e.getMessage());
        }
    }
}
