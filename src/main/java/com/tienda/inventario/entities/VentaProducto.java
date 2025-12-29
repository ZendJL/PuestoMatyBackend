package com.tienda.inventario.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "venta_productos")
public class VentaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonIgnore
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Float precioUnitario;

    @Column(name = "importe")
    private Float importe;

    @Column(name = "costo_total")
    private Float costoTotal;

    // NUEVA RELACIÓN: lotes consumidos de esta línea
    @OneToMany(mappedBy = "ventaProducto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaProductoLote> lotesConsumidos = new ArrayList<>();

    public VentaProducto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Float getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Float precioUnitario) { this.precioUnitario = precioUnitario; }

    public Float getImporte() { return importe; }
    public void setImporte(Float importe) { this.importe = importe; }

    public Float getCostoTotal() { return costoTotal; }
    public void setCostoTotal(Float costoTotal) { this.costoTotal = costoTotal; }

    public List<VentaProductoLote> getLotesConsumidos() { return lotesConsumidos; }
    public void setLotesConsumidos(List<VentaProductoLote> lotesConsumidos) {
        this.lotesConsumidos = lotesConsumidos;
    }
}
