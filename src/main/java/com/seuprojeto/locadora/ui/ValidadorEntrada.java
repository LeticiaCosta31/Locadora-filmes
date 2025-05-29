package com.seuprojeto.locadora.ui;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

public class ValidadorEntrada {

    public String validarEntradaTexto(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        String entrada = scanner.nextLine();

        while (entrada.trim().isEmpty()) {
            System.out.println("O campo é obrigatório. Por favor, insira um valor válido.");
            System.out.print(mensagem);
            entrada = scanner.nextLine();
        }
        return entrada;
    }

    public int validarEntradaNumerica(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextInt()) {
            System.out.println("A entrada deve ser um número. Por favor, tente novamente.");
            System.out.print(mensagem);
            scanner.next();
        }
        int entrada = scanner.nextInt();
        scanner.nextLine();
        return entrada;
    }

    public String validarEntradaData(Scanner scanner, String mensagem) {
        String entrada;
        while (true) {
            System.out.print(mensagem);
            entrada = scanner.nextLine().trim();

            if (entrada.isEmpty()) {
                System.out.println("O campo é obrigatório. Por favor, insira um valor válido.");
                continue;
            }

            String[] vet = entrada.split("-");
            if (vet.length != 3) {
                System.out.println("Formato de data inválido. Por favor, insira uma data no formato YYYY-MM-DD.");
                continue;
            }

            try {
                int ano = Integer.parseInt(vet[0]);
                int mes = Integer.parseInt(vet[1]);
                int dia = Integer.parseInt(vet[2]);

                if (ano < 1900 || ano > 2100) {
                    System.out.println("Ano fora do intervalo permitido (1900-2100). Por favor, insira uma data válida.");
                    continue;
                }

                if (mes < 1 || mes > 12) {
                    System.out.println("Mês inválido. Por favor, insira um mês entre 01 e 12.");
                    continue;
                }

                if (dia < 1 || dia > 31) {
                    System.out.println("Dia inválido. Por favor, insira um dia entre 01 e 31.");
                    continue;
                }

                if (!isValidDate(ano, mes, dia)) {
                    System.out.println("Data inválida. Por favor, insira uma data real.");
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Formato de data inválido. Por favor, insira uma data no formato YYYY-MM-DD.");
            }
        }
        return entrada;
    }

    private static boolean isValidDate(int ano, int mes, int dia) {
        try {
            LocalDate.of(ano, mes, dia);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public int validarAnoLancamento(Scanner scanner, String mensagem) {
        int ano;
        while (true) {
            ano = validarEntradaNumerica(scanner, mensagem);
            if (ano < 1900 || ano > 2100) {
                System.out.println("Ano fora do intervalo permitido (1900-2100). Por favor, insira um ano válido.");
            } else {
                break;
            }
        }
        return ano;
    }
}
