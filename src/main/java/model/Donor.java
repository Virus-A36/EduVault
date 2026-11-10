package model;

import java.time.LocalDate;
import java.time.Period;

public class Donor {

    public static final int COOLDOWN_MONTHS = 3;

    private long id;
    private String name;
    private String bloodGroup;
    private String phone;
    private String location;
    private boolean consent;
    private LocalDate lastDonationDate;

    public Donor(String name, String bloodGroup,
                 String phone, String location,
                 boolean consent) {
        this(0, name, bloodGroup, phone, location, consent, null);
    }

    public Donor(String name, String bloodGroup,
                 String phone, String location,
                 boolean consent, LocalDate lastDonationDate) {
        this(0, name, bloodGroup, phone, location, consent, lastDonationDate);
    }

    public Donor(long id, String name, String bloodGroup,
                 String phone, String location,
                 boolean consent) {
        this(id, name, bloodGroup, phone, location, consent, null);
    }

    public Donor(long id, String name, String bloodGroup,
                 String phone, String location,
                 boolean consent, LocalDate lastDonationDate) {
        this.id = id;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.phone = phone;
        this.location = location;
        this.consent = consent;
        this.lastDonationDate = lastDonationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getPhone() {
        return phone;
    }

    public String getLocation() {
        return location;
    }

    public boolean isConsent() {
        return consent;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public LocalDate getNextEligibleDate() {
        if (lastDonationDate == null) {
            return null;
        }
        return lastDonationDate.plusMonths(COOLDOWN_MONTHS);
    }

    public boolean isAvailable() {
        if (lastDonationDate == null) {
            return true;
        }
        LocalDate nextEligible = getNextEligibleDate();
        return !LocalDate.now().isBefore(nextEligible);
    }

    public String getAvailabilityStatus() {
        return isAvailable() ? "Available" : "Unavailable";
    }

    public String getStatusDetails() {
        if (lastDonationDate == null) {
            return "Available (Never donated)";
        }

        LocalDate today = LocalDate.now();
        LocalDate nextEligible = getNextEligibleDate();

        if (today.isBefore(nextEligible)) {
            Period p = Period.between(today, nextEligible);
            String timeRemaining;
            if (p.getMonths() > 0) {
                timeRemaining = p.getMonths() + " month(s) " + p.getDays() + " day(s)";
            } else {
                timeRemaining = p.getDays() + " day(s)";
            }
            return "Unavailable (Eligible in " + timeRemaining + " - " + nextEligible + ")";
        } else {
            return "Available (Eligible since " + nextEligible + ")";
        }
    }
}
