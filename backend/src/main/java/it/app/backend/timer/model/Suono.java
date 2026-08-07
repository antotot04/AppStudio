package it.app.backend.timer.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="SUONO", schema="public")
public class Suono {

    @Id
    @Column(name="Id_Suono")
    private UUID Id_Suono = UUID.randomUUID();

    @Column(name="Tipo", length=10, nullable=false)
    private String Tipo;

    @Column(name="Nome", length=150, nullable=false)
    private String Nome;

    @Column(name="Contenuto", nullable=false)
    private byte[] Contenuto;

    @Column(name="MIME_Type", nullable=false)
    private String MIME_Type;

    public UUID getId_Suono() {
        return Id_Suono;
    }

    public String getTipo() {
        return Tipo;
    }

    public String getNome() {
        return Nome;
    }

    public byte[] getContenuto() {
        return Contenuto;
    }

    public String getMIME_Type() {
        return MIME_Type;
    }

    public void setTipo(String Tipo) {
        this.Tipo = Tipo;
    }

    public void setNome(String Nome) {
        this.Nome = Nome;
    }

    public void setContenuto(byte[] Contenuto) {
        this.Contenuto = Contenuto;
    }

    public void setMIME_Type(String MIME_Type) {
        this.MIME_Type = MIME_Type;
    }
    
}
