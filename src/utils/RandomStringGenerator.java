package utils;

import java.util.Random;

class RandomStringGenerator {

    private Random generator = new Random();
    private int size;
    private char[] alphabet;

    RandomStringGenerator(int size, String digits) {
        this.size = size;
        this.alphabet = digits.toCharArray();
    }

    /* generates a random code of [size] characters with the given alphabet String*/
    String generateCode() {
        char[] code = new char[size];

        for (int i = 0; i < size; i++) {
            code[i] = alphabet[generator.nextInt(alphabet.length)];
        }

        return new String(code);
    }
}