package cz.cuni.mff.diff;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SideBySidePrinter {
    private final List<Operation> operations;
    private final int width;
    private final int oneSideWidth;
    private static final Map<Operator, Character> SYMBOLS_OF_OPERATORS = Map.of(
            Operator.ADD, '>',
            Operator.DELETE, '<',
            Operator.CHANGE, '|',
            Operator.COPY, ' '
    );
    public SideBySidePrinter(List<Operation> operations, int width) {
        this.operations = operations;
        this.width = width;
        this.oneSideWidth = Math.max(0, width-3) / 2;
    }
    public void printInteractive(Path outputFilePath) throws IOException {
        List<String> outputContent = new ArrayList<>();
        for (Operation operation : operations) {
            Character operatorSymbol = SYMBOLS_OF_OPERATORS.get(operation.operator);
            String file1line;
            String file2line;
            if (operation.operator == Operator.COPY) {
                file1line = operation.lineToCopy;
                file2line = operation.lineToCopy;
            }
            else {
                file1line = operation.lineToDelete;
                file2line = operation.lineToAdd;
            }

            System.out.printf("%s %s %s\n", padOrTruncate(file1line), operatorSymbol, padOrTruncate(file2line));
            if (operation.operator != Operator.COPY) {
                Version version = readVersion();
                String line = null;
                switch (version) {
                    case OLD:
                        line = file1line;
                        break;
                    case NEW:
                        line = file2line;
                        break;
                }
                if (line != null) {
                    outputContent.add(line);
                }
            } else {
                outputContent.add(file1line);
            }


        }
        if (outputFilePath == null) {
            for (String line : outputContent) {
                System.out.println(line);
            }
        } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(outputFilePath)));
            for (String line : outputContent) {
                writer.write(line+"\n");
            }
            writer.close();
        }
    }
    private Version readVersion() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to choose from the first or second file? (1/2)");
        String fileNumber = scanner.nextLine();
        if (fileNumber.equals("1")) {
            return Version.OLD;
        } else if (fileNumber.equals("2")) {
            return Version.NEW;
        } else {
            return readVersion();
        }
    }
    public void printSideBySide() {
        for (Operation operation : operations) {
            Character operatorSymbol = SYMBOLS_OF_OPERATORS.get(operation.operator);
            String file1line;
            String file2line;
            if (operation.operator == Operator.COPY) {
                file1line = operation.lineToCopy;
                file2line = operation.lineToCopy;
            }
            else {
                file1line = operation.lineToDelete;
                file2line = operation.lineToAdd;
            }
            System.out.printf("%s %s %s\n", padOrTruncate(file1line), operatorSymbol, padOrTruncate(file2line));
        }
    }
    private String padOrTruncate(String fileLine) {
        String newStr;
        if (fileLine == null) {
            fileLine = "";
        }
        if (fileLine.length() < oneSideWidth) {
            String format = String.format("%%%ds", -oneSideWidth);
            newStr = String.format(format, fileLine);
        }
        else {
            newStr = fileLine.substring(0, oneSideWidth);
        }
        return newStr;
    }
}
