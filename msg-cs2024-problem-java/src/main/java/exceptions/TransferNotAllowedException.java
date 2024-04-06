package exceptions;

public class TransferNotAllowedException extends RuntimeException {
    public TransferNotAllowedException(String message) {
        super(message);
    }
}
