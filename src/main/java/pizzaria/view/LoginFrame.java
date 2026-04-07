package pizzaria.view;

import pizzaria.dao.UsuarioDAO;
import pizzaria.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JButton btnCancelar;

    private UsuarioDAO usuarioDAO;

    public LoginFrame() {

        usuarioDAO = new UsuarioDAO();

        setTitle("Sistema Pizzaria - Login");
        setSize(400,280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("LOGIN PIZZARIA");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        txtEmail = new JTextField();
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Senha:"), gbc);

        txtSenha = new JPasswordField();
        gbc.gridx = 1;
        panel.add(txtSenha, gbc);

        JPanel botoes = new JPanel();

        btnLogin = new JButton("Entrar");
        btnCancelar = new JButton("Cancelar");

        botoes.add(btnLogin);
        botoes.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(botoes, gbc);

        add(panel);

        btnLogin.addActionListener(e -> login());
        btnCancelar.addActionListener(e -> System.exit(0));

        getRootPane().setDefaultButton(btnLogin);
    }

    private void login() {

        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());

        if(email.isEmpty() || senha.isEmpty()){
            JOptionPane.showMessageDialog(this,"Preencha os campos");
            return;
        }

        boolean autenticado = usuarioDAO.autenticar(email, senha);

        if(autenticado){

            Usuario usuario = usuarioDAO.buscarPorEmail(email);

            JOptionPane.showMessageDialog(this,
                    "Bem vindo " + usuario.getNome());

            new TelaProdutos().setVisible(true);

            dispose();

        }else{

            JOptionPane.showMessageDialog(this,
                    "Email ou senha inválidos");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });

    }
}