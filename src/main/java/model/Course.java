package model;

public class Course {

    private String name;
    private double credit;
    private double gradePoint;

    public Course(String name, double credit, double gradePoint) {
        this.name = name;
        this.credit = credit;
        this.gradePoint = gradePoint;
    }

    public String getName() {
        return name;
    }

    public double getCredit() {
        return credit;
    }

    public double getGradePoint() {
        return gradePoint;
    }
}