package com.tienda.inventario.services;

import java.util.Date;
import java.util.List;

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;

public interface AbonoService {

    Abono guardar(Abono abono);

    Abono buscarPorId(Long id);

    List<Abono> listarTodos();

    void eliminar(Long id);

    List<Abono> abonosDeCuenta(CuentaCliente cuenta);

    List<Abono> abonosDeCuentaEntreFechas(CuentaCliente cuenta, Date desde, Date hasta);

    List<Abono> abonosEntreFechas(Date desde, Date hasta);
}
