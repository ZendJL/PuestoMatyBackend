package com.tienda.inventario.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

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

    @Column(name = "ultima_venta")
    private LocalDateTime ultimaVenta;

    @JsonIgnore
    @OneToMany(mappedBy = "producto")
    private List<VentaProducto> ventaProductos;

    @JsonIgnore
    @OneToMany(mappedBy = "producto")
    private List<Merma> mermas;

    public Producto() {
    }

    public Producto(Long id, String codigo, String descripcion, Float precio,
            String proveedor, Long cantidad, LocalDateTime ultimaCompra, LocalDateTime ultimaVenta) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.proveedor = proveedor;
        this.cantidad = cantidad;
        this.ultimaCompra = ultimaCompra;
        this.ultimaVenta = ultimaVenta;
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

    public LocalDateTime getUltimaVenta() {
        return ultimaVenta;
    }

    public void setUltimaVenta(LocalDateTime ultimaVenta) {
        this.ultimaVenta = ultimaVenta;
    }

    public List<VentaProducto> getVentaProductos() {
        return ventaProductos;
    }

    public void setVentaProductos(List<VentaProducto> ventaProductos) {
        this.ventaProductos = ventaProductos;
    }

    public List<Merma> getMermas() {
        return mermas;
    }

    public void setMermas(List<Merma> mermas) {
        this.mermas = mermas;
    }

}
