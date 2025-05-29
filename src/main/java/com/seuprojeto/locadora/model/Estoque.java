package com.seuprojeto.locadora.model;

import java.util.HashMap;
import java.util.Map;

public class Estoque {
    private Map<Filme, Integer> estoqueFilmes;

    public Estoque() {
        estoqueFilmes = new HashMap<>();
    }

    public void adicionarFilme(Filme filme, int quantidade) {
        estoqueFilmes.put(filme, estoqueFilmes.getOrDefault(filme, 0) + quantidade);
        filme.setQuantidadeEstoque(estoqueFilmes.get(filme));
    }

    public void atualizarEstoque(Filme filme, int novaQuantidade) {
        estoqueFilmes.put(filme, novaQuantidade);
        filme.setQuantidadeEstoque(novaQuantidade);
    }

    public boolean verificarDisponibilidade(Filme filme) {
        return estoqueFilmes.getOrDefault(filme, 0) > 0;
    }

    public int quantidadeDisponivel(Filme filme) {
        return estoqueFilmes.getOrDefault(filme, 0);
    }
}
