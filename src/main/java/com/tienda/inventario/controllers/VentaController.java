package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.tienda.inventario.services.VentaService;

import jakarta.persistence.EntityManager;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:5173")  // ✅ Para Vite dev
public class VentaController {

    @Autowired
    private EntityManager entityManager;
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

    @GetMapping("/reporte-generales")
public ResponseEntity<List<Map<String, Object>>> reporteVentasGenerales(
    @RequestParam String desde,
    @RequestParam String hasta
) {
    // ✅ FIX: Formato correcto YYYY-MM-DDTHH:MM:SS
    LocalDateTime dDesde = LocalDateTime.parse(desde + "T00:00:00");
    LocalDateTime dHasta = LocalDateTime.parse(hasta + "T23:59:59");
    List<Map<String, Object>> ventas = ventaService.ventasReporteGenerales(dDesde, dHasta);
    return ResponseEntity.ok(ventas);
}
@GetMapping("/{id}/productos")
public ResponseEntity<List<Map<String, Object>>> productosVenta(@PathVariable Integer id) {
    List<Map<String, Object>> productos = ventaService.productosDeVenta(id);
    return ResponseEntity.ok(productos);
}
@GetMapping("/{id}/costos-lotes-optimizado")
public ResponseEntity<List<Map<String, Object>>> costosLotesVentaOptimizada(@PathVariable Integer id) {
    List<Map<String, Object>> lotes = ventaService.costosPorLotesDeVentaOptimizado(id);
    return ResponseEntity.ok(lotes);
}


 @GetMapping("/{id}/ticket-completo")
    public ResponseEntity<?> getTicketCompleto(@PathVariable Long id) {
        try {
            String sql = """
                SELECT 
                    v.id as v_id, v.fecha as v_fecha, v.total as v_total, 
                    COALESCE(v.status, 'COMPLETADA') as v_status, v.pago_cliente as v_pago,
                    cc.id as cc_id, cc.nombre as cc_nombre,
                    vp.id as vp_id, p.id as p_id, p.codigo as p_codigo, 
                    p.descripcion as p_descripcion, vp.cantidad as vp_cant, 
                    vp.precio_unitario as vp_precio
                FROM ventas v
                LEFT JOIN cuenta_cliente cc ON v.cuenta_id = cc.id
                LEFT JOIN venta_productos vp ON vp.venta_id = v.id
                LEFT JOIN productos p ON p.id = vp.producto_id
                WHERE v.id = ?
                ORDER BY vp.id
            """;

            @SuppressWarnings("unchecked")
            List<Object[]> rows = entityManager.createNativeQuery(sql)
                    .setParameter(1, id)
                    .getResultList();

            if (rows.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Transformar resultado
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> venta = new HashMap<>();
            List<Map<String, Object>> productos = new ArrayList<>();

            for (Object[] row : rows) {
                // Primera fila: datos venta
                if (venta.isEmpty()) {
                    venta.put("id", ((Number) row[0]).longValue());
                    venta.put("fecha", row[1].toString());
                    venta.put("total", ((Number) row[2]).doubleValue());
                    venta.put("status", (String) row[3]);
                    venta.put("pagoCliente", row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
                    
                    if (row[5] != null) {
                        venta.put("cuentaId", ((Number) row[5]).longValue());
                        venta.put("cuentaNombre", (String) row[6]);
                    }
                }

                // Productos (si existen)
                if (row[7] != null) {
                    Map<String, Object> producto = new HashMap<>();
                    Map<String, Object> vp = new HashMap<>();
                    
                    vp.put("id", ((Number) row[7]).longValue());
                    vp.put("cantidad", ((Number) row[11]).intValue());
                    vp.put("precioUnitario", ((Number) row[12]).doubleValue());
                    
                    producto.put("id", ((Number) row[8]).longValue());
                    producto.put("codigo", row[9] != null ? (String) row[9] : "");
                    producto.put("descripcion", row[10] != null ? (String) row[10] : "");
                    
                    vp.put("producto", producto);
                    productos.add(vp);
                }
            }

            result.put("venta", venta);
            result.put("productos", productos);

            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
