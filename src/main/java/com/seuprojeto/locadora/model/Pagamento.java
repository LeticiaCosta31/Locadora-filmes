package com.seuprojeto.locadora.model;

import java.time.LocalDate;

public class Pagamento {
    private int id;
    private Locacao locacao;
    private double valor;
    private LocalDate dataPagamento;
    private String tipo; // Ex: "Dinheiro", "Cartão"

    public Pagamento(int id, Locacao locacao, double valor, LocalDate dataPagamento, String tipo) {
        this.id = id;
        this.locacao = locacao;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public Locacao getLocacao() { return locacao; }
    public double getValor() { return valor; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public String getTipo() { return tipo; }

    public boolean validarPagamento() {
        return valor > 0 && dataPagamento != null && (tipo.equalsIgnoreCase("Dinheiro") || tipo.equalsIgnoreCase("Cartão"));
    }

    public void processarPagamento() {
        if (!validarPagamento()) {
            throw new IllegalArgumentException("Pagamento inválido");
        }
        // Lógica para processar pagamento (exemplo: registrar, enviar confirmação etc)
    }

    @Override
    public String toString() {
        return "Pagamento [id=" + id + ", locacaoId=" + locacao.getId() + ", valor=" + valor +
                ", dataPagamento=" + dataPagamento + ", tipo=" + tipo + "]";
    }
}
