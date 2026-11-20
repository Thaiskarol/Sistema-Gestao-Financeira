package com.bradesco.entity;

import java.time.LocalDateTime;

import com.bradesco.enums.NivelSaudeFinanceira;

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
@Table(name = "score_historico")
public class ScoreHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_score_historico")
    private Long id;

    @Column(nullable = false, length = 7)
    private String mes;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelSaudeFinanceira nivel;

    @Column(name = "calculado_em", nullable = false)
    private LocalDateTime calculadoEm;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Long getId() { return id; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public NivelSaudeFinanceira getNivel() { return nivel; }
    public void setNivel(NivelSaudeFinanceira nivel) { this.nivel = nivel; }

    public LocalDateTime getCalculadoEm() { return calculadoEm; }
    public void setCalculadoEm(LocalDateTime calculadoEm) { this.calculadoEm = calculadoEm; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
