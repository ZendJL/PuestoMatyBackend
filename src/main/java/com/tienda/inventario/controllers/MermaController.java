package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

import com.tienda.inventario.dto.MermaCostoRequestDto;
import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.repositories.MermaRepository;
import com.tienda.inventario.services.MermaService;

@RestController
@RequestMapping("/api/mermas")
public class MermaController {

    private final MermaService mermaService;
    private final MermaRepository mermaRepository;
    

    public MermaController(MermaService mermaService,MermaRepository mermaRepository) {
        this.mermaService = mermaService;
        this.mermaRepository = mermaRepository;
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

    // ✅ REPORTES OPTIMIZADOS (1 query cada uno)
// ✅ REPORTE OPTIMIZADO (agregar al final)
// ✅ AGREGAR filtros en reporte:
@GetMapping("/reporte")
public ResponseEntity<List<Map<String, Object>>> reporteMermas(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
        @RequestParam(required = false) String tipo) {
    
    LocalDateTime dDesde = desde.withHour(0).withMinute(0).withSecond(0);
    LocalDateTime dHasta = hasta.withHour(23).withMinute(59).withSecond(59);
    
    List<Map<String, Object>> reporte;
    if (tipo != null && !tipo.isEmpty()) {
        reporte = mermaService.reporteMermasPorTipoCompleto(dDesde, dHasta, tipo); // ✅ 1 QUERY
    } else {
        reporte = mermaService.reporteMermasCompleto(dDesde, dHasta); // ✅ 1 QUERY
    }
    return ResponseEntity.ok(reporte);
}


@PostMapping("/costos-batch")
    public ResponseEntity<List<Float>> costosMermaBatch(
            @RequestBody List<MermaCostoRequestDto> requests) {  // ✅ List genérico
        List<Float> costos = mermaService.calcularCostosMermaBatch(requests);
        return ResponseEntity.ok(costos);
    }


}