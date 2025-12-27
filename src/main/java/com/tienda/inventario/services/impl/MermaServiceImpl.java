package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.entities.CompraProducto;
import com.tienda.inventario.entities.Merma;
import com.tienda.inventario.entities.MermaProducto;
import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.repositories.CompraProductoRepository;
import com.tienda.inventario.repositories.MermaProductoRepository;
import com.tienda.inventario.repositories.MermaRepository;
import com.tienda.inventario.repositories.ProductoRepository;
import com.tienda.inventario.services.MermaService;

@Service
@Transactional
public class MermaServiceImpl implements MermaService {

    private final MermaRepository mermaRepository;
    private final MermaProductoRepository mermaProductoRepository;
    private final ProductoRepository productoRepository;
    private final CompraProductoRepository compraProductoRepository;

    public MermaServiceImpl(MermaRepository mermaRepository,
                            MermaProductoRepository mermaProductoRepository,
                            ProductoRepository productoRepository,
                            CompraProductoRepository compraProductoRepository) {
        this.mermaRepository = mermaRepository;
        this.mermaProductoRepository = mermaProductoRepository;
        this.productoRepository = productoRepository;
        this.compraProductoRepository = compraProductoRepository;
    }

    // ===== Métodos básicos =====

    @Override
    public Merma guardar(Merma merma) {
        if (merma.getFecha() == null) {
            merma.setFecha(LocalDateTime.now());
        }
        if (merma.getFechaSalida() == null) {
            merma.setFechaSalida(LocalDateTime.now());
        }
        return mermaRepository.save(merma);
    }

    @Override
    public List<Merma> listar() {
        return mermaRepository.findAll();
    }

    @Override
    public Merma buscarPorId(Integer id) {
        return mermaRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Integer id) {
        mermaRepository.deleteById(id);
    }

    @Override
    public List<MermaProducto> mermasDeProducto(Producto producto) {
        return mermaProductoRepository.findByProducto(producto);
    }

    @Override
    public List<Merma> mermasPorTipoYRango(String tipoMerma,
                                           LocalDateTime desde,
                                           LocalDateTime hasta) {
        // si tus queries siguen usando "fecha", deja esto así;
        // si cambiaste el repo a fechaSalida, ajusta el nombre del método
        return mermaRepository.findByTipoMermaAndFechaSalidaBetween(tipoMerma, desde, hasta);
    }

    @Override
    public List<Merma> mermasEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return mermaRepository.findByFechaSalidaBetween(desde, hasta);
    }

    // ===== Crear merma con detalle y consumo FIFO =====

    /**
     * Crea una merma con sus productos, restando stock en Producto y en la tabla
     * compra_productos por FIFO. Se espera que merma.mermaProductos venga con
     * producto (id) y cantidad para cada línea.
     */
    @Override
    @Transactional
    public Merma crearMermaConProductos(Merma merma) {
        if (merma.getFecha() == null) {
            merma.setFecha(LocalDateTime.now());
        }
        if (merma.getFechaSalida() == null) {
            merma.setFechaSalida(LocalDateTime.now());
        }

        if (merma.getMermaProductos() != null) {
            for (MermaProducto mp : merma.getMermaProductos()) {

                Integer prodId = mp.getProducto().getId();
                Producto producto = productoRepository.findById(prodId)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

                // enlazar relaciones
                mp.setMerma(merma);
                mp.setProducto(producto);

                int cantidadMerma = mp.getCantidad();

                // 1) Restar stock total de productos
                int stockActual = producto.getCantidad() == null ? 0 : producto.getCantidad();
                int nuevoStock = stockActual - cantidadMerma;
                if (nuevoStock < 0) {
                    throw new IllegalArgumentException(
                        "Stock insuficiente para registrar merma de " + cantidadMerma +
                        " unidades del producto " + producto.getDescripcion());
                }
                producto.setCantidad(nuevoStock);
                productoRepository.save(producto);

                // 2) Consumir lotes FIFO en compra_productos
                List<CompraProducto> lotes = compraProductoRepository
                    .findByProductoIdOrderByFechaCompraAsc(prodId);

                int restante = cantidadMerma;
                float costoTotalMerma = 0f;

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
                    costoTotalMerma += aConsumir * precioCompra;
                }

                compraProductoRepository.saveAll(lotes);

                if (restante > 0) {
                    throw new IllegalStateException(
                        "Stock inconsistente: no hay suficiente en compra_productos para merma del producto " + prodId);
                }

                mp.setCostoTotal(costoTotalMerma);
            }
        }

        return mermaRepository.save(merma);
    }
}
