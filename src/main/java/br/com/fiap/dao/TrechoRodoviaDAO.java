package br.com.fiap.dao;

import br.com.fiap.db.ConexaoBanco;
import br.com.fiap.model.TrechoComSensor;
import br.com.fiap.model.TrechoRodovia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrechoRodoviaDAO {

    public void listarTodos() {
        String sql = "SELECT * FROM trechos ORDER BY id";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String trecho = trechoEncontrado(rs);
                System.out.println(trecho);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar trechos: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o ResultSet: " + e.getMessage()); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o Statement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }
    }

    public void atualizar(TrechoRodovia trechoRodovia) {
        String sql = "UPDATE trechos SET nivelVegetacaoEmCm = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ConexaoBanco.getConexao();
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            pstmt.setDouble(1, trechoRodovia.getNivelVegetacaoEmCm());
            pstmt.setLong(2, trechoRodovia.getId());

            int rowsAffected = pstmt.executeUpdate();

            conn.commit();

            if (rowsAffected > 0) {
                System.out.printf("✅ Nível da vegetação atualizado no trecho %s\n\n", trechoRodovia.getNomeTrecho());
            } else {
                System.out.println("⚠️ Trecho não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar nível da vegetação: " + e.getMessage(), e);
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o PreparedStatement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }
    }

    public List<TrechoRodovia> buscarTodos() {
        String sql = "SELECT * FROM trechos ORDER BY NIVELVEGETACAOEMCM";
        List<TrechoRodovia> lista = new ArrayList<>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBanco.getConexao();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Long id = rs.getLong("id");
                String nome = rs.getString("nome");
                int kmInicial = rs.getInt("quilometroInicial");
                int kmFinal = rs.getInt("quilometroFinal");
                double nivelVeg = rs.getDouble("nivelVegetacaoEmCm");
                String tipoClima = rs.getString("tipoClima");
                boolean temSensor = rs.getInt("trechoComSenor") == 1;

                TrechoRodovia trecho;
                trecho = new TrechoRodovia(nome, kmInicial, kmFinal, nivelVeg, tipoClima);
                if (temSensor) {
                    trecho = new TrechoComSensor(nome, kmInicial, kmFinal, nivelVeg, tipoClima);
                }
                trecho.setId(id);
                lista.add(trecho);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar trechos do banco: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o ResultSet: " + e.getMessage()); }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { System.err.println("Erro ao fechar o Statement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage()); }
        }

        return lista;
    }

    public void atualizandoTrechosBanco() {
        TrechoRodoviaDAO trechoDAO = new TrechoRodoviaDAO();
        System.out.println("Atualizando crescimento no banco de dados...");
        Random random = new Random();

        List<TrechoRodovia> trechos = trechoDAO.buscarTodos();

        for (TrechoRodovia trecho : trechos) {
            if (trecho instanceof TrechoComSensor sensor) {
                sensor.simularCrescimento();
            } else {
                trecho.registrarCrescimento(random.nextInt(1,15));
            }
            trechoDAO.atualizar(trecho);
        }
    }

    public String trechoEncontrado(ResultSet rs) throws SQLException {
        return String.format("""
                Nome: %s,
                Km inicial: %d,
                Km final: %d,
                Nível vegetação: %.2f,
                Tipo clima: %s,
                Possui Sensor: %s
                """,
                rs.getString("nome"),
                rs.getInt("quilometroInicial"),
                rs.getInt("quilometroFinal"),
                rs.getDouble("nivelVegetacaoEmCm"),
                rs.getString("tipoClima"),
                rs.getInt("trechoComSenor") == 1 ? "Sim" : "Não"
        );
    }
}