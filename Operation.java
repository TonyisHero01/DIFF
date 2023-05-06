package cz.cuni.mff.diff;

public class Operation {
    public final Operator operator;
    public final int lineNumberInFile1;
    public final int lineNumberInFile2;
    public final String lineToAdd;
    public final String lineToDelete;
    public final String lineToCopy;
    public Operation(Operator operator, int lineNumberInFile1, int lineNumberInFile2, String lineToAdd, String lineToDelete, String lineToCopy) {
        this.operator = operator;
        this.lineNumberInFile1 = lineNumberInFile1;
        this.lineNumberInFile2 = lineNumberInFile2;
        this.lineToAdd = lineToAdd;
        this.lineToDelete = lineToDelete;
        this.lineToCopy = lineToCopy;
    }
    public static Operation Add(int lineNumberInFile1, int lineNumberInFile2, String lineToAdd) {
        return new Operation(Operator.ADD, lineNumberInFile1, lineNumberInFile2, lineToAdd, null, null);
    }
    public static Operation Delete(int lineNumberInFile1, int lineNumberInFile2, String lineToDelete) {
        return new Operation(Operator.DELETE, lineNumberInFile1, lineNumberInFile2, null, lineToDelete, null);
    }
    public static Operation Copy(int lineNumberInFile1, int lineNumberInFile2, String lineToCopy) {
        return new Operation(Operator.COPY, lineNumberInFile1, lineNumberInFile2, null, null, lineToCopy);
    }
    public static Operation Change(int lineNumberInFile1, int lineNumberInFile2, String lineToDelete, String lineToAdd) {
        return new Operation(Operator.CHANGE, lineNumberInFile1, lineNumberInFile2, lineToAdd, lineToDelete, null);
    }
}
