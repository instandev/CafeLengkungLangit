package com.baguslangit.cafelengkunglangit;

public class Note {
    private String nama;
    private String jumlah;
    private String satuan;
    private String jaminput;
    private String harga;
    private String tanggalinput;
    private String kodebarang;
    private String kategori;
    private String hargatotal;
    private String orderid;
    private String disc;
    private String disc10;
    private String doc;
    private String hargaasli;
    private String status;
    private String total;
    private String jumlahorder;
    private String print;


    public Note() {
        //empty constructor needed
    }

    public Note(String nama,
                String jumlah,
                String satuan,
                String jaminput,
                String harga,
                String tanggalinput,
                String kodebarang,
                String kategori,
                String hargatotal,
                String orderid,
                String disc,
                String disc10,
                String doc,
                String hargaasli,
                String status,
                String total,
                String jumlahorder,
                String print
    ) {
        this.nama = nama;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.jaminput = jaminput;
        this.harga = harga;
        this.tanggalinput = tanggalinput;
        this.kodebarang = kodebarang;
        this.kategori = kategori;
        this.hargatotal = hargatotal;
        this.disc10 = disc10;
        this.doc = doc;
        this.orderid = orderid;
        this.disc = disc;
        this.hargaasli = hargaasli;
        this.status = status;
        this.total = total;
        this.jumlahorder = jumlahorder;
        this.print = print;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getPrint() {
        return print;
    }

    public void setPrint(String print) {
        this.print = print;
    }

    public String getJumlahorder() {
        return jumlahorder;
    }

    public void setJumlahorder(String jumlahorder) {
        this.jumlahorder = jumlahorder;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisc() {
        return disc;
    }

    public void setDisc(String disc) {
        this.disc = disc;
    }

    public String getDisc10() {
        return disc10;
    }

    public void setDisc10(String disc10) {
        this.disc10 = disc10;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getHargaasli() {
        return hargaasli;
    }

    public void setHargaasli(String hargaasli) {
        this.hargaasli = hargaasli;
    }

    public String getNama() {
        return nama.toLowerCase();
    }

    public void setNama(String nama) {
        this.nama = nama.toLowerCase();
    }

    public String getHargatotal() {
        return hargatotal;
    }

    public void setHargatotal(String hargatotal) {
        this.hargatotal = hargatotal;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKodebarang() {
        return kodebarang;
    }

    public void setKodebarang(String kodebarang) {
        this.kodebarang = kodebarang;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getJaminput() {
        return jaminput;
    }

    public void setJaminput(String jaminput) {
        this.jaminput = jaminput;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getTanggalinput() {
        return tanggalinput;
    }

    public void setTanggalinput(String tanggalinput) {
        this.tanggalinput = tanggalinput;
    }

}

class menu{
    private String nama;
    private String harga;

    public menu(String nama, String harga){
        this.nama = nama;

        this.harga = harga;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
