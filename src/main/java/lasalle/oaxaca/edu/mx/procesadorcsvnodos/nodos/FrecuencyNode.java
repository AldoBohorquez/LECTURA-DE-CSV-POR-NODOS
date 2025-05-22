/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lasalle.oaxaca.edu.mx.procesadorcsvnodos.nodos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Nodo para analizar frecuencias y duplicados
 * 
 * Map: Un objeto que asigna claves a valores. 
 *      Un mapa no puede contener claves duplicadas; 
 *      cada clave puede asignarse a un solo valor como máximo.
 * 
 *      Colección que almacena pares clave-valor, 
 *      donde cada clave es única. Modela un diccionario o una matriz asociativa.
 * 
 * Set: Es una colección que no admite elementos duplicados. 
 *      Modela la abstracción matemática de conjuntos.
 * 
 * 
 */
class FrequencyNode extends ProcessingNode {
    private Map<String, Integer> frequencies;
    private Set<String[]> uniqueRecords;
    
    public FrequencyNode(String[] headers, int[] columns) {
        super(headers, columns);
        this.frequencies = new HashMap<>();
        this.uniqueRecords = new HashSet<>();
    }
    
    @Override
    public void process(List<String[]> records) {
        for (String[] record : records) {
            String[] extractedValues = extractColumns(record);
            String key = generateKey(extractedValues);
            
            // Incrementar la frecuencia
            frequencies.put(key, frequencies.getOrDefault(key, 0) + 1);
            
            // Añadir a registros únicos (automáticamente elimina duplicados)
            uniqueRecords.add(extractedValues);
        }
    }
    
    @Override
    public void showResults() {
        System.out.println("\n----- Resultados del Nodo de Frecuencias -----");
        
        // Mostrar las columnas que está procesando
        System.out.print("Analizando columnas: ");
        for (int col : columns) {
            System.out.print(headers[col] + " ");
        }
        System.out.println();
        
        // Mostrar registros únicos
        System.out.println("Registros únicos encontrados: " + uniqueRecords.size());
        
        // Mostrar frecuencias
        System.out.println("Frecuencias de aparición:");
        frequencies.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " veces");
            });
    }
    
}
