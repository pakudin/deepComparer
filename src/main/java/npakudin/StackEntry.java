package npakudin;

public class StackEntry {

    private String name;
    private Object expected;
    private Object actual;

    public StackEntry(String name, Object expected, Object actual) {
        this.name = name;
        this.expected = expected;
        this.actual = actual;
    }

    public String getName() {
        return name;
    }

    public Object getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }
}
