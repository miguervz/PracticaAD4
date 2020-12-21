package academia;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.matisse.MtDatabase;
import com.matisse.MtException;
import com.matisse.MtObjectIterator;
import com.matisse.MtPackageObjectFactory;

public class Main {

	static String hostname = "localhost";
	static String dbname = "academia";

	public static void main(String[] args) {
		
System.out.println("Seleccione su opción: \n 1.CREAR \n 2.ELIMINAR \n 3.MODIFICAR \n 4.CONSULTAR ");
Scanner teclado = new Scanner(System.in);
String numero = teclado.nextLine();
switch (Integer.parseInt(numero)){
	case 1:
		creaObjetos(hostname, dbname);
		
		break;
	case 2:
		borrarTodos(hostname, dbname);
		
		break;
	case 3:
		modificaObjeto(hostname, dbname, "Miguel", 911000011);
		
		break;
	case 4:
		ejecutaOQL(hostname, dbname);
		
		break;
		
}


	}

	public static void creaObjetos(String hostname, String dbname) {
		try {
	
			MtDatabase db = new MtDatabase(hostname, dbname, new MtPackageObjectFactory());
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.toString() + " de Matisse");
			// Crea un objeto Profesor
			Profesor a1 = new Profesor(db);
			a1.setNombre("Miguel");
			a1.setApellidos("Ramírez");
			a1.setDni("77137106M");
			a1.setTelefono(958508518);
			System.out.println("Creando");
			// Crea un objeto Asignatura
			Asignatura l1 = new Asignatura(db);
			l1.setNombre("Matemáticas");
			l1.setDiaSemana("Lunes");
			l1.setAula("C2");
			l1.setDuracion(1);
			l1.setHoraInicio(10.30);
			System.out.println("Asignatura de Matemáticas los Lunes");
			// Crea otro objeto asignatura
			Asignatura l2 = new Asignatura(db);
			l2.setNombre("Lengua");
			l2.setDiaSemana("Martes");
			l2.setAula("C1");
			l2.setDuracion(1.5);
			l2.setHoraInicio(12.30);
			
			System.out.println("Asignatura de Lengua los Martes");
			// Crea un array de Obras para guardar los libros y hacer lar leaciones

			Clase o1[] = new Clase[2];
			o1[0] = l1;
			o1[1] = l2;
			// Guarda las relaciones del profesor con las asignaturas que imparte

			a1.setImparte(o1);
			// Ejecuta un commit para materializar las peticiones.
			db.commit();
			// Cierra la base de datos.
			db.close();
			System.out.println("\nHecho.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	public static void borrarTodos(String hostname, String dbname) {
		System.out.println("====================== Borrar Todos============");
		try {
			MtDatabase db = new MtDatabase(hostname, dbname, new MtPackageObjectFactory());
			db.open();
			db.startTransaction();
			// Lista todos los objetos Clase que hay en la base de datos, con el método
			// getInstanceNumber
			System.out.println("\n" + Clase.getInstanceNumber(db) + "Clase(s) en la DB.");
			// Borra todas las instancias de Clase
			Clase.getClass(db).removeAllInstances();
			// materializa los cambios y cierra la BD
			db.commit();
			db.close();
			System.out.println("\nHecho.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	public static void modificaObjeto(String hostname, String dbname, String nombre, Integer nuevoTelefono) {
		System.out.println("=========== Modifica un objeto	==========\n");
		int nProfesores = 0;
		try {
			MtDatabase db = new MtDatabase(hostname, dbname, new MtPackageObjectFactory());
			db.open();
			db.startTransaction();
			// Lista cuántos objetos Clase con el método getInstanceNumber
			System.out.println("\n" + Profesor.getInstanceNumber(db) + " Profesores en la DB.");
			nProfesores = (int) Profesor.getInstanceNumber(db);
			// Crea un Iterador (propio de Java)
			MtObjectIterator<Profesor> iter = Profesor.<Profesor>instanceIterator(db);
			System.out.println("Recorro el iterador de uno en uno cambio cuando encuentro 'nombre'");
			while (iter.hasNext()) {
				Profesor[] profesores = iter.next(nProfesores);
				for (int i = 0; i < profesores.length; i++) {
					// Busca una profesor con nombre 'nombre'
					if (profesores[i].getNombre().compareTo(nombre) == 0) {
						profesores[i].setTelefono(nuevoTelefono);
					} else {
					}
				}
			}
			iter.close();
			// materializa los cambios y cierra la BD
			db.commit();
			db.close();
			System.out.println("\nHecho.");
		} catch (MtException mte) {
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	public static void ejecutaOQL(String hostname, String dbname) {
		MtDatabase dbcon = new MtDatabase(hostname, dbname);
		Connection jdbcon = dbcon.getJDBCConnection();

		// Abre una conexión a la base de datos
		dbcon.open();
		try {
			// Crea una instancia de Statement
			java.sql.Statement stmt = dbcon.createStatement();
			// Asigna una consulta OQL. Esta consulta lo que hace esutilizar REF() para
			// obtener el objeto
			// directamente en vez de obtener valores concretos (quetambién podría ser).
			String commandText = "SELECT REF(a) from academia.Profesor	a;";
			// Ejecuta la consulta y obtiene un ResultSet
			ResultSet rset = stmt.executeQuery(commandText);
			Profesor a1;
			// Lee rset uno a uno.
			while (rset.next()) {
				// Obtiene los objetos Profesor.
				a1 = (Profesor) rset.getObject(1);
				// Imprime los atributos de cada objeto con un formato determinado.

				System.out.println("Profesor: " + String.format("%16s", a1.getNombre())+ String.format("%16s", a1.getApellidos()) + "          Teléfono: " + String.format("%16s", a1.getTelefono()));
			}
			// Cierra las conexiones
			rset.close();
			stmt.close();
			jdbcon.close();
			dbcon.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}

}
