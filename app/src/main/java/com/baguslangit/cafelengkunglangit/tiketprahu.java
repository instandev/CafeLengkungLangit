package com.baguslangit.cafelengkunglangit;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
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

public class tiketprahu extends AppCompatActivity {
    FirebaseFirestore db;
    ViewLoadingDialog loadingDialog;
    SimpleDateFormat df, df1, df3;
    String id, namaid, tanggalinput, num, num1, num2, strnama, jumlahtiket, jaminput, jamc, hargaprahu;
    TextView print, nomorantri;
    EditText jumlah;
    double jmlh, ttl, jum = 0;
    boolean cetakk = false;

    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiketprahu);


        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        hargaprahu = intent.getStringExtra("harga");
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        }

        id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        namaid = Objects.requireNonNull(mAuth.getCurrentUser().getEmail());

        loadingDialog = new ViewLoadingDialog(tiketprahu.this);

        df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        df1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        df3 = new SimpleDateFormat("HH-mm-ss-SSS", Locale.getDefault());
        tanggalinput = df.format(new Date());
        loadDatasemua();
        print = (TextView) findViewById(R.id.print);
        jumlah = (EditText) findViewById(R.id.jumlah);
        nomorantri = (TextView) findViewById(R.id.nomorantri);



        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cetakk = true;
                tanggalinput = df.format(new Date());
                jaminput = df1.format(new Date());
                jamc = df3.format(new Date());
                loadingDialog.setPesan("Mohon Tunggu Sebentar");
                loadingDialog.show();
                jumlahtiket = jumlah.getText().toString();
                jmlh = Double.parseDouble(jumlahtiket);
                double hrg = Double.parseDouble(hargaprahu);
                ttl = jmlh*hrg;
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
                printBill();
                //cetak();
            }
        });
    }


    protected void printBill() {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        } else {
           cetak();
        }
    }

    private void cetak(){
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
            cetakk = false;
            printCustom("Lengkung Langit 2", 2, 1);
            printCustom("Sumber Agung, Kemiling,", 1, 1);
            printCustom("Bandar Lampung", 1, 1);
            printCustom("PK-" + num + num1 + num2, 1, 1);
            printCustom("--------------------------------", 1, 1);
            printCustom("NOMOR  ANTRI", 3, 1);
            printCustom(String.format("%.0f", jum+1), 3, 1);
            printCustom("Tiket Prahu Kaca", 3, 1);
            printCustom("--------------------------------\n", 1, 1);
            printCustom("Tiket ini berlaku untuk 1 Kali", 1, 1);
            printCustom("Harga  : "+formatRupiah(Double.valueOf(hargaprahu)), 1, 0);
            printCustom("Jumlah : "+String.format("%.0f", jmlh), 1, 0);
            printCustom("Total  : " + formatRupiah(ttl), 1, 0);
            String msg3 = "\n-----" + tanggalinput + "----" + jaminput + "-----" + "\n";
            printCustom(msg3, 1, 1);
            upData();
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

    public void upData() {
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

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x15}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
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
                    outputStream.write(bb3);
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

    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = DeviceList.getSocket();
            if(btsocket!=null && cetakk){
                cetak();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDatasemua() {
        db.collection(namaid)
                .document(id)
                .collection("prahukaca")
                .whereEqualTo("tanggalinput", tanggalinput)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        jum = 0;
                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            double j = Double.parseDouble(doc.getString("jml"));
                            jum = jum + j;
                        }
                        nomorantri.setText("Nomor antri terakhir : " + String.format("%.0f", jum));
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
    }
}