package com.baguslangit.cafelengkunglangit;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

public class TambahPesan extends AppCompatActivity {
    String sNama, sHarga, jamin, id, namaid, strnama, tanggalinput, sOrder, paksa,
            num, num1, num2, jumlah, doc, asal, disc, jdisc, jmlh, ttldc, namacafe, lokasi, pajak;
    EditText eJumlahpesan, input;
    TextView tNama, tJumlah, tHarga, tSimpan, home, tJumlahbln, disc10, disc20, disc0, disc50, pesan;
    FirebaseFirestore db;
    ViewLoadingDialog loadingDialog;
    SimpleDateFormat df, df1, df3;
    boolean edit;
    LinearLayout discon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pesan);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        tNama = (TextView) findViewById(R.id.namamenu);
        tHarga = (TextView) findViewById(R.id.hargamenu);
        tJumlah = (TextView) findViewById(R.id.terjualmenu);
        tSimpan = (TextView) findViewById(R.id.simpan);
        tJumlahbln = (TextView) findViewById(R.id.bulanini);
        disc10 = (TextView) findViewById(R.id.dis10);
        disc0 = (TextView) findViewById(R.id.dis0);
        disc20 = (TextView) findViewById(R.id.dis20);
        disc50 = (TextView) findViewById(R.id.dis50);
        discon = (LinearLayout) findViewById(R.id.discon);
        pesan = (TextView) findViewById(R.id.pesan);

        eJumlahpesan = (EditText) findViewById(R.id.jumlah);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        namaid = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());

        loadingDialog = new ViewLoadingDialog(TambahPesan.this);


        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        df1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        df3 = new SimpleDateFormat("HH-mm-ss-SSS", Locale.getDefault());

        tanggalinput = df.format(new Date());

        Intent intent = getIntent();
        sNama = intent.getStringExtra("nama");
        sHarga = intent.getStringExtra("harga");
        sOrder = intent.getStringExtra("status-order");
        paksa = intent.getStringExtra("paksa");
        asal = intent.getStringExtra("asal");
        namacafe = intent.getStringExtra("namacafe");
        lokasi = intent.getStringExtra("lokasi");
        pajak = intent.getStringExtra("pajak");

        disc10.setVisibility(View.GONE);
        disc20.setVisibility(View.GONE);
        disc50.setVisibility(View.GONE);
        pesan.setVisibility(View.GONE);

        if (Double.parseDouble(sHarga) <= 10000) {
            disc10.setVisibility(View.GONE);
            disc20.setVisibility(View.GONE);
            disc50.setVisibility(View.GONE);
        }

        if (asal.equals("printnota")) {
            edit = true;
        }

        switch (namaid) {
            case "cafelangit@baguslangit.com":
            case "cafell2bawah@baguslangit.com":
            case "cafell2atas@baguslangit.com":
            case "restoll2bawah@baguslangit.com":
                //pesan.setVisibility(View.VISIBLE);
                disc10.setVisibility(View.VISIBLE);
                disc20.setVisibility(View.VISIBLE);
                //disc50.setVisibility(View.VISIBLE);
                break;
            case "restoll2@baguslangit.com":
                disc20.setVisibility(View.VISIBLE);
                break;
            default:
                disc10.setVisibility(View.GONE);
                disc20.setVisibility(View.GONE);
                disc50.setVisibility(View.GONE);
                pesan.setVisibility(View.GONE);
                disc0.setText("Tambah");
                break;
        }


        if (!paksa.equals("login")) {
            FirebaseAuth.getInstance().signOut();
            Intent home = new Intent(TambahPesan.this, login.class);
            startActivity(home);

        }


        if (sOrder.equals("print")) {
            loadingDialog.setPesan("Mohon Tunggu Sebentar");
            loadingDialog.show();
            Random rnd = new Random();
            Random rnd1 = new Random();
            Random rnd2 = new Random();
            int number = 100 + rnd.nextInt(899);
            int number1 = 100 + rnd1.nextInt(899);
            int number2 = 100 + rnd2.nextInt(899);
            num = String.valueOf(number);
            num1 = String.valueOf(number1);
            num2 = String.valueOf(number2);
            String code;
            if(namaid.equals("admin@ruangawi.com")){
                code = "RA-";
            }else if(namaid.equals("wbcafe@baguslangit.com")){
                code = "WB-";
            }else {
                code = "LL-";
            }
            strnama = code + num + num1 + num2;
            loadingDialog.dismiss();
        } else {
            strnama = intent.getStringExtra("strnama");
            getDataorder();
        }


        tNama.setText(sNama);
        tHarga.setText(formatRupiah(Double.parseDouble(sHarga)));


        disc0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eJumlahpesan.getText().toString().equals("")) {
                    Toast.makeText(TambahPesan.this, "tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.setPesan("Mohon Tunggu Sebentar");
                    loadingDialog.show();
                    double hr = Double.parseDouble(sHarga);
                    double dis = 0;
                    disc = String.valueOf(dis);
                    jdisc = "0%";
                    double jl = Double.parseDouble(eJumlahpesan.getText().toString());
                    double tl = hr * jl;
                    double tll = tl - dis;
                    ttldc = String.valueOf(tll);
                    if (sOrder.equals("print")) {
                        setJumlahTransaksi();
                    }
                    if (!edit) {
                        upData();
                    } else {
                        editData();
                    }
                }
            }
        });

        disc10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eJumlahpesan.getText().toString().equals("")) {
                    Toast.makeText(TambahPesan.this, "tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (eJumlahpesan.getText().toString().equals("1")) {
                    loadingDialog.setPesan("Mohon Tunggu Sebentar");
                    loadingDialog.show();
                    jumlah = eJumlahpesan.getText().toString();
                    jmlh = "1";
                    double jum = Double.parseDouble(jmlh);
                    double hr = Double.parseDouble(sHarga);
                    double dis = jum * hr * 0.5;
                    disc = String.valueOf(dis);
                    jdisc = "50%";
                    double jl = Double.parseDouble(jumlah);
                    double tl = hr * jl;
                    double tll = tl - dis;
                    ttldc = String.valueOf(tll);
                    if (jum > jl) {
                        Toast.makeText(TambahPesan.this, "kebanyakan", Toast.LENGTH_SHORT).show();
                    } else {
                        if (sOrder.equals("print")) {
                            setJumlahTransaksi();
                        }
                        if (!edit) {
                            upData();
                        } else {
                            editData();
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TambahPesan.this);
                    builder.setTitle("Untuk berapa Cup?");

                    input = new EditText(TambahPesan.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setLayoutParams(lp);
                    input.setHint("jumlah discount");
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.setPesan("Mohon Tunggu Sebentar");
                            loadingDialog.show();
                            jumlah = eJumlahpesan.getText().toString();
                            jmlh = input.getText().toString();
                            double jum = Double.parseDouble(jmlh);
                            double hr = Double.parseDouble(sHarga);
                            double dis = jum * hr * 0.5;
                            disc = String.valueOf(dis);
                            jdisc = "10%";
                            double jl = Double.parseDouble(jumlah);
                            double tl = hr * jl;
                            double tll = tl - dis;
                            ttldc = String.valueOf(tll);
                            if (jum > jl) {
                                Toast.makeText(TambahPesan.this, "kebanyakan", Toast.LENGTH_SHORT).show();
                            } else {
                                if (sOrder.equals("print")) {
                                    setJumlahTransaksi();
                                }
                                if (!edit) {
                                    upData();
                                } else {
                                    editData();
                                }
                            }

                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });

        disc20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eJumlahpesan.getText().toString().equals("")) {
                    Toast.makeText(TambahPesan.this, "tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (eJumlahpesan.getText().toString().equals("1")) {
                    loadingDialog.setPesan("Mohon Tunggu Sebentar");
                    loadingDialog.show();
                    jumlah = eJumlahpesan.getText().toString();
                    jmlh = "1";
                    double jum = Double.parseDouble(jmlh);
                    double hr = Double.parseDouble(sHarga);
                    double dis = jum * hr * 0.2;
                    disc = String.valueOf(dis);
                    jdisc = "20%";
                    double jl = Double.parseDouble(jumlah);
                    double tl = hr * jl;
                    double tll = tl - dis;
                    ttldc = String.valueOf(tll);
                    if (jum > jl) {
                        Toast.makeText(TambahPesan.this, "kebanyakan", Toast.LENGTH_SHORT).show();
                    } else {
                        if (sOrder.equals("print")) {
                            setJumlahTransaksi();
                        }
                        if (!edit) {
                            upData();
                        } else {
                            editData();
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TambahPesan.this);
                    builder.setTitle("Untuk berapa Cup?");

                    input = new EditText(TambahPesan.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setLayoutParams(lp);
                    input.setHint("jumlah discount");
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.setPesan("Mohon Tunggu Sebentar");
                            loadingDialog.show();
                            jumlah = eJumlahpesan.getText().toString();
                            jmlh = input.getText().toString();
                            double jum = Double.parseDouble(jmlh);
                            double hr = Double.parseDouble(sHarga);
                            double dis = jum * hr * 0.2;
                            disc = String.valueOf(dis);
                            jdisc = "20%";
                            double jl = Double.parseDouble(jumlah);
                            double tl = hr * jl;
                            double tll = tl - dis;
                            ttldc = String.valueOf(tll);
                            if (jum > jl) {
                                Toast.makeText(TambahPesan.this, "Kebanyakan", Toast.LENGTH_SHORT).show();
                            } else {
                                if (sOrder.equals("print")) {
                                    setJumlahTransaksi();
                                }
                                if (!edit) {
                                    upData();
                                } else {
                                    editData();
                                }

                            }
                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });

        disc50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eJumlahpesan.getText().toString().equals("")) {
                    Toast.makeText(TambahPesan.this, "tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (eJumlahpesan.getText().toString().equals("1")) {
                    loadingDialog.setPesan("Mohon Tunggu Sebentar");
                    loadingDialog.show();
                    jumlah = eJumlahpesan.getText().toString();
                    jmlh = "1";
                    double jum = Double.parseDouble(jmlh);
                    double hr = Double.parseDouble(sHarga);
                    double dis = jum * hr * 0.3;
                    disc = String.valueOf(dis);
                    jdisc = "30%";
                    double jl = Double.parseDouble(jumlah);
                    double tl = hr * jl;
                    double tll = tl - dis;
                    ttldc = String.valueOf(tll);
                    if (jum > jl) {
                        Toast.makeText(TambahPesan.this, "kebanyakan", Toast.LENGTH_SHORT).show();
                    } else {
                        if (sOrder.equals("print")) {
                            setJumlahTransaksi();
                        }
                        if (!edit) {
                            upData();
                        } else {
                            editData();
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TambahPesan.this);
                    builder.setTitle("Untuk berapa Cup?");

                    input = new EditText(TambahPesan.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setLayoutParams(lp);
                    input.setHint("jumlah discount");
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingDialog.setPesan("Mohon Tunggu Sebentar");
                            loadingDialog.show();
                            jumlah = eJumlahpesan.getText().toString();
                            jmlh = input.getText().toString();
                            double jum = Double.parseDouble(jmlh);
                            double hr = Double.parseDouble(sHarga);
                            double dis = jum * hr * 0.5;
                            disc = String.valueOf(dis);
                            jdisc = "50%";
                            double jl = Double.parseDouble(jumlah);
                            double tl = hr * jl;
                            double tll = tl - dis;
                            ttldc = String.valueOf(tll);
                            if (jum > jl) {
                                Toast.makeText(TambahPesan.this, "Kebanyakan", Toast.LENGTH_SHORT).show();
                            } else {
                                if (sOrder.equals("print")) {
                                    setJumlahTransaksi();
                                }
                                if (!edit) {
                                    upData();
                                } else {
                                    editData();
                                }

                            }
                        }
                    });
                    builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });


        loadDatahari();
        loadDatasemua();

    }

    private void getDataorder() {
        db.collection(namaid)
                .document(id)
                .collection("order")
                .whereEqualTo("orderid", strnama)
                .whereEqualTo("nama", sNama)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                jumlah = document.getString("jumlah");
                                eJumlahpesan.setText(jumlah);
                                doc = document.getString("doc");
                                edit = true;
                                loadingDialog.dismiss();
                            }
                        } else {
                            loadingDialog.dismiss();
                            edit = false;
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    public boolean checkInternet() {
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }

    public void setJumlahTransaksi() {
        Map<String, Object> user = new HashMap<>();
        user.put("status-order", "order");
        user.put("strnama", strnama);
        db.collection(namaid)
                .document(id)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
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

    public void upData() {
        tanggalinput = df.format(new Date());
        jamin = df3.format((new Date()));
        String jmlh = eJumlahpesan.getText().toString();
        double pjk = Double.parseDouble(ttldc) * Double.parseDouble(pajak) / 100;

        Map<String, Object> user = new HashMap<>();
        user.put("nama", sNama);
        user.put("harga", sHarga);
        user.put("jumlah", jmlh);
        user.put("orderid", strnama);
        user.put("tanggalinput", tanggalinput);
        user.put("jaminput", jamin);
        user.put("doc", tanggalinput + "-" + jamin);
        user.put("disc", disc);
        user.put("jdisc", jdisc);
        user.put("total", ttldc);
        user.put("lokasi", lokasi);
        user.put("namacafe", namacafe);
        user.put("pajak", String.format("%.0f", pjk));

        db.collection(namaid)
                .document(id)
                .collection("order")
                .document(tanggalinput + "-" + jamin)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        loadingDialog.dismiss();
                        if (asal.equals("printnota")) {
                            Intent home = new Intent(TambahPesan.this, PrintNota.class);
                            startActivity(home);
                        } else if (asal.equals("main")) {
                            Intent home = new Intent(TambahPesan.this, MainActivity.class);
                            startActivity(home);
                        }
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    public void editData() {
        tanggalinput = df.format(new Date());
        jamin = df3.format((new Date()));
        String jmlh = eJumlahpesan.getText().toString();
        double pjk = Double.parseDouble(ttldc) * Double.parseDouble(pajak) / 100;

        Map<String, Object> user = new HashMap<>();
        user.put("jumlah", jmlh);
        user.put("disc", disc);
        user.put("jdisc", jdisc);
        user.put("total", ttldc);
        user.put("pajak", String.format("%.0f", pjk));

        db.collection(namaid)
                .document(id)
                .collection("order")
                .document(doc)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        loadingDialog.dismiss();
                        if (asal.equals("printnota")) {
                            Intent home = new Intent(TambahPesan.this, PrintNota.class);
                            home.putExtra("orderid", "");
                            home.putExtra("asal", "");
                            startActivity(home);
                        } else if (asal.equals("main")) {
                            onBackPressed();
                        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void loadDatahari() {
        db.collection(namaid)
                .document(id)
                .collection("order")
                .whereEqualTo("nama", sNama)
                .whereEqualTo("tanggalinput", tanggalinput)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        double jml = 0;
                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            double j = Double.parseDouble(doc.getString("jumlah"));
                            jml = jml + j;
                        }
                        tJumlah.setText("Terjual hari ini : " + String.format("%.0f", jml));
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
    }

    private void loadDatasemua() {
        db.collection(namaid)
                .document(id)
                .collection("order")
                .whereEqualTo("nama", sNama)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        double jml = 0;
                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            double j = Double.parseDouble(doc.getString("jumlah"));
                            jml = jml + j;
                        }
                        tJumlahbln.setText("terjual selama ini : " + String.format("%.0f", jml));
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
    }


}