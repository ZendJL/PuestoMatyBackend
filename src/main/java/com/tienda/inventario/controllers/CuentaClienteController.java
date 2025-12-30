package com.tienda.inventario.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.tienda.inventario.dto.CuentaClienteDetallesDto;
import com.tienda.inventario.dto.CuentaClienteResumenDto;
import com.tienda.inventario.dto.CuentaResumenDto;
import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.VentaCliente; // ✅ AGREGADO
import com.tienda.inventario.services.AbonoService;
import com.tienda.inventario.services.CuentaClienteService;
import com.tienda.inventario.services.VentaClienteService; // ✅ AGREGADO
import com.tienda.inventario.services.VentaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "http://localhost:5173")
public class CuentaClienteController {

    private final VentaService ventaService;
    private final VentaClienteService ventaClienteService;
    private final CuentaClienteService cuentaClienteService;
    private final AbonoService abonoService; // ✅ AGREGADO

    // ✅ CONSTRUCTOR CON AbonoService
    public CuentaClienteController(
            CuentaClienteService cuentaClienteService,
            VentaService ventaService,
            VentaClienteService ventaClienteService,
            AbonoService abonoService) { // ✅ AGREGADO
        this.cuentaClienteService = cuentaClienteService;
        this.ventaService = ventaService;
        this.ventaClienteService = ventaClienteService;
        this.abonoService = abonoService; // ✅ AGREGADO
    }

    // ✅ MÉTODO ABONAR CORREGIDO - AHORA SÍ GUARDA EN TABLA ABONOS
    @PostMapping("/{id}/abonar")
    public ResponseEntity<Map<String, Object>> abonar(
            @PathVariable Integer id,
            @RequestParam("monto") Float monto) {
        
        if (monto == null || monto <= 0f) {
            return ResponseEntity.badRequest().body(Map.of("error", "Monto inválido"));
        }

        try {
            // ✅ 1. Buscar cuenta
            CuentaCliente cuenta = cuentaClienteService.buscarPorId(id);
            if (cuenta == null) {
                return ResponseEntity.notFound().build();
            }
            
            // ✅ 2. Crear ABONO
            Abono abono = new Abono();
            abono.setCuenta(cuenta);
            abono.setCantidad(monto);
            abono.setViejoSaldo(cuenta.getSaldo() == null ? 0f : cuenta.getSaldo());
            abono.setNuevoSaldo(abono.getViejoSaldo() - monto);
            
            // ✅ 3. GUARDAR ABONO EN TABLA ABONOS
            abonoService.guardar(abono);
            
            // ✅ 4. Actualizar saldo cuenta
            Float saldoActual = cuenta.getSaldo() == null ? 0f : cuenta.getSaldo();
            Float nuevoSaldo = Math.max(0f, saldoActual - monto);
            cuenta.setSaldo(nuevoSaldo);
            cuentaClienteService.guardar(cuenta);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "abono", abono,
                "nuevoSaldo", nuevoSaldo,
                "mensaje", "Abono registrado correctamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public List<CuentaCliente> listarTodas() {
        return cuentaClienteService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaCliente> obtenerPorId(@PathVariable Integer id) {
        CuentaCliente c = cuentaClienteService.buscarPorId(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CuentaCliente> crear(@RequestBody CuentaCliente cuenta) {
        return ResponseEntity.ok(cuentaClienteService.guardar(cuenta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaCliente> actualizar(@PathVariable Integer id, @RequestBody CuentaCliente cuenta) {
        CuentaCliente existente = cuentaClienteService.buscarPorId(id);
        if (existente == null)
            return ResponseEntity.notFound().build();
        cuenta.setId(id);
        return ResponseEntity.ok(cuentaClienteService.guardar(cuenta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        cuentaClienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public List<CuentaCliente> buscarPorNombre(@RequestParam String nombre) {
        return cuentaClienteService.buscarPorNombre(nombre);
    }

    @GetMapping("/deuda")
    public List<CuentaCliente> cuentasConDeuda() {
        return cuentaClienteService.cuentasConDeuda();
    }

    @GetMapping("/sindeuda")
    public List<CuentaCliente> cuentasSinDeudaONegro() {
        return cuentaClienteService.cuentasSinDeudaONegro();
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<CuentaClienteResumenDto>> resumenCompleto() {
        List<CuentaClienteResumenDto> resumen = cuentaClienteService.resumenCompleto();
        return ResponseEntity.ok(resumen);
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<?> detalles(@PathVariable Integer id) {
        System.out.println("🔍 === DETALLES REQUEST ID=" + id + " ===");
        try {
            System.out.println("✅ Service inyectado: " + (cuentaClienteService != null));
            CuentaClienteDetallesDto detalles = cuentaClienteService.getDetallesById(id.longValue());
            System.out.println("✅ Detalles OK: " + detalles.getNombre());
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            System.err.println("❌ ERROR detalles ID=" + id + ":");
            e.printStackTrace();
            return ResponseEntity.status(500).body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/{cuentaId}/resumen")
    public ResponseEntity<CuentaResumenDto> resumenCuenta(@PathVariable Integer cuentaId) {
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(cuentaId);
        if (cuenta == null) {
            return ResponseEntity.notFound().build();
        }

        List<VentaCliente> ventasCliente = ventaClienteService.ventasDeCuenta(cuenta);

        double totalVentasMonto = ventasCliente.stream()
            .mapToDouble(vc -> vc.getVenta().getTotal())
            .sum();

        CuentaResumenDto resumen = new CuentaResumenDto(
            cuenta.getId(),
            cuenta.getNombre(),
            cuenta.getSaldo(),
            ventasCliente.size(),
            0,
            (float) totalVentasMonto
        );

        return ResponseEntity.ok(resumen);
    }
    
   @RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "http://localhost:5173")
public class CuentaController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/optimizadas-pos")
    public ResponseEntity<List<CuentaClienteDetallesDto>> getCuentasOptimizadasPos() {
        try {
            // ✅ 1 SOLA QUERY con todos los JOINs
            String sql = """
                SELECT 
                    cc.id, cc.nombre, cc.descripcion, cc.saldo,
                    COALESCE(SUM(a.cantidad), 0) as total_abonos,
                    COALESCE(COUNT(vc.id), 0) as total_ventas,
                    COALESCE(SUM(v.total), 0) as total_facturado,
                    COALESCE(SUM(a.cantidad), 0) as total_pagado,
                    COALESCE(cc.saldo, 0) as deuda_pendiente,
                    NULL as abono_id, NULL as abono_fecha, NULL as abono_cantidad,  -- ultimosAbonos
                    NULL as venta_id, NULL as venta_fecha, NULL as venta_total     -- ultimasVentas
                FROM cuenta_cliente cc
                LEFT JOIN abonos a ON a.cuenta_id = cc.id
                LEFT JOIN ventas_cliente vc ON vc.cuenta_id = cc.id
                LEFT JOIN ventas v ON v.id = vc.venta_id
                GROUP BY cc.id, cc.nombre, cc.descripcion, cc.saldo
                ORDER BY cc.nombre
            """;

            List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
            
            // ✅ Transformar a tu DTO (SIN listas detalladas para POS)
            List<CuentaClienteDetallesDto> cuentas = new ArrayList<>();
            
            for (Object[] row : rows) {
                CuentaClienteDetallesDto dto = new CuentaClienteDetallesDto(
                    ((Number) row[0]).longValue(),           // id
                    (String) row[1],                        // nombre
                    (String) row[2],                        // descripcion
                    ((Number) row[3]).floatValue(),         // saldo
                    ((Number) row[4]).doubleValue(),        // totalAbonos
                    ((Long) row[5]).intValue(),             // totalVentas
                    ((Number) row[6]).doubleValue(),        // totalFacturado
                    ((Number) row[7]).doubleValue(),        // totalPagado
                    ((Number) row[8]).doubleValue(),        // deudaPendiente
                    new ArrayList<>(),                      // ultimosAbonos (vacío)
                    new ArrayList<>()                       // ultimasVentas (vacío)
                );
                cuentas.add(dto);
            }
            
            return ResponseEntity.ok(cuentas);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}

}
