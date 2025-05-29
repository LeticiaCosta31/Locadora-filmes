package com.seuprojeto.locadora.service;

import com.seuprojeto.locadora.model.Locacao;
import com.seuprojeto.locadora.model.Filme;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioLocacoes {

    // Gera relatório de locações por período
    public static List<Locacao> gerarRelatorioPorPeriodo(List<Locacao> locacoes, LocalDate inicio, LocalDate fim) {
        return locacoes.stream()
                .filter(loc -> !loc.getDataAluguel().isBefore(inicio) && !loc.getDataAluguel().isAfter(fim))
                .collect(Collectors.toList());
    }

    // Gera relatório dos filmes mais alugados (top N)
    public static List<Map.Entry<Filme, Long>> filmesMaisAlugados(List<Locacao> locacoes, int topN) {
        Map<Filme, Long> contagem = locacoes.stream()
                .collect(Collectors.groupingBy(Locacao::getFilme, Collectors.counting()));

        return contagem.entrySet().stream()
                .sorted(Map.Entry.<Filme, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }
}
