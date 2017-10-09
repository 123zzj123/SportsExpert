package cn.kingsleychung.sportsexpert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PicturePreview extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);

        Bundle bundle = this.getIntent().getExtras();
        String imagePath = bundle.getString("imagePath");

        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ((ImageView)findViewById(R.id.pic_preview)).setImageBitmap(bm);

        findViewById(R.id.upload).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload:
                Intent intent = new Intent();
                intent.setClass(PicturePreview.this, KnowledgeActivity.class);
                PicturePreview.this.startActivity(intent);
                break;
        }
    }
}
