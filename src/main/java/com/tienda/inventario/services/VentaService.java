package com.tienda.inventario.services;

import java.util.Date;
import java.util.List;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;

public interface VentaService {

    Venta guardar(Venta venta);

    Venta buscarPorId(Long id);

    List<Venta> listarTodas();

    void eliminar(Long id);

    List<Venta> ventasEntreFechas(Date desde, Date hasta);

    List<Venta> ventasPorStatus(String status);

    List<VentaProducto> productosDeVenta(Venta venta);
}
