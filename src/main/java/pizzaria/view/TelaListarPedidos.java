package pizzaria.view;

import pizzaria.dao.PedidoDAO;
import pizzaria.model.Pedido;
import pizzaria.model.ItemPedido;
import pizzaria.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaListarPedidos extends JDialog {
    private JTable tabelaPedidos;
    private DefaultTableModel tableModel;
    private JTextArea txtDetalhes;
    private JComboBox<String> cbStatus;
    private JButton btnAtualizarStatus;
    private PedidoDAO pedidoDAO;
    private Usuario usuario;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    public TelaListarPedidos(JFrame parent, Usuario usuario) {
        super(parent, "Listar Pedidos", true);
        this.usuario = usuario;
        this.pedidoDAO = new PedidoDAO();
        initComponents();
        carregarPedidos();
    }
    
    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Painel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        panelFiltros.add(new JLabel("Status:"));
        String[] status = {"Todos", "em_andamento", "confirmado", "entregue", "cancelado"};
        cbStatus = new JComboBox<>(status);
        panelFiltros.add(cbStatus);
        
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> carregarPedidos());
        panelFiltros.add(btnFiltrar);
        
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarPedidos());
        panelFiltros.add(btnAtualizar);
        
        add(panelFiltros, BorderLayout.NORTH);
        
        // Tabela de pedidos
        String[] colunas = {"ID", "Data", "Cliente", "Status", "Total"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaPedidos = new JTable(tableModel);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPedidos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelaPedidos.getSelectedRow();
                if (row >= 0) {
                    int pedidoId = (int) tableModel.getValueAt(row, 0);
                    carregarDetalhesPedido(pedidoId);
                }
            }
        });
        
        JScrollPane scrollTabela = new JScrollPane(tabelaPedidos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Pedidos"));
        add(scrollTabela, BorderLayout.CENTER);
        
        // Painel de detalhes
        JPanel panelDetalhes = new JPanel(new BorderLayout());
        panelDetalhes.setBorder(BorderFactory.createTitledBorder("Detalhes do Pedido"));
        panelDetalhes.setPreferredSize(new Dimension(400, 0));
        
        txtDetalhes = new JTextArea();
        txtDetalhes.setEditable(false);
        txtDetalhes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollDetalhes = new JScrollPane(txtDetalhes);
        panelDetalhes.add(scrollDetalhes, BorderLayout.CENTER);
        
        // Botão de atualizar status (apenas para admin)
        if (usuario.getTipoUsuario().equals("admin")) {
            JPanel panelStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            panelStatus.add(new JLabel("Alterar Status:"));
            JComboBox<String> cbNovoStatus = new JComboBox<>(new String[]{"em_andamento", "confirmado", "entregue", "cancelado"});
            panelStatus.add(cbNovoStatus);
            
            btnAtualizarStatus = new JButton("Aplicar");
            btnAtualizarStatus.addActionListener(e -> {
                int row = tabelaPedidos.getSelectedRow();
                if (row >= 0) {
                    int pedidoId = (int) tableModel.getValueAt(row, 0);
                    String novoStatus = (String) cbNovoStatus.getSelectedItem();
                    atualizarStatusPedido(pedidoId, novoStatus);
                } else {
                    JOptionPane.showMessageDialog(TelaListarPedidos.this, "Selecione um pedido!", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            });
            panelStatus.add(btnAtualizarStatus);
            panelDetalhes.add(panelStatus, BorderLayout.SOUTH);
        }
        
        add(panelDetalhes, BorderLayout.EAST);
    }
    
    private void carregarPedidos() {
        tableModel.setRowCount(0);
        List<Pedido> pedidos = pedidoDAO.listarTodos();
        
        String statusFiltro = (String) cbStatus.getSelectedItem();
        
        for (Pedido p : pedidos) {
            // Aplicar filtro de status
            if (!statusFiltro.equals("Todos") && !p.getStatus().equals(statusFiltro)) {
                continue;
            }
            
            // Para clientes, mostrar apenas seus pedidos
            if (!usuario.getTipoUsuario().equals("admin") && p.getUsuarioId() != usuario.getId()) {
                continue;
            }
            
            Object[] row = {
                p.getId(),
                sdf.format(p.getDataPedido()),
                p.getNomeUsuario(),
                traduzirStatus(p.getStatus()),
                String.format("R$ %.2f", p.getValorTotal())
            };
            tableModel.addRow(row);
        }
    }
    
    private void carregarDetalhesPedido(int pedidoId) {
        List<Pedido> pedidos = pedidoDAO.listarTodos();
        Pedido pedido = null;
        
        for (Pedido p : pedidos) {
            if (p.getId() == pedidoId) {
                pedido = p;
                break;
            }
        }
        
        if (pedido != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("=== PEDIDO #").append(pedido.getId()).append(" ===\n");
            sb.append("Data: ").append(sdf.format(pedido.getDataPedido())).append("\n");
            sb.append("Cliente: ").append(pedido.getNomeUsuario()).append("\n");
            sb.append("Status: ").append(traduzirStatus(pedido.getStatus())).append("\n");
            sb.append("Observações: ").append(pedido.getObservacoes() != null ? pedido.getObservacoes() : "Nenhuma").append("\n");
            sb.append("\n--- ITENS ---\n");
            
            for (ItemPedido item : pedido.getItens()) {
                sb.append(String.format("%s\n   %d x R$ %.2f = R$ %.2f\n",
                    item.getNomeProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    item.getSubtotal()));
            }
            
            sb.append("\n--- TOTAL ---\n");
            sb.append(String.format("R$ %.2f\n", pedido.getValorTotal()));
            
            txtDetalhes.setText(sb.toString());
        }
    }
    
    private void atualizarStatusPedido(int pedidoId, String novoStatus) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja alterar o status do pedido #" + pedidoId + " para " + traduzirStatus(novoStatus) + "?", 
            "Confirmar alteração", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean sucesso = pedidoDAO.atualizarStatus(pedidoId, novoStatus);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Status atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarPedidos();
                txtDetalhes.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar status!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String traduzirStatus(String status) {
        switch (status) {
            case "em_andamento": return "Em Andamento";
            case "confirmado": return "Confirmado";
            case "entregue": return "Entregue";
            case "cancelado": return "Cancelado";
            default: return status;
        }
    }
}