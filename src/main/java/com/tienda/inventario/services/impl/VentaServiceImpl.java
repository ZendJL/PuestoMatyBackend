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
public Venta crearVentaConProductos(Venta venta) {

    if (venta.getCuenta() == null && venta.getCuentaId() != null) {
        CuentaCliente cuenta = cuentaClienteService.buscarPorId(venta.getCuentaId());
        venta.setCuenta(cuenta);
    }

    // Asociar la venta a cada detalle y resolver el Producto real
    if (venta.getVentaProductos() != null) {
        for (VentaProducto vp : venta.getVentaProductos()) {
            Long prodId = vp.getProducto().getId();
            Producto producto = productoRepository.findById(prodId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));
            vp.setVenta(venta);
            vp.setProducto(producto);

            // Si no mandas precioUnitario desde el front, puedes rellenarlo aquí:
            if (vp.getPrecioUnitario() == null) {
                vp.setPrecioUnitario(producto.getPrecio());
            }
        }
    }

    // gracias a cascade=ALL, al guardar la venta se guardan también los VentaProducto
    return ventaRepository.save(venta);
}

}
