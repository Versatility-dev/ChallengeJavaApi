
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);
        Functions function = new Functions();


        while (true) {

            int option = function.validaOptionCase(teclado);

            switch (option) {
                case 1:
                    function.caseFunction("PEN", "USD");
                    break;
                case 2:
                    function.caseFunction("USD", "PEN");
                    break;
                case 3:
                    function.caseFunction("PEN", "EUR");
                    break;
                case 4:
                    function.caseFunction("EUR", "PEN");
                    break;
                case 5:
                    function.personalizedFuction();
                    break;
                case 6:
                    function.optionSix();
                    break;
                case 7:
                    function.optionSeven();
                    break;
                case 8:

                    function.salirOption();
                    return;

                default:
                    System.out.println("Opción no válida, intente de nuevo.");
                    break;
            }
        }
    }







}
