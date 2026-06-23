package com.Pago.Metodo_de_pago.Service;

import com.Pago.Metodo_de_pago.Client.CarritoFeingClient;
import com.Pago.Metodo_de_pago.Client.ClienteFeingClient;
import com.Pago.Metodo_de_pago.DTO.BoletaItemDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.DTO.CarritoDetalleDTO;
import com.Pago.Metodo_de_pago.Model.MetodoPago;
import com.Pago.Metodo_de_pago.Repostory.BoletaRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BoletaService {
    private static final double IVA_RATE = 0.19;

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private CarritoFeingClient carritoClient;

    @Autowired
    private ClienteFeingClient clienteClient;

    private BoletaResponseDTO toResponse(MetodoPago boleta) {
        List<Long> ids = Arrays.stream(boleta.getPedidosIds().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<CarritoDetalleDTO> pedidos;
        try {
            pedidos = new java.util.ArrayList<>(carritoClient.getPedidosDelCliente(boleta.getClienteId()).getContent());
        } catch (Exception e) {
            pedidos = List.of();
        }

        List<BoletaItemDTO> items = pedidos.stream()
                .filter(p -> ids.contains(p.getPedidoId()))
                .flatMap(p -> p.getItems() != null ? p.getItems().stream()
                                                     .map(item -> new BoletaItemDTO(
                                                             p.getPedidoId(),
                                                             item.getProductoNombre(),
                                                             item.getProductoCategoria(),
                                                             item.getCantidad(),
                                                             item.getProductoPrecio(),
                                                             item.getSubtotal()
                                                     )) : java.util.stream.Stream.empty())
                .collect(Collectors.toList());

        return new BoletaResponseDTO(
                boleta.getId(),
                boleta.getFechaEmision(),
                boleta.getEstado().name(),
                boleta.getClienteId(),
                boleta.getClienteNombre(),
                boleta.getClienteRun(),
                boleta.getClienteCorreo(),
                boleta.getClienteDireccion(),
                boleta.getClienteTelefono(),
                boleta.getTipoPago().name(),
                items,
                boleta.getTotalNeto(),
                boleta.getIva(),
                boleta.getTotalConIva()
        );
    }

    public BoletaResponseDTO emitirBoleta(BoletaRequestDTO request) {

        // 1. Validar que el cliente existe
        try {
            Boolean existe = clienteClient.existsById(request.getClienteId());
            if (!Boolean.TRUE.equals(existe)) {
                throw new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId());
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de clientes: " + e.getMessage());
        }

        // 2. Obtener pedidos del cliente desde el Carrito
        List<CarritoDetalleDTO> todosPedidos;
        try {
            todosPedidos = new java.util.ArrayList<>(carritoClient.getPedidosDelCliente(request.getClienteId()).getContent());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de Carrito: " + e.getMessage());
        }

        // 3. Filtrar solo los CONFIRMADOS
        List<CarritoDetalleDTO> pedidosConfirmados = todosPedidos.stream()
                .filter(p -> "CONFIRMADO".equalsIgnoreCase(p.getEstadoPedido()))
                .collect(Collectors.toList());

        // 4. Si se especificaron IDs concretos, filtrar por ellos
        if (request.getPedidosIds() != null && !request.getPedidosIds().isEmpty()) {
            pedidosConfirmados = pedidosConfirmados.stream()
                    .filter(p -> request.getPedidosIds().contains(p.getPedidoId()))
                    .collect(Collectors.toList());
        }

        if (pedidosConfirmados.isEmpty()) {
            throw new RuntimeException(
                    "El cliente no tiene pedidos CONFIRMADOS disponibles para facturar. " +
                            "Confirme los pedidos en el Carrito antes de emitir una boleta.");
        }

        // 5. Construir items y calcular totales
        List<BoletaItemDTO> items = pedidosConfirmados.stream()
                .flatMap(p -> p.getItems() != null ? p.getItems().stream()
                                                     .map(item -> new BoletaItemDTO(
                                                             p.getPedidoId(),
                                                             item.getProductoNombre(),
                                                             item.getProductoCategoria(),
                                                             item.getCantidad(),
                                                             item.getProductoPrecio(),
                                                             item.getSubtotal()
                                                     )) : java.util.stream.Stream.empty())
                .collect(Collectors.toList());

        double totalNeto   = items.stream().mapToDouble(BoletaItemDTO::getSubtotal).sum();
        double iva         = Math.round(totalNeto * IVA_RATE * 100.0) / 100.0;
        double totalConIva = Math.round((totalNeto + iva) * 100.0) / 100.0;

        // 6. Validar y parsear método de pago
        MetodoPago.TipoPago tipoPago;
        try {
            tipoPago = MetodoPago.TipoPago.valueOf(request.getMetodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Método de pago inválido: '" + request.getMetodoPago() +
                            "'. Use: EFECTIVO, DEBITO, CREDITO, TRANSFERENCIA");
        }

        // 7. Guardar la boleta
        CarritoDetalleDTO primero = pedidosConfirmados.get(0);
        String pedidosIdsStr = pedidosConfirmados.stream()
                .map(p -> p.getPedidoId().toString())
                .collect(Collectors.joining(","));

        MetodoPago boleta = new MetodoPago();
        boleta.setClienteId(primero.getClienteId());
        boleta.setClienteNombre(primero.getClienteNombre());
        boleta.setClienteRun(primero.getClienteRun());
        boleta.setClienteCorreo(primero.getClienteCorreo());
        boleta.setClienteDireccion(primero.getClienteDireccion());
        boleta.setClienteTelefono(primero.getClienteTelefono());
        boleta.setTipoPago(tipoPago);
        boleta.setTotalNeto(totalNeto);
        boleta.setIva(iva);
        boleta.setTotalConIva(totalConIva);
        boleta.setFechaEmision(LocalDateTime.now());
        boleta.setEstado(MetodoPago.EstadoBoleta.EMITIDA);
        boleta.setPedidosIds(pedidosIdsStr);
        boletaRepository.save(boleta);

        // 8. Marcar cada pedido como PAGADO en el Carrito
        for (CarritoDetalleDTO pedido : pedidosConfirmados) {
            try {
                carritoClient.marcarComoPagado(pedido.getPedidoId());
                log.info("✅ Pedido {} marcado como PAGADO en Carrito", pedido.getPedidoId());
            } catch (Exception e) {
                log.warn("⚠️ No se pudo marcar como PAGADO el pedido {}: {}", pedido.getPedidoId(), e.getMessage());
            }
        }

        // 9. Retornar la boleta generada
        return new BoletaResponseDTO(
                boleta.getId(),
                boleta.getFechaEmision(),
                boleta.getEstado().name(),
                primero.getClienteId(),
                primero.getClienteNombre(),
                primero.getClienteRun(),
                primero.getClienteCorreo(),
                primero.getClienteDireccion(),
                primero.getClienteTelefono(),
                tipoPago.name(),
                items,
                totalNeto,
                iva,
                totalConIva
        );
    }

    public List<BoletaResponseDTO> getBoletas() {
        return boletaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BoletaResponseDTO getBoletaById(Long id) {
        MetodoPago boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada con ID: " + id));
        return toResponse(boleta);
    }

    public List<BoletaResponseDTO> getBoletasByCliente(Long clienteId) {
        return boletaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BoletaResponseDTO anular(Long id) {
        MetodoPago boleta = boletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada con ID: " + id));

        if (boleta.getEstado() == MetodoPago.EstadoBoleta.ANULADA) {
            throw new RuntimeException("La boleta ya está anulada.");
        }

        boleta.setEstado(MetodoPago.EstadoBoleta.ANULADA);
        boletaRepository.save(boleta);
        return toResponse(boleta);
    }
}