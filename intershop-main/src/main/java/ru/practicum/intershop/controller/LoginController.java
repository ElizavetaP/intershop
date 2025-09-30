package ru.practicum.intershop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public Mono<String> login(Principal principal) {
        log.info("Пользователь {} успешно авторизовался", principal.getName());
        return Mono.just("redirect:/main/items");
    }
}
