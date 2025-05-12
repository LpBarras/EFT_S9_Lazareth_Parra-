# EFT_S9_Lazareth_Parra-
El menu permite gestionar reservas, compras y modificaciones de entradas para el teatro moro, tomao en cuenta 80 asientos, divididos por zonas y con distintos precios y descuento.

Al inicio se definen las variables y estructuras que almacenan los estados, precios y datos relacionados con cada asiento y reserva:

-boolean[] asientosDisponibles controla si un asiento está libre.

-estadoAsientos indica si está Disponible, Reservado o Vendido.

-tipoEntradas almacena el tipo de cliente o descuento (Niño, Mujer,estudiante, tercera edad o normal).

-preciosPagados guarda cuánto pagó cada cliente.

-boletaImpresa evita imprimir varias veces la misma boleta.

-clientes vincula nombre del cliente al asiento.

-reservas y mapaClientes permiten gestionar clientes y sus identificadores. Decidi usar map al darme cuenta que si ingresaba mi nombre nuevamente luego de realizar la primera compra, se me asignaba un id distintos. Al usar hashmap cada nombre queda asignado a un id unico y no genera repetidos

-Antes de seguir al menu principal se incial el constructor que asegura de que al iniciar el programa todos los asientos estén correctamente marcados como disponibles y sin datos asociados.

-El menu ofrece 5 opciones: Reservar Entrada, Comprar Entradas, Modificar Asientos, Ver Disponibilidad y salir. y se ejecuta en bucle hasta seleccionar salir, de esta forma cada vez que se termina una de la opciones vuelvo al menu, al igual que si ingreso entrdas invalidas.


1. Reservar Entrada
   Al seleccionar 1, comienza por mostrar la disponibilad de asientos por medio del metodo auxiliar mostrarAsientos, en este se muestra el tipo de asiento por zona, el numero y su disponnibilidad. Por ejemplo, los asient vip son del 1 al 20, por lo que en la primera reserva se mostarar como V1D (Vip, 1, disponible), y cambiara a medida que se reserven y vendan asientos.
   A continuación se debe ingresar el nombre del cliente, este nombre se guarda y se genera un id de cliente unico para el(si no ha sido ingresado antes). Se selecciona un asiento libre y el tipo de entrada que se desea comprar. Si se selecciona una opcion distinta a "normal", el sistema requiere verificación de la aplicabilidad del descuento por medio del carnet de identidad, si no puede verificar se asigna el valor base de la entrada o se vuelve al menu segun desee el cliente.

Genera una reserva temporal (no pagada aún), guarda tipo y cliente, y se le asigna un ID único de reserva (tipo R0001).

2. Comprar Entradas.
   Comienza por recorrer las reservas activas para poder realizar la compra, siempre debe realizarse la reserva primero. Con los datos guardados en reserva entrada, calcula el precio final segun la zona y el descuento, si es aplicable. Finalmente actializa el estado de cada asiento pagado a "vendido" y "no disponible" en las estructuras, limpia las reservas, ademas de añadir los ingresos y la cantidad de entradas vendidas a los contadores. Imprime la boleta de forma opcional
2.1 Imprimir Boletas
Se generan boletas para los asientos vendidos con el nombre e id del cliente, numero de cada asiento, tipo de entrada, precio, y descuento (si no aplica se usa "normal").
Se utiliza un boolean para marcar los asientos vendidos como impresos al emitir la boleta. Esto se debe a que en una entrega anterior surgia el problema de que se imprimiar las boletas de cada asiento vendido de forma repetida.


3. Modificar Asientos
   Inicialmete solo permitia eliminar asientos comprados, pero agregue la opción de editar las reservas en caso de error al reservar. Le perimite al usuario seleccionar cual de las dos opciones requiere, debe ingresar el numero de asiento y finalemente vuelve a los valores bases las estructuras del asiento, ademas de descontar de los ingresos totales y asientos vendidos.
   Se valida que el asiento esté realmente reservado o vendido antes de modificar.

4. Mostrar Asientos
Imprime la matriz de 80 asientos, agrupados en filas de 10:
Usa etiquetas de zonas (V-vip, B-platea baja, A- platea alta, P-palco, G-galeria), el numero de asiento y letras D-disponible, R-reservado, V-vendido para estado del asiento.



5. Resumen Final (salir)
Al salir, se muestra:
Total de entradas vendidas
Ingresos totales
Porcentaje de ocupación del teatro

*Lectura de Datos y Validaciones
leerNumeroConValidacion() 
Se utiliza para controlar que los datos ingresados por el usuario estén dentro de un rango válido y sean del tipo correcto. Evita que el menu colapse al ingresar caracteres y simbolos. metodo try-catch

*Clase Interna Reserva
Se usa para almacenar de forma temporal la información de cada reserva antes de ser comprada. Acceso rapido a los datos de cada reserva, se guardan en la lista de reservas.
