package com.tienda.inventario.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ventas_cliente")
public class VentaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaCliente cuentaCliente;

    public VentaCliente() {
    }

    public VentaCliente(Venta venta, CuentaCliente cuentaCliente) {
        this.venta = venta;
        this.cuentaCliente = cuentaCliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public CuentaCliente getCuentaCliente() {
        return cuentaCliente;
    }

    public void setCuentaCliente(CuentaCliente cuentaCliente) {
        this.cuentaCliente = cuentaCliente;
    }

    
}
