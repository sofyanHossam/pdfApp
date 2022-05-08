package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bookapp.databinding.ActivityDashboardUserBinding;
import com.example.bookapp.databinding.ActivityDashoardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityDashboardUserBinding binding;
    public ArrayList<ModelCategory>categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;
     String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent =getIntent();
        name = intent.getStringExtra("ame");


        auth=FirebaseAuth.getInstance();
        checkUser();
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auth.getCurrentUser()==null){
                    Toast.makeText(DashboardUserActivity.this, "ضيف ... يرجي تسجيل الدخول", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
                }
            }
        });



        setUpViewpagerAdapter(binding.viewpager);
        binding.tab.setupWithViewPager(binding.viewpager);
        binding.Ulogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (auth.getCurrentUser()!=null)
                    {auth.signOut();
                        startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));}
                else {
                    startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));
                }
            }
        });

    }

    private void setUpViewpagerAdapter(ViewPager viewpager){
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);

        categoryArrayList=new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                ModelCategory modelAll=new ModelCategory("01","كل الاقسام","","1");

                categoryArrayList.add(modelAll);
                viewPagerAdapter.addFragment(BookUserFragment.newInstance(""+modelAll.getId(),""+modelAll.getCategory(),""+modelAll.getUid())
                 ,modelAll.getCategory());

                viewPagerAdapter.notifyDataSetChanged();

                for( DataSnapshot ds:snapshot.getChildren()){
                    ModelCategory modelCategory=ds.getValue(ModelCategory.class);
                    categoryArrayList.add(modelCategory);

                    viewPagerAdapter.addFragment(BookUserFragment.newInstance(
                            ""+modelCategory.getId(),
                            ""+modelCategory.getCategory(),
                            ""+modelCategory.getUid()),
                   modelCategory.getCategory() );
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewpager.setAdapter(viewPagerAdapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<BookUserFragment>fragmentArrayList=new ArrayList<>();
        private ArrayList<String> fragmentTitleList =new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,Context context) {
            super(fm, behavior);
            this.context=context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }
        private void addFragment (BookUserFragment fragment,String title){
            fragmentArrayList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
    private void checkUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(auth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name =""+snapshot.child("name").getValue();
                            binding.UNameTv.setText(""+name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        } else if(user ==null)
            {
                binding.UNameTv.setText(name);
            }

    }
}