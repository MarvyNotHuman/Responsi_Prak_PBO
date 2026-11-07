package Proyek_SPBU_Responsi_PBO;

public class Main {
    private static final java.util.Scanner input = new java.util.Scanner(System.in);

    public static void main(String[] args) {
        BahanBakar[] daftarBBM = {
            new BahanBakar("Pertalite", 10000, false, true),
            new BahanBakar("Pertamax", 16950),
            new BahanBakar("Solar", 6800, true, true)
        };
        SPBU spbu = new SPBU("Purwokerto, Jawa Tengah", "SPBU-001");

        boolean berjalan = true;
        while (berjalan) {
            System.out.println("\n========== MENU UTAMA ==========");
            System.out.println("1 = Transaksi");
            System.out.println("2 = Demonstrasi konsep");
            System.out.println("3 = Keluar");
            int menu = bacaPilihan("Pilih menu (1-3): ", 1, 3);

            if (menu == 1) {
                int lanjut;
                do {
                    jalankanTransaksi(spbu, daftarBBM);
                    System.out.println("\n1 = Transaksi baru");
                    System.out.println("2 = Kembali ke menu utama");
                    lanjut = bacaPilihan("Pilih (1-2): ", 1, 2);
                } while (lanjut == 1);
            } else if (menu == 2) {
                int ulang;
                do {
                    jalankanDemonstrasi();
                    System.out.println("\n1 = Kembali ke menu utama");
                    System.out.println("2 = Ulangi demonstrasi");
                    ulang = bacaPilihan("Pilih (1-2): ", 1, 2);
                } while (ulang == 2);
            } else {
                berjalan = false;
            }
        }

        System.out.println("\nTerima kasih telah menggunakan sistem " + SPBU.NAMA_SPBU + ".");
        input.close();
    }

    // =====================================================================
    //  MENU 1: TRANSAKSI
    // =====================================================================
    private static void jalankanTransaksi(SPBU spbu, BahanBakar[] daftarBBM) {
        // 1. Data awal SPBU dan bahan bakar yang tersedia
        spbu.tampilkanInfo();
        tampilkanDaftarBBM(daftarBBM);

        // 2. Isi data kendaraan
        int jenis = bacaPilihan("\nJenis kendaraan (1=Mobil, 2=Motor): ", 1, 2);
        String pemilik = bacaTeks("Nama pemilik: ");
        String plat = bacaTeks("Nomor plat: ");

        Kendaraan kendaraan;
        Pengisian layananPengisian;   // polimorfisme lewat interface
        Pembayaran layananPembayaran; // interface kedua (multiple interface)

        if (jenis == 1) {
            int mesin = bacaIntPositif("Masukkan kapasitas mesin mobil (cc): ");
            int roda = bacaRodaMobil();
            int penumpang = bacaIntPositif("Masukkan maksimal kapasitas penumpang (orang): ");
            double kapasitasBBM = bacaDoublePositif("Masukkan maksimal kapasitas bahan bakar (liter): ");
            Mobil mobil = new Mobil(plat, pemilik, mesin, roda, penumpang, kapasitasBBM);
            kendaraan = mobil;
            layananPengisian = mobil;
            layananPembayaran = mobil;
        } else {
            int mesin = bacaIntPositif("Masukkan kapasitas mesin motor (cc): ");
            double kapasitasBBM = bacaDoublePositif("Masukkan maksimal kapasitas bahan bakar (liter): ");
            Motor motor = new Motor(plat, pemilik, mesin, kapasitasBBM);
            kendaraan = motor;
            layananPengisian = motor;
            layananPembayaran = motor;
            System.out.println("Kapasitas penumpang motor ditetapkan 2 orang.");
        }

        // Isi tangki saat datang ke SPBU (state awal kendaraan)
        double isiAwal = bacaDoubleRentang(
                "Isi tangki saat ini (liter, 0 jika kosong): ",
                kendaraan.getMaksimalKapasitasBahanBakar());
        kendaraan.setTotalLiter(isiAwal);

        // Tangki sudah penuh: tidak ada yang bisa diisi, transaksi langsung dihentikan.
        if (kendaraan.getSisaKapasitas() <= 0) {
            System.out.printf("%nTangki penuh (%.2f dari %.2f liter). Pengisian tidak dapat dilakukan.%n",
                    kendaraan.getTotalLiter(), kendaraan.getMaksimalKapasitasBahanBakar());
            return;
        }

        // 3. Data kendaraan ditampilkan
        System.out.println();
        kendaraan.cekKendaraan();
        System.out.println();
        kendaraan.tampilkanInfo();

        // 4. Daftar bahan bakar ditampilkan kembali
        tampilkanDaftarBBM(daftarBBM);

        // 5. Pilih BBM dan isi jumlah liter
        double isiSebelum = kendaraan.getTotalLiter();
        BahanBakar bbmDipilih = null;
        double liter = 0;
        double total = 0;
        while (total == 0) {
            int pilihan = bacaPilihan("\nPilih BBM (1-" + daftarBBM.length + "): ", 1, daftarBBM.length);
            bbmDipilih = daftarBBM[pilihan - 1];
            if (!kendaraan.bolehMemakai(bbmDipilih)) {
                System.out.println("Pilihan ditolak: " + kendaraan.getAlasanPenolakan(bbmDipilih));
                System.out.println("Silakan pilih BBM lain.");
                continue;
            }
            System.out.printf("BBM dipilih: %s (Rp%,.0f/liter)%n",
                    bbmDipilih.getNama(), bbmDipilih.getHargaPerLiter());

            // Selama pengisian gagal, hanya jumlah liter yang diminta ulang.
            // Memasukkan 0 membatalkan BBM dan kembali ke pilihan BBM.
            boolean gantiBBM = false;
            while (total == 0 && !gantiBBM) {
                liter = bacaLiterAtauGanti("Jumlah liter (0 = ganti BBM): ");
                if (liter == 0) {
                    gantiBBM = true;
                    System.out.println("Pilihan BBM dibatalkan. Silakan pilih BBM kembali.");
                } else {
                    total = isiDenganSubsidi(spbu, kendaraan, layananPengisian, bbmDipilih, liter);
                    if (total == 0) {
                        System.out.println("Silakan masukkan jumlah liter kembali (atau 0 untuk ganti BBM).");
                    }
                }
            }
        }

        System.out.printf("%nTotal pembayaran: Rp%,.0f%n", total);
        layananPembayaran.bayar(total);

        // 6. Struk transaksi
        cetakStruk(spbu, kendaraan, bbmDipilih, isiSebelum, liter, total);
    }

    // Urutan pengisian: (1) cek kuota subsidi, (2) cek kapasitas tangki (di prosesPengisian).
    // Mengembalikan total harga, atau 0 jika salah satu pengecekan gagal.
    private static double isiDenganSubsidi(SPBU spbu, Kendaraan kendaraan, Pengisian layanan,
                                           BahanBakar bahanBakar, double liter) {
        if (!spbu.cekSubsidi(kendaraan, bahanBakar, liter)) {
            return 0;
        }
        double total = layanan.prosesPengisian(bahanBakar, liter);
        if (total > 0) {
            spbu.catatPengisian(kendaraan, bahanBakar, liter);
        }
        return total;
    }

    private static void tampilkanDaftarBBM(BahanBakar[] daftar) {
        System.out.println("\nDaftar bahan bakar:");
        for (int i = 0; i < daftar.length; i++) {
            System.out.print((i + 1) + ". ");
            daftar[i].tampilkanInfo();
        }
    }

    private static void cetakStruk(SPBU spbu, Kendaraan kendaraan, BahanBakar bbm,
                                   double isiSebelum, double liter, double total) {
        System.out.println("\n========== STRUK TRANSAKSI ==========");
        System.out.println(SPBU.NAMA_SPBU + " (" + spbu.getKodeSPBU() + ")");
        System.out.println(spbu.getLokasi());
        System.out.println("-------------------------------------");
        kendaraan.tampilkanInfo();
        System.out.println("-------------------------------------");
        System.out.println("BBM            : " + bbm.getNama());
        System.out.printf("Harga/liter    : Rp%,.0f%n", bbm.getHargaPerLiter());
        System.out.printf("Isi sebelum    : %.2f liter%n", isiSebelum);
        System.out.printf("Liter diisi    : %.2f%n", liter);
        System.out.printf("Total bayar    : Rp%,.0f%n", total);
        System.out.println("-------------------------------------");
        double batas = kendaraan.batasSubsidiPerHari(bbm);
        if (!bbm.isBersubsidi()) {
            System.out.println("Subsidi        : - (BBM non-subsidi)");
        } else if (Double.isInfinite(batas)) {
            System.out.println("Subsidi        : tanpa batas");
        } else {
            System.out.printf("Batas subsidi  : %.2f liter/hari%n", batas);
            System.out.printf("Sisa subsidi   : %.2f liter%n", spbu.getSisaSubsidi(kendaraan, bbm));
        }
        System.out.println("=====================================");
        System.out.println("Terima kasih, selamat jalan!");
    }

    // =====================================================================
    //  MENU 2: DEMONSTRASI KONSEP (pembuktian 10 persyaratan)
    //  Memakai data tetap, tanpa input pengguna.
    // =====================================================================
    private static void jalankanDemonstrasi() {
        System.out.println("\n========== DEMONSTRASI KONSEP OOP ==========");
        System.out.println("Pembuktian 10 persyaratan teknis (data tetap, tanpa input pengguna).");

        SPBU spbu = new SPBU("Purwokerto, Jawa Tengah", "SPBU-001");
        BahanBakar pertalite = new BahanBakar("Pertalite", 10000, false, true);
        BahanBakar solar = new BahanBakar("Solar", 6800, true, true);

        // --- Persyaratan 1 ---
        judul(1, "State dan Behavior",
                "Atribut berupa variabel, method prosedur (void), dan method fungsi (return value).",
                "BahanBakar.java, Kendaraan.java");
        Mobil mobil = new Mobil("R 1111 AA", "Andi", 1500, 4, 5, 40);
        baris("State (atribut)", "plat = " + mobil.getPlatNomor() + ", pemilik = " + mobil.getNamaPemilik()
                + ", isi tangki = " + liter(mobil.getTotalLiter()));
        System.out.printf(FORMAT_LABEL, "Method void");
        pertalite.tampilkanInfo();
        baris("Method return value", String.format("hitungHarga(5 liter) = Rp%,.0f", pertalite.hitungHarga(5)));

        // --- Persyaratan 2 ---
        judul(2, "Immutability",
                "Minimal satu konstanta global (public static final) dan satu atribut instans kekal (private final).",
                "SPBU.java, Kendaraan.java, BahanBakar.java");
        baris("Konstanta global", "NAMA_SPBU = \"" + SPBU.NAMA_SPBU + "\" [" + modifier(SPBU.class, "NAMA_SPBU") + "]");
        baris("Konstanta global", "PAJAK = " + SPBU.PAJAK + " [" + modifier(SPBU.class, "PAJAK") + "]");
        baris("Atribut instans kekal", "kodeSPBU = " + spbu.getKodeSPBU() + " [" + modifier(SPBU.class, "kodeSPBU") + "]");
        baris("Atribut instans kekal", "idKendaraan [" + modifier(Kendaraan.class, "idKendaraan") + "]");
        baris("Atribut instans kekal", "diesel (BBM) [" + modifier(BahanBakar.class, "diesel") + "]");
        baris("Catatan", "atribut final tidak memiliki setter, sehingga tidak dapat diubah.");

        // --- Persyaratan 3 ---
        judul(3, "Enkapsulasi dan Overloading",
                "Atribut private + getter/setter, memakai this, minimal dua konstruktor (overloaded).",
                "Mobil.java (konstruktor 6 dan 7 parameter, setter), Kendaraan.java");
        Mobil mobilA = new Mobil("R 2222 BB", "Budi", 1200, 4, 4, 35);                  // 6 parameter
        Mobil mobilB = new Mobil("R 3333 CC", "Citra", "ID-MBL-01", 1300, 4, 4, 38);    // 7 parameter
        baris("Modifier atribut", "Mobil.kapasitasMesin = [" + modifier(Mobil.class, "kapasitasMesin")
                + "], Kendaraan.platNomor = [" + modifier(Kendaraan.class, "platNomor") + "]");
        baris("Konstruktor 6 parameter", "idKendaraan otomatis = " + mobilA.getIdKendaraan());
        baris("Konstruktor 7 parameter", "idKendaraan manual   = " + mobilB.getIdKendaraan());
        mobilA.setNamaPemilik("Budi Santoso");
        baris("Setter dan getter", "pemilik setelah setNamaPemilik() = " + mobilA.getNamaPemilik());
        try {
            mobilA.setKapasitasMesin(0);
        } catch (IllegalArgumentException e) {
            baris("Validasi di setter", e.getMessage());
        }

        // --- Persyaratan 4 ---
        judul(4, "Inheritance",
                "Relasi superclass dan minimal dua subclass melalui extends; class Main terpisah.",
                "Mobil.java, Motor.java (extends Kendaraan), Main.java (file terpisah)");
        Motor motor = new Motor("R 4444 DD", "Dina", 150, 5);
        baris("Superclass Mobil", Mobil.class.getSuperclass().getSimpleName());
        baris("Superclass Motor", Motor.class.getSuperclass().getSimpleName());
        baris("Mobil adalah Kendaraan?", String.valueOf(mobil instanceof Kendaraan));
        baris("Motor adalah Kendaraan?", String.valueOf(motor instanceof Kendaraan));

        // --- Persyaratan 5 ---
        judul(5, "Super Chaining",
                "Pemanggilan super(...) pada konstruktor subclass dan super.namaMethod().",
                "Mobil.java dan Motor.java: konstruktor (super(...)) dan cekKendaraan() (super.cekKendaraan())");
        baris("super(...) pada konstruktor", "plat = " + mobil.getPlatNomor() + ", pemilik = " + mobil.getNamaPemilik()
                + ", kapasitas = " + liter(mobil.getMaksimalKapasitasBahanBakar()) + " (disimpan oleh Kendaraan)");
        baris("super.cekKendaraan()", "baris pertama di bawah berasal dari superclass:");
        mobil.cekKendaraan();

        // --- Persyaratan 6 ---
        judul(6, "Method Overriding",
                "Penimpaan method superclass pada subclass menggunakan anotasi @Override.",
                "Mobil.java dan Motor.java: cekKendaraan(), prosesPengisian(), bayar(), bolehMemakai()");
        baris("cekKendaraan() versi Motor", "berbeda dari versi Mobil pada persyaratan 5:");
        motor.cekKendaraan();
        baris("mobil.bolehMemakai(Solar)", String.valueOf(mobil.bolehMemakai(solar)) + " (versi Kendaraan)");
        baris("motor.bolehMemakai(Solar)", String.valueOf(motor.bolehMemakai(solar)) + " (versi Motor, di-override)");

        // --- Persyaratan 7 ---
        judul(7, "Abstract Class",
                "Superclass berstatus abstract dan memiliki minimal satu abstract method.",
                "Kendaraan.java: public abstract class Kendaraan");
        baris("Kendaraan abstract?", String.valueOf(java.lang.reflect.Modifier.isAbstract(Kendaraan.class.getModifiers())));
        baris("Abstract method", abstractMethods(Kendaraan.class));
        baris("Catatan", "Kendaraan tidak dapat diinstansiasi; hanya Mobil dan Motor.");

        // --- Persyaratan 8 ---
        judul(8, "Logika State Percabangan",
                "Implementasi abstract method memuat if/else yang membaca dan mengubah state internal objek.",
                "Mobil.prosesPengisian() dan Motor.prosesPengisian()");
        baris("Isi tangki awal", liter(mobil.getTotalLiter()) + " dari kapasitas " + liter(mobil.getMaksimalKapasitasBahanBakar()));
        System.out.println("-> Mobil isi 30 liter:");
        mobil.prosesPengisian(pertalite, 30);
        baris("Isi tangki sekarang", liter(mobil.getTotalLiter()) + " (state berubah)");
        System.out.println("-> Mobil isi 20 liter (melebihi sisa):");
        mobil.prosesPengisian(pertalite, 20);
        baris("Isi tangki sekarang", liter(mobil.getTotalLiter()) + " (state tidak berubah)");
        System.out.println("-> Motor isi Solar (ditolak oleh aturan motor):");
        motor.prosesPengisian(solar, 2);

        // --- Persyaratan 9 ---
        judul(9, "Polimorfisme Interface",
                "Pemanggilan method lewat variabel bertipe interface (NamaInterface obj = new Subclass();).",
                "Main.java: Pengisian pengisian1 = new Mobil(...); Pengisian pengisian2 = new Motor(...);");
        Pengisian pengisian1 = new Mobil("R 5555 EE", "Eko", 1500, 4, 5, 40);
        Pengisian pengisian2 = new Motor("R 6666 FF", "Fani", 125, 5);
        double total1 = pengisian1.prosesPengisian(pertalite, 10);
        baris("pengisian1 (objek Mobil)", String.format("10 liter Pertalite = Rp%,.0f", total1));
        double total2 = pengisian2.prosesPengisian(pertalite, 3);
        baris("pengisian2 (objek Motor)", String.format("3 liter Pertalite = Rp%,.0f", total2));

        // --- Persyaratan 10 ---
        judul(10, "Multiple Interface",
                "Minimal satu subclass mengimplementasikan minimal dua interface (implements A, B).",
                "Mobil.java dan Motor.java: implements Pengisian, Pembayaran");
        baris("Interface milik Mobil", namaInterface(Mobil.class));
        baris("Interface milik Motor", namaInterface(Motor.class));
        Pembayaran pembayaran = new Motor("R 7777 GG", "Gita", 110, 5);
        baris("Pembayaran via interface", "Pembayaran pembayaran = new Motor(...); pembayaran.bayar(30000)");
        pembayaran.bayar(30000);

        // --- Proses bisnis tambahan: subsidi BBM ---
        System.out.println("\n[Proses Bisnis] Subsidi BBM per Hari");
        System.out.println("Ketentuan : Solar: roda 4 maks 50 liter, roda 6 ke atas maks 200 liter. "
                + "Pertalite: mobil maks 50 liter, motor tanpa batas.");
        System.out.println("Lokasi    : Kendaraan.java dan Mobil.java (batasSubsidiPerHari), "
                + "SPBU.java (cekSubsidi, catatPengisian)");
        System.out.println("---------------------------------------------");
        SPBU spbuSubsidi = new SPBU("Purwokerto, Jawa Tengah", "SPBU-002");
        Mobil roda4 = new Mobil("R 8888 HH", "Hadi", 2000, 4, 5, 100);
        Mobil roda6 = new Mobil("R 9999 II", "Indra", 7000, 6, 3, 300);
        baris("Batas Solar roda 4", teksBatas(roda4.batasSubsidiPerHari(solar)));
        baris("Batas Solar roda 6", teksBatas(roda6.batasSubsidiPerHari(solar)));
        baris("Batas Pertalite mobil", teksBatas(roda4.batasSubsidiPerHari(pertalite)));
        baris("Batas Pertalite motor", teksBatas(motor.batasSubsidiPerHari(pertalite)));
        baris("Roda 5 valid?", Mobil.isJumlahRodaValid(5) + " (input roda diulang pada menu transaksi)");
        System.out.println("-> Roda 4 isi Solar 30 liter:");
        isiDenganSubsidi(spbuSubsidi, roda4, roda4, solar, 30);
        baris("Sisa subsidi roda 4", liter(spbuSubsidi.getSisaSubsidi(roda4, solar)));
        System.out.println("-> Roda 4 isi Solar 30 liter lagi (melebihi sisa):");
        isiDenganSubsidi(spbuSubsidi, roda4, roda4, solar, 30);
        System.out.println("-> Roda 4 isi Solar 20 liter (menghabiskan kuota):");
        isiDenganSubsidi(spbuSubsidi, roda4, roda4, solar, 20);
        System.out.println("-> Roda 4 isi Solar 5 liter (kuota sudah habis):");
        isiDenganSubsidi(spbuSubsidi, roda4, roda4, solar, 5);
        System.out.println("-> Roda 6 isi Solar 150 liter:");
        isiDenganSubsidi(spbuSubsidi, roda6, roda6, solar, 150);
        baris("Sisa subsidi roda 6", liter(spbuSubsidi.getSisaSubsidi(roda6, solar)));

        System.out.println("\n========== AKHIR DEMONSTRASI ==========");
    }

    // ---------- Helper tampilan demonstrasi ----------
    private static final String FORMAT_LABEL = "%-28s: ";

    private static void judul(int nomor, String nama, String ketentuan, String lokasi) {
        System.out.println("\n[Persyaratan " + nomor + "] " + nama);
        System.out.println("Ketentuan : " + ketentuan);
        System.out.println("Lokasi    : " + lokasi);
        System.out.println("---------------------------------------------");
    }

    private static void baris(String label, String nilai) {
        System.out.printf(FORMAT_LABEL + "%s%n", label, nilai);
    }

    private static String teksBatas(double batas) {
        return Double.isInfinite(batas) ? "tanpa batas" : liter(batas) + " per hari";
    }

    private static String liter(double nilai) {
        return String.format("%.2f liter", nilai);
    }

    // Mengambil modifier atribut langsung dari class (bukti dari program, bukan teks).
    private static String modifier(Class<?> kelas, String namaAtribut) {
        try {
            return java.lang.reflect.Modifier.toString(kelas.getDeclaredField(namaAtribut).getModifiers());
        } catch (NoSuchFieldException e) {
            return "tidak ditemukan";
        }
    }

    private static String abstractMethods(Class<?> kelas) {
        StringBuilder hasil = new StringBuilder();
        for (java.lang.reflect.Method method : kelas.getDeclaredMethods()) {
            if (java.lang.reflect.Modifier.isAbstract(method.getModifiers())) {
                if (hasil.length() > 0) hasil.append(", ");
                hasil.append(method.getName()).append("(...)");
            }
        }
        return hasil.length() == 0 ? "(tidak ada)" : hasil.toString();
    }

    private static String namaInterface(Class<?> kelas) {
        StringBuilder hasil = new StringBuilder();
        for (Class<?> antarmuka : kelas.getInterfaces()) {
            if (hasil.length() > 0) hasil.append(", ");
            hasil.append(antarmuka.getSimpleName());
        }
        return hasil.toString();
    }

    // =====================================================================
    //  Helper input (mengulang sampai valid, tidak crash)
    // =====================================================================
    private static String bacaTeks(String prompt) {
        String teks;
        do {
            System.out.print(prompt);
            teks = input.nextLine().trim();
            if (teks.isEmpty()) System.out.println("Input tidak boleh kosong.");
        } while (teks.isEmpty());
        return teks;
    }

    private static int bacaInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String baris = input.nextLine().trim();
            try {
                return Integer.parseInt(baris);
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa bilangan bulat. Silakan coba kembali.");
            }
        }
    }

    private static int bacaPilihan(String prompt, int min, int maks) {
        while (true) {
            int nilai = bacaInt(prompt);
            if (nilai >= min && nilai <= maks) return nilai;
            System.out.println("Pilihan tidak tersedia. Silakan pilih kembali.");
        }
    }

    private static int bacaRodaMobil() {
        while (true) {
            int roda = bacaInt("Masukkan jumlah roda mobil (4, atau 6 ke atas): ");
            if (Mobil.isJumlahRodaValid(roda)) return roda;
            System.out.println("Jumlah roda tidak valid. Gunakan 4, atau 6 ke atas.");
        }
    }

    private static int bacaIntPositif(String prompt) {
        while (true) {
            int nilai = bacaInt(prompt);
            if (nilai > 0) return nilai;
            System.out.println("Nilai harus lebih dari 0.");
        }
    }

    private static double bacaDoubleRentang(String prompt, double maks) {
        while (true) {
            System.out.print(prompt);
            String baris = input.nextLine().trim().replace(',', '.');
            try {
                double nilai = Double.parseDouble(baris);
                if (nilai >= 0 && nilai <= maks) return nilai;
                System.out.printf("Nilai harus antara 0 dan %.2f liter (kapasitas tangki).%n", maks);
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka. Silakan coba kembali.");
            }
        }
    }

    // Menerima angka >= 0. Nilai 0 dipakai pengguna untuk mengganti pilihan BBM.
    private static double bacaLiterAtauGanti(String prompt) {
        while (true) {
            System.out.print(prompt);
            String baris = input.nextLine().trim().replace(',', '.');
            try {
                double nilai = Double.parseDouble(baris);
                if (nilai >= 0 && Double.isFinite(nilai)) return nilai;
                System.out.println("Nilai tidak boleh negatif. Masukkan 0 untuk ganti BBM.");
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka. Silakan coba kembali.");
            }
        }
    }

    private static double bacaDoublePositif(String prompt) {
        while (true) {
            System.out.print(prompt);
            String baris = input.nextLine().trim().replace(',', '.');
            try {
                double nilai = Double.parseDouble(baris);
                if (nilai > 0 && Double.isFinite(nilai)) return nilai;
                System.out.println("Nilai harus lebih dari 0.");
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka. Silakan coba kembali.");
            }
        }
    }
}
