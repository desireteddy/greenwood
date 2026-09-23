package com.smartcampus.service;

public class GradeCalculator {
    public static double calculateTotal(
        double coursework, double exam
    ) {
        return coursework + exam;
    }

    public String calculateGrade(double total) {
        if (total >= 80) {
            return "A";
        } else if (total >= 70) {
            return "B";
        } else if (total >= 60) {
            return "C";
        } else if (total >= 50) {
            return "D";
        } else {
            return "F";
        }
    }
}