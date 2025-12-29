package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.services.MermaService;

@RestController
@RequestMapping("/api/mermas")
public class MermaController {

    private final MermaService mermaService;

    public MermaController(MermaService mermaService) {
        this.mermaService = mermaService;
    }

    @GetMapping
    public ResponseEntity<List<Merma>> listar() {
        List<Merma> mermas = mermaService.listar();
        return ResponseEntity.ok(mermas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merma> obtenerPorId(@PathVariable Integer id) {
        Merma m = mermaService.buscarPorId(id);
        return m != null ? ResponseEntity.ok(m) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Merma> crear(@RequestBody Merma merma) {

        if (merma.getFecha() == null) {
            merma.setFecha(LocalDateTime.now());
        }

        if (merma.getMermaProductos() == null || merma.getMermaProductos().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Merma guardada = mermaService.crearMermaConProductos(merma);
        return ResponseEntity.ok(guardada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        mermaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

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

    // Para que el frontend pueda mostrar el mensaje de validación
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // Costo estimado FIFO para un solo producto (no modifica BD)
    @GetMapping("/costo")
    public ResponseEntity<Float> costoMerma(
            @RequestParam("productoId") Integer productoId,
            @RequestParam("cantidad") Integer cantidad) {

        float costo = mermaService.calcularCostoMermaProducto(productoId, cantidad);
        return ResponseEntity.ok(costo);
    }

}
