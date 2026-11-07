package Proyek_SPBU_Responsi_PBO;

public class Motor extends Kendaraan implements Pengisian, Pembayaran {
    private int kapasitasMesin;
    private static final int KAPASITAS_PENUMPANG = 2;
    private static final int JUMLAH_RODA = 2;

    public Motor(String platNomor, String namaPemilik, String idKendaraan,
                 int kapasitasMesin, double maksimalKapasitasBahanBakar) {
        super(platNomor, namaPemilik, idKendaraan, maksimalKapasitasBahanBakar);
        setKapasitasMesin(kapasitasMesin);
    }

    public Motor(String platNomor, String namaPemilik, int kapasitasMesin,
                 double maksimalKapasitasBahanBakar) {
        this(platNomor, namaPemilik, platNomor, kapasitasMesin,
             maksimalKapasitasBahanBakar);
    }

    public int getKapasitasMesin() { return kapasitasMesin; }
    public void setKapasitasMesin(int kapasitasMesin) {
        if (kapasitasMesin > 0) this.kapasitasMesin = kapasitasMesin;
        else throw new IllegalArgumentException("Kapasitas mesin harus lebih dari 0.");
    }

    public int getKapasitasPenumpang() { return KAPASITAS_PENUMPANG; }
    public int getJumlahRoda() { return JUMLAH_RODA; }

    // Aturan khusus motor: tidak boleh memakai BBM diesel (Solar).
    @Override
    public boolean bolehMemakai(BahanBakar bahanBakar) {
        return super.bolehMemakai(bahanBakar) && !bahanBakar.isDiesel();
    }

    @Override
    public String getAlasanPenolakan(BahanBakar bahanBakar) {
        if (bahanBakar != null && bahanBakar.isDiesel()) {
            return "motor tidak dapat memakai Solar.";
        }
        return super.getAlasanPenolakan(bahanBakar);
    }

    // Implementasi abstract method Kendaraan sekaligus kontrak interface Pengisian.
    @Override
    public double prosesPengisian(BahanBakar bahanBakar, double liter) {
        if (!dataPengisianValid(bahanBakar, liter)) {
            System.out.println("Pengisian gagal: data BBM/liter tidak valid.");
            return 0;
        }
        if (!bolehMemakai(bahanBakar)) {
            System.out.println("Pengisian gagal: " + getAlasanPenolakan(bahanBakar));
            return 0;
        } else if (getTotalLiter() + liter > getMaksimalKapasitasBahanBakar()) {
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
            System.out.printf("Pembayaran motor sebesar Rp%,.0f diterima.%n", jumlah);
            return true;
        }
        System.out.println("Pembayaran tidak valid.");
        return false;
    }

    @Override
    public void cekKendaraan() {
        super.cekKendaraan();
        System.out.println("Kapasitas mesin: " + kapasitasMesin + " cc");
        System.out.println("Jumlah roda: " + JUMLAH_RODA + " (tetap)");
        System.out.println("Kapasitas penumpang: " + KAPASITAS_PENUMPANG + " orang (tetap)");
        System.out.printf("Maksimal kapasitas bahan bakar: %.2f liter%n",
                          getMaksimalKapasitasBahanBakar());
    }
}
