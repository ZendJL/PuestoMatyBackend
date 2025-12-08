package com.tienda.inventario.entities;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "cuenta_cliente")
public class CuentaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Float saldo;

    @OneToMany(mappedBy = "cuentaCliente")
    private List<VentaCliente> ventasCliente;

    @OneToMany(mappedBy = "cuenta")
    private List<Abono> abonos;

    public CuentaCliente() {
    }

    public CuentaCliente(Long id, String nombre, String descripcion, Float saldo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getSaldo() {
        return saldo;
    }

    public void setSaldo(Float saldo) {
        this.saldo = saldo;
    }

    public List<VentaCliente> getVentasCliente() {
        return ventasCliente;
    }

    public void setVentasCliente(List<VentaCliente> ventasCliente) {
        this.ventasCliente = ventasCliente;
    }

    public List<Abono> getAbonos() {
        return abonos;
    }

    public void setAbonos(List<Abono> abonos) {
        this.abonos = abonos;
    }

    
}
