package com.tienda.inventario.dto;

import lombok.Data;

@Data
public class VentaClienteDto {
    private Long id;
    private Long ventaId;
    private Double pagocliente;
    private String fecha;
    private String status;
    private Double totalVenta;

    public VentaClienteDto() {
    }

    public VentaClienteDto(Long id, Long ventaId, Double pagocliente, String fecha, String status, Double totalVenta) {
        this.id = id;
        this.ventaId = ventaId;
        this.pagocliente = pagocliente;
        this.fecha = fecha;
        this.status = status;
        this.totalVenta = totalVenta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Double getPagocliente() {
        return pagocliente;
    }

    public void setPagocliente(Double pagocliente) {
        this.pagocliente = pagocliente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(Double totalVenta) {
        this.totalVenta = totalVenta;
    }

}
