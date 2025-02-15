import java.io.*;
import java.util.*;

public class Modeling {

    // Method to read the ARFF file
    public static List<String> readArffFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    // Method to get numeric columns from the ARFF header
    public static List<Integer> getNumericColumns(List<String> headerLines) {
        List<Integer> numericColumns = new ArrayList<>();
        int attributeIndex = 0;
        for (String line : headerLines) {
            if (line.startsWith("@attribute")) {
                attributeIndex++;
                String[] parts = line.split(" ");
                if (parts[2].equals("numeric")) {
                    numericColumns.add(attributeIndex);
                }
            }
            if (line.startsWith("@data")) {
                break;
            }
        }
        return numericColumns;
    }

    public static Map<Integer, List<Integer>> calculateCategories(List<String> datalines,
            List<Integer> numericColumns) {
        Map<Integer, Double[]> ranges = new HashMap<>();
        Map<Integer, List<Double[]>> categories = new HashMap<>();
        Map<Integer, Integer> columnCategoryMap = new HashMap<>();
        // define the amount of categories.
        columnCategoryMap.put(2, 5);
        columnCategoryMap.put(3, 3);
        columnCategoryMap.put(7, 3);
        columnCategoryMap.put(10, 5);
        columnCategoryMap.put(13, 10);
        columnCategoryMap.put(14, 20);

        for (int column : numericColumns) {
            ranges.put(column, new Double[] { Double.MAX_VALUE, Double.MIN_VALUE });
        }

        // removes @data line
        datalines.remove(0);

        // go through each line and get numeric value for each column and then compare
        // to get ranges
        for (String line : datalines) {
            String[] values = line.split(",");
            for (int column : numericColumns) {
                double numeric = Double.parseDouble(values[column - 1]);
                Double[] range = ranges.get(column);
                range[0] = Math.min(range[0], numeric);
                range[1] = Math.max(range[1], numeric);
            }
        }

        // get the catergories for each column
        for (int column : numericColumns) {
            Double[] range = ranges.get(column);
            int categoryAmount = columnCategoryMap.get(column);
            double rangeSize = range[1] - range[0];
            double categorySize = rangeSize / categoryAmount;
            List<Double[]> columnCategories = new ArrayList<>();

            for (int i = 0; i < categoryAmount; i++) {
                // shift the start depending on what range number
                double categoryStart = range[0] + (i * categorySize);
                // if last then assgin range[1] if not then assgin current start plus the size
                double categoryEnd = (i == categoryAmount - 1) ? range[1] : categoryStart + categorySize;
                categoryStart = Math.round(categoryStart * 100.0) / 100.0;
                categoryEnd = Math.round(categoryEnd * 100.0) / 100.0;

                columnCategories.add(new Double[] { categoryStart, categoryEnd });
                categories.put(column, columnCategories);

            }
        }

        Map<Integer, List<Integer>> categoryAssignments = new HashMap<>();
        for (int column : numericColumns) {
            categoryAssignments.put(column, new ArrayList<>());
        }

        // loop throuhg every line and for each column assign the value to a category
        // based on catergories made for each column
        for (String line : datalines) {
            String[] values = line.split(",");

            for (int column : numericColumns) {
                double numeric = Double.parseDouble(values[column - 1]);
                List<Double[]> columnCategories = categories.get(column);

                for (int i = 0; i < columnCategories.size(); i++) {
                    Double[] categoryRange = columnCategories.get(i);
                    double categoryStart = categoryRange[0];
                    double categoryEnd = categoryRange[1];

                    if (numeric >= categoryStart && numeric < categoryEnd) {
                        categoryAssignments.get(column).add(i + 1);
                        break;
                    }
                }
            }
        }
        return categoryAssignments;
    }

    // write the new values to the ARFF file
    public static void updateArffFile(Map<Integer, List<Integer>> categoryAssignments, String outputFilePath,
            List<String> datalines, List<String> headerlines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            Map<Integer, Integer> columnCategoryMap = new HashMap<>();
            columnCategoryMap.put(2, 5);
            columnCategoryMap.put(3, 3);
            columnCategoryMap.put(7, 3);
            columnCategoryMap.put(10, 5);
            columnCategoryMap.put(13, 10);
            columnCategoryMap.put(14, 20);
            // update the attribute lines so instead of numeric it shows catergories
            // loop through index of first attribute and last
            for (int i = 6; i < 19; i++) {
                String headerline = headerlines.get(i);
                String[] parts = headerline.split(" ");
                int columnNumber = Integer.parseInt(parts[1].substring(1));

                if (columnCategoryMap.containsKey(columnNumber) && parts[2].equals("numeric")) {
                    int catergoryAmount = columnCategoryMap.get(columnNumber);
                    StringBuilder newAttribute = new StringBuilder("{");
                    for (int j = 0; j < catergoryAmount; j++) {
                        newAttribute.append(j + 1);
                        if (j < catergoryAmount - 1) {
                            newAttribute.append(",");
                        }
                    }
                    newAttribute.append("}");
                    headerlines.set(i, headerline.replace("numeric", newAttribute.toString()));
                }
            }
            for (String headerline : headerlines) {
                writer.write(headerline);
                writer.newLine();
            }
            writer.write("@data");
            writer.newLine();

            for (int i = 1; i < datalines.size(); i++) {
                String[] values = datalines.get(i).split(",");

                for (int column : categoryAssignments.keySet()) {
                    List<Integer> columnCategories = categoryAssignments.get(column);
                    values[column - 1] = String.valueOf(columnCategories.get(i - 1));
                }
                writer.write(String.join(",", values));
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String inputFilePath = "originalCredit.arff";
        String outputFilePath = "Credit.arff";
        try {

            List<String> lines = readArffFile(inputFilePath);
            List<String> headerLines = new ArrayList<>();
            List<String> dataLines = new ArrayList<>();
            boolean isDataSection = false;

            for (String line : lines) {
                if (line.startsWith("@data")) {
                    isDataSection = true;
                }
                if (isDataSection) {
                    dataLines.add(line);
                } else {
                    headerLines.add(line);
                }
            }
            // gets the column position of all numeric values
            List<Integer> numericColumns = getNumericColumns(headerLines);
            // creates a map of the attribute number and the created range
            Map<Integer, List<Integer>> catergoryAssignemnt = calculateCategories(dataLines, numericColumns);

            updateArffFile(catergoryAssignemnt, outputFilePath, dataLines, headerLines);
            System.out.println("ARFF file updated successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}