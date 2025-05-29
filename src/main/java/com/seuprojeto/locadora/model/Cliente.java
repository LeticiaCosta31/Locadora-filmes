package com.seuprojeto.locadora.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nome;
    private String telefone;
    private String endereco;
    private List<Locacao> alugueisAtivos;

    public Cliente(int id, String nome, String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.alugueisAtivos = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public List<Locacao> getAlugueisAtivos() { return alugueisAtivos; }

    public void adicionarLocacao(Locacao locacao) {
        alugueisAtivos.add(locacao);
    }

    public void removerLocacao(Locacao locacao) {
        alugueisAtivos.remove(locacao);
    }

    public boolean validarCadastro() {
        return nome != null && !nome.trim().isEmpty() &&
               telefone != null && !telefone.trim().isEmpty();
    }

    public boolean possuiPendencias() {
        for (Locacao loc : alugueisAtivos) {
            if (!loc.isDevolvido()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Cliente [id=" + id + ", nome=" + nome + ", telefone=" + telefone + ", endereco=" + endereco + "]";
    }
}
