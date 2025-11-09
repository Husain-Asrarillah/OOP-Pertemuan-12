-----

# Laporan Praktikum PBOPertemuan12: CRUD JPA & JasperReports Toko Komik 📚

## 📝 Pendahuluan

Dalam pengembangan aplikasi ini, kami mengimplementasikan sistem yang mengelola data dari dua tabel berelasi: **Komik** dan **Pengarang**. Tujuan utama praktikum ini adalah mengintegrasikan operasi **CRUD** (Create, Read, Update, Delete) menggunakan **Java Persistence API (JPA)** untuk koneksi *database* efisien, dan **JasperReports** untuk menghasilkan laporan profesional.

Aplikasi ini menghasilkan sistem yang dinamis, terstruktur, dan mampu memproses data yang terhubung secara logis melalui *Foreign Key*.

-----

## 📚 Tinjauan Pustaka

### Java Persistence API (JPA)

JPA adalah spesifikasi Java yang digunakan untuk mengelola data relasional. Data disimpan sebagai **Entity Class** (`Komik.java`, `Pengarang.java`) dan dipetakan ke tabel *database*. JPA mempermudah operasi CRUD tanpa perlu menulis *query* SQL manual.

### Foreign Key & Relasi

*Foreign Key* adalah kolom yang menjaga integritas data dan menunjukkan hubungan antar entitas (misalnya, `Komik.id_pengarang` menunjuk ke `Pengarang.id_pengarang`).

### CRUD (Create, Read, Update, Delete)

Empat operasi dasar pengelolaan data yang diimplementasikan menggunakan `EntityManager` dari JPA:

  * **Create (Insert):** Menambah data baru (melalui `em.persist()`).
  * **Read (Tampil):** Menampilkan data yang sudah ada (melalui `em.createNamedQuery()`).
  * **Update:** Memperbarui data tertentu (melalui `em.merge()`).
  * **Delete:** Menghapus data dari *database* (melalui `em.remove()`).

### JasperReports

*Library open-source* yang digunakan untuk menghasilkan laporan profesional (PDF) dari data aplikasi. Dalam proyek ini, JasperReports diintegrasikan menggunakan koneksi **JDBC** dan *query* SQL langsung di file `.jrxml` untuk mencetak data Komik dan Pengarang yang terurut.

-----

## 🛠️ Langkah – Langkah Implementasi

### A. Persiapan Project & Entitas

1.  **Database & Project:** Membuat *database* (`Toko_komik`) dengan tabel **`komik`** dan **`pengarang`**, kemudian membuat *project* Java (`Toko_Komik.java`) dengan *package* `PertemuanKeduabelas`.
2.  **Desain GUI:** Mendesain `JFrame` utama dengan `JTabbedPane` (tab Komik dan Pengarang), masing-masing berisi `JTable` dan tombol CRUD/Utility.

    <img width="740" height="592" alt="image" src="https://github.com/user-attachments/assets/c2088ebc-b55e-449a-810e-2562779a4820" />

3.  **Entity Class & PU:** Membuat **`Komik.java`** dan **`Pengarang.java`** dengan *mapping* JPA yang benar (menggunakan `@GeneratedValue(strategy = GenerationType.IDENTITY)`) dan menyiapkan `persistence.xml`.
4.  **Dialogs:** Membuat dan mendesain 4 *JDialog* untuk operasi **Insert** dan **Update** Komik/Pengarang.

    <img width="348" height="167" alt="image" src="https://github.com/user-attachments/assets/2095382f-a7fb-4de0-8cfe-0832ce090878" />


### B. Implementasi Fungsionalitas Utama

| Fungsionalitas | Metode Kunci | Deskripsi |
| :--- | :--- | :--- |
| **Tampil Data (Read)** | `tampilDataKomik()` / `tampilDataPengarang()` | Mengisi `JTable` dengan data dari JPA (`findAll`), diurutkan berdasarkan ID (`ORDER BY ID`). |
| **Insert Komik** | `jSimpanActionPerformed` | Mengambil data dari *form*, menggunakan `loadPengarangToComboBox()` (sebelumnya) untuk mendapatkan objek **Pengarang** (FK), kemudian `em.persist(komik)`. |
| **Update Komik/Pengarang**| `jButton12ActionPerformed` / `jSimpan2ActionPerformed` | Tombol `UPDATE` memicu `tampilkanDataKomikUntukEdit(ID)` untuk memuat data ke *dialog*. Simpan menggunakan `em.merge(dataLama)`. |
| **Delete Komik/Pengarang**| `jButton13ActionPerformed` / `jButton3ActionPerformed` | Memeriksa pilihan, menampilkan `JOptionPane.showConfirmDialog` kustom (dengan *HTML styling*), dan mengeksekusi `em.remove(data)`. |
| **Upload CSV** | `importCsvKomik(File)` / `importCsvPengarang(File)` | Membaca *file* CSV baris per baris, mem-*parsing* data, dan menyimpannya menggunakan `em.persist()`. *ComboBox* Pengarang di-*refresh* setelah impor. |
| **Load ComboBox** | `loadPengarangToComboBox(JComboBox target)` | *Method* yang dapat digunakan kembali untuk mengisi *ComboBox* Insert (`cbPengarang`) dan Update (`cbPengarang1`) hanya sekali saat *startup* aplikasi. |

### C. Laporan (JasperReports)

| Laporan | Metode | Detail |
| :--- | :--- | :--- |
| **Cetak Komik** | `jButton15ActionPerformed` / `cetakLaporanKomik()` | Mengkompilasi `Komik.jrxml` (yang berisi *query* `SELECT... JOIN...`) dan mengisi laporan menggunakan koneksi **JDBC** (`getConnection()`). |
| **Cetak Pengarang** | `jButton5ActionPerformed` / `cetakLaporanPengarang()` | Mengkompilasi `reportPengarang.jrxml` (yang berisi *query* `SELECT... FROM pengarang`) dan mengisi laporan menggunakan koneksi **JDBC**. |

-----

## 📋 Detail Implementasi

### Koneksi Database (JDBC)

```java
static final String URL = "jdbc:postgresql://localhost:5432/Toko_komik";
static final String USER = "postgres"; 
static final String PASSWORD = "170206"; 

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
```

### Contoh Konfirmasi Hapus (Aksi Tombol)

```java
private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {
    int barisTerpilih = jTableKomik.getSelectedRow();
    if (barisTerpilih == -1) {
        JOptionPane.showMessageDialog(this, "Silakan pilih data Komik yang ingin diubah terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    // ... (ambil ID dan Judul)
    int konfirmasi = JOptionPane.showConfirmDialog(this, 
        "<html><b>ANDA YAKIN?</b> Data Komik berikut akan dihapus secara permanen:<br><br>" +
        "&nbsp; &nbsp; <b>ID:</b> " + idKomikTerpilih + "<br>" +
        "&nbsp; &nbsp; <b>Judul:</b> " + judulKomik + "<br><br>" +
        "Aksi ini tidak dapat dibatalkan.</html>",
        "KONFIRMASI HAPUS DATA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    
    if (konfirmasi == JOptionPane.YES_OPTION) {
        hapusDataKomik(idKomikTerpilih);
    }
}
```
