package jobserver.client.lang;

public class Panic {

    public static void raise(Exception e, String errorMessage) {
        throw new RuntimeException(errorMessage, e);
    }

    public static void raise(Exception e) {
        throw new RuntimeException(e);
    }
}
