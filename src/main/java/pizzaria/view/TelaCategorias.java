// view/TelaCategorias.java
package pizzaria.view;

import pizzaria.dao.CategoriaDAO;
import pizzaria.model.Categoria;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaCategorias extends JDialog {
    private JTextField txtNome;
    private JTextArea txtDescricao;
    private JTable tabelaCategorias;
    private DefaultTableModel tableModel;
    private CategoriaDAO categoriaDAO;
    private Categoria categoriaSelecionada;
    private JButton btnNovo, btnSalvar, btnCancelar, btnExcluir;
    
    public TelaCategorias(JFrame parent) {
        super(parent, "Gerenciar Categorias", true);
        categoriaDAO = new CategoriaDAO();
        initComponents();
        carregarTabela();
    }
    
    private void initComponents() {
        setSize(800, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        // Painel de formulário
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Dados da Categoria"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(new JLabel("Nome:"), gbc);
        
        txtNome = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
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
        gbc.gridwidth = 3;
        panelForm.add(scrollDesc, gbc);
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnNovo = new JButton("Novo");
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        btnExcluir = new JButton("Excluir");
        
        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarCategoria());
        btnCancelar.addActionListener(e -> limparFormulario());
        btnExcluir.addActionListener(e -> excluirCategoria());
        
        panelBotoes.add(btnNovo);
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnExcluir);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        panelForm.add(panelBotoes, gbc);
        
        add(panelForm, BorderLayout.NORTH);
        
        // Tabela de categorias
        String[] colunas = {"ID", "Nome", "Descrição", "Status"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaCategorias = new JTable(tableModel);
        tabelaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tabelaCategorias.getSelectedRow();
                if (row >= 0) {
                    carregarCategoriaParaFormulario(row);
                }
            }
        });
        
        JScrollPane scrollTabela = new JScrollPane(tabelaCategorias);
        scrollTabela.setBorder(BorderFactory.createTitledBorder("Lista de Categorias"));
        add(scrollTabela, BorderLayout.CENTER);
        
        btnExcluir.setEnabled(false);
    }
    
    private void carregarTabela() {
        tableModel.setRowCount(0);
        List<Categoria> categorias = categoriaDAO.listarTodos();
        
        for (Categoria c : categorias) {
            Object[] row = {
                c.getId(),
                c.getNome(),
                c.getDescricao(),
                c.isAtivo() ? "Ativo" : "Inativo"
            };
            tableModel.addRow(row);
        }
    }
    
    private void limparFormulario() {
        txtNome.setText("");
        txtDescricao.setText("");
        categoriaSelecionada = null;
        btnExcluir.setEnabled(false);
        txtNome.requestFocus();
    }
    
    private void carregarCategoriaParaFormulario(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String nome = (String) tableModel.getValueAt(row, 1);
        String descricao = (String) tableModel.getValueAt(row, 2);
        
        categoriaSelecionada = new Categoria();
        categoriaSelecionada.setId(id);
        categoriaSelecionada.setNome(nome);
        categoriaSelecionada.setDescricao(descricao);
        
        txtNome.setText(nome);
        txtDescricao.setText(descricao);
        btnExcluir.setEnabled(true);
    }
    
    private void salvarCategoria() {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();
        
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setDescricao(descricao);
        
        boolean sucesso;
        if (categoriaSelecionada == null) {
            sucesso = categoriaDAO.inserir(categoria);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Categoria cadastrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            categoria.setId(categoriaSelecionada.getId());
            sucesso = categoriaDAO.atualizar(categoria);
            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Categoria atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        if (sucesso) {
            carregarTabela();
            limparFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluirCategoria() {
        if (categoriaSelecionada != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Deseja realmente excluir a categoria " + categoriaSelecionada.getNome() + "?\n" +
                "Produtos vinculados a esta categoria também serão excluídos!", 
                "Confirmar exclusão", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean sucesso = categoriaDAO.deletar(categoriaSelecionada.getId());
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Categoria excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    carregarTabela();
                    limparFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao excluir categoria!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}