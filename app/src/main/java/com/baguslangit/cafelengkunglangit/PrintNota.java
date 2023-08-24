package com.baguslangit.cafelengkunglangit;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PrintNota extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;

    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;

    InputStream inputStream;
    Thread thread;
    CoursesLVAdapter adapterlv;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    double jmlh, discttl;
    double jmlh1 = 0;
    double ttlhrg = 0;
    double discttl1 = 0;
    boolean dell, disc10c, disc20c;
    double juml1 = 0, juml = 0;

    TextView coba1, coba, printer, print, totaluang, jumlah, prin, nomorantri, nama;
    String jumlahprint, docu, doctgl, hargaprahu;
    LinearLayout lprint, prahukaca;

    StringBuilder coba2 = new StringBuilder();

    FirebaseFirestore db;
    ListView coursesLV;
    ViewLoadingDialog loadingDialog;
    String id, namaid, strnama, paksa, tanggalinput, num, num1, num2, jaminput, jam, jamc, jumlahtiket, msg, tgl, tgl1, status, jamin, asal, doc, dcc, nmdic, stsprint;
    ArrayList<Note> dataModalArrayList;
    SimpleDateFormat df, df1, df2, df3, df4, df5, df6;
    byte[] image;
    String lokasi, cafe, alamat, ttll, pajak;
    TextView home, plus, riwayat, disc20rb;
    double hr, tl, jm, dc2, dc1, dc3, ttl, jum;
    String ttlu;
    String ttlua;
    String ttlub;
    TextView jumlahsub;
    TextView totaluangsub;
    TextView disc;
    TextView totaluangdisc;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_nota);
        coursesLV = (ListView) findViewById(R.id.list_view);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        namaid = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());
        dataModalArrayList = new ArrayList<>();
        printer = (TextView) findViewById(R.id.printer);
        print = (TextView) findViewById(R.id.print);
        totaluang = (TextView) findViewById(R.id.totaluang);
        jumlah = (TextView) findViewById(R.id.jumlah1);
        coba = (TextView) findViewById(R.id.coba);
        coba1 = (TextView) findViewById(R.id.coba1);
        loadingDialog = new ViewLoadingDialog(PrintNota.this);
        home = (TextView) findViewById(R.id.home);
        plus = (TextView) findViewById(R.id.plus);
        riwayat = (TextView) findViewById(R.id.riwayat);
        prin = (TextView) findViewById(R.id.prin);
        jumlah = (EditText) findViewById(R.id.jumlah);
        nomorantri = (TextView) findViewById(R.id.nomorantri);
        prahukaca = (LinearLayout) findViewById(R.id.prahukaca);
        nama = (TextView) findViewById(R.id.nama);
        disc20rb = findViewById(R.id.dis20rb);
        jumlahsub = (TextView) findViewById(R.id.jumlahsub);
        totaluangsub = (TextView) findViewById(R.id.totaluangsub);
        disc = (TextView) findViewById(R.id.disc);
        totaluangdisc = (TextView) findViewById(R.id.totaluangdisc);

        //print.setVisibility(View.GONE);

        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df6 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        df1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        df2 = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        df3 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        df4 = new SimpleDateFormat("HH-mm-ss-SSS", Locale.getDefault());
        df5 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tanggalinput = df.format(new Date());
        tgl = df2.format(new Date());
        tgl1 = df3.format(new Date());
        jaminput = df4.format(new Date());
        doctgl = df5.format(new Date());

        asal = "";
        Intent intent = getIntent();
        strnama = intent.getStringExtra("orderid");
        asal = intent.getStringExtra("asal");
        hargaprahu = intent.getStringExtra("harga");




        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        loadDatasemua();
        getTenant();
        if (namaid.equals("cafell2atas@baguslangit.com")) {
            prahukaca.setVisibility(View.VISIBLE);
        }

        if (!namaid.equals("wahanatembakll1@baguslangit.com")) {
            if (!namaid.equals("wahanatembakll2@baguslangit.com")) {
                if (btsocket == null) {
                    Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
                    this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
                }
            }
        }




        if (asal.equals("histori")) {
            String jp = intent.getStringExtra("print");
            docu = intent.getStringExtra("doc");
            double djp = Double.parseDouble(jp) + 1;
            jumlahprint = String.format("%.0f", djp);
            tanggalinput = intent.getStringExtra("tanggalinput");
            TextView order = (TextView) findViewById(R.id.order);
            order.setText(strnama);

            loadDatainListview();
        } else {
            jumlahprint = "1";
            getDataorder();
        }

        switch (namaid) {
            case "restoll2@baguslangit.com":
                if(asal.equals("histori")){
                    disc20rb.setVisibility(View.GONE);
                }else {
                    disc20rb.setVisibility(View.VISIBLE);
                }
                break;
            default:
                disc20rb.setVisibility(View.GONE);
                break;
        }


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print.setVisibility(View.GONE);
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                dell = true;

                if (!namaid.equals("wahanatembakll1@baguslangit.com")) {
                    if (!namaid.equals("wahanatembakll2@baguslangit.com")) {
                        try {
                            printBill();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        if (!asal.equals("histori")) {
                            upData();
                            setJumlahTransaksi();
                        } else {
                            upDataprint();
                        }
                    }
                } else {
                    if (!asal.equals("histori")) {
                        upData();
                        setJumlahTransaksi();
                    } else {
                        upDataprint();
                    }
                }
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(PrintNota.this, MainActivity.class);
                startActivity(home);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(PrintNota.this, PrintNota.class);
                home.putExtra("orderid", "");
                home.putExtra("asal", "");
                startActivity(home);
            }
        });
        riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(PrintNota.this, histori.class);
                startActivity(home);
            }
        });

        prin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tanggalinput = df.format(new Date());
                jaminput = df1.format(new Date());
                jamc = df3.format(new Date());
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                jumlahtiket = jumlah.getText().toString();
                jmlh = Double.parseDouble(jumlahtiket);
                double hrg = Double.parseDouble(hargaprahu);
                ttl = jmlh * hrg;
                Random rnd = new Random();
                Random rnd1 = new Random();
                Random rnd2 = new Random();
                int number = 100 + rnd.nextInt(899);
                int number1 = 100 + rnd1.nextInt(899);
                int number2 = 100 + rnd2.nextInt(899);
                num = String.valueOf(number);
                num1 = String.valueOf(number1);
                num2 = String.valueOf(number2);
                strnama = "PK-" + num + num1 + num2;
                printBill1();

            }
        });



    }

    private void getorderid(){
        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .whereEqualTo("orderid", strnama)
                .whereEqualTo("tanggalinput", tanggalinput)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String disc20rb = doc.getString("disc");

                                discttl1 = Double.parseDouble(Objects.requireNonNull(disc20rb));
                            if(discttl1>0) {
                                totaluang.setText(formatRupiah(ttlhrg - discttl1));
                                disc.setText("Disc ");
                                discttl1 = 20000;
                                totaluangdisc.setText(formatRupiah(discttl1));
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void getTenant() {
        db.collection("admin")
                .document(namaid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.getString("nama") != null) {
                                    cafe = document.getString("nama");
                                    lokasi = document.getString("lokasi");
                                    pajak = document.getString("pajak");

                                    if (lokasi.equals("Lengkung Langit 1")) {
                                        alamat = "Pinang Jaya, Kemiling,";
                                        nama.setText(cafe);
                                    } else if (lokasi.equals("Lengkung Langit 2")) {
                                        alamat = "Sumber Agung, Kemiling,";
                                        nama.setText(cafe);
                                    }else {
                                        alamat = "Kec. Kemiling";
                                        nama.setText(lokasi);
                                    }

                                    if (asal.equals("histori")) {
                                        msg = "\n--------------------------------\n" + lokasi + "\n" + cafe + "\n" + alamat + "\nBandar Lampung\n--------------------------------\n" + "ID:" + strnama + "\n\n";
                                        String cb1 = "\n--------------------------------\n" + "ID:" + strnama + "\n\n";
                                        String cb2 = "\n----------------\n" + "ID:" + strnama + "\n\n";


                                        //coba.setText(msg);
                                        //coba1.setText(cb1);
                                        //coba2.append(cb1);
                                        coba.setText("--------------------For Customer" + msg);
                                        coba1.setText("-----------------------For Admin" + cb1);
                                        coba2.append("-----For Kitchen" + cb2 + "\n\n");
                                    }
                                } else {
                                    if (checkInternet()) {
                                        Toast.makeText(PrintNota.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                        loadingDialog.dismiss();
                                    } else {
                                        Toast.makeText(PrintNota.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                        loadingDialog.dismiss();
                                    }
                                }
                            } else {
                                if (checkInternet()) {
                                    Toast.makeText(PrintNota.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                } else {
                                    Toast.makeText(PrintNota.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                }
                            }
                        } else {
                            if (checkInternet()) {
                                Toast.makeText(PrintNota.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(PrintNota.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });

    }

    protected void printBill1() {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        } else {
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();
                byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                outputStream.write(printformat);

                printCustom("Lengkung Langit 2", 2, 1);
                printCustom("Sumber Agung, Kemiling,", 1, 1);
                printCustom("Bandar Lampung", 1, 1);
                printCustom("PK-" + num + num1 + num2, 1, 1);
                printCustom("--------------------------------", 1, 1);
                printCustom("NOMOR  ANTRI", 3, 1);
                printCustom(String.format("%.0f", jum + 1), 3, 1);
                printCustom("Tiket Prahu Kaca", 3, 1);
                printCustom("--------------------------------\n", 1, 1);
                printCustom("Tiket ini berlaku untuk 1 Kali", 1, 1);
                printCustom("Harga  : " + formatRupiah(Double.valueOf(hargaprahu)), 1, 0);
                printCustom("Jumlah : " + String.format("%.0f", jmlh), 1, 0);
                printCustom("Total  : " + formatRupiah(ttl), 1, 0);
                String msg3 = "\n-----" + tanggalinput + "----" + jaminput + "-----" + "\n";
                printCustom(msg3, 1, 1);
                upData1();
                //jumlahTiket();
                printCustom("Thank you for coming & we look", 1, 1);
                printCustom("forward to serve you again\n\n", 1, 1);
                printNewLine();
                printNewLine();

                outputStream.flush();
                jumlah.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upData1() {
        tanggalinput = df.format(new Date());
        jaminput = df3.format((new Date()));
        String jmlh = jumlah.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("nama", "prahu kaca");
        user.put("harga", "5000");
        user.put("jumlah", jmlh);
        user.put("jml", "1");
        user.put("orderid", strnama);
        user.put("tanggalinput", tanggalinput);
        user.put("jaminput", jaminput);
        user.put("doc", tanggalinput + "-" + jamc);
        user.put("total", ttl);

        db.collection(namaid)
                .document(id)
                .collection("prahukaca")
                .document(tanggalinput + "-" + jamc)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        loadingDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void loadDatasemua() {
        String tgl = df6.format(new Date());
        db.collection(namaid)
                .document(id)
                .collection("prahukaca")
                .whereEqualTo("tanggalinput", tgl)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        jum = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            double j = Double.parseDouble(Objects.requireNonNull(doc.getString("jml")));
                            jum = jum + j;
                        }
                        nomorantri.setText("Nomor antri terakhir : " + String.format("%.0f", jum));
                    }
                });
    }


    private void getDataorder() {
        db.collection(namaid)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                strnama = document.getString("strnama");
                                paksa = document.getString("paksa");
                                status = document.getString("status-order");
                                ttlhrg = 0;
                                jmlh1 = 0;
                                discttl1 = 0;
                                dc2 = 0;
                                dc3 = 0;
                                if (!paksa.equals("login")) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent home = new Intent(PrintNota.this, login.class);
                                    startActivity(home);

                                }
                                stsprint = document.getString("status-order");
                                assert stsprint != null;
                                if (stsprint.equals("print")) {
                                    strnama = "0";

                                }

                                TextView order = (TextView) findViewById(R.id.order);
                                order.setText(strnama);
                                msg = "--------------------------------\n" + lokasi + "\n" + cafe + "\n" + alamat + "\nBandar Lampung\n--------------------------------\n" + "ID:" + strnama + "\n\n";
                                String cb1 = "\n--------------------------------\n" + "ID:" + strnama + "\n\n";
                                String cb2 = "\n----------------\n" + "ID:" + strnama + "\n\n";

                                coba.setText("--------------------For Customer" + msg);
                                coba1.setText("-----------------------For Admin" + cb1);
                                coba2.append("-----For Kitchen" + cb2 + "\n\n");
                                loadDatainListview();
                            } else {
                                strnama = "LL001";
                            }
                        } else {
                            if (checkInternet()) {
                                Toast.makeText(PrintNota.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(PrintNota.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private void loadDatainListview() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        loadingDialog.setPesan("Mohon Tunggu Sebentar");
        loadingDialog.show();
        db.collection(namaid)
                .document(id)
                .collection("order")
                .whereEqualTo("orderid", strnama)
                .whereEqualTo("tanggalinput", tanggalinput)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.

                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding
                            // our progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Note dataModal = d.toObject(Note.class);

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                dataModalArrayList.add(dataModal);


                                discttl = 0;
                                if (dataModal.getDisc() != null) {
                                    discttl = Double.parseDouble(dataModal.getDisc());
                                }
                                hr = Double.parseDouble(dataModal.getHarga());
                                tl = Double.parseDouble(dataModal.getTotal());
                                jm = Double.parseDouble(dataModal.getJumlah());
                                String sub = String.format("%.0f", hr * jm);
                                String ttla = formatRupiah(hr * jm);
                                if (sub.length() == 4) {
                                    if (ttla.length() == 7) {
                                        ttll = ttla;
                                    } else {
                                        ttll = ttla.substring(0, 7);
                                    }
                                } else if (sub.length() == 5) {
                                    if (ttla.length() == 8) {
                                        ttll = ttla;
                                    } else {
                                        ttll = ttla.substring(0, 8);
                                    }
                                } else if (sub.length() == 6) {
                                    if (ttla.length() == 9) {
                                        ttll = ttla;
                                    } else {
                                        ttll = ttla.substring(0, 9);
                                    }
                                } else if (sub.length() == 7) {
                                    if (ttla.length() == 10) {
                                        ttll = ttla;
                                    } else {
                                        ttll = ttla.substring(0, 10);
                                    }
                                }
                                String nm = dataModal.getNama();
                                String nm1 = dataModal.getJumlah();
                                int lg = nm.length();
                                if(lg>16){
                                    nm = nm.substring(0,16);
                                    lg = nm.length();
                                }
                                int lg1 = ttll.length();
                                int lg2 = nm1.length();

                                jmlh = Double.parseDouble(nm1);


                                String sp = "                 ";//17carater
                                String sp1 = "          ";//10carater
                                String sp2 = "   ";//3carater
                                String nmb = sp.substring(lg);
                                String nmg = nm + nmb;
                                String nmb1 = sp1.substring(lg1);
                                String nmg1 = ttll + nmb1;
                                String nmb2 = sp2.substring(lg2);
                                String nmg2 = nm1 + nmb2;

                                String cet = nmg2 + nmg + "  " + nmg1 + "\n";
                                String cet1 = nmg2 + nmg + "\n";
                                double jmtl = jm * hr;
                                coba.append(cet);
                                coba1.append(cet);
                                coba2.append(cet1.substring(0,16));
                                jmlh1 = jmlh1 + jmlh;
                                ttlhrg = ttlhrg + jmtl;
                                discttl1 = discttl1 + discttl;



                            }
                            totaluangsub.setText(formatRupiah(ttlhrg));

                            if(namaid.equals("restoll2@baguslangit.com")){
                                getorderid();
                            }

                            if(!coba.equals("")){
                                loadingDialog.dismiss();
                            }

                            disc20rb.setOnClickListener(v -> {
                                if(!disc20rb.getText().toString().equals("batal")) {
                                    if (namaid.equals("restoll2@baguslangit.com")) {
                                        if (ttlhrg > 200000) {
                                            discttl1 = 20000;
                                        }
                                        jumlahsub.setText(String.format("%.0f", jmlh1));
                                        totaluang.setText(formatRupiah(ttlhrg - discttl1));
                                        disc.setText("Disc ");
                                        totaluangdisc.setText(formatRupiah(discttl1));
                                        disc20rb.setText("batal");
                                    }
                                }else {
                                    if (namaid.equals("restoll2@baguslangit.com")) {
                                        discttl1 = 0;
                                        totaluang.setText(formatRupiah(ttlhrg - discttl1));
                                        jumlahsub.setText(String.format("%.0f", jmlh1));
                                        disc.setText("Disc ");
                                        totaluangdisc.setText(formatRupiah(discttl1));
                                        disc20rb.setText("Terapkan disc Rp20.000");
                                    }
                                }
                            });




                            // after that we are passing our array list to our adapter class.
                            CoursesLVAdapter adapter = new CoursesLVAdapter(PrintNota.this, dataModalArrayList);

                            // after passing this array list to our adapter
                            // class we are setting our adapter to our list view.
                            coursesLV.setAdapter(adapter);

                        } else {
                            print.setVisibility(View.GONE);
                            totaluang.setText("0");
                            jumlah.setText("0");
                            // if the snapshot is empty we are displaying a toast message.
                            if (checkInternet()) {
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(PrintNota.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            }
                            loadingDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        if (checkInternet()) {
                            Toast.makeText(PrintNota.this, "Fail to load data..Cek Internet", Toast.LENGTH_LONG).show();
                            loadingDialog.dismiss();
                        } else {
                            Toast.makeText(PrintNota.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                            loadingDialog.dismiss();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    public boolean checkInternet() {
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }


    public class CoursesLVAdapter extends ArrayAdapter<Note> {
        public CoursesLVAdapter(@NonNull Context context, ArrayList<Note> dataModalArrayList) {
            super(context, 0, dataModalArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // below line is use to inflate the
            // layout for our item of list view.
            Note dataModal = getItem(position);
            discttl = 0;
            if (dataModal.getDisc() != null) {
                discttl = Double.parseDouble(dataModal.getDisc());
            }
            hr = Double.parseDouble(dataModal.getHarga());
            tl = Double.parseDouble(dataModal.getTotal());
            jm = Double.parseDouble(dataModal.getJumlah());
            String sub = String.format("%.0f", hr * jm);
            String ttla = formatRupiah(hr * jm);
            if (sub.length() == 4) {
                if (ttla.length() == 7) {
                    ttll = ttla;
                } else {
                    ttll = ttla.substring(0, 7);
                }
            } else if (sub.length() == 5) {
                if (ttla.length() == 8) {
                    ttll = ttla;
                } else {
                    ttll = ttla.substring(0, 8);
                }
            } else if (sub.length() == 6) {
                if (ttla.length() == 9) {
                    ttll = ttla;
                } else {
                    ttll = ttla.substring(0, 9);
                }
            } else if (sub.length() == 7) {
                if (ttla.length() == 10) {
                    ttll = ttla;
                } else {
                    ttll = ttla.substring(0, 10);
                }
            }
            String nm = dataModal.getNama();
            String nm1 = dataModal.getJumlah();
            int lg = nm.length();
            int lg1 = ttll.length();
            int lg2 = nm1.length();

            jmlh = Double.parseDouble(nm1);


            String sp = "                 ";//17carater
            String sp1 = "          ";//10carater
            String sp2 = "   ";//3carater
            String nmb = sp.substring(lg);
            String nmg = nm + nmb;
            String nmb1 = sp1.substring(lg1);
            String nmg1 = ttll + nmb1;
            String nmb2 = sp2.substring(lg2);
            String nmg2 = nm1 + nmb2;

            String cet = nmg2 + nmg + "  " + nmg1 + "\n";
            String cet1 = nmg2 + nmg + "\n";
            View listitemView = convertView;
            if (listitemView == null) {
                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_card2, parent, false);
            }


            // totalutama
            totaluang.setText(formatRupiah(ttlhrg - discttl1));
            jumlah.setText(String.format("%.0f", jmlh1));
            // after inflating an item of listview item
            // we are getting data from array list inside
            // our modal class.

            // initializing our UI components of list view item.
            TextView nama = listitemView.findViewById(R.id.nama);
            TextView jumlah = listitemView.findViewById(R.id.jumlah);
            TextView total = listitemView.findViewById(R.id.total);
            ImageView hapus = listitemView.findViewById(R.id.hapus);

            if (asal.equals("histori")) {
                hapus.setVisibility(View.GONE);
            } else if (stsprint.equals("print")) {
                hapus.setVisibility(View.GONE);
            }
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.

            nmdic = dataModal.getNama();

            jumlahsub.setText(String.format("%.0f", jmlh1));
            totaluangsub.setText(formatRupiah(ttlhrg));
            disc.setText("Disc ");
            totaluangdisc.setText(formatRupiah(discttl1));

            nama.setText(dataModal.getNama());
            total.setText(ttll);
            jumlah.setText(String.format("%.0f", jm));

            hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (asal.equals("histori")) {
                        Toast.makeText(PrintNota.this, "tidak bisa di ubah", Toast.LENGTH_LONG).show();
                    } else {
                        if (dell) {
                            Toast.makeText(PrintNota.this, "Sudah di print", Toast.LENGTH_LONG).show();
                        } else {
                            coba.setText("");
                            coba1.setText("");
                            coba2 = new StringBuilder();
                            db.collection(namaid).document(id)
                                    .collection("order")
                                    .document(dataModal.getDoc())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            msg = "--------------------------------\n" + lokasi + "\n" + cafe + "\n" + alamat + "\nBandar Lampung\n--------------------------------\n" + "ID:" + strnama + "\n\n";
                                            String cb1 = "\n--------------------------------\n" + "ID:" + strnama + "\n\n";
                                            String cb2 = "\n----------------\n" + "ID:" + strnama + "\n\n";


                                            //coba.setText(msg);
                                            //coba1.setText(cb1);
                                            //coba2.append(cb1);
                                            coba.setText("--------------------For Customer" + msg);
                                            coba1.setText("-----------------------For Admin" + cb1);
                                            coba2.append("-----For Kitchen" + cb2 + "\n\n");
                                            dataModalArrayList.clear();
                                            ttlhrg = 0;
                                            jmlh1 = 0;
                                            discttl1 = 0;
                                            dc2 = 0;
                                            dc3 = 0;
                                            adapterlv = new CoursesLVAdapter(PrintNota.this, dataModalArrayList);
                                            coursesLV.setAdapter(adapterlv);
                                            loadDatainListview();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        }
                    }
                }
            });


            // in below line we are using Picasso to
            // load image from URL in our Image VIew.

            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (asal.equals("histori")) {
                        Toast.makeText(PrintNota.this, "tidak bisa di ubah", Toast.LENGTH_LONG).show();
                    } else {
                        if (dell) {
                            Toast.makeText(PrintNota.this, "Sudah di print", Toast.LENGTH_LONG).show();
                        } else {
                            Intent home = new Intent(PrintNota.this, TambahPesan.class);
                            home.putExtra("nama", dataModal.getNama());
                            home.putExtra("harga", dataModal.getHarga());
                            home.putExtra("status-order", status);
                            home.putExtra("strnama", strnama);
                            home.putExtra("paksa", paksa);
                            home.putExtra("asal", "printnota");
                            startActivity(home);
                        }
                    }
                }
            });
            // below line is use to add item click listener
            // for our item of list view.

            loadingDialog.dismiss();

            return listitemView;
        }
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void upData() {
        tanggalinput = df.format(new Date());
        jamin = df1.format((new Date()));
        doctgl = df5.format(new Date());
        double pjk = Double.parseDouble(String.valueOf(ttlhrg - discttl1)) * Double.parseDouble(pajak) / 100;

        Map<String, Object> user = new HashMap<>();
        user.put("orderid", strnama);
        user.put("tanggalinput", tanggalinput);
        user.put("jaminput", jamin);
        user.put("total", String.valueOf(ttlhrg - discttl1));
        user.put("disc", String.valueOf(discttl1));
        user.put("doc", doctgl + "-" + jaminput);
        user.put("print", jumlahprint);
        user.put("ubah", "");
        user.put("namacafe", cafe);
        user.put("lokasi", lokasi);
        user.put("pajak", String.format("%.0f", pjk));

        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .document(doctgl + "-" + jaminput)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        loadingDialog.dismiss();
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void upDataprint() {
        tanggalinput = df.format(new Date());
        jamin = df1.format((new Date()));
        double pjk = Double.parseDouble(String.valueOf(ttlhrg - discttl1)) * Double.parseDouble(pajak) / 100;

        Map<String, Object> user = new HashMap<>();
        user.put("total", String.valueOf(ttlhrg - discttl1));
        user.put("disc", String.valueOf(discttl1));
        user.put("print", jumlahprint);
        user.put("pajak", String.format("%.0f", pjk));
        user.put("jaminput" + jumlahprint, jamin);
        user.put("tanggalinput" + jumlahprint, tanggalinput);
        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .document(docu)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        loadingDialog.dismiss();
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    // Printing Text to Bluetooth Printer //

    protected void printBill() {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        } else {

            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();

                jam = df1.format(new Date());
                String ttlu1 = String.format("%.0f", ttlhrg);
                String ttlua1 = String.format("%.0f", discttl1);
                String ttlub1 = String.format("%.0f", ttlhrg - discttl1);


                String ttlu2 = formatRupiah(ttlhrg);
                String ttlua2 = formatRupiah(discttl1);
                String ttlub2 = formatRupiah(ttlhrg - discttl1);
                ttlu = ttlu2;
                ttlua = ttlua2;
                ttlub = ttlub2;

                if (ttlu1.length() == 4) {
                    if (ttlu2.length() == 7) {
                        ttlu = ttlu2;
                    } else {
                        ttlu = ttlu2.substring(0, 7);
                    }
                } else if (ttlu1.length() == 5) {
                    if (ttlu2.length() == 8) {
                        ttlu = ttlu2;
                    } else {
                        ttlu = ttlu2.substring(0, 8);
                    }
                } else if (ttlu1.length() == 6) {
                    if (ttlu2.length() == 9) {
                        ttlu = ttlu2;
                    } else {
                        ttlu = ttlu2.substring(0, 9);
                    }
                } else if (ttlu1.length() == 7) {
                    if (ttlu2.length() == 10) {
                        ttlu = ttlu2;
                    } else {
                        ttlu = ttlu2.substring(0, 10);
                    }
                }

                if (ttlua1.length() == 3) {
                    if (ttlua2.length() == 6) {
                        ttlua = ttlua2;
                    } else {
                        ttlua = ttlua2.substring(0, 6);
                    }
                } else if (ttlua1.length() == 4) {
                    if (ttlua2.length() == 7) {
                        ttlua = ttlua2;
                    } else {
                        ttlua = ttlua2.substring(0, 7);
                    }
                } else if (ttlua1.length() == 5) {
                    if (ttlua2.length() == 8) {
                        ttlua = ttlua2;
                    } else {
                        ttlua = ttlua2.substring(0, 8);
                    }
                } else if (ttlua1.length() == 6) {
                    if (ttlua2.length() == 9) {
                        ttlua = ttlua2;
                    } else {
                        ttlua = ttlua2.substring(0, 9);
                    }
                } else if (ttlua1.length() == 7) {
                    if (ttlua2.length() == 10) {
                        ttlua = ttlua2;
                    } else {
                        ttlua = ttlua2.substring(0, 10);
                    }
                }

                if (ttlub1.length() == 4) {
                    if (ttlub2.length() == 7) {
                        ttlub = ttlub2;
                    } else {
                        ttlub = ttlub2.substring(0, 7);
                    }
                } else if (ttlub1.length() == 5) {
                    if (ttlub2.length() == 8) {
                        ttlub = ttlub2;
                    } else {
                        ttlub = ttlub2.substring(0, 8);
                    }
                } else if (ttlub1.length() == 6) {
                    if (ttlub2.length() == 9) {
                        ttlub = ttlub2;
                    } else {
                        ttlub = ttlub2.substring(0, 9);
                    }
                } else if (ttlub1.length() == 7) {
                    if (ttlub2.length() == 10) {
                        ttlub = ttlub2;
                    } else {
                        ttlub = ttlub2.substring(0, 10);
                    }
                }

                String nm1 = String.format("%.0f", jmlh1);
                int lg1 = ttlu.length();
                int lg1a = ttlua.length();
                int lg1b = ttlub.length();
                int lg2 = nm1.length();
                String sp = "        ";//8carater
                String sp1 = "          ";//10carater
                String sp2 = "   ";//3carater
                String nmb1 = sp1.substring(lg1);
                String nmb1a = sp1.substring(lg1a);
                String nmb1b = sp1.substring(lg1b);
                String nmg1 = ttlu + nmb1;
                String nmg1a = ttlua + nmb1a;
                String nmg1b = ttlub + nmb1b;
                String nmb2 = sp2.substring(lg2);
                String nmg2 = nm1 + nmb2;
                String msg1 = coba.getText().toString();
                String msg1a = coba1.getText().toString();
                String msg2 = "\n" + nmg2 + sp + "Subtotal : " + nmg1;
                String msg2a = "\n" + sp2 + sp + "    Disc" + " : " + nmg1a;
                String msg2b = "\n" + sp2 + sp + "   Total : " + nmg1b;
                String msg3 = "\n\n\n-----" + tgl1 + "----" + jam + "-----" + "\n" + "Terima Kasih\n" + "Pembelian Anda Gratis\nJika Tidak Ada Struk" + "\n";
                String msg4 = msg1 + msg2 + msg2a + msg2b + msg3;
                printCustom(msg4, 1, 1);
                //printPhoto1(R.drawable.logoawi);
                //printCustom("\nScan aku untuk lihat promo!", 1, 1);
                printNewLine();
                printNewLine();
                printNewLine();
                /// start untuk admin
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printNewLine();
                printCustom("\n\n" + msg1a + msg2 + msg2a + msg2b + "\n\n\n-----" + tgl1 + "----" + jam + "-----\n\n", 1, 1);
                printNewLine();
                printNewLine();
                printNewLine();
                /// end untuk admin

                /// start untuk kitchen
                if (namaid.equals("") || namaid.equals("")) {
                    try {
                        Thread.sleep(4500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printNewLine();
                    printNewLine();
                    printCustom("\n\n" + String.valueOf(coba2) + "\n\n\n---" + tgl1 + "-------" + jam + "----\n\n", 6, 1);
                    printNewLine();
                    printNewLine();
                    printNewLine();
                }
                //end untuk kitchen

                if (!asal.equals("histori")) {
                    upData();
                    setJumlahTransaksi();
                } else {
                    upDataprint();
                }

                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void printPhoto1(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x15}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text


        byte[] bb4 = new byte[]{0x1B, 0x21, 0x2B}; // 3- bold with large text33-39
        byte[] bb5 = new byte[]{0x1B, 0x21, 0x30}; // 3- bold with large text
        byte[] bb6 = new byte[]{0x1B, 0x21, 0x35}; // 3- bold with large text
        byte[] bb7 = new byte[]{0x1B, 0x21, 0x3A}; // 3- bold with large text
        byte[] bb8 = new byte[]{0x1B, 0x21, 0x3F}; // 3- bold with large text
        byte[] bb9 = new byte[]{0x1B, 0x21, 0x40}; // 3- bold with large text
        byte[] bb10 = new byte[]{0x1B, 0x21, 0x4A}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb4);
                    break;
                case 4:
                    outputStream.write(bb5);
                    break;
                case 5:
                    outputStream.write(bb6);
                    break;
                case 6:
                    outputStream.write(bb7);
                    break;
                case 7:
                    outputStream.write(bb8);
                    break;
                case 8:
                    outputStream.write(bb9);
                    break;
                case 9:
                    outputStream.write(bb10);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
            loadingDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setJumlahTransaksi() {
        Map<String, Object> user = new HashMap<>();
        user.put("status-order", "print");
        db.collection(namaid)
                .document(id)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                        loadingDialog.dismiss();
                        Intent home = new Intent(PrintNota.this, MainActivity.class);
                        startActivity(home);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = DeviceList.getSocket();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}