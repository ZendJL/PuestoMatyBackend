package com.tienda.inventario.dto;

import java.util.List;

public class CuentaClienteDetallesDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Float saldo;
    private Double totalAbonos;
    private Integer totalVentas;
    private Double totalFacturado;
    private Double totalPagado;
    private Double deudaPendiente;
    private List<AbonoDto> ultimosAbonos;
    private List<VentaClienteDto> ultimasVentas;

    // ✅ CONSTRUCTOR COMPLETO
    public CuentaClienteDetallesDto(Long id, String nombre, String descripcion, Float saldo,
                                   Double totalAbonos, Integer totalVentas, Double totalFacturado,
                                   Double totalPagado, Double deudaPendiente,
                                   List<AbonoDto> ultimosAbonos, List<VentaClienteDto> ultimasVentas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.saldo = saldo;
        this.totalAbonos = totalAbonos;
        this.totalVentas = totalVentas;
        this.totalFacturado = totalFacturado;
        this.totalPagado = totalPagado;
        this.deudaPendiente = deudaPendiente;
        this.ultimosAbonos = ultimosAbonos;
        this.ultimasVentas = ultimasVentas;
    }

    // ✅ GETTERS (solo estos)
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Float getSaldo() { return saldo; }
    public Double getTotalAbonos() { return totalAbonos; }
    public Integer getTotalVentas() { return totalVentas; }
    public Double getTotalFacturado() { return totalFacturado; }
    public Double getTotalPagado() { return totalPagado; }
    public Double getDeudaPendiente() { return deudaPendiente; }
    public List<AbonoDto> getUltimosAbonos() { return ultimosAbonos; }
    public List<VentaClienteDto> getUltimasVentas() { return ultimasVentas; }
}
