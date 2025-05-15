package com.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberPOJOPermutations {
    public static void main(String[] args) {
        NumberPOJOPermutations generator = new NumberPOJOPermutations();
        generator.generatePermutations();
    }

    public void generatePermutations() {
        // Define value sets for each field
        byte[] byteValues = {0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE};
        short[] shortValues = {0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE};
        int[] intValues = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE};
        long[] longValues = {0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE};
        float[] floatValues = {0.0f, 1.5f, -1.5f, Float.MIN_VALUE, Float.MAX_VALUE};
        double[] doubleValues = {0.0, 1.5, -1.5, Double.MIN_VALUE, Double.MAX_VALUE};
        Byte[] wrapperByteValues = {0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE, null};
        Short[] wrapperShortValues = {0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE, null};
        Integer[] wrapperIntegerValues = {0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, null};
        Long[] wrapperLongValues = {0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, null};
        Float[] wrapperFloatValues = {0.0f, 1.5f, -1.5f, Float.MIN_VALUE, Float.MAX_VALUE, null};
        Double[] wrapperDoubleValues = {0.0, 1.5, -1.5, Double.MIN_VALUE, Double.MAX_VALUE, null};
        BigDecimal[] bigDecimalValues = {
            BigDecimal.ZERO,
            BigDecimal.ONE,
            BigDecimal.ONE.negate(),
            BigDecimal.valueOf(Double.MAX_VALUE),
            BigDecimal.valueOf(-Double.MAX_VALUE),
            null
        };

        List<NumberPOJO> pojos = new ArrayList<>();
        int maxPermutations = 10000; // Limit to prevent excessive memory usage
        int[] indices = new int[13]; // Track current index for each field
        int totalGenerated = 0;

        while (totalGenerated < maxPermutations) {
            NumberPOJO pojo = new NumberPOJO();
            pojo.setPrimitiveByte(byteValues[indices[0]]);
            pojo.setPrimitiveShort(shortValues[indices[1]]);
            pojo.setPrimitiveInt(intValues[indices[2]]);
            pojo.setPrimitiveLong(longValues[indices[3]]);
            pojo.setPrimitiveFloat(floatValues[indices[4]]);
            pojo.setPrimitiveDouble(doubleValues[indices[5]]);
            pojo.setWrapperByte(wrapperByteValues[indices[6]]);
            pojo.setWrapperShort(wrapperShortValues[indices[7]]);
            pojo.setWrapperInteger(wrapperIntegerValues[indices[8]]);
            pojo.setWrapperLong(wrapperLongValues[indices[9]]);
            pojo.setWrapperFloat(wrapperFloatValues[indices[10]]);
            pojo.setWrapperDouble(wrapperDoubleValues[indices[11]]);
            pojo.setBigDecimal(bigDecimalValues[indices[12]]);

            pojos.add(pojo);
            printPOJO(pojo, totalGenerated);

            // Increment indices to generate next combination
            boolean carry = true;
            for (int i = 0; i < indices.length && carry; i++) {
                int maxIndex = i < 6 ? 5 : 6; // 5 for primitives, 6 for wrappers and BigDecimal
                indices[i]++;
                if (indices[i] >= maxIndex) {
                    indices[i] = 0;
                } else {
                    carry = false;
                }
            }
            if (carry) {
                break; // All combinations exhausted (though unlikely with limit)
            }
            totalGenerated++;
        }

        System.out.println("Total NumberPOJO instances generated: " + pojos.size());
    }

    private void printPOJO(NumberPOJO pojo, int index) {
        System.out.println("NumberPOJO #" + index + ":");
        System.out.println("  primitiveByte: " + pojo.getPrimitiveByte());
        System.out.println("  primitiveShort: " + pojo.getPrimitiveShort());
        System.out.println("  primitiveInt: " + pojo.getPrimitiveInt());
        System.out.println("  primitiveLong: " + pojo.getPrimitiveLong());
        System.out.println("  primitiveFloat: " + pojo.getPrimitiveFloat());
        System.out.println("  primitiveDouble: " + pojo.getPrimitiveDouble());
        System.out.println("  wrapperByte: " + pojo.getWrapperByte());
        System.out.println("  wrapperShort: " + pojo.getWrapperShort());
        System.out.println("  wrapperInteger: " + pojo.getWrapperInteger());
        System.out.println("  wrapperLong: " + pojo.getWrapperLong());
        System.out.println("  wrapperFloat: " + pojo.getWrapperFloat());
        System.out.println("  wrapperDouble: " + pojo.getWrapperDouble());
        System.out.println("  bigDecimal: " + pojo.getBigDecimal());
    }
}