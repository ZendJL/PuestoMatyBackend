package com.tienda.inventario.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.tienda.inventario.entities.Abono;
import com.tienda.inventario.entities.CuentaCliente;

public interface AbonoService {

    Abono guardar(Abono abono);

    Abono buscarPorId(Integer id);

    List<Abono> listarTodos();

    void eliminar(Integer id);

    List<Abono> abonosDeCuenta(CuentaCliente cuenta);

    List<Abono> abonosDeCuentaEntreFechas(CuentaCliente cuenta, LocalDateTime desde, LocalDateTime hasta);

    List<Abono> abonosEntreFechas(LocalDateTime desde, LocalDateTime hasta);

public Abono abonarACuenta(Integer cuentaId, Float monto);

public Map<String, Object> generarReciboAbono(Integer abonoId);
}
