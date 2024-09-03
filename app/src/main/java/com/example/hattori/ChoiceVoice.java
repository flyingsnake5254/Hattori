package com.example.hattori;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hattori.Utils.SpeechUtil;

public class ChoiceVoice extends AppCompatActivity {

    com.example.hattori.databinding.ActivityChoiceVoiceBinding binding;

    private ListView listZh, listEn;

    private SpeechUtil speechUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.hattori.databinding.ActivityChoiceVoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listener();
    }

    private void init() {
        listZh = (ListView) binding.choiceVoiceZhList;
        listZh.setAdapter(new ArrayAdapter<>(ChoiceVoice.this, android.R.layout.simple_list_item_1, GlobalVariable.VOICE_ZH));

        listEn = (ListView) binding.choiceVoiceEnList;
        listEn.setAdapter(new ArrayAdapter<>(ChoiceVoice.this, android.R.layout.simple_list_item_1, GlobalVariable.VOICE_EN));

        speechUtil = new SpeechUtil(this);
    }

    private void listener() {
        listZh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.CURRENT_ZH_VOICE = GlobalVariable.VOICE_ZH[i];
                speechUtil.setVoice(GlobalVariable.CURRENT_ZH_VOICE);
                speechUtil.speak("真相永遠只有一個");
            }
        });

        listEn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GlobalVariable.CURRENT_EN_VOICE = GlobalVariable.VOICE_EN[i];
                speechUtil.setVoice(GlobalVariable.CURRENT_EN_VOICE);
                speechUtil.speak("Jolly Roger");
            }
        });
    }
}