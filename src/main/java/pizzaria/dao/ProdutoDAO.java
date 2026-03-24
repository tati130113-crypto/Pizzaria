// dao/ProdutoDAO.java
package pizzaria.dao;

import pizzaria.model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    
    public List<Produto> listarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, c.nome as categoria_nome FROM produtos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.ativo = true ORDER BY p.nome";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexaoBD.getConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setCategoriaId(rs.getInt("categoria_id"));
                produto.setNomeCategoria(rs.getString("categoria_nome"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setAtivo(rs.getBoolean("ativo"));
                produtos.add(produto);
            }
            
            System.out.println("📋 Produtos listados: " + produtos.size());
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            // NÃO FECHA A CONEXÃO AQUI
        }
        return produtos;
    }
    
    public Produto buscarPorId(int id) {
        String sql = "SELECT p.*, c.nome as categoria_nome FROM produtos p " +
                     "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                     "WHERE p.id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexaoBD.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setCategoriaId(rs.getInt("categoria_id"));
                produto.setNomeCategoria(rs.getString("categoria_nome"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setAtivo(rs.getBoolean("ativo"));
                return produto;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar produto: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
        return null;
    }
    
    public boolean inserir(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco, categoria_id, quantidade_estoque, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, 1)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexaoBD.getConexao();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getCategoriaId());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                    System.out.println("✅ Produto inserido! ID: " + produto.getId());
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
    }
    
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, " +
                     "categoria_id = ?, quantidade_estoque = ? WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexaoBD.getConexao();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getCategoriaId());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setInt(6, produto.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
    }
    
    public boolean deletar(int id) {
        String sql = "UPDATE produtos SET ativo = false WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = ConexaoBD.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        }
    }
}