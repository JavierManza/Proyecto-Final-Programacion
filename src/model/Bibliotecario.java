package model;

public class Bibliotecario extends Usuario {
    private String numEmpleado;
    private String turno;

    public Bibliotecario() {
    }

    public Bibliotecario(Usuario u, String numEmpleado, String turno) {
        super(u.getId(), u.getUsername(), u.getPassword(), u.getEmail(), u.getNombre(), u.getApellidos(), u.getDni(),
                u.getRol());
        this.numEmpleado = numEmpleado;
        this.turno = turno;
    }

    public String getNumEmpleado() {
        return numEmpleado;
    }

    public void setNumEmpleado(String numEmpleado) {
        this.numEmpleado = numEmpleado;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
