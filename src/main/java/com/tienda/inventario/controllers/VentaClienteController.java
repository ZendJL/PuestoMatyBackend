package com.tienda.inventario.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaCliente;
import com.tienda.inventario.services.CuentaClienteService;
import com.tienda.inventario.services.VentaClienteService;
import com.tienda.inventario.services.VentaService;

@RestController
@RequestMapping("/api/ventas-cuenta")
public class VentaClienteController {

    private final VentaClienteService ventaClienteService;
    private final VentaService ventaService;
    private final CuentaClienteService cuentaClienteService;

    public VentaClienteController(VentaClienteService ventaClienteService,
                                  VentaService ventaService,
                                  CuentaClienteService cuentaClienteService) {
        this.ventaClienteService = ventaClienteService;
        this.ventaService = ventaService;
        this.cuentaClienteService = cuentaClienteService;
    }

    // Crear relación venta-cuenta (asociar una venta a una cuenta de cliente)
    @PostMapping
    public ResponseEntity<VentaCliente> crear(@RequestParam Integer ventaId,
                                              @RequestParam Integer cuentaId) {
        Venta venta = ventaService.buscarPorId(ventaId);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(cuentaId);
        if (cuenta == null) {
            return ResponseEntity.notFound().build();
        }
        VentaCliente vc = new VentaCliente(venta, cuenta);
        return ResponseEntity.ok(ventaClienteService.guardar(vc));
    }

    // Listar ventas asociadas a una cuenta
    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<VentaCliente>> ventasDeCuenta(@PathVariable Integer cuentaId) {
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(cuentaId);
        if (cuenta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ventaClienteService.ventasDeCuenta(cuenta));
    }

    // Listar cuentas asociadas a una venta
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<VentaCliente>> cuentasDeVenta(@PathVariable Integer ventaId) {
        Venta venta = ventaService.buscarPorId(ventaId);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ventaClienteService.cuentasDeVenta(venta));
    }
}
