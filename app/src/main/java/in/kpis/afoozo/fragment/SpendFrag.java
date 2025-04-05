package in.kpis.afoozo.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kpis.afoozo.R;
import in.kpis.afoozo.activity.ReservationActi;
import com.kpis.afoozo.databinding.FragmentSpendBinding;
import in.kpis.afoozo.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpendFrag extends Fragment {

    private View view;

    private Context mContext;

    private FragmentSpendBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_spend, container, false);
        view = binding.getRoot();

        initialize();
        return view;
    }

    private void initialize() {
        binding.cvPayAtStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReservationActi.class);
                intent.putExtra(Constants.IS_FROM_WALLET, true);
                startActivity(intent);
            }
        });
    }

}
