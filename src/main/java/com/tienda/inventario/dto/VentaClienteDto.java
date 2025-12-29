package com.tienda.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaClienteDto {
    private Long id;
    private Long ventaId;
    private Double pagocliente;
    private String fecha;
    private String status;
    private Double totalVenta;
}
