package com.tienda.inventario.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.services.VentaService;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listarTodas() {
        return ventaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        Venta v = ventaService.buscarPorId(id);
        return v != null ? ResponseEntity.ok(v) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@RequestBody Venta venta) {
        return ResponseEntity.ok(ventaService.guardar(venta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizar(@PathVariable Long id, @RequestBody Venta venta) {
        Venta existente = ventaService.buscarPorId(id);
        if (existente == null) return ResponseEntity.notFound().build();
        venta.setId(id);
        return ResponseEntity.ok(ventaService.guardar(venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rango")
    public List<Venta> ventasEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date hasta) {
        return ventaService.ventasEntreFechas(desde, hasta);
    }

    @GetMapping("/status/{status}")
    public List<Venta> ventasPorStatus(@PathVariable String status) {
        return ventaService.ventasPorStatus(status);
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<VentaProducto>> productosDeVenta(@PathVariable Long id) {
        Venta v = ventaService.buscarPorId(id);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ventaService.productosDeVenta(v));
    }
}
