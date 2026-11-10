package model;

public class Credential {

    private long id;
    private long userId;
    private String website;
    private String username;
    private String secret;

    public Credential(long id, long userId,
                      String website, String username,
                      String secret) {

        this.id = id;
        this.userId = userId;
        this.website = website;
        this.username = username;
        this.secret = secret;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getWebsite() {
        return website;
    }

    public String getUsername() {
        return username;
    }

    public String getSecret() {
        return secret;
    }
}