package com.tienda.inventario.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "merma")
public class Merma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "tipo_merma", nullable = false, length = 25)
    private String tipoMerma; // EXPIRADO, USO_PERSONAL, MAL_ESTADO, etc.

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    @Column(name = "cantidad", nullable = false)
    private Long cantidad;

    public Merma() {
    }

    public Merma(Producto producto, String tipoMerma, String descripcion,
                 LocalDateTime fechaSalida, Long cantidad) {
        this.producto = producto;
        this.tipoMerma = tipoMerma;
        this.descripcion = descripcion;
        this.fechaSalida = fechaSalida;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getTipoMerma() {
        return tipoMerma;
    }

    public void setTipoMerma(String tipoMerma) {
        this.tipoMerma = tipoMerma;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    
}
