package com.tienda.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbonoDto {
    private Long id;
    private Double cantidad;
    private Double viejoSaldo;
    private Double nuevoSaldo;
    private String fecha;
}
