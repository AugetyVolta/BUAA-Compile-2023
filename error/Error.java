package error;

public class Error implements Comparable<Error> {
    private final int line;

    private final ErrorType errorType;

    public Error(int line, ErrorType errorType) {
        this.line = line;
        this.errorType = errorType;
    }

    public int getLine() {
        return line;
    }

    public String toString() {
        return line +
                " " +
                ErrorType.ErrorType2Id.get(errorType) + "\n";
    }

    @Override
    public int compareTo(Error other) {
        return Integer.compare(line, other.getLine()); //从小到大顺序排列
    }
}
