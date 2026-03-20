package com.tienda.inventario.controllers;

import java.util.ArrayList;
import java.util.List;

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
import com.tienda.inventario.entities.VentaCliente;
import com.tienda.inventario.services.AbonoService;
import com.tienda.inventario.services.CuentaClienteService;
import com.tienda.inventario.services.VentaClienteService;
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
    private final AbonoService abonoService;

    public CuentaClienteController(
            CuentaClienteService cuentaClienteService,
            VentaService ventaService,
            VentaClienteService ventaClienteService,
            AbonoService abonoService) {
        this.cuentaClienteService = cuentaClienteService;
        this.ventaService = ventaService;
        this.ventaClienteService = ventaClienteService;
        this.abonoService = abonoService;
    }

    /**
     * POST /api/cuentas/{id}/abonar?monto=XX&tipoPago=PESOS
     * Retorna el objeto Abono guardado directamente (no un wrapper).
     * El frontend puede leer: res.data.id, res.data.cantidad, res.data.viejoSaldo, res.data.nuevoSaldo, res.data.tipoPago
     */
    @PostMapping("/{id}/abonar")
    public ResponseEntity<Abono> abonar(
            @PathVariable Integer id,
            @RequestParam("monto") Float monto,
            @RequestParam(value = "tipoPago", defaultValue = "PESOS") String tipoPago) {

        if (monto == null || monto <= 0f) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Abono abono = abonoService.abonarACuenta(id, monto, tipoPago);
            return ResponseEntity.ok(abono);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
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
        System.out.println("\uD83D\uDD0D === DETALLES REQUEST ID=" + id + " ===");
        try {
            CuentaClienteDetallesDto detalles = cuentaClienteService.getDetallesById(id.longValue());
            return ResponseEntity.ok(detalles);
        } catch (Exception e) {
            System.err.println("\u274C ERROR detalles ID=" + id);
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
                String sql = """
                    SELECT 
                        cc.id, cc.nombre, cc.descripcion, cc.saldo,
                        COALESCE(SUM(a.cantidad), 0) as total_abonos,
                        COALESCE(COUNT(vc.id), 0) as total_ventas,
                        COALESCE(SUM(v.total), 0) as total_facturado,
                        COALESCE(SUM(a.cantidad), 0) as total_pagado,
                        COALESCE(cc.saldo, 0) as deuda_pendiente
                    FROM cuenta_cliente cc
                    LEFT JOIN abonos a ON a.cuenta_id = cc.id
                    LEFT JOIN ventas_cliente vc ON vc.cuenta_id = cc.id
                    LEFT JOIN ventas v ON v.id = vc.venta_id
                    GROUP BY cc.id, cc.nombre, cc.descripcion, cc.saldo
                    ORDER BY cc.nombre
                """;

                List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
                List<CuentaClienteDetallesDto> cuentas = new ArrayList<>();

                for (Object[] row : rows) {
                    CuentaClienteDetallesDto dto = new CuentaClienteDetallesDto(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).floatValue(),
                        ((Number) row[4]).doubleValue(),
                        ((Long) row[5]).intValue(),
                        ((Number) row[6]).doubleValue(),
                        ((Number) row[7]).doubleValue(),
                        ((Number) row[8]).doubleValue(),
                        new ArrayList<>(),
                        new ArrayList<>()
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
