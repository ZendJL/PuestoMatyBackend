package com.tienda.inventario.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.services.VentaService;

@RestController
@RequestMapping("/api/ventas-productos")
public class VentaProductoController {

    private final VentaService ventaService;

    public VentaProductoController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<VentaProducto>> obtenerProductosDeVenta(@PathVariable Long ventaId) {
        Venta venta = ventaService.buscarPorId(ventaId);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }

        List<VentaProducto> detalles = ventaService.productosDeVenta(venta);
        return ResponseEntity.ok(detalles);
    }
    
}
