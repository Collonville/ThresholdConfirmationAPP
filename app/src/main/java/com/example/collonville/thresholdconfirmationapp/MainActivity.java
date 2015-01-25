package com.example.collonville.thresholdconfirmationapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat outImg;

    private SeekBar upperSeekBar,thresholdSeekBar;
    private TextView upperTxt,thresholdTxt;

    //ライブラリ初期化終了後に呼ばれるコールバック(非同期)
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        /**
         * ライブラリの初期化に成功したらカメラプレビューを開始する
         * @param status
         */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //画面消灯を防ぐ
        setContentView(R.layout.activity_main); //レイアウトに指定

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        upperTxt = (TextView)findViewById(R.id.upperTextView);
        thresholdTxt = (TextView) findViewById(R.id.thesh);

        upperSeekBar = (SeekBar) findViewById(R.id.upperSeekBar);
        upperSeekBar.setProgress(0); //初期値
        upperSeekBar.setMax(255); //最大値

        thresholdSeekBar = (SeekBar) findViewById(R.id.threshSeekBar);
        thresholdSeekBar.setProgress(0); //初期値
        thresholdSeekBar.setMax(255); //最大値

        //シークバーの値変更時に呼び出される
        upperSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * バーを動かした時
             * @param seekBar
             * @param i
             * @param b
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                upperTxt.setText(String.valueOf(upperSeekBar.getProgress()));
            }

            /**
             * バーに触れた時
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * バーから離した時
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * バーを動かした時
             * @param seekBar
             * @param i
             * @param b
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                thresholdTxt.setText(String.valueOf(thresholdSeekBar.getProgress()));
            }

            /**
             * バーに触れた時
             * @param seekBar
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /**
             * バーから離した時
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //非同期でライブラリの初期化を行う
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /**
     * カメラビュー開始時に呼ばれる
     * @param width -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    public void onCameraViewStarted(int width, int height) {
        //8bit符号なしデータ
        outImg = new Mat(height, width, CvType.CV_8UC1);
    }

    /**
     * カメラビュー終了時に呼ばれる
     */
    public void onCameraViewStopped() {
    }

    /**
     *フレーム枚をキャプチャ(CvCameraViewListener->引数:Mat inputFrame,CvCameraViewListener2->引数:CvCameraViewFrame inputFrame)
     * @param inputFrame
     * @return
     */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //入力画像(RGBA)をグレースケールに変換してoutImgに渡す
        Imgproc.cvtColor(inputFrame.rgba(), outImg, Imgproc.COLOR_RGBA2GRAY);

        //2値化
        Imgproc.threshold(outImg, outImg, thresholdSeekBar.getProgress(), upperSeekBar.getProgress(), Imgproc.THRESH_BINARY);
        return outImg;
    }
}
