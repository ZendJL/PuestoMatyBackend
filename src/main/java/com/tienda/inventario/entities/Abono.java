package com.tienda.inventario.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "abonos")
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "cuenta_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private CuentaCliente cuenta;

    @Column(name = "cantidad", nullable = false)
    private Float cantidad;

    @Column(name = "viejo_saldo", nullable = false)
    private Float viejoSaldo;

    @Column(name = "nuevo_saldo", nullable = false)
    private Float nuevoSaldo;

    @com.fasterxml.jackson.annotation.JsonProperty("cuentaId")
    public Integer getCuentaId() {
        return cuenta != null ? cuenta.getId() : null;
    }

    public Abono() {
    }

    public Abono(LocalDateTime fecha, CuentaCliente cuenta, Float cantidad,
                 Float viejoSaldo, Float nuevoSaldo) {
        this.fecha = fecha;
        this.cuenta = cuenta;
        this.cantidad = cantidad;
        this.viejoSaldo = viejoSaldo;
        this.nuevoSaldo = nuevoSaldo;
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
