package cz.cuni.mff.diff;

import java.util.List;

public class Block {
    public GroupedOperation previousContext;
    public List<GroupedOperation> change;
    public GroupedOperation nextContext;

    public Block(GroupedOperation previousContext, List<GroupedOperation> change, GroupedOperation nextContext) {
        this.previousContext = previousContext;
        this.change = change;
        this.nextContext = nextContext;
    }
    public Operator getOperator() {
        boolean areAllAdd = true;
        boolean areAllDelete = true;
        for (int i=0; i<change.size(); i+=2) {
            Operator operator = change.get(i).operator;
            if (operator == Operator.CHANGE) {
                return Operator.CHANGE;
            } else if (operator == Operator.ADD) {
                areAllDelete = false;
            } else if (operator == Operator.DELETE) {
                areAllAdd = false;
            }
        }
        if (areAllDelete) {
            return Operator.DELETE;
        } else if (areAllAdd) {
            return Operator.ADD;
        } else {
            return Operator.CHANGE;
        }
    }
}
