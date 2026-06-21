package com.Pago.Metodo_de_pago.Config;

import com.Pago.Metodo_de_pago.Model.MetodoPago;
import com.Pago.Metodo_de_pago.Repostory.BoletaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoletaConfig implements CommandLineRunner {

    private final BoletaRepository boletaRepository;

    @Override
    public void run(String... args) {
        if (boletaRepository.count() > 0) {
            log.info(">>> Boletas ya cargadas. Se omite inicialización.");
            return;
        }

        log.info(">>> Cargando boletas iniciales...");


        double b1Neto  = 18970.0;
        double b1Iva   = Math.round(b1Neto * 0.19 * 100.0) / 100.0;
        double b1Total = Math.round((b1Neto + b1Iva) * 100.0) / 100.0;

        MetodoPago boleta1 = new MetodoPago();
        boleta1.setClienteId(1L);
        boleta1.setClienteNombre("Bruno Mateluna");
        boleta1.setClienteRun("20688880-7");
        boleta1.setClienteCorreo("br.mateluna@duocuc.cl");
        boleta1.setClienteDireccion("Calle quinta vergara 666");
        boleta1.setClienteTelefono(949000000);
        boleta1.setTipoPago(MetodoPago.TipoPago.DEBITO);
        boleta1.setTotalNeto(b1Neto);
        boleta1.setIva(b1Iva);
        boleta1.setTotalConIva(b1Total);
        boleta1.setFechaEmision(LocalDateTime.now().minusMonths(2).minusDays(3));
        boleta1.setEstado(MetodoPago.EstadoBoleta.EMITIDA);
        boleta1.setPedidosIds("1");
        boletaRepository.save(boleta1);


        double b2Neto  = 23260.0;
        double b2Iva   = Math.round(b2Neto * 0.19 * 100.0) / 100.0;
        double b2Total = Math.round((b2Neto + b2Iva) * 100.0) / 100.0;

        MetodoPago boleta2 = new MetodoPago();
        boleta2.setClienteId(1L);
        boleta2.setClienteNombre("Bruno Mateluna");
        boleta2.setClienteRun("20688880-7");
        boleta2.setClienteCorreo("br.mateluna@duocuc.cl");
        boleta2.setClienteDireccion("Calle quinta vergara 666");
        boleta2.setClienteTelefono(949000000);
        boleta2.setTipoPago(MetodoPago.TipoPago.TRANSFERENCIA);
        boleta2.setTotalNeto(b2Neto);
        boleta2.setIva(b2Iva);
        boleta2.setTotalConIva(b2Total);
        boleta2.setFechaEmision(LocalDateTime.now().minusMonths(1).minusDays(8));
        boleta2.setEstado(MetodoPago.EstadoBoleta.EMITIDA);
        boleta2.setPedidosIds("2");
        boletaRepository.save(boleta2);


        double c1Neto  = 8980.0;
        double c1Iva   = Math.round(c1Neto * 0.19 * 100.0) / 100.0;
        double c1Total = Math.round((c1Neto + c1Iva) * 100.0) / 100.0;

        MetodoPago boleta3 = new MetodoPago();
        boleta3.setClienteId(2L);
        boleta3.setClienteNombre("Claudio Bravo");
        boleta3.setClienteRun("12345678-9");
        boleta3.setClienteCorreo("cl.bravo@chile.cl");
        boleta3.setClienteDireccion("Av.santiago 522");
        boleta3.setClienteTelefono(967342652);
        boleta3.setTipoPago(MetodoPago.TipoPago.EFECTIVO);
        boleta3.setTotalNeto(c1Neto);
        boleta3.setIva(c1Iva);
        boleta3.setTotalConIva(c1Total);
        boleta3.setFechaEmision(LocalDateTime.now().minusMonths(3));
        boleta3.setEstado(MetodoPago.EstadoBoleta.EMITIDA);
        boleta3.setPedidosIds("5");
        boletaRepository.save(boleta3);


        double c2Neto  = 33470.0;
        double c2Iva   = Math.round(c2Neto * 0.19 * 100.0) / 100.0;
        double c2Total = Math.round((c2Neto + c2Iva) * 100.0) / 100.0;

        MetodoPago boleta4 = new MetodoPago();
        boleta4.setClienteId(2L);
        boleta4.setClienteNombre("Claudio Bravo");
        boleta4.setClienteRun("12345678-9");
        boleta4.setClienteCorreo("cl.bravo@chile.cl");
        boleta4.setClienteDireccion("Av.santiago 522");
        boleta4.setClienteTelefono(967342652);
        boleta4.setTipoPago(MetodoPago.TipoPago.CREDITO);
        boleta4.setTotalNeto(c2Neto);
        boleta4.setIva(c2Iva);
        boleta4.setTotalConIva(c2Total);
        boleta4.setFechaEmision(LocalDateTime.now().minusDays(4));
        boleta4.setEstado(MetodoPago.EstadoBoleta.ANULADA);
        boleta4.setPedidosIds("6");
        boletaRepository.save(boleta4);

        log.info(">>> 4 boletas cargadas OK — Bruno Mateluna (2) | Claudio Bravo (2, 1 anulada).");
    }
}
