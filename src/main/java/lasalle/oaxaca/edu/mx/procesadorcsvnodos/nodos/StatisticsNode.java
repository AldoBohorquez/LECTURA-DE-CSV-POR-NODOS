/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lasalle.oaxaca.edu.mx.procesadorcsvnodos.nodos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nodo para calcular estadísticas básicas
 */

class StatisticsNode extends ProcessingNode {
    private Map<Integer, List<Double>> numericValues;
    private Map<Integer, Double> averages;
    private Map<Integer, Double> medians;
    private Map<Integer, Double> mins;
    private Map<Integer, Double> maxs;
    
    public StatisticsNode(String[] headers, int[] columns) {
        super(headers, columns);
        this.numericValues = new HashMap<>();
        this.averages = new HashMap<>();
        this.medians = new HashMap<>();
        this.mins = new HashMap<>();
        this.maxs = new HashMap<>();
        
        // Inicializar las listas para cada columna
        for (int i = 0; i < columns.length; i++) {
            numericValues.put(i, new ArrayList<>());
        }
    }
    
    @Override
    public void process(List<String[]> records) {
        // Extraer y convertir valores numéricos
        for (String[] record : records) {
            String[] extractedValues = extractColumns(record);
            
            for (int i = 0; i < extractedValues.length; i++) {
                try {
                    double value = Double.parseDouble(extractedValues[i]);
                    numericValues.get(i).add(value);
                } catch (NumberFormatException e) {
                    // Ignorar valores no numéricos
                }
            }
        }
        
        // Calcular estadísticas
        for (int i = 0; i < columns.length; i++) {
            List<Double> values = numericValues.get(i);
            
            if (!values.isEmpty()) {
                // Calcular promedio
                double sum = values.stream().mapToDouble(Double::doubleValue).sum();
                averages.put(i, sum / values.size());
                
                // Calcular mediana
                List<Double> sortedValues = new ArrayList<>(values);
                Collections.sort(sortedValues);
                
                if (sortedValues.size() % 2 == 0) {
                    int mid = sortedValues.size() / 2;
                    medians.put(i, (sortedValues.get(mid - 1) + sortedValues.get(mid)) / 2);
                } else {
                    medians.put(i, sortedValues.get(sortedValues.size() / 2));
                }
                
                // Min y Max
                mins.put(i, sortedValues.get(0));
                maxs.put(i, sortedValues.get(sortedValues.size() - 1));
            }
        }
    }
    
    @Override
    public void showResults() {
        System.out.println("\n----- Resultados del Nodo de Estadísticas -----");
        
        for (int i = 0; i < columns.length; i++) {
            int columnIndex = columns[i];
            String columnName = headers[columnIndex];
            
            System.out.println("Estadísticas para la columna: " + columnName);
            
            if (numericValues.get(i).isEmpty()) {
                System.out.println("  No se encontraron valores numéricos.");
                continue;
            }
            
            System.out.println("  Cantidad de valores: " + numericValues.get(i).size());
            System.out.println("  Promedio: " + averages.get(i));
            System.out.println("  Mediana: " + medians.get(i));
            System.out.println("  Mínimo: " + mins.get(i));
            System.out.println("  Máximo: " + maxs.get(i));
        }
    }
}
