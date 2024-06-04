package com.liangchao.camera2glcodec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liangchao.camera2glcodec.util.FileUtil;
import com.liangchao.camera2glcodec.weight.GlRenderView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button btStartRecord,btStopRecord;
    private GlRenderView glRenderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btStartRecord = findViewById(R.id.startRecord);
        btStopRecord = findViewById(R.id.stopRecord);
        glRenderView = findViewById(R.id.render_view);
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(checkSelfPermission  == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},0);
        }

        btStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                File file = null;
                try {
                    file = FileUtil.createFile(MainActivity.this, false, "opengl", sdf.format(new Date(System.currentTimeMillis())) + ".mp4", 1074000000);
                    glRenderView.setSavePath(file.getAbsolutePath());
                } catch (FileUtil.NoExternalStoragePermissionException e) {
                    throw new RuntimeException(e);
                } catch (FileUtil.NoExternalStorageMountedException e) {
                    throw new RuntimeException(e);
                } catch (FileUtil.DirHasNoFreeSpaceException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                glRenderView.startRecord();
            }
        });

        btStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glRenderView.stopRecord();
            }
        });

    }
}