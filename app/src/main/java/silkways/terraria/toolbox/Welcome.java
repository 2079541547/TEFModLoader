package silkways.terraria.toolbox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import silkways.terraria.toolbox.databinding.WelcomeMainBinding;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        silkways.terraria.toolbox.databinding.WelcomeMainBinding binding = WelcomeMainBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());
    }
}