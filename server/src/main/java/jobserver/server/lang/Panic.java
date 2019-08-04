package jobserver.server.lang;

public class Panic {

    public static void raise(Exception e) {
        throw new RuntimeException(e);
    }
}
