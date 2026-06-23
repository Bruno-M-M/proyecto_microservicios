package com.Carrito.Carrito_compras.Service;

import com.Carrito.Carrito_compras.Client.ClienteFeingClient;
import com.Carrito.Carrito_compras.Client.InventarioFeingClient;
import com.Carrito.Carrito_compras.DTO.*;
import com.Carrito.Carrito_compras.Exception.ResourceNotFound;
import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Model.CarritoItem;
import com.Carrito.Carrito_compras.Repository.CarritoRepository;
import com.Carrito.Carrito_compras.Client.PagoFeingClient;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ClienteFeingClient clienteClient;

    @Autowired
    private InventarioFeingClient inventarioClient;


    private ClienteDTO obtenerCliente(Long clienteId){

        try {
            return clienteClient.getClientById(clienteId).getContent();
        }catch (FeignException.NotFound e){
            throw new RuntimeException("Cliente no encontrado con ID: " +clienteId);
        } catch (Exception e){
            throw new RuntimeException("Error al conectar con el servicio de clientes: " + e.getMessage());
        }
    }

    private boolean verificarStock(Long productoId, Integer cantidad) {
        try {
            Boolean resultado = inventarioClient.checkStock(productoId, cantidad);
            return Boolean.TRUE.equals(resultado);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar stock: " + e.getMessage());
        }
    }

    public CarritoDetalleDTO marcarComoPagado(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        if (carrito.getEstado() == Carrito.EstadoPedido.PAGADO) {
            return construirDetalle(carrito);
        }
        if (carrito.getEstado() == Carrito.EstadoPedido.PENDIENTE) {
            throw new RuntimeException("El pedido debe ser CONFIRMADO antes de pagarse.");
        }
        if (carrito.getEstado() == Carrito.EstadoPedido.CANCELADO) {
            throw new RuntimeException("No se puede pagar un pedido CANCELADO.");
        }


        for (CarritoItem item : carrito.getItems()) {
            descontarStock(item.getProductoId(), item.getCantidad());
        }

        carrito.setEstado(Carrito.EstadoPedido.PAGADO);
        carritoRepository.save(carrito);
        return construirDetalle(carrito);
    }

    private ProductoDTO obtenerProducto(Long productoId) {
        try {
            return inventarioClient.getProductoById(productoId).getContent();
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Producto no encontrado con ID: " + productoId);
        }
    }




    private void descontarStock(Long productoId, Integer cantidad) {
        try {
            inventarioClient.reduceStock(productoId, cantidad);
        } catch (Exception e) {
            throw new RuntimeException("Error al descontar stock: " + e.getMessage());
        }
    }

    private CarritoDetalleDTO construirDetalle(Carrito carrito) {
        ClienteDTO cliente = obtenerCliente(carrito.getClienteId());
        String run = cliente.getRun() + "-" + cliente.getDv();

        List<CarritoItemDetalleDTO> itemsDetalle = new ArrayList<>();
        double total = 0.0;

        for (CarritoItem item : carrito.getItems()) {
            ProductoDTO producto = obtenerProducto(item.getProductoId());
            double subtotal = (double) producto.getPrecio() * item.getCantidad();
            total += subtotal;
            itemsDetalle.add(new CarritoItemDetalleDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getCategoria(),
                    item.getCantidad(),
                    subtotal
            ));
        }

        return new CarritoDetalleDTO(
                carrito.getId(),
                carrito.getEstado().name(),
                carrito.getFechaCreacion(),
                carrito.getFechaConfirmacion(),
                total,
                cliente.getId(),
                run,
                cliente.getNombre(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                itemsDetalle
        );
    }

    public List<CarritoDetalleDTO> getTodo() {
        return carritoRepository.findAll().stream()
                .map(this::construirDetalle).collect(Collectors.toList());
    }

    public List<CarritoDetalleDTO> getByEstado(String estado) {
        Carrito.EstadoPedido estadoEnum;
        try {
            estadoEnum = Carrito.EstadoPedido.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Estado inválido: '" + estado + "'. Use: PENDIENTE, CONFIRMADO, PAGADO, CANCELADO");
        }
        return carritoRepository.findByEstado(estadoEnum)
                .stream()
                .map(this::construirDetalle)
                .collect(Collectors.toList());
    }
    public CarritoDetalleDTO agregar(CarritoRequestDTO request) {


        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("El pedido debe contener al menos 1 producto.");
        }

        // Sin productos duplicados en el mismo pedido
        long distintos = request.getItems().stream()
                .map(CarritoItemRequestDTO::getProductoId)
                .distinct().count();
        if (distintos < request.getItems().size()) {
            throw new RuntimeException(
                    "No puedes agregar el mismo producto más de una vez. " +
                            "Ajusta la cantidad del ítem duplicado.");
        }


        obtenerCliente(request.getClienteId());

        for (CarritoItemRequestDTO itemReq : request.getItems()) {
            obtenerProducto(itemReq.getProductoId());
            if (!verificarStock(itemReq.getProductoId(), itemReq.getCantidad())) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto ID: " + itemReq.getProductoId());
            }
        }

        Carrito carrito = new Carrito();
        carrito.setClienteId(request.getClienteId());
        carrito.setEstado(Carrito.EstadoPedido.PENDIENTE);
        carrito.setFechaCreacion(LocalDateTime.now());

        for (CarritoItemRequestDTO itemReq : request.getItems()) {
            ProductoDTO producto = obtenerProducto(itemReq.getProductoId());
            CarritoItem item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProductoId(itemReq.getProductoId());
            item.setNombreProducto(producto.getNombre());
            item.setCantidad(itemReq.getCantidad());
            carrito.getItems().add(item);
        }

        carritoRepository.save(carrito);
        return construirDetalle(carrito);
    }

    @Autowired
    private PagoFeingClient pagoClient;

    public CarritoDetalleDTO confirmar(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (carrito.getEstado() != Carrito.EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar pedidos en estado PENDIENTE.");
        }


        carrito.setEstado(Carrito.EstadoPedido.CONFIRMADO);
        carrito.setFechaConfirmacion(LocalDateTime.now());
        carritoRepository.save(carrito);

        try {
            pagoClient.notificarConfirmacion(carrito.getClienteId(), carrito.getId());
        } catch (Exception e) {
            System.err.println(" No se pudo notificar a Pago: " + e.getMessage());
        }

        return construirDetalle(carrito);
    }

    public CarritoDetalleDTO cancelar(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        if (carrito.getEstado() == Carrito.EstadoPedido.PAGADO) {
            throw new RuntimeException("No se puede cancelar un pedido ya PAGADO.");
        }
        if (carrito.getEstado() == Carrito.EstadoPedido.CANCELADO) {
            throw new RuntimeException("El pedido ya está CANCELADO.");
        }
        carrito.setEstado(Carrito.EstadoPedido.CANCELADO);
        carritoRepository.save(carrito);
        return construirDetalle(carrito);
    }




    public void eliminar(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        if (carrito.getEstado() == Carrito.EstadoPedido.PAGADO) {
            throw new RuntimeException("No se puede eliminar un pedido ya PAGADO.");
        }
        carritoRepository.deleteById(id);
    }

    public List<CarritoDetalleDTO> getByCliente(Long clienteId) {
        obtenerCliente(clienteId);
        return carritoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::construirDetalle)
                .collect(Collectors.toList());
    }

    public long countPedidosPorMes(Long clienteId, int mes, int anio) {
        return carritoRepository.countConfirmadosByClienteYMes(
                clienteId, mes, anio,
                Carrito.EstadoPedido.PAGADO,
                Carrito.EstadoPedido.CONFIRMADO
        );
    }

    public long countPedidosPorAnio(Long clienteId, int anio) {
        return carritoRepository.countConfirmadosByClienteYAnio(clienteId, anio);
    }
}