package com.tienda.inventario.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.inventario.dto.MermaCostoRequestDto;
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

private final Map<Integer, List<CompraProducto>> lotesCache = new ConcurrentHashMap<>();

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

    // ✅ MÉTODOS EXISTENTES (sin cambios)
    @Override
    public Merma guardar(Merma merma) {
        if (merma.getFecha() == null) {
            merma.setFecha(LocalDateTime.now());
        }
        return mermaRepository.save(merma);
    }

    @Override
    public List<Merma> listar() {
         return mermaRepository.findAllConProductos();
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
    public List<Merma> mermasPorTipoYRango(String tipoMerma, LocalDateTime desde, LocalDateTime hasta) {
        return mermaRepository.findByTipoMermaAndFechaBetween(tipoMerma, desde, hasta);
    }

    @Override
    public List<Merma> mermasEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return mermaRepository.findByFechaBetween(desde, hasta);
    }

  @Override
@Transactional
public Merma crearMermaConProductos(Merma merma) {
    if (merma.getFecha() == null) {
        merma.setFecha(LocalDateTime.now());
    }

    float totalMerma = 0f;

    if (merma.getMermaProductos() != null && !merma.getMermaProductos().isEmpty()) {
        // ✅ BATCH 1: Extraer productoIds del frontend
        Set<Integer> productoIds = new HashSet<>();
        List<MermaProducto> detallesParaProcesar = new ArrayList<>();

        for (MermaProducto mp : merma.getMermaProductos()) {
            // Frontend envía: {productoId: 1, cantidad: 5}
            if (mp.getProducto() != null && mp.getProducto().getId() != null) {
                productoIds.add(mp.getProducto().getId());
            }
            // O directo si tiene productoId (futuro)
            detallesParaProcesar.add(mp);
        }

        if (productoIds.isEmpty()) {
            throw new IllegalArgumentException("No hay productos válidos");
        }

        // ✅ BATCH 2: Cargar TODOS productos (1 query)
        List<Producto> productosBatch = productoRepository.findAllById(productoIds);
        Map<Integer, Producto> productosMap = productosBatch.stream()
            .collect(Collectors.toMap(Producto::getId, p -> p));

        // ✅ BATCH 3: Cargar lotes para todos productos
        Map<Integer, List<CompraProducto>> lotesBatch = new HashMap<>();
        for (Integer prodId : productoIds) {
            lotesBatch.put(prodId, compraProductoRepository.findByProductoIdOrderByFechaCompraAsc(prodId));
        }

        // ✅ PROCESAR cada detalle
        for (MermaProducto mp : detallesParaProcesar) {
            Integer prodId = mp.getProducto() != null ? mp.getProducto().getId() : null;
            if (prodId == null) {
                throw new IllegalArgumentException("Producto requerido");
            }

            Producto producto = productosMap.get(prodId);
            if (producto == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + prodId);
            }

            // ✅ Enlazar relaciones
            mp.setMerma(merma);
            mp.setProducto(producto);

            // 1) Validar cantidad
            Integer cantidadMermaObj = mp.getCantidad();
            if (cantidadMermaObj == null || cantidadMermaObj <= 0) {
                throw new IllegalArgumentException("Cantidad > 0 requerida para " + producto.getDescripcion());
            }
            int cantidadMerma = cantidadMermaObj;

            // 2) Validar stock
            Integer stockActualObj = producto.getCantidad();
            int stockActual = stockActualObj == null ? 0 : stockActualObj;
            if (cantidadMerma > stockActual) {
                throw new IllegalArgumentException(String.format(
                    "Sin stock: %d/%d und %s", cantidadMerma, stockActual, producto.getDescripcion()
                ));
            }

            // 3) Restar stock
            producto.setCantidad(stockActual - cantidadMerma);

            // 4) Consumir lotes FIFO
            List<CompraProducto> lotes = lotesBatch.get(prodId);
            int restante = cantidadMerma;
            float costoTotalMerma = 0f;

            for (CompraProducto lote : lotes) {
                if (restante <= 0) break;

                Integer disponibleObj = lote.getCantidadDisponible();
                int disponible = (disponibleObj == null) ? 0 : disponibleObj;
                if (disponible <= 0) continue;

                int aConsumir = Math.min(disponible, restante);
                lote.setCantidadDisponible(disponible - aConsumir);
                restante -= aConsumir;

                Float precioCompraObj = lote.getPrecioCompra();
                float precioCompra = (precioCompraObj == null) ? 0f : precioCompraObj;
                costoTotalMerma += aConsumir * precioCompra;
            }

            if (restante > 0) {
                throw new IllegalStateException("Lotes insuficientes para producto " + prodId);
            }

            // 5) Asignar costo
            mp.setCostoTotal(costoTotalMerma);
            totalMerma += costoTotalMerma;
        }

        // ✅ BATCH SAVE: Productos + lotes
        if (!productosBatch.isEmpty()) {
            productoRepository.saveAll(productosBatch);
        }
        
        // Flatten all lotes para saveAll
        List<CompraProducto> allLotes = lotesBatch.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        if (!allLotes.isEmpty()) {
            compraProductoRepository.saveAll(allLotes);
        }
    }

    merma.setCostoTotal(totalMerma);
    return mermaRepository.save(merma);
}

@Override
public List<Map<String, Object>> reporteMermasCompleto(LocalDateTime desde, LocalDateTime hasta) {
    // ✅ 1 QUERY - carga Merma + MermaProducto + Producto
    System.out.println("🔥 USANDO: findByFechaBetweenConProductos");  // DEBUG
    
    List<Merma> mermas = mermaRepository.findByFechaBetweenConProductos(desde, hasta);
    System.out.println("🔥 CARGADAS: " + mermas.size() + " mermas");  // DEBUG
    
    
    return mermas.stream()
        .map(merma -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", merma.getId());
            row.put("fecha", merma.getFecha());
            row.put("tipoMerma", merma.getTipoMerma());
            row.put("motivoGeneral", merma.getMotivoGeneral());
            row.put("costoTotal", merma.getCostoTotal());
            
            // ✅ Ya cargados - 0 queries extras
            List<Map<String, Object>> detalles = merma.getMermaProductos().stream()
                .map(mp -> {
                    Map<String, Object> d = new HashMap<>();
                    d.put("productoId", mp.getProducto().getId());
                    d.put("productoNombre", mp.getProducto().getDescripcion());
                    d.put("cantidad", mp.getCantidad());
                    d.put("costo", mp.getCostoTotal());
                    return d;
                })
                .collect(Collectors.toList());
            
            row.put("detalles", detalles);
            return row;
        })
        .collect(Collectors.toList());
}


@Override
public List<Map<String, Object>> reporteMermasPorTipoCompleto(LocalDateTime desde, LocalDateTime hasta, String tipo) {
    
    System.out.println("🔥 USANDO: findByFechaBetweenConProductos");  // DEBUG
    
    List<Merma> mermas = mermaRepository.findByFechaBetweenConProductos(desde, hasta);
    System.out.println("🔥 CARGADAS: " + mermas.size() + " mermas");  // DEBUG
    
    return mermas.stream()
        .map(merma -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", merma.getId());
            row.put("fecha", merma.getFecha());
            row.put("tipoMerma", merma.getTipoMerma());
            row.put("motivoGeneral", merma.getMotivoGeneral());
            row.put("costoTotal", merma.getCostoTotal());
            
            row.put("detalles", merma.getMermaProductos().stream()
                .map(mp -> {
                    Map<String, Object> d = new HashMap<>();
                    d.put("productoId", mp.getProducto().getId());
                    d.put("productoNombre", mp.getProducto().getDescripcion());
                    d.put("cantidad", mp.getCantidad());
                    d.put("costo", mp.getCostoTotal());
                    return d;
                })
                .collect(Collectors.toList()));
            return row;
        }) 
        .collect(Collectors.toList());
}


// ✅ AGREGAR CAMPO caché al inicio de clase:
// ✅ REEMPLAZAR calcularCostoMermaProducto COMPLETO:
@Override
public float calcularCostoMermaProducto(Integer productoId, Integer cantidad) {
    if (cantidad == null || cantidad <= 0) {
        throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
    }

    // ✅ CACHÉ: 1 query máximo por producto
    List<CompraProducto> lotes = lotesCache.computeIfAbsent(productoId, id -> 
        compraProductoRepository.findByProductoIdOrderByFechaCompraAsc(id)
    );

    int restante = cantidad;
    float costoTotal = 0f;

    for (CompraProducto lote : lotes) {
        if (restante <= 0) break;

        Integer disponibleObj = lote.getCantidadDisponible();
        int disponible = (disponibleObj == null) ? 0 : disponibleObj;
        if (disponible <= 0) continue;

        int aConsumir = Math.min(disponible, restante);
        restante -= aConsumir;

        Float precioObj = lote.getPrecioCompra();
        float precio = (precioObj == null) ? 0f : precioObj;

        costoTotal += aConsumir * precio;
    }

    if (restante > 0) {
        throw new IllegalArgumentException("No hay suficiente inventario para calcular costo");
    }

    return costoTotal;
}
@Override
public List<Float> calcularCostosMermaBatch(List<MermaCostoRequestDto> requests) {
    if (requests.isEmpty()) return List.of();
    
    // ✅ 1 QUERY - todos productos únicos
    Set<Integer> productoIds = requests.stream()
        .map(req -> req.productoId)
        .collect(Collectors.toSet());
    
    Map<Integer, List<CompraProducto>> lotesBatch = new HashMap<>();
    for (Integer id : productoIds) {
        lotesBatch.put(id, compraProductoRepository.findByProductoIdOrderByFechaCompraAsc(id));
    }
    
    return requests.stream()
        .map(req -> {
            List<CompraProducto> lotes = lotesBatch.get(req.productoId);
            return calcularCostoConLotes(lotes, req.cantidad);
        })
        .collect(Collectors.toList());
}

// ✅ MÉTODO AUXILIAR:
private float calcularCostoConLotes(List<CompraProducto> lotes, Integer cantidad) {
    if (cantidad == null || cantidad <= 0) return 0f;
    
    int restante = cantidad;
    float costoTotal = 0f;
    
    for (CompraProducto lote : lotes) {
        if (restante <= 0) break;
        
        Integer disponibleObj = lote.getCantidadDisponible();
        int disponible = (disponibleObj == null) ? 0 : disponibleObj;
        if (disponible <= 0) continue;
        
        int aConsumir = Math.min(disponible, restante);
        restante -= aConsumir;
        
        Float precioObj = lote.getPrecioCompra();
        float precio = (precioObj == null) ? 0f : precioObj;
        
        costoTotal += aConsumir * precio;
    }
    
    return costoTotal;
}




    /*@Override
    public List<Merma> reporteMermas(LocalDateTime desde, LocalDateTime hasta, String tipo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reporteMermas'");
    }
*/
}
