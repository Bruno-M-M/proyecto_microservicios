package com.Carrito.Carrito_compras.Service;

import com.Carrito.Carrito_compras.Client.ClienteFeingClient;
import com.Carrito.Carrito_compras.Client.InventarioFeingClient;
import com.Carrito.Carrito_compras.DTO.CarritoDetalleDTO;
import com.Carrito.Carrito_compras.DTO.CarritoRequestDTO;
import com.Carrito.Carrito_compras.DTO.ClienteDTO;
import com.Carrito.Carrito_compras.DTO.ProductoDTO;
import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Repository.CarritoRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
            return clienteClient.getClientById(clienteId);
        }catch (FeignException.NotFound e){
            throw new RuntimeException("Cliente no encontrado con ID: " +clienteId);
        } catch (Exception e){
            throw new RuntimeException("Error al conectar con el servicio de clientes: " + e.getMessage());
        }
    }

    private ProductoDTO obtenerProducto(Long productoId) {
        try {
            return inventarioClient.getProductoById(productoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Producto no encontrado con ID: " + productoId);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de inventario: " + e.getMessage());
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

    private void descontarStock(Long productoId, Integer cantidad) {
        try {
            inventarioClient.reduceStock(productoId, cantidad);
        } catch (Exception e) {
            throw new RuntimeException("Error al descontar stock: " + e.getMessage());
        }
    }

    private CarritoDetalleDTO construirDetalle(Carrito carrito) {
        ClienteDTO  cliente  = obtenerCliente(carrito.getClienteId());
        ProductoDTO producto = obtenerProducto(carrito.getProductoId());

        double subtotal = (double) producto.getPrecio() * carrito.getCantidad();
        String run      = cliente.getRun() + "-" + cliente.getDv();

        return new CarritoDetalleDTO(
                carrito.getId(),
                carrito.getCantidad(),
                carrito.getEstado().name(),
                subtotal,
                cliente.getId(),
                run,
                cliente.getNombre(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria()
        );
    }

    public List<CarritoDetalleDTO> getTodo() {
        return carritoRepository.findAll()
                .stream()
                .map(this::construirDetalle)
                .collect(Collectors.toList());
    }

    public List<CarritoDetalleDTO> getByEstado(String estado) {
        Carrito.EstadoPedido estadoEnum = Carrito.EstadoPedido.valueOf(estado.toUpperCase());
        return carritoRepository.findByEstado(estadoEnum)
                .stream()
                .map(this::construirDetalle)
                .collect(Collectors.toList());
    }
    public CarritoDetalleDTO agregar(CarritoRequestDTO request) {obtenerCliente(request.getClienteId());

        ProductoDTO producto = obtenerProducto(request.getProductoId());

        if (!verificarStock(request.getProductoId(), request.getCantidad())) {
            throw new RuntimeException("Stock insuficiente para el producto ID: " + request.getProductoId());
        }


        Carrito carrito = new Carrito();
        carrito.setClienteId(request.getClienteId());
        carrito.setProductoId(request.getProductoId());
        carrito.setCantidad(request.getCantidad());
        carrito.setNombre_producto(producto.getNombre());
        carrito.setEstado(Carrito.EstadoPedido.PENDIENTE);
        carritoRepository.save(carrito);

        return construirDetalle(carrito);
    }

    public CarritoDetalleDTO confirmar(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        if (carrito.getEstado() != Carrito.EstadoPedido.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar pedidos en estado PENDIENTE");
        }


        if (!verificarStock(carrito.getProductoId(), carrito.getCantidad())) {
            throw new RuntimeException("Stock insuficiente al confirmar el pedido");
        }
        descontarStock(carrito.getProductoId(), carrito.getCantidad());

        carrito.setFechaConfirmacion(LocalDateTime.now());
        carrito.setEstado(Carrito.EstadoPedido.CONFIRMADO);
        carritoRepository.save(carrito);

        return construirDetalle(carrito);
    }

    public CarritoDetalleDTO cancelar(Long id) {
        Carrito carrito = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        if (carrito.getEstado() == Carrito.EstadoPedido.CONFIRMADO) {
            throw new RuntimeException("No se puede cancelar un pedido ya confirmado");
        }

        carrito.setEstado(Carrito.EstadoPedido.CANCELADO);
        carritoRepository.save(carrito);

        return construirDetalle(carrito);
    }

    public void eliminar(Long id) {
        if (!carritoRepository.existsById(id)) {
            throw new RuntimeException("Pedido no encontrado con ID: " + id);
        }
        carritoRepository.deleteById(id);
    }

    public List<CarritoDetalleDTO> getByCliente(Long clienteId) {
        obtenerCliente(clienteId); // validar que existe
        return carritoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::construirDetalle)
                .collect(Collectors.toList());
    }

    public long countPedidosPorMes(Long clienteId, int mes, int anio) {
        return carritoRepository.findConfirmadosByClienteYMes(clienteId, mes, anio).size();
    }

    public long countPedidosPorAnio(Long clienteId, int anio) {
        return carritoRepository.countConfirmadosByClienteYAnio(clienteId, anio);
    }


}
