// view/LoginFrame.java
package pizzaria.view;

import pizzaria.dao.UsuarioDAO;
import pizzaria.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import pizzaria.dao.ConexaoBD;

public class LoginFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnLogin, btnCancelar;
    private UsuarioDAO usuarioDAO;
    private JLabel lblStatus;
    
    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();
        initComponents();
        configurarAtalhos();
        testarConexaoInicial();
    }
    
    private void initComponents() {
        setTitle("Pizzaria - Sistema de Login");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Painel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Logo/Ícone
        JLabel lblIcone = new JLabel("🍕", SwingConstants.CENTER);
        lblIcone.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblIcone, gbc);
        
        // Título
        JLabel lblTitulo = new JLabel("Sistema de Pizzaria", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(200, 50, 50));
        gbc.gridy = 1;
        panel.add(lblTitulo, gbc);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Faça login para continuar", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panel.add(lblSubtitulo, gbc);
        
        // Espaço
        gbc.gridy = 3;
        panel.add(Box.createVerticalStrut(15), gbc);
        
        // Email
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel lblEmail = new JLabel("E-mail:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblEmail, gbc);
        
        txtEmail = new JTextField(25);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);
        
        // Senha
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lblSenha, gbc);
        
        txtSenha = new JPasswordField(25);
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSenha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        panel.add(txtSenha, gbc);
        
        // Espaço
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(Box.createVerticalStrut(10), gbc);
        
        // Status
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblStatus.setForeground(Color.RED);
        gbc.gridy = 7;
        panel.add(lblStatus, gbc);
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotoes.setOpaque(false);
        
        btnLogin = new JButton("Entrar");
        btnLogin.setBackground(new Color(50, 150, 50));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(100, 35));
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(150, 150, 150));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        panelBotoes.add(btnLogin);
        panelBotoes.add(btnCancelar);
        
        gbc.gridy = 8;
        panel.add(panelBotoes, gbc);
        
        // Rodapé com credenciais
        JPanel panelRodape = new JPanel(new GridLayout(2, 1));
        panelRodape.setOpaque(false);
        
        JLabel lblCredAdmin = new JLabel("Admin: admin@pizzaria.com / admin123", SwingConstants.CENTER);
        lblCredAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblCredAdmin.setForeground(new Color(100, 100, 100));
        
        JLabel lblCredCliente = new JLabel("Cliente: cliente@email.com / cliente123", SwingConstants.CENTER);
        lblCredCliente.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblCredCliente.setForeground(new Color(100, 100, 100));
        
        panelRodape.add(lblCredAdmin);
        panelRodape.add(lblCredCliente);
        
        gbc.gridy = 9;
        panel.add(panelRodape, gbc);
        
        add(panel);
        
        // Atalho Enter para login
        getRootPane().setDefaultButton(btnLogin);
        
        // Preencher campos padrão para teste (opcional)
        txtEmail.setText("admin@pizzaria.com");
        txtSenha.setText("admin123");
    }
    
    private void configurarAtalhos() {
        // Atalho ESC para limpar campos
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    limparCampos();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        };
        
        txtEmail.addKeyListener(keyAdapter);
        txtSenha.addKeyListener(keyAdapter);
    }
    
    private void testarConexaoInicial() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Connection conn = ConexaoBD.getConexao();
                    if (conn != null && !conn.isClosed()) {
                        System.out.println("✅ Conexão com banco de dados estabelecida!");
                        
                        // Verificar se há usuários no banco
                        String sql = "SELECT COUNT(*) FROM usuarios";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            if (count == 0) {
                                System.out.println("⚠️ Nenhum usuário encontrado no banco!");
                                lblStatus.setText("⚠️ Nenhum usuário cadastrado. Use as credenciais padrão.");
                            } else {
                                System.out.println("✅ " + count + " usuário(s) encontrado(s) no banco.");
                            }
                        }
                        rs.close();
                        stmt.close();
                    } else {
                        System.out.println("❌ Falha na conexão com o banco de dados!");
                        lblStatus.setText("❌ Erro de conexão com o banco de dados!");
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Erro ao testar conexão: " + e.getMessage());
                    lblStatus.setText("❌ Erro de conexão: Verifique se o MySQL está rodando!");
                    e.printStackTrace();
                }
                return null;
            }
        };
        worker.execute();
    }
    
    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        // Validação dos campos
        if (email.isEmpty()) {
            mostrarErro("Por favor, informe seu e-mail!");
            txtEmail.requestFocus();
            return;
        }
        
        if (senha.isEmpty()) {
            mostrarErro("Por favor, informe sua senha!");
            txtSenha.requestFocus();
            return;
        }
        
        // Desabilitar botão e mostrar loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Entrando...");
        lblStatus.setText("🔄 Autenticando...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        // Executar login em thread separada
        SwingWorker<Usuario, Void> worker = new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() throws Exception {
                System.out.println("🔐 Tentando login para: " + email);
                
                // Autenticar usuário
                boolean autenticado = usuarioDAO.autenticar(email, senha);
                
                if (autenticado) {
                    System.out.println("✅ Autenticação bem-sucedida!");
                    return usuarioDAO.buscarPorEmail(email);
                } else {
                    System.out.println("❌ Autenticação falhou!");
                    return null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    
                    if (usuario != null) {
                        System.out.println("✅ Login realizado com sucesso!");
                        System.out.println("   Usuário: " + usuario.getNome());
                        System.out.println("   Tipo: " + usuario.getTipoUsuario());
                        
                        mostrarSucesso("Bem-vindo(a), " + usuario.getNome() + "!");
                        
                        // Abrir tela principal
                        TelaPrincipal telaPrincipal = new TelaPrincipal(usuario);
                        telaPrincipal.setVisible(true);
                        dispose(); // Fecha a tela de login
                    } else {
                        System.out.println("❌ Login falhou!");
                        mostrarErro("E-mail ou senha inválidos!\n\n" +
                                   "Credenciais padrão:\n" +
                                   "📧 Admin: admin@pizzaria.com\n" +
                                   "🔑 Senha: admin123\n\n" +
                                   "📧 Cliente: cliente@email.com\n" +
                                   "🔑 Senha: cliente123\n\n" +
                                   "Verifique se o banco de dados está rodando.");
                        txtSenha.setText("");
                        txtSenha.requestFocus();
                        lblStatus.setText("❌ E-mail ou senha inválidos!");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erro durante o login: " + e.getMessage());
                    e.printStackTrace();
                    mostrarErro("Erro ao conectar com o banco de dados!\n\n" +
                               "Verifique se:\n" +
                               "1. O MySQL está rodando\n" +
                               "2. O banco 'pizzaria_db' foi criado\n" +
                               "3. As tabelas foram criadas\n\n" +
                               "Erro: " + e.getMessage());
                    lblStatus.setText("❌ Erro de conexão com o banco!");
                } finally {
                    // Reabilitar botão
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Entrar");
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        worker.execute();
    }
    
    private void limparCampos() {
        txtEmail.setText("");
        txtSenha.setText("");
        lblStatus.setText(" ");
        txtEmail.requestFocus();
    }
    
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, 
            mensagem, 
            "Erro de Login", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, 
            mensagem, 
            "Login Realizado", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Iniciar aplicação
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}