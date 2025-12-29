package com.tienda.inventario.dto;

public class CuentaResumenDto {
    private Integer id;
    private String nombre;
    private Float saldo;
    private Integer totalVentas;
    private Integer totalAbonos;
    private Float totalVentasMonto;

    public CuentaResumenDto() {}

    public CuentaResumenDto(Integer id, String nombre, Float saldo, 
                           Integer totalVentas, Integer totalAbonos, Float totalVentasMonto) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
        this.totalVentas = totalVentas;
        this.totalAbonos = totalAbonos;
        this.totalVentasMonto = totalVentasMonto;
    }

    // Getters y Setters COMPLETOS
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Float getSaldo() { return saldo; }
    public void setSaldo(Float saldo) { this.saldo = saldo; }

    public Integer getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Integer totalVentas) { this.totalVentas = totalVentas; }

    public Integer getTotalAbonos() { return totalAbonos; }
    public void setTotalAbonos(Integer totalAbonos) { this.totalAbonos = totalAbonos; }

    public Float getTotalVentasMonto() { return totalVentasMonto; }
    public void setTotalVentasMonto(Float totalVentasMonto) { this.totalVentasMonto = totalVentasMonto; }
}
