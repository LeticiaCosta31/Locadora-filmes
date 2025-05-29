package com.seuprojeto.locadora.service;

import com.seuprojeto.locadora.model.Cliente;

public class Notificacao {

    public void enviarNotificacaoAtraso(Cliente cliente) {
        // Simulação simples de envio (pode substituir por email ou SMS)
        System.out.println("Notificação para cliente " + cliente.getNome() + ": Você possui locações atrasadas. Por favor, regularize.");
    }

    public void enviarNotificacaoPromocao(Cliente cliente, String promocao) {
        System.out.println("Notificação para cliente " + cliente.getNome() + ": Promoção especial! " + promocao);
    }

    public void enviarAvisoFuncionario(String mensagem) {
        System.out.println("Aviso para funcionários: " + mensagem);
    }
}
