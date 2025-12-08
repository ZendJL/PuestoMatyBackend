package com.tienda.inventario.entities;


import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="productos")
public class Producto {

    private Long id;
    private String codigo;
    private String descripcion;
    private Float precio;
    private String proveedor;
    private Long cantidad;
    private Date ultima_compra;
    private Date ultima_venta;
    
    public Producto() {
    }

    public Producto(Long id, String codigo, String descripcion, Float precio, String proveedor, Long cantidad,
            Date ultima_compra, Date ultima_venta) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.proveedor = proveedor;
        this.cantidad = cantidad;
        this.ultima_compra = ultima_compra;
        this.ultima_venta = ultima_venta;
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
    public Date getUltima_compra() {
        return ultima_compra;
    }
    public void setUltima_compra(Date ultima_compra) {
        this.ultima_compra = ultima_compra;
    }
    public Date getUltima_venta() {
        return ultima_venta;
    }
    public void setUltima_venta(Date ultima_venta) {
        this.ultima_venta = ultima_venta;
    }
    
}
