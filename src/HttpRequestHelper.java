import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

public class HttpRequestHelper {

    private static final int MAX_REINTENTOS = 5;
    private static final int RETARDO_BASE_MS = 3000;

    public HttpClient crearHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public HttpRequest crearHttpRequest(URI direccion) {
        return HttpRequest.newBuilder()
                .uri(direccion)
                .timeout(Duration.ofSeconds(10))
                .build();
    }

    public <T> Optional<T> enviarPeticionYProcesarRespuesta(HttpRequest request, Class<T> claseRespuesta) {
        int intentos = 0;
        boolean exito = false;

        while (intentos < MAX_REINTENTOS && !exito) {
            intentos++;
            try {
                HttpClient client = crearHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    T resultado = new Gson().fromJson(response.body(), claseRespuesta);
                    return Optional.of(resultado);

                } else if (response.statusCode() >= 500 && response.statusCode() < 600) {
                    System.out.println("Error del servidor: " + response.statusCode() + ". Intentando de nuevo...");
                } else {
                    System.out.println("Error: No se pudo obtener una respuesta válida de la API. Código: " + response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Error de conexión o tiempo de espera agotado. Intentando de nuevo...");
            }

            esperarReintento(intentos);
        }

        System.out.println("Error: No se pudo realizar la operación después de " + intentos + " intentos.");
        return Optional.empty();
    }

    private void esperarReintento(int intentos) {
        try {
            Thread.sleep(RETARDO_BASE_MS * intentos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("El hilo fue interrumpido.");
        }
    }
}
