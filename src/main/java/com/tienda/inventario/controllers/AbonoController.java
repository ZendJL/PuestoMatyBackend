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

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.services.AbonoService;
import com.tienda.inventario.services.CuentaClienteService;

@RestController
@RequestMapping("/api/abonos")
public class AbonoController {

    private final AbonoService abonoService;
    private final CuentaClienteService cuentaClienteService;

    public AbonoController(AbonoService abonoService,
                           CuentaClienteService cuentaClienteService) {
        this.abonoService = abonoService;
        this.cuentaClienteService = cuentaClienteService;
    }

    @GetMapping
    public List<Abono> listarTodos() {
        return abonoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Abono> obtenerPorId(@PathVariable Integer id) {
        Abono a = abonoService.buscarPorId(id);
        return a != null ? ResponseEntity.ok(a) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Abono> crear(@RequestBody Abono abono) {
        return ResponseEntity.ok(abonoService.guardar(abono));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        abonoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cuenta/{cuentaId}")
    public ResponseEntity<List<Abono>> abonosDeCuenta(@PathVariable Integer cuentaId) {
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(cuentaId);
        if (cuenta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(abonoService.abonosDeCuenta(cuenta));
    }

    @GetMapping("/cuenta/{cuentaId}/rango")
    public ResponseEntity<List<Abono>> abonosDeCuentaEntreFechas(
            @PathVariable Integer cuentaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(cuentaId);
        if (cuenta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(abonoService.abonosDeCuentaEntreFechas(cuenta, desde, hasta));
    }

    @GetMapping("/rango")
    public List<Abono> abonosEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return abonoService.abonosEntreFechas(desde, hasta);
    }
    
}
