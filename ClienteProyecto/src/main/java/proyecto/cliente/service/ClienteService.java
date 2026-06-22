package proyecto.cliente.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.cliente.dto.ClienteMapper;
import proyecto.cliente.dto.ClienteRequestDTO;
import proyecto.cliente.dto.ClienteResponseDTO;
import proyecto.cliente.dto.LoginRequestDTO;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public List<ClienteResponseDTO> getClientes(){
        return clienteRepository.findAll()
                .stream().map(mapper::toResponse)
                .toList();
    }

    public ClienteResponseDTO getClienteById(Long id){
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Cliente no encontrado: " + id));
        return mapper.toResponse(cliente);
    }

    public ClienteResponseDTO getByRun(Integer run){
        Cliente cliente = clienteRepository.findByRun(run)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con RUN: " + run));
        return mapper.toResponse(cliente);
    }

    public ClienteResponseDTO registrar(ClienteRequestDTO dto){
        if(clienteRepository.existsByCorreo(dto.getCorreo())){
            throw new RuntimeException("Ya existe un cliente con este email.");
        }
        Cliente cliente = mapper.toEntity(dto);
        cliente.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));
        return mapper.toResponse(clienteRepository.save(cliente));
    }

    public ClienteResponseDTO login(LoginRequestDTO dto){
        Cliente cliente = clienteRepository.findByCorreo(dto.getCorreo());
        if (cliente == null)
            throw new RuntimeException("Correo no registrado");
        if (!passwordEncoder.matches(dto.getContrasenia(), cliente.getContrasenia())){
            throw new RuntimeException("Contraseña incorrecta");
        }
        return mapper.toResponse(cliente);
    }

    public ClienteResponseDTO updateCliente(Long id, ClienteRequestDTO dto){
        Cliente antiguo = clienteRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Cliente no encontrado: " + id));;
        antiguo.setRun(dto.getRun());
        antiguo.setDv(dto.getDv());
        antiguo.setNombre(dto.getNombre());
        antiguo.setCorreo(dto.getCorreo());
        antiguo.setDireccion(dto.getDireccion());
        antiguo.setTelefono(dto.getTelefono());
        antiguo.setContrasenia(passwordEncoder.encode(dto.getContrasenia()));
        return mapper.toResponse(clienteRepository.save(antiguo));
    }

    public void deleteCliente(Long id){
        getClienteById(id);
        clienteRepository.deleteById(id);
    }

    public boolean existsById(Long id){
        return clienteRepository.existsById(id);
    }

}
