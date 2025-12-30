package com.tienda.inventario.dto;

public class MermaCostoRequestDto {
    public Integer productoId;
    public Integer cantidad;
    
    public MermaCostoRequestDto() {}
    
    public MermaCostoRequestDto(Integer productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }
}
