package estandar;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class Polifasico {
	
	
	// ----- Metodo determinarNumeroTramoIniciales -------- // 
	// es mas lento que lo que propone el libro pero nos ayuda a entender como podemos recorrer el fichero y acceder a posiciones dentro de él
	
												// recibe ubicacion archivo y lo podemos poner a recibir int disponibles = interpreta.available();) // Lo ponemos asi por el manejo de la excepcion ya que si mandamos esto desde antes es mas facil controlarla.
	public int determinarNumeroTramosIniciales(String ubicacionArchivo) throws FileNotFoundException, IOException { // Hay que atrapar excepciones en orden jerarquico de menor a mayor 
		InputStream lectura = new FileInputStream(ubicacionArchivo); // Si no se encuentra el archivo FileNotFoundException se arroja 
		// Abre el archivo en modo lectura para leer byte por byte.
		
		DataInputStream interpreta = new DataInputStream(lectura);
		// Recibe como parametro a FileInputStream (lo envuelve).
		// Usa esos bytes y los interpreta como datos primitivos.
		
		int disponibles = interpreta.available(); // Metodo que devuelve cantidad de bytes que quedan por leer logramos saber cuantas veces vamos a revisar, se puede cambiar como el distribuir() de mezcla multiple
		int cantidadElementos = disponibles / 4; // los int son de 4 bytes
		
		int numeroTramosIniciales = 0; // Iniciamos contador en 0
		
		int num1 = interpreta.readInt();
		int num2;  
		for (int i = 1; i < cantidadElementos; i++) { //Empieza en 1 porque ya leimos el primero
			// Comparaciones de tramos aca. Cada vez que se encuentre un "Numero mayor seguido de uno menor" contador++; 
			num2 = interpreta.readInt(); 
			if (num1 > num2) {
				numeroTramosIniciales++; 
			}
			num1 = num2; 
		}
		lectura.close();
		
		numeroTramosIniciales++; // Siempre hay almenos 1 tramo
		
		return numeroTramosIniciales; 
	}
	
	//---------- Metodo determinarNumeroTramosInciales parecido al libro ---------- //
	// Este metodo recorre usando un while (true) y BufferedInputStream
	
	public int determinarNumeroTramosInicialesLibro(String ubicacionArchivo) throws FileNotFoundException, IOException {
	    DataInputStream lectura = new DataInputStream(new BufferedInputStream(new FileInputStream(ubicacionArchivo)));
	    
	    // En este agregamos el BufferedInputStream como en el libro porque no nos toca estar accediendo al disco duro 
	    // basicamente divide el fichero de a 8 KB que son 8,192 bytes y almacena temporalmente en memoria de manera que
	    // cada que necesita mas lee los siguientes 8kb. 
	    
	    int numeroTramosIniciales = 0;
	    
	    try {
	        int anterior = lectura.readInt(); // Leemos el primer número
	        int actual; 
	        
	        while (true) { // Seguimos leyendo hasta EOF
	            actual = lectura.readInt();
	            
	            if (actual < anterior) {
	                numeroTramosIniciales++; // Detectamos un cambio de tramo
	            }
	            
	            anterior = actual; // Actualizamos igual que en el otro 
	        }
	    } catch (EOFException e) {
	        // Cuando termina el archivo
	        numeroTramosIniciales++; // Siempre hay al menos un tramo
	    } finally { // Siempre hay que cerrar
	        lectura.close();
	    }

	    return numeroTramosIniciales;
	}

	// --------- Metodo distribuirInicialmente los tramos ------- //
	public int[] distribuirInicialmente(String origen, String f1, String f2) throws FileNotFoundException, IOException {
		
		int numeroTramosIniciales = determinarNumeroTramosInicialesLibro(origen); 
		int [] a = {1, 0}; // Arreglo numero de tramos por archivo (Reales + ficticios)
		int [] d = {1, 0}; // Arrgelo numero de tramos ficticios por archivo
		// El libro pide iniciar en {1,1,...,1) pero usaremos formuals recurrentes y manejaremos el caso donde ya este ordenado
		
		int nivel = 0; 
		
		while ((a[0] + a[1]) < numeroTramosIniciales) { //Sucesion Fibonacci en formulas recurrentes
			a[1] = a[0]; 
			a[0] = a[0] + a[1]; 
			nivel++; 
		}
		
		// Tramos ficticios tienen que ser iguales a tramos reales siguiendo logica del libro
		d[0] = a[0];
		d[1] = a[1];
		// Abrir los archivos
		DataInputStream lectura = new DataInputStream(new BufferedInputStream(new FileInputStream(origen)));
		
		DataOutputStream escritura1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f1)));
		DataOutputStream escritura2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f2)));
		try {
	        int anterior = lectura.readInt(); // Leemos el primer número
	        int actual; 
	        
	        int i = 0; 
	        
	     // diferencia entre a[0] y a[1] se mete en f1 para que los ficticios queden uniformemente separados
	        while (true && i < (a[0] - a[1])) { // Seguimos leyendo hasta EOF
	            actual = lectura.readInt();
	            
	            if (actual < anterior) { // Detectamos un cambio de tramo
	                 i++; 
	                 escritura1.writeInt(anterior);
	                 d[0]--; 
	            } else {
	            	escritura1.writeInt(anterior);
	            	d[0]--; 
	            }
	            
	            anterior = actual; // Actualizamos igual que en el otro 
	        }
	        boolean turnof1 = false; 
	     // intercalamos entre f1 y f2 la escritura de los tramos ficticios restantes 
	        while (true) { // Seguimos leyendo hasta EOF
	            actual = lectura.readInt();
	            
	            if (actual < anterior) { // Detectamos un cambio de tramo
	                if (turnof1 == true) { 
	                	escritura1.writeInt(anterior);
	                	d[0]--; 
	                	turnof1 = false; 
	                } else {
	                	escritura2.writeInt(anterior);
	                	d[1]--; 
	                	turnof1 = true; 
	                }
	            } else {
	            	if (turnof1 == true) { 
	                	escritura1.writeInt(anterior);
	                	d[0]--; 
	            	} else {
	            		escritura2.writeInt(anterior);
	                	d[1]--; 
	            	}
	            }
	            
	            anterior = actual; // Actualizamos igual que en el otro 
	        }
	    } catch (EOFException e) {
	        // Cuando termina el archivo
	    	
	    } finally { // Siempre hay que cerrar
			lectura.close();
			escritura1.close();
			escritura2.close();
	    }
		return d; 
	}
}
