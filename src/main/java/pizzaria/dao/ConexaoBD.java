// dao/ConexaoBD.java
package pizzaria.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private static final String URL = "jdbc:mysql://localhost:3306/pizzaria_db?useSSL=false&serverTimezone=UTC&autoReconnect=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "M@ia2505";
    
    private static Connection conexao = null;
    
    public static synchronized Connection getConexao() {
        try {
            // Se a conexão for nula ou estiver fechada, cria uma nova
            if (conexao == null || conexao.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                conexao.setAutoCommit(true);
                System.out.println("✅ Nova conexão estabelecida com sucesso!");
            } else {
                System.out.println("✅ Reutilizando conexão existente!");
            }
            return conexao;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erro ao conectar ao banco: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public static void fecharConexao() {
        if (conexao != null) {
            try {
                if (!conexao.isClosed()) {
                    conexao.close();
                    System.out.println("🔌 Conexão fechada!");
                }
            } catch (SQLException e) {
                System.err.println("❌ Erro ao fechar conexão: " + e.getMessage());
            } finally {
                conexao = null;
            }
        }
    }
}