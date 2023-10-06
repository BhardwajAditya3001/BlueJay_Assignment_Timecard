package org.example;
import java.util.ArrayList;
import java.util.List;

class Employee {
    private String name;
    private String position;
    private List<Integer> shiftDurations;  // Store shift durations in minutes

    public Employee(String name, String position) {
        this.name = name;
        this.position = position;
        this.shiftDurations = new ArrayList<>();
    }

    public void addShiftDuration(int duration) {
        shiftDurations.add(duration);
    }

    public boolean hasConsecutiveDays(int consecutiveDays) {
        int count = 0;
        for (int i = 0; i < shiftDurations.size(); i++) {
            if (shiftDurations.get(i) > 0) {
                count++;
                if (count >= consecutiveDays) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    public boolean hasShortBreakBetweenShifts(int maxMinutesBetweenShifts) {
        for (int i = 1; i < shiftDurations.size(); i++) {
            int minutesBetweenShifts = Math.abs(shiftDurations.get(i - 1)) + shiftDurations.get(i);
            if (minutesBetweenShifts > 1 && minutesBetweenShifts < maxMinutesBetweenShifts) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLongShift(int maxMinutesInShift) {
        for (int duration : shiftDurations) {
            if (Math.abs(duration) > maxMinutesInShift) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }
}