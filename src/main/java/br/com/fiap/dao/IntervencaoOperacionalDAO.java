package br.com.fiap.dao;
import br.com.fiap.db.ConexaoBanco;
import br.com.fiap.model.*;

import java.sql.*;
import br.com.fiap.model.IntervencaoOperacional;

public class IntervencaoOperacionalDAO {


    public void inserir(IntervencaoOperacional intervencao, TrechoRodovia trecho, EquipeManutencao equipe) {
        String sql = "INSERT INTO intervencoesOperacionais (nome, quilometroInicial, quilometroFinal, tipoClima, nomeEquipe) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            pstmt = conn.prepareStatement(sql, new String[]{"ID"});

            pstmt.setString(1, trecho.getNomeTrecho());
            pstmt.setInt(2, trecho.getQuilometroInicial());
            pstmt.setInt(3, trecho.getQuilometroFinal());
            pstmt.setString(4, trecho.getTipoClima());
            pstmt.setString(5, equipe.getNomeEquipe());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {

                    intervencao.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir intervenção: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o ResultSet: " + e.getMessage()); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o PreparedStatement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }
    }

    public void listarTodos() {
        String sql = "SELECT * FROM intervencoesOperacionais ORDER BY id";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String intervencao = intervencaoEncontrada(rs);
                System.out.println(intervencao);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar intervenções: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o ResultSet: " + e.getMessage()); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o Statement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }
    }

    public String intervencaoEncontrada(ResultSet rs) throws SQLException {
        return String.format("""
                ID: %d,
                Nome: %s,
                Km inicial: %d,
                Km final: %d,
                Tipo clima: %s,
                Nome da Equipe: %s
                """,
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getInt("quilometroInicial"),
                rs.getInt("quilometroFinal"),
                rs.getString("tipoClima"),
                rs.getString("nomeEquipe")
        );
    }
}