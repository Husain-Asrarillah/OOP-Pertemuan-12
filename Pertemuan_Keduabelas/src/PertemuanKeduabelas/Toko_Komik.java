/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package PertemuanKeduabelas;

import java.awt.HeadlessException;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.*;
import javax.swing.JFileChooser;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author Husain
 */
public class Toko_Komik extends javax.swing.JFrame {

    static final String URL = "jdbc:postgresql://localhost:5432/Toko_Komik";
    static final String USER = "postgres"; // ganti sesuai user PostgreSQL
    static final String PASSWORD = "170206"; // ganti sesuai password PostgreSQL

    /**
     * Creates new form Toko_Komik
     */
    public Toko_Komik() {
        initComponents();
        tampilDataKomik();
        tampilDataPengarang();
        loadPengarangToComboBox(cbPengarang);
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Koneksi ke PostgreSQL berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }

    private void tampilkanDataKomikUntukEdit(Integer id) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();
            Komik data = em.find(Komik.class, id);

            if (data != null) {
                // >>> 1. MUAT DULU COMBOBOX UPDATE <<<
                loadPengarangToComboBox(cbPengarang1); // Panggil load untuk mengisi cbPengarang1

                // 2. Isi Field Input di Dialog Edit Anda
                tId.setText(data.getIdKomik().toString());
                tId.setEditable(false);

                tJudul3.setText(data.getJudul());
                tTahunTerbit1.setText(data.getTahunTerbit().toString());
                tGenre1.setText(data.getGenre());

                Pengarang pengarangLama = data.getIdPengarang();

                // 3. Set item yang terpilih (sekarang cbPengarang1 sudah terisi)
                if (pengarangLama != null) {
                    cbPengarang1.setSelectedItem(pengarangLama);
                } else {
                    cbPengarang1.setSelectedIndex(0);
                }

                // 4. Tampilkan Dialog Edit
                UpdateDialog.pack();
                UpdateDialog.setLocationRelativeTo(this);
                UpdateDialog.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }

    private void tampilkanDataPengarangUntukEdit(Integer id) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            Pengarang data = em.find(Pengarang.class, id);

            if (data != null) {
                // ID (Sudah Benar - tId)
                tIdP.setText(data.getIdPengarang().toString());
                tIdP.setEditable(false); // ID TIDAK BISA DIEDIT (Sudah Benar)

                // PERBAIKAN: Pastikan ini adalah nama field yang BENAR di UpdateDialogP
                tNama1.setText(data.getNamaPengarang());
                tEmail1.setText(data.getEmail());
                tAsal1.setText(data.getAsalNegara());

                // Tampilkan Dialog Edit
                UpdateDialogP.pack(); // Menggunakan Dialog P yang benar
                UpdateDialogP.setLocationRelativeTo(this);
                UpdateDialogP.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Data Pengarang tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data Pengarang: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }

    private void tampilDataKomik() {
        EntityManager em = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU").createEntityManager();

        try {
            List<Komik> hasil = em.createNamedQuery("Komik.findAll", Komik.class).getResultList();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID Komik", "Judul", "Tahun Terbit", "Genre", "Nama Pengarang"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Komik data : hasil) {
                Object[] baris = new Object[5];
                baris[0] = data.getIdKomik();
                baris[1] = data.getJudul();
                baris[2] = data.getTahunTerbit();
                baris[3] = data.getGenre();
                // Asumsi getter relasi: data.getIdPengarang().getNamaPengarang()
                baris[4] = data.getIdPengarang() != null ? data.getIdPengarang().getNamaPengarang() : "-";
                model.addRow(baris);
            }

            jTableKomik.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data Komik: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void loadPengarangToComboBox(javax.swing.JComboBox<Pengarang> comboBoxTarget) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            List<Pengarang> daftarPengarang = em.createNamedQuery("Pengarang.findAll", Pengarang.class).getResultList();

            // Menggunakan COMBOBOX TARGET
            comboBoxTarget.removeAllItems();
            comboBoxTarget.addItem(null);

            for (Pengarang p : daftarPengarang) {
                comboBoxTarget.addItem(p);
            }

        } catch (Exception e) {
            System.err.println("Gagal memuat data Pengarang: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }

    // ---
    /**
     * Metode untuk menampilkan data Pengarang dari database ke jTablePengarang
     */
    private void tampilDataPengarang() {
        EntityManager em = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU").createEntityManager();

        try {
            List<Pengarang> hasil = em.createNamedQuery("Pengarang.findAll", Pengarang.class).getResultList();

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID Pengarang", "Nama Pengarang", "Email", "Asal Negara"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Pengarang data : hasil) {
                Object[] baris = new Object[4];
                baris[0] = data.getIdPengarang();
                baris[1] = data.getNamaPengarang();
                baris[2] = data.getEmail();
                baris[3] = data.getAsalNegara();
                model.addRow(baris);
            }

            jTablePengarang.setModel(model);
            jTablePengarang.revalidate();
            jTablePengarang.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan data Pengarang: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void hapusDataKomik(Integer id) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            // 1. Cari objek yang akan dihapus
            Komik data = em.find(Komik.class, id);

            if (data == null) {
                JOptionPane.showMessageDialog(this, "Data Komik tidak ditemukan di database.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Eksekusi Hapus (Remove)
            em.getTransaction().begin();
            em.remove(data); // Hapus objek yang terkelola
            em.getTransaction().commit();

            // 3. Selesaikan dan Refresh
            JOptionPane.showMessageDialog(this, "Data Komik dengan ID " + id + " berhasil dihapus!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            tampilDataKomik(); // Refresh tabel setelah penghapusan

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }

    private void hapusDataPengarang(Integer id) {
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            // 1. Cari objek yang akan dihapus
            Pengarang data = em.find(Pengarang.class, id);

            if (data == null) {
                JOptionPane.showMessageDialog(this, "Data Pengarang tidak ditemukan di database.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Eksekusi Hapus (Remove)
            em.getTransaction().begin();
            em.remove(data); // Hapus objek yang terkelola
            em.getTransaction().commit();

            // 3. Selesaikan dan Refresh
            JOptionPane.showMessageDialog(this, "Data Pengarang dengan ID " + id + " berhasil dihapus!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Refresh kedua tabel (Komik dan Pengarang) dan ComboBox Komik
            tampilDataPengarang();
            tampilDataKomik();
            loadPengarangToComboBox(cbPengarang);   // Refresh ComboBox Insert
            loadPengarangToComboBox(cbPengarang1);  // Refresh ComboBox Update

        } catch (javax.persistence.PersistenceException pe) {
            // Menangkap error jika Pengarang masih terkait dengan Komik (Foreign Key Constraint)
            JOptionPane.showMessageDialog(this,
                    "Gagal menghapus Pengarang. Data ini masih terkait dengan data Komik!",
                    "FK Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }

    // ... (metode lain, seperti constructor, main, dll.) ...
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        InsertDialog = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jSimpan = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tJudul = new javax.swing.JTextField();
        tTahunTerbit = new javax.swing.JTextField();
        tGenre = new javax.swing.JTextField();
        cbPengarang = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        InsertDialogP = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jSimpan1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tNama = new javax.swing.JTextField();
        tEmail = new javax.swing.JTextField();
        tAsal = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        UpdateDialog = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jSimpan2 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tId = new javax.swing.JTextField();
        tTahunTerbit1 = new javax.swing.JTextField();
        tGenre1 = new javax.swing.JTextField();
        cbPengarang1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        tJudul3 = new javax.swing.JTextField();
        UpdateDialogP = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jSimpan4 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        tNama1 = new javax.swing.JTextField();
        tEmail1 = new javax.swing.JTextField();
        tAsal1 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tIdP = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTableKomik = new javax.swing.JTable();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTablePengarang = new javax.swing.JTable();

        jPanel4.setBackground(new java.awt.Color(0, 51, 51));

        jSimpan.setBackground(new java.awt.Color(0, 102, 102));
        jSimpan.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jSimpan.setForeground(new java.awt.Color(0, 204, 204));
        jSimpan.setText("SIMPAN");
        jSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSimpanActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(0, 102, 102));
        jLabel3.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 204, 204));
        jLabel3.setText("JUDUL");

        jLabel4.setBackground(new java.awt.Color(0, 102, 102));
        jLabel4.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 204, 204));
        jLabel4.setText("TAHUN ");

        jLabel5.setBackground(new java.awt.Color(0, 102, 102));
        jLabel5.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 204, 204));
        jLabel5.setText("PENGARANG");

        jLabel6.setBackground(new java.awt.Color(0, 102, 102));
        jLabel6.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 204, 204));
        jLabel6.setText("GENRE");

        tJudul.setBackground(new java.awt.Color(0, 102, 102));
        tJudul.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tJudul.setForeground(new java.awt.Color(0, 204, 204));
        tJudul.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tJudulActionPerformed(evt);
            }
        });

        tTahunTerbit.setBackground(new java.awt.Color(0, 102, 102));
        tTahunTerbit.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tTahunTerbit.setForeground(new java.awt.Color(0, 204, 204));

        tGenre.setBackground(new java.awt.Color(0, 102, 102));
        tGenre.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tGenre.setForeground(new java.awt.Color(0, 204, 204));
        tGenre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tGenreActionPerformed(evt);
            }
        });

        cbPengarang.setBackground(new java.awt.Color(0, 102, 102));
        cbPengarang.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        cbPengarang.setForeground(new java.awt.Color(0, 204, 204));
        cbPengarang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbPengarangItemStateChanged(evt);
            }
        });
        cbPengarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPengarangActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 153, 153));
        jLabel2.setText("INSERT DATA PENGARANG");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSimpan)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tGenre)
                            .addComponent(tTahunTerbit)
                            .addComponent(tJudul)
                            .addComponent(cbPengarang, 0, 199, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(80, 80, 80))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tTahunTerbit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tGenre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(cbPengarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout InsertDialogLayout = new javax.swing.GroupLayout(InsertDialog.getContentPane());
        InsertDialog.getContentPane().setLayout(InsertDialogLayout);
        InsertDialogLayout.setHorizontalGroup(
            InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        InsertDialogLayout.setVerticalGroup(
            InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(0, 51, 51));

        jSimpan1.setBackground(new java.awt.Color(0, 102, 102));
        jSimpan1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jSimpan1.setForeground(new java.awt.Color(0, 204, 204));
        jSimpan1.setText("SIMPAN");
        jSimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSimpan1ActionPerformed(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(0, 102, 102));
        jLabel7.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 204, 204));
        jLabel7.setText("NAMA");

        jLabel8.setBackground(new java.awt.Color(0, 102, 102));
        jLabel8.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 204, 204));
        jLabel8.setText("EMAIL");

        jLabel10.setBackground(new java.awt.Color(0, 102, 102));
        jLabel10.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 204, 204));
        jLabel10.setText("ASAL");

        tNama.setBackground(new java.awt.Color(0, 102, 102));
        tNama.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tNama.setForeground(new java.awt.Color(0, 204, 204));
        tNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNamaActionPerformed(evt);
            }
        });

        tEmail.setBackground(new java.awt.Color(0, 102, 102));
        tEmail.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tEmail.setForeground(new java.awt.Color(0, 204, 204));

        tAsal.setBackground(new java.awt.Color(0, 102, 102));
        tAsal.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tAsal.setForeground(new java.awt.Color(0, 204, 204));
        tAsal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tAsalActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 153, 153));
        jLabel11.setText("INSERT DATA PENGARANG");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addComponent(jLabel11))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSimpan1)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tAsal, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                                    .addComponent(tEmail)
                                    .addComponent(tNama))))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel11)
                .addGap(35, 35, 35)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(tAsal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSimpan1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout InsertDialogPLayout = new javax.swing.GroupLayout(InsertDialogP.getContentPane());
        InsertDialogP.getContentPane().setLayout(InsertDialogPLayout);
        InsertDialogPLayout.setHorizontalGroup(
            InsertDialogPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        InsertDialogPLayout.setVerticalGroup(
            InsertDialogPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel6.setBackground(new java.awt.Color(0, 51, 51));

        jSimpan2.setBackground(new java.awt.Color(0, 102, 102));
        jSimpan2.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jSimpan2.setForeground(new java.awt.Color(0, 204, 204));
        jSimpan2.setText("SIMPAN");
        jSimpan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSimpan2ActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(0, 102, 102));
        jLabel9.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 204, 204));
        jLabel9.setText("ID");

        jLabel12.setBackground(new java.awt.Color(0, 102, 102));
        jLabel12.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 204, 204));
        jLabel12.setText("TAHUN ");

        jLabel13.setBackground(new java.awt.Color(0, 102, 102));
        jLabel13.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 204, 204));
        jLabel13.setText("PENGARANG");

        jLabel14.setBackground(new java.awt.Color(0, 102, 102));
        jLabel14.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 204, 204));
        jLabel14.setText("GENRE");

        tId.setBackground(new java.awt.Color(204, 204, 204));
        tId.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tId.setForeground(new java.awt.Color(0, 204, 204));
        tId.setEnabled(false);
        tId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tIdActionPerformed(evt);
            }
        });

        tTahunTerbit1.setBackground(new java.awt.Color(0, 102, 102));
        tTahunTerbit1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tTahunTerbit1.setForeground(new java.awt.Color(0, 204, 204));

        tGenre1.setBackground(new java.awt.Color(0, 102, 102));
        tGenre1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tGenre1.setForeground(new java.awt.Color(0, 204, 204));
        tGenre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tGenre1ActionPerformed(evt);
            }
        });

        cbPengarang1.setBackground(new java.awt.Color(0, 102, 102));
        cbPengarang1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        cbPengarang1.setForeground(new java.awt.Color(0, 204, 204));
        cbPengarang1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbPengarang1ItemStateChanged(evt);
            }
        });
        cbPengarang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPengarang1ActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 153, 153));
        jLabel15.setText("INSERT DATA PENGARANG");

        jLabel21.setBackground(new java.awt.Color(0, 102, 102));
        jLabel21.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 204, 204));
        jLabel21.setText("JUDUL");

        tJudul3.setBackground(new java.awt.Color(0, 102, 102));
        tJudul3.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tJudul3.setForeground(new java.awt.Color(0, 204, 204));
        tJudul3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tJudul3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSimpan2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tGenre1)
                            .addComponent(tTahunTerbit1)
                            .addComponent(tId)
                            .addComponent(cbPengarang1, 0, 199, Short.MAX_VALUE)
                            .addComponent(tJudul3))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(80, 80, 80))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel15)
                .addGap(32, 32, 32)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tJudul3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(tTahunTerbit1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(tGenre1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(cbPengarang1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(jSimpan2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UpdateDialogLayout = new javax.swing.GroupLayout(UpdateDialog.getContentPane());
        UpdateDialog.getContentPane().setLayout(UpdateDialogLayout);
        UpdateDialogLayout.setHorizontalGroup(
            UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        UpdateDialogLayout.setVerticalGroup(
            UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel8.setBackground(new java.awt.Color(0, 51, 51));

        jSimpan4.setBackground(new java.awt.Color(0, 102, 102));
        jSimpan4.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jSimpan4.setForeground(new java.awt.Color(0, 204, 204));
        jSimpan4.setText("SIMPAN");
        jSimpan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSimpan4ActionPerformed(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(0, 102, 102));
        jLabel22.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 204, 204));
        jLabel22.setText("ID");

        jLabel23.setBackground(new java.awt.Color(0, 102, 102));
        jLabel23.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 204, 204));
        jLabel23.setText("EMAIL");

        jLabel24.setBackground(new java.awt.Color(0, 102, 102));
        jLabel24.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 204, 204));
        jLabel24.setText("ASAL");

        tNama1.setBackground(new java.awt.Color(0, 102, 102));
        tNama1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tNama1.setForeground(new java.awt.Color(0, 204, 204));
        tNama1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tNama1ActionPerformed(evt);
            }
        });

        tEmail1.setBackground(new java.awt.Color(0, 102, 102));
        tEmail1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tEmail1.setForeground(new java.awt.Color(0, 204, 204));

        tAsal1.setBackground(new java.awt.Color(0, 102, 102));
        tAsal1.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tAsal1.setForeground(new java.awt.Color(0, 204, 204));
        tAsal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tAsal1ActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 153, 153));
        jLabel25.setText("UPDATE DATA PENGARANG");

        jLabel26.setBackground(new java.awt.Color(0, 102, 102));
        jLabel26.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 204, 204));
        jLabel26.setText("NAMA");

        tIdP.setBackground(new java.awt.Color(204, 204, 204));
        tIdP.setFont(new java.awt.Font("Cambria", 1, 12)); // NOI18N
        tIdP.setForeground(new java.awt.Color(0, 204, 204));
        tIdP.setEnabled(false);
        tIdP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tIdPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSimpan4)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tAsal1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                            .addComponent(tEmail1)
                            .addComponent(tNama1)
                            .addComponent(tIdP))))
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(81, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addGap(73, 73, 73))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel25)
                .addGap(34, 34, 34)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(tIdP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(tNama1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(tEmail1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(tAsal1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSimpan4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout UpdateDialogPLayout = new javax.swing.GroupLayout(UpdateDialogP.getContentPane());
        UpdateDialogP.getContentPane().setLayout(UpdateDialogPLayout);
        UpdateDialogPLayout.setHorizontalGroup(
            UpdateDialogPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        UpdateDialogPLayout.setVerticalGroup(
            UpdateDialogPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 51, 51));

        jLabel1.setBackground(new java.awt.Color(255, 204, 0));
        jLabel1.setFont(new java.awt.Font("STZhongsong", 1, 40)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 128, 128));
        jLabel1.setText("TOKO KOMIK");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane1.setForeground(new java.awt.Color(0, 102, 102));
        jTabbedPane1.setFont(new java.awt.Font("STZhongsong", 0, 12)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(1, 75, 75));

        jTableKomik.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "id", "Judul", "Title 3", "Title 4"
            }
        ));
        jTableKomik.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableKomikMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTableKomik);

        jButton14.setBackground(new java.awt.Color(0, 51, 51));
        jButton14.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton14.setForeground(new java.awt.Color(0, 153, 153));
        jButton14.setText("UPLOAD");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(0, 51, 51));
        jButton15.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton15.setForeground(new java.awt.Color(0, 153, 153));
        jButton15.setText("CETAK");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(0, 51, 51));
        jButton11.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton11.setForeground(new java.awt.Color(0, 153, 153));
        jButton11.setText("INSERT");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(0, 51, 51));
        jButton13.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton13.setForeground(new java.awt.Color(0, 153, 153));
        jButton13.setText("DELETE");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(0, 51, 51));
        jButton12.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton12.setForeground(new java.awt.Color(0, 153, 153));
        jButton12.setText("UPDATE");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(91, 91, 91))
        );

        jTabbedPane1.addTab("KOMIK", jPanel2);

        jPanel3.setBackground(new java.awt.Color(0, 76, 76));

        jButton1.setBackground(new java.awt.Color(0, 51, 51));
        jButton1.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 153, 153));
        jButton1.setText("INSERT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 51, 51));
        jButton2.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 153, 153));
        jButton2.setText("UPDATE");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 51, 51));
        jButton3.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 153, 153));
        jButton3.setText("DELETE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 51, 51));
        jButton4.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 153, 153));
        jButton4.setText("UPLOAD");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(0, 51, 51));
        jButton5.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 153, 153));
        jButton5.setText("CETAK");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTablePengarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(jTablePengarang);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("PENGARANG", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// 1. Cek apakah ada baris yang dipilih di jTablePengarang
        int barisTerpilih = jTablePengarang.getSelectedRow();

        if (barisTerpilih == -1) {
            // Jika tidak ada baris yang dipilih, tampilkan peringatan
            JOptionPane.showMessageDialog(this, "Silakan pilih data Pengarang yang ingin diubah terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return; // Hentikan proses
        }

        // 2. Jika ada baris, ambil ID-nya
        try {
            // Ambil ID Pengarang dari kolom pertama (index 0)
            Object idObj = jTablePengarang.getValueAt(barisTerpilih, 0);
            Integer idPengarangTerpilih = Integer.valueOf(idObj.toString());

            // 3. Panggil method untuk menampilkan dialog edit dan memuat data
            tampilkanDataPengarangUntukEdit(idPengarangTerpilih);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan dalam membaca ID data.",
                    "Error Data", JOptionPane.ERROR_MESSAGE);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // 1. Cek apakah ada baris yang dipilih
        int barisTerpilih = jTableKomik.getSelectedRow();

        if (barisTerpilih == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data Komik yang ingin diubah terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Jika ada baris yang dipilih, ambil ID-nya
        try {
            Object idObj = jTableKomik.getValueAt(barisTerpilih, 0);
            Integer idKomikTerpilih = Integer.valueOf(idObj.toString());

            // 3. Panggil method untuk menampilkan dialog edit dan memuat data
            tampilkanDataKomikUntukEdit(idKomikTerpilih); // Method ini yang akan memunculkan dialog

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan dalam membaca ID data.",
                    "Error Data", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void tJudulActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tJudulActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tJudulActionPerformed

    private void tGenreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tGenreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tGenreActionPerformed

    private void jSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSimpanActionPerformed
// Asumsi: cbPengarang adalah JComboBox yang berisi OBJEK Pengarang
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // 1. Ambil nilai dari input form
            String judul = tJudul.getText();
            Integer tahunTerbit = Integer.valueOf(tTahunTerbit.getText());
            String genre = tGenre.getText();

            // **AMBIL OBJEK PENGARANG LANGSUNG DARI COMBOBOX**
            Pengarang pengarangDipilih = (Pengarang) cbPengarang.getSelectedItem();

            // 2. Validasi Foreign Key
            if (pengarangDipilih == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih Pengarang!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Buat EntityManager
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            // 4. Attach objek Pengarang ke sesi EntityManager saat ini
            // Objek Pengarang yang diambil dari ComboBox perlu "dilekatkan" (merge) 
            // ke sesi EntityManager baru ini sebelum digunakan untuk relasi.
            Pengarang pengarangManaged = em.merge(pengarangDipilih);

            // 5. Buat objek Komik
            Komik dataKomik = new Komik();
            dataKomik.setJudul(judul);
            dataKomik.setTahunTerbit(tahunTerbit);
            dataKomik.setGenre(genre);
            dataKomik.setIdPengarang(pengarangManaged); // Set Objek Pengarang yang terkelola

            // 6. Simpan ke database
            em.getTransaction().begin();
            em.persist(dataKomik);
            em.getTransaction().commit();

            // 7. Selesaikan dan Refresh
            JOptionPane.showMessageDialog(this, "Data Komik berhasil ditambahkan!");
            tampilDataKomik();
            // clearKomik();
            InsertDialog.setVisible(false);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tahun Terbit harus berupa angka yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan Insert: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }//GEN-LAST:event_jSimpanActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        InsertDialogP.pack(); // atur ulang ukuran sesuai desain
        InsertDialogP.setLocationRelativeTo(this);
        InsertDialogP.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
//        clear();
        InsertDialog.pack(); // atur ulang ukuran sesuai desain
        InsertDialog.setLocationRelativeTo(this);
        InsertDialog.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jSimpan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSimpan1ActionPerformed
        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // 1. Ambil nilai dari input form (ID Pengarang TIDAK diambil)
            String namaPengarang = tNama.getText();
            String email = tEmail.getText();
            String asalNegara = tAsal.getText();

            // --- Validasi Dasar ---
            if (namaPengarang.trim().isEmpty() || email.trim().isEmpty() || asalNegara.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Buat EntityManager
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            // 3. Buat objek Pengarang
            Pengarang dataPengarang = new Pengarang();
            // ID Pengarang TIDAK di-set, database yang akan mengisinya
            dataPengarang.setNamaPengarang(namaPengarang);
            dataPengarang.setEmail(email);
            dataPengarang.setAsalNegara(asalNegara);

            // 4. Simpan ke database
            em.getTransaction().begin();
            em.persist(dataPengarang);
            em.getTransaction().commit();

            // 5. Selesaikan dan Refresh
            JOptionPane.showMessageDialog(this, "Data Pengarang berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Tutup Dialog (Ganti 'InsertDialogPengarang' dengan variabel JDialog Anda)
            InsertDialogP.setVisible(false);
            loadPengarangToComboBox(cbPengarang);
            tampilDataPengarang(); // Refresh tabel utama Pengarang
            //clearPengarang(); // Bersihkan field input

        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan Insert: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Tangkap exception umum lain, misalnya jika koneksi gagal
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // 6. Tutup koneksi
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jSimpan1ActionPerformed

    private void tNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNamaActionPerformed

    private void tAsalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tAsalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tAsalActionPerformed

    private void cbPengarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPengarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPengarangActionPerformed

    private void cbPengarangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbPengarangItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPengarangItemStateChanged

    private void jSimpan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSimpan2ActionPerformed

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // 1. Ambil semua nilai, termasuk ID (dari tIdKomikEdit)
            Integer idKomik = Integer.valueOf(tId.getText()); // ID diambil untuk kunci update
            String judul = tJudul3.getText();
            Integer tahunTerbit = Integer.valueOf(tTahunTerbit1.getText());
            String genre = tGenre1.getText();

            // Ambil Objek Pengarang dari ComboBox
            Pengarang pengarangDipilih = (Pengarang) cbPengarang1.getSelectedItem();

            if (pengarangDipilih == null) {
                JOptionPane.showMessageDialog(this, "Silakan pilih Pengarang!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Buat EntityManager
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            // 3. Cari entitas yang akan diubah (Optional, tapi disarankan)
            Komik dataLama = em.find(Komik.class, idKomik);

            if (dataLama == null) {
                JOptionPane.showMessageDialog(this, "Data lama tidak ditemukan untuk diupdate!");
                return;
            }

            // 4. Update dataLama dengan nilai baru
            dataLama.setJudul(judul);
            dataLama.setTahunTerbit(tahunTerbit);
            dataLama.setGenre(genre);

            // Merge Objek Pengarang
            Pengarang pengarangManaged = em.merge(pengarangDipilih);
            dataLama.setIdPengarang(pengarangManaged);

            // 5. Eksekusi Update (Merge)
            em.getTransaction().begin();
            em.merge(dataLama); // Menggunakan merge untuk update
            em.getTransaction().commit();

            // 6. Selesaikan dan Refresh
            JOptionPane.showMessageDialog(this, "Data Komik berhasil diperbarui!");
            UpdateDialog.setVisible(false);
            tampilDataKomik();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID, Tahun Terbit harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jSimpan2ActionPerformed

    private void tIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tIdActionPerformed

    private void tGenre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tGenre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tGenre1ActionPerformed

    private void cbPengarang1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbPengarang1ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPengarang1ItemStateChanged

    private void cbPengarang1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPengarang1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPengarang1ActionPerformed

    private void tJudul3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tJudul3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tJudul3ActionPerformed

    private void jTableKomikMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableKomikMouseClicked

    }//GEN-LAST:event_jTableKomikMouseClicked

    private void jSimpan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSimpan4ActionPerformed
        EntityManagerFactory emf = null;
        EntityManager em = null;
        Integer idPengarang = null;

        // Ambil teks ID dari field Pengarang dan bersihkan spasi
        String idText = tIdP.getText().trim();

        try {
            // 1. Cek dan konversi ID
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID Pengarang tidak boleh kosong!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            idPengarang = Integer.valueOf(idText); // Konversi ID yang sudah di-trim

            // 2. Ambil nilai lainnya
            String namaPengarang = tNama1.getText(); // Asumsi field Update Pengarang adalah tNama1
            String email = tEmail1.getText();       // Asumsi field Update Pengarang adalah tEmail1
            String asalNegara = tAsal1.getText();   // Asumsi field Update Pengarang adalah tAsal1

            // 3. Buat EntityManager
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();

            // 4. Cari entitas yang akan diubah
            Pengarang dataLama = em.find(Pengarang.class, idPengarang);

            if (dataLama == null) {
                JOptionPane.showMessageDialog(this, "Data lama tidak ditemukan untuk diupdate!");
                return;
            }

            // 5. Update dataLama dengan nilai baru
            dataLama.setNamaPengarang(namaPengarang);
            dataLama.setEmail(email);
            dataLama.setAsalNegara(asalNegara);

            // 6. Eksekusi Update (Merge)
            em.getTransaction().begin();
            em.merge(dataLama);
            em.getTransaction().commit();

            // 7. Selesaikan dan Refresh
            JOptionPane.showMessageDialog(this, "Data Pengarang berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            UpdateDialogP.setVisible(false);
            loadPengarangToComboBox(cbPengarang); // Refresh ComboBox Insert Komik
            loadPengarangToComboBox(cbPengarang1); // Refresh ComboBox Update Komik
            tampilDataPengarang();

        } catch (NumberFormatException e) {
            // Hanya menangani NumberFormatException (misalnya, jika Nama atau Asal diisi angka)
            JOptionPane.showMessageDialog(this, "Terdapat input yang tidak valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate data Pengarang: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jSimpan4ActionPerformed

    private void tNama1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tNama1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tNama1ActionPerformed

    private void tAsal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tAsal1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tAsal1ActionPerformed

    private void tIdPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tIdPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tIdPActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // 1. Cek apakah ada baris yang dipilih di jTablePengarang
        int barisTerpilih = jTablePengarang.getSelectedRow();

        if (barisTerpilih == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data Pengarang yang ingin dihapus terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Ambil ID dan Nama Pengarang dari baris yang dipilih
        try {
            // Ambil ID Pengarang dari kolom pertama (index 0)
            Object idObj = jTablePengarang.getValueAt(barisTerpilih, 0);
            // Asumsi Nama Pengarang ada di kolom index 1
            Object namaObj = jTablePengarang.getValueAt(barisTerpilih, 1);

            Integer idPengarangTerpilih = Integer.valueOf(idObj.toString());
            String namaPengarang = namaObj.toString();

            // 3. Tampilkan Dialog Konfirmasi (dengan styling yang diperbagus)
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    "<html><b>ANDA YAKIN?</b> Data Pengarang berikut akan dihapus secara permanen:<br><br>"
                    + "&nbsp; &nbsp; <b>ID:</b> " + idPengarangTerpilih + "<br>"
                    + "&nbsp; &nbsp; <b>Nama:</b> " + namaPengarang + "<br><br>"
                    + "Aksi ini tidak dapat dibatalkan.</html>",
                    "KONFIRMASI HAPUS DATA PENGARANG",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE); // Menggunakan ikon peringatan

            // 4. Jika pengguna memilih YES (Hapus), panggil method hapus
            if (konfirmasi == JOptionPane.YES_OPTION) {
                hapusDataPengarang(idPengarangTerpilih);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan dalam membaca ID data.",
                    "Error Data", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        // 1. Cek apakah ada baris yang dipilih
        int barisTerpilih = jTableKomik.getSelectedRow();

        if (barisTerpilih == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data Komik yang ingin dihapus terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Ambil ID dan Judul dari baris yang dipilih
        try {
            Object idObj = jTableKomik.getValueAt(barisTerpilih, 0);
            Object judulObj = jTableKomik.getValueAt(barisTerpilih, 1); // Asumsi Judul ada di kolom index 1

            Integer idKomikTerpilih = Integer.valueOf(idObj.toString());
            String judulKomik = judulObj.toString();

            // 3. Tampilkan Dialog Konfirmasi
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                    // Menggunakan HTML untuk sedikit pemformatan (tebal)
                    "<html><b>ANDA YAKIN?</b> Data Komik berikut akan dihapus secara permanen:<br><br>"
                    + "&nbsp; &nbsp; <b>ID:</b> " + idKomikTerpilih + "<br>"
                    + "&nbsp; &nbsp; <b>Judul:</b> " + judulKomik + "<br><br>"
                    + "Aksi ini tidak dapat dibatalkan.</html>",
                    "KONFIRMASI HAPUS DATA", // Judul Uppercase untuk penekanan
                    JOptionPane.YES_NO_OPTION,
                    // Mengganti ikon QUESTION_MESSAGE menjadi WARNING_MESSAGE
                    JOptionPane.WARNING_MESSAGE);

            // 4. Jika pengguna memilih YES (Hapus), panggil method hapus
            if (konfirmasi == JOptionPane.YES_OPTION) {
                hapusDataKomik(idKomikTerpilih);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan dalam membaca ID data.",
                    "Error Data", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        // TODO add your handling code here:    // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed
    private void importCsvKomik(File csvFile) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        BufferedReader br = null;
        int successCount = 0;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();
            br = new BufferedReader(new FileReader(csvFile));

            String line;
            em.getTransaction().begin();

            // Lewati baris header (jika ada)
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";"); // Asumsi delimiter koma (,)

                if (data.length < 4) {
                    continue; // Lewati baris yang tidak lengkap
                }
                try {
                    // 1. Parsing Data CSV
                    String judul = data[0].trim();
                    Integer tahunTerbit = Integer.valueOf(data[1].trim());
                    String genre = data[2].trim();
                    Integer idPengarangDipilih = Integer.valueOf(data[3].trim());

                    // 2. Cari Objek Pengarang (Foreign Key)
                    Pengarang pengarang = em.find(Pengarang.class, idPengarangDipilih);

                    if (pengarang == null) {
                        System.err.println("Gagal import baris: Pengarang ID " + idPengarangDipilih + " tidak ditemukan.");
                        continue; // Lewati baris ini
                    }

                    // 3. Buat dan Set Objek Komik
                    Komik dataKomik = new Komik();
                    dataKomik.setJudul(judul);
                    dataKomik.setTahunTerbit(tahunTerbit);
                    dataKomik.setGenre(genre);
                    dataKomik.setIdPengarang(pengarang); // Set Objek Pengarang

                    // 4. Persist (Simpan)
                    em.persist(dataKomik);
                    successCount++;

                } catch (NumberFormatException nfe) {
                    System.err.println("Gagal import baris (Format Angka Salah): " + line);
                }
            }

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(this,
                    "Impor CSV berhasil! Total " + successCount + " data Komik ditambahkan.",
                    "Sukses Impor", JOptionPane.INFORMATION_MESSAGE);

            tampilDataKomik(); // Refresh tabel

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal membaca file CSV: " + e.getMessage(),
                    "Error File", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(this, "Gagal mengimpor data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Tutup resource
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }
    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            importCsvKomik(selectedFile);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed
    private void importCsvPengarang(File csvFile) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        BufferedReader br = null;
        int successCount = 0;

        try {
            emf = Persistence.createEntityManagerFactory("Pertemuan_KeduabelasPU");
            em = emf.createEntityManager();
            br = new BufferedReader(new FileReader(csvFile));

            String line;
            em.getTransaction().begin();

            // Lewati baris header (jika ada)
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";"); // Asumsi delimiter koma (,)

                // Pengarang memiliki 3 field non-ID: Nama, Email, Asal Negara
                if (data.length < 3) {
                    continue;
                }

                try {
                    // 1. Parsing Data CSV
                    String namaPengarang = data[0].trim();
                    String email = data[1].trim();
                    String asalNegara = data[2].trim();

                    // 2. Buat dan Set Objek Pengarang
                    Pengarang dataPengarang = new Pengarang();
                    // ID akan di-generate otomatis oleh database
                    dataPengarang.setNamaPengarang(namaPengarang);
                    dataPengarang.setEmail(email);
                    dataPengarang.setAsalNegara(asalNegara);

                    // 3. Persist (Simpan)
                    em.persist(dataPengarang);
                    successCount++;

                } catch (Exception e) {
                    System.err.println("Gagal import baris (Data Error): " + line + ". Error: " + e.getMessage());
                }
            }

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(this,
                    "Impor CSV berhasil! Total " + successCount + " data Pengarang ditambahkan.",
                    "Sukses Impor", JOptionPane.INFORMATION_MESSAGE);

            tampilDataPengarang(); // Refresh tabel Pengarang
            loadPengarangToComboBox(cbPengarang);

            // Refresh ComboBox di Form Komik (jika sedang terbuka)
            // Jika Anda memiliki referensi ke ComboBox Komik, refresh juga di sini:
            // loadPengarangToComboBox(cbPengarang); 
            // loadPengarangToComboBox(cbPengarang1); 
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal membaca file CSV: " + e.getMessage(),
                    "Error File", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(this, "Gagal mengimpor data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Tutup resource
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            importCsvPengarang(selectedFile);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        try {
            // Lokasi file laporan .jasper (hasil compile dari .jrxml)
            String reportPath = "src/PertemuanKeduabelas/Komik.jasper";
            Connection conn = getConnection();

            net.sf.jasperreports.engine.JasperPrint jp
                    = net.sf.jasperreports.engine.JasperFillManager.fillReport(reportPath, null, conn);

            net.sf.jasperreports.view.JasperViewer.viewReport(jp, false);

        } catch (JRException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Gagal mencetak laporan: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        try {
            // Lokasi file laporan .jasper (hasil compile dari .jrxml)
            String reportPath = "src/PertemuanKeduabelas/Pengarang.jasper";
            Connection conn = getConnection();

            net.sf.jasperreports.engine.JasperPrint jp
                    = net.sf.jasperreports.engine.JasperFillManager.fillReport(reportPath, null, conn);

            net.sf.jasperreports.view.JasperViewer.viewReport(jp, false);

        } catch (JRException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Gagal mencetak laporan: " + e.getMessage());
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Toko_Komik.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Toko_Komik.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Toko_Komik.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Toko_Komik.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Toko_Komik().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog InsertDialog;
    private javax.swing.JDialog InsertDialogP;
    private javax.swing.JDialog UpdateDialog;
    private javax.swing.JDialog UpdateDialogP;
    private javax.swing.JComboBox<Pengarang> cbPengarang;
    private javax.swing.JComboBox<Pengarang> cbPengarang1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JButton jSimpan;
    private javax.swing.JButton jSimpan1;
    private javax.swing.JButton jSimpan2;
    private javax.swing.JButton jSimpan4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableKomik;
    private javax.swing.JTable jTablePengarang;
    private javax.swing.JTextField tAsal;
    private javax.swing.JTextField tAsal1;
    private javax.swing.JTextField tEmail;
    private javax.swing.JTextField tEmail1;
    private javax.swing.JTextField tGenre;
    private javax.swing.JTextField tGenre1;
    private javax.swing.JTextField tId;
    private javax.swing.JTextField tIdP;
    private javax.swing.JTextField tJudul;
    private javax.swing.JTextField tJudul3;
    private javax.swing.JTextField tNama;
    private javax.swing.JTextField tNama1;
    private javax.swing.JTextField tTahunTerbit;
    private javax.swing.JTextField tTahunTerbit1;
    // End of variables declaration//GEN-END:variables
}
