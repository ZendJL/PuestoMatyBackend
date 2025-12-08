package com.tienda.inventario.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaCliente;
import com.tienda.inventario.repositories.VentaClienteRepository;
import com.tienda.inventario.services.VentaClienteService;

@Service
@Transactional
public class VentaClienteServiceImpl implements VentaClienteService {

    private final VentaClienteRepository ventaClienteRepository;

    public VentaClienteServiceImpl(VentaClienteRepository ventaClienteRepository) {
        this.ventaClienteRepository = ventaClienteRepository;
    }

    @Override
    public VentaCliente guardar(VentaCliente vc) {
        return ventaClienteRepository.save(vc);
    }

    @Override
    public List<VentaCliente> ventasDeCuenta(CuentaCliente cuenta) {
        return ventaClienteRepository.findByCuentaCliente(cuenta);
    }

    @Override
    public List<VentaCliente> cuentasDeVenta(Venta venta) {
        return ventaClienteRepository.findByVenta(venta);
    }
}
