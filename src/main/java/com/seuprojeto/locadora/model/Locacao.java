package com.seuprojeto.locadora.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Locacao {
    private int id;
    private Cliente cliente;
    private Filme filme;
    private LocalDate dataAluguel;
    private LocalDate dataPrevistaDevolucao;
    private boolean devolvido;

    public Locacao(int id, Cliente cliente, Filme filme, LocalDate dataAluguel, LocalDate dataPrevistaDevolucao) {
        this.id = id;
        this.cliente = cliente;
        this.filme = filme;
        this.dataAluguel = dataAluguel;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.devolvido = false;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Filme getFilme() { return filme; }
    public LocalDate getDataAluguel() { return dataAluguel; }
    public LocalDate getDataPrevistaDevolucao() { return dataPrevistaDevolucao; }
    public boolean isDevolvido() { return devolvido; }

    public void registrarDevolucao() {
        this.devolvido = true;
    }

    public long calcularAtraso() {
        if (!devolvido) {
            long dias = ChronoUnit.DAYS.between(dataPrevistaDevolucao, LocalDate.now());
            return dias > 0 ? dias : 0;
        }
        return 0;
    }

    public double calcularMulta(double valorPorDia) {
        return calcularAtraso() * valorPorDia;
    }

    @Override
    public String toString() {
        return "Locacao [id=" + id + ", cliente=" + cliente.getNome() + ", filme=" + filme.getTitulo() +
                ", dataAluguel=" + dataAluguel + ", dataPrevistaDevolucao=" + dataPrevistaDevolucao +
                ", devolvido=" + devolvido + "]";
    }
}
