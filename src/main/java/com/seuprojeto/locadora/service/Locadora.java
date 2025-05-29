package com.seuprojeto.locadora.service;

import com.seuprojeto.locadora.model.Cliente;
import com.seuprojeto.locadora.model.Filme;
import com.seuprojeto.locadora.model.Locacao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class Locadora {

    private static final Logger logger = Logger.getLogger(Locadora.class.getName());

    private final String url = "jdbc:sqlite:./locadora.db"; // ajuste seu caminho

    private final Object lockDB = new Object(); // objeto para sincronização

    private Connection conn; // conexão única

    static {
        try {
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("locadora.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (Exception e) {
            System.out.println("Erro ao configurar o sistema de logs: " + e.getMessage());
        }
    }

    public Locadora() {
        try {
            this.conn = DriverManager.getConnection(url);
            logger.info("Conexão com o banco de dados estabelecida.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao conectar ao banco de dados.", e);
        }
        criarTabelasSeNaoExistem();
    }

    // Fecha a conexão quando terminar
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                logger.info("Conexão com banco fechada.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao fechar conexão.", e);
        }
    }

    public void limparTabelas() {
    synchronized(lockDB) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM locacao");
            stmt.executeUpdate("DELETE FROM cliente");
            stmt.executeUpdate("DELETE FROM filmeMarvel");
            logger.info("Tabelas limpas com sucesso.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao limpar tabelas.", e);
            throw new RuntimeException(e);
        }
    }
}


    private void criarTabelasSeNaoExistem() {
        String sqlFilme = "CREATE TABLE IF NOT EXISTS filmeMarvel (" +
                "id INTEGER PRIMARY KEY," +
                "titulo TEXT NOT NULL," +
                "diretor TEXT NOT NULL," +
                "ano_lancamento INTEGER NOT NULL," +
                "genero TEXT," +
                "data_estreia TEXT NOT NULL," +
                "categoria TEXT," +
                "duracao INTEGER," +
                "quantidade_estoque INTEGER DEFAULT 0" +
                ");";

        String sqlCliente = "CREATE TABLE IF NOT EXISTS cliente (" +
                "id INTEGER PRIMARY KEY," +
                "nome TEXT NOT NULL," +
                "telefone TEXT NOT NULL," +
                "endereco TEXT" +
                ");";

        String sqlLocacao = "CREATE TABLE IF NOT EXISTS locacao (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_cliente INTEGER NOT NULL," +
                "id_filme INTEGER NOT NULL," +
                "data_aluguel TEXT NOT NULL," +
                "data_prevista_devolucao TEXT NOT NULL," +
                "devolvido INTEGER DEFAULT 0," +
                "FOREIGN KEY(id_cliente) REFERENCES cliente(id)," +
                "FOREIGN KEY(id_filme) REFERENCES filmeMarvel(id)" +
                ");";

        synchronized(lockDB) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sqlFilme);
                stmt.execute(sqlCliente);
                stmt.execute(sqlLocacao);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao criar tabelas", e);
            }
        }
    }

    // FILME

    public void cadastrarFilme(Filme filme) {
        String sql = "INSERT INTO filmeMarvel(id, titulo, diretor, ano_lancamento, genero, data_estreia, categoria, duracao, quantidade_estoque) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        synchronized(lockDB) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, filme.getId());
                pstmt.setString(2, filme.getTitulo());
                pstmt.setString(3, filme.getDiretor());
                pstmt.setInt(4, filme.getAnoLancamento());
                pstmt.setString(5, filme.getGenero());
                pstmt.setString(6, filme.getDataEstreia());
                pstmt.setString(7, filme.getCategoria());
                pstmt.setInt(8, filme.getDuracao());
                pstmt.setInt(9, filme.getQuantidadeEstoque());
                pstmt.executeUpdate();
                logger.info("Filme cadastrado com sucesso: " + filme.getTitulo());
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao cadastrar filme: " + filme.getTitulo(), e);
            }
        }
    }

    public List<Filme> listarFilmes() {
        List<Filme> filmes = new ArrayList<>();
        String sql = "SELECT * FROM filmeMarvel";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Filme filme = new Filme(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        rs.getInt("duracao"),
                        rs.getInt("quantidade_estoque"),
                        rs.getString("diretor"),
                        rs.getInt("ano_lancamento"),
                        rs.getString("genero"),
                        rs.getString("data_estreia"));

                filmes.add(filme);
            }
            logger.info("Filmes listados com sucesso. Total: " + filmes.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar filmes.", e);
        }
        return filmes;
    }

    public Filme buscarFilmePorId(int id) {
        String sql = "SELECT * FROM filmeMarvel WHERE id = ?";
        Filme filme = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    filme = new Filme(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("categoria"),
                            rs.getInt("duracao"),
                            rs.getInt("quantidade_estoque"),
                            rs.getString("diretor"),
                            rs.getInt("ano_lancamento"),
                            rs.getString("genero"),
                            rs.getString("data_estreia"));
                }
            }
            if(filme != null)
                logger.info("Filme encontrado: " + filme.getTitulo());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar filme com ID: " + id, e);
        }
        return filme;
    }

    public void atualizarFilme(Filme filme) {
        String sql = "UPDATE filmeMarvel SET titulo = ?, categoria = ?, duracao = ?, quantidade_estoque = ?, diretor = ?, ano_lancamento = ?, genero = ?, data_estreia = ? WHERE id = ?";

        synchronized(lockDB) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, filme.getTitulo());
                pstmt.setString(2, filme.getCategoria());
                pstmt.setInt(3, filme.getDuracao());
                pstmt.setInt(4, filme.getQuantidadeEstoque());
                pstmt.setString(5, filme.getDiretor());
                pstmt.setInt(6, filme.getAnoLancamento());
                pstmt.setString(7, filme.getGenero());
                pstmt.setString(8, filme.getDataEstreia());
                pstmt.setInt(9, filme.getId());

                int updated = pstmt.executeUpdate();
                if (updated == 0) {
                    logger.warning("Nenhum filme atualizado com o id: " + filme.getId());
                } else {
                    logger.info("Filme atualizado com sucesso: " + filme.getTitulo());
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao atualizar filme: " + filme.getTitulo(), e);
            }
        }
    }

    public void deletarFilme(int id) {
        String sql = "DELETE FROM filmeMarvel WHERE id = ?";

        synchronized(lockDB) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                logger.info("Filme deletado com sucesso. ID: " + id);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao deletar filme com ID: " + id, e);
            }
        }
    }

    // CLIENTE

    public void cadastrarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente(id, nome, telefone, endereco) VALUES (?, ?, ?, ?)";

        synchronized(lockDB) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, cliente.getId());
                pstmt.setString(2, cliente.getNome());
                pstmt.setString(3, cliente.getTelefone());
                pstmt.setString(4, cliente.getEndereco());
                pstmt.executeUpdate();
                logger.info("Cliente cadastrado com sucesso: " + cliente.getNome());
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao cadastrar cliente: " + cliente.getNome(), e);
            }
        }
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("endereco"));
                clientes.add(cliente);
            }
            logger.info("Clientes listados com sucesso. Total: " + clientes.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar clientes.", e);
        }
        return clientes;
    }

    public Cliente buscarClientePorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        Cliente cliente = null;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("telefone"),
                            rs.getString("endereco"));
                }
            }
            if(cliente != null)
                logger.info("Cliente encontrado: " + cliente.getNome());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar cliente com ID: " + id, e);
        }
        return cliente;
    }

    // LOCAÇÃO

    public Locacao realizarLocacao(int idCliente, int idFilme, LocalDate dataPrevistaDevolucao) {
        Cliente cliente = buscarClientePorId(idCliente);
        Filme filme = buscarFilmePorId(idFilme);

        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado");
        }
        if (filme.getQuantidadeEstoque() <= 0) {
            throw new IllegalStateException("Filme sem estoque disponível");
        }

        String sql = "INSERT INTO locacao(id_cliente, id_filme, data_aluguel, data_prevista_devolucao, devolvido) VALUES (?, ?, ?, ?, 0)";

        synchronized(lockDB) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idCliente);
                pstmt.setInt(2, idFilme);
                pstmt.setString(3, LocalDate.now().toString());
                pstmt.setString(4, dataPrevistaDevolucao.toString());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idLocacao = generatedKeys.getInt(1);

                        // Atualiza estoque do filme
                        filme.setQuantidadeEstoque(filme.getQuantidadeEstoque() - 1);
                        atualizarFilme(filme);

                        Locacao locacao = new Locacao(idLocacao, cliente, filme, LocalDate.now(), dataPrevistaDevolucao);
                        logger.info("Locação realizada com sucesso. ID: " + idLocacao);
                        return locacao;
                    } else {
                        throw new SQLException("Falha ao obter ID da locação.");
                    }
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao realizar locação.", e);
                throw new RuntimeException(e);
            }
        }
    }

    public void registrarDevolucao(int idLocacao) {
        String sqlBusca = "SELECT * FROM locacao WHERE id = ?";
        String sqlAtualiza = "UPDATE locacao SET devolvido = 1 WHERE id = ?";

        synchronized(lockDB) {
            try (PreparedStatement pstmtBusca = conn.prepareStatement(sqlBusca)) {
                pstmtBusca.setInt(1, idLocacao);
                try (ResultSet rs = pstmtBusca.executeQuery()) {
                    if (rs.next()) {
                        int idFilme = rs.getInt("id_filme");
                        Filme filme = buscarFilmePorId(idFilme);
                        if (filme == null) {
                            throw new SQLException("Filme da locação não encontrado");
                        }

                        // Atualiza estoque
                        filme.setQuantidadeEstoque(filme.getQuantidadeEstoque() + 1);
                        atualizarFilme(filme);

                        // Atualiza status devolvido no banco
                        try (PreparedStatement pstmtAtualiza = conn.prepareStatement(sqlAtualiza)) {
                            pstmtAtualiza.setInt(1, idLocacao);
                            pstmtAtualiza.executeUpdate();
                        }

                        logger.info("Devolução registrada com sucesso para locação ID: " + idLocacao);
                    } else {
                        throw new SQLException("Locação não encontrada");
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao registrar devolução.", e);
                throw new RuntimeException(e);
            }
        }
    }

    public List<Locacao> listarLocacoes() {
        List<Locacao> locacoes = new ArrayList<>();
        String sql = "SELECT l.id, l.id_cliente, l.id_filme, l.data_aluguel, l.data_prevista_devolucao, l.devolvido, " +
                "c.nome AS nome_cliente, c.telefone, c.endereco, " +
                "f.titulo, f.diretor, f.ano_lancamento, f.genero, f.data_estreia, f.categoria, f.duracao, f.quantidade_estoque " +
                "FROM locacao l " +
                "JOIN cliente c ON l.id_cliente = c.id " +
                "JOIN filmeMarvel f ON l.id_filme = f.id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("nome_cliente"),
                        rs.getString("telefone"),
                        rs.getString("endereco"));
                Filme filme = new Filme(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("categoria"),
                        rs.getInt("duracao"),
                        rs.getInt("quantidade_estoque"),
                        rs.getString("diretor"),
                        rs.getInt("ano_lancamento"),
                        rs.getString("genero"),
                        rs.getString("data_estreia"));

                Locacao locacao = new Locacao(
                        rs.getInt("id"),
                        cliente,
                        filme,
                        LocalDate.parse(rs.getString("data_aluguel")),
                        LocalDate.parse(rs.getString("data_prevista_devolucao")));
                if (rs.getInt("devolvido") == 1) {
                    locacao.registrarDevolucao();
                }
                locacoes.add(locacao);
            }
            logger.info("Locações listadas com sucesso. Total: " + locacoes.size());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar locações.", e);
        }
        return locacoes;
    }
}
