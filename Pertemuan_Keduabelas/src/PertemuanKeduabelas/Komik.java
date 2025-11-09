/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PertemuanKeduabelas;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Husain
 */
@Entity
@Table(name = "komik")
@NamedQueries({
    @NamedQuery(name = "Komik.findAll", query = "SELECT k FROM Komik k ORDER BY k.idKomik ASC"),
    @NamedQuery(name = "Komik.findByIdKomik", query = "SELECT k FROM Komik k WHERE k.idKomik = :idKomik"),
    @NamedQuery(name = "Komik.findByJudul", query = "SELECT k FROM Komik k WHERE k.judul = :judul"),
    @NamedQuery(name = "Komik.findByTahunTerbit", query = "SELECT k FROM Komik k WHERE k.tahunTerbit = :tahunTerbit"),
    @NamedQuery(name = "Komik.findByGenre", query = "SELECT k FROM Komik k WHERE k.genre = :genre")})
public class Komik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_komik")
    private Integer idKomik;
    @Basic(optional = false)
    @Column(name = "judul")
    private String judul;
    @Column(name = "tahun_terbit")
    private Integer tahunTerbit;
    @Column(name = "genre")
    private String genre;
    @JoinColumn(name = "id_pengarang", referencedColumnName = "id_pengarang")
    @ManyToOne(optional = false)
    private Pengarang idPengarang;

    public Komik() {
    }

    public Komik(Integer idKomik) {
        this.idKomik = idKomik;
    }

    public Komik(Integer idKomik, String judul) {
        this.idKomik = idKomik;
        this.judul = judul;
    }

    public Integer getIdKomik() {
        return idKomik;
    }

    public void setIdKomik(Integer idKomik) {
        this.idKomik = idKomik;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public Integer getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(Integer tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Pengarang getIdPengarang() {
        return idPengarang;
    }

    public void setIdPengarang(Pengarang idPengarang) {
        this.idPengarang = idPengarang;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idKomik != null ? idKomik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Komik)) {
            return false;
        }
        Komik other = (Komik) object;
        if ((this.idKomik == null && other.idKomik != null) || (this.idKomik != null && !this.idKomik.equals(other.idKomik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pertemuan_keduabelas.Komik[ idKomik=" + idKomik + " ]";
    }
    
}
