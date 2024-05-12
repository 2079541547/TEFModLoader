package silkways.terraria.toolbox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import silkways.terraria.toolbox.databinding.WelcomeMainBinding;

public class Welcome extends AppCompatActivity {
    private WelcomeMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WelcomeMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}