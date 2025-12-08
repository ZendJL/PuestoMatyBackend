package com.tienda.inventario.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.services.CuentaClienteService;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaClienteController {

    private final CuentaClienteService cuentaClienteService;

    public CuentaClienteController(CuentaClienteService cuentaClienteService) {
        this.cuentaClienteService = cuentaClienteService;
    }

    @GetMapping
    public List<CuentaCliente> listarTodas() {
        return cuentaClienteService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaCliente> obtenerPorId(@PathVariable Long id) {
        CuentaCliente c = cuentaClienteService.buscarPorId(id);
        return c != null ? ResponseEntity.ok(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<CuentaCliente> crear(@RequestBody CuentaCliente cuenta) {
        return ResponseEntity.ok(cuentaClienteService.guardar(cuenta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaCliente> actualizar(@PathVariable Long id, @RequestBody CuentaCliente cuenta) {
        CuentaCliente existente = cuentaClienteService.buscarPorId(id);
        if (existente == null) return ResponseEntity.notFound().build();
        cuenta.setId(id);
        return ResponseEntity.ok(cuentaClienteService.guardar(cuenta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
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
}
