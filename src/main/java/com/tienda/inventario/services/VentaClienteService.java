package com.tienda.inventario.services;

import java.util.List;

import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaCliente;

public interface VentaClienteService {

    VentaCliente guardar(VentaCliente vc);

    List<VentaCliente> ventasDeCuenta(CuentaCliente cuenta);

    List<VentaCliente> cuentasDeVenta(Venta venta);
}
