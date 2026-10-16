import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Lector {

    private static final String NOMBRE_FICHERO = "AleatorioDepart.dat";
    private static final int TAMANO_REGISTRO = 64;
    private static final int LONGITUD_TEXTO = 15;

    private static RandomAccessFile raf = null;

    public static void main(String[] args) {
        crearAbrirFichero();
        menu();
    }

    private static void menu() {
        int opcion;
        do {
            System.out.println("--- Menú ---");
            System.out.println("1. Insertar departamento");
            System.out.println("2. Consultar departamento");
            System.out.println("3. Actualizar número de empleados");
            System.out.println("4. Actualizar nombre y localidad");
            System.out.println("5. Eliminar departamento");
            System.out.println("6. Listar departamentos");
            System.out.println("7. Inspección física");
            System.out.println("8. Estadísticas");
            System.out.println("9. Comprobar integridad");
            System.out.println("0. Salir");
            System.out.print("Elija una opción: ");
            opcion = ES.leerInt();

            switch (opcion) {
                case 0:
                    ES.cerrarTeclado();
                    System.out.println("Fin del programa");
                    break;
                case 1:
                    System.out.print("Número: ");
                    int num = ES.leerInt();
                    System.out.print("Nombre: ");
                    String nombre = ES.leerString();
                    System.out.print("Localidad: ");
                    String localidad = ES.leerString();
                    System.out.print("Empleados: ");
                    short emp = ES.leerShort();
                    insertarDepartamento(num, nombre, localidad, emp);
                    break;
                case 2:
                    System.out.print("Número a consultar: ");
                    int numConsulta = ES.leerInt();
                    consultaDepartamento(numConsulta);
                    break;
                case 3:
                    System.out.print("Número departamento: ");
                    int numEmp = ES.leerInt();
                    System.out.print("Nuevo número de empleados: ");
                    short nuevos = ES.leerShort();
                    actualizarNumeroEmpleados(numEmp, nuevos);
                    break;
                case 4:
                    System.out.print("Número: ");
                    int numAct = ES.leerInt();
                    System.out.print("Nombre: ");
                    String nomAct = ES.leerString();
                    System.out.print("Localidad: ");
                    String locAct = ES.leerString();
                    actualizarNombreLocalidad(numAct, nomAct, locAct);
                    break;
                case 5:
                    System.out.print("Número a eliminar: ");
                    int numEli = ES.leerInt();
                    System.out.print("Seguro? (si/no): ");
                    String conf = ES.leerString();
                    if (conf.equalsIgnoreCase("si")) {
                        eliminaDepartamento(numEli);
                    } else {
                        System.out.println("cancelado...");
                    }
                    break;
                case 6:
                    listadoDepartamentos();
                    break;
                case 7:
                    inspeccionFisica();
                    break;
                case 8:
                    estadisticas();
                    break;
                case 9:
                    integridad();
                    break;

                default:
                    System.out.println("error...");
                    break;
            }
        } while (opcion != 0);
    }

    // Crea el fichero si no existe y lo abre en modo lectura o escritura

    private static void crearAbrirFichero() {
        // Objeto File = la "ruta" del fichero
        File archivo = new File("AleatorioDepart.dat");

        try {
            if (archivo.createNewFile()) {
                System.out.println("Fichero creado vacío");
            } else {
                System.out.println("El fichero ya existía");
            }

            // RandomAccessFile = acceso aleatorio ponemos el modo "rw" como me dijo el
            // profesor en clase
            // Se asigna a la variable de clase para usarlo en el menú
            raf = new RandomAccessFile(archivo, "rw");
            System.out.println("Fichero abierto de " + raf.length() + " bytes");
        } catch (IOException ioe) {
            System.out.println("error: " + ioe.getMessage());
        }
    }

    private static long despazamiento(int numDep) {
        return (numDep - 1) * TAMANO_REGISTRO;
    }

    private static void insertarDepartamento(int numDep, String nombre, String localidad, short empleados) {
        try {
            long pos = despazamiento(numDep);
            raf.seek(pos);

            raf.writeShort(numDep);

            // Nombre a 15 caracteres
            String n = nombre;
            while (n.length() < LONGITUD_TEXTO) {
                n = n + " ";
            }
            if (n.length() > LONGITUD_TEXTO) {
                n = n.substring(0, LONGITUD_TEXTO);
            }
            raf.writeChars(n);

            // Localidad a 15 caracteres (igual que el nombre)
            String l = localidad;
            while (l.length() < LONGITUD_TEXTO) {
                l = l + " ";
            }
            if (l.length() > LONGITUD_TEXTO) {
                l = l.substring(0, LONGITUD_TEXTO);
            }
            raf.writeChars(l);

            raf.writeShort(empleados);

            System.out.println("Departamento insertado en la posición " + pos);
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static void consultaDepartamento(int numDep) {
        try {
            long pos = despazamiento(numDep);

            // Si la posición no existe en el fichero
            if (pos >= raf.length()) {
                System.out.println("El departamento no existe");
            }

            raf.seek(pos);

            short id = raf.readShort();

            // Leer 15 caracteres del nombre
            String nombre = "";
            for (int i = 0; i < LONGITUD_TEXTO; i++) {
                nombre = nombre + raf.readChar();
            }

            // Leer 15 caracteres de la localidad
            String localidad = "";
            for (int i = 0; i < LONGITUD_TEXTO; i++) {
                localidad = localidad + raf.readChar();
            }

            short empleados = raf.readShort();

            System.out.println("Departamento: " + id);
            System.out.println("Nombre: " + nombre.trim());
            System.out.println("Localidad: " + localidad.trim());
            System.out.println("Empleados: " + empleados);
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static void actualizarNumeroEmpleados(int numDep, short nuevosEmpleados) {
        try {
            long pos = despazamiento(numDep);
            if (pos < raf.length()) {
                // Saltamos lo que hay antes de empleados:
                // 2 bytes del número + 30 del nombre + 30 de la localidad
                raf.seek(pos + 2 + 30 + 30);
                raf.writeShort(nuevosEmpleados);
                System.out.println("Empleados actualizados");
            } else {
                System.out.println("El departamento no existe...");
            }
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static void actualizarNombreLocalidad(int numDep, String nombre, String localidad) {
        try {
            long pos = despazamiento(numDep);

            if (pos >= raf.length()) {
                System.out.println("El departamento no existe");
            }

            // Nos colocamos en el nombre, luego le sumamos 2 bytes)
            raf.seek(pos + 2);

            // Nombre a 15 caracteres como en el insectarDepartamento
            String n = nombre;
            while (n.length() < LONGITUD_TEXTO) {
                n = n + " ";
            }
            if (n.length() > LONGITUD_TEXTO) {
                n = n.substring(0, LONGITUD_TEXTO);
            }
            raf.writeChars(n);

            // Localidad a 15
            String l = localidad;
            while (l.length() < LONGITUD_TEXTO) {
                l = l + " ";
            }
            if (l.length() > LONGITUD_TEXTO) {
                l = l.substring(0, LONGITUD_TEXTO);
            }
            raf.writeChars(l);

            // No tocamos el short de empleados
            System.out.println("Nombre y localidad actualizados");
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static void eliminaDepartamento(int numDep) {
        try {
            long pos = despazamiento(numDep);

            if (pos >= raf.length()) {
                System.out.println("El departamento no existe");
            } else {
                // con writeChars ponemos los 15 asteriscos
                raf.seek(pos);
                raf.writeShort(-1);
                raf.writeChars("***************");
                raf.writeChars("***************");
                raf.writeShort(-1);
                System.out.println("Departamento eliminado");
            }
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static String leerTexto() throws IOException {
        String t = "";
        for (int i = 0; i < LONGITUD_TEXTO; i++) {
            t += raf.readChar();
        }
        return t;
    }

    private static void listadoDepartamentos() {
        try {
            int n = 0;
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                short id = raf.readShort();
                String nombre = leerTexto();
                String loc = leerTexto();
                short emp = raf.readShort();
                if (id > 0) {
                    System.out.println(id + " | " + nombre.trim() + " | " + loc.trim() + " | " + emp);
                    n++;
                }
            }
            if (n == 0) {
                System.out.println("No hay departamentos activos");
            } else {
                System.out.println("Total: " + n);
            }
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static void inspeccionFisica() {
        try {
            // arrancamos desde la pos 0
            raf.seek(0);
            int pos = 1;

            // leemos los registross hasta el final
            while (raf.getFilePointer() < raf.length()) {
                long offset = raf.getFilePointer();
                short id = raf.readShort();
                leerTexto();
                leerTexto();
                raf.readShort();

                // por si no se ha llegado a usar
                String estado = "nunca utilizada";
                if (id > 0) {
                    estado = "activa";

                } else if (id == -1) {
                    estado = "eliminada";
                }

                System.out.println(pos + " | byte " + offset + " | id " + id + " | " + estado);
                pos++;
            }
        } catch (IOException e) {
            System.out.println("error...");
        }
    }

    private static void estadisticas() {
        try {
            int activos = 0;
            int eliminados = 0;
            int nunca = 0;
            int sumEmp = 0;

            raf.seek(0); // recorro os ficheros desde la pos 0
            while (raf.getFilePointer() < raf.length()) {
                short id = raf.readShort();
                leerTexto();
                leerTexto();
                short emp = raf.readShort();
                // Voy contando según el id
                if (id > 0) {
                    activos++;
                    sumEmp = sumEmp + emp;
                } else if (id == -1) {
                    eliminados++;
                } else {
                    nunca++;
                }
            }
            System.out.println("Longitud: " + raf.length() + " bytes");
            System.out.println("Activos: " + activos);
            System.out.println("Eliminados: " + eliminados);
            System.out.println("Nunca usadas: " + nunca);
            System.out.println("Suma empleados: " + sumEmp);
            // Ocupación = activos respecto a los huecos (eliminados + nunca)
            int libres = eliminados + nunca;
            if (libres > 0) {
                System.out.println("Ocupación: " + (activos * 100.0 / libres) + " %");
            } else {
                System.out.println("Ocupación: no hay huecos");
            }
        } catch (IOException e) {
            System.out.println("error...");
        }

    }

    private static void integridad() {
        try {
            int problemas = 0;

            if (raf.length() % TAMANO_REGISTRO != 0) {
                System.out.println("Longitud no múltiplo de 64");
                problemas++;
            }

            raf.seek(0);
            int pos = 1;
            while (raf.getFilePointer() < raf.length()) {
                long offset = raf.getFilePointer();
                short id = raf.readShort();
                leerTexto();
                leerTexto();
                short emp = raf.readShort();

                if ((id > 0 && id != pos) || (id > 0 && emp < 0) || (id == -1 && emp != -1)) {
                    System.out.println("Problema en byte " + offset);
                    problemas++;
                }
                pos++;
            }

            if (problemas == 0) {
                System.out.println("Todo correcto");
            } else {
                System.out.println("Problemas encontrados: " + problemas);
            }
        } catch (IOException e) {
            System.out.println("error...");
        }
    }
}
