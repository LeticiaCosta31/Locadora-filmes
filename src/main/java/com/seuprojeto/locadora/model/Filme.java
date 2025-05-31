package com.seuprojeto.locadora.model;

public class Filme {
    private int id;
    private String titulo;
    private String categoria;
    private int duracao; // em minutos
    private int quantidadeEstoque;
    private String diretor;
    private int anoLancamento;
    private String genero;
    private String dataEstreia;

    public Filme(int id, String titulo, String categoria, int duracao, int quantidadeEstoque,
                 String diretor, int anoLancamento, String genero, String dataEstreia) {
        this.id = id;
        this.titulo = titulo;
        this.categoria = categoria;
        this.duracao = duracao;
        this.quantidadeEstoque = quantidadeEstoque;
        this.diretor = diretor;
        this.anoLancamento = anoLancamento;
        this.genero = genero;
        this.dataEstreia = dataEstreia;
    }
    public Filme(){
        
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getDuracao() { return duracao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }

    public String getDiretor() { return diretor; }
    public void setDiretor(String diretor) { this.diretor = diretor; }

    public int getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(int anoLancamento) { this.anoLancamento = anoLancamento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getDataEstreia() { return dataEstreia; }
    public void setDataEstreia(String dataEstreia) { this.dataEstreia = dataEstreia; }

    public boolean verificarDisponibilidade() {
        return quantidadeEstoque > 0;
    }

    public void atualizarEstoque(int quantidade) {
        this.quantidadeEstoque += quantidade; // Pode ser positivo ou negativo
        if (this.quantidadeEstoque < 0) {
            this.quantidadeEstoque = 0;
        }
    }

    @Override
    public String toString() {
        return "Filme [id=" + id + ", titulo=" + titulo + ", categoria=" + categoria + ", duracao=" + duracao +
                " min, estoque=" + quantidadeEstoque + ", diretor=" + diretor + ", anoLancamento=" + anoLancamento +
                ", genero=" + genero + ", dataEstreia=" + dataEstreia + "]";
    }
}
