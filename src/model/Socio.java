package model;

import java.time.LocalDate;

public class Socio extends Usuario {
    private String direccion;
    private String telefono;
    private LocalDate fechaAlta;

    public Socio() {
    }

    public Socio(Usuario u, String direccion, String telefono, LocalDate fechaAlta) {
        super(u.getId(), u.getUsername(), u.getPassword(), u.getEmail(), u.getNombre(), u.getApellidos(), u.getDni(),
                u.getRol());
        this.direccion = direccion;
        this.telefono = telefono;
        this.fechaAlta = fechaAlta;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
