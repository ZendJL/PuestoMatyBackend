package com.tienda.inventario.services;

import java.util.List;

import com.tienda.inventario.dto.CuentaClienteDetallesDto;
import com.tienda.inventario.dto.CuentaClienteResumenDto;
import com.tienda.inventario.entities.CuentaCliente;

public interface CuentaClienteService {

    CuentaCliente guardar(CuentaCliente cuenta);

    CuentaCliente buscarPorId(Integer id);

    List<CuentaCliente> listarTodas();

    void eliminar(Integer id);

    List<CuentaCliente> buscarPorNombre(String nombre);

    List<CuentaCliente> cuentasConDeuda();

    List<CuentaCliente> cuentasSinDeudaONegro();
    
    // ✅ MANTENER SOLO 2 métodos nuevos
    List<CuentaClienteResumenDto> resumenCompleto();
    CuentaClienteDetallesDto getDetallesById(Long id);

}
