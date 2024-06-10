package com.muhammadbillieelianjbusrs.ambatufeast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.muhammadbillieelianjbusrs.ambatufeast.Common.Common;
import com.muhammadbillieelianjbusrs.ambatufeast.Model.User;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.INodeJS;
import com.muhammadbillieelianjbusrs.ambatufeast.Retrofit.RetrofitClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.experimental.ExperimentalObjCRefinement;
import retrofit2.Retrofit;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.rxjava3.core.Observable;




public class MainActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    EditText edt_email, edt_password;
    MaterialButton btn_register,btn_login;


    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init API
        Retrofit retrofit = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT);
        myAPI = retrofit.create(INodeJS.class);

        btn_login = (MaterialButton)findViewById(R.id.login_button);
        btn_register = (MaterialButton)findViewById(R.id.register_button);

        edt_email = findViewById(R.id.edt_email);
        edt_password =findViewById(R.id.edt_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(edt_email.getText().toString(),edt_password.getText().toString());
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(edt_email.getText().toString(),edt_password.getText().toString());
            }
        });

    }

    private void registerUser(String email, String password) {
        View enter_name_view = LayoutInflater.from(this).inflate(R.layout.enter_name_layout, null);
        MaterialEditText edit_name = enter_name_view.findViewById(R.id.edt_name);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Register");
        builder.setView(enter_name_view);
        builder.setMessage("One more step");
        builder.setIcon(R.drawable.ic_user);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Register", (dialog, which) -> {
            String name = edit_name.getText().toString();
            if (name != null) {
                compositeDisposable.add(myAPI.registerUser(email, name, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                            saveUserInformation(email, name); // Menyimpan informasi pengguna setelah registrasi
                            Common.currentUser = new User(email, name, ""); // Isi alamat jika tersedia
                            navigateToHomeActivity();
                        }, error -> {
                            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
            }
        });



        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void loginUser(String email, String password) {
        compositeDisposable.add(
                myAPI.loginUser(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            if (user.contains("encrypted_password")) {
                                String userName = extractUserName(user);
                                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                saveUserInformation(email, userName);

                                Common.currentUser = new User(email, userName, "");
                                navigateToHomeActivity();
                            } else {
                                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }


    private void navigateToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();  // Jika Anda ingin mengakhiri MainActivity setelah berpindah
    }

    private void saveUserInformation(String email, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserEmail", email);
        editor.putString("UserName", name);
        editor.apply();
    }


    private String extractUserName(String response) {
        // Implementasi sederhana jika respons adalah JSON dan Anda memiliki library JSON
        try {
            JSONObject jsonObj = new JSONObject(response);
            return jsonObj.getString("name");  // Pastikan kunci "name" sesuai dengan yang dikirim server
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



}