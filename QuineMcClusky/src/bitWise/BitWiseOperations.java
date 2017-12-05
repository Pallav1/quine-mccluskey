package bitWise;

public class BitWiseOperations {

    public static int getBinaryWeight(int num) {
        int weight = 0;
        while (num > 0) {
            int result = num & 1;
            weight = weight + result;
            num = num >> 1;
        }
        return weight;
    }

    public static String getBinaryStringRepresentation(int intValue, int numOfBits) {
        int num = intValue;
        String strRep = "";
        while (num > 0) {
            int rem = num % 2;
            strRep = rem + strRep;
            num = num / 2;
        }
        // to append zeros
        if (strRep.length() < numOfBits) {
            int length = strRep.length();
            for (int i = 0; i < numOfBits - length; i++) {
                strRep = "0" + strRep;
            }
        }
        return strRep;
    }
}
