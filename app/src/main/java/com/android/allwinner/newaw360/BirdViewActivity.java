package com.android.allwinner.newaw360;

import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.allwinner.newaw360.media.AW360MediaManager;
import com.android.allwinner.newaw360.utils.DisplayUtil;

public class BirdViewActivity extends AppCompatActivity {
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mSurfaceView;
    private TextureView mTextureView;
    private float mPreviewRate = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bird_view);

        boolean result = AW360MediaManager.getInstance().init(getApplicationContext());
        if (result) {
            initSurfaceView();
//            initTextureView();
        }else {
            finish();
        }
    }

    private void initSurfaceView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initViewParams();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AW360MediaManager.getInstance().startPreview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AW360MediaManager.getInstance().stopPreview();
            }
        });
    }

    private void initTextureView() {
        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                AW360MediaManager.getInstance().startPreview(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                AW360MediaManager.getInstance().stopPreview();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void initViewParams() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mSurfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        mPreviewRate = DisplayUtil.getScreenRate(this); // 默认全屏的比例预览
        mSurfaceView.setLayoutParams(params);

    }
}
