package com.bradesco.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.bradesco.enums.TipoTransacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "transacao")
public class Transacao {

  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column (name = "id_transacao")
  private Long id;

  @Column(nullable =  false, length = 350)
  private String descricao;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal valor;

  @Column (name = "data_transacao", nullable = false)
  private LocalDate dataTransacao;

  @Column (length = 50)
  private String recorrencia;

    @Column(length = 150)
  private String estabelecimento;

  @Column (nullable = false, length = 10)
  private TipoTransacao tipo;

  @ManyToOne 
  @JoinColumn (name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne 
  @JoinColumn (name = "categoria_id", nullable = false)
  private Categoria categoria;

  public Long getId() {
    return id;
  }

  public String getDescricao(){
    return descricao;
  }

  public BigDecimal getValor(){
    return valor;
  }

  public LocalDate getDataTransacao(){
    return dataTransacao;
  }

  public String getRecorrencia(){
    return recorrencia;
  }

    public String getEstabelecimento(){
    return estabelecimento;
  }

  public TipoTransacao getTipo(){
    return tipo;
  }

  public Usuario getUsuario(){
    return usuario;
  }

  public Categoria getCategoria(){
    return categoria;
  }

  public void setDescricao(String descricao){
    this.descricao = descricao;
  }

  public void setValor(BigDecimal valor){
    this.valor = valor;
  }

  public void setDataTransacao(LocalDate dataTransacao){
    this.dataTransacao = dataTransacao;
  }

  public void setRecorrencia(String recorrencia){
    this.recorrencia = recorrencia;
  }

  public void setEstabelecimento(String estabelecimento){
    this.estabelecimento = estabelecimento;
  }

  public void setTipo(TipoTransacao tipo){
    this.tipo = tipo;
  }

  public void setUsuario(Usuario usuario){
    this.usuario = usuario;
  }

  public void setCategoria(Categoria categoria){
    this.categoria = categoria;
  }
}
