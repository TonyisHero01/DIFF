package cz.cuni.mff.diff;

import java.util.ArrayList;
import java.util.List;

public class OperationList implements Comparable<OperationList>{
    public final List<Operation> operations;
    public final int editDistance;
    public OperationList(List<Operation> operations, int editDistance) {
        this.operations = operations;
        this.editDistance = editDistance;
    }
    public static OperationList join(OperationList operationList1, OperationList operationList2) {
        List<Operation> newOperations = new ArrayList<>();
        newOperations.addAll(operationList1.operations);
        newOperations.addAll(operationList2.operations);
        return new OperationList(newOperations, operationList1.editDistance + operationList2.editDistance);
    }
    @Override
    public int compareTo(OperationList other) {
        return Integer.compare(this.editDistance, other.editDistance);
    }
}
