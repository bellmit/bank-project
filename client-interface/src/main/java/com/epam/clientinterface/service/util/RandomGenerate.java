package com.epam.clientinterface.service.util;

import java.util.Random;

public class RandomGenerate {

    public static String generateCardNumber() {
        return randomGenerateStringOfInt(16);
    }

    public static String generatePinCode() {
        return randomGenerateStringOfInt(4);
    }

    public static String generateAccountNumber() {
        return randomGenerateStringOfInt(20);
    }

    protected static String randomGenerateStringOfInt(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            builder.append(digit);
        }
        return builder.toString();
    }
}
