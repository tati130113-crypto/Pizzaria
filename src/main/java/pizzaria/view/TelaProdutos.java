// view/TelaProdutos.java
package pizzaria.view;

import pizzaria.dao.ProdutoDAO;
import pizzaria.dao.CategoriaDAO;
import pizzaria.model.Produto;
import pizzaria.model.Categoria;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaProdutos extends JDialog {
    private JTextField txtNome;
    private JTextField txtPreco;
    private JTextField txtEstoque;
    private JTextArea txtDescricao;
    private JComboBox<String> cbCategoria;
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private ProdutoDAO produtoDAO;
    private CategoriaDAO categoriaDAO;
    private int produtoSelecionadoId = -1;
    
    public TelaProdutos(JFrame parent) {
        super(parent, "Gerenciar Produtos", true);
        produtoDAO = new ProdutoDAO();
        categoriaDAO = new CategoriaDAO();
        initComponents();
        carregarCategorias();
        carregarTabela();
    }
    
    private void initComponents() {
        setSize(900, 650);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de formulário
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Nome:"), gbc);
        
        txtNome = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelForm.add(txtNome, gbc);
        
        // Descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelForm.add(new JLabel("Descrição:"), gbc);
        
        txtDescricao = new JTextArea(3, 30);
        txtDescricao.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelForm.add(scrollDesc, gbc);
        
        // Categoria
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panelForm.add(new JLabel("Categoria:"), gbc);
        
        cbCategoria = new JComboBox<>();
        cbCategoria.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        panelForm.add(cbCategoria, gbc);
        
        // Preço
        gbc.gridx = 0;
        gbc.gridy = 3;
        panelForm.add(new JLabel("Preço (R$):"), gbc);
        
        txtPreco = new JTextField(10);
        gbc.gridx = 1;
        panelForm.add(txtPreco, gbc);
        
        // Estoque
        gbc.gridx = 0;
        gbc.gridy = 4;
        panelForm.add(new JLabel("Estoque:"), gbc);
        
        txtEstoque = new JTextField(10);
        gbc.gridx = 1;
        panelForm.add(txtEstoque, gbc);
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarProduto());
        btnExcluir.addActionListener(e -> excluirProduto());
        
        panelBotoes.add(btnNovo);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnExcluir);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panelForm.add(panelBotoes, gbc);
        
        panelPrincipal.add(panelForm, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Nome", "Categoria", "Preço", "Estoque"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelaProdutos.getSelectedRow();
                if (row >= 0) {
                    carregarProdutoParaFormulario(row);
                }
            }
        });
        
        JScrollPane scrollTabela = new JScrollPane(tabelaProdutos);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Lista de Produtos"));
        panelPrincipal.add(scrollTabela, BorderLayout.CENTER);
        
        add(panelPrincipal);
    }
    
    private void carregarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.listarTodos();
            cbCategoria.removeAllItems();
            
            for (Categoria cat : categorias) {
                cbCategoria.addItem(cat.getId() + " - " + cat.getNome());
            }
            
            System.out.println("Categorias carregadas: " + categorias.size());
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar categorias: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar categorias!\n" + e.getMessage(),
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void carregarTabela() {
        try {
            tableModel.setRowCount(0);
            List<Produto> produtos = produtoDAO.listarTodos();
            
            for (Produto p : produtos) {
                Object[] row = {
                    p.getId(),
                    p.getNome(),
                    p.getNomeCategoria(),
                    String.format("R$ %.2f", p.getPreco()),
                    p.getQuantidadeEstoque()
                };
                tableModel.addRow(row);
            }
            
            System.out.println("Produtos carregados: " + produtos.size());
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
        }
    }
    
    private void limparFormulario() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
        if (cbCategoria.getItemCount() > 0) {
            cbCategoria.setSelectedIndex(0);
        }
        produtoSelecionadoId = -1;
        txtNome.requestFocus();
    }
    
    private void carregarProdutoParaFormulario(int row) {
        produtoSelecionadoId = (int) tableModel.getValueAt(row, 0);
        String nome = (String) tableModel.getValueAt(row, 1);
        String preco = (String) tableModel.getValueAt(row, 3);
        int estoque = (int) tableModel.getValueAt(row, 4);
        
        txtNome.setText(nome);
        txtPreco.setText(preco.replace("R$ ", "").replace(",", "."));
        txtEstoque.setText(String.valueOf(estoque));
        
        // Buscar descrição completa
        Produto p = produtoDAO.buscarPorId(produtoSelecionadoId);
        if (p != null) {
            txtDescricao.setText(p.getDescricao());
        }
    }
    
    private void salvarProduto() {
        try {
            String nome = txtNome.getText().trim();
            String descricao = txtDescricao.getText().trim();
            String precoStr = txtPreco.getText().trim();
            String estoqueStr = txtEstoque.getText().trim();
            
            // Validações
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do produto!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (precoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o preço do produto!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (cbCategoria.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double preco;
            try {
                precoStr = precoStr.replace(",", ".");
                preco = Double.parseDouble(precoStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Preço inválido! Use formato: 35.90", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int estoque = 0;
            if (!estoqueStr.isEmpty()) {
                try {
                    estoque = Integer.parseInt(estoqueStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Estoque inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Pegar ID da categoria
            String categoriaSelecionada = (String) cbCategoria.getSelectedItem();
            int categoriaId = Integer.parseInt(categoriaSelecionada.split(" - ")[0]);
            
            // Criar produto
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setCategoriaId(categoriaId);
            produto.setQuantidadeEstoque(estoque);
            
            boolean sucesso;
            if (produtoSelecionadoId == -1) {
                sucesso = produtoDAO.inserir(produto);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                produto.setId(produtoSelecionadoId);
                sucesso = produtoDAO.atualizar(produto);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (sucesso) {
                carregarTabela();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar produto!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto!\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirProduto() {
        if (produtoSelecionadoId != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja realmente excluir este produto?", 
                "Confirmar", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean sucesso = produtoDAO.deletar(produtoSelecionadoId);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarTabela();
                    limparFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir produto!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}