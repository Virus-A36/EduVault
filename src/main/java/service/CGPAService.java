package service;

import model.Course;
import java.util.List;

public class CGPAService {

    public double calculateCGPA(List<Course> courses) {

        double totalPoints = 0;
        double totalCredits = 0;

        for (Course course : courses) {

            totalPoints +=
                    course.getCredit() * course.getGradePoint();

            totalCredits += course.getCredit();
        }

        if (totalCredits == 0) {
            return 0;
        }

        return totalPoints / totalCredits;
    }
}