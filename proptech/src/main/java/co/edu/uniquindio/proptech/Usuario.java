package co.edu.uniquindio.proptech;

public class Usuario {
    private String username;
    private String password;
    private String rol;
    private String idAsociado;

    public Usuario(String username, String password, String rol, String idAsociado){
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.idAsociado = idAsociado;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getRol() {
        return rol;
    }
    public String getIdAsociado() {
        return idAsociado;
    }
}
