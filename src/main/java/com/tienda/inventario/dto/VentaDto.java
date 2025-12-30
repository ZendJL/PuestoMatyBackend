package com.tienda.inventario.dto;

import java.security.Timestamp;

public class VentaDto {
    private Long id;
    private Timestamp fecha;
    private Double total;
    private String status;
    private Double pagoCliente;
    private Long cuentaId;
    private String cuentaNombre;
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getPagoCliente() { return pagoCliente; }
    public void setPagoCliente(Double pagoCliente) { this.pagoCliente = pagoCliente; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public String getCuentaNombre() { return cuentaNombre; }
    public void setCuentaNombre(String cuentaNombre) { this.cuentaNombre = cuentaNombre; }
}
