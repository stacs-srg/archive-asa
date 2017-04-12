package uk.ac.stand.dcs.asa.util;

public class Assert {
    
    /**
     * @param condition a condition that is asserted to be true
     * @param message a message to be displayed if the assertion fails
     */
    public static void assertion(boolean condition, String message) {

        if (!condition) {
            System.out.println(message);
            System.exit(-1);
        }
    }
}