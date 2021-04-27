package generator;

public class GUID {

    public static String generate() {
        return java.util.UUID.randomUUID().toString();
    }
}
