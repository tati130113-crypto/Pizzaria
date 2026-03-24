// dao/UsuarioDAO.java
package pizzaria.dao;

import pizzaria.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    
    private Connection conexao;
    
    public UsuarioDAO() {
        this.conexao = ConexaoBD.getConexao();
    }
    
    /**
     * Método para autenticar usuário no sistema
     * @param email E-mail do usuário
     * @param senha Senha do usuário
     * @return true se autenticado, false caso contrário
     */
    public boolean autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = SHA2(?, 256) AND ativo = 1";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            System.out.println("🔐 Tentando autenticar: " + email);
            
            ResultSet rs = stmt.executeQuery();
            boolean autenticado = rs.next();
            
            if (autenticado) {
                System.out.println("✅ Autenticação bem-sucedida para: " + email);
            } else {
                System.out.println("❌ Falha na autenticação para: " + email);
            }
            
            rs.close();
            return autenticado;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao autenticar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca um usuário pelo e-mail
     * @param email E-mail do usuário
     * @return Objeto Usuario ou null se não encontrado
     */
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            System.out.println("🔍 Buscando usuário por email: " + email);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                usuario.setAtivo(rs.getBoolean("ativo"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                
                System.out.println("✅ Usuário encontrado: " + usuario.getNome());
                System.out.println("   ID: " + usuario.getId());
                System.out.println("   Tipo: " + usuario.getTipoUsuario());
                System.out.println("   Ativo: " + usuario.isAtivo());
                
                rs.close();
                return usuario;
            } else {
                System.out.println("❌ Nenhum usuário encontrado com email: " + email);
                rs.close();
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar usuário por email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Busca um usuário pelo ID
     * @param id ID do usuário
     * @return Objeto Usuario ou null se não encontrado
     */
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            System.out.println("🔍 Buscando usuário por ID: " + id);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                usuario.setAtivo(rs.getBoolean("ativo"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                
                System.out.println("✅ Usuário encontrado: " + usuario.getNome());
                rs.close();
                return usuario;
            } else {
                System.out.println("❌ Nenhum usuário encontrado com ID: " + id);
                rs.close();
                return null;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar usuário por ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Insere um novo usuário no banco de dados
     * @param usuario Objeto Usuario a ser inserido
     * @return true se inserido com sucesso, false caso contrário
     */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, tipo_usuario, ativo) VALUES (?, ?, SHA2(?, 256), ?, 1)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getTipoUsuario());
            
            System.out.println("📝 Inserindo novo usuário: " + usuario.getEmail());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    usuario.setId(id);
                    System.out.println("✅ Usuário inserido com sucesso! ID: " + id);
                }
                rs.close();
                return true;
            } else {
                System.out.println("❌ Falha ao inserir usuário!");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Atualiza os dados de um usuário existente
     * @param usuario Objeto Usuario com dados atualizados
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, tipo_usuario = ?, ativo = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTipoUsuario());
            stmt.setBoolean(4, usuario.isAtivo());
            stmt.setInt(5, usuario.getId());
            
            System.out.println("📝 Atualizando usuário ID: " + usuario.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Usuário atualizado com sucesso!");
                return true;
            } else {
                System.out.println("❌ Nenhum usuário encontrado com ID: " + usuario.getId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Atualiza a senha de um usuário
     * @param id ID do usuário
     * @param senhaAntiga Senha antiga para verificação
     * @param senhaNova Nova senha
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizarSenha(int id, String senhaAntiga, String senhaNova) {
        String sql = "UPDATE usuarios SET senha = SHA2(?, 256) WHERE id = ? AND senha = SHA2(?, 256)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, senhaNova);
            stmt.setInt(2, id);
            stmt.setString(3, senhaAntiga);
            
            System.out.println("📝 Atualizando senha do usuário ID: " + id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Senha atualizada com sucesso!");
                return true;
            } else {
                System.out.println("❌ Senha antiga incorreta ou usuário não encontrado!");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar senha: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Desativa um usuário (soft delete)
     * @param id ID do usuário
     * @return true se desativado com sucesso, false caso contrário
     */
    public boolean desativar(int id) {
        String sql = "UPDATE usuarios SET ativo = 0 WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            System.out.println("📝 Desativando usuário ID: " + id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Usuário desativado com sucesso!");
                return true;
            } else {
                System.out.println("❌ Usuário não encontrado!");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao desativar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ativa um usuário
     * @param id ID do usuário
     * @return true se ativado com sucesso, false caso contrário
     */
    public boolean ativar(int id) {
        String sql = "UPDATE usuarios SET ativo = 1 WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            System.out.println("📝 Ativando usuário ID: " + id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Usuário ativado com sucesso!");
                return true;
            } else {
                System.out.println("❌ Usuário não encontrado!");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao ativar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lista todos os usuários ativos
     * @return Lista de usuários
     */
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ativo = 1 ORDER BY nome";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("📋 Listando todos os usuários ativos");
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                usuario.setAtivo(rs.getBoolean("ativo"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                usuarios.add(usuario);
            }
            
            System.out.println("✅ Total de usuários encontrados: " + usuarios.size());
            return usuarios;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
            return usuarios;
        }
    }
    
    /**
     * Lista todos os usuários (incluindo inativos)
     * @return Lista de usuários
     */
    public List<Usuario> listarTodosIncluindoInativos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("📋 Listando TODOS os usuários (incluindo inativos)");
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                usuario.setAtivo(rs.getBoolean("ativo"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                usuarios.add(usuario);
            }
            
            System.out.println("✅ Total de usuários encontrados: " + usuarios.size());
            return usuarios;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
            return usuarios;
        }
    }
    
    /**
     * Verifica se um e-mail já está cadastrado
     * @param email E-mail a ser verificado
     * @return true se já existe, false caso contrário
     */
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                return count > 0;
            }
            
            rs.close();
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao verificar e-mail: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Conta o número total de usuários ativos
     * @return Número de usuários
     */
    public int contarUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE ativo = 1";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📊 Total de usuários ativos: " + count);
                return count;
            }
            
            return 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Erro ao contar usuários: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}