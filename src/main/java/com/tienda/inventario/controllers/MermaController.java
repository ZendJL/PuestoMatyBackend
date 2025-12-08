package com.tienda.inventario.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.services.MermaService;
import com.tienda.inventario.services.ProductoService;

@RestController
@RequestMapping("/api/mermas")
public class MermaController {

    private final MermaService mermaService;
    private final ProductoService productoService;

    public MermaController(MermaService mermaService, ProductoService productoService) {
        this.mermaService = mermaService;
        this.productoService = productoService;
    }

    @GetMapping
    public List<Merma> listarTodas() {
        return mermaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merma> obtenerPorId(@PathVariable Long id) {
        Merma m = mermaService.buscarPorId(id);
        return m != null ? ResponseEntity.ok(m) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Merma> crear(@RequestBody Merma merma) {
        return ResponseEntity.ok(mermaService.guardar(merma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mermaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Merma>> mermasDeProducto(@PathVariable Long productoId) {
        Producto p = productoService.buscarPorId(productoId);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mermaService.mermasDeProducto(p));
    }

    // tipoMerma: EXPIRADO, USO_PERSONAL, MAL_ESTADO, etc.
    @GetMapping("/tipo")
    public List<Merma> mermasPorTipoYRango(
            @RequestParam String tipoMerma,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date hasta) {
        return mermaService.mermasPorTipoYRango(tipoMerma, desde, hasta);
    }

    @GetMapping("/rango")
    public List<Merma> mermasEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date hasta) {
        return mermaService.mermasEntreFechas(desde, hasta);
    }
}
