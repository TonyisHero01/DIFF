package cz.cuni.mff.diff;

public class Range {
    public int start;
    public int end;
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }
    public String shiftByOne() {
        if (start == end) {
            return Integer.toString(start+1);
        }
        if (start == end+1) {
            return Integer.toString(end+1);
        }
        return (start+1) + "," + (end+1);
    }
}
