package fs;

public class ForbiddenOperation extends Exception {
    public ForbiddenOperation() {
        super("Operation forbidden on this object instance");
    }
}
