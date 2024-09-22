import java.text.DecimalFormat;

public record Monedas(String base_code,
                      String target_code,
                      double conversion_rate,
                      double conversion_result,
                      String time_last_update_utc) {


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.####");
        return "******************* - Resultado - **************************\n" +
                "Moneda : " + base_code + "\n" +
                "Moneda destino : " + target_code + "\n" +
                "Tasa de Conversión : " + df.format(conversion_rate) + "("+base_code+")\n" +
                "Resultado conversión : " + df.format(conversion_result) +"("+target_code+")\n" +
                "Última actualización del valor : " + time_last_update_utc + "\n" +
                "**********************************************************\n";
    }

    public String toString(InfoCountry paisBase, InfoCountry paisDestino) {


        DecimalFormat df = new DecimalFormat("#.####");

        return "******************* - Resultado - **************************\n" +
                "-("+base_code+")-"+
                paisBase +
                "-("+target_code+")-"+
                paisDestino +
                "Tasa de Conversión : " + df.format(conversion_rate) + " (" + base_code + ")\n" +
                "Resultado conversión : " + df.format(conversion_result) + " (" + target_code + ")\n" +
                "Última actualización del valor : " + time_last_update_utc + "\n" +
                "**********************************************************\n";
    }
}
