/*
 * Created on Feb 7, 2005 at 10:08:06 PM.
 */
package uk.ac.stand.dcs.asa.experiments.gloss;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.math.BigInteger;
//import java.util.Random;
//
//import uk.ac.stand.dcs.asa.interfaces.Key;
//import uk.ac.stand.dcs.asa.simulation.util.SimProgressWindow;
//import uk.ac.stand.dcs.asa.util.Assert;
//import uk.ac.stand.dcs.asa.util.KeyImpl;
//import uk.ac.stand.dcs.asa.util.SegmentArithmetic;
//import uk.ac.stand.dcs.gloss.model.space.LatLongCoordinate;

/**
 * Experiments with location-based keys.
 *
 * @author graham
 */
public class GeographicalKeys {
    
//    private static int SEED = 34827435;
//    
//    private static Random random = new Random(SEED);
//
//    private static final double MIN_LATITUDE = -90.0;
//
//    private static final double LATITUDE_RANGE = 180.0;
//
//    private static final double MIN_LONGITUDE = -180.0;
//
//    private static final double LONGITUDE_RANGE = 360.0;
//    
//    private static final int PRECISION = 7;
//
//    private static final double MULTIPLIER_DOUBLE = Math.pow(10.0, PRECISION);
//    
//    private static final BigInteger MULTIPLIER_BIG_INTEGER = new BigInteger(String.valueOf((int)MULTIPLIER_DOUBLE));
//
//    public static void main(String[] args) {
//
//        filterResults("separations6.txt", "separations6filtered.txt", 593);
//    }
//    
//    public static void testFingerLocations() {
//        
//    }
//    
//    public static void testAllSeparations(int coordinate_size, String description) {
//        
//		File sourceFile = new File("separations" + coordinate_size + ".txt");
//
//		PrintWriter printWriter = null;
//        try {
//            printWriter = new PrintWriter (new FileOutputStream (sourceFile));
//        } catch (FileNotFoundException e) {
//            
//            System.out.println("can't open file");
//        }
//        
//        int edge_size = (int)Math.pow(2, coordinate_size);
//        BigInteger keyspace_size = new BigInteger(String.valueOf(edge_size)).pow(2);
//        
//        SimProgressWindow progress = new SimProgressWindow("Calculating Separations for square with edge " + edge_size + "...", 0, edge_size);
//        
//        printWriter.println(description);
//        printWriter.println("coordinate size in bits: " + coordinate_size);
//        
//        for (int x1 = 0; x1 < edge_size; x1++) {
//            
//            for (int y1 = 0; y1 < edge_size; y1++)
//                for (int x2 = 0; x2 < edge_size; x2++)
//                    for (int y2 = 0; y2 < edge_size; y2++) {
//
//                        //System.out.print("(" + x1 + "," + y1 + ") (" + x2 + "," + y2 + ") -> ");
//                        testSeparation(x1, y1, x2, y2, coordinate_size * 2, keyspace_size, printWriter);
//                    }
//            
//            progress.incrementProgress();
//        }
//        
//		printWriter.flush();
//		printWriter.close();
//		progress.dispose();
//    }
//    
//    public static void filterResults(String input_file_name, String output_file_name, int stride) {
//        
//        File inputFile = new File(input_file_name);
//        File outputFile = new File(output_file_name);
//        
//        BufferedReader bufferedReader = null;
//        PrintWriter printWriter = null;
//        try {
//            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
//            printWriter = new PrintWriter (new FileOutputStream (outputFile));
//        } catch (FileNotFoundException e) {
//            
//            System.out.println("can't open file: " + e.getMessage());
//        }
//        
//        try {
//            printWriter.println(bufferedReader.readLine());
//            printWriter.println(bufferedReader.readLine());
//            printWriter.println("stride value: " + stride);
//            
//            String line = "";
//            while (line != null) {
//                
//                printWriter.println(bufferedReader.readLine());
//                
//                for (int i = 0; i < stride - 1; i++)
//                    line = bufferedReader.readLine();
//            }
//            
//            bufferedReader.close();
//            
//        } catch (IOException e) {
//            System.out.println("exception: " + e);
//        }
//        
//        printWriter.flush();
//        printWriter.close();
//    }
//    
//    public static void testSeparations() {
//        
//        int NUMBER_OF_ITERATIONS = 1000;
//        
//        for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
//
//            LatLongCoordinate c1 = randomCoordinate();
//            LatLongCoordinate c2 = randomCoordinate();
//            
//            testSeparation(c1, c2);
//        }
//    }
//    
//    private static void testSeparation(int x1, int y1, int x2, int y2, int coordinate_size, BigInteger keyspace_size, PrintWriter writer) {
//        
//        Key k1 = physicalLocationToKey(x1, y1, coordinate_size);
//        Key k2 = physicalLocationToKey(x2, y2, coordinate_size);
//
//        //System.out.print(keyAsBinaryString(k1, coordinate_size) + " " + keyAsBinaryString(k2, coordinate_size) + " -> ");
//        
//        double key_distance = keySeparationAsRingFraction(k1, k2, keyspace_size);
//        int physical_distance = physicalSeparation(x1, y1, x2, y2);
//        
//        writer.println(physical_distance + "\t" + key_distance);
//    }
//
//    public static String toBinaryString(Key k, int number_of_bits) {
//        
//        return toBinaryString(k.bigIntegerRepresentation(), number_of_bits);
//    }
//    
//    public static String toBinaryString(int i, int number_of_bits) {
//        
//        return toBinaryString(new BigInteger(String.valueOf(i)), number_of_bits);
//    }
//    
//    public static String toBinaryString(BigInteger big_int, int number_of_bits) {
//        
//        return pad(big_int.toString(2), number_of_bits);
//    }
//    
//    
//    /**
//     * From description at http://mathworld.wolfram.com/GrayCode.html
//     * 
//     * @param binary_string a string representing a conventional binary number
//     * @return the Gray code equivalent
//     */
//    public static String binaryToGray(String binary_string) {
//        
//        StringBuffer result = new StringBuffer(binary_string);
//        
//        for (int i = binary_string.length() - 1; i > 0; i--) {
//            
//            if (result.charAt(i - 1) == '1') invertBit(result, i);
//        }
//        
//        return result.toString();
//    }
//    
//    public static String grayToBinary(String gray_string) {
//        
//        StringBuffer result = new StringBuffer(gray_string);
//        
//        for (int i = gray_string.length() - 1; i > 0; i--) {
//            
//            if (oddNumberOfOnes(result, i - 1)) invertBit(result, i);
//        }
//        
//        return result.toString();
//    }
//
//    public static boolean oddNumberOfOnes(StringBuffer result, int index) {
//        
//        return numberOfOnes(result, index) % 2 == 1;
//    }
//
//    public static int numberOfOnes(StringBuffer result, int index) {
//        
//        int count = 0;
//        for (int i = 0; i <= index; i++)
//            if (result.charAt(i) == '1') count++;
//        return count;
//    }
//
//    private static void invertBit(StringBuffer buffer, int index) {
//        
//        char current_bit = buffer.charAt(index);
//        if (current_bit == '1') buffer.setCharAt(index, '0');
//        else buffer.setCharAt(index, '1');
//    }
//    
//    private static void testSeparation(LatLongCoordinate c1, LatLongCoordinate c2) {
//        
//        Key k1 = physicalLocationToKey(c1);
//        Key k2 = physicalLocationToKey(c2);
//        
//        double key_distance = keySeparationAsRingFraction(k1, k2, SegmentArithmetic.KEYSPACE_SIZE);
//        int physical_distance = physicalSeparation(c1, c2);
//        
//        System.out.println(physical_distance + ", " + key_distance);
//    }
//
//    public static int physicalSeparation(LatLongCoordinate c1, LatLongCoordinate c2) {
//        
//        return (int)pythagoras(c1, c2);
//    }
//
//    private static int physicalSeparation(int x1, int y1, int x2, int y2) {
//
//        return (int)Math.round(pythagoras(x1, y1, x2, y2));
//    }
//
//    public static double keySeparationAsRingFraction(Key k1, Key k2, BigInteger keyspace_size) {
//        
//        BigInteger key_distance = keyDistance(k1, k2);
//        int inflated_fraction = key_distance.multiply(MULTIPLIER_BIG_INTEGER).divide(keyspace_size.subtract(BigInteger.ONE)).intValue();
//
//        return inflated_fraction / MULTIPLIER_DOUBLE;
//    }
//
//    public static BigInteger keyDistance(Key k1, Key k2) {
//        
//        return k1.bigIntegerRepresentation().subtract(k2.bigIntegerRepresentation()).abs();
//        
//        //return k1.ringDistanceTo(k2).min(k2.ringDistanceTo(k1));
//    }
//
//    public static double pythagoras(LatLongCoordinate c1, LatLongCoordinate c2) {
//        
//        return pythagoras(c1.getLatitude(), c1.getLongitude(), c2.getLatitude(), c2.getLongitude());
//    }
//
//    public static double pythagoras(double x1, double y1, double x2, double y2) {
//        
//        double x_difference = x1 - x2;
//        double y_difference = y1 - y2;
//        
//        return Math.sqrt(x_difference * x_difference + y_difference * y_difference);
//    }
//
//    public static Key physicalLocationToKey(LatLongCoordinate coord) {
//        
//        double latitude_fraction = (coord.getLatitude() - MIN_LATITUDE) / LATITUDE_RANGE;   // between 0 and 1
//        double longitude_fraction = (coord.getLongitude() - MIN_LONGITUDE) / LONGITUDE_RANGE;   // between 0 and 1
//        
//        int number_of_latitude_bits = SegmentArithmetic.KEY_LENGTH / 2;
//
//        String latitude_bits =  fractionToBits(latitude_fraction, number_of_latitude_bits);
//        String longitude_bits = fractionToBits(longitude_fraction, number_of_latitude_bits);
//        
//        return new KeyImpl(new BigInteger(interleave(latitude_bits, longitude_bits), 2));
//    }
//
//    private static Key physicalLocationToKey(int x, int y, int coordinate_size) {
//
//        String x_bits = binaryToGray(toBinaryString(x, coordinate_size));
//        String y_bits = binaryToGray(toBinaryString(y, coordinate_size));
//
//        return new KeyImpl(new BigInteger(interleave(x_bits, y_bits), 2));
//    }
//
////    private static Key physicalLocationToKey(int x, int y, int coordinate_size) {
////
////        String x_bits = toBinaryString(x, coordinate_size);
////        String y_bits = toBinaryString(y, coordinate_size);
////
////        return new KeyImpl(new BigInteger(interleave(x_bits, y_bits), 2));
////    }
//
//    public static LatLongCoordinate keyToPhysicalLocation(Key k) {
//        
//        String key_bits = k.bigIntegerRepresentation().toString(2);
//        
//        String latitude_bits = oddBits(key_bits);
//        String longitude_bits = evenBits(key_bits);
//
//        int number_of_latitude_bits = SegmentArithmetic.KEY_LENGTH / 2;
//
//        double latitude_fraction = bitsToFraction(latitude_bits, number_of_latitude_bits);
//        double longitude_fraction = bitsToFraction(longitude_bits, number_of_latitude_bits);
//        
//        double latitude = fractionToLatitude(latitude_fraction);
//        double longitude = fractionToLongitude(longitude_fraction);
//        
//        return new LatLongCoordinate(latitude, longitude);
//    }
//    
//    public static String oddBits(String key_bits) {
//        
//        return alternateBits(key_bits, 0);
//    }
//
//    public static String evenBits(String key_bits) {
//        
//        return alternateBits(key_bits, 1);
//    }
//
//    private static String alternateBits(String key_bits, int start_index) {
//        
//        StringBuffer result = new StringBuffer();
//        for (int i = 0; i < key_bits.length() / 2; i++) result.append(key_bits.charAt(i * 2 + start_index));
//        return result.toString();
//    }
//
//    private static double fractionToLatitude(double latitude_fraction) {
//        
//        return MIN_LATITUDE + latitude_fraction * LATITUDE_RANGE;
//    }
//    
//    private static double fractionToLongitude(double longitude_fraction) {
//        
//        return MIN_LONGITUDE + longitude_fraction * LONGITUDE_RANGE;
//    }
//
//    public static String interleave(String s1, String s2) {
//        
//        Assert.assertion(s1.length() == s2.length(), "interleaving different length strings: " + s1 + " and " + s2);
//        
//        StringBuffer result = new StringBuffer(s1.length() * 2);
//        
//        for (int i = 0; i < s1.length(); i++) {
//
//            result.append(s1.charAt(i));
//            result.append(s2.charAt(i));
//        }
//        return result.toString();
//    }
//
//    public static String pad(String s, int number_of_bits) {
//        
//        StringBuffer result = new StringBuffer(s);
//        while (result.length() < number_of_bits) result.insert(0, "0");
//        return result.toString();
//    }
//
//    public static String fractionToBits(double fraction, int number_of_bits) {
//        
//        String inflated_fraction_string = String.valueOf((int)(fraction * MULTIPLIER_DOUBLE));
//        
//        BigInteger inflated_fraction_big_int = new BigInteger(inflated_fraction_string, 10);
//        
//        BigInteger inflated_value = SegmentArithmetic.TWO.pow(number_of_bits).multiply(inflated_fraction_big_int);
//        
//        BigInteger key_value = inflated_value.divide(MULTIPLIER_BIG_INTEGER);
//        
//        String bits = key_value.toString(2);
//        
//        return pad(bits, number_of_bits);
//    }
//
//    public static double bitsToFraction(String key_bits, int number_of_bits) {
//        
//        BigInteger key_value = new BigInteger(key_bits, 2);
//        BigInteger inflated_value = key_value.multiply(MULTIPLIER_BIG_INTEGER);
//        BigInteger inflated_fraction_big_int = inflated_value.divide(SegmentArithmetic.TWO.pow(number_of_bits));
//        String inflated_fraction_string = inflated_fraction_big_int.toString(10);
//        
//        return Integer.parseInt(inflated_fraction_string) / MULTIPLIER_DOUBLE;
//    }
//    
//    public static LatLongCoordinate randomCoordinate() {
//
//        double latitude = MIN_LATITUDE + random.nextDouble() * LATITUDE_RANGE;
//        double longitude = MIN_LONGITUDE + random.nextDouble() * LONGITUDE_RANGE;
//        
//        return new LatLongCoordinate(latitude, longitude);
//    }
}
