package com.tienda.inventario.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String descripcion;
    private Float precio;
    private String proveedor;
    private Long cantidad;

    @Column(name = "ultima_compra")
    private LocalDateTime ultimaCompra;

    @Column(name = "activo")
    private Boolean activo;

      @JsonIgnore
    @OneToMany(mappedBy = "producto")
    private List<VentaProducto> ventaProductos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "producto")
    private List<MermaProducto> mermaProductos = new ArrayList<>();



    public Producto() {
    }

    public Producto(Long id, String codigo, String descripcion, Float precio,
            String proveedor, Long cantidad, LocalDateTime ultimaCompra, Boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.proveedor = proveedor;
        this.cantidad = cantidad;
        this.ultimaCompra = ultimaCompra;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getUltimaCompra() {
        return ultimaCompra;
    }

    public void setUltimaCompra(LocalDateTime ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<VentaProducto> getVentaProductos() {
        return ventaProductos;
    }

    public void setVentaProductos(List<VentaProducto> ventaProductos) {
        this.ventaProductos = ventaProductos;
    }

    public List<MermaProducto> getMermaProductos() {
        return mermaProductos;
    }

    public void setMermaProductos(List<MermaProducto> mermaProductos) {
        this.mermaProductos = mermaProductos;
    }
    

}
