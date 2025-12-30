package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.repositories.AbonoRepository;
import com.tienda.inventario.services.AbonoService;
import com.tienda.inventario.services.CuentaClienteService;

@Service
@Transactional
public class AbonoServiceImpl implements AbonoService {

    private final AbonoRepository abonoRepository;
    private final CuentaClienteService cuentaClienteService;

    public AbonoServiceImpl(AbonoRepository abonoRepository, CuentaClienteService cuentaClienteService) {
        this.abonoRepository = abonoRepository;
        this.cuentaClienteService = null;
    }

    @Override
    public Abono guardar(Abono abono) {
            if (abono.getFecha() == null) {
        abono.setFecha(LocalDateTime.now());
    }

        return abonoRepository.save(abono);
    }

    @Override
    public Abono buscarPorId(Integer id) {
        return abonoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Abono> listarTodos() {
        return abonoRepository.findAll();
    }

    @Override
    public void eliminar(Integer id) {
        abonoRepository.deleteById(id);
    }

    @Override
    public List<Abono> abonosDeCuenta(CuentaCliente cuenta) {
        return abonoRepository.findByCuentaOrderByFechaDesc(cuenta);
    }

    @Override
    public List<Abono> abonosDeCuentaEntreFechas(CuentaCliente cuenta, LocalDateTime desde, LocalDateTime hasta) {
        return abonoRepository.findByCuentaAndFechaBetweenOrderByFechaDesc(cuenta, desde, hasta);
    }

    @Override
    public List<Abono> abonosEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return abonoRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
    }
    // ✅ En AbonoService / AbonoServiceImpl
@Transactional
public Abono abonarACuenta(Integer cuentaId, Float monto) {
    CuentaCliente cuenta = cuentaClienteService.buscarPorId(cuentaId);
    if (cuenta == null) {
        throw new IllegalArgumentException("Cuenta no encontrada: " + cuentaId);
    }
    
    Float saldoActual = cuenta.getSaldo() == null ? 0f : cuenta.getSaldo();
    Float nuevoSaldo = saldoActual - monto;
    
    Abono abono = new Abono();
    abono.setCuenta(cuenta);
    abono.setCantidad((float) monto);
    abono.setFecha(LocalDateTime.now());
    abono.setViejoSaldo(saldoActual);
    abono.setNuevoSaldo(nuevoSaldo);
    
    cuenta.setSaldo(nuevoSaldo);
    cuentaClienteService.guardar(cuenta);
    
    return guardar(abono);
}

public Map<String, Object> generarReciboAbono(Integer abonoId) {
    Abono abono = buscarPorId(abonoId);
    if (abono == null) {
        throw new IllegalArgumentException("Abono no encontrado");
    }
    
    Map<String, Object> recibo = new HashMap<>();
    recibo.put("abonoId", abono.getId());
    recibo.put("fecha", abono.getFecha());
    recibo.put("monto", abono.getCantidad());
    recibo.put("saldoAnterior", abono.getViejoSaldo());
    recibo.put("nuevoSaldo", abono.getNuevoSaldo());
    recibo.put("nombreCliente", abono.getCuenta().getNombre());
    
    return recibo;
}

}
