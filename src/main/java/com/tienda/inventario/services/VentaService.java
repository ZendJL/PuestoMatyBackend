package com.tienda.inventario.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.tienda.inventario.dto.VentaProductoResumenDto;
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

    Venta crearVentaConProductos(Venta venta);

    List<Object[]> costosPorLotesDeVenta(Integer ventaId);

    List<VentaProductoResumenDto> obtenerVentasPorProducto(LocalDate desde, LocalDate hasta);

    public Map<String, Object> getReporteVentas(LocalDateTime desde, LocalDateTime hasta);

public List<Map<String, Object>> ventasReporteGenerales(LocalDateTime desde, LocalDateTime hasta);

public List<Map<String, Object>> productosDeVenta(Integer ventaId);
public List<Map<String, Object>> costosPorLotesDeVentaOptimizado(Integer ventaId);

}
