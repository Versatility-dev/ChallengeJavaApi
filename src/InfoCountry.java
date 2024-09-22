public record InfoCountry(String common,
                          String nameMoneda ,
                          String symbol) {

    @Override
    public String toString() {
        return "País: " + common + "\n" +
                "Moneda: " + nameMoneda + " (" + symbol +")\n";
    }
}
