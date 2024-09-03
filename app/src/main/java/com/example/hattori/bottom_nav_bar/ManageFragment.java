package com.example.hattori.bottom_nav_bar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hattori.GlobalVariable;
import com.example.hattori.MainActivity;
import com.example.hattori.R;
import com.example.hattori.database.AppDatabase;
import com.example.hattori.database.AppDatabaseClient;
import com.example.hattori.database.Word;
import com.example.hattori.database.WordDao;
import com.example.hattori.databinding.FragmentAddBinding;
import com.example.hattori.databinding.FragmentManageBinding;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    // bind
    private FragmentManageBinding binding;


    // 資料庫
    AppDatabase db;
    WordDao wordDao;
    List<Word> wordList;


    // 目前頁面第一筆資料的 index
    int currentHeadIndex = 0;

    // 單頁顯示資料筆數
    int DATA_SIZE = GlobalVariable.PAGE_ITEM_SIZE;

    public ManageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageFragment newInstance(String param1, String param2) {
        ManageFragment fragment = new ManageFragment();
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
        binding = FragmentManageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initDatabase(); // 資料庫初始化
        listener();


        return view;
    }

    // 資料庫初始化
    private void initDatabase() {
        // 參數初始化
        db = AppDatabaseClient.getInstance(getContext()).getAppDatabase();
        wordDao = db.wordDao();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // 取得 Words
                wordList = wordDao.getAllWords();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initWordLayout(); // 單字清單 layout 初始化
                    }
                });
            }
        });
    }


    // 單字清單初始化
    private void initWordLayout() {
        // 清除 word layout 所有 widget
        binding.manageWordLayout.removeAllViews();

        for (int i = 0 ; (i < DATA_SIZE) && currentHeadIndex + i < (wordList.size()) ; i ++) {
            // 取得當前 Word
            Word currentWord = wordList.get(currentHeadIndex + i);

            // 建立單字卡 view
            View view = LayoutInflater.from(getContext()).inflate(R.layout.card_item_manage, binding.manageWordLayout, false);

            // 取得單字卡上的 widget
            EditText eWord = (EditText) view.findViewById(R.id.card_word);
            EditText eKk = (EditText) view.findViewById(R.id.card_kk);
            EditText eWordZh = (EditText) view.findViewById(R.id.card_word_zh);
            EditText eSentence = (EditText) view.findViewById(R.id.card_sentence_en);
            EditText eSentenceZh = (EditText) view.findViewById(R.id.card_sentence_zh);
            EditText eAnswer = (EditText) view.findViewById(R.id.card_answer);
            EditText eRemark = (EditText) view.findViewById(R.id.card_remark);
            ShapeableImageView btnEdit = (ShapeableImageView) view.findViewById(R.id.manage_edit);

            // 設置 value
            eWord.setText(currentWord.getWord());
            eKk.setText(currentWord.getKk());
            eWordZh.setText(currentWord.getWord_zh());
            eSentence.setText(currentWord.getSentence());
            eSentenceZh.setText(currentWord.getSentence_zh());
            eAnswer.setText(currentWord.getAnswer());
            eRemark.setText(currentWord.getRemark());

            // 編輯按鈕設置監聽
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 創建並顯示一個 AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("請選擇編輯動作")
                            .setMessage("請選擇您要進行的動作")
                            .setPositiveButton("儲存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 處理儲存動作
                                    Word newWord = new Word(
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
                                            wordDao.deleteWord(currentWord);
                                            wordDao.insertWord(newWord);
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "儲存完成", Toast.LENGTH_SHORT).show();
                                                    initDatabase();
                                                }
                                            });
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("刪除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 處理刪除動作
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            wordDao.deleteWord(eWord.getText().toString());
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "刪除完成", Toast.LENGTH_SHORT).show();
                                                    initDatabase();
                                                }
                                            });
                                        }
                                    });
                                }
                            })
                            .setCancelable(true); // 設置是否允許取消對話框

                    // 顯示對話框
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            // 添加到 layout
            binding.manageWordLayout.addView(view);
        }
    }


    private void listener() {
        // 上一頁
        binding.managePrePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentHeadIndex != 0) {
                    currentHeadIndex -= DATA_SIZE;
                    initWordLayout();
                }
            }
        });

        // 下一頁
        binding.manageNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentHeadIndex + DATA_SIZE < wordList.size()) {
                    currentHeadIndex += DATA_SIZE;
                    initWordLayout();
                }
            }
        });
    }
}