package cz.cuni.mff.diff;

import java.util.List;

public class GroupedOperation {
    public final Operator operator;
    public final Range rangeInFile1;
    public final Range rangeInFile2;
    public final List<String> linesToAdd;
    public final List<String> linesToDelete;
    public final List<String> linesToCopy;
    public GroupedOperation(Operator operator, Range rangeInFile1, Range rangeInFile2,
                            List<String> linesToAdd, List<String> linesToDelete, List<String> linesToCopy) {
        this.operator = operator;
        this.rangeInFile1 = rangeInFile1;
        this.rangeInFile2 = rangeInFile2;
        this.linesToAdd = linesToAdd;
        this.linesToDelete = linesToDelete;
        this.linesToCopy = linesToCopy;
    }
    public static GroupedOperation Add(Range rangeInFile1, Range rangeInFile2, List<String> linesToAdd) {
        return new GroupedOperation(Operator.ADD, rangeInFile1, rangeInFile2, linesToAdd, null, null);
    }
    public static GroupedOperation Delete(Range rangeInFile1, Range rangeInFile2, List<String> linesToDelete) {
        return new GroupedOperation(Operator.DELETE, rangeInFile1, rangeInFile2, null, linesToDelete, null);
    }
    public static GroupedOperation Copy(Range rangeInFile1, Range rangeInFile2, List<String> linesToCopy) {
        return new GroupedOperation(Operator.COPY, rangeInFile1, rangeInFile2, null, null, linesToCopy);
    }
    public static GroupedOperation Change(Range rangeInFile1, Range rangeInFile2, List<String> linesToAdd, List<String> linesToDelete) {
        return new GroupedOperation(Operator.CHANGE, rangeInFile1, rangeInFile2, linesToAdd, linesToDelete, null);
    }
}
