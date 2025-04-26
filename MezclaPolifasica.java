package mezclaPolifasicaExterna;

import java.util.*;

/*
 * Notas:
 * Solo funciona con Integer por ahora utilizando 3 arrays auxiliares 
 * */

public class MezclaPolifasica {	
	public static ArrayList<Integer> mezclaPolifasica(ArrayList<Integer> input) {				
		return input;
	}
	
	public static ArrayList<Integer> distribucionPolifasica(ArrayList<Integer> input) {
        // Cantidad total de tramos
        int totalTramos = 1;
        int totalFicticios = 0;       
        
//      Indices en donde se dividiran los tramos (final de cada tramo)
        ArrayList<Integer> indexTramos = new ArrayList<Integer>();
        
//        Comparamos cada elemento, si el siguiente es menor que el actual, se suma un tramo
        for (int i = 0; i < input.size() - 1; i++) {
        	if (input.get(i).compareTo(input.get(i + 1)) > 0) {
        		totalTramos++;
        		indexTramos.add(i);
        	}
        }
        
//        System.out.println("Tramos Originales: " + totalTramos);
        
//        Convirtiendo el numero de tramos a un numero de Fibonacci y definiendo la cantidad de
//        Tramos ficticios que necesitaremos
        
        int temp = totalTramos;
        totalTramos = getFibonacciMayorCercano(totalTramos);
        totalFicticios = totalTramos - temp;        
        
//        Definimos el tamano que tendra cada array
        int[] tamanoFull = getParCercanoFibonacci(totalTramos);
        int tamanoUno = tamanoFull[0];
        int tamanoDos = tamanoFull[1];

        System.out.println("Total Tramos: " + totalTramos);
        System.out.println("Total Ficticios: " + totalFicticios);
        System.out.println("Tamano primer Array: " + tamanoUno);
        System.out.println("Tamano segundo Array: " + tamanoDos);
        System.out.println("\n");
        
        for (int n : indexTramos) System.out.println(n); 
                     
        ArrayList output = new ArrayList<Integer>();
        output.add(totalTramos);
        output.add(totalFicticios);
        output.add(tamanoUno);
        output.add(tamanoDos);
        
        return output;
    }
	
	
//	Metodo para encontrar los dos numeros de la sucesion de fibonacci 
//	mas cercanos entre si que sumados nos den un numero en especifico
	
//	Esto se hace para poder definir la longitud de cada tramo
	public static int[] getParCercanoFibonacci(int target) {
        // Caso especial para 0
        if (target == 0) {
            return new int[] {0, 0}; // F(0) + F(0) = 0 + 0 = 0
        }
        
        // Para números negativos no hay solución
        if (target < 0) {
            return null;
        }
        
        // Generar serie de Fibonacci hasta superar el objetivo
        List<Integer> fibSeries = new ArrayList<>();
        fibSeries.add(0); // F(0) = 0
        fibSeries.add(1); // F(1) = 1
        
        while (fibSeries.get(fibSeries.size() - 1) <= target) {
            int nextFib = fibSeries.get(fibSeries.size() - 1) + fibSeries.get(fibSeries.size() - 2);
            fibSeries.add(nextFib);
        }
        
        // Buscar los dos números de Fibonacci más cercanos entre sí que sumen el objetivo
        int[] closestPair = null;
        int minDifference = Integer.MAX_VALUE;
        
        for (int i = 0; i < fibSeries.size(); i++) {
            for (int j = i; j < fibSeries.size(); j++) {
                int sum = fibSeries.get(i) + fibSeries.get(j);
                if (sum == target) {
                    int difference = Math.abs(fibSeries.get(j) - fibSeries.get(i));
                    if (difference < minDifference) {
                        minDifference = difference;
                        closestPair = new int[] {fibSeries.get(j), fibSeries.get(i)};
                    }
                }
            }
        }
        
        return closestPair;
    }
	
//	Encuentra el numero de fibonacci mas cercano
	
//	UTILIZAREMOS ESTA FUNCION PARA DEFINIR LA CANTIDAD DE TRAMOS QUE VAMOS A NECESITAR
//	SE LE PASA LA CANTIDAD DE TRAMOS ACTUAL PARA QUE NOS DE LA CANTIDAD DE TRAMOS FICTICIOS QUE
//	NECESITAREMOS. EN CASO DE QUE EL OUTPUT SEA IGUAL AL NUMERO DE TRAMOS, NO SE NECESITA
//	TRAMOS FICTICIOS
	
	public static int getFibonacciMayorCercano(int target) {
		// Trabajamos con el valor absoluto del target
	    target = Math.abs(target);

	    // Casos base
	    if (target == 0) return 0;
	    if (target == 1) return 1;

	    int prev = 0;
	    int current = 1;

	    // Buscamos el primer número de Fibonacci que sea mayor o igual al target
	    while (current < target) {
	        int next = prev + current;
	        prev = current;
	        current = next;
	    }

	    return current; // Este es el Fibonacci más cercano y mayor o igual
    }
	
	
//	Metodo para verificar si un numero pertenece a la sucesion de fibonacci
	    public static boolean esFibonacci(int n) {
//	    	Un numero pertence a la sucesion de fibonacci si y solo si cumple con la formula inferior
	        return esCuadradoPerfecto(5 * n * n + 4) || esCuadradoPerfecto(5 * n * n - 4);
	    }

//	    Confirma usando dos metodos si el numero es un cuadrado perfecto
	    private static boolean esCuadradoPerfecto(int x) {
	        int raiz = (int) Math.sqrt(x);
	        return raiz * raiz == x;
	    }
	
	public static void main(String args[]) {		
//		System.out.println(getFibonacciCercano(35));
		
		ArrayList<Integer> numeros = new ArrayList<>(List.of(
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2,
				1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2
				));

		System.out.println(distribucionPolifasica(numeros).get(1));
	}
}
