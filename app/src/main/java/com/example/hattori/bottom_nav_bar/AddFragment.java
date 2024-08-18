package com.example.hattori.bottom_nav_bar;



import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hattori.CustomEditText;
import com.example.hattori.CustomTextView;
import com.example.hattori.R;
import com.example.hattori.database.AppDatabase;
import com.example.hattori.database.AppDatabaseClient;
import com.example.hattori.database.Word;
import com.example.hattori.database.WordDao;
import com.example.hattori.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

// ------------------------------
    // bind
    private FragmentAddBinding binding;

    // action bar menu
    private MenuProvider menuProvider;


    // camera
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri photoUri = null;


    // storage
    private ActivityResultLauncher<String> getContentLauncher;


    // 圖片擷取文字
    TextRecognizer engRecognizer, chineseRecognizer;
    InputImage image;
    Text englishResult = null;
    Text chineseResult = null;
    boolean finishEnglishResult = false, finishChineseResult = false;


    // CardView
    View wordCard;

    // database
    AppDatabase db;
    WordDao wordDao;


    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 初始化 ViewBinding
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        listener();
        initLauncher();
        initRecognizer();
        initDatabase();


        // action bar
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.add_action_bar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int menuItemId = menuItem.getItemId();
                if (menuItemId == R.id.take_photo) {
                    Toast.makeText(getContext(), "camera", Toast.LENGTH_SHORT).show();
                    checkCameraPermission();
                    return true;
                }
                else if (menuItemId == R.id.folder_upload) {
                    Toast.makeText(getContext(), "folder", Toast.LENGTH_SHORT).show();
                    getContentLauncher.launch("image/*");
                    return true;
                }
                return false;
            }

        };
        requireActivity().addMenuProvider(menuProvider);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 釋放 binding 資源
        binding = null;

        // 移除 MenuProvider
        requireActivity().removeMenuProvider(menuProvider);
    }

    public void listener() {
        // add button listener
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click_add();
            }
        });

        // save button listener
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click_save();
            }
        });

        binding.radioEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    binding.recognEnglishLayout.setVisibility(View.VISIBLE);
                    binding.recognChineseLayout.setVisibility(View.GONE);
                    showText();
                }
                else {
                    binding.recognChineseLayout.setVisibility(View.VISIBLE);
                    binding.recognEnglishLayout.setVisibility(View.GONE);
                    showText();
                }
            }
        });

        binding.textModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                showText();
            }
        });
    }

    public void click_add() {
        // 清空 word linearlayout 中的所有子視圖
        binding.wordCardview.removeAllViews();

        // get card item by xml
        wordCard = LayoutInflater.from(getContext()).inflate(R.layout.card_item, binding.wordCardview, false);


        // add to linearlayout
        binding.wordCardview.addView(wordCard);
    }

    public void click_save() {
        // 清空 word linearlayout 中的所有子視圖
        binding.wordCardview.removeAllViews();

        // 取得單字卡上的 widget
        EditText eWord = (EditText) wordCard.findViewById(R.id.card_word);
        EditText eKk = (EditText) wordCard.findViewById(R.id.card_kk);
        EditText eWordZh = (EditText) wordCard.findViewById(R.id.card_word_zh);
        EditText eSentence = (EditText) wordCard.findViewById(R.id.card_sentence_en);
        EditText eSentenceZh = (EditText) wordCard.findViewById(R.id.card_sentence_zh);
        EditText eAnswer = (EditText) wordCard.findViewById(R.id.card_answer);
        EditText eRemark = (EditText) wordCard.findViewById(R.id.card_remark);

        // 建立 Word
        Word word = new Word(
                eWord.getText().toString(),
                eKk.getText().toString(),
                eWordZh.getText().toString(),
                eSentence.getText().toString(),
                eSentenceZh.getText().toString(),
                eAnswer.getText().toString(),
                eRemark.getText().toString()
        );

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // 插入數據
                wordDao.insertWord(word);

                // 在主執行緒中更新 UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "新增完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initLauncher() {
        // 初始化相機權限請求的 ActivityResultLauncher
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean isGranted) {
                        // 有相機權限
                        if (isGranted) {
                            takePicture();
                        }
                        else {
                            Toast.makeText(getContext(), "相機權限被拒絕", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // 初始化拍照的 ActivityResultLauncher
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        // 拍照成功
                        if (result) {
                            // 顯示照片
                            ImageView imageView = binding.photo;
                            imageView.setImageURI(photoUri);
                            imageView.setVisibility(View.VISIBLE);

                            // 圖片擷取文字 - set InputImage
                            try {
                                image = InputImage.fromFilePath(getContext(), photoUri);
                                processImage();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(getContext(), "拍照失敗", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            // 處理選取的內容
                            binding.photo.setImageURI(uri);
                            binding.photo.setVisibility(View.VISIBLE);
                            photoUri = uri;

                            // 圖片擷取文字 - set InputImage
                            try {
                                image = InputImage.fromFilePath(getContext(), photoUri);
                                processImage();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }

    private void takePicture() {
        // 準備照片的保存位置
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        photoUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // 啟動相機
        takePictureLauncher.launch(photoUri);
    }

    private void checkCameraPermission() {
        if (getActivity().checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 有權限, 拍照
            takePicture();
        }
        else {
            // 無權限，則請求權限
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void initRecognizer() {
        engRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        chineseRecognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
    }


    // 辨識圖片，擷取文字
    private void processImage() {
        finishEnglishResult = false;
        finishChineseResult = false;

        Task<Text> engTaskResult =
                engRecognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                englishResult = visionText;
                                finishEnglishResult = true;
                                showResult();
                                click_add();
                                Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                                    }
                                });
        Task<Text> chiTaskResult =
                chineseRecognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                chineseResult = visionText;
                                finishChineseResult = true;
                                showResult();
                                Toast.makeText(getContext(), "yes", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Toast.makeText(getContext(), "failure", Toast.LENGTH_SHORT).show();
                                    }
                                });
    }

    private void showResult() {
        if (finishEnglishResult && finishChineseResult) {
            // 語言 radio group
            binding.radioEnglish.setChecked(true);
            binding.radioChinese.setChecked(false);
            binding.languageRadioGroup.setVisibility(View.VISIBLE);

            // text mode radio group
            binding.block.setChecked(true);
            binding.line.setChecked(false);
            binding.element.setChecked(false);
            binding.textModeRadioGroup.setVisibility(View.VISIBLE);

            // result layout
            binding.recognEnglishLayout.setVisibility(View.VISIBLE);
            binding.recognChineseLayout.setVisibility(View.GONE);

            // output text result
            showText();
        }
    }

    private void showText() {

        Text inputText = binding.radioEnglish.isChecked() ? englishResult : chineseResult;
        LinearLayout inputLayout = binding.radioEnglish.isChecked() ? binding.recognEnglishLayout : binding.recognChineseLayout;

        inputLayout.removeAllViews();

        // output mode
        if (binding.block.isChecked()) {
            showByBlock(inputText, inputLayout);
        }
        else if (binding.line.isChecked()) {
            showByLine(inputText, inputLayout);
        }
        else if (binding.element.isChecked()) {
            showByElement(inputText, inputLayout);
        }
    }

    private void showByBlock(Text result, LinearLayout layout) {
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            CustomEditText customEditText = (CustomEditText) LayoutInflater.from(getContext()).inflate(R.layout.recognize_text_item, null);
            // 設置 LayoutParams 並包含 margin
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10); // 設置 margin，單位為像素

            // 設置自訂的 LayoutParams 到 TextView
            customEditText.setLayoutParams(params);
            customEditText.setText(blockText);
            customEditText.setBackground(getContext().getDrawable(R.drawable.text_border_block));
            customEditText.setAllowKeyboard(false);
            setCustomEditTextListener(customEditText);
            layout.addView(customEditText);
        }
    }

    private void showByLine(Text result, LinearLayout layout) {
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                CustomEditText customEditText = (CustomEditText) LayoutInflater.from(getContext()).inflate(R.layout.recognize_text_item, null);
                // 設置 LayoutParams 並包含 margin
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(10, 10, 10, 10); // 設置 margin，單位為像素

                // 設置自訂的 LayoutParams 到 TextView
                customEditText.setLayoutParams(params);
                customEditText.setText(lineText);
                customEditText.setBackground(getContext().getDrawable(R.drawable.text_border_line));
                customEditText.setAllowKeyboard(false);
                setCustomEditTextListener(customEditText);
                layout.addView(customEditText);
            }
        }
    }

    private void showByElement(Text result, LinearLayout layout) {
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();

                    CustomEditText customEditText = (CustomEditText) LayoutInflater.from(getContext()).inflate(R.layout.recognize_text_item, null);
                    // 設置 LayoutParams 並包含 margin
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(10, 10, 10, 10); // 設置 margin，單位為像素

                    // 設置自訂的 LayoutParams 到 TextView
                    customEditText.setLayoutParams(params);
                    customEditText.setText(elementText);
                    customEditText.setBackground(getContext().getDrawable(R.drawable.text_border_element));
                    customEditText.setAllowKeyboard(false);
                    setCustomEditTextListener(customEditText);
                    layout.addView(customEditText);
                }
            }
        }
    }

    private void setCustomEditTextListener(CustomEditText customEditText) {
        customEditText.setOnCustomOptionClickListener(new CustomEditText.OnCustomOptionClickListener() {
            @Override
            public void onCustomOptionClick(int optionId) {
                if (optionId == 1) {
                    // 單字, Answer
                    EditText editTextWord = (EditText) wordCard.findViewById(R.id.card_word);
                    EditText editTextAns = (EditText) wordCard.findViewById(R.id.card_answer);
                    editTextWord.setText(customEditText.getText().toString());
                    editTextAns.setText(customEditText.getText().toString());
                }
                else if (optionId == 2){
                    // KK音標
                    EditText editText = (EditText) wordCard.findViewById(R.id.card_kk);
                    editText.setText(customEditText.getText().toString());
                }
                else if (optionId == 3){
                    // 單字中文
                    EditText editText = (EditText) wordCard.findViewById(R.id.card_word_zh);
                    editText.setText(customEditText.getText().toString());
                }
                else if (optionId == 4){
                    // KK音標
                    EditText editText = (EditText) wordCard.findViewById(R.id.card_sentence_en);
                    editText.setText(customEditText.getText().toString());
                }
                else if (optionId == 5){
                    // KK音標
                    EditText editText = (EditText) wordCard.findViewById(R.id.card_sentence_zh);
                    editText.setText(customEditText.getText().toString());
                }
            }
        });
    }

    private void initDatabase() {
        db = AppDatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase();
        wordDao = db.wordDao();
    }
}