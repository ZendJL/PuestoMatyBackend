package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.CompraProducto;
import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.repositories.CompraProductoRepository;
import com.tienda.inventario.repositories.ProductoRepository;
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

    public VentaServiceImpl(VentaRepository ventaRepository,
                            VentaProductoRepository ventaProductoRepository,
                            ProductoRepository productoRepository,
                            CuentaClienteService cuentaClienteService,
                            CompraProductoRepository compraProductoRepository) {
        this.ventaRepository = ventaRepository;
        this.ventaProductoRepository = ventaProductoRepository;
        this.productoRepository = productoRepository;
        this.cuentaClienteService = cuentaClienteService;
        this.compraProductoRepository = compraProductoRepository;
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

        // Resolver cuenta si viene solo cuentaId
        if (venta.getCuenta() == null && venta.getCuentaId() != null) {
            CuentaCliente cuenta = cuentaClienteService.buscarPorId(venta.getCuentaId());
            venta.setCuenta(cuenta);
        }

        if (venta.getVentaProductos() != null) {
            for (VentaProducto vp : venta.getVentaProductos()) {

                Integer prodId = vp.getProducto().getId();
                Producto producto = productoRepository.findById(prodId)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

                // Enlazar relaciones
                vp.setVenta(venta);
                vp.setProducto(producto);

                // Si no viene precioUnitario, tomar el actual
                if (vp.getPrecioUnitario() == null) {
                    vp.setPrecioUnitario(producto.getPrecio());
                }

                int cantidadVendida = vp.getCantidad();

                // 1) Restar stock total del producto
                Integer stockActual = producto.getCantidad() == null ? 0 : producto.getCantidad();
                int nuevaCantidad = stockActual - cantidadVendida;
                if (nuevaCantidad < 0) {
                    throw new IllegalArgumentException(
                        "Stock insuficiente para el producto " + producto.getDescripcion());
                }
                producto.setCantidad(nuevaCantidad);
                productoRepository.save(producto);

                // 2) Consumir lotes de compra_productos por FIFO y calcular costoTotal
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
                    costoTotal += aConsumir * precioCompra;
                }

                compraProductoRepository.saveAll(lotes);

                // Si por algún bug quedara restante > 0, significa que no había stock en lotes;
                // aquí podrías lanzar excepción o ignorar. De momento lanzamos:
                if (restante > 0) {
                    throw new IllegalStateException(
                        "Stock inconsistente: no hay suficiente en compra_productos para el producto " + prodId);
                }

                vp.setCostoTotal(costoTotal);
            }
        }

        // 3) Actualizar saldo si es PRESTAMO
        if ("PRESTAMO".equalsIgnoreCase(venta.getStatus()) && venta.getCuenta() != null) {
            CuentaCliente cuenta = venta.getCuenta();
            Float saldoActual = cuenta.getSaldo() == null ? 0f : cuenta.getSaldo();
            cuenta.setSaldo(saldoActual + venta.getTotal());
            cuentaClienteService.guardar(cuenta);
        }

        return ventaRepository.save(venta);
    }
}
