package dto;

import java.time.LocalDate;

public class PrestamoDTO {
    private int id;
    private int socioId; // ID del socio para filtrar por usuario
    private String nombreSocio;
    private String tituloLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionPrevista;
    private String estado;

    public PrestamoDTO(int id, int socioId, String nombreSocio, String tituloLibro, LocalDate fechaPrestamo,
            LocalDate fechaDevolucionPrevista, String estado) {
        this.id = id;
        this.socioId = socioId;
        this.nombreSocio = nombreSocio;
        this.tituloLibro = tituloLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.estado = estado;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getSocioId() {
        return socioId;
    } // Necesario para filtrar préstamos del socio actual

    public String getNombreSocio() {
        return nombreSocio;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucionPrevista() {
        return fechaDevolucionPrevista;
    }

    public String getEstado() {
        return estado;
    }
}
