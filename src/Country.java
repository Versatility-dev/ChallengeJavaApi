import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.Optional;

public class Country {

    private final HttpRequestHelper httpRequestHelper = new HttpRequestHelper();

    public InfoCountry obtenerPaisPorMoneda(String codigoMoneda) throws Exception {
        String apiUrl = "https://restcountries.com/v3.1/currency/" + codigoMoneda;
        HttpRequest request = httpRequestHelper.crearHttpRequest(URI.create(apiUrl));

        Optional<JsonArray> optionalResponse = httpRequestHelper.enviarPeticionYProcesarRespuesta(request, JsonArray.class);

        if (optionalResponse.isPresent()) {
            JsonArray jsonArray = optionalResponse.get();
            JsonObject paisInfo = jsonArray.get(0).getAsJsonObject();
            JsonObject monedaInfo = paisInfo.getAsJsonObject("currencies").getAsJsonObject(codigoMoneda);

            String nombreComun = paisInfo.getAsJsonObject("name").get("common").getAsString();
            String nombreMoneda = monedaInfo.get("name").getAsString();
            String simbolo = monedaInfo.get("symbol").getAsString();

            // Reemplazo de nombre
            if (nombreComun.equals("French Guiana")) {
                nombreComun = "European Union";
            }
            if(nombreComun.equals("Guam")){
                nombreComun="United States";
            }


            return new InfoCountry(nombreComun, nombreMoneda, simbolo);
        } else {
            throw new Exception("Error al obtener los datos del país para la moneda: " + codigoMoneda);
        }
    }

    public String obtenerCodigoMoneda(String pais) {
        try {
            String urlString = "https://restcountries.com/v3.1/name/" + pais;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Verificar el código de respuesta HTTP
            if (conn.getResponseCode() == 404) {
                System.out.println("País no encontrado: " + pais);

                return null;

            } else if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Error en la conexión: " + conn.getResponseCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Procesar la respuesta JSON
            JsonArray countries = JsonParser.parseString(response.toString()).getAsJsonArray();
            JsonObject country = countries.get(0).getAsJsonObject();

            if (country.has("currencies")) {
                JsonObject currencies = country.getAsJsonObject("currencies");
                String codigoMoneda = currencies.keySet().iterator().next();

                return codigoMoneda;
            }
        } catch (Exception e) {
            System.out.println("Error al obtener el código de moneda: " + e.getMessage());
        }
        return null;
    }

}
