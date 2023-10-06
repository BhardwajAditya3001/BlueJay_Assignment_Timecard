package org.example;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream("Assignment_Timecard.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue; // Skip the header row
                }

                String name = row.getCell(7).getStringCellValue(); // Name is in the 8th column (index 7)
                String position = row.getCell(0).getStringCellValue(); // Position is in the 1st column (index 0)

                Cell durationCell = row.getCell(4); // Duration is in the 3rd column (index 2)
                int duration = parseDurationCellValue(durationCell);

                Employee employee = findEmployee(employees, name, position);
                if (employee == null) {
                    employee = new Employee(name, position);
                    employees.add(employee);
                }
                employee.addShiftDuration(duration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int consecutiveDaysCriteria = 7;
        int maxMinutesBetweenShiftsCriteria = 10 * 60; // Convert to minutes
        int maxMinutesInShiftCriteria = 14 * 60; // Convert to minutes

        // Print the table header
        System.out.println("------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-40s | %-20s | %-10s | %-10s | %-10s |%n", "Employee Name", "Position", "Consecutive Days", "Short Breaks", "Long Shifts");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------");

        for (Employee employee : employees) {
            // Initialize columns with "No" and update if the condition is met
            String consecutiveDays = "No";
            String shortBreaks = "No";
            String longShifts = "No";

            if (employee.hasConsecutiveDays(consecutiveDaysCriteria)) {
                consecutiveDays = "Yes";
            }
            if (employee.hasShortBreakBetweenShifts(maxMinutesBetweenShiftsCriteria)) {
                shortBreaks = "Yes";
            }
            if (employee.hasLongShift(maxMinutesInShiftCriteria)) {
                longShifts = "Yes";
            }

            // Print employee data in a table row
            System.out.printf("| %-40s | %-20s | %-15s | %-11s | %-11s |%n", employee.getName(), employee.getPosition(), consecutiveDays, shortBreaks, longShifts);
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
        }
    }

    private static int parseDurationCellValue(Cell durationCell) {
        String cellValue = durationCell.getStringCellValue();
        Pattern pattern = Pattern.compile("(\\d+):(\\d+)");
        Matcher matcher = pattern.matcher(cellValue);

        if (matcher.find()) {
            int hours = Integer.parseInt(matcher.group(1));
            int minutes = Integer.parseInt(matcher.group(2));
            return hours * 60 + minutes; // Convert to minutes
        } else {
            // Handle invalid or unexpected cell value
            return 0; // Set a default value or handle as needed
        }
    }

    private static Employee findEmployee(List<Employee> employees, String name, String position) {
        for (Employee employee : employees) {
            if (employee.getName().equals(name) && employee.getPosition().equals(position)) {
                return employee;
            }
        }
        return null;
    }
}
