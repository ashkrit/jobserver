package jobserver.server.lang;

public class Exceptions {

    public static void raise(Exception e) {
        throw new RuntimeException(e);
    }
}
