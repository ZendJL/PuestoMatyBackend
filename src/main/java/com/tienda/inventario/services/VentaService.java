package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;

public interface VentaService {

    Venta guardar(Venta venta);

    Venta buscarPorId(Integer id);

    List<Venta> listarTodas();

    void eliminar(Integer id);

    List<Venta> ventasEntreFechas(LocalDateTime desde, LocalDateTime hasta);

    List<Venta> ventasPorStatus(String status);

    List<VentaProducto> productosDeVenta(Venta venta);

     //Venta crearVentaConProductos(Venta venta, List<Integer> productosIds, Float cantidad);
// VentaService
Venta crearVentaConProductos(Venta venta);

    
}
