package com.example.hattori.bottom_nav_bar;



import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hattori.R;
import com.example.hattori.databinding.FragmentAddBinding;

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


    // camera
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri photoUri = null;


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

        // action bar
        requireActivity().addMenuProvider(new MenuProvider() {
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
                    return true;
                }
                return false;
            }
        });



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 釋放 binding 資源
        binding = null;
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
    }

    public void click_add() {
        // 清空 word linearlayout 中的所有子視圖
        binding.wordCardview.removeAllViews();

        // get card item by xml
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, binding.wordCardview, false);

        // add to linearlayout
        binding.wordCardview.addView(cardView);
    }

    public void click_save() {
        // 清空 word linearlayout 中的所有子視圖
        binding.wordCardview.removeAllViews();
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
                        }
                        else {
                            Toast.makeText(getContext(), "拍照失敗", Toast.LENGTH_SHORT).show();
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

}