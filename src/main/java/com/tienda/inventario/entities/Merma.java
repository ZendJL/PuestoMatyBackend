package com.tienda.inventario.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "merma")
public class Merma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // fecha de registro de la merma (puede ser nullable si quieres)
    @Column(name = "fecha")
    private LocalDateTime fecha;

    // fecha efectiva de salida de los productos, NOT NULL
    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    // tipo de merma: "CADUCIDAD", "ROBO", "ROTURA", etc.
    @Column(name = "tipo_merma")
    private String tipoMerma;

    // descripción general opcional
    @Column(name = "motivo_general")
    private String motivoGeneral;

    @OneToMany(mappedBy = "merma", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MermaProducto> mermaProductos = new ArrayList<>();

    public Merma() {
    }

    public Merma(LocalDateTime fecha, LocalDateTime fechaSalida,
            String tipoMerma, String motivoGeneral) {
        this.fecha = fecha;
        this.fechaSalida = fechaSalida;
        this.tipoMerma = tipoMerma;
        this.motivoGeneral = motivoGeneral;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getTipoMerma() {
        return tipoMerma;
    }

    public void setTipoMerma(String tipoMerma) {
        this.tipoMerma = tipoMerma;
    }

    public String getMotivoGeneral() {
        return motivoGeneral;
    }

    public void setMotivoGeneral(String motivoGeneral) {
        this.motivoGeneral = motivoGeneral;
    }

    public List<MermaProducto> getMermaProductos() {
        return mermaProductos;
    }

    public void setMermaProductos(List<MermaProducto> mermaProductos) {
        this.mermaProductos = mermaProductos;
    }

    public void agregarDetalle(MermaProducto mp) {
        mermaProductos.add(mp);
        mp.setMerma(this);
    }
}
