// model/Usuario.java
package pizzaria.model;

import java.sql.Timestamp;

public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String tipoUsuario;
    private boolean ativo;
    private Timestamp dataCadastro;
    
    public Usuario() {}
    
    public Usuario(int id, String nome, String email, String senha, String tipoUsuario, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.ativo = ativo;
    }
    
    // Getters e Setters
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getSenha() { 
        return senha; 
    }
    
    public void setSenha(String senha) { 
        this.senha = senha; 
    }
    
    public String getTipoUsuario() { 
        return tipoUsuario; 
    }
    
    public void setTipoUsuario(String tipoUsuario) { 
        this.tipoUsuario = tipoUsuario; 
    }
    
    public boolean isAtivo() { 
        return ativo; 
    }
    
    public void setAtivo(boolean ativo) { 
        this.ativo = ativo; 
    }
    
    public Timestamp getDataCadastro() { 
        return dataCadastro; 
    }
    
    public void setDataCadastro(Timestamp dataCadastro) { 
        this.dataCadastro = dataCadastro; 
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               ", tipoUsuario='" + tipoUsuario + '\'' +
               ", ativo=" + ativo +
               '}';
    }
}