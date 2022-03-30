package com.panchbhutas.shambho.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.panchbhutas.shambho.HomeActivity;
import com.panchbhutas.shambho.R;

import java.io.Console;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private ImageAdapter imgAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        GridView gridView = (GridView) root.findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class
        imgAdapter=new ImageAdapter(getActivity());
        gridView.setAdapter(imgAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                //Selected item is at index "position".
                Log.e("GRID", "Position: "+position);
                ((HomeActivity)getActivity()).loadObject(imgAdapter.mObjFileNames[position]);
            }
        });

        return root;
    }
}