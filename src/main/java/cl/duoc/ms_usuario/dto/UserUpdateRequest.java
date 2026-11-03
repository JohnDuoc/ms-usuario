package cl.duoc.ms_usuario.dto;

public class UserUpdateRequest {
    private String email;
    private String password;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String email() { return email; }
    public String password() { return password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}