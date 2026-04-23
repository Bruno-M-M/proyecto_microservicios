package proyecto.cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getClientes(){
        return clienteRepository.findAll();
    }

    public Cliente getClienteById(Long id){
        return clienteRepository.findById(id).orElseThrow(()-> new RuntimeException("Cliente no encontrado: " + id));
    }

    public Cliente getByRun(Integer run){
        return clienteRepository.findByRun(run);
    }

    public Cliente registrar(Cliente cliente){
        if(clienteRepository.existsByCorreo(cliente.getCorreo())){
            throw new RuntimeException("Ya existe un cliente con este email.");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente login(String correo, String password){
        Cliente cliente = clienteRepository.findByCorreo(correo);

        if (!cliente.getContraseña().equals(password)){
            throw new RuntimeException("Contraseña incorrecta");
        }
        return cliente;
    }

    public Cliente updateCliente(Long id, Cliente update){
        Cliente antiguo = clienteRepository.findById(id).orElseThrow(()-> new RuntimeException("Cliente no encontrado: " + id));;
        antiguo.setRun(update.getRun());
        antiguo.setDv(update.getDv());
        antiguo.setNombre(update.getNombre());
        antiguo.setCorreo(update.getCorreo());
        antiguo.setDireccion(update.getDireccion());
        antiguo.setTelefono(update.getTelefono());
        antiguo.setContraseña(update.getContraseña());
        return clienteRepository.save(antiguo);
    }

    public void deleteCliente(Long id){
        getClienteById(id);
        clienteRepository.deleteById(id);
    }

    public boolean existsById(Long id){
        return clienteRepository.existsById(id);
    }

}
