package exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String msg, IOException e) {
        super(msg, e);
    }
}
