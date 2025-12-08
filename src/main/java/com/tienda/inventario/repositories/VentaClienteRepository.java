package com.tienda.inventario.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.inventario.entities.VentaCliente;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.CuentaCliente;

public interface VentaClienteRepository extends JpaRepository<VentaCliente, Long> {

    List<VentaCliente> findByVenta(Venta venta);

    List<VentaCliente> findByCuentaCliente(CuentaCliente cuentaCliente);
}
