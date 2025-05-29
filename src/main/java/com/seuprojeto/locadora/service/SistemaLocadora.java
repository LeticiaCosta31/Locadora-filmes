package com.seuprojeto.locadora.service;

import com.seuprojeto.locadora.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SistemaLocadora {

    private Locadora locadora;
    private Estoque estoque;
    private Notificacao notificacao;

    public SistemaLocadora() {
        locadora = new Locadora();
        estoque = new Estoque();
        notificacao = new Notificacao();
    }

    // Filmes
    public void cadastrarFilme(Filme filme, int quantidadeEstoque) {
        locadora.cadastrarFilme(filme);
        estoque.adicionarFilme(filme, quantidadeEstoque);
    }

    public List<Filme> listarFilmesDisponiveis() {
        List<Filme> disponiveis = new ArrayList<>();
        for (Filme f : locadora.listarFilmes()) {
            if (estoque.verificarDisponibilidade(f)) {
                disponiveis.add(f);
            }
        }
        return disponiveis;
    }

    // Clientes
    public void cadastrarCliente(Cliente cliente) {
        locadora.cadastrarCliente(cliente);
    }

    // Locação
    public Locacao realizarLocacao(int idCliente, int idFilme, LocalDate dataPrevistaDevolucao) {
        Filme filme = locadora.buscarFilmePorId(idFilme);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }
        if (!estoque.verificarDisponibilidade(filme)) {
            throw new IllegalStateException("Filme não disponível no estoque");
        }

        Locacao locacao = locadora.realizarLocacao(idCliente, idFilme, dataPrevistaDevolucao);
        estoque.atualizarEstoque(filme, estoque.quantidadeDisponivel(filme) - 1);

        return locacao;
    }

    public void registrarDevolucao(int idLocacao) {
        Locacao locacao = null;
        for (Locacao l : locadora.listarLocacoes()) {
            if (l.getId() == idLocacao) {
                locacao = l;
                break;
            }
        }
        if (locacao == null) {
            throw new IllegalArgumentException("Locação não encontrada");
        }

        locadora.registrarDevolucao(idLocacao);
        Filme filme = locacao.getFilme();
        estoque.atualizarEstoque(filme, estoque.quantidadeDisponivel(filme) + 1);
    }

    // Notificações
    public void enviarNotificacaoAtraso(Cliente cliente) {
        notificacao.enviarNotificacaoAtraso(cliente);
    }

    // Outros métodos podem ser adicionados conforme necessário
}
