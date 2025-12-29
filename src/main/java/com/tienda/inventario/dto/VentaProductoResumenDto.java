package com.tienda.inventario.dto;

public class VentaProductoResumenDto {

    private Integer productoId;
    private String codigo;
    private String descripcion;
    private Long totalCantidad;
    private Double totalVentas;
    private Double totalCosto;

    public VentaProductoResumenDto(
            Integer productoId,
            String codigo,
            String descripcion,
            Long totalCantidad,
            Double totalVentas,
            Double totalCosto) {
        this.productoId = productoId;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.totalCantidad = totalCantidad;
        this.totalVentas = totalVentas;
        this.totalCosto = totalCosto;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Long getTotalCantidad() {
        return totalCantidad;
    }

    public Double getTotalVentas() {
        return totalVentas;
    }

    public Double getTotalCosto() {
        return totalCosto;
    }

    public Double getTotalGanancia() {
        return totalVentas - totalCosto;
    }
}
