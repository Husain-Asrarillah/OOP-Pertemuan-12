/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PertemuanKeduabelas;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Husain
 */
@Entity
@Table(name = "pengarang")
@NamedQueries({
    @NamedQuery(name = "Pengarang.findAll", query = "SELECT p FROM Pengarang p ORDER BY p.idPengarang ASC"),
    @NamedQuery(name = "Pengarang.findByIdPengarang", query = "SELECT p FROM Pengarang p WHERE p.idPengarang = :idPengarang"),
    @NamedQuery(name = "Pengarang.findByNamaPengarang", query = "SELECT p FROM Pengarang p WHERE p.namaPengarang = :namaPengarang"),
    @NamedQuery(name = "Pengarang.findByEmail", query = "SELECT p FROM Pengarang p WHERE p.email = :email"),
    @NamedQuery(name = "Pengarang.findByAsalNegara", query = "SELECT p FROM Pengarang p WHERE p.asalNegara = :asalNegara")})
public class Pengarang implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pengarang")
    private Integer idPengarang;
    @Basic(optional = false)
    @Column(name = "nama_pengarang")
    private String namaPengarang;
    @Column(name = "email")
    private String email;
    @Column(name = "asal_negara")
    private String asalNegara;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPengarang")
    private Collection<Komik> komikCollection;

    public Pengarang() {
    }

    public Pengarang(Integer idPengarang) {
        this.idPengarang = idPengarang;
    }

    public Pengarang(Integer idPengarang, String namaPengarang) {
        this.idPengarang = idPengarang;
        this.namaPengarang = namaPengarang;
    }

    public Integer getIdPengarang() {
        return idPengarang;
    }

    public void setIdPengarang(Integer idPengarang) {
        this.idPengarang = idPengarang;
    }

    public String getNamaPengarang() {
        return namaPengarang;
    }

    public void setNamaPengarang(String namaPengarang) {
        this.namaPengarang = namaPengarang;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAsalNegara() {
        return asalNegara;
    }

    public void setAsalNegara(String asalNegara) {
        this.asalNegara = asalNegara;
    }

    public Collection<Komik> getKomikCollection() {
        return komikCollection;
    }

    public void setKomikCollection(Collection<Komik> komikCollection) {
        this.komikCollection = komikCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPengarang != null ? idPengarang.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pengarang)) {
            return false;
        }
        Pengarang other = (Pengarang) object;
        if ((this.idPengarang == null && other.idPengarang != null) || (this.idPengarang != null && !this.idPengarang.equals(other.idPengarang))) {
            return false;
        }
        return true;
    }

    /**
     * PERBAIKAN PENTING: Mengubah toString() agar ComboBox menampilkan Nama Pengarang.
     */
    @Override
    public String toString() {
        return namaPengarang; // Mengembalikan nama_pengarang sebagai representasi string
    }
    
}