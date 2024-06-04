package com.muhammadbillieelianjbusrs.ambatufeast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;

import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.CartDatabase;
import com.muhammadbillieelianjbusrs.ambatufeast.Database.LocalCartDataSource;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;

import org.w3c.dom.Text;

import java.util.Calendar;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class PlaceOrderActivity extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    EditText edt_date;
    TextView txt_total_cash;
    TextView txt_user_email;
    TextView txt_user_address;
    TextView txt_new_address;
    Button btn_add_new_address;
    CheckBox ckb_default_address;
    RadioButton rdi_cod;
    RadioButton rdi_online_payment;
    Button btn_proceed;
    Toolbar toolbar;
    INodeJS ambatufeastAPI;
    android.app.AlertDialog dialog;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    boolean isSelectedDate = false, isAddNewAddress=false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        edt_date = findViewById(R.id.edt_data);
        txt_total_cash = findViewById(R.id.txt_total_cash);
        txt_user_email = findViewById(R.id.txt_user_email);
        txt_user_address = findViewById(R.id.txt_user_address);
        txt_new_address = findViewById(R.id.txt_new_address);
        btn_add_new_address = findViewById(R.id.btn_add_new_address);
        ckb_default_address = findViewById(R.id.ckb_default_address);
        rdi_cod = findViewById(R.id.rdi_cod);
        rdi_online_payment = findViewById(R.id.rdi_online_payment);
        btn_proceed = findViewById(R.id.btn_proceed);
        toolbar = findViewById(R.id.toolbar);
        init();
        initView();
    }

    private void initView(){
        txt_user_email.setText(Common.currentUser.getEmail());
        txt_user_address.setText(Common.currentUser.getAddress());
        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_add_new_address.setOnClickListener(v -> {
            isAddNewAddress=true;
            ckb_default_address.setChecked(false);

            View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
                    .inflate(R.layout.layout_add_new_address, null);
            EditText edt_new_address = (EditText) layout_add_new_address.findViewById(R.id.edt_add_new_address);
            edt_new_address.setText(txt_new_address.getText().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Add New Address")
                    .setView(layout_add_new_address)
                    .setNegativeButton("CANCEL",(dialogInterface, i)->dialogInterface.dismiss())
                    .setPositiveButton("ADD",(dialogInterface,i)->txt_new_address.setText(edt_new_address.getText().toString()));

            androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
            addNewAddressDialog.show();
        });

        edt_date.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSelectedDate)
                {
                    Toast.makeText(PlaceOrderActivity.this, "Please select Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isAddNewAddress)
                {
                    if(!ckb_default_address.isChecked())
                    {
                        Toast.makeText(PlaceOrderActivity.this, "Please choose default address or set new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(rdi_cod.isChecked())
                {
                    //process cod
                }
                else if(rdi_online_payment.isChecked())
                {
                    //process online payment
                }
            }
        });
    }

    private void init(){
        ambatufeastAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(INodeJS.class);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectedDate = true;

        edt_date.setText(new StringBuilder("")
                .append(monthOfYear)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));
    }
}