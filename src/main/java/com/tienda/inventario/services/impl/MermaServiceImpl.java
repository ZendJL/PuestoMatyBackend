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

    @Override
    public Merma guardar(Merma merma) {
        if (merma.getFecha() == null) {
            merma.setFecha(LocalDateTime.now());
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
        return mermaRepository.findByTipoMermaAndFechaBetween(tipoMerma, desde, hasta);
    }

    @Override
    public List<Merma> mermasEntreFechas(LocalDateTime desde,
            LocalDateTime hasta) {
        return mermaRepository.findByFechaBetween(desde, hasta);
    }

    // ===================== LÓGICA COMPLETA MERMA + FIFO =====================

    @Override
    @Transactional
    public Merma crearMermaConProductos(Merma merma) {

        if (merma.getFecha() == null) {
            merma.setFecha(LocalDateTime.now());
        }

        float totalMerma = 0f;

        if (merma.getMermaProductos() != null) {

            for (MermaProducto mp : merma.getMermaProductos()) {

                Integer prodId = mp.getProducto().getId();

                // 1) Cargar producto y enlazar relaciones
                Producto producto = productoRepository.findById(prodId)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + prodId));

                mp.setMerma(merma);
                mp.setProducto(producto);

                // 2) Validar cantidad de merma
                if (mp.getCantidad() == null || mp.getCantidad() <= 0) {
                    throw new IllegalArgumentException(
                            "La cantidad de merma debe ser mayor que cero para el producto "
                                    + producto.getDescripcion());
                }

                int cantidadMerma = mp.getCantidad();

                int stockActual = producto.getCantidad() == null ? 0 : producto.getCantidad();
                if (cantidadMerma > stockActual) {
                    throw new IllegalArgumentException(
                            "No se puede registrar merma de " + cantidadMerma +
                                    " unidades del producto " + producto.getDescripcion() +
                                    " porque solo hay " + stockActual + " en inventario");
                }

                // 3) Restar stock del producto
                int nuevoStock = stockActual - cantidadMerma;
                producto.setCantidad(nuevoStock);
                productoRepository.save(producto);

                // 4) Consumir lotes FIFO en compra_productos
                List<CompraProducto> lotes = compraProductoRepository
                        .findByProductoIdOrderByFechaCompraAsc(prodId);

                int restante = cantidadMerma;
                float costoTotalMerma = 0f;

                for (CompraProducto lote : lotes) {
                    if (restante <= 0)
                        break;

                    Integer disponibleObj = lote.getCantidadDisponible();
                    int disponible = (disponibleObj == null) ? 0 : disponibleObj;
                    if (disponible <= 0)
                        continue;

                    int aConsumir = Math.min(disponible, restante);
                    lote.setCantidadDisponible(disponible - aConsumir);
                    restante -= aConsumir;

                    Float precioCompraObj = lote.getPrecioCompra();
                    float precioCompra = (precioCompraObj == null) ? 0f : precioCompraObj;

                    costoTotalMerma += aConsumir * precioCompra;
                }

                compraProductoRepository.saveAll(lotes);

                if (restante > 0) {
                    throw new IllegalStateException(
                            "Stock inconsistente: no hay suficiente en compra_productos para merma del producto "
                                    + prodId);
                }

                // 5) Guardar costo total en el detalle
                mp.setCostoTotal(costoTotalMerma);
                totalMerma += costoTotalMerma;
            }
        }

        // 6) Guardar total en cabecera (si existe la columna costo_total en merma)
        merma.setCostoTotal(totalMerma);

        return mermaRepository.save(merma);
    }

    @Override
    public float calcularCostoMermaProducto(Integer productoId, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        }

        List<CompraProducto> lotes = compraProductoRepository
                .findByProductoIdOrderByFechaCompraAsc(productoId);

        int restante = cantidad;
        float costoTotal = 0f;

        for (CompraProducto lote : lotes) {
            if (restante <= 0)
                break;

            Integer disponibleObj = lote.getCantidadDisponible();
            int disponible = (disponibleObj == null) ? 0 : disponibleObj;
            if (disponible <= 0)
                continue;

            int aConsumir = Math.min(disponible, restante);
            restante -= aConsumir;

            Float precioObj = lote.getPrecioCompra();
            float precio = (precioObj == null) ? 0f : precioObj;

            costoTotal += aConsumir * precio;
        }

        if (restante > 0) {
            throw new IllegalArgumentException(
                    "No hay suficiente inventario por lotes para calcular costo de merma (faltan "
                            + restante + " unidades)");
        }

        return costoTotal;
    }

}
