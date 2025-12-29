package com.tienda.inventario.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "venta_producto_lotes")
public class VentaProductoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_producto_id", nullable = false)
    @JsonIgnore
    private VentaProducto ventaProducto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "compra_producto_id", nullable = false)
    private CompraProducto compraProducto;

    @Column(name = "cantidad_consumida", nullable = false)
    private Integer cantidadConsumida;

    @Column(name = "costo_unitario", nullable = false)
    private Float costoUnitario;

    @Column(name = "costo_total", nullable = false)
    private Float costoTotal;

    @Column(name = "fecha_consumo", nullable = false)
    private LocalDateTime fechaConsumo;

    public VentaProductoLote() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public VentaProducto getVentaProducto() { return ventaProducto; }
    public void setVentaProducto(VentaProducto ventaProducto) { this.ventaProducto = ventaProducto; }

    public CompraProducto getCompraProducto() { return compraProducto; }
    public void setCompraProducto(CompraProducto compraProducto) { this.compraProducto = compraProducto; }

    public Integer getCantidadConsumida() { return cantidadConsumida; }
    public void setCantidadConsumida(Integer cantidadConsumida) { this.cantidadConsumida = cantidadConsumida; }

    public Float getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(Float costoUnitario) { this.costoUnitario = costoUnitario; }

    public Float getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Float costoTotal) { this.costoTotal = costoTotal; }

    public LocalDateTime getFechaConsumo() { return fechaConsumo; }
    public void setFechaConsumo(LocalDateTime fechaConsumo) { this.fechaConsumo = fechaConsumo; }
}
