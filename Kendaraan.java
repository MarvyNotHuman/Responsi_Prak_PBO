package Proyek_SPBU_Responsi_PBO;

public abstract class Kendaraan {
    private String platNomor;
    private String namaPemilik;
    private double totalLiter;
    private final String idKendaraan;
    private double maksimalKapasitasBahanBakar;

    public Kendaraan(String platNomor, String namaPemilik, String idKendaraan,
                     double maksimalKapasitasBahanBakar) {
        this.platNomor = platNomor;
        this.namaPemilik = namaPemilik;
        this.idKendaraan = idKendaraan;
        setMaksimalKapasitasBahanBakar(maksimalKapasitasBahanBakar);
        this.totalLiter = 0;
    }

    public Kendaraan(String platNomor, String namaPemilik,
                     double maksimalKapasitasBahanBakar) {
        this(platNomor, namaPemilik, platNomor, maksimalKapasitasBahanBakar);
    }

    public String getPlatNomor() { return platNomor; }
    public void setPlatNomor(String platNomor) { this.platNomor = platNomor; }
    public String getNamaPemilik() { return namaPemilik; }
    public void setNamaPemilik(String namaPemilik) { this.namaPemilik = namaPemilik; }
    public double getTotalLiter() { return totalLiter; }
    public void setTotalLiter(double totalLiter) {
        if (totalLiter >= 0 && totalLiter <= maksimalKapasitasBahanBakar) {
            this.totalLiter = totalLiter;
        }
    }
    public String getIdKendaraan() { return idKendaraan; }
    public double getMaksimalKapasitasBahanBakar() {
        return maksimalKapasitasBahanBakar;
    }
    public void setMaksimalKapasitasBahanBakar(double kapasitas) {
        if (kapasitas > 0 && Double.isFinite(kapasitas)) {
            maksimalKapasitasBahanBakar = kapasitas;
        } else {
            throw new IllegalArgumentException("Kapasitas tangki harus lebih dari 0.");
        }
    }

    // ABSTRACT METHOD: aturan pengisian berbeda untuk tiap jenis kendaraan,
    // sehingga diimplementasikan oleh subclass (Mobil dan Motor).
    // Mengembalikan total harga, atau 0 jika pengisian gagal.
    public abstract double prosesPengisian(BahanBakar bahanBakar, double liter);

    // Validasi dasar yang dipakai bersama oleh subclass.
    protected boolean dataPengisianValid(BahanBakar bahanBakar, double liter) {
        return bahanBakar != null && liter > 0 && Double.isFinite(liter);
    }

    // Aturan umum: kendaraan boleh memakai semua BBM yang valid.
    // Subclass dapat menimpa method ini untuk membatasi jenis BBM.
    public boolean bolehMemakai(BahanBakar bahanBakar) {
        return bahanBakar != null;
    }

    // Alasan penolakan yang ditampilkan jika bolehMemakai() bernilai false.
    public String getAlasanPenolakan(BahanBakar bahanBakar) {
        return "BBM ini tidak dapat dipakai oleh kendaraan Anda.";
    }

    // Batas subsidi per hari (liter) untuk BBM tertentu. Default: tanpa batas.
    // Subclass dapat menimpa method ini sesuai aturan jenis kendaraannya.
    public double batasSubsidiPerHari(BahanBakar bahanBakar) {
        return Double.POSITIVE_INFINITY;
    }

    public double getSisaKapasitas() {
        return maksimalKapasitasBahanBakar - totalLiter;
    }

    public void tampilkanInfo() {
        System.out.println("Kendaraan : " + getClass().getSimpleName());
        System.out.println("Plat      : " + platNomor);
        System.out.println("Pemilik   : " + namaPemilik);
        System.out.printf("Isi tangki: %.2f liter%n", totalLiter);
        System.out.printf("Kapasitas tangki: %.2f liter%n", maksimalKapasitasBahanBakar);
    }

    public void cekKendaraan() {
        System.out.println("Kendaraan " + platNomor + " siap diperiksa.");
    }
}
