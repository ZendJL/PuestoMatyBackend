package com.tienda.inventario.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.dto.VentaProductoResumenDto;
import com.tienda.inventario.entities.CompraProducto;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.entities.VentaProductoLote;
import com.tienda.inventario.repositories.CompraProductoRepository;
import com.tienda.inventario.repositories.ProductoRepository;
import com.tienda.inventario.repositories.VentaProductoLoteRepository;
import com.tienda.inventario.repositories.VentaProductoRepository;
import com.tienda.inventario.repositories.VentaRepository;
import com.tienda.inventario.services.CuentaClienteService;
import com.tienda.inventario.services.VentaService;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final VentaProductoRepository ventaProductoRepository;
    private final ProductoRepository productoRepository;
    private final CuentaClienteService cuentaClienteService;
    private final CompraProductoRepository compraProductoRepository;
    private final VentaProductoLoteRepository ventaProductoLoteRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            VentaProductoRepository ventaProductoRepository,
                            ProductoRepository productoRepository,
                            CuentaClienteService cuentaClienteService,
                            CompraProductoRepository compraProductoRepository,
                            VentaProductoLoteRepository ventaProductoLoteRepository) {
        this.ventaRepository = ventaRepository;
        this.ventaProductoRepository = ventaProductoRepository;
        this.productoRepository = productoRepository;
        this.cuentaClienteService = cuentaClienteService;
        this.compraProductoRepository = compraProductoRepository;
        this.ventaProductoLoteRepository = ventaProductoLoteRepository;
    }

    @Override
    public Venta guardar(Venta venta) {
        venta.setFecha(LocalDateTime.now());
        return ventaRepository.save(venta);
    }

    @Override
    public Venta buscarPorId(Integer id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Override
    public void eliminar(Integer id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public List<Venta> ventasEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.findByFechaBetweenOrderByFechaAsc(desde, hasta);
    }

    @Override
    public List<Venta> ventasPorStatus(String status) {
        return ventaRepository.findByStatus(status);
    }

    @Override
    public List<VentaProducto> productosDeVenta(Venta venta) {
        return ventaProductoRepository.findByVenta(venta);
    }

    @Override
    @Transactional
    public Venta crearVentaConProductos(Venta venta) {
        venta.setFecha(LocalDateTime.now());

        if (venta.getCuentaId() != null) {
            CuentaCliente cuenta = cuentaClienteService.buscarPorId(venta.getCuentaId());
            venta.setCuenta(cuenta);
        }

        if (venta.getVentaProductos() != null && !venta.getVentaProductos().isEmpty()) {
            List<Integer> productoIds = venta.getVentaProductos().stream()
                .map(vp -> vp.getProducto().getId())
                .distinct()
                .collect(Collectors.toList());

            List<Producto> productos = productoRepository.findAllById(productoIds);
            Map<Integer, Producto> productosMap = productos.stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

            List<CompraProducto> todosLotes = new ArrayList<>();
            for (Integer prodId : productoIds) {
                todosLotes.addAll(compraProductoRepository.findByProductoIdOrderByFechaCompraAsc(prodId));
            }

            Map<Integer, List<CompraProducto>> lotesPorProducto = todosLotes.stream()
                .collect(Collectors.groupingBy(cp -> cp.getProducto().getId()));

            for (VentaProducto vp : venta.getVentaProductos()) {
                Integer prodId = vp.getProducto().getId();
                Producto producto = productosMap.get(prodId);

                if (producto == null) {
                    throw new IllegalArgumentException("Producto no encontrado: " + prodId);
                }

                vp.setVenta(venta);
                vp.setProducto(producto);

                if (vp.getPrecioUnitario() == null) {
                    vp.setPrecioUnitario(producto.getPrecio());
                }

                int cantidadVendida = vp.getCantidad() == null ? 1 : vp.getCantidad();
                int stockActual = producto.getCantidad() == null ? 0 : producto.getCantidad();

                // Stock puede quedar negativo: el producto llegó pero no se metió al sistema todavía
                int nuevaCantidad = stockActual - cantidadVendida;
                producto.setCantidad(nuevaCantidad);

                if (nuevaCantidad < 0) {
                    System.out.println("⚠️ Stock negativo permitido: " + producto.getDescripcion()
                        + " | antes=" + stockActual + ", vendido=" + cantidadVendida
                        + ", nuevo=" + nuevaCantidad);
                }

                // Consumo de lotes FIFO
                List<CompraProducto> lotesProducto = lotesPorProducto.getOrDefault(prodId, new ArrayList<>());
                float costoTotal = 0f;
                int cantidadRestante = cantidadVendida;

                for (CompraProducto lote : lotesProducto) {
                    if (cantidadRestante <= 0) break;
                    Integer disponible = lote.getCantidadDisponible();
                    if (disponible == null || disponible <= 0) continue;
                    int aConsumir = Math.min(disponible, cantidadRestante);
                    lote.setCantidadDisponible(disponible - aConsumir);
                    cantidadRestante -= aConsumir;
                    float precioCompra = lote.getPrecioCompra() == null ? 0f : lote.getPrecioCompra();
                    float costoParcial = aConsumir * precioCompra;
                    costoTotal += costoParcial;
                    VentaProductoLote vpl = new VentaProductoLote();
                    vpl.setVentaProducto(vp);
                    vpl.setCompraProducto(lote);
                    vpl.setCantidadConsumida(aConsumir);
                    vpl.setCostoUnitario(precioCompra);
                    vpl.setCostoTotal(costoParcial);
                    vpl.setFechaConsumo(venta.getFecha());
                    vp.getLotesConsumidos().add(vpl);
                }

                vp.setCostoTotal(costoTotal);
            }

            productoRepository.saveAll(productos);
            compraProductoRepository.saveAll(todosLotes);
        }

        if ("PRESTAMO".equalsIgnoreCase(venta.getStatus()) && venta.getCuenta() != null) {
            CuentaCliente cuenta = venta.getCuenta();
            Float saldoActual = cuenta.getSaldo() == null ? 0f : cuenta.getSaldo();
            cuenta.setSaldo(saldoActual + venta.getTotal());
            cuentaClienteService.guardar(cuenta);
        }

        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> costosPorLotesDeVenta(Integer ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
            .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada: " + ventaId));
        return ventaProductoLoteRepository.findByVentaProducto_Venta(venta).stream()
            .map(vpl -> new Object[] {
                vpl.getVentaProducto().getProducto().getId(),
                vpl.getVentaProducto().getProducto().getDescripcion(),
                vpl.getCompraProducto().getId(),
                vpl.getCompraProducto().getFechaCompra(),
                vpl.getCantidadConsumida(),
                vpl.getCostoUnitario(),
                vpl.getCostoTotal()
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaProductoResumenDto> obtenerVentasPorProducto(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);
        return ventaProductoRepository.resumenPorProducto(ini, fin);
    }

    @Override
    public Map<String, Object> getReporteVentas(LocalDateTime desde, LocalDateTime hasta) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(desde, hasta);
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("totalVentas", ventas.size());
        reporte.put("totalFacturado", ventas.stream().mapToDouble(Venta::getTotal).sum());
        reporte.put("ventasCompletadas", ventas.stream()
            .filter(v -> "COMPLETADA".equals(v.getStatus()))
            .count());
        reporte.put("ticketPromedio", ventas.stream()
            .mapToDouble(Venta::getTotal)
            .average()
            .orElse(0.0));
        return reporte;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Map<String, Object>> ventasReporteGenerales(LocalDateTime desde, LocalDateTime hasta) {
        List<Object[]> raw = ventaRepository.ventasReporteOptimizado(desde, hasta);
        return raw.stream().map(row -> {
            Map<String, Object> ventaMap = new HashMap<>();
            ventaMap.put("id", ((Number) row[0]).intValue());
            ventaMap.put("fecha", row[1]);
            ventaMap.put("total", ((Number) row[2]).doubleValue());
            ventaMap.put("status", (String) row[3]);
            ventaMap.put("pagoCliente", row[4]);
            ventaMap.put("cuentaId", row[5] != null ? ((Number) row[5]).intValue() : null);
            String cuentaNombre = row[6] != null ? (String) row[6] : null;
            if (cuentaNombre != null) {
                ventaMap.put("cuenta", Map.of("nombre", cuentaNombre));
            } else {
                ventaMap.put("cuenta", null);
            }
            ventaMap.put("productosCount", ((Number) row[7]).intValue());
            return ventaMap;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> productosDeVenta(Integer ventaId) {
        List<Object[]> raw = ventaRepository.productosDeVentaOptimizado(ventaId);
        return raw.stream().map(row -> {
            Map<String, Object> producto = new HashMap<>();
            producto.put("id", ((Number) row[0]).intValue());
            producto.put("producto", Map.of(
                "id", ((Number) row[1]).intValue(),
                "codigo", row[2],
                "descripcion", row[3]
            ));
            producto.put("cantidad", ((Number) row[4]).intValue());
            producto.put("precioUnitario", ((Number) row[5]).doubleValue());
            Double costoTotal = row[6] != null ? ((Number) row[6]).doubleValue() : 0.0;
            producto.put("costoTotal", costoTotal);
            return producto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Map<String, Object>> costosPorLotesDeVentaOptimizado(Integer ventaId) {
        List<Object[]> raw = ventaProductoLoteRepository.findCostosPorLotesDeVentaOptimizado(ventaId);
        return raw.stream().map(row -> {
            Map<String, Object> lote = new HashMap<>();
            lote.put("productoId", ((Number) row[0]).intValue());
            lote.put("productoDescripcion", row[2]);
            lote.put("loteId", ((Number) row[3]).intValue());
            lote.put("fechaCompra", row[4]);
            lote.put("cantidad", ((Number) row[5]).intValue());
            lote.put("costoUnitario", ((Number) row[6]).doubleValue());
            lote.put("costoTotal", ((Number) row[7]).doubleValue());
            return lote;
        }).collect(Collectors.toList());
    }
}
