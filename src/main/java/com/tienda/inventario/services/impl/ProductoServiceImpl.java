package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.CompraProducto;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.repositories.CompraProductoRepository;
import com.tienda.inventario.repositories.ProductoRepository;
import com.tienda.inventario.services.ProductoService;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CompraProductoRepository compraProductoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CompraProductoRepository compraProductoRepository) {
        this.productoRepository = productoRepository;
        this.compraProductoRepository = compraProductoRepository;
    }

    @Override
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Producto guardar(Producto producto) {
        // Alta/edición básica de producto (no movimiento de stock)
        if (producto.getUltimaCompra() == null) {
            producto.setUltimaCompra(LocalDateTime.now());
        }
        return productoRepository.save(producto);
    }

    @Override
    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> productosConStock() {
        return productoRepository.findByCantidadGreaterThan(0);
    }

    @Override
    public List<Producto> productosCompradosEnRango(LocalDateTime desde, LocalDateTime hasta) {
        return productoRepository.findByUltimaCompraBetween(desde, hasta);
    }

    @Override
    public List<Producto> findByActivoTrue() {
        return productoRepository.findByActivoTrue();
    }

    /**
     * Registrar una compra de producto (agregar stock):
     *  - Crea un lote en compra_productos
     *  - Actualiza stock total y datos de última compra en productos
     */
    @Override 
    public Producto registrarCompra(Integer productoId, int cantidad, float precioCompra) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        // 1) Crear lote de compra
        CompraProducto lote = new CompraProducto();
        lote.setProducto(producto);
        lote.setCantidadComprada(cantidad);
        lote.setCantidadDisponible(cantidad);
        lote.setPrecioCompra(precioCompra);
        lote.setFechaCompra(LocalDateTime.now());

        compraProductoRepository.save(lote);

        // 2) Actualizar producto (stock + última compra)
        int stockActual = producto.getCantidad() == null ? 0 : producto.getCantidad();
        producto.setCantidad(stockActual + cantidad);
        producto.setPrecioCompra(precioCompra);
        producto.setUltimaCompra(LocalDateTime.now());

        return productoRepository.save(producto);
    }

    @Override
public Producto actualizarCostoCompraYUltimoLote(Integer productoId, float nuevoPrecioCompra) {
    Producto producto = productoRepository.findById(productoId)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

    // actualizar campo precio_compra en producto
    producto.setPrecioCompra(nuevoPrecioCompra);
    productoRepository.save(producto);

    // actualizar SOLO el último lote de compra_productos
    compraProductoRepository.findTopByProductoIdOrderByFechaCompraDesc(productoId)
        .ifPresent(lote -> {
            lote.setPrecioCompra(nuevoPrecioCompra);
            compraProductoRepository.save(lote);
        });

    return producto;
}


}
