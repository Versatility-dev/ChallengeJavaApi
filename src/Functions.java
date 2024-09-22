import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Functions {

    private final Scanner teclado = new Scanner(System.in);
    private final ConvierteMoneda convierte = new ConvierteMoneda();
    private final HttpRequestHelper httpHelper = new HttpRequestHelper(); // Reutilizamos HttpRequestHelper
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/86a18a5b07825b45a1e85edc/latest/USD";
    private static final String URL_CODIGOS = "https://www.exchangerate-api.com/docs/supported-currencies";
    private final  Country country = new Country();

    /* Método para obtener la cantidad ingresada por el usuario */
    private double obtenerCantidadValida(String monedaOne, String monedaTwo) {
        double cantidadIngresada = 0;
        boolean inputValido = false;

        while (!inputValido) {
            try {
                System.out.println("Escriba la cantidad de (" + monedaOne + ") que desea convertir a (" + monedaTwo + ")");
                cantidadIngresada = teclado.nextDouble();
                teclado.nextLine();

                if (cantidadIngresada > 0) {
                    inputValido = true;
                } else {
                    System.out.println("Ingrese una cantidad válida (debe ser mayor que 0):");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Solo se permiten números. Inténtalo de nuevo.");
                teclado.nextLine(); // Limpiar el buffer
            }
        }
        return cantidadIngresada;
    }

    // Método para procesar la conversión de monedas
    public void caseFunction(String monedaOne, String monedaTwo) {
        double cantidadIngresada = obtenerCantidadValida(monedaOne, monedaTwo);

        // Obtener el Optional de la conversión
        Optional<Monedas> monedasOptional = convierte.convierteMonedasOpcion(monedaOne, monedaTwo, cantidadIngresada);

        if (monedasOptional.isPresent()) {
            manejarResultadoConversion(monedasOptional.get());
        } else {
            System.out.println("No se pudo realizar la conversión.");
        }
    }

    // Método para manejar el resultado de la conversión
    private void manejarResultadoConversion(Monedas monedas) {
        try {


            // Consultar los datos de los países
            Country country = new Country();
            InfoCountry paisBase = country.obtenerPaisPorMoneda(monedas.base_code());
            InfoCountry paisDestino = country.obtenerPaisPorMoneda(monedas.target_code());
            System.out.println(monedas.toString(paisBase,paisDestino));
            Historial historial = new Historial();
            historial.guardarConvercion(monedas);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para manejar el proceso personalizado
    public void personalizedFuction() {
        mostrarInformacionPersonalizada();
        realizarReintentosApi();
    }

    private void mostrarInformacionPersonalizada() {
        System.out.println("--------------------------------------------------------------------------------------------------------\n" +
                " Utilizamos códigos de moneda de tres letras ISO 4217, por ejemplo, USD para dólares estadounidenses," +
                " EUR para euros, etc.\n " +
                " Para consultar las codigos admitidos puede visitar: " + URL_CODIGOS + "\n" +
                "--------------------------------------------------------------------------------------------------------\n");
    }

    // Método para realizar el proceso de reintento de API usando HttpRequestHelper
    private void realizarReintentosApi() {
        URI uri = URI.create(API_URL);
        Optional<JsonObject> respuestaApi = httpHelper.enviarPeticionYProcesarRespuesta(httpHelper.crearHttpRequest(uri), JsonObject.class);

        if (respuestaApi.isPresent()) {
            manejarRespuestaApi(respuestaApi.get().toString());
        } else {
            System.out.println("No se pudo completar la operación tras varios intentos.");
        }
    }

    // Método para manejar la respuesta de la API
    private void manejarRespuestaApi(String jsonResponse) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
        Map<String, Object> conversionRatesMap = gson.fromJson(conversionRates, Map.class);

        String moneda1 = obtenerMonedaValida(conversionRatesMap, "Ingrese el código de la moneda que desea convertir:");
        String moneda2 = obtenerMonedaValida(conversionRatesMap, "Ingrese el código de la moneda destino:");

        if (moneda1 != null && moneda2 != null) {
            caseFunction(moneda1, moneda2);
        }
    }

    private String obtenerMonedaValida(Map<String, Object> conversionRatesMap, String mensaje) {
        String moneda = "";
        boolean inputValido = false;

        while (!inputValido) {
            System.out.println(mensaje);
            moneda = teclado.nextLine().toUpperCase();

            // Validar si el código ingresado está en el JSON
            if (moneda != null && !moneda.trim().isEmpty() && moneda.length() == 3 && conversionRatesMap.containsKey(moneda)) {
                inputValido = true;
            } else {
                System.out.println("Código no válido. Ingrese un código de moneda válido de 3 letras.");
            }
        }

        return moneda;
    }

    public void salirOption() {
        System.out.println("¡Hasta pronto!");

        Historial historial = new Historial();
        try {
            historial.limpiarHistorial();
        } catch (IOException e) {
            System.out.println("Error al limpiar el historial: " + e.getMessage());
        }

        teclado.close();
    }

    public void optionSeven() {
        try {
            Historial historial = new Historial();
            historial.mostrarHistorial();
        } catch (IOException e) {
            System.out.println("Error al leer el historial de conversiones.");
        }
    }

    public int validaOptionCase(Scanner teclado) {
        boolean inputValido = false;
        int option = -1;

        while (!inputValido) {
            System.out.println("""
                **************Conversor de monedas**********************
                
                1) Peruvian Sol(PEN)  --->  United States Dollar(USD).
                2) United States(USD) --->  Peruvian Sol(PEN).
                3) Peruvian Sol(PEN)  --->  Euro(EUR).
                4) Euro(EUR)          --->  Peruvian Sol(PEN)
                5) Personalized.
                6) Buscar codigo.
                7) Historial de conversión.
                8) Salir.
                
                *********************************************************
                """);

            System.out.print("Seleccione una opción: ");

            if (teclado.hasNextInt()) {
                option = teclado.nextInt();
                teclado.nextLine();

                if (option >= 1 && option <= 8) {
                    inputValido = true;
                } else {
                    System.out.println("Por favor, ingrese una opción válida entre 1 y 8.");
                }
            } else {
                System.out.println("Error: Solo se permiten números. Inténtalo de nuevo.");
                teclado.next();
            }
        }

        return option;
    }

    public void optionSix() {
        try {
            String pais;
            System.out.println("Ingrese el país para obtener su código:");
            pais = teclado.nextLine();

            // Validar si el país ingresado es un número
            if (pais.matches(".*\\d.*")) {
                System.out.println("El país ingresado no puede contener números.");
                return; // Salir del método si hay un error
            }

            // Obtener el código de moneda
            String codigoMoneda = country.obtenerCodigoMoneda(pais);
            if (codigoMoneda != null) {
                System.out.println("Código de moneda de " + pais + ": " + codigoMoneda);
            } else {
                System.out.println("No se encontró información de moneda para " + pais);
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



}
