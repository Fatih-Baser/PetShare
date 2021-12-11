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

        binding.cats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Cats");
            }
        });

        binding.dogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Dogs");
            }
        });

        binding.birds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Birds");
            }
        });

        binding.rabbits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Rabbits");
            }
        });
        binding.horses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Horses");
            }
        });
        binding.ferrets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Ferrets");
            }
        });
        binding.fish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Fish");
            }
        });
        binding.pigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Guinea Pigs");
            }
        });
        binding.rats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Rats and Mice");
            }
        });

        binding.amphibians.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Amphibians");
            }
        });

        binding.reptiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Reptiles");
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