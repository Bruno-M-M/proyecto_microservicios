package com.Pago.Metodo_de_pago.Service;

import com.Pago.Metodo_de_pago.Client.CarritoFeingClient;
import com.Pago.Metodo_de_pago.DTO.BoletaItemDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.DTO.CarritoDetalleDTO;
import com.Pago.Metodo_de_pago.Model.MetodoPago;
import com.Pago.Metodo_de_pago.Repostory.BoletaRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoletaService {
    private static final double IVA_RATE = 0.19;

    @Autowired
    private BoletaRepository boletaRepository;

    @Autowired
    private CarritoFeingClient carritoClient;

    private BoletaResponseDTO toResponse(MetodoPago boleta) {
        List<Long> ids = Arrays.stream(boleta.getPedidosIds().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<CarritoDetalleDTO> pedidos;
        try {
            pedidos = carritoClient.getPedidosDelCliente(boleta.getClienteId());
        } catch (Exception e) {
            pedidos = List.of();
        }

        List<BoletaItemDTO> items = pedidos.stream()
                .filter(p -> ids.contains(p.getPedidoId()))
                .map(p -> new BoletaItemDTO(
                        p.getPedidoId(),
                        p.getProductoNombre(),
                        p.getProductoCategoria(),
                        p.getCantidad(),
                        p.getProductoPrecio(),
                        p.getSubtotal()
                ))
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

        List<CarritoDetalleDTO> todosPedidos;
        try {
            todosPedidos = carritoClient.getPedidosDelCliente(request.getClienteId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Cliente no encontrado con ID: " + request.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de Carrito: " + e.getMessage());
        }

        List<CarritoDetalleDTO> pedidosConfirmados = todosPedidos.stream()
                .filter(p -> "PAGADO".equalsIgnoreCase(p.getEstadoPedido()))
                .collect(Collectors.toList());

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

        List<BoletaItemDTO> items = pedidosConfirmados.stream()
                .map(p -> new BoletaItemDTO(
                        p.getPedidoId(),
                        p.getProductoNombre(),
                        p.getProductoCategoria(),
                        p.getCantidad(),
                        p.getProductoPrecio(),
                        p.getSubtotal()
                ))
                .collect(Collectors.toList());

        double totalNeto   = items.stream().mapToDouble(BoletaItemDTO::getSubtotal).sum();
        double iva         = Math.round(totalNeto * IVA_RATE * 100.0) / 100.0;
        double totalConIva = Math.round((totalNeto + iva) * 100.0) / 100.0;

        CarritoDetalleDTO primero = pedidosConfirmados.get(0);

        MetodoPago.TipoPago tipoPago;
        try {
            tipoPago = MetodoPago.TipoPago.valueOf(request.getMetodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Método de pago inválido: '" + request.getMetodoPago() +
                            "'. Use: EFECTIVO, DEBITO, CREDITO, TRANSFERENCIA");
        }

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

        for (CarritoDetalleDTO pedido : pedidosConfirmados) {
            try {
                carritoClient.marcarComoPagado(pedido.getPedidoId());
            } catch (Exception e) {
                System.err.println("Advertencia: no se pudo marcar como PAGADO el pedido "
                        + pedido.getPedidoId() + ": " + e.getMessage());
            }
        }

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
