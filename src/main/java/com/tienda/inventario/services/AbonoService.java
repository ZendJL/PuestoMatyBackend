package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;

public interface AbonoService {

    Abono guardar(Abono abono);

    Abono buscarPorId(Long id);

    List<Abono> listarTodos();

    void eliminar(Long id);

    List<Abono> abonosDeCuenta(CuentaCliente cuenta);

    List<Abono> abonosDeCuentaEntreFechas(CuentaCliente cuenta, LocalDateTime desde, LocalDateTime hasta);

    List<Abono> abonosEntreFechas(LocalDateTime desde, LocalDateTime hasta);
}
