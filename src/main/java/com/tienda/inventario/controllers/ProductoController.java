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

    // productos activos para vender
    @GetMapping("/activos")
    public List<Producto> listarActivos() {
        return productoService.findByActivoTrue(); // usa findByActivoTrue()
    }

    // ProductoController.java
    @PostMapping("/{id}/agregar-stock")
    public ResponseEntity<Producto> agregarStock(
            @PathVariable Integer id,
            @RequestParam("cantidad") Integer cantidadAgregar) {

        if (cantidadAgregar == null || cantidadAgregar <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        Integer nuevoStock = (producto.getCantidad() == null ? 0 : producto.getCantidad())
                + cantidadAgregar;
        producto.setCantidad(nuevoStock);
        producto.setUltimaCompra(LocalDateTime.now());

        Producto guardado = productoService.guardar(producto);
        return ResponseEntity.ok(guardado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
        Producto p = productoService.buscarPorId(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto p) {
        if (p.getActivo() == null) {
            p.setActivo(true); // por defecto activo
        }
        return ResponseEntity.ok(productoService.guardar(p));
    }

    @PutMapping("/{id}")
public ResponseEntity<Producto> actualizar(@PathVariable Integer id,
                                           @RequestBody Producto req) {
    Producto existente = productoService.buscarPorId(id);
    if (existente == null) return ResponseEntity.notFound().build();

    existente.setCodigo(req.getCodigo());              // ← importante
    existente.setDescripcion(req.getDescripcion());
    existente.setPrecio(req.getPrecio());
    existente.setProveedor(req.getProveedor());
    existente.setCantidad(req.getCantidad());
    existente.setActivo(req.getActivo());

    Producto guardado = productoService.guardar(existente);
    return ResponseEntity.ok(guardado);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock")
    public List<Producto> productosConStock() {
        return productoService.productosConStock();
    }

    @GetMapping("/ventas")
    public List<Producto> productosCompradosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return productoService.productosCompradosEnRango(desde, hasta);
    }
}
