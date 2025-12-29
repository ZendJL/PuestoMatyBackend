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

    // Fecha de registro
    @Column(name = "fecha")
    private LocalDateTime fecha;

    // tipo de merma: "CADUCIDAD", "ROBO", "ROTURA", etc.
    @Column(name = "tipo_merma")
    private String tipoMerma;

    // descripción general opcional
    @Column(name = "motivo_general")
    private String motivoGeneral;

    // Opcional: total de la merma (si creaste la columna en BD)
    @Column(name = "costo_total")
    private Float costoTotal;

    @OneToMany(mappedBy = "merma", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MermaProducto> mermaProductos = new ArrayList<>();

    public Merma() {
    }

    public Merma(LocalDateTime fecha, LocalDateTime fechaSalida,
            String tipoMerma, String motivoGeneral) {
        this.fecha = fecha;
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

    public Float getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(Float costoTotal) {
        this.costoTotal = costoTotal;
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
