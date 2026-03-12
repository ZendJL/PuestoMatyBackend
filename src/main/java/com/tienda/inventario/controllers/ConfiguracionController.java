package com.tienda.inventario.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Configuracion;
import com.tienda.inventario.repositories.ConfiguracionRepository;

@RestController
@RequestMapping("/api/configuracion")
@CrossOrigin(origins = "*")
public class ConfiguracionController {

    private final ConfiguracionRepository repo;

    public ConfiguracionController(ConfiguracionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/tasa-cambio")
    public ResponseEntity<Map<String, Object>> getTasaCambio() {
        Configuracion config = repo.findById("tasa_cambio_usd")
            .orElse(new Configuracion("tasa_cambio_usd", "17.0"));
        double tasa = Double.parseDouble(config.getValor());
        return ResponseEntity.ok(Map.of("tasa", tasa));
    }

    @PutMapping("/tasa-cambio")
    public ResponseEntity<Map<String, Object>> setTasaCambio(@RequestBody Map<String, Double> body) {
        Double tasa = body.get("tasa");
        if (tasa == null || tasa <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tasa inválida"));
        }
        repo.save(new Configuracion("tasa_cambio_usd", String.valueOf(tasa)));
        return ResponseEntity.ok(Map.of("tasa", tasa));
    }
}
