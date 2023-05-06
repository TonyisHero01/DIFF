package cz.cuni.mff.diff;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ContextModePrinter {
    private final List<GroupedOperation> groupedOperations;
    private final int contextSize;
    private final Path file1Path;
    private final Path file2Path;

    private static final Map<Operator, Character> SYMBOLS_OF_OPERATORS = Map.of(
            Operator.ADD, '+',
            Operator.DELETE, '-',
            Operator.CHANGE, '!',
            Operator.COPY, ' '
    );
    public ContextModePrinter(List<GroupedOperation> groupedOperations, int contextSize, Path file1Path, Path file2Path) {
        this.groupedOperations = groupedOperations;
        this.contextSize = contextSize;
        this.file1Path = file1Path;
        this.file2Path = file2Path;
    }
    private static String getLastModified(Path filePath) throws IOException {
        FileTime fileTime = Files.getLastModifiedTime(filePath);
        Date date = new Date(fileTime.toMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS Z");
        return format.format(date);
    }
    private List<Block> joinGroups() {
        List<Block> blocks = new ArrayList<>();
        int start = (groupedOperations.get(0).operator == Operator.COPY) ? 1 : 0;

        for (int i=start; i<groupedOperations.size(); i+=2) {
            GroupedOperation currentGroup = groupedOperations.get(i);
            GroupedOperation previousContext = (i-1 >= 0) ? groupedOperations.get(i-1) : null;
            List<GroupedOperation> change = new ArrayList<>();
            change.add(currentGroup);

            while (i+2 < groupedOperations.size() && contextSize * 2 >= groupedOperations.get(i+1).linesToCopy.size()) {
                change.add(groupedOperations.get(i+1));
                change.add(groupedOperations.get(i+2));
                i += 2;
            }
            GroupedOperation nextContext = (i+1 < groupedOperations.size()) ? groupedOperations.get(i+1) : null;
            Block block = new Block(previousContext, change, nextContext);
            blocks.add(block);
        }
        return blocks;
    }
    public void print() throws IOException {
        System.out.printf("*** %s %s\n", file1Path.getFileName(), getLastModified(file1Path));
        System.out.printf("--- %s %s\n", file2Path.getFileName(), getLastModified(file2Path));

        List<Block> blocks = joinGroups();

        for (Block block : blocks) {

            System.out.println("******************");
            printFile1(block);
            printFile2(block);
        }
    }
    private void printFile1(Block block){
        int startIndexOfPreviousContext = -1;
        int endIndexOfPreviousContext = -1;
        int firstLineNumberOfPreviousContext;
        if (block.previousContext != null) {
            startIndexOfPreviousContext = Math.max(block.previousContext.linesToCopy.size()-contextSize, 0);
            firstLineNumberOfPreviousContext = block.previousContext.rangeInFile1.start + startIndexOfPreviousContext;
            endIndexOfPreviousContext = block.previousContext.linesToCopy.size();
        } else {
            firstLineNumberOfPreviousContext = 0;
        }
        int sizeOfNextContext;
        int startIndexOfNextContext = -1;
        int endIndexOfNextContext = -1;
        if (block.nextContext != null) {
            startIndexOfNextContext = 0;
            endIndexOfNextContext = Math.min(contextSize, block.nextContext.linesToCopy.size());
            sizeOfNextContext = endIndexOfNextContext - startIndexOfNextContext;
        } else {
            sizeOfNextContext = 0;
        }
        int rangeEnd = block.change.get(block.change.size()-1).rangeInFile1.end + sizeOfNextContext;
        Range range = new Range(firstLineNumberOfPreviousContext, rangeEnd);
        System.out.printf("*** %s ****\n", range.shiftByOne());
        if (block.getOperator() != Operator.ADD) {
            if (block.previousContext != null) {
                for (int j=startIndexOfPreviousContext; j<endIndexOfPreviousContext; j++) {
                    System.out.printf("%s %s\n", SYMBOLS_OF_OPERATORS.get(Operator.COPY), block.previousContext.linesToCopy.get(j));
                }
            }
            for (GroupedOperation g : block.change) {
                if (g.operator == Operator.ADD) {
                    continue;
                }
                Character symbolOfOperator = SYMBOLS_OF_OPERATORS.get(g.operator);

                List<String> lines = (g.operator == Operator.COPY) ? g.linesToCopy : g.linesToDelete;

                for (String line : lines) {
                    System.out.println(symbolOfOperator + " " + line);
                }

            }
            if (block.nextContext != null) {
                for (int j=startIndexOfNextContext; j<endIndexOfNextContext; j++) {
                    System.out.printf("%s %s\n", SYMBOLS_OF_OPERATORS.get(Operator.COPY), block.nextContext.linesToCopy.get(j));
                }
            }
        }
    }
    private void printFile2(Block block){
        int startIndexOfPreviousContext = -1;
        int endIndexOfPreviousContext = -1;
        int firstLineNumberOfPreviousContext;
        if (block.previousContext != null) {
            startIndexOfPreviousContext = Math.max(block.previousContext.linesToCopy.size()-contextSize, 0);
            firstLineNumberOfPreviousContext = block.previousContext.rangeInFile2.start + startIndexOfPreviousContext;
            endIndexOfPreviousContext = block.previousContext.linesToCopy.size();
        } else {
            firstLineNumberOfPreviousContext = 0;
        }
        int sizeOfNextContext;
        int startIndexOfNextContext = -1;
        int endIndexOfNextContext = -1;
        if (block.nextContext != null) {
            startIndexOfNextContext = 0;
            endIndexOfNextContext = Math.min(contextSize, block.nextContext.linesToCopy.size());
            sizeOfNextContext = endIndexOfNextContext - startIndexOfNextContext;
        } else {
            sizeOfNextContext = 0;
        }
        int rangeEnd = block.change.get(block.change.size()-1).rangeInFile2.end + sizeOfNextContext;
        Range range = new Range(firstLineNumberOfPreviousContext, rangeEnd);
        System.out.printf("--- %s ----\n", range.shiftByOne());
        if (block.getOperator() != Operator.DELETE) {
            if (block.previousContext != null) {
                for (int j=startIndexOfPreviousContext; j<endIndexOfPreviousContext; j++) {
                    System.out.printf("%s %s\n", SYMBOLS_OF_OPERATORS.get(Operator.COPY), block.previousContext.linesToCopy.get(j));
                }
            }
            for (GroupedOperation g : block.change) {
                if (g.operator == Operator.DELETE) {
                    continue;
                }
                Character symbolOfOperator = SYMBOLS_OF_OPERATORS.get(g.operator);

                List<String> lines = (g.operator == Operator.COPY) ? g.linesToCopy : g.linesToAdd;

                for (String line : lines) {
                    System.out.println(symbolOfOperator + " " + line);
                }

            }
            if (block.nextContext != null) {
                for (int j=startIndexOfNextContext; j<endIndexOfNextContext; j++) {
                    System.out.printf("%s %s\n", SYMBOLS_OF_OPERATORS.get(Operator.COPY), block.nextContext.linesToCopy.get(j));
                }
            }
        }
    }
}
