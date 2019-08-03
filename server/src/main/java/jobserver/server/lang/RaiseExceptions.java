package jobserver.server.lang;

public class RaiseExceptions {

    public static void raise(Exception e) {
        throw new RuntimeException(e);
    }
}
