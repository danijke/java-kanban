package exception;

public class InteractedException extends RuntimeException {
    public InteractedException() {
        super("задача пересекается по времени с существующими");
    }
}
