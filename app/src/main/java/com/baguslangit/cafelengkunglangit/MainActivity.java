package com.baguslangit.cafelengkunglangit;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ListView coursesLV;
    ArrayList<Note> dataModalArrayList;
    FirebaseFirestore db;
    ViewLoadingDialog loadingDialog;
    String id, caribarang, namaid, sOrder, strnama, tanggalinput, kategori, sbulan, paksa, cafe, lokasi, pajak;
    String namatenant, lokasitenant, hargaprahu, hari, harga, hargaayunan1, hargaayunan2;
    EditText cari;
    TextView refreshLayout, home, plus, riwayat, btncari, namatenan, back, coffee, tnd, mct, sign, lain, tiket, tiket1;
    LinearLayout cari1, btncari1, kate, tiketprahu, tiketayunan;
    Boolean vcari;
    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();
    CoursesLVAdapter adapterlv;
    SimpleDateFormat df, df2, df3;
    private static BluetoothSocket btsocket;
    ArrayList<Note> arraylama = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        coursesLV = (ListView) findViewById(R.id.list_view);
        refreshLayout = (TextView) findViewById(R.id.refresh);
        home = (TextView) findViewById(R.id.home);
        plus = (TextView) findViewById(R.id.plus);
        riwayat = (TextView) findViewById(R.id.riwayat);
        btncari = (TextView) findViewById(R.id.btncari);
        cari = (EditText) findViewById(R.id.cari);
        cari1 = (LinearLayout) findViewById(R.id.cari1);
        btncari1 = (LinearLayout) findViewById(R.id.btncari1);
        back = (TextView) findViewById(R.id.back);
        kate = (LinearLayout) findViewById(R.id.kate);
        tiketprahu = (LinearLayout) findViewById(R.id.tiketprahu);
        tiketayunan = (LinearLayout) findViewById(R.id.tiketayunan);
        tiket = (TextView) findViewById(R.id.tiket);
        tiket1 = (TextView) findViewById(R.id.tiket1);

        coffee = (TextView) findViewById(R.id.coffee);
        tnd = (TextView) findViewById(R.id.tnd);
        mct = (TextView) findViewById(R.id.mct);
        sign = (TextView) findViewById(R.id.sign);
        lain = (TextView) findViewById(R.id.lain);

        tiketprahu.setVisibility(View.GONE);
        tiketayunan.setVisibility(View.GONE);

        df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        df2 = new SimpleDateFormat("MMMM", Locale.getDefault());
        df3 = new SimpleDateFormat("EEEE", Locale.getDefault());
        tanggalinput = df.format(new Date());
        sbulan = df2.format(new Date());
        hari = df3.format(new Date());




        loadingDialog = new ViewLoadingDialog(MainActivity.this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        namaid = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());
        TextView nama = (TextView) findViewById(R.id.nama);
        nama.setText(namaid);
        kategori = "Coffee";
        if(namaid.equals("cafelangit@baguslangit.com")){
            kategori = "Coffee";
        }else {
            if(namaid.equals("cafell2atas@baguslangit.com")){
                tiketprahu.setVisibility(View.VISIBLE);
                tiketayunan.setVisibility(View.VISIBLE);
            }
            kategori = "";
            kate.setVisibility(View.GONE);
        }

        dataModalArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        getDataorder();
        getTenant();
        refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                loadDatainListview();
                getDataorder();
            }
        });
        tiket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(MainActivity.this, tiketprahu.class);
                home.putExtra("harga", hargaprahu);
                startActivity(home);
            }
        });
        tiket1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(MainActivity.this, tiketayunan.class);
                home.putExtra("harga1", hargaayunan1);
                home.putExtra("harga2", hargaayunan2);
                startActivity(home);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(MainActivity.this, MainActivity.class);
                startActivity(home);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent home = new Intent(MainActivity.this, login.class);
                startActivity(home);
                finish();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(MainActivity.this, PrintNota.class);
                home.putExtra("orderid", "");
                home.putExtra("asal", "");
                home.putExtra("harga", hargaprahu);
                startActivity(home);
            }
        });
        riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(MainActivity.this, histori.class);
                startActivity(home);
                finish();
            }
        });
        cari.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                caribarang = cari.getText().toString();

                if(caribarang.length() >1){
                    loadCariNew(caribarang);
                }else {
                    dataModalArrayList = arraylama;
                }

                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);


            }
        });



        coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kategori = "Coffee";
                coffee.setTextColor(getResources().getColor(R.color.c1));
                tnd.setTextColor(getResources().getColor(R.color.c7));
                mct.setTextColor(getResources().getColor(R.color.c7));
                sign.setTextColor(getResources().getColor(R.color.c7));
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                loadDatainListview();
            }
        });

        tnd.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                coffee.setTextColor(getResources().getColor(R.color.c7));
                tnd.setTextColor(getResources().getColor(R.color.c1));
                mct.setTextColor(getResources().getColor(R.color.c7));
                sign.setTextColor(getResources().getColor(R.color.c7));
                kategori = "Tea & Drink";
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                loadDatainListview();
            }
        });

        mct.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                coffee.setTextColor(getResources().getColor(R.color.c7));
                tnd.setTextColor(getResources().getColor(R.color.c7));
                mct.setTextColor(getResources().getColor(R.color.c1));
                sign.setTextColor(getResources().getColor(R.color.c7));
                kategori = "Mocktail";
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                loadDatainListview();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                coffee.setTextColor(getResources().getColor(R.color.c7));
                tnd.setTextColor(getResources().getColor(R.color.c7));
                mct.setTextColor(getResources().getColor(R.color.c7));
                sign.setTextColor(getResources().getColor(R.color.c1));
                kategori = "Signature";
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                loadDatainListview();
            }
        });

        lain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kategori = "Lainnya";
                coffee.setTextColor(getResources().getColor(R.color.c7));
                tnd.setTextColor(getResources().getColor(R.color.c7));
                mct.setTextColor(getResources().getColor(R.color.c7));
                sign.setTextColor(getResources().getColor(R.color.c7));
                lain.setTextColor(getResources().getColor(R.color.c1));
                dataModalArrayList.clear();
                adapterlv = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                coursesLV.setAdapter(adapterlv);
                loadDatainListview();
            }
        });

        loadDatainListview();

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

    private void loadDatainListview() {
        arraylama = new ArrayList<>();
        // below line is use to get data from Firebase
        // firestore using collection in android.
        db.collection(namaid)
                .document(id)
                .collection("menu")
                .whereEqualTo("kategori", kategori)
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
                            ArrayList<menu> Menu = new ArrayList<>();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Note dataModal = d.toObject(Note.class);
                                String nama = d.getString("nama");
                                String harga = d.getString("harga");
                                menu m = new menu(nama, harga);
                                Menu.add(m);
                                // after getting data from Firebase we are
                                // storing that data in our array list
                                dataModalArrayList.add(dataModal);
                                arraylama.add(dataModal);
                                loadingDialog.dismiss();
                            }

                            //saveDataToFile(Menu);

                            // after that we are passing our array list to our adapter class.
                            CoursesLVAdapter adapter = new CoursesLVAdapter(MainActivity.this, dataModalArrayList);
                            coursesLV.setAdapter(adapter);
                            loadingDialog.dismiss();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            if (checkInternet()) {
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "Fail to load data..Cek Internet", Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
                loadingDialog.dismiss();
            }
        });
    }


    public void getAllDataFromCollection(String collectionName) {
        db.collection("tiketll1@baguslangit.com")
                .orderBy("")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Data berhasil diambil, berada dalam queryDocumentSnapshots
                        // QueryDocumentSnapshot berisi data dari setiap dokumen dalam koleksi
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Mendapatkan data dari setiap dokumen
                            String data = documentSnapshot.getString("nama_field"); // Ganti "nama_field" dengan nama field yang ingin Anda ambil
                            // Lakukan sesuatu dengan data
                            System.out.println(data);
                        }
                    }
                });
    }

    private void saveDataToFile(ArrayList<menu> Menu) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(Menu);

        // Cek apakah folder Download ada atau tidak
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "cafe");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = "cafell1c.txt";
        File file = new File(folder, fileName);

        try {
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.flush();
            writer.close();

            // Berhasil menyimpan file
            Toast.makeText(this, "Data berhasil disimpan di folder Download.", Toast.LENGTH_SHORT).show();

            // Kirim file menggunakan Intent
            sendFileToOtherApp(file);
        } catch (IOException e) {
            // Gagal menyimpan file
            Toast.makeText(this, "Gagal menyimpan data.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendFileToOtherApp(File file) {
        // Tentukan MIME type file yang akan dibagikan
        String mimeType = "text/plain";

        // Buat URI untuk file yang akan dibagikan
        Uri uri = FileProvider.getUriForFile(this, "com.baguslangit.cafelengkunglangit", file);

        // Buat Intent ACTION_SEND untuk mengirim file
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Kirim file ke aplikasi lain
        startActivity(Intent.createChooser(intent, "Bagikan file dengan"));
    }


    private void loadCariNew(String text){
        ArrayList<Note> arraynew = new ArrayList<>();
        for(Note note : arraylama){
            if(note.getNama().toLowerCase().contains(text.toLowerCase())){
                arraynew.add(note);
            }
        }

        dataModalArrayList = arraynew;

    }


    private void getTenant() {
        db.collection("admin")
                .document(namaid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot document,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (document != null && document.exists()) {
                            if (document.getString("nama") != null) {
                                cafe = document.getString("nama");
                                lokasi = document.getString("lokasi");
                                pajak = document.getString("pajak");
                                if(namaid.equals("cafell2atas@baguslangit.com")){
                                    hargaprahu = document.getString("harga1");
                                    hargaayunan1 = document.getString("hargaay1");
                                    hargaayunan2 = document.getString("hargaay2");
                                }
                            } else {
                                if (checkInternet()) {
                                    Toast.makeText(MainActivity.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                }
                            }
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });




    }

    public void setharga() {
        Map<String, Object> user = new HashMap<>();
        user.put("harga", harga);
        db.collection("admin")
                .document(namaid)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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
                                if (document.getString("status-order") != null) {
                                    sOrder = document.getString("status-order");
                                    strnama = document.getString("strnama");
                                    paksa = document.getString("paksa");
                                    if(!paksa.equals("login")){
                                        FirebaseAuth.getInstance().signOut();
                                        Intent home = new Intent(MainActivity.this, login.class);
                                        startActivity(home);
                                        finish();
                                    }
                                }else{
                                    sOrder = "print";
                                    strnama = "";
                                    paksa = "login";
                                }
                            } else {
                                sOrder = "print";
                                strnama = "";
                                paksa = "login";
                            }
                        } else {
                            if (checkInternet()) {
                                Toast.makeText(MainActivity.this, "Cek Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            } else {
                                Toast.makeText(MainActivity.this, "Tidak Ada Internet", Toast.LENGTH_LONG).show();
                                loadingDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
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
            View listitemView = convertView;
            if (listitemView == null) {
                listitemView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_card1, parent, false);
            }

            Note dataModal = getItem(position);


            TextView nama = listitemView.findViewById(R.id.title2Txt);
            TextView harga = listitemView.findViewById(R.id.harga);
            TextView jumlah = listitemView.findViewById(R.id.jmlh);
            TextView jumlahorder = listitemView.findViewById(R.id.jmlhttl);

            double hr = Double.parseDouble(dataModal.getHarga());

            nama.setText(dataModal.getNama());
            harga.setText(formatRupiah(hr));

            listitemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent home = new Intent(MainActivity.this, TambahPesan.class);
                    home.putExtra("nama", dataModal.getNama());
                    home.putExtra("harga", dataModal.getHarga());
                    home.putExtra("status-order", sOrder);
                    home.putExtra("strnama", strnama);
                    home.putExtra("paksa", paksa);
                    home.putExtra("asal", "main");
                    home.putExtra("namacafe", cafe);
                    home.putExtra("lokasi", lokasi);
                    home.putExtra("pajak", pajak);
                    startActivity(home);
                    Toast.makeText(MainActivity.this, dataModal.getNama(), Toast.LENGTH_SHORT).show();
                }
            });
            // below line is use to add item click listener
            // for our item of list view.

            return listitemView;
        }
    }


}