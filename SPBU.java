package Proyek_SPBU_Responsi_PBO;

public class SPBU {
    public static final String NAMA_SPBU = "SPBU UNSOED";
    public static final double PAJAK = 0.0; // Tidak ada pajak tambahan dalam simulasi

    private String lokasi;
    private final String kodeSPBU;
    // Catatan pemakaian BBM bersubsidi per plat dan jenis BBM (disimpan di memori).
    private final java.util.Map<String, Double> subsidiTerpakai;

    public SPBU(String lokasi, String kodeSPBU) {
        this.lokasi = lokasi;
        this.kodeSPBU = kodeSPBU;
        this.subsidiTerpakai = new java.util.HashMap<>();
    }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public String getKodeSPBU() { return kodeSPBU; }

    public double getSubsidiTerpakai(Kendaraan kendaraan, BahanBakar bahanBakar) {
        return subsidiTerpakai.getOrDefault(kunci(kendaraan, bahanBakar), 0.0);
    }

    // Sisa subsidi hari ini = batas kendaraan untuk BBM tersebut - yang sudah terpakai.
    public double getSisaSubsidi(Kendaraan kendaraan, BahanBakar bahanBakar) {
        double sisa = kendaraan.batasSubsidiPerHari(bahanBakar)
                - getSubsidiTerpakai(kendaraan, bahanBakar);
        return Math.max(0, sisa);
    }

    // Langkah 1 pengisian: cek kuota subsidi. Jika tidak cukup, alasan dicetak dan hasilnya false.
    public boolean cekSubsidi(Kendaraan kendaraan, BahanBakar bahanBakar, double liter) {
        double sisa = getSisaSubsidi(kendaraan, bahanBakar);
        if (liter <= sisa + 0.000001) {
            return true;
        } else if (sisa <= 0.000001) {
            System.out.printf("Pengisian gagal: kuota subsidi %s hari ini sudah habis. "
                    + "Silakan isi kembali besok.%n", bahanBakar.getNama());
            return false;
        } else {
            System.out.printf("Pengisian gagal: melebihi sisa kuota subsidi %s hari ini (sisa %.2f liter).%n",
                    bahanBakar.getNama(), sisa);
            return false;
        }
    }

    // Dipanggil setelah pengisian berhasil.
    public void catatPengisian(Kendaraan kendaraan, BahanBakar bahanBakar, double liter) {
        if (bahanBakar.isBersubsidi()) {
            subsidiTerpakai.merge(kunci(kendaraan, bahanBakar), liter, Double::sum);
        }
    }

    // Spasi dan huruf besar/kecil pada plat diabaikan: "R 1 AB" sama dengan "r1ab".
    private String kunci(Kendaraan kendaraan, BahanBakar bahanBakar) {
        return kendaraan.getPlatNomor().replaceAll("\\s+", "").toUpperCase()
                + "|" + bahanBakar.getNama();
    }

    public void tampilkanInfo() {
        System.out.println("\n=== " + NAMA_SPBU + " ===");
        System.out.println("Kode SPBU : " + kodeSPBU);
        System.out.println("Lokasi    : " + lokasi);
    }
}
