package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.entities.VentaProductoRequest;

public interface VentaService {

    Venta guardar(Venta venta);

    Venta buscarPorId(Long id);

    List<Venta> listarTodas();

    void eliminar(Long id);

    List<Venta> ventasEntreFechas(LocalDateTime desde, LocalDateTime hasta);

    List<Venta> ventasPorStatus(String status);

    List<VentaProducto> productosDeVenta(Venta venta);

    Venta crearVentaConProductos(Venta venta, List<Long> productosIds);

    Venta crearVentaConItems(Venta venta, List<VentaProductoRequest> items);

    
}
