package com.idon.emergencmanagement.view.customview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.idon.emergencmanagement.R;
import com.idon.emergencmanagement.model.UserFull;
import com.zine.ketotime.util.Constant;

import org.parceler.Parcels;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetDescDialog extends BottomSheetDialogFragment {

    TextView nameTV;
    CircleImageView img;
    TextView telTV;
    TextView typeTV;

    UserFull data;

    public BottomSheetDescDialog(UserFull data) {
        this.data = data;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_desc, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        img = view.findViewById(R.id.avatarImg);
        nameTV = view.findViewById(R.id.nameTV);
        telTV = view.findViewById(R.id.telTV);
        typeTV = view.findViewById(R.id.typeTV);



        nameTV.setText(data.getDisplay_name());
        typeTV.setText(data.getType());
        telTV.setText("Tel. "+data.getTel());
        Glide.with(getContext()).load(Constant.BASE_URL+""+data.getAvatar()).into(img);

        //                viewmore.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        Intent intent = new Intent(getContext(), WorningDescActivity.class);
//                        intent.putExtra("data",Parcels.wrap(data.getStation()));
//                        startActivity(intent);
//
//
//
//                    }
//                }
//        );



//        Glide.with(getContext()).load(data.getU_avatar()).into(img);
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), ShopDescActivity.class);
//                intent.putExtra("data", Parcels.wrap(data));
//                startActivity(intent);
//                dismiss();
//
//            }
//        });


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
