package estandar;

import java.io.InputStream;
import java.util.Random;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

// Metodo polifasico con 3 archivos 

public class Polifasico {
	// Variables globales static
    private static int[] a = {1, 0}; // Arreglo numero de tramos por archivo (Reales + ficticios)
    private static int[] d = {1, 0}; // Arrgelo numero de tramos ficticios por archivo
    // El libro pide iniciar en {1,1,...,1) pero usaremos formuals recurrentes y manejaremos el caso donde ya este ordenado
    private static int nivel = 0; 
    
    // Ubicacion de archivos 
    private static String origen = "C:\\Pedro\\EIA\\Semestre3\\Estructuras de datos\\FicherosPolifasico\\Origen.bin"; 
	private static String f1 = "C:\\Pedro\\EIA\\Semestre3\\Estructuras de datos\\FicherosPolifasico\\f1.bin";
	private static String f2 = "C:\\Pedro\\EIA\\Semestre3\\Estructuras de datos\\FicherosPolifasico\\f2.bin";
	private static String f3 = "C:\\Pedro\\EIA\\Semestre3\\Estructuras de datos\\FicherosPolifasico\\f3.bin"; 
	
	// ----- Metodo determinarNumeroTramoIniciales -------- // 
	// es mas lento que lo que propone el libro pero nos ayuda a entender como podemos recorrer el fichero y acceder a posiciones dentro de él
	
												//lo podemos poner a recibir int disponibles = interpreta.available();) // Lo ponemos asi por el manejo de la excepcion ya que si mandamos esto desde antes es mas facil controlarla.
	public static int determinarNumeroTramosIniciales() throws FileNotFoundException, IOException { // Hay que atrapar excepciones en orden jerarquico de menor a mayor 
		InputStream lectura = new FileInputStream(origen); // Si no se encuentra el archivo FileNotFoundException se arroja 
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
	
    	numeroTramosIniciales++; // Siempre hay al menos un tramo
   
		return numeroTramosIniciales; 
	}
	
	//---------- Metodo determinarNumeroTramosInciales parecido al libro ---------- //
	// Este metodo recorre usando un while (true) y BufferedInputStream
	
	public static int determinarNumeroTramosInicialesLibro() throws FileNotFoundException, IOException {
	    DataInputStream lectura = new DataInputStream(new BufferedInputStream(new FileInputStream(origen)));
	    
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
	public static void distribuirInicialmente() throws FileNotFoundException, IOException {
		
		int numeroTramosIniciales = determinarNumeroTramosInicialesLibro(); 
		int temp; 
		while ((a[0] + a[1]) < numeroTramosIniciales) { //Sucesion Fibonacci en formulas recurrentes
			temp = a[1]; // el orden de las formulas importa por eso meti este
			a[1] = a[0]; 
			a[0] = a[0] + temp; 
			
			nivel++; 
		}
		
		// Tramos ficticios tienen que ser iguales a tramos reales siguiendo logica del libro
		d[0] = a[0];
		d[1] = a[1];
		// Abrir los archivos
		DataInputStream lectura = new DataInputStream(new BufferedInputStream(new FileInputStream(origen)));
		
		DataOutputStream escritura1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f1)));
		DataOutputStream escritura2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f2)));
		
		// Estas variables a continuacion se declaran antes del try para que en el catch (cuando se acaba el fichero de origen) podamos utilizarlas
		boolean turnof1 = false; // Empieza en false para que el primer bloque luego de la diferencia vaya a f2. 

        int anterior = lectura.readInt(); // Leemos el primer número
		int actual; 
		try {
			
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
	            }
	            
	            anterior = actual; // Actualizamos igual que en el otro 
	        }
	        
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
	            	} else {
	            		escritura2.writeInt(anterior);
	            	}
	            }
	            
	            anterior = actual; // Actualizamos igual que en el otro 
	        }
	    } catch (EOFException e) {
	        // Cuando termina el archivo
	    	if (turnof1 == true) {
	            escritura1.writeInt(anterior);
	        } else {
	            escritura2.writeInt(anterior);
	        }
	    } finally { // Siempre hay que cerrar
			lectura.close();
			escritura1.close();
			escritura2.close();
	    }
	}
	
	// --------- Metodo Mezcla -------- // 
	public void mezclarTramos() {
		// En proceso...
		/* Si usas new FileOutputStream("archivo.bin"), el archivo se sobrescribe. Si el archivo ya existe, su contenido anterior será eliminado y reemplazado por los nuevos datos.
		 * Si usas new FileOutputStream("archivo.bin", true), los datos existentes no se borrarán y se agregarán los nuevos datos al final del archivo.*/ 
	}
	
	public static void llenarArchivoAleatoriamente() throws FileNotFoundException, IOException {
		DataOutputStream escritura = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(origen)));
		
		Random random = new Random(); 
		int numeroAleatorio; 
		
		try {
			for (int i = 0; i < 1000; i++) {
				numeroAleatorio = random.nextInt(); //Numeros de 2^(32) se pueden generar
				escritura.writeInt(numeroAleatorio);
			}
		} catch (IOException e) {
			// Es solo un control para que sin importar si se genera excepcion o no se cierre la escritura
		} finally {
			escritura.close();
		}
		
	}
	
	// PORFAVOR NO BORRAR SON PARA HACER PRUEBAS CON ARREGLOS QUE PUEDA HACER A MANO TAMBIEN :)
	
	public static void llenarArchivoPrueba() throws FileNotFoundException, IOException {
		DataOutputStream escritura = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(origen)));
		int arreglo [] = {1, 3, 4, 6, 2, 5, 25, 6, 10, 15, 8, 12, 3, 7, 9, 11, 14, 15, 1, 4, 40, 7, 28, 13, 15, 15, 15, 18, 9, 12, 3, 4, 8, 6, 2, 80, 5, 19, 21, 3, 5, 1, 150, 30, 35, 38}; 
						//   1      /    2    /     3    /   4  /          5         /    6    /   7  /        8          /   9  /   10    /11/  12  /    13    /    14  /   15  /    16     /
		int arregloL [] = {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}; 
		try {
			for (int i = 0; i < arreglo.length; i++) {
				escritura.writeInt(arreglo[i]);
			}
		} catch (IOException e) {
			// Es solo un control para que sin importar si se genera excepcion o no se cierre la escritura
		} finally {
			escritura.close();
		}
		
	}
	
	public static int determinarNumeroTramosLibroPrueba(String ubicacionArchivo) throws FileNotFoundException, IOException {
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
	// -------------------------------------------------------- Gracias 
	public static void main (String[] args) {
		try {
			
			llenarArchivoPrueba(); 
			int numeroT = determinarNumeroTramosInicialesLibro(); 
			
			System.out.println("Tramos = " + numeroT);
			distribuirInicialmente(); 
			
			for (int i = 0; i < 2; i++) {
				System.out.println("d = " + d[0] + " " + d[1]);
			}
			for (int j = 0; j < 2; j++) {
				System.out.println("a = " + a[0] + " " + a[1]);
			}
			int numeroT1 = determinarNumeroTramosLibroPrueba(f1); 
			System.out.println("Tramos en 1: " + numeroT1);
			int numeroT2 = determinarNumeroTramosLibroPrueba(f2); 
			System.out.println("Tramos en 2: " + numeroT2);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
