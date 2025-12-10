package com.tienda.inventario.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.repositories.VentaProductoRepository;
import com.tienda.inventario.services.VentaService;

@RestController
@RequestMapping("/api/ventas-productos")
public class VentaProductoController {

    private final VentaService ventaService;
    private final VentaProductoRepository ventaProductoRepository;

    public VentaProductoController(VentaService ventaService,
                                   VentaProductoRepository ventaProductoRepository) {
        this.ventaService = ventaService;
        this.ventaProductoRepository = ventaProductoRepository;
    }

    // Todas las líneas de venta (para ReporteVentasPorProducto.jsx)
    @GetMapping
    public ResponseEntity<List<VentaProducto>> listarTodas() {
        List<VentaProducto> lista = ventaProductoRepository.findAll();
        return ResponseEntity.ok(lista);
    }

    // Detalle por venta (lo que ya tenías)
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<VentaProducto>> obtenerProductosDeVenta(@PathVariable Integer ventaId) {
        Venta venta = ventaService.buscarPorId(ventaId);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }
        List<VentaProducto> detalles = ventaService.productosDeVenta(venta);
        return ResponseEntity.ok(detalles);
    }
}
