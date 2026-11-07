package Proyek_SPBU_Responsi_PBO;

public class BahanBakar {
    private String nama;
    private double hargaPerLiter;
    private final boolean diesel;      // jenis BBM tidak berubah setelah dibuat
    private final boolean bersubsidi;  // status subsidi tidak berubah setelah dibuat

    public BahanBakar(String nama, double hargaPerLiter, boolean diesel, boolean bersubsidi) {
        this.nama = nama;
        this.hargaPerLiter = hargaPerLiter;
        this.diesel = diesel;
        this.bersubsidi = bersubsidi;
    }

    // BBM biasa: bukan diesel dan tidak bersubsidi
    public BahanBakar(String nama, double hargaPerLiter) {
        this(nama, hargaPerLiter, false, false);
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public double getHargaPerLiter() { return hargaPerLiter; }
    public void setHargaPerLiter(double hargaPerLiter) {
        if (hargaPerLiter >= 0) this.hargaPerLiter = hargaPerLiter;
    }
    public boolean isDiesel() { return diesel; }
    public boolean isBersubsidi() { return bersubsidi; }

    public double hitungHarga(double liter) {
        return liter * hargaPerLiter;
    }

    public void tampilkanInfo() {
        System.out.printf("%s - Rp%,.0f/liter%s%n", nama, hargaPerLiter,
                bersubsidi ? " (subsidi)" : "");
    }
}
