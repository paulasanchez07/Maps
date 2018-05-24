package com.hacu1.icropmaps.activities;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.hacu1.icropmaps.R;
import com.hacu1.icropmaps.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    android.support.v4.app.Fragment currentFragment; //Para llevar el control de que fragmento se esta cargando

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //tambien se controla cuando cambie la rotacion del dispositivo se mantenga donde estaba.
        Toast.makeText(this,"Llamado onCreate",Toast.LENGTH_SHORT).show();
        if (savedInstanceState ==null){//Si es la primera vez que se crea el activity
            currentFragment = new WelcomeFragment(); //El fragment que aparece por defecto al inicio
            changeFragment(currentFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu); //inflamos
        return super.onCreateOptionsMenu(menu);
    }

    //Acciones

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            //Accion a ejecutar en el menu welcome
            case R.id.menu_welcome:
                currentFragment = new WelcomeFragment();
                break;
            case R.id.menu_map:
                currentFragment = new com.hacu1.icropmaps.fragments.MapFragment();
                break;
        }
        changeFragment(currentFragment);
        return super.onOptionsItemSelected(item);
    }

    //Cambia de fragment
    private void changeFragment(android.support.v4.app.Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }
}
