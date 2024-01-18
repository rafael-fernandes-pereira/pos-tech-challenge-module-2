package com.github.rafaelfernandes.parquimetro.cliente.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record Contact(

        @NotEmpty(message = "O campo contact.email deve estar preenchido")
        @Email(message = "O campo contact.email deve ser um email válido")
        String email,
        @NotEmpty(message = "O campo contact.cellphone deve estar preenchido")
        @Pattern(regexp = "\\d{2}?9[1-9]\\d{3}\\d{4}", message = "O campo contact.cellphone está com formatação inválida")
        String cellphone
) {
}
