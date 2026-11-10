package model;

import java.time.LocalDate;

public class Task {

    private long id;
    private long userId;

    private String title;
    private String description;
    private String status;

    private LocalDate dueDate;

    public Task(long userId, String title,
                String description, LocalDate dueDate) {

        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = "Pending";
    }

    public Task(long id, long userId, String title,
                String description, LocalDate dueDate,
                String status) {

        this(userId, title, description, dueDate);

        this.id = id;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }
}