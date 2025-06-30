package me.rexe0.bettersurvival.util;

import java.util.ArrayList;
import java.util.List;

public final class InventoryUtil {
    public static int convertArrayIndexToInvIndex(int i) {
        // Make sure the integer is within the 28 slots given, 0-27

        while (true) {
            if (i < 28) {
                break;
            }
            i -= 28;
        }
        // Calculate the row number, 0-3. 6 rows in a regular inventory, but minus 2 because of border.
        int rowNumber = (int) Math.floor(i/7);

        // Calculate the column to add onto the row
        int columnNumber = (int) Math.floor(i%7);

        // Increase by 1 because of inital row being the border
        rowNumber++;

        // Increase by 1 because of inital collumn being border
        columnNumber++;

        return (rowNumber*9)+columnNumber;
    }
    public static int convertInvIndexToArrayIndex(int i) {
        // Calculate the row number, 0-5. 6 rows in a regular inventory.
        int rowNumber = (int) Math.floor(i/9);

        // Calculate the column to add onto the row
        int columnNumber = (int) Math.floor(i%9);

        if (rowNumber == 0 || rowNumber == 5 || columnNumber == 0 || columnNumber == 8) return -1;

        // Decrease by 1 because of inital row being the border
        rowNumber--;

        // Decrease by 1 because of inital collumn being border
        columnNumber--;

        return (rowNumber*7)+columnNumber;
    }
    public static List<Integer> getBorder() {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 54; i++) {
            if (i <= 8 || i >= 45) ints.add(i);
            if (i == 9 || i == 18 || i == 27 || i == 36) ints.add(i);
            if (i == 17 || i == 26 || i == 35 || i == 44) ints.add(i);
        }
        return ints;
    }


    public static List<Integer> getLeftSide() {
        return List.of(0, 9, 18, 27, 36, 45);
    }
    public static List<Integer> getRightSide() {
        return List.of(8, 17, 26, 35, 44, 53);
    }
}
