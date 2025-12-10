package com.tienda.inventario.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "tipo_merma", nullable = false, length = 25)
    private String tipoMerma; // EXPIRADO, USO_PERSONAL, MAL_ESTADO, etc.

    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    @OneToMany(mappedBy = "merma", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MermaProducto> mermaProductos = new ArrayList<>();

    
    public Merma() {
    }

    public Merma(Producto producto, String tipoMerma, String descripcion,
                 LocalDateTime fechaSalida, Integer cantidad) {
        this.tipoMerma = tipoMerma;
        this.descripcion = descripcion;
        this.fechaSalida = fechaSalida;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public List<MermaProducto> getMermaProductos() {
        return mermaProductos;
    }

    public void setMermaProductos(List<MermaProducto> mermaProductos) {
        this.mermaProductos = mermaProductos;
    }

    

    
}
