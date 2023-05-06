package cz.cuni.mff.diff;

import java.util.*;

public class DiffFinder {
    public final List<String> file1Lines;
    public final List<String> file2Lines;
    private HashMap<String, OperationList> memo = new HashMap<>();
    public DiffFinder(List<String> file1Lines, List<String> file2Lines) {
        this.file1Lines = file1Lines;
        this.file2Lines = file2Lines;
    }
    public static List<GroupedOperation> groupOperations(List<Operation> operations) {
        if (operations.isEmpty()) {
            return new ArrayList<>();
        }

        List<GroupedOperation> groupedOperations = new ArrayList<>();

        int operationsToGroupNumber = 1;
        for (int i=1; i<=operations.size(); i++) {
            Operation previousOperation = operations.get(i-1);
            Operation currentOperation = null;
            if (i < operations.size()) {
                currentOperation = operations.get(i);
            }
            if (currentOperation != null
                    && ((currentOperation.operator == Operator.COPY) == (previousOperation.operator == Operator.COPY))) {
                operationsToGroupNumber++;
            }
            else {
                int startIndex = i - operationsToGroupNumber;
                int endIndex = i;
                Range range1 = new Range(operations.get(startIndex).lineNumberInFile1, operations.get(endIndex-1).lineNumberInFile1);
                Range range2 = new Range(operations.get(startIndex).lineNumberInFile2, operations.get(endIndex-1).lineNumberInFile2);
                List<Operation> operationsToGroup = operations.subList(startIndex, endIndex);
                Operator operator = getOperator(operationsToGroup);
                List<String> linesToAdd = new ArrayList<>();
                List<String> linesToDelete = new ArrayList<>();
                List<String> linesToCopy = new ArrayList<>();
                addLines(operationsToGroup, linesToAdd, linesToDelete, linesToCopy);
                GroupedOperation groupedOperation = null;
                switch (operator) {
                    case COPY:
                        groupedOperation = GroupedOperation.Copy(range1, range2, linesToCopy);
                        break;
                    case ADD:
                        groupedOperation = GroupedOperation.Add(range1, range2, linesToAdd);
                        break;
                    case DELETE:
                        groupedOperation = GroupedOperation.Delete(range1, range2, linesToDelete);
                        break;
                    case CHANGE:
                        groupedOperation = GroupedOperation.Change(range1, range2, linesToAdd, linesToDelete);
                        break;
                }
                groupedOperations.add(groupedOperation);
                operationsToGroupNumber = 1;
            }
        }
        return groupedOperations;
    }
    private static void addLines(List<Operation> operations, List<String> linesToAdd, List<String> linesToDelete, List<String> linesToCopy) {
        for (Operation operation : operations) {
            switch (operation.operator) {
                case COPY:
                    linesToCopy.add(operation.lineToCopy);
                    break;
                case ADD:
                    linesToAdd.add(operation.lineToAdd);
                    break;
                case DELETE:
                    linesToDelete.add(operation.lineToDelete);
                    break;
                case CHANGE:
                    linesToAdd.add(operation.lineToAdd);
                    linesToDelete.add(operation.lineToDelete);
                    break;
            }
        }
    }
    private static Operator getOperator(List<Operation> operations) {
        Operator operator = operations.get(0).operator;
        for (Operation op : operations) {
            if (op.operator != operator) {
                return Operator.CHANGE;
            }
        }
        return operator;
    }
    public List<Operation> findDiffs() {
        return findDiffs(0, 0).operations;
    }
    private OperationList findDiffs(int index1, int index2) {
        String indicesInString = convertIndicesToString(index1, index2);
        if (memo.containsKey(indicesInString)) {
            return memo.get(indicesInString);
        }
        List<Operation> operations = new ArrayList<>();
        OperationList operationList;
        if (file1Lines.size() - index1 == 0) {
            for (int i = index2; i < file2Lines.size(); i++) {
                Operation operation = Operation.Add(index1-1, i, file2Lines.get(i));
                operations.add(operation);
            }
            operationList = new OperationList(operations, operations.size());
        } else if (file2Lines.size() - index2 == 0) {
            for (int i = index1; i < file1Lines.size(); i++) {
                Operation operation = Operation.Delete(i, index2-1, file1Lines.get(i));
                operations.add(operation);
            }
            operationList = new OperationList(operations, operations.size());
        } else if (file1Lines.get(index1).equals(file2Lines.get(index2))) {
            Operation operation = Operation.Copy(index1, index2, file1Lines.get(index1));
            operations.add(operation);

            OperationList newOperationList = findDiffs(index1+1, index2+1);
            operations.addAll(newOperationList.operations);
            operationList = new OperationList(operations, 0+newOperationList.editDistance);
        } else {
            List<Operation> operations1 = Collections.singletonList(
                    Operation.Delete(index1, index2-1, file1Lines.get(index1))
            );
            OperationList operationList1a = new OperationList(operations1, 1);
            OperationList operationList1b = findDiffs(index1+1, index2);
            OperationList operationList1 = OperationList.join(operationList1a, operationList1b);

            List<Operation> operations2 = Collections.singletonList(
                    Operation.Add(index1-1, index2, file2Lines.get(index2))
            );
            OperationList operationList2a = new OperationList(operations2, 1);
            OperationList operationList2b = findDiffs(index1, index2+1);
            OperationList operationList2 = OperationList.join(operationList2a, operationList2b);

            List<Operation> operations3 = Collections.singletonList(
                    Operation.Change(index1, index2, file1Lines.get(index1), file2Lines.get(index2))
            );
            OperationList operationList3a = new OperationList(operations3, 1);
            OperationList operationList3b = findDiffs(index1+1, index2+1);
            OperationList operationList3 = OperationList.join(operationList3a, operationList3b);
            List<OperationList> operationLists = new ArrayList<OperationList>(Arrays.asList(operationList1, operationList2, operationList3));

            operationList = Collections.min(operationLists);
        }
        memo.put(indicesInString, operationList);
        return operationList;
    }
    private String convertIndicesToString(int index1, int index2) {
        return index1 + " " + index2;
    }
}
