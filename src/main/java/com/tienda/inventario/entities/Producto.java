package com.tienda.inventario.entities;


import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String descripcion;
    private Float precio;
    private String proveedor;
    private Long cantidad;
    private Date ultimaCompra;
    private Date ultimaVenta;
    
    public Producto() {
    }
    
    public Producto(Long id, String codigo, String descripcion, Float precio, String proveedor, Long cantidad,
        Date ultimaCompra, Date ultimaVenta) {
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
    public Date getUltimaCompra() {
        return ultimaCompra;
    }
    public void setUltima_compra(Date ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
    }
    public Date getUltimaVenta() {
        return ultimaVenta;
    }
    public void setUltimaVenta(Date ultimaVenta) {
        this.ultimaVenta = ultimaVenta;
    }
    
    @Override
    public String toString() {
        return "Producto [id=" + id + ", codigo=" + codigo + ", descripcion=" + descripcion + ", precio=" + precio
                + ", proveedor=" + proveedor + ", cantidad=" + cantidad + ", ultimaCompra=" + ultimaCompra
                + ", ultimaVenta=" + ultimaVenta + "]";
    }
    
}
