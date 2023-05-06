package cz.cuni.mff.diff;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Diff {
    public static final Integer DEFAULT_WIDTH = 130;
    public static void main(String[] args) throws Exception {
        Arguments arguments = parseArguments(args);
        List<String> file1Lines = readFile(arguments.file1Path);
        List<String> file2Lines = readFile(arguments.file2Path);
        DiffFinder diffFinder = new DiffFinder(file1Lines, file2Lines);
        List<Operation> operations = diffFinder.findDiffs();
        List<GroupedOperation> groupedOperations = DiffFinder.groupOperations(operations);
        switch (arguments.mode) {
            case STANDARD:
                printGrouped(groupedOperations);
                break;
            case CONTEXT:
                ContextModePrinter contextModePrinter = new ContextModePrinter(groupedOperations, arguments.contextLinesCount, arguments.file1Path, arguments.file2Path);
                contextModePrinter.print();
                break;
            case SIDE_BY_SIDE:
                SideBySidePrinter sideBySidePrinter = new SideBySidePrinter(operations, arguments.width);
                sideBySidePrinter.printSideBySide();
                break;
            case INTERACTIVE:
                SideBySidePrinter sideBySidePrinter1 = new SideBySidePrinter(operations, arguments.width);
                sideBySidePrinter1.printInteractive(arguments.outputFilePath);
                break;
        }
    }

    private static void printGrouped(List<GroupedOperation> groupedOperations) {
        for (GroupedOperation op : groupedOperations) {
            if (op.operator == Operator.COPY) {
                continue;
            }
            String resultOperator = "";
            StringBuilder sb = new StringBuilder();
            switch (op.operator) {
                case ADD:
                    resultOperator = "a";
                    for (String line : op.linesToAdd) {
                        sb.append("> " + line + "\n");
                    }
                    break;
                case DELETE:
                    resultOperator = "d";
                    for (String line : op.linesToDelete) {
                        sb.append("< " + line + "\n");
                    }
                    break;
                case CHANGE:
                    resultOperator = "c";
                    for (String line : op.linesToDelete) {

                        sb.append("< " + line + "\n");
                    }
                    sb.append("---\n");
                    for (String lineToAdd : op.linesToAdd) {
                        sb.append("> " + lineToAdd + "\n");
                    }
                    break;
            }
            System.out.println(op.rangeInFile1.shiftByOne() + resultOperator + op.rangeInFile2.shiftByOne());
            System.out.println(sb);
        }
    }
    private static void print(List<Operation> operations) {
        for (Operation op : operations) {
            String resultOperator = "";
            String lineToPrint = "";
            switch (op.operator) {
                case ADD:
                    resultOperator = "a";
                    lineToPrint = "> " + op.lineToAdd;
                    break;
                case DELETE:
                    resultOperator = "d";
                    lineToPrint = "< " + op.lineToDelete;
                    break;
                case CHANGE:
                    resultOperator = "c";
                    lineToPrint = "< " + op.lineToDelete + "\n" + "---\n" + "> " + op.lineToAdd;
                    break;
                case COPY:
            }
            if (op.operator == Operator.COPY) {
                continue;
            }
            System.out.println((op.lineNumberInFile1+1) + resultOperator + (op.lineNumberInFile2+1));
            System.out.println(lineToPrint);
        }
    }
    public static List<String> readFile(Path path) throws IOException {
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static Arguments parseArguments(String[] args) throws Exception {
        Mode mode = Mode.STANDARD;
        Integer contextLinesCount = null;
        Integer width = null;
        File file1 = null;
        File file2 = null;
        File outputFile = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--context":
                case "-C":
                    try {
                        mode = Mode.CONTEXT;
                        contextLinesCount = Integer.parseInt(args[i+1]);
                        i++;
                    } catch (NumberFormatException ex) {
                        System.out.println("Wrong format");
                        System.exit(1);
                    }

                    break;
                case "--side-by-side":
                case "-y":
                    mode = Mode.SIDE_BY_SIDE;
                    break;
                case "--width":
                case "-W":
                    try {
                        width = Integer.parseInt(args[i+1]);
                        i++;
                    } catch (NumberFormatException ex) {
                        System.out.println("Wrong format");
                        System.exit(1);
                    }

                    break;
                case "--interactive":
                case "-i":
                    mode = Mode.INTERACTIVE;
                    break;
                case "--output":
                case "-o":
                    outputFile = new File(args[i+1]);
                    i++;
                    break;
                default:
                    if (file1 == null) {
                        file1 = new File(args[i]);
                    }
                    else if(file2 == null){
                        file2 = new File(args[i]);
                    }
                    else {
                        System.out.println("Wrong format");
                        System.exit(1);
                    }
            }
        }
        Path outputFilePath = null;
        if ((mode == Mode.SIDE_BY_SIDE || mode == Mode.INTERACTIVE) && width == null) {
            width = DEFAULT_WIDTH;

        }
        if (outputFile != null) {
            outputFilePath = outputFile.toPath();
        }
        Path file1Path = null;
        Path file2Path = null;
        try {
            file1Path = file1.toPath();
            file2Path = file2.toPath();
        } catch (NullPointerException ex) {
            System.out.println("Wrong format");
            System.exit(1);
        }
        return new Arguments(file1Path, file2Path, mode, contextLinesCount, width, outputFilePath);
    }
}
