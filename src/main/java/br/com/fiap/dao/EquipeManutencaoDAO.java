package br.com.fiap.dao;

import br.com.fiap.db.ConexaoBanco;
import br.com.fiap.model.EquipeManutencao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipeManutencaoDAO{

    public void listarTodos() {
        String sql = "SELECT * FROM equipesManutencao ORDER BY id";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String equipe = equipeEncontrada(rs);
                System.out.println(equipe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar equipes: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o ResultSet: " + e.getMessage()); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o Statement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }
    }

    public List<EquipeManutencao> buscarTodos() {
        String sql = "SELECT * FROM equipesManutencao";
        List<EquipeManutencao> lista = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Long id = rs.getLong("id");
                String nome = rs.getString("nomeEquipe");
                int qtdFuncionarios = rs.getInt("quantidadeFuncionarios");
                String rocada = rs.getString("rocadaDeAtuacao");

                EquipeManutencao equipe = new EquipeManutencao(nome, qtdFuncionarios, rocada);
                equipe.setId(id);
                lista.add(equipe);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar equipes do banco: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o ResultSet: " + e.getMessage()); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o Statement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }

        return lista;
    }

    public String equipeEncontrada(ResultSet rs) throws SQLException {
        return String.format("""
                Nome da Equipe: %s,
                Quantidade de Funcionários: %d,
                Roçada de Atuação: %s
                """,
                rs.getString("nomeEquipe"),
                rs.getInt("quantidadeFuncionarios"),
                rs.getString("rocadaDeAtuacao")
        );
    }
}