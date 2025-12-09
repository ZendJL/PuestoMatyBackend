package com.tienda.inventario.controllers;

import java.util.List;

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

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.repositories.AbonoRepository;
import com.tienda.inventario.services.CuentaClienteService;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaClienteController {

    private final CuentaClienteService cuentaClienteService;
    private final AbonoRepository abonoRepository;

    public CuentaClienteController(CuentaClienteService cuentaClienteService,
            AbonoRepository abonoRepository) {
        this.cuentaClienteService = cuentaClienteService;
        this.abonoRepository = abonoRepository;
    }

    @PostMapping("/{id}/abonar")
    public ResponseEntity<CuentaCliente> abonar(
            @PathVariable Long id,
            @RequestParam("monto") Float monto) {

        if (monto == null || monto <= 0f) {
            return ResponseEntity.badRequest().build();
        }

        CuentaCliente cuenta = cuentaClienteService.buscarPorId(id);
        if (cuenta == null) {
            return ResponseEntity.notFound().build();
        }

        Float saldoActual = cuenta.getSaldo() == null ? 0f : cuenta.getSaldo();
        Float nuevoSaldo = saldoActual - monto;
        if (nuevoSaldo < 0f) {
            nuevoSaldo = 0f; // no permitir saldo negativo
        }

        // actualizar saldo
        cuenta.setSaldo(nuevoSaldo);
        CuentaCliente guardada = cuentaClienteService.guardar(cuenta);

        // registrar abono
        Abono abono = new Abono();
        abono.setFecha(java.time.LocalDateTime.now());
        abono.setCuenta(guardada); // campo @ManyToOne CuentaCliente cuenta;
        abono.setCantidad(monto);
        abono.setViejoSaldo(saldoActual);
        abono.setNuevoSaldo(nuevoSaldo);

        abonoRepository.save(abono);

        return ResponseEntity.ok(guardada);
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
        if (existente == null)
            return ResponseEntity.notFound().build();
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
