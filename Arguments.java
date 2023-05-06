package cz.cuni.mff.diff;

import java.nio.file.Path;

public class Arguments {
    public Path file1Path;
    public Path file2Path;
    public Mode mode;
    public Integer contextLinesCount;

    public Integer width;
    public Path outputFilePath;
    public Arguments (Path file1Path, Path file2Path, Mode mode, Integer contextLinesCount, Integer width, Path outputFilePath) {
        this.file1Path = file1Path;
        this.file2Path = file2Path;
        this.mode = mode;
        this.contextLinesCount = contextLinesCount;
        this.width = width;
        this.outputFilePath = outputFilePath;
    }
    public void Print() {
        System.out.println(this.file1Path);
        System.out.println(this.file2Path);
        System.out.println(this.mode);
        System.out.println(this.contextLinesCount);
        System.out.println(this.width);
        System.out.println(this.outputFilePath);
    }
}
