package gui;

public class UtilisateurRow {

    private final long id;
    private final String fullName;
    private final String email;
    private final String type;

    public UtilisateurRow(long id, String fullName, String email, String type) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }
}
