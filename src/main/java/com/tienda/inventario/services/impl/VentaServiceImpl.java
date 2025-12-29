package com.tienda.inventario.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

        if (venta.getCuenta() == null && venta.getCuentaId() != null) {
            CuentaCliente cuenta = cuentaClienteService.buscarPorId(venta.getCuentaId());
            venta.setCuenta(cuenta);
        }

        if (venta.getVentaProductos() != null) {
            for (VentaProducto vp : venta.getVentaProductos()) {

                Integer prodId = vp.getProducto().getId();
                Producto producto = productoRepository.findById(prodId)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

                vp.setVenta(venta);
                vp.setProducto(producto);

                if (vp.getPrecioUnitario() == null) {
                    vp.setPrecioUnitario(producto.getPrecio());
                }

                int cantidadVendida = vp.getCantidad() == null ? 0 : vp.getCantidad();

                Integer stockActual = producto.getCantidad() == null ? 0 : producto.getCantidad();
                int nuevaCantidad = stockActual - cantidadVendida;
                if (nuevaCantidad < 0) {
                    throw new IllegalArgumentException(
                        "Stock insuficiente para el producto " + producto.getDescripcion());
                }
                producto.setCantidad(nuevaCantidad);
                productoRepository.save(producto);

                List<CompraProducto> lotes = compraProductoRepository
                        .findByProductoIdOrderByFechaCompraAsc(prodId);

                int restante = cantidadVendida;
                float costoTotal = 0f;

                for (CompraProducto lote : lotes) {
                    if (restante <= 0) break;

                    int disponible = lote.getCantidadDisponible() == null
                            ? 0 : lote.getCantidadDisponible();
                    if (disponible <= 0) continue;

                    int aConsumir = Math.min(disponible, restante);
                    lote.setCantidadDisponible(disponible - aConsumir);
                    restante -= aConsumir;

                    float precioCompra = lote.getPrecioCompra() == null
                            ? 0f : lote.getPrecioCompra();
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

                if (restante > 0) {
                    throw new IllegalStateException(
                        "Stock inconsistente: no hay suficiente en compra_productos para el producto " + prodId);
                }

                vp.setCostoTotal(costoTotal);

                compraProductoRepository.saveAll(lotes);
            }
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

        List<VentaProductoLote> lotes = ventaProductoLoteRepository.findByVentaProducto_Venta(venta);
        List<Object[]> resultado = new ArrayList<>();

        for (VentaProductoLote vpl : lotes) {
            Object[] fila = new Object[7];
            fila[0] = vpl.getVentaProducto().getProducto().getId();
            fila[1] = vpl.getVentaProducto().getProducto().getDescripcion();
            fila[2] = vpl.getCompraProducto().getId();
            fila[3] = vpl.getCompraProducto().getFechaCompra();
            fila[4] = vpl.getCantidadConsumida();
            fila[5] = vpl.getCostoUnitario();
            fila[6] = vpl.getCostoTotal();
            resultado.add(fila);
        }

        return resultado;
    }

     @Override
    @Transactional(readOnly = true)
    public List<VentaProductoResumenDto> obtenerVentasPorProducto(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.atTime(LocalTime.MAX);
        return ventaProductoRepository.resumenPorProducto(ini, fin);
    }
}
