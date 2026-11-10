package controller;

import dao.DonorDAO;
import model.Donor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonorController {

    private final DonorDAO donorDAO = new DonorDAO();

    public boolean registerDonor(String name,
                                 String bloodGroup,
                                 String phone,
                                 String location,
                                 boolean consent) {
        return registerDonor(name, bloodGroup, phone, location, consent, null);
    }

    public boolean registerDonor(String name,
                                 String bloodGroup,
                                 String phone,
                                 String location,
                                 boolean consent,
                                 LocalDate lastDonationDate) {

        try {
            if (name == null || name.trim().isEmpty()
                    || phone == null || phone.trim().isEmpty()
                    || location == null || location.trim().isEmpty()
                    || !consent) {
                return false;
            }

            String[] groups = {
                    "A+", "A-", "B+", "B-",
                    "AB+", "AB-", "O+", "O-"
            };

            boolean valid = false;
            for (String group : groups) {
                if (group.equalsIgnoreCase(bloodGroup)) {
                    valid = true;
                    bloodGroup = group; // standardize casing
                    break;
                }
            }

            if (!valid) {
                return false;
            }

            donorDAO.add(new Donor(
                    name.trim(), bloodGroup, phone.trim(), location.trim(), consent, lastDonationDate
            ));

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDonationDate(long donorId, LocalDate date) {
        try {
            donorDAO.updateDonationDate(donorId, date);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Donor> searchDonors(String bloodGroup) {
        try {
            return donorDAO.search(bloodGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
