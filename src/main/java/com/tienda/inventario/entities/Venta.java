package com.tienda.inventario.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime fecha;

    @Column(name = "cuenta_id", insertable = false, updatable = false)
    private Integer cuentaId;

    private Float total;

    private String status;

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private CuentaCliente cuenta;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
@com.fasterxml.jackson.annotation.JsonManagedReference
private List<VentaProducto> ventaProductos;

    @OneToMany(mappedBy = "venta")
    private List<VentaCliente> ventasCliente;

    

    public Venta() {
    }

    public Venta(Integer id, LocalDateTime fecha, Integer cuentaId, Float total, String status, CuentaCliente cuenta,
            List<VentaProducto> ventaProductos, List<VentaCliente> ventasCliente) {
        this.id = id;
        this.fecha = fecha;
        this.cuentaId = cuentaId;
        this.total = total;
        this.status = status;
        this.cuenta = cuenta;
        this.ventaProductos = ventaProductos;
        this.ventasCliente = ventasCliente;
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

    public Integer getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Integer cuentaId) {
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

    public CuentaCliente getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaCliente cuenta) {
        this.cuenta = cuenta;
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