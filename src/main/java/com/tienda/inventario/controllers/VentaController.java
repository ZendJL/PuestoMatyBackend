package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.services.VentaService;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:5173")  // ✅ Para Vite dev
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
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Integer id) {
        Venta v = ventaService.buscarPorId(id);
        return v != null ? ResponseEntity.ok(v) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Venta> crear(@RequestBody Venta venta) {
        Venta guardada = ventaService.crearVentaConProductos(venta);
        return ResponseEntity.ok(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> actualizar(@PathVariable Integer id, @RequestBody Venta venta) {
        Venta existente = ventaService.buscarPorId(id);
        if (existente == null) return ResponseEntity.notFound().build();
        venta.setId(id);
        return ResponseEntity.ok(ventaService.guardar(venta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rango")
    public List<Venta> ventasEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ventaService.ventasEntreFechas(desde, hasta);
    }

    @GetMapping("/status/{status}")
    public List<Venta> ventasPorStatus(@PathVariable String status) {
        return ventaService.ventasPorStatus(status);
    }

    @GetMapping("/{id}/productos")
    public ResponseEntity<List<VentaProducto>> productosDeVenta(@PathVariable Integer id) {
        Venta v = ventaService.buscarPorId(id);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ventaService.productosDeVenta(v));
    }

    @GetMapping("/{id}/costos-lotes")
    public ResponseEntity<List<?>> costosPorLotes(@PathVariable Integer id) {
        try {
            List<Object[]> filas = ventaService.costosPorLotesDeVenta(id);

            List<?> respuesta = filas.stream().map(arr -> {
                return java.util.Map.of(
                    "productoId", arr[0],
                    "productoDescripcion", arr[1],
                    "loteId", arr[2],
                    "fechaCompra", arr[3],
                    "cantidad", arr[4],
                    "costoUnitario", arr[5],
                    "costoTotal", arr[6]
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
