package com.tienda.inventario.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private Long cuentaId; // si prefieres, puedes quitar esto y usar solo VentaCliente

    private Float total;
    private String status;

    @OneToMany(mappedBy = "venta")
    private List<VentaProducto> ventaProductos;

    @OneToMany(mappedBy = "venta")
    private List<VentaCliente> ventasCliente;

    public Venta() {
    }

    public Venta(Long id, LocalDateTime fecha, Long cuentaId, Float total, String status) {
        this.id = id;
        this.fecha = fecha;
        this.cuentaId = cuentaId;
        this.total = total;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<VentaProducto> getVentaProductos() {
        return ventaProductos;
    }

    public void setVentaProductos(List<VentaProducto> ventaProductos) {
        this.ventaProductos = ventaProductos;
    }

    public List<VentaCliente> getVentasCliente() {
        return ventasCliente;
    }

    public void setVentasCliente(List<VentaCliente> ventasCliente) {
        this.ventasCliente = ventasCliente;
    }

    
}
