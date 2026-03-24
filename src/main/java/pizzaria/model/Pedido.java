// model/Pedido.java
package pizzaria.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private int usuarioId;
    private String nomeUsuario;
    private Timestamp dataPedido;
    private String status;
    private double valorTotal;
    private String observacoes;
    private List<ItemPedido> itens;
    
    // Construtores
    public Pedido() {
        this.itens = new ArrayList<>();
        this.status = "em_andamento";
        this.valorTotal = 0;
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    
    public Timestamp getDataPedido() { return dataPedido; }
    public void setDataPedido(Timestamp dataPedido) { this.dataPedido = dataPedido; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }
    
    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
        this.valorTotal += item.getSubtotal();
    }
}