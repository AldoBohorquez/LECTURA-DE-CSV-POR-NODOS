/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lasalle.oaxaca.edu.mx.procesadorcsvnodos.nodos;

import java.util.List;

/**
 * Clase base para todos los nodos de procesamiento
 */
abstract class ProcessingNode {
    
    protected String[] headers;
    protected int[] columns;
    
    public ProcessingNode(String[] headers, int[] columns) {
        this.headers = headers;
        this.columns = columns;
    }
    
    /**
     * Procesa los registros del CSV
     */
    public abstract void process(List<String[]> records);
    
    /**
     * Muestra los resultados del procesamiento
     */
    public abstract void showResults();
    
    /**
     * Extrae las columnas específicas de un registro
     */
    protected String[] extractColumns(String[] record) {
        String[] result = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            if (columns[i] < record.length) {
                result[i] = record[columns[i]];
            } else {
                result[i] = "";
            }
        }
        return result;
    }
    
    /**
     * Genera una clave única para un conjunto de valores
     */
    protected String generateKey(String[] values) {
        return String.join("-", values);
    }
}