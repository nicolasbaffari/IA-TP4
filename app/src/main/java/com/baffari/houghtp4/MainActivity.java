package com.baffari.houghtp4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btLineas;
    private Button btCirculos;
    private ImageView ivTransformada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btCirculos= findViewById(R.id.btCirculos);
        btCirculos.setOnClickListener(this);
        btLineas= findViewById(R.id.btLineas);
        btLineas.setOnClickListener(this);
        ivTransformada = findViewById(R.id.ivTransformada);

        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV successfully loaded.");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btCirculos) {
            detectarCirculos(getApplicationContext());
        }
        if (view.getId()==R.id.btLineas) {
            detectarLineas(getApplicationContext());
        }
    }

    public void detectarCirculos(Context context) {
        // Setea la imagen
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.imagen_de_prueba);
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        // Convertir a escala de grises
        Mat gris = new Mat();
        Imgproc.cvtColor(src, gris, Imgproc.COLOR_BGR2GRAY);

        // Suavizar la imagen para reducir el ruido
        Imgproc.GaussianBlur(gris, gris, new Size(9, 9), 2, 2);

        // Detectar círculos usando la transformada de Hough
        Mat circulos = new Mat();
        Imgproc.HoughCircles(gris, circulos, Imgproc.HOUGH_GRADIENT, 1.2,
                gris.rows() / 16, 100, 30, 300 , 380);

        // Dibujar los círculos detectados
        for (int i = 0; i < circulos.cols(); i++) {
            double[] circulo = circulos.get(0, i);
            Point centro = new Point(circulo[0], circulo[1]);
            int radio = (int) Math.round(circulo[2]);
            Imgproc.circle(src, centro, radio, new Scalar(0, 255, 0), 30);
        }

        // Mostrar resultado
        Bitmap resultBitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, resultBitmap);
        ivTransformada.setImageBitmap(resultBitmap);
    }


    public void detectarLineas(Context context) {
        // Setea la imagen
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.imagen_de_prueba);
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        // Convertir a escala de grises
        Mat gris = new Mat();
        Imgproc.cvtColor(src, gris, Imgproc.COLOR_BGR2GRAY);

        // Aplicar detección de bordes
        Mat bordes = new Mat();
        Imgproc.Canny(gris, bordes, 50, 150, 3, false);

        // Detectar líneas usando la transformada de Hough
        Mat lineas = new Mat();
        Imgproc.HoughLinesP(bordes, lineas, 1, Math.PI / 180, 100, 50, 10);

        // Dibujar las líneas detectadas
        for (int i = 0; i < lineas.rows(); i++) {
            double[] linea = lineas.get(i, 0);
            Point pt1 = new Point(linea[0], linea[1]);
            Point pt2 = new Point(linea[2], linea[3]);
            Imgproc.line(src, pt1, pt2, new Scalar(0, 255, 0), 30);
        }

        // Mostrar resultado
        Bitmap resultBitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, resultBitmap);
        ivTransformada.setImageBitmap(resultBitmap);
    }
}



