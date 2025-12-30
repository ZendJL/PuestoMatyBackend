package com.tienda.inventario.services.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.dto.AbonoDto;
import com.tienda.inventario.dto.CuentaClienteDetallesDto;
import com.tienda.inventario.dto.CuentaClienteResumenDto;
import com.tienda.inventario.dto.VentaClienteDto;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.repositories.AbonoRepository;
import com.tienda.inventario.repositories.CuentaClienteRepository;
import com.tienda.inventario.services.CuentaClienteService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CuentaClienteServiceImpl implements CuentaClienteService {

    private final CuentaClienteRepository cuentaClienteRepository;
    private final AbonoRepository abonoRepository;

    public CuentaClienteServiceImpl(CuentaClienteRepository cuentaClienteRepository, AbonoRepository abonoRepository) {
        this.cuentaClienteRepository = cuentaClienteRepository;
        this.abonoRepository = abonoRepository;
    }

    // ✅ MÉTODOS ORIGINALES (mantener intactos)
    @Override
    public CuentaCliente guardar(CuentaCliente cuenta) {
        return cuentaClienteRepository.save(cuenta);
    }

    @Override
    public CuentaCliente buscarPorId(Integer id) {
        return cuentaClienteRepository.findById(id).orElse(null);
    }

    @Override
    public List<CuentaCliente> listarTodas() {
        return cuentaClienteRepository.findAll();
    }

    @Override
    public void eliminar(Integer id) {
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

    // ✅ RESUMEN COMPLETO (FUNCIONA)
    @Override
    public List<CuentaClienteResumenDto> resumenCompleto() {
        List<Object[]> results = cuentaClienteRepository.resumenCompletoNative();
        return results.stream()
            .map(this::mapToResumenDto)
            .collect(Collectors.toList());
    }

    private CuentaClienteResumenDto mapToResumenDto(Object[] row) {
        CuentaClienteResumenDto dto = new CuentaClienteResumenDto(
            ((Number) row[0]).intValue(),      // 0: id
            (String) row[1],                   // 1: nombre
            (String) row[2],                   // 2: descripcion
            ((Number) row[3]).floatValue(),    // 3: saldo
            ((Number) row[4]).doubleValue(),   // 4: totalFacturado
            ((Number) row[5]).intValue(),      // 5: totalVentas
            ((Number) row[6]).doubleValue(),   // 6: totalPagado
            ((Number) row[7]).doubleValue()    // 7: saldoCalculado
        );
        
        // ✅ FIX: String → Timestamp (MySQL devuelve String)
        if (row.length > 8 && row[8] != null) {
            try {
                String fechaStr = (String) row[8];
                Timestamp fecha = Timestamp.valueOf(fechaStr);
                dto.setUltimaActividad(fecha);
            } catch (Exception e) {
                System.out.println("⚠️ Fecha inválida: " + row[8]);
                dto.setUltimaActividad(null);
            }
        }
        
        return dto;
    }

    // 🔥 CORREGIDO COMPLETO - ABONOS MÁS RECIENTES PRIMERO
    @Override
@Transactional(readOnly = true)
public CuentaClienteDetallesDto getDetallesById(Long id) {
    Optional<CuentaCliente> cuentaOpt = cuentaClienteRepository.findById(id.intValue());
    if (cuentaOpt.isEmpty()) {
        throw new EntityNotFoundException("Cuenta no encontrada ID=" + id);
    }
    CuentaCliente cuenta = cuentaOpt.get();

    // ✅ ABONOS MÁS RECIENTES PRIMERO
    List<Object[]> abonosData = cuentaClienteRepository.ultimosAbonosByCuenta(id);
    List<Object[]> ventasData = cuentaClienteRepository.ultimasVentasByCuenta(id);

    // ✅ MAPPINGS CORRECTOS
    List<AbonoDto> ultimosAbonos = abonosData.stream()
        .map(this::mapToAbonoDto)
        .collect(Collectors.toList());

    List<VentaClienteDto> ultimasVentas = ventasData.stream()
        .map(this::mapToVentaDto)
        .collect(Collectors.toList());

    // ✅ TOTALES
    Double totalAbonos = ultimosAbonos.stream()
        .mapToDouble(AbonoDto::getCantidad)
        .sum();
    
    Double totalFacturado = ultimasVentas.stream()
        .mapToDouble(VentaClienteDto::getTotalVenta)
        .sum();
    
    Double totalPagado = totalAbonos;
    Double deudaPendiente = Math.max(0.0, (cuenta.getSaldo() != null ? cuenta.getSaldo() : 0f) - totalPagado);

    // ✅ CONSTRUCTOR CORRECTO (tu DTO original)
    return new CuentaClienteDetallesDto(
        cuenta.getId().longValue(),
        cuenta.getNombre(),
        cuenta.getDescripcion(),
        cuenta.getSaldo(),
        totalAbonos,
        ultimasVentas.size(),
        totalFacturado,
        totalPagado,
        deudaPendiente,
        ultimosAbonos,     // ✅ List<AbonoDto>
        ultimasVentas      // ✅ List<VentaClienteDto>
    );
}

    // ✅ MAPPINGS CORREGIDOS - FECHA COMO TIMESTAMP
    private AbonoDto mapToAbonoDto(Object[] row) {
        return new AbonoDto(
            ((Number) row[0]).longValue(),
            ((Number) row[1]).doubleValue(),
            ((Number) row[2]).doubleValue(),
            ((Number) row[3]).doubleValue(),
            row[4] != null ? row[4].toString() : ""  // Temporal - ya no se usa
        );
    }

    private VentaClienteDto mapToVentaDto(Object[] row) {
        return new VentaClienteDto(
            ((Number) row[0]).longValue(),
            ((Number) row[1]).longValue(),
            ((Number) row[2]).doubleValue(),
            row[3] != null ? row[3].toString() : "",
            row[4] != null ? (String) row[4] : "PENDIENTE",
            ((Number) row[5]).doubleValue()
        );
    }

}
