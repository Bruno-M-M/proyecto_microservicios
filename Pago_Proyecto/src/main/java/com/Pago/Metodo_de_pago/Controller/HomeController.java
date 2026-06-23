package com.Pago.Metodo_de_pago.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/actuator/info")
    public String info() {
        return "redirect:/doc/swagger-ui/index.html";
    }
}
