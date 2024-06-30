package com.baffari.houghtp4;

/**
 *
 * @author Nico Baffari
 */
import android.graphics.Bitmap;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;

public class TransformadaHoughLineas {
    private int ancho, alto;
    private int[][] acumulador;
    private int maxDist;
    private List<int[]> lineas;
    private LineasTransformacionListener listener;

    public interface LineasTransformacionListener {
        void onLineasTransformadas(List<int[]> lineas);
    }

    public TransformadaHoughLineas(Bitmap imagen, LineasTransformacionListener listener) {
        ancho = imagen.getWidth();
        alto = imagen.getHeight();
        maxDist = (int) Math.sqrt(ancho * ancho + alto * alto);
        acumulador = new int[2 * maxDist][180];
        lineas = new ArrayList<>();
        this.listener = listener;
    }

    public void iniciarTransformacion(Bitmap imagen) {
        new TransformacionAsyncTask().execute(imagen);
    }

    private class TransformacionAsyncTask extends AsyncTask<Bitmap, Void, List<int[]>> {
        @Override
        protected List<int[]> doInBackground(Bitmap... bitmaps) {
            Bitmap imagen = bitmaps[0];
            List<int[]> lineasDetectadas = new ArrayList<>();

            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    if (imagen.getPixel(x, y) != 0) { // punto de borde
                        for (int theta = 0; theta < 180; theta++) {
                            double rad = Math.toRadians(theta);
                            int rho = (int) (x * Math.cos(rad) + y * Math.sin(rad));
                            acumulador[rho + maxDist][theta]++;
                        }
                    }
                }
            }

            int umbral = 100; // Umbral de ejemplo
            for (int rho = 0; rho < 2 * maxDist; rho++) {
                for (int theta = 0; theta < 180; theta++) {
                    if (acumulador[rho][theta] > umbral) {
                        lineasDetectadas.add(new int[]{rho - maxDist, theta});
                    }
                }
            }

            return lineasDetectadas;
        }

        @Override
        protected void onPostExecute(List<int[]> result) {
            super.onPostExecute(result);
            if (listener != null) {
                listener.onLineasTransformadas(result);
            }
        }
    }

    public List<int[]> obtenerLineas() {
        return lineas;
    }
}
