package proyecto.cliente.dto;

import org.springframework.stereotype.Component;
import proyecto.cliente.model.Cliente;

@Component
public class ClienteMapper {

    public ClienteResponseDTO toResponse(Cliente cliente) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(cliente.getId());
        dto.setRun(cliente.getRun());
        dto.setDv(cliente.getDv());
        dto.setNombre(cliente.getNombre());
        dto.setCorreo(cliente.getCorreo());
        dto.setDireccion(cliente.getDireccion());
        dto.setTelefono(cliente.getTelefono());
        return dto;
    }

    public Cliente toEntity(ClienteRequestDTO dto){
        Cliente cliente = new Cliente();
        cliente.setRun(dto.getRun());
        cliente.setDv(dto.getDv());
        cliente.setNombre(dto.getNombre());
        cliente.setCorreo(dto.getCorreo());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setContrasenia(dto.getContrasenia());
        return cliente;
    }
}
