package com.tienda.inventario.entities;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "abonos")
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaCliente cuenta;

    @Column(name = "cantidad", nullable = false)
    private Float cantidad;

    @Column(name = "viejo_saldo", nullable = false)
    private Float viejoSaldo;

    @Column(name = "nuevo_saldo", nullable = false)
    private Float nuevoSaldo;

    public Abono() {
    }

    public Abono(Date fecha, CuentaCliente cuenta, Float cantidad,
                 Float viejoSaldo, Float nuevoSaldo) {
        this.fecha = fecha;
        this.cuenta = cuenta;
        this.cantidad = cantidad;
        this.viejoSaldo = viejoSaldo;
        this.nuevoSaldo = nuevoSaldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public CuentaCliente getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaCliente cuenta) {
        this.cuenta = cuenta;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Float getViejoSaldo() {
        return viejoSaldo;
    }

    public void setViejoSaldo(Float viejoSaldo) {
        this.viejoSaldo = viejoSaldo;
    }

    public Float getNuevoSaldo() {
        return nuevoSaldo;
    }

    public void setNuevoSaldo(Float nuevoSaldo) {
        this.nuevoSaldo = nuevoSaldo;
    }

    
}
