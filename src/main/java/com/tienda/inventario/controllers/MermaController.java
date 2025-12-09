package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Merma;
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
    public ResponseEntity<List<Merma>> listar() {
        List<Merma> mermas = mermaService.listar();
        return ResponseEntity.ok(mermas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merma> obtenerPorId(@PathVariable Long id) {
        Merma m = mermaService.buscarPorId(id);
        return m != null ? ResponseEntity.ok(m) : ResponseEntity.notFound().build();
    }

    @PostMapping
public ResponseEntity<Merma> crear(@RequestBody Merma merma) {
    Merma guardada = mermaService.guardar(merma);
    return ResponseEntity.ok(guardada);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mermaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // tipoMerma: EXPIRADO, USO_PERSONAL, MAL_ESTADO, etc.
    @GetMapping("/tipo")
    public List<Merma> mermasPorTipoYRango(
            @RequestParam String tipoMerma,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return mermaService.mermasPorTipoYRango(tipoMerma, desde, hasta);
    }

    @GetMapping("/rango")
    public List<Merma> mermasEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return mermaService.mermasEntreFechas(desde, hasta);
    }
}
