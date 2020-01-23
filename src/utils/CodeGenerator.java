package utils;

public class CodeGenerator {
    private String code;

    /* generates a random code of 5 digits, from 0-9, calling the generate method of the
     * RandomStringGenerator class*/
    public CodeGenerator() {
        String digits = "0123456789";
        RandomStringGenerator random = new RandomStringGenerator(5, digits);
        code = random.generateCode();
    }

    /* returns the generated code*/
    public String getCode() {
        return "V" + code;
    }
}
