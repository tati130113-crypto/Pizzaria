// view/TelaPedidos.java
package pizzaria.view;

import pizzaria.dao.PedidoDAO;
import pizzaria.dao.ProdutoDAO;
import pizzaria.model.Pedido;
import pizzaria.model.Produto;
import pizzaria.model.ItemPedido;
import pizzaria.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TelaPedidos extends JDialog {
    private JComboBox<Produto> cbProdutos;
    private JSpinner spQuantidade;
    private JButton btnAdicionar, btnFinalizar, btnCancelar;
    private JTable tabelaItens;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private List<ItemPedido> itensCarrinho;
    private ProdutoDAO produtoDAO;
    private PedidoDAO pedidoDAO;
    private Usuario usuario;
    private double total;
    
    public TelaPedidos(JFrame parent, Usuario usuario) {
        super(parent, "Novo Pedido", true);
        this.usuario = usuario;
        produtoDAO = new ProdutoDAO();
        pedidoDAO = new PedidoDAO();
        itensCarrinho = new ArrayList<>();
        total = 0;
        initComponents();
        carregarProdutos();
    }
    
    private void initComponents() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Painel superior - Seleção de produtos
        JPanel panelSelecao = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSelecao.setBorder(BorderFactory.createTitledBorder("Adicionar Itens"));
        
        panelSelecao.add(new JLabel("Produto:"));
        cbProdutos = new JComboBox<>();
        cbProdutos.setPreferredSize(new Dimension(300, 25));
        panelSelecao.add(cbProdutos);
        
        panelSelecao.add(new JLabel("Quantidade:"));
        spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spQuantidade.setPreferredSize(new Dimension(80, 25));
        panelSelecao.add(spQuantidade);
        
        btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarItem());
        panelSelecao.add(btnAdicionar);
        
        add(panelSelecao, BorderLayout.NORTH);
        
        // Tabela de itens do pedido
        String[] colunas = {"ID", "Produto", "Quantidade", "Preço Unit.", "Subtotal"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaItens = new JTable(tableModel);
        JScrollPane scrollTabela = new JScrollPane(tabelaItens);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Itens do Pedido"));
        add(scrollTabela, BorderLayout.CENTER);
        
        // Painel inferior - Totais e ações
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTotal.add(new JLabel("Total do Pedido:"));
        lblTotal = new JLabel("R$ 0,00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 100, 0));
        panelTotal.add(lblTotal);
        panelInferior.add(panelTotal, BorderLayout.NORTH);
        
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnFinalizar = new JButton("Finalizar Pedido");
        btnCancelar = new JButton("Cancelar");
        
        btnFinalizar.addActionListener(e -> finalizarPedido());
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotoes.add(btnFinalizar);
        panelBotoes.add(btnCancelar);
        panelInferior.add(panelBotoes, BorderLayout.SOUTH);
        
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void carregarProdutos() {
        List<Produto> produtos = produtoDAO.listarTodos();
        cbProdutos.removeAllItems();
        for (Produto p : produtos) {
            if (p.getQuantidadeEstoque() > 0) {
                cbProdutos.addItem(p);
            }
        }
    }
    
    private void adicionarItem() {
        Produto produto = (Produto) cbProdutos.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantidade = (int) spQuantidade.getValue();
        
        if (quantidade > produto.getQuantidadeEstoque()) {
            JOptionPane.showMessageDialog(this, 
                "Estoque insuficiente! Disponível: " + produto.getQuantidadeEstoque(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verificar se o item já está no carrinho
        boolean itemExistente = false;
        for (ItemPedido item : itensCarrinho) {
            if (item.getProdutoId() == produto.getId()) {
                int novaQuantidade = item.getQuantidade() + quantidade;
                if (novaQuantidade > produto.getQuantidadeEstoque()) {
                    JOptionPane.showMessageDialog(this, 
                        "Quantidade total excede o estoque!", 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                item.setQuantidade(novaQuantidade);
                itemExistente = true;
                break;
            }
        }
        
        if (!itemExistente) {
            ItemPedido item = new ItemPedido(
                produto.getId(),
                produto.getNome(),
                quantidade,
                produto.getPreco()
            );
            itensCarrinho.add(item);
        }
        
        atualizarTabela();
        calcularTotal();
        
        // Resetar quantidade
        spQuantidade.setValue(1);
    }
    
    private void atualizarTabela() {
        tableModel.setRowCount(0);
        for (ItemPedido item : itensCarrinho) {
            Object[] row = {
                item.getProdutoId(),
                item.getNomeProduto(),
                item.getQuantidade(),
                String.format("R$ %.2f", item.getPrecoUnitario()),
                String.format("R$ %.2f", item.getSubtotal())
            };
            tableModel.addRow(row);
        }
    }
    
    private void calcularTotal() {
        total = 0;
        for (ItemPedido item : itensCarrinho) {
            total += item.getSubtotal();
        }
        lblTotal.setText(String.format("R$ %.2f", total));
    }
    
    private void finalizarPedido() {
        if (itensCarrinho.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Adicione pelo menos um item ao pedido!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Confirmar pedido no valor de " + lblTotal.getText() + "?", 
            "Confirmar Pedido", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Pedido pedido = new Pedido();
            pedido.setUsuarioId(usuario.getId());
            pedido.setItens(itensCarrinho);
            pedido.setValorTotal(total);
            
            boolean sucesso = pedidoDAO.inserir(pedido);
            
            if (sucesso) {
                JOptionPane.showMessageDialog(this, 
                    "Pedido realizado com sucesso!", 
                    "Sucesso", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Erro ao realizar pedido!", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}