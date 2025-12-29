package com.tienda.inventario.dto;

import java.sql.Timestamp;

public class CuentaClienteResumenDto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Float saldo;
    private Double totalFacturado;
    private Integer totalVentas;
    private Double totalPagado;
    private Double saldoCalculado;
    private Timestamp ultimaActividad;

    // ✅ CONSTRUCTOR CON 9 PARÁMETROS (OBLIGATORIO)
    public CuentaClienteResumenDto(Integer id, String nombre, String descripcion, Float saldo,
                                   Double totalFacturado, Integer totalVentas, Double totalPagado, 
                                   Double saldoCalculado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.saldo = saldo;
        this.totalFacturado = totalFacturado;
        this.totalVentas = totalVentas;
        this.totalPagado = totalPagado;
        this.saldoCalculado = saldoCalculado;
    }

    // ✅ GETTERS Y SETTERS COMPLETOS
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Float getSaldo() { return saldo; }
    public void setSaldo(Float saldo) { this.saldo = saldo; }

    public Double getTotalFacturado() { return totalFacturado; }
    public void setTotalFacturado(Double totalFacturado) { this.totalFacturado = totalFacturado; }

    public Integer getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Integer totalVentas) { this.totalVentas = totalVentas; }

    public Double getTotalPagado() { return totalPagado; }
    public void setTotalPagado(Double totalPagado) { this.totalPagado = totalPagado; }

    public Double getSaldoCalculado() { return saldoCalculado; }
    public void setSaldoCalculado(Double saldoCalculado) { this.saldoCalculado = saldoCalculado; }

    public Timestamp getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(Timestamp ultimaActividad) { this.ultimaActividad = ultimaActividad; }
}
