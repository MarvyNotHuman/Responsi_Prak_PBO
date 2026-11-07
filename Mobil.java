package Proyek_SPBU_Responsi_PBO;

public class Mobil extends Kendaraan implements Pengisian, Pembayaran {
    // Batas subsidi per hari (liter)
    public static final double BATAS_SUBSIDI_SOLAR_RODA_4 = 50;
    public static final double BATAS_SUBSIDI_SOLAR_RODA_6_KE_ATAS = 200;
    public static final double BATAS_SUBSIDI_PERTALITE = 50; // semua mobil, berapa pun rodanya

    private int kapasitasMesin;
    private int jumlahRoda;
    private int maksimalPenumpang;

    public Mobil(String platNomor, String namaPemilik, String idKendaraan,
                 int kapasitasMesin, int jumlahRoda, int maksimalPenumpang,
                 double maksimalKapasitasBahanBakar) {
        super(platNomor, namaPemilik, idKendaraan, maksimalKapasitasBahanBakar);
        setKapasitasMesin(kapasitasMesin);
        setJumlahRoda(jumlahRoda);
        setMaksimalPenumpang(maksimalPenumpang);
    }

    public Mobil(String platNomor, String namaPemilik, int kapasitasMesin,
                 int jumlahRoda, int maksimalPenumpang,
                 double maksimalKapasitasBahanBakar) {
        this(platNomor, namaPemilik, platNomor, kapasitasMesin, jumlahRoda,
             maksimalPenumpang, maksimalKapasitasBahanBakar);
    }

    public int getKapasitasMesin() { return kapasitasMesin; }
    public void setKapasitasMesin(int kapasitasMesin) {
        if (kapasitasMesin > 0) this.kapasitasMesin = kapasitasMesin;
        else throw new IllegalArgumentException("Kapasitas mesin harus lebih dari 0.");
    }

    // Jumlah roda valid: 4, atau 6 ke atas.
    public static boolean isJumlahRodaValid(int roda) {
        return roda == 4 || roda >= 6;
    }

    public int getJumlahRoda() { return jumlahRoda; }
    public void setJumlahRoda(int jumlahRoda) {
        if (isJumlahRodaValid(jumlahRoda)) this.jumlahRoda = jumlahRoda;
        else throw new IllegalArgumentException("Jumlah roda tidak valid. Gunakan 4, atau 6 ke atas.");
    }

    public int getMaksimalPenumpang() { return maksimalPenumpang; }
    public void setMaksimalPenumpang(int maksimalPenumpang) {
        if (maksimalPenumpang > 0) this.maksimalPenumpang = maksimalPenumpang;
        else throw new IllegalArgumentException("Jumlah penumpang harus lebih dari 0.");
    }

    // Aturan subsidi mobil (menimpa aturan umum "tanpa batas" di Kendaraan).
    @Override
    public double batasSubsidiPerHari(BahanBakar bahanBakar) {
        if (!bahanBakar.isBersubsidi()) {
            return super.batasSubsidiPerHari(bahanBakar);
        } else if (bahanBakar.isDiesel()) {
            if (jumlahRoda >= 6) {
                return BATAS_SUBSIDI_SOLAR_RODA_6_KE_ATAS;
            } else {
                return BATAS_SUBSIDI_SOLAR_RODA_4;
            }
        } else {
            return BATAS_SUBSIDI_PERTALITE;
        }
    }

    // Implementasi abstract method Kendaraan sekaligus kontrak interface Pengisian.
    // Logika if/else membaca dan mengubah state (totalLiter).
    @Override
    public double prosesPengisian(BahanBakar bahanBakar, double liter) {
        if (!dataPengisianValid(bahanBakar, liter)) {
            System.out.println("Pengisian gagal: data BBM/liter tidak valid.");
            return 0;
        }
        if (getTotalLiter() + liter > getMaksimalKapasitasBahanBakar()) {
            System.out.printf("Pengisian gagal: sisa kapasitas tangki hanya %.2f liter.%n",
                              getSisaKapasitas());
            return 0;
        } else {
            setTotalLiter(getTotalLiter() + liter);
            System.out.println("Pengisian berhasil.");
            return bahanBakar.hitungHarga(liter);
        }
    }

    @Override
    public boolean bayar(double jumlah) {
        if (jumlah > 0) {
            System.out.printf("Pembayaran mobil sebesar Rp%,.0f diterima.%n", jumlah);
            return true;
        }
        System.out.println("Pembayaran tidak valid.");
        return false;
    }

    @Override
    public void cekKendaraan() {
        super.cekKendaraan();
        System.out.println("Kapasitas mesin: " + kapasitasMesin + " cc");
        System.out.println("Jumlah roda: " + jumlahRoda);
        System.out.println("Maksimal penumpang: " + maksimalPenumpang + " orang");
        System.out.printf("Maksimal kapasitas bahan bakar: %.2f liter%n",
                          getMaksimalKapasitasBahanBakar());
    }
}
