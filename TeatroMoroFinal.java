/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.teatromorofinal;


import java.util.Scanner; 
import java.util.List;    
import java.util.ArrayList;
import java.util.Map;     
import java.util.HashMap;

public class TeatroMoroFinal {

    // Cantidad total de asientos
    private final int totalAsientos = 80;

    // Precios definidos por zona
    private final int precioVIP = 20000;
    private final int precioPlateaBaja = 15000;
    private final int precioPlateaAlta = 12000;
    private final int precioPalco = 10000;
    private final int precioGaleria = 8000;

    // seguimiento de estado de los asientos y ventas
    private boolean[] asientosDisponibles = new boolean[totalAsientos];
    private String[] estadoAsientos = new String[totalAsientos];
    private String[] tipoEntradas = new String[totalAsientos];
    private int[] preciosPagados = new int[totalAsientos];
    private boolean[] boletaImpresa = new boolean[totalAsientos];
    private String[] clientes = new String[totalAsientos];

    // Estadísticas del "dia" del teatro
    private int totalEntradasVendidas = 0;
    private int totalIngresos = 0;

    // Lista de reservas y mapa de clientes con ID, utilizo hashmap para darle un unica a cada cleinte. Si un lcinete realiza una segunda compra, no se le asignara un nuevo id.
    private List<Reserva> reservas = new ArrayList<>();
    private Map<String, String> mapaClientes = new HashMap<>();

    // Método principal
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TeatroMoroFinal teatro = new TeatroMoroFinal();
        teatro.menu(scanner);
    }

    // Inicializa todos los asientos como disponibles con un constructor
    public TeatroMoroFinal() {
        for (int i = 0; i < totalAsientos; i++) {
            asientosDisponibles[i] = true;
            estadoAsientos[i] = "Disponible";
            tipoEntradas[i] = "";
            preciosPagados[i] = 0;
            boletaImpresa[i] = false;
            clientes[i] = "";
        }
    }

    // Menú principal
    private void menu(Scanner scanner) {
        int opcion;
        do {
            System.out.println("\n--- ¡Bienvenido a Teatro Moro! A continuación puedes gestionar tu atención a traves del menu ---");
            System.out.println("1. Reservar Entrada");
            System.out.println("2. Comprar Entradas");
            System.out.println("3. Modificar Asientos");
            System.out.println("4. Ver disponibilidad de Asientos");
            System.out.println("5. Salir");

            opcion = leerNumeroConValidacion(scanner, "Seleccione una opción: ", 1, 5);

            switch (opcion) {
                case 1 -> reservarEntrada(scanner);
                case 2 -> comprarEntradas(scanner);
                case 3 -> modificarAsientos(scanner);
                case 4 -> mostrarAsientos();
                case 5 -> mostrarResumen();
            }
        } while (opcion != 5); //se mantinee menu activo mientras no se selccione salir
    }

    // Permite reservar una entrada, validando descuento con carnet
    public void reservarEntrada(Scanner scanner) {
        mostrarAsientos();
        int asiento = leerNumeroConValidacion(scanner,"Seleccione asiento: ", 1, totalAsientos) - 1; 

        if (!asientosDisponibles[asiento]) { //verfica disponibilidad el asiento seleccionado
            System.out.println("Asiento no disponible.");
            return; //vuelve al menu
        }

        System.out.print("Nombre del cliente: ");
        String cliente = scanner.nextLine().trim(); //Se utiliza trim para evitar que los espacios extra generen distintos id para el mismo nombre

        String tipo = leerTipoEntrada(scanner);//selecciona el tipo de cliente y su descuento correspondiente

        if (!tipo.equals("Normal")) { //si se aplica descuento se necesita proprcionar carnet como evidencia de aplicabilidad
            System.out.println("¿Tiene carnet de identidad para aplicar el descuento? (1 - Sí / 2 - No): ");
            int opcionCarnet = leerNumeroConValidacion(scanner, "", 1, 2);
            if (opcionCarnet == 2) { //si no valida, debe págar precio normal o volver al menu
                System.out.println("Al no validar con su carnet solo puede reservar la entrada normal, ¿desea continuar de todas formas? (1 - Sí / 2 - No):");
                int continuar = leerNumeroConValidacion(scanner, "", 1, 2);
                if (continuar == 2) { //cancela el proceso de reserva
                    System.out.println("Reserva cancelada. Volviendo al menú principal.");
                    return;
                } else {
                    tipo = "Normal"; //se asigna precio normal al no validar descuento
                }
            }
        }

        asientosDisponibles[asiento] = false; // asiento reservado deja de estar disponible
        estadoAsientos[asiento] = "Reservado";
        tipoEntradas[asiento] = tipo; //se guarda el tipo de descuento aplicado o si es normal
        clientes[asiento] = cliente; //se guarda el nombre del cliente en el lugar que correponde al asiento 

        String idReserva = String.format("R%03d", reservas.size() + 1); //se crea id con 3 ceros y el numero de la reserva en el orden en que son hechas
        reservas.add(new Reserva(asiento, tipo, idReserva)); //agrega la nueva reserva 

        mapaClientes.putIfAbsent(cliente, "C" + String.format("%03d", mapaClientes.size() + 1)); // agrega y crea el id de cleinte si es que no ha hecho una compra anterior

        System.out.println("Reserva realizada con éxito. Su ID de reserva es de: " + idReserva);
    }

    // Permite realizar la compra de las reservas existentes
    public void comprarEntradas(Scanner scanner) {
        boolean tieneReservas = false; //valor inicial

        for (Reserva r : reservas) { //recorre cada reserva en reservas 
            if (estadoAsientos[r.asiento].equals("Reservado")) { // verifica si el asiento de la reserva revisada esta reservado
                tieneReservas = true;
                int precioBase = obtenerPrecioZona(r.asiento); //para evitar mucha informacion en el mismo metodo se usa metodo auxiliar para asignar el valor base de los asientos dependiendo del numero 

                int precioFinal = precioBase; //caso base

                // Aplicación de descuento según tipo de entrada
                switch (r.tipoEntrada) {
                    case "Niño" -> precioFinal = (int) (precioBase * 0.90);
                    case "Mujer" -> precioFinal = (int) (precioBase * 0.80);
                    case "Estudiante" -> precioFinal = (int) (precioBase * 0.85);
                    case "Tercera Edad" -> precioFinal = (int) (precioBase * 0.75);
                }

                preciosPagados[r.asiento] = precioFinal; //se guarda el precio final de la entrada para mostar en boleta
                totalIngresos += precioFinal; //se acumulan los ingresos del dia
                totalEntradasVendidas++; //se acumulan las entradas vendidas del dia
                estadoAsientos[r.asiento] = "Vendido"; //se cambia de reservado a vendido
            }
        }

        if (!tieneReservas) { //metodo auxiliar para definir si existen reservas activas
            System.out.println("No hay asientos reservados.");
            return;
        }

        reservas.clear(); //se quitan las reservas al ser compradas

        System.out.println("\n¿Desea imprimir boleta? (1 - Sí / 2 - No): ");
        if (leerNumeroConValidacion(scanner, "", 1, 2) == 1) {
            imprimirBoletas(); //imprime boleta
        }
    }

    // Calcula el precio de una entrada según su zona
    private int obtenerPrecioZona(int asiento) { //metodo auxiliar, asigna el precio segun la zona 
        int i = asiento + 1;
        if (i >= 1 && i <= 20) return precioVIP;
        if ((i >= 22 && i <= 29) || (i >= 32 && i <= 39)) return precioPlateaBaja;
        if ((i >= 42 && i <= 49) || (i >= 52 && i <= 59)) return precioPlateaAlta;
        if (i == 21 || i == 31 || i == 41 || i == 51 || i == 30 || i == 40 || i == 50 || i == 60) return precioPalco;
        return precioGaleria;
    }

    // Permite cancelar reservas o ventas existentes
    public void modificarAsientos(Scanner scanner) {
        System.out.println("¿Desea cancelar una reserva o una compra? 1-reserva / 2-compra");
        int opcion = leerNumeroConValidacion(scanner, "", 1, 2);

        if (opcion == 1) { //cancelar reserva
            int asiento = leerNumeroConValidacion(scanner, "Ingrese el número de asiento reservado que desea cancelar: ", 1, totalAsientos) - 1;
            if (!estadoAsientos[asiento].equals("Reservado")) { //comprueba que exista la reserva
                System.out.println("Este asiento no está reservado.");
                return; //regresa almenu 
            }
            estadoAsientos[asiento] = "Disponible"; //se libera el asiento en todas las estructuras
            asientosDisponibles[asiento] = true;
            tipoEntradas[asiento] = "";
            clientes[asiento] = "";
            reservas.removeIf(r -> r.asiento == asiento); //se elimina la reserva de reservas, comprobando que coincida el numero de asiento a liberar
            System.out.println("Reserva cancelada con éxito.");

        } else { //eliminar compra
            int asiento = leerNumeroConValidacion(scanner, "Ingrese el asiento comprado que desea devolver (1-" + totalAsientos + "): ", 1, totalAsientos) - 1;
            if (!estadoAsientos[asiento].equals("Vendido")) { //comprueba estado del asiento 
                System.out.println("Este asiento no ha sido vendido aún.");
                return;//regresa al menu
            }
            estadoAsientos[asiento] = "Disponible"; //libera el asiento en todas las estructuras y disminuye los ingresos y asientos vendidos
            asientosDisponibles[asiento] = true;
            totalIngresos -= preciosPagados[asiento];
            totalEntradasVendidas--;
            tipoEntradas[asiento] = "";
            preciosPagados[asiento] = 0;
            boletaImpresa[asiento] = false;
            clientes[asiento] = "";
            System.out.println("Venta cancelada con éxito.");
        }
    }

    // Imprime la boleta de asientos vendidos
    public void imprimirBoletas() {
        System.out.println("\n--- BOLETAS ---");
        for (int i = 0; i < totalAsientos; i++) { //recorre todos los asientos buscando los vendidos
            if (estadoAsientos[i].equals("Vendido") && !boletaImpresa[i]) { //evita imprimir la misma boletade una compra anterior
                System.out.println("Asiento: " + (i + 1));
                System.out.println("Cliente: " + clientes[i]);
                System.out.println("ID Cliente: " + mapaClientes.get(clientes[i]));
                System.out.println("Tipo Entrada: " + obtenerZonaCompleta(i + 1)); //imprime el nombre de la zona con un metodo auxiliar
                System.out.println("Precio: $" + preciosPagados[i]);
                System.out.println("Descuento aplicado: " + (tipoEntradas[i].equals("Normal") ? "Sin descuento" : tipoEntradas[i])); //imprime el tipo de descuento
                System.out.println("----------------------");
                boletaImpresa[i] = true;
            }
        }
    }

    // Retorna el nombre completo de la zona según el número de asiento
    private String obtenerZonaCompleta(int i) { //metodo auxiliar pára convertir el nombre de cada zona a un string 
        if (i >= 1 && i <= 20) return "VIP";
        if ((i >= 22 && i <= 29) || (i >= 32 && i <= 39)) return "Platea Baja";
        if ((i >= 42 && i <= 49) || (i >= 52 && i <= 59)) return "Platea Alta";
        if (i == 21 || i == 31 || i == 41 || i == 51 || i == 30 || i == 40 || i == 50 || i == 60) return "Palco";
        return "Galería";
    }

    // Muestra el estado actual de todos los asientos
    private void mostrarAsientos() {
        System.out.println("\nEstado de asientos: D-disponible, R-reservado, V-vendido");
        System.out.println("Zonas: V=VIP, B=Platea Baja, A=Platea Alta, P=Palco, G=Galería");
        System.out.println("-------------Pantalla---------------");
        for (int i = 0; i < totalAsientos; i++) {
            if (i % 10 == 0) {
                System.out.println();
                 if (i < 10) {
                     System.out.print("     ");//agrega 5 espacis en la primera fila para que se vea centrado
                 } 
            }
            String zona = obtenerEtiquetaZona(i + 1); //asigna la el tipo de asiento por zona segun el numero
            String estado = estadoAsientos[i]; //obtiene el string del estado del asiento
            String tipo = estado.equals("Disponible") ? "D" : estado.equals("Reservado") ? "R" : "V"; //asingna el estado del asiento 
            System.out.print(zona + (i + 1) + tipo + " "); //ej; vip 12 vendido seria V12V
        }
        System.out.println();
    }

    // Determina la etiqueta de zona para mostrar los asientos
    private String obtenerEtiquetaZona(int i) { //metodo auxiliar para crear la etiqueta que se muestra en los asientos
        if (i >= 1 && i <= 20) return "V";
        if ((i >= 22 && i <= 29) || (i >= 32 && i <= 39)) return "B";
        if ((i >= 42 && i <= 49) || (i >= 52 && i <= 59)) return "A";
        if (i == 21 || i == 31 || i == 41 || i == 51 || i == 30 || i == 40 || i == 50 || i == 60) return "P";
        return "G";
    }

    // Muestra resumen final de ventas
    private void mostrarResumen() {
        System.out.println("\n--- RESUMEN ---");
        System.out.println("Entradas vendidas: " + totalEntradasVendidas);
        System.out.println("Ingresos totales: $" + totalIngresos);
        System.out.printf("Porcentaje de ocupación: %.2f%%\n", (totalEntradasVendidas / (double) totalAsientos) * 100);
    }

    // Método reutilizable para validar entrada que se ingrese una entrada valida y no colapse el programa, usada cada vez que se usa scanner
    private int leerNumeroConValidacion(Scanner scanner, String mensaje, int min, int max) {
        //valors iniciales
        int numero = 0;
        boolean valido = false;
        while (!valido) { //mientras no sea valido
            System.out.print(mensaje);
            try {
                numero = Integer.parseInt(scanner.nextLine()); //recibe la ingreado por el usuario con un string y lo transforma a entero
                if (numero >= min && numero <= max) { //si esta dentro del rango permitido por la opcion 
                    valido = true;
                } else {
                    System.out.println("Debe ingresar un numero dentro lo especificado. Intente nuevamente");
                }
            } catch (NumberFormatException e) { //se utiliza la identificación de excepcion para "colar" entradas no numericas
                System.out.println("Opción inválida, debe ingresar un número");
            }
        }
        return numero; //regresa la opcion seleccionada si es valida
    }

    // Permite al usuario seleccionar el tipo de entrada con descuento
    private String leerTipoEntrada(Scanner scanner) { //metodo auxiliar para seleccionar si existe descuento
        System.out.println("Tipo de entrada:");
        System.out.println("1. Normal");
        System.out.println("2. Niño (10% desc.)");
        System.out.println("3. Mujer (20% desc.)");
        System.out.println("4. Estudiante (15% desc.)");
        System.out.println("5. Tercera Edad (25% desc.)");
        int opcion = leerNumeroConValidacion(scanner, "Seleccione una opción: ", 1, 5);

        return switch (opcion) { //retorna el tipo de entrada como un string para uso en boleta
            case 2 -> "Niño";
            case 3 -> "Mujer";
            case 4 -> "Estudiante";
            case 5 -> "Tercera Edad";
            default -> "Normal";
        };
    }

    // Clase interna que representa cada reserva guardada en reservas, se utiliza en la impresion de boleta y para la comprobacion eficiente de los estados de cada reserva
    private class Reserva {
        int asiento;
        String tipoEntrada;
        String idReserva; //se utiliza en la impresión de la boleta

        public Reserva(int asiento, String tipoEntrada, String idReserva) {
            this.asiento = asiento;
            this.tipoEntrada = tipoEntrada;
            this.idReserva = idReserva;
        }
    }
}






