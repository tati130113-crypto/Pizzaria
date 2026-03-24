// dao/PedidoDAO.java
package pizzaria.dao;

import pizzaria.model.Pedido;
import pizzaria.model.ItemPedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {
    
    public boolean inserir(Pedido pedido) {
        Connection conn = null;
        try {
            conn = ConexaoBD.getConexao();
            conn.setAutoCommit(false);
            
            // Inserir pedido
            String sqlPedido = "INSERT INTO pedidos (usuario_id, status, valor_total, observacoes) " +
                               "VALUES (?, ?, ?, ?)";
            PreparedStatement stmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            stmtPedido.setInt(1, pedido.getUsuarioId());
            stmtPedido.setString(2, pedido.getStatus());
            stmtPedido.setDouble(3, pedido.getValorTotal());
            stmtPedido.setString(4, pedido.getObservacoes());
            
            stmtPedido.executeUpdate();
            
            ResultSet rs = stmtPedido.getGeneratedKeys();
            int pedidoId = 0;
            if (rs.next()) {
                pedidoId = rs.getInt(1);
            }
            
            // Inserir itens do pedido
            String sqlItem = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal) " +
                             "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtItem = conn.prepareStatement(sqlItem);
            
            for (ItemPedido item : pedido.getItens()) {
                stmtItem.setInt(1, pedidoId);
                stmtItem.setInt(2, item.getProdutoId());
                stmtItem.setInt(3, item.getQuantidade());
                stmtItem.setDouble(4, item.getPrecoUnitario());
                stmtItem.setDouble(5, item.getSubtotal());
                stmtItem.addBatch();
            }
            
            stmtItem.executeBatch();
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro no rollback: " + ex.getMessage());
                }
            }
            System.err.println("Erro ao inserir pedido: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Erro ao resetar auto commit: " + e.getMessage());
                }
            }
        }
    }
    
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT p.*, u.nome as usuario_nome FROM pedidos p " +
                     "LEFT JOIN usuarios u ON p.usuario_id = u.id " +
                     "ORDER BY p.data_pedido DESC";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setUsuarioId(rs.getInt("usuario_id"));
                pedido.setNomeUsuario(rs.getString("usuario_nome"));
                pedido.setDataPedido(rs.getTimestamp("data_pedido"));
                pedido.setStatus(rs.getString("status"));
                pedido.setValorTotal(rs.getDouble("valor_total"));
                pedido.setObservacoes(rs.getString("observacoes"));
                
                // Carregar itens do pedido
                pedido.setItens(listarItensPorPedido(pedido.getId()));
                pedidos.add(pedido);
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar pedidos: " + e.getMessage());
        }
        return pedidos;
    }
    
    public List<ItemPedido> listarItensPorPedido(int pedidoId) {
        List<ItemPedido> itens = new ArrayList<>();
        String sql = "SELECT ip.*, p.nome as produto_nome FROM itens_pedido ip " +
                     "LEFT JOIN produtos p ON ip.produto_id = p.id " +
                     "WHERE ip.pedido_id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ItemPedido item = new ItemPedido();
                item.setId(rs.getInt("id"));
                item.setPedidoId(rs.getInt("pedido_id"));
                item.setProdutoId(rs.getInt("produto_id"));
                item.setNomeProduto(rs.getString("produto_nome"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getDouble("preco_unitario"));
                item.setSubtotal(rs.getDouble("subtotal"));
                itens.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens do pedido: " + e.getMessage());
        }
        return itens;
    }
    
    public boolean atualizarStatus(int pedidoId, String status) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, pedidoId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
            return false;
        }
    }
}