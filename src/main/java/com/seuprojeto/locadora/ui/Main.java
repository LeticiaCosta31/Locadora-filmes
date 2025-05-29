package com.seuprojeto.locadora.ui;

import com.seuprojeto.locadora.model.Cliente;
import com.seuprojeto.locadora.model.Filme;
import com.seuprojeto.locadora.model.Locacao;
import com.seuprojeto.locadora.service.Locadora;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static void listarFilmes(Locadora locadora) {
        List<Filme> filmes = locadora.listarFilmes();
        if (filmes.isEmpty()) {
            System.out.println("Nenhum filme cadastrado.");
        } else {
            filmes.forEach(System.out::println);
        }
    }

    private static void listarClientes(Locadora locadora) {
        List<Cliente> clientes = locadora.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            clientes.forEach(System.out::println);
        }
    }

    private static void listarLocacoes(Locadora locadora) {
        List<Locacao> locacoes = locadora.listarLocacoes();
        if (locacoes.isEmpty()) {
            System.out.println("Nenhuma locação encontrada.");
        } else {
            locacoes.forEach(System.out::println);
        }
    }

    public static void main(String[] args) {
        Locadora locadora = new Locadora();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Listar Filmes");
            System.out.println("2. Cadastrar Filme");
            System.out.println("3. Listar Clientes");
            System.out.println("4. Cadastrar Cliente");
            System.out.println("5. Realizar Locação");
            System.out.println("6. Registrar Devolução");
            System.out.println("7. Listar Locações");
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (opcao) {
                    case 1 -> listarFilmes(locadora);

                    case 2 -> {
                        System.out.print("ID do filme: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Título: ");
                        String titulo = scanner.nextLine();
                        System.out.print("Categoria: ");
                        String categoria = scanner.nextLine();
                        System.out.print("Duração (minutos): ");
                        int duracao = scanner.nextInt();
                        System.out.print("Quantidade no estoque: ");
                        int estoque = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Diretor: ");
                        String diretor = scanner.nextLine();
                        System.out.print("Ano de lançamento: ");
                        int ano = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Gênero: ");
                        String genero = scanner.nextLine();
                        System.out.print("Data de estreia (YYYY-MM-DD): ");
                        String dataEstreia = scanner.nextLine();

                        Filme filme = new Filme(id, titulo, categoria, duracao, estoque, diretor, ano, genero, dataEstreia);
                        locadora.cadastrarFilme(filme);
                        System.out.println("Filme cadastrado com sucesso!");
                    }

                    case 3 -> listarClientes(locadora);

                    case 4 -> {
                        System.out.print("ID do cliente: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nome: ");
                        String nome = scanner.nextLine();
                        System.out.print("Telefone: ");
                        String telefone = scanner.nextLine();
                        System.out.print("Endereço: ");
                        String endereco = scanner.nextLine();

                        Cliente cliente = new Cliente(id, nome, telefone, endereco);
                        locadora.cadastrarCliente(cliente);
                        System.out.println("Cliente cadastrado com sucesso!");
                    }

                    case 5 -> {
                        System.out.print("ID do cliente: ");
                        int idCliente = scanner.nextInt();
                        System.out.print("ID do filme: ");
                        int idFilme = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Data prevista de devolução (YYYY-MM-DD): ");
                        String dataPrev = scanner.nextLine();

                        Locacao locacao = locadora.realizarLocacao(idCliente, idFilme, LocalDate.parse(dataPrev));
                        System.out.println("Locação realizada com sucesso! ID da locação: " + locacao.getId());
                    }

                    case 6 -> {
                        System.out.print("ID da locação para devolução: ");
                        int idLocacao = scanner.nextInt();
                        scanner.nextLine();
                        locadora.registrarDevolucao(idLocacao);
                        System.out.println("Devolução registrada com sucesso!");
                    }

                    case 7 -> listarLocacoes(locadora);

                    case 8 -> {
                        System.out.println("Saindo...");
                        scanner.close();
                        return;
                    }

                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
}
