package admin_management.utils;


public class TableFormatter {
    private static final String HORIZONTAL_LINE = "─";
    private static final String VERTICAL_LINE = "│";
    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    private static final String BOTTOM_RIGHT = "┘";
    private static final String T_DOWN = "┬";
    private static final String T_UP = "┴";
    private static final String T_RIGHT = "├";
    private static final String T_LEFT = "┤";
    private static final String CROSS = "┼";

    public static void printTableHeader(String[] headers, int[] columnWidths) {
        printTopBorder(columnWidths);
        printRow(headers, columnWidths);
        printMiddleBorder(columnWidths);
    }

    public static void printTableFooter(int[] columnWidths) {
        printBottomBorder(columnWidths);
    }

    public static void printRow(String[] data, int[] columnWidths) {
        String[] lines = new String[getMaxLines(data, columnWidths)];
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            StringBuilder row = new StringBuilder(VERTICAL_LINE);
            for (int i = 0; i < data.length; i++) {
                String cell = data[i] != null ? data[i] : "";
                String[] cellLines = cell.split("(?<=\\G.{" + columnWidths[i] + "})");
                String cellContent = lineIndex < cellLines.length ? cellLines[lineIndex] : "";
                row.append(padRight(cellContent, columnWidths[i])).append(VERTICAL_LINE);
            }
            System.out.println(row);
        }
    }

    private static void printTopBorder(int[] columnWidths) {
        System.out.print(TOP_LEFT);
        printBorderLine(columnWidths, T_DOWN);
        System.out.println(TOP_RIGHT);
    }

    private static void printMiddleBorder(int[] columnWidths) {
        System.out.print(T_RIGHT);
        printBorderLine(columnWidths, CROSS);
        System.out.println(T_LEFT);
    }

    private static void printBottomBorder(int[] columnWidths) {
        System.out.print(BOTTOM_LEFT);
        printBorderLine(columnWidths, T_UP);
        System.out.println(BOTTOM_RIGHT);
    }

    private static void printBorderLine(int[] columnWidths, String separator) {
        for (int i = 0; i < columnWidths.length; i++) {
            if (i > 0) {
                System.out.print(separator);
            }
            System.out.print(HORIZONTAL_LINE.repeat(columnWidths[i] + 2));
        }
    }

    private static int getMaxLines(String[] data, int[] columnWidths) {
        int maxLines = 1;
        for (int i = 0; i < data.length; i++) {
            String cell = data[i];
            if (cell != null) {
                int lines = (int) Math.ceil(cell.length() / (double) columnWidths[i]);
                maxLines = Math.max(maxLines, lines);
            }
        }
        return maxLines;
    }

    private static String padRight(String s, int n) {
        return String.format(" %-" + n + "s ", s);
    }
}