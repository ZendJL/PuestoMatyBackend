package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.services.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }

    // ProductoController.java
@PostMapping("/{id}/agregar-stock")
public ResponseEntity<Producto> agregarStock(
        @PathVariable Long id,
        @RequestParam("cantidad") Integer cantidadAgregar) {

    if (cantidadAgregar == null || cantidadAgregar <= 0) {
        return ResponseEntity.badRequest().build();
    }

    Producto producto = productoService.buscarPorId(id);
    if (producto == null) {
        return ResponseEntity.notFound().build();
    }

    Long nuevoStock = (producto.getCantidad() == null ? 0 : producto.getCantidad())
        + cantidadAgregar;
    producto.setCantidad(nuevoStock);
    producto.setUltimaCompra(LocalDateTime.now());

    Producto guardado = productoService.guardar(producto);
    return ResponseEntity.ok(guardado);
}


    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Producto p = productoService.buscarPorId(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        Producto existente = productoService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        producto.setId(id);
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock")
    public List<Producto> productosConStock() {
        return productoService.productosConStock();
    }

    @GetMapping("/ventas")
    public List<Producto> productosVendidosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return productoService.productosVendidosEnRango(desde, hasta);
    }

    @GetMapping("/compras")
    public List<Producto> productosCompradosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return productoService.productosCompradosEnRango(desde, hasta);
    }
}
