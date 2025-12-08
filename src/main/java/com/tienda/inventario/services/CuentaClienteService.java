package com.tienda.inventario.services;

import java.util.List;

import com.tienda.inventario.entities.CuentaCliente;

public interface CuentaClienteService {

    CuentaCliente guardar(CuentaCliente cuenta);

    CuentaCliente buscarPorId(Long id);

    List<CuentaCliente> listarTodas();

    void eliminar(Long id);

    List<CuentaCliente> buscarPorNombre(String nombre);

    List<CuentaCliente> cuentasConDeuda();

    List<CuentaCliente> cuentasSinDeudaONegro();
}
