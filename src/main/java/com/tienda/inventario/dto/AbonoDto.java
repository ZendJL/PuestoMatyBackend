package com.tienda.inventario.dto;

import lombok.Data;

@Data
public class AbonoDto {
    private Long id;
    private Double cantidad;
    private Double viejoSaldo;
    private Double nuevoSaldo;
    private String fecha;

    public AbonoDto() {
    }

    public AbonoDto(Long id, Double cantidad, Double viejoSaldo, Double nuevoSaldo, String fecha) {
        this.id = id;
        this.cantidad = cantidad;
        this.viejoSaldo = viejoSaldo;
        this.nuevoSaldo = nuevoSaldo;
        this.fecha = fecha;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setViejoSaldo(Double viejoSaldo) {
        this.viejoSaldo = viejoSaldo;
    }

    public Double getViejoSaldo() {
        return viejoSaldo;
    }

    public void setNuevoSaldo(Double nuevoSaldo) {
        this.nuevoSaldo = nuevoSaldo;
    }

    public Double getNuevoSaldo() {
        return nuevoSaldo;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getGecha() {
        return fecha;
    }

}
