package admin_management.utils;

/**
 * Utility class for formatting and displaying data in tabular format.
 * Provides methods to create professional-looking ASCII tables with:
 * - Unicode box-drawing characters for borders
 * - Dynamic column width handling
 * - Multi-line cell support
 * - Consistent padding and alignment
 */
public class TableFormatter {
    /**
     * Unicode box-drawing characters for table borders.
     * Used to create professional-looking table boundaries and separators.
     */
    private static final String HORIZONTAL_LINE = "─";  // Horizontal table line
    private static final String VERTICAL_LINE = "│";    // Vertical table line
    private static final String TOP_LEFT = "┌";         // Top-left corner
    private static final String TOP_RIGHT = "┐";        // Top-right corner
    private static final String BOTTOM_LEFT = "└";      // Bottom-left corner
    private static final String BOTTOM_RIGHT = "┘";     // Bottom-right corner
    private static final String T_DOWN = "┬";           // Top T-junction
    private static final String T_UP = "┴";             // Bottom T-junction
    private static final String T_RIGHT = "├";          // Left T-junction
    private static final String T_LEFT = "┤";           // Right T-junction
    private static final String CROSS = "┼";            // Cross junction

    /**
     * Prints the header section of the table.
     * Creates the top border and header row with column titles.
     * 
     * @param headers Array of column headers
     * @param columnWidths Array of column widths
     */
    public static void printTableHeader(String[] headers, int[] columnWidths) {
        printTopBorder(columnWidths);
        printRow(headers, columnWidths);
        printMiddleBorder(columnWidths);
    }

    /**
     * Prints the footer (bottom border) of the table.
     * Completes the table with a bottom border.
     * 
     * @param columnWidths Array of column widths
     */
    public static void printTableFooter(int[] columnWidths) {
        printBottomBorder(columnWidths);
    }

    /**
     * Prints a data row in the table.
     * Handles multi-line content within cells.
     * Maintains proper alignment and padding.
     * 
     * @param data Array of cell data
     * @param columnWidths Array of column widths
     */
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

    /**
     * Prints the top border of the table.
     * Uses corner pieces and T-junctions for professional appearance.
     * 
     * @param columnWidths Array of column widths
     */
    private static void printTopBorder(int[] columnWidths) {
        System.out.print(TOP_LEFT);
        printBorderLine(columnWidths, T_DOWN);
        System.out.println(TOP_RIGHT);
    }

    /**
     * Prints the middle border of the table.
     * Used between header and data rows.
     * 
     * @param columnWidths Array of column widths
     */
    private static void printMiddleBorder(int[] columnWidths) {
        System.out.print(T_RIGHT);
        printBorderLine(columnWidths, CROSS);
        System.out.println(T_LEFT);
    }

    /**
     * Prints the bottom border of the table.
     * Completes the table with proper corner pieces.
     * 
     * @param columnWidths Array of column widths
     */
    private static void printBottomBorder(int[] columnWidths) {
        System.out.print(BOTTOM_LEFT);
        printBorderLine(columnWidths, T_UP);
        System.out.println(BOTTOM_RIGHT);
    }

    /**
     * Helper method to print horizontal border lines.
     * Handles proper spacing and separators between columns.
     * 
     * @param columnWidths Array of column widths
     * @param separator Character to use at column junctions
     */
    private static void printBorderLine(int[] columnWidths, String separator) {
        for (int i = 0; i < columnWidths.length; i++) {
            if (i > 0) {
                System.out.print(separator);
            }
            System.out.print(HORIZONTAL_LINE.repeat(columnWidths[i] + 2));
        }
    }

    /**
     * Calculates the maximum number of lines needed for multi-line cells.
     * Ensures proper vertical alignment of data.
     * 
     * @param data Array of cell data
     * @param columnWidths Array of column widths
     * @return Maximum number of lines needed
     */
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

    /**
     * Pads a string with spaces for proper column alignment.
     * Ensures consistent cell formatting.
     * 
     * @param s String to pad
     * @param n Desired width
     * @return Padded string
     */
    private static String padRight(String s, int n) {
        return String.format(" %-" + n + "s ", s);
    }
}