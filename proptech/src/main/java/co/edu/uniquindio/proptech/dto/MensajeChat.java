package co.edu.uniquindio.proptech.dto;

public class MensajeChat {

    private String role;
    private String content;

    public MensajeChat(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}