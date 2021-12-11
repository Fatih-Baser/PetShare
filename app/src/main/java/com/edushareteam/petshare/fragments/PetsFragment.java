package com.edushareteam.petshare.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edushareteam.petshare.R;
import com.edushareteam.petshare.activities.FilterActivity;
import com.edushareteam.petshare.databinding.FragmentPetsBinding;
import com.edushareteam.petshare.databinding.FragmentProductsBinding;

/**
 * A simple {@link Fragment} subclass.

 * create an instance of this fragment.
 */
public class PetsFragment extends Fragment {
    private FragmentPetsBinding binding;
    public PetsFragment() {
        // Required empty public constructor
    }
    //TODO: DINAMIK YAPP
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPetsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.fenbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Fen Bilimleri");
            }
        });

        binding.eItimbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Eğitim Bilimleri");
            }
        });

        binding.dilveedebiyat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Dil ve Edebiyat");
            }
        });

        binding.yabancDiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Yabancı Diller");
            }
        });
        binding.mimarliK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Mimarlık");
            }
        });
        binding.teknolojivemuhendislkik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Mühendislik");
            }
        });
        binding.guzelsanatlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Güzel Sanatlar");
            }
        });
        binding.iktisat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("İktisadi Bilimler");
            }
        });
        binding.sporbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Spor Bilimleri");
            }
        });

        return view;
    }

    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FilterActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

}