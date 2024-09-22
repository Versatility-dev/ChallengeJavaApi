import java.io.FileInputStream;
import java.io.IOException;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Optional;
import java.util.Properties;

public class ConvierteMoneda {

    private String key;
    private HttpRequestHelper httpHelper = new HttpRequestHelper();

    public String getKey() {
        return key;
    }

    public ConvierteMoneda() {
        loadApiKey();
    }

    private void loadApiKey() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            key = properties.getProperty("api.key");
        } catch (IOException e) {
            System.out.println("Error al cargar la clave de la API: " + e.getMessage());
        }
    }

    public Optional<Monedas> convierteMonedasOpcion(String moneda1, String moneda2, double cantidad) {
        URI direccion = construirURI(moneda1, moneda2, cantidad);
        HttpRequest request = httpHelper.crearHttpRequest(direccion);

        return httpHelper.enviarPeticionYProcesarRespuesta(request, Monedas.class);
    }

    private URI construirURI(String moneda1, String moneda2, double cantidad) {
        return URI.create("https://v6.exchangerate-api.com/v6/" + key + "/pair/" + moneda1 + "/" + moneda2 + "/" + cantidad);
    }







}
