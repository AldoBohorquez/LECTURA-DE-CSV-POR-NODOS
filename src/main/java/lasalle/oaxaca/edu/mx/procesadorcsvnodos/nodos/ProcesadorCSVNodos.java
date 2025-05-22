package lasalle.oaxaca.edu.mx.procesadorcsvnodos.nodos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Sistema de procesamiento de CSV por nodos con visualizaciones
 * Cada nodo se encarga de procesar un conjunto específico de columnas
 */
public class ProcesadorCSVNodos {

    public static void main(String[] args) {
        String csvFile = "bankdataset.csv";  // Archivo por defecto
        
        if (args.length > 0) {
            csvFile = args[0];
        }
        
        try {
            System.out.println("Cargando archivo CSV: " + csvFile);
            
            // Leer el archivo CSV
            List<String[]> data = readCSV(csvFile);
            if (data.isEmpty()) {
                System.out.println("El archivo CSV está vacío o tiene problemas.");
                return;
            }

            // Obtener los nombres de las columnas (primera fila)
            String[] headers = data.get(0);
            System.out.println("Columnas encontradas: " + Arrays.toString(headers));
            
            // Eliminar la fila de encabezados para tener solo datos
            List<String[]> records = data.subList(1, data.size());
            System.out.println("Total de registros: " + records.size());
            
            // ===== PROCESAMIENTO TRADICIONAL =====
            
            // Nodo 1: Procesamiento de duplicados y frecuencias
            int[] columnsNode1 = {3, 3}; // Smoking_Status
            FrequencyNode frequencyNode = new FrequencyNode(headers, columnsNode1);
            frequencyNode.process(records);
            frequencyNode.showResults();
            
            // Nodo 2: Estadísticas básicas
            int[] columnsNode2 = {1, 4}; // Age, Years_Smoking
            try {
                StatisticsNode statsNode = new StatisticsNode(headers, columnsNode2);
                statsNode.process(records);
                statsNode.showResults();
            } catch (Exception e) {
                System.out.println("Error al procesar el nodo de estadísticas: " + e.getMessage());
            }
            
            // ===== VISUALIZACIONES =====
            
            System.out.println("\n===== GENERANDO VISUALIZACIONES =====");
            
            // Visualización 1: Gráfica de pastel para Smoking_Status
            int[] columnsViz1 = {3}; // Smoking_Status
            VisualizationNode pieChart = new VisualizationNode(
                headers, columnsViz1, "pie", "Distribución de Estado de Fumador"
            );
            pieChart.process(records);
            pieChart.showResults();
            
            // Visualización 2: Gráfica de barras para frecuencias
            VisualizationNode barChart = new VisualizationNode(
                headers, columnsViz1, "bar", "Frecuencias de Estado de Fumador"
            );
            barChart.process(records);
            barChart.showResults();
            
            // Visualización 3: Histograma de edades
            int[] columnsViz2 = {1}; // Age
            VisualizationNode histogram = new VisualizationNode(
                headers, columnsViz2, "histogram", "Distribución de Edades"
            );
            histogram.process(records);
            histogram.showResults();
            
            // Visualización 4: Scatter plot Age vs Years_Smoking
            int[] columnsViz3 = {1, 4}; // Age, Years_Smoking
            VisualizationNode scatterPlot = new VisualizationNode(
                headers, columnsViz3, "scatter", "Edad vs Años Fumando"
            );
            scatterPlot.process(records);
            scatterPlot.showResults();
            
            System.out.println("\n===== PROCESAMIENTO COMPLETADO =====");
            System.out.println("Se han generado las siguientes gráficas:");
            System.out.println("1. Gráfica de pastel: Distribución de Estado de Fumador");
            System.out.println("2. Gráfica de barras: Frecuencias de Estado de Fumador");
            System.out.println("3. Histograma: Distribución de Edades");
            System.out.println("4. Scatter plot: Edad vs Años Fumando");
            System.out.println("\nLas gráficas se han guardado como archivos PNG en el directorio actual.");
            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }
    }

    /**
     * Lee un archivo CSV y devuelve una lista de arrays de strings (filas)
     */
    private static List<String[]> readCSV(String filename) throws IOException {
        List<String[]> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Dividir por comas (se puede mejorar para manejar casos con comas dentro de comillas)
                String[] values = line.split(",");
                // Limpiar espacios en blanco
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                data.add(values);
            }
        }
        
        return data;
    }
}
