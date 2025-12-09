package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.CuentaCliente;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
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

    public VentaServiceImpl(VentaRepository ventaRepository,
                        VentaProductoRepository ventaProductoRepository,
                        ProductoRepository productoRepository,
                        CuentaClienteService cuentaClienteService) {
    this.ventaRepository = ventaRepository;
    this.ventaProductoRepository = ventaProductoRepository;
    this.productoRepository = productoRepository;
    this.cuentaClienteService = cuentaClienteService;
}

    @Override
    public Venta guardar(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
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

    // VentaServiceImpl
@Override
@Transactional
public Venta crearVentaConProductos(Venta venta) {

    // Resolver cuenta (ya lo tienes)
    if (venta.getCuenta() == null && venta.getCuentaId() != null) {
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(venta.getCuentaId());
        venta.setCuenta(cuenta);
    }

    if (venta.getVentaProductos() != null) {
        for (VentaProducto vp : venta.getVentaProductos()) {

            Long prodId = vp.getProducto().getId();
            Producto producto = productoRepository.findById(prodId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

            // Enlazar relaciones
            vp.setVenta(venta);
            vp.setProducto(producto);

            // Si no viene precioUnitario, tomar el actual
            if (vp.getPrecioUnitario() == null) {
                vp.setPrecioUnitario(producto.getPrecio());
            }

            // 1) Restar stock
            Long stockActual = producto.getCantidad();
            Long nuevaCantidad = stockActual - vp.getCantidad();
            if (nuevaCantidad < 0) {
                throw new IllegalArgumentException("Stock insuficiente para el producto " + producto.getDescripcion());
            }
            producto.setCantidad(nuevaCantidad);
            productoRepository.save(producto);
        }
    }

    // 2) Actualizar saldo si es PRESTAMO
    if ("PRESTAMO".equalsIgnoreCase(venta.getStatus()) && venta.getCuenta() != null) {
        CuentaCliente cuenta = venta.getCuenta();
        Float saldoActual = cuenta.getSaldo() == null ? 0f : cuenta.getSaldo();
        cuenta.setSaldo(saldoActual + venta.getTotal());
        // si tienes CuentaClienteRepository o service, persístelo
        cuentaClienteService.guardar(cuenta);
    }

    return ventaRepository.save(venta);
}


}
