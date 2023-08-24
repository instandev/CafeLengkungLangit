package com.baguslangit.cafelengkunglangit;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class histori extends AppCompatActivity {
    ListView coursesLV;
    ArrayList<Note> dataModalArrayList;
    FirebaseFirestore db;
    Spinner satuan;
    String id, caribarang, status, namaid, tgl, docinput, oin, docinputlama, hargaprahu;
    String doc, jaminput, orderid, tanggalinput, total, cafe, lokasi, pajak, jumlahc, jdisc, hargac, namac, idd;
    TextView cari, email;

    TextView refreshLayout, home, plus, riwayat, back, nama, lok;
    ViewLoadingDialog loadingDialog;
    LinearLayout cari1;
    Boolean vcari;
    Boolean vload;
    final Calendar myCalendar = Calendar.getInstance();
    SimpleDateFormat df, df3;
    ArrayAdapter<String> adapter;
    CoursesLVAdapter adapterlv;
    double juml = 0, juml1, jml, getpjk;
    List<String> list = new ArrayList<>();
    List<String> list1 = new ArrayList<>();
    int k;

    List<String> listjudul = new ArrayList<>();
    List<String> listharga = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histori);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        coursesLV = (ListView) findViewById(R.id.list_view);
        refreshLayout = (TextView) findViewById(R.id.refresh);
        home = (TextView) findViewById(R.id.home);
        plus = (TextView) findViewById(R.id.plus);
        riwayat = (TextView) findViewById(R.id.riwayat);
        cari = (TextView) findViewById(R.id.cari);
        cari1 = (LinearLayout) findViewById(R.id.cari1);
        satuan = (Spinner) findViewById(R.id.satuan);
        back = (TextView) findViewById(R.id.back);
        nama = (TextView) findViewById(R.id.nama);
        lok = (TextView) findViewById(R.id.lok);
        email = (TextView) findViewById(R.id.email);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        namaid = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());
        email.setText(namaid);
        getTenant();

        //nanti di ganti
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        df3 = new SimpleDateFormat("HH-mm-ss-SSS", Locale.getDefault());

        caribarang = df.format(new Date());
        cari.setText(df.format(myCalendar.getTime()));
        vcari = false;
        vload = false;

        loadingDialog = new ViewLoadingDialog(histori.this);
        loadingDialog.setPesan("Mohon Tunggu Sebentar");
        loadingDialog.show();

        dataModalArrayList = new ArrayList<>();

        String[] items1 = new String[]{"Input Barang Baru", "Tambah Barang", "Ambil Barang", "Hapus Barang", "Refresh"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);

        satuan.setAdapter(adapter);

        satuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = String.valueOf(satuan.getSelectedItem());
                cari.setText(df.format(myCalendar.getTime()));
                caribarang = df.format(myCalendar.getTime());
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(histori.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                getTenant();
                loadDatainListview();
                loadDatainListview5();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getTenant();
            }
        });


        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                status = String.valueOf(satuan.getSelectedItem());
                cari.setText(df.format(myCalendar.getTime()));
                caribarang = df.format(myCalendar.getTime());
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(histori.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                getTenant();
                loadDatainListview();
                loadDatainListview5();

                //updatedoc();
                //up1();
            }

        };


        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataModalArrayList.clear();
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                caribarang = cari.getText().toString();
                status = String.valueOf(satuan.getSelectedItem());
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(histori.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                getTenant();
                loadDatainListview();
                loadDatainListview5();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(histori.this, MainActivity.class);
                startActivity(home);
                finish();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(histori.this, PrintNota.class);
                home.putExtra("orderid", "");
                home.putExtra("asal", "");
                home.putExtra("harga", hargaprahu);
                startActivity(home);
                finish();
            }
        });
        riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(histori.this, "mulai",Toast.LENGTH_LONG).show();
                //up1();
            }
        });


        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(histori.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent home = new Intent(histori.this, login.class);
                startActivity(home);
                finish();
            }
        });

        loadDatainListview();
        loadDatainListview5();

        //loadDatasemua();
        //updatedoc();

        //up1();
    }

    private void getTenant() {
        db.collection("admin")
                .document(namaid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.getString("nama") != null) {
                                    cafe = document.getString("nama");
                                    lokasi = document.getString("lokasi");
                                    pajak = document.getString("pajak");
                                    if (namaid.equals("cafell2atas@baguslangit.com")) {
                                        hargaprahu = document.getString("harga");
                                    }
                                    getpjk = Double.parseDouble(pajak) / 100;
                                    if (lokasi.equals("Lengkung Langit 1")) {
                                        lok.setText("LL1");
                                        nama.setText(cafe);
                                    } else if (lokasi.equals("Lengkung Langit 2")){
                                        lok.setText("LL2");
                                        nama.setText(cafe);
                                    }else {
                                        lok.setText("WB");
                                        nama.setText(lokasi);
                                    }
                                    nama.setAllCaps(true);
                                    loadDatainListview5();
                                } else {
                                    if (checkInternet()) {
                                        Toast.makeText(histori.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                        loadingDialog.dismiss();
                                    } else {
                                        Toast.makeText(histori.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                        loadingDialog.dismiss();
                                    }
                                }
                            } else {
                                if (checkInternet()) {
                                    Toast.makeText(histori.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                } else {
                                    Toast.makeText(histori.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                }
                            }
                        } else {
                            if (checkInternet()) {
                                Toast.makeText(histori.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(histori.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });

    }

    private static String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    public boolean checkInternet() {
        boolean connectStatus = true;
        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(histori.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        connectStatus = networkInfo != null && networkInfo.isConnected();
        return connectStatus;
    }

    private void loadDatainListview() {

        // below line is use to get data from Firebase
        // firestore using collection in android.
        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .orderBy("doc", Query.Direction.ASCENDING)
                .startAt(caribarang)
                .endAt(caribarang + "\uf8ff")
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
                                loadingDialog.dismiss();
                            }

                            // after that we are passing our array list to our adapter class.
                            adapterlv = new CoursesLVAdapter(histori.this, dataModalArrayList);
                            coursesLV.setAdapter(adapterlv);
                            loadingDialog.dismiss();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.


                            if (checkInternet()) {
                                Toast.makeText(histori.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(histori.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(histori.this, "Fail to load data..Cek Internet", Toast.LENGTH_LONG).show();
                            loadingDialog.dismiss();
                        } else {
                            Toast.makeText(histori.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                            loadingDialog.dismiss();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }


    private void up1() {
        db.collection(namaid)
                .document(id)
                .collection("menu")
                // .whereEqualTo("tanggalinput", caribarang)
                //.limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Random rnd1 = new Random();
                                int number1 = 100 + rnd1.nextInt(899);

                                listjudul.add(document.getId());
                                listharga.add(document.getString("harga"));

                                //tgl = document.getString("tanggalinput");
                                //jaminput = document.getString("jaminput");
                                //disc = document.getString("disc");
                                //orderid = document.getString("orderid");
                                //total = document.getString("total");
                                //total = String.valueOf(total1);

                                //jumlahc = document.getString("jumlah");
                                //namac = document.getString("nama");
                                //jdisc = document.getString("jdisc");
                                //hargac = document.getString("harga");
//
                                ////double hrg = Double.parseDouble(Objects.requireNonNull(hargac))-1000;
////
                                ////hargac = String.format("%.0f", hrg);
                                //String docc = document.getId();
////
                                //String tgl1 = tgl.substring(0,2);
                                //String tgl2 = tgl.substring(3,5);
                                //String tgl3 = tgl.substring(6);
                                //String jam = jaminput;
                                //String jam1 = jam.substring(0, 2);
                                //String jam2 = jam.substring(3, 5);
                                //String jam3 = jam.substring(6, 8);
                                ////String jam4 = docc.substring(20);  //untuk order aja
                                //String num = String.valueOf(number1);
                                //docinput = tgl3+"-"+tgl2+"-"+tgl1+"-"+jam1+"-"+jam2+"-"+jam3+"-"+num;
                                //tanggalinput = tgl3+"-"+tgl2+"-"+tgl1;
                                //docinputlama = document.getString("doc");
                                //docinput = document.getString("doc");
                                //docinput = document.getId();
                                //upTiketoffline();
                                //delet();
                            }
                            k = 0;
                            upTiketoffline();
                        } else {
                            loadingDialog.dismiss();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void upTiketoffline() {

//
        Map<String, Object> user = new HashMap<>();
        //user.put("nama", namac);
        //user.put("harga", hargac);
        //user.put("jdisc", jdisc);
        //user.put("jumlah", jumlahc);

        //user.put("doc", docinput);
        //user.put("jaminput", jaminput);
        //user.put("orderid", orderid);
        //user.put("tanggalinput", tanggalinput);
        //user.put("total", total);
        //user.put("disc", disc);
        //user.put("print", "1");
        //user.put("ubah", "new");
        //user.put("pajak", "0");
        //user.put("lokasi", "Lengkung Langit 2");
        user.put("nama", listjudul.get(k));
        user.put("harga", listharga.get(k));
        user.put("namacafe", "Resto LL2 Bawah");
        user.put("kategori", "");
        db.collection("restoll2bawah@baguslangit.com")
                .document("mQt5538zfZQQR3SHP2RNzDmwY2g2")
                .collection("menu")
                .document(listjudul.get(k))
                .set(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "tanggal " + docinputlama);
                        //delet();
                        //up1();
                        if (k <= listjudul.size()) {
                            k++;
                            upTiketoffline();
                        } else {
                            Toast.makeText(histori.this, "selesai", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void upT() {
//
        Map<String, Object> user = new HashMap<>();
        user.put("ubah", "");
        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .document(docinputlama)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                        up1();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void delet() {
        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .document(orderid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                        up1();
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
        db.collection(namaid)
                .whereEqualTo("paksa", "login")
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
                            tgl = doc.getString("tanggal");
                        }
                        updatedoc();
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
    }

    //
    // coba update doc
    void updatedoc() {
        db.collection(namaid)
                .document(id)
                .collection("order")
                .whereEqualTo("tanggalinput", caribarang)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());

                            }
                            Log.d(TAG, list.toString());
                            updateData(list); // *** new ***
                            // Toast.makeText(histori.this, caribarang,Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    //
//
    void updateData(List<String> list) {

        // Get a new write batch
        WriteBatch batch = db.batch();

        // Iterate through the list
        for (k = 0; k < list.size(); k++) {
            DocumentReference ref = db.collection(namaid)
                    .document(id)
                    .collection("order")
                    .document(list.get(k));
            batch.update(ref, "ubah", "");
            //batch.delete(ref);


        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                list.clear();
                String cr = caribarang.substring(0, 2);
                String cr1 = caribarang.substring(2);
                double cj = Double.parseDouble(cr) + 1;
                String cj1 = String.format("%.0f", cj);
                caribarang = cj1 + cr1;
                if (cj < 10) {
                    caribarang = "0" + cj1 + cr1;
                }

                if (cj < 32) {
                    updatedoc();
                } else {
                    Toast.makeText(histori.this, "selesai", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(histori.this, MainActivity.class);
        startActivity(home);
        finish();
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


            View listitemView = convertView;
            if (listitemView == null) {

                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_card3, parent, false);
            }

            // after inflating an item of listview item
            // we are getting data from array list inside
            // our modal class.

            // initializing our UI components of list view item.
            TextView nama = listitemView.findViewById(R.id.title2Txt);
            TextView harga = listitemView.findViewById(R.id.harga);
            TextView jumlah = listitemView.findViewById(R.id.jmlh);
            TextView jumlahorder = listitemView.findViewById(R.id.jmlhttl);

            double hr = Double.parseDouble(dataModal.getTotal());
            double hrdc = Double.parseDouble(dataModal.getDisc());
            // after initializing our items we are
            // setting data to our view.
            // below line is use to set data to our text view.
            nama.setText(dataModal.getOrderid());
            harga.setText(formatRupiah(hr));
            jumlah.setText(dataModal.getJaminput());

            // in below line we are using Picasso to
            // load image from URL in our Image VIew.


            if (namaid.equals("admin@ruangawi.com")) {
                listitemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doc = dataModal.getDoc();
                        AlertDialog.Builder builder = new AlertDialog.Builder(histori.this);
                        builder.setTitle("Hapus Barang ini?");

                        builder.setPositiveButton("Print Ulang", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent home = new Intent(histori.this, PrintNota.class);
                                home.putExtra("orderid", dataModal.getOrderid());
                                home.putExtra("tanggalinput", dataModal.getTanggalinput());
                                home.putExtra("asal", "histori");
                                home.putExtra("print", dataModal.getPrint());
                                home.putExtra("doc", dataModal.getDoc());
                                startActivity(home);
                            }
                        });
                        builder.setNegativeButton("Hapus Item", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                orderid = dataModal.getOrderid();
                                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                                loadingDialog.show();
                                db.collection(namaid).document(id).collection("transaksi").document(dataModal.getDoc())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                                                loadingDialog.show();
                                                up2();
                                                dataModalArrayList.clear();
                                                adapterlv = new CoursesLVAdapter(histori.this, dataModalArrayList);
                                                coursesLV.setAdapter(adapterlv);
                                                loadDatainListview();
                                                loadDatainListview5();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (checkInternet()) {
                                                    Toast.makeText(histori.this, "internet tidak stabil", Toast.LENGTH_LONG).show();
                                                    loadingDialog.dismiss();
                                                } else {
                                                    Toast.makeText(histori.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                                    loadingDialog.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
                        builder.show();

                    }

                });
            }else {
                listitemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent home = new Intent(histori.this, PrintNota.class);
                        home.putExtra("orderid", dataModal.getOrderid());
                        home.putExtra("tanggalinput", dataModal.getTanggalinput());
                        home.putExtra("asal", "histori");
                        home.putExtra("print", dataModal.getPrint());
                        home.putExtra("doc", dataModal.getDoc());
                        startActivity(home);
                    }
                });
            }
            // below line is use to add item click listener
            // for our item of list view.

            return listitemView;
        }
    }

    private void up2() {
        db.collection(namaid)
                .document(id)
                .collection("order")
                .whereEqualTo("orderid", orderid)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                idd = document.getId();
                                delet1();
                            }
                        } else {
                            loadingDialog.dismiss();
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void delet1() {
        db.collection(namaid)
                .document(id)
                .collection("order")
                .document(idd)
                .delete()
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d(TAG, "DocumentSnapshot added with ID: ");
                        up2();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void loadDatainListview5() {
        // below line is use to get data from Firebase
        // firestore using collection in android.
        db.collection(namaid)
                .document(id)
                .collection("transaksi")
                .orderBy("doc", Query.Direction.ASCENDING)
                .startAt(caribarang)
                .endAt(caribarang + "\uf8ff")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(L.TAG, "Listen failed.", e);
                            return;
                        } else {
                            juml = 0;
                            jml = 0;
                        }


                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Log.d(L.TAG, "Current cites in CA: " + doc.getData());
                            loadingDialog.dismiss();
                            jml = jml + 1;
                            double hgga = Double.parseDouble(doc.getString("total"));
                            juml = juml + hgga;
                        }
                        double pjk = juml * getpjk;
                        loadingDialog.dismiss();
                        TextView jumlahuang = (TextView) findViewById(R.id.totaluang);

                        jumlahuang.setText(formatRupiah(juml) + " x " + pajak + "% = " + formatRupiah(pjk));
                        Log.d(L.TAG, "Current cites in CA: " + cities);
                    }
                });
    }
}