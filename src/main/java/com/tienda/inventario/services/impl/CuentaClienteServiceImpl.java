package com.tienda.inventario.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.repositories.CuentaClienteRepository;
import com.tienda.inventario.services.CuentaClienteService;

@Service
@Transactional
public class CuentaClienteServiceImpl implements CuentaClienteService {

    private final CuentaClienteRepository cuentaClienteRepository;

    public CuentaClienteServiceImpl(CuentaClienteRepository cuentaClienteRepository) {
        this.cuentaClienteRepository = cuentaClienteRepository;
    }

    @Override
    public CuentaCliente guardar(CuentaCliente cuenta) {
        return cuentaClienteRepository.save(cuenta);
    }

    @Override
    public CuentaCliente buscarPorId(Long id) {
        return cuentaClienteRepository.findById(id).orElse(null);
    }

    @Override
    public List<CuentaCliente> listarTodas() {
        return cuentaClienteRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        cuentaClienteRepository.deleteById(id);
    }

    @Override
    public List<CuentaCliente> buscarPorNombre(String nombre) {
        return cuentaClienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<CuentaCliente> cuentasConDeuda() {
        return cuentaClienteRepository.findBySaldoGreaterThan(0f);
    }

    @Override
    public List<CuentaCliente> cuentasSinDeudaONegro() {
        return cuentaClienteRepository.findBySaldoLessThanEqual(0f);
    }
}
