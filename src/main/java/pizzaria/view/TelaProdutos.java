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

public class TelaProdutos extends JFrame {

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

    public TelaProdutos() {

        setTitle("Cadastro de Produtos");
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        produtoDAO = new ProdutoDAO();
        categoriaDAO = new CategoriaDAO();

        initComponents();
        carregarCategorias();
        carregarTabela();
    }

    private void initComponents() {

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Nome:"), gbc);

        txtNome = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Descrição:"), gbc);

        txtDescricao = new JTextArea(3,20);
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);

        gbc.gridx = 1;
        form.add(scrollDesc, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Categoria:"), gbc);

        cbCategoria = new JComboBox<>();
        gbc.gridx = 1;
        form.add(cbCategoria, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Preço:"), gbc);

        txtPreco = new JTextField();
        gbc.gridx = 1;
        form.add(txtPreco, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(new JLabel("Estoque:"), gbc);

        txtEstoque = new JTextField();
        gbc.gridx = 1;
        form.add(txtEstoque, gbc);

        JPanel botoes = new JPanel();

        JButton btnNovo = new JButton("Novo");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");

        botoes.add(btnNovo);
        botoes.add(btnSalvar);
        botoes.add(btnExcluir);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        form.add(botoes, gbc);

        panel.add(form, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID","Nome","Categoria","Preço","Estoque"},0){

            public boolean isCellEditable(int row,int column){
                return false;
            }
        };

        tabelaProdutos = new JTable(tableModel);

        tabelaProdutos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabelaProdutos.getSelectedRow();
                if(row >= 0){
                    carregarFormulario(row);
                }
            }
        });

        panel.add(new JScrollPane(tabelaProdutos), BorderLayout.CENTER);

        add(panel);

        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
    }

    private void carregarCategorias() {

        cbCategoria.removeAllItems();

        List<Categoria> lista = categoriaDAO.listarTodos();

        for(Categoria c : lista){
            cbCategoria.addItem(c.getId()+" - "+c.getNome());
        }
    }

    private void carregarTabela() {

        tableModel.setRowCount(0);

        List<Produto> lista = produtoDAO.listarTodos();

        for(Produto p : lista){

            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getNomeCategoria(),
                    p.getPreco(),
                    p.getQuantidadeEstoque()
            });
        }
    }

    private void limpar() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtEstoque.setText("");
        produtoSelecionadoId = -1;
    }

    private void carregarFormulario(int row) {

        produtoSelecionadoId =
                (int) tableModel.getValueAt(row,0);

        txtNome.setText(
                tableModel.getValueAt(row,1).toString());

        txtPreco.setText(
                tableModel.getValueAt(row,3).toString());

        txtEstoque.setText(
                tableModel.getValueAt(row,4).toString());
    }

    private void salvar() {

        try{

            Produto p = new Produto();

            p.setNome(txtNome.getText());
            p.setDescricao(txtDescricao.getText());

            p.setPreco(
                    Double.parseDouble(
                            txtPreco.getText().replace(",",".")
                    )
            );

            p.setQuantidadeEstoque(
                    Integer.parseInt(txtEstoque.getText())
            );

            String categoria =
                    cbCategoria.getSelectedItem().toString();

            int categoriaId =
                    Integer.parseInt(categoria.split(" - ")[0]);

            p.setCategoriaId(categoriaId);

            boolean sucesso;

            if(produtoSelecionadoId == -1){
                sucesso = produtoDAO.inserir(p);
            }else{
                p.setId(produtoSelecionadoId);
                sucesso = produtoDAO.atualizar(p);
            }

            if(sucesso){
                JOptionPane.showMessageDialog(this,"Salvo com sucesso");
                carregarTabela();
                limpar();
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Erro: "+e.getMessage());
        }
    }

    private void excluir() {

        if(produtoSelecionadoId != -1){

            produtoDAO.deletar(produtoSelecionadoId);

            carregarTabela();
            limpar();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new TelaProdutos().setVisible(true);
        });

    }
}