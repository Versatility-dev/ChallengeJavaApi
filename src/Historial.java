import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Historial {

    private static final String HISTORIAL_FILE = "historial.json";
    private Gson gson = new Gson();


    public void guardarConvercion(Monedas nuevaConversion) throws IOException {

        List<Monedas> historial;
        File archivo = new File(HISTORIAL_FILE);

        if (archivo.exists()) {
            // Leer el historial existente
            FileReader reader = new FileReader(archivo);
            Type tipoLista = new TypeToken<ArrayList<Monedas>>() {}.getType();
            historial = gson.fromJson(reader, tipoLista);
            reader.close();


            if (historial == null) {
                historial = new ArrayList<>();
            }

        } else {
            // Si el archivo no existe, creamos una nueva lista de historial
            historial = new ArrayList<>();
        }

        // Agregar la nueva conversión
        historial.add(nuevaConversion);

        // Guardar el historial actualizado en el archivo JSON
        FileWriter writer = new FileWriter(HISTORIAL_FILE);
        gson.toJson(historial, writer);
        writer.flush();
        writer.close();

        System.out.println("Conversión guardada exitosamente en el historial.");
    }


    // Método para leer y mostrar el historial
    public void mostrarHistorial() throws IOException {
        File archivo = new File(HISTORIAL_FILE);

        if (!archivo.exists()) {
            System.out.println("No hay historial de conversiones.");
            return;
        }

        // Leer el historial del archivo
        FileReader reader = new FileReader(archivo);
        Type tipoLista = new TypeToken<ArrayList<Monedas>>() {}.getType();
        List<Monedas> historial = gson.fromJson(reader, tipoLista);
        reader.close();


        if (historial == null || historial.isEmpty()) {
            System.out.println("El historial de conversiones está vacío.");
        } else {

            int i=0;
            System.out.println("~~~~~~~~~~~~~~~~~ Historial de Conversiones ~~~~~~~~~~~~~~~~~");
            System.out.println("N° de converciones : " + historial.size());
            for (Monedas conversion : historial) {
                i++;
                System.out.println("Conversion N° : " + i);
                System.out.println(conversion.toString());
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }

    public void limpiarHistorial() throws IOException {
        // Abrir el archivo en modo escritura pero sin añadir contenido (esto lo vacía)
        FileWriter writer = new FileWriter(HISTORIAL_FILE);
        writer.write("");  // Sobrescribir el archivo con una cadena vacía
        writer.flush();
        writer.close();

        System.out.println("El historial ha sido limpiado.");
    }


}

