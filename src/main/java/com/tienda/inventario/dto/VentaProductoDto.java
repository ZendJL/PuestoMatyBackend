package com.tienda.inventario.dto;



public class VentaProductoDto {
    private Long id;
    private Integer cantidad;
    private Double precioUnitario;
    private ProductoDto producto;
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
    public ProductoDto getProducto() { return producto; }
    public void setProducto(ProductoDto producto) { this.producto = producto; }
}