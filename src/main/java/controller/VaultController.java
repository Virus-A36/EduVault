package controller;

import dao.VaultDAO;
import model.Credential;
import util.SecurityUtil;

import java.util.ArrayList;
import java.util.List;

public class VaultController {

    private final VaultDAO vaultDAO = new VaultDAO();
    private String lastError = null;

    public String getLastError() {
        return lastError;
    }

    public boolean addCredential(long userId,
                                 String website,
                                 String username,
                                 String password) {

        try {
            lastError = null;
            if (website == null || website.trim().isEmpty()
                    || username == null || username.trim().isEmpty()
                    || password == null || password.isEmpty()) {
                lastError = "All fields (Website, Username, Password) are required.";
                return false;
            }

            String encrypted = SecurityUtil.encrypt(password);

            Credential credential = new Credential(
                    0, userId, website, username, encrypted
            );

            vaultDAO.add(credential);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            lastError = e.getMessage() != null ? e.getMessage() : e.toString();
            return false;
        }
    }

    public List<Credential> getCredentials(long userId) {

        try {
            return vaultDAO.list(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String decryptPassword(String encrypted) {

        try {
            return SecurityUtil.decrypt(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteCredential(long id, long userId) {

        try {
            vaultDAO.delete(id, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
