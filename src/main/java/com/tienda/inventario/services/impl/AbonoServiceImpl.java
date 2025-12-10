package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.repositories.AbonoRepository;
import com.tienda.inventario.services.AbonoService;

@Service
@Transactional
public class AbonoServiceImpl implements AbonoService {

    private final AbonoRepository abonoRepository;

    public AbonoServiceImpl(AbonoRepository abonoRepository) {
        this.abonoRepository = abonoRepository;
    }

    @Override
    public Abono guardar(Abono abono) {
        abono.setFecha(LocalDateTime.now());
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
}
