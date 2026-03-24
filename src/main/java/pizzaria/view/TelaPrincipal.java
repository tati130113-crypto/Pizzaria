// view/TelaPrincipal.java
package pizzaria.view;

import pizzaria.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {
    private Usuario usuario;
    private JPanel panelConteudo;
    private JLabel lblStatus;
    
    public TelaPrincipal(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Pizzaria - Sistema de Gerenciamento");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Menu principal
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        
        JMenuItem menuProdutos = new JMenuItem("Produtos");
        menuProdutos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTelaProdutos();
            }
        });
        
        JMenuItem menuCategorias = new JMenuItem("Categorias");
        menuCategorias.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTelaCategorias();
            }
        });
        
        menuCadastros.add(menuProdutos);
        menuCadastros.add(menuCategorias);
        
        // Menu Pedidos
        JMenu menuPedidos = new JMenu("Pedidos");
        
        JMenuItem menuNovoPedido = new JMenuItem("Novo Pedido");
        menuNovoPedido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTelaPedidos();
            }
        });
        
        JMenuItem menuListarPedidos = new JMenuItem("Listar Pedidos");
        menuListarPedidos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTelaListarPedidos();
            }
        });
        
        menuPedidos.add(menuNovoPedido);
        menuPedidos.add(menuListarPedidos);
        
        // Menu Sair
        JMenu menuSair = new JMenu("Sistema");
        JMenuItem menuLogout = new JMenuItem("Sair");
        menuLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogout();
            }
        });
        menuSair.add(menuLogout);
        
        menuBar.add(menuCadastros);
        menuBar.add(menuPedidos);
        menuBar.add(menuSair);
        
        setJMenuBar(menuBar);
        
        // Painel de conteúdo
        panelConteudo = new JPanel(new BorderLayout());
        panelConteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de boas-vindas
        JPanel panelWelcome = new JPanel(new GridBagLayout());
        panelWelcome.setBackground(new Color(240, 240, 240));
        
        JLabel lblWelcome = new JLabel("Bem-vindo ao Sistema de Pizzaria");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblUser = new JLabel("Usuário: " + usuario.getNome() + " (" + usuario.getTipoUsuario() + ")");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panelWelcome.add(lblWelcome);
        panelWelcome.add(lblUser);
        
        panelConteudo.add(panelWelcome, BorderLayout.CENTER);
        
        // Barra de status
        lblStatus = new JLabel("Pronto");
        lblStatus.setBorder(BorderFactory.createEtchedBorder());
        add(lblStatus, BorderLayout.SOUTH);
        
        add(panelConteudo);
        
        // Se for admin, mostrar mais opções
        if (usuario.getTipoUsuario().equals("admin")) {
            lblStatus.setText("Logado como Administrador - Acesso total ao sistema");
        } else {
            lblStatus.setText("Logado como Cliente - Acesso limitado");
            // Desabilitar menus de cadastro para clientes
            menuCadastros.setEnabled(false);
        }
    }
    
    private void abrirTelaProdutos() {
        if (usuario.getTipoUsuario().equals("admin")) {
            TelaProdutos telaProdutos = new TelaProdutos(this);
            telaProdutos.setVisible(true);
        }
    }
    
    private void abrirTelaCategorias() {
        if (usuario.getTipoUsuario().equals("admin")) {
            TelaCategorias telaCategorias = new TelaCategorias(this);
            telaCategorias.setVisible(true);
        }
    }
    
    private void abrirTelaPedidos() {
        TelaPedidos telaPedidos = new TelaPedidos(this, usuario);
        telaPedidos.setVisible(true);
    }
    
    private void abrirTelaListarPedidos() {
        TelaListarPedidos telaListar = new TelaListarPedidos(this, usuario);
        telaListar.setVisible(true);
    }
    
    private void realizarLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente sair do sistema?", 
            "Confirmar saída", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
        }
    }
}