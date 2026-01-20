package com.tienda.inventario.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.inventario.entities.Producto;
import com.tienda.inventario.services.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarTodos() {
        return productoService.listarTodos();
    }

    @GetMapping("/activos")
    public List<Producto> listarActivos() {
        return productoService.findByActivoTrue();
    }

    // Agregar stock a un producto (registrar compra + lote)
    @PostMapping("/{id}/agregar-stock")
    public ResponseEntity<Producto> agregarStock(
            @PathVariable Integer id,
            @RequestParam("cantidad") Integer cantidadAgregar,
            @RequestParam("precioCompra") Float precioCompra) {

        if (cantidadAgregar == null || cantidadAgregar <= 0 || precioCompra == null || precioCompra < 0) {
            return ResponseEntity.badRequest().build();
        }

        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        // Usa la lógica de negocio centralizada en el servicio:
        Producto actualizado = productoService.registrarCompra(id, cantidadAgregar, precioCompra);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Integer id) {
        Producto p = productoService.buscarPorId(id);
        return p != null ? ResponseEntity.ok(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto p) {
        if (p.getActivo() == null) {
            p.setActivo(true);
        }
        return ResponseEntity.ok(productoService.guardar(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Integer id,
            @RequestBody Producto req) {
        Producto existente = productoService.buscarPorId(id);
        if (existente == null)
            return ResponseEntity.notFound().build();

        boolean cambiaCosto = req.getPrecioCompra() != null
                && !req.getPrecioCompra().equals(existente.getPrecioCompra());

        existente.setCodigo(req.getCodigo());
        existente.setDescripcion(req.getDescripcion());
        existente.setPrecio(req.getPrecio());
        existente.setProveedor(req.getProveedor());
        existente.setCantidad(req.getCantidad());
        existente.setActivo(req.getActivo());

        if (cambiaCosto) {
            // actualiza producto + último lote
            Producto actualizado = productoService
                    .actualizarCostoCompraYUltimoLote(id, req.getPrecioCompra());
            // reflejar demás cambios en el objeto
            actualizado.setCodigo(existente.getCodigo());
            actualizado.setDescripcion(existente.getDescripcion());
            actualizado.setPrecio(existente.getPrecio());
            actualizado.setProveedor(existente.getProveedor());
            actualizado.setCantidad(existente.getCantidad());
            actualizado.setActivo(existente.getActivo());
            return ResponseEntity.ok(productoService.guardar(actualizado));
        } else {
            existente.setPrecioCompra(req.getPrecioCompra());
            return ResponseEntity.ok(productoService.guardar(existente));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock")
    public List<Producto> productosConStock() {
        return productoService.productosConStock();
    }

    @GetMapping("/ventas")
    public List<Producto> productosCompradosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return productoService.productosCompradosEnRango(desde, hasta);
    }
     /**
     * ⭐ NUEVO ENDPOINT PARA IMPRIMIR CÓDIGO DE BARRAS
     * Ruta: /api/impresora/codigo-barras
     */
    @PostMapping("/impresora/codigo-barras")
    public ResponseEntity<?> imprimirCodigoBarras(@RequestBody Map<String, String> datos) {
        String codigo = datos.get("codigo");
        String descripcion = datos.getOrDefault("descripcion", "");

        if (codigo == null || codigo.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body("{\"error\":\"Código requerido\"}");
        }

        try {
            imprimirTicketCodigoBarras(codigo, descripcion);
            return ResponseEntity.ok()
                .body("{\"success\":\"Código de barras impreso correctamente\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Error al imprimir: " + e.getMessage() + "\"}");
        }
    }
    /**
     * ⭐ FUNCIÓN INTERNA para generar e imprimir ticket de código de barras
     */
    private void imprimirTicketCodigoBarras(String codigo, String descripcion) throws Exception {
        StringBuilder comandos = new StringBuilder();
        
        // ⭐ INICIALIZAR IMPRESORA (ESC @)
        comandos.append((char) 27).append((char) 64);
        
        // ⭐ CENTRAR TEXTO (ESC a 1)
        comandos.append((char) 27).append((char) 97).append((char) 1);
        
        // ⭐ TÍTULO - Negrita ON
        comandos.append((char) 27).append((char) 69).append((char) 1); // Negrita ON
        comandos.append("CÓDIGO DE BARRAS\n");
        comandos.append((char) 27).append((char) 69).append((char) 0); // Negrita OFF
        
        // ⭐ DESCRIPCIÓN DEL PRODUCTO
        if (!descripcion.trim().isEmpty()) {
            comandos.append(descripcion).append("\n");
        }
        
        // ⭐ CÓDIGO DE BARRAS CODE128 (GS k 73)
        comandos.append((char) 29).append((char) 107).append((char) 73); // GS k 73 (CODE128)
        comandos.append((char) codigo.length()); // Longitud del código
        comandos.append(codigo); // Datos del código
        
        // ⭐ NUEVA LÍNEA después del barcode
        comandos.append("\n\n");
        
        // ⭐ CÓDIGO EN TEXTO - Doble altura + Negrita
        comandos.append((char) 29).append((char) 33).append((char) 16); // Doble altura
        comandos.append((char) 27).append((char) 69).append((char) 1); // Negrita ON
        comandos.append(codigo).append("\n");
        comandos.append((char) 27).append((char) 69).append((char) 0); // Negrita OFF
        comandos.append((char) 29).append((char) 33).append((char) 0); // Normal
        
        // ⭐ SEPARADOR
        comandos.append("\n----------------------------------------\n");
        comandos.append("Tienda - ").append(LocalDateTime.now().toLocalDate()).append("\n");
        
        // ⭐ CORTAR PAPEL (GS V 1)
        comandos.append("\n\n");
        comandos.append((char) 29).append((char) 86).append((char) 1);
        
        // Convertir a bytes
        byte[] bytesComandos = comandos.toString().getBytes("UTF-8");
        
        // ⭐ ENVIAR A IMPRESORA POR DEFECTO
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        if (printService == null) {
            throw new Exception("No se encontró impresora por defecto");
        }
        
        DocPrintJob job = printService.createPrintJob();
        Doc doc = new SimpleDoc(bytesComandos, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        job.print(doc, null);
    }
}
