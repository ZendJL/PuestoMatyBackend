package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.entities.Venta;
import com.tienda.inventario.entities.VentaProducto;
import com.tienda.inventario.repositories.ProductoRepository;
import com.tienda.inventario.repositories.VentaProductoRepository;
import com.tienda.inventario.repositories.VentaRepository;
import com.tienda.inventario.services.VentaService;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final VentaProductoRepository ventaProductoRepository;
    private final ProductoRepository productoRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            VentaProductoRepository ventaProductoRepository,
                            ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.ventaProductoRepository = ventaProductoRepository;
        this.productoRepository = productoRepository;
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

    @Override
    public Venta crearVentaConProductos(Venta venta, List<Long> productosIds) {
        // 1. Guardar la venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // 2. Crear y guardar los registros en venta_productos
        if (productosIds != null && !productosIds.isEmpty()) {
            List<VentaProducto> detalles = productosIds.stream()
                .map(prodId -> {
                    Producto p = productoRepository.findById(prodId)
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));
                    return new VentaProducto(ventaGuardada, p);
                })
                .collect(Collectors.toList());

            ventaProductoRepository.saveAll(detalles);
        }

        return ventaGuardada;
    }
}
