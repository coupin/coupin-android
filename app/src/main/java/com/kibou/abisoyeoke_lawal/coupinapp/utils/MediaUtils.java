package com.kibou.abisoyeoke_lawal.coupinapp.utils;

import android.content.res.AssetFileDescriptor;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.view.TextureView;
import android.widget.FrameLayout;

public class MediaUtils {
    // Video measurement
    private static float videoWidth;
    private static float videoHeight;

    /**
     * Used to calculate the video size
     * @param assetFileDescriptor
     */
    public static void calculateVideoSize(AssetFileDescriptor assetFileDescriptor) {
        try {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());

        String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

        videoWidth = Float.parseFloat(width);
        videoHeight = Float.parseFloat(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update video size based on changes
     * @param width
     * @param height
     * @param mTextureView
     */
    public static void updateVideoSize(int width, int height, TextureView mTextureView) {
        float scaleX = 1.5f;
        float scaleY = 1.5f;

        if(videoWidth > width && videoHeight > height) {
            scaleX = videoWidth / width;
            scaleY = videoHeight / height;
        } else if(videoWidth < width && videoHeight < height) {
            scaleX = width / videoWidth;
            scaleY = height /videoHeight;
        } else if( width > videoHeight) {
            scaleY = (width / videoWidth) / (height / videoHeight);
        } else if( height > videoHeight) {
            scaleX = (height / videoHeight) / (width / videoWidth);
        }

        // Calculate where to crop from or pivot point, here it is center
        int pivotX = width / 2;
        int pivotY = height / 2;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotX, pivotY);

        mTextureView.setTransform(matrix);
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
    }
}
