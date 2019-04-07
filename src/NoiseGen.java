import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;


public class NoiseGen {

    String filename;
    public String filepath = "ColorQRs/";   // load files from this directory
    public String filepathSaveNoise = "finals/Noise/";
    public String filepathSaveBlur = "finals/Blur/";  //// make sure that the final directory exists
    public String filepathSaveRotation = "finals/Rotation/"; // make sure that the final directory exists

    File[] files;

    Mat [] img; // loaded images
    Mat []imgproc;  // images to work on and to save
    Mat [] imgproc_;
    MatOfDouble mean = new MatOfDouble();
    MatOfDouble dev = new MatOfDouble();

    int coeffs10 = 10;
    int coeffs20 = 20;

    public NoiseGen() {
        files = new File(filepath).listFiles();

        img = new Mat[files.length]; // loaded images
        imgproc = new Mat[files.length];  // images to work on and to save
        imgproc_ = new Mat[files.length];
    }

    //  double scale;   // the lower scale the bigger noise (up to mean value and standard deviation of readed QR which itself makes code unreadable for detector - happens to read red band from time to time))
    // 2 is still fine and the noise is surprisingly big



    public void Noise(int idx) {


        for (File file : files) {
            filename = filepath + file.getName();
            img[idx] = Imgcodecs.imread(filename);
            imgproc[idx] = img[idx].clone();
            //System.out.println(filename);
            double scale = (Math.random() * (4 - 0.2)) + 0.2; // you are dividing mean and std. deviation of the QR code by this value - the bigger value the lower noise?
//////////// noise generation and addition part - not much to change, can get rid of 1st line and arbitrarily set values of mean value and standard deviation (but what for ;))
            Core.meanStdDev(img[idx], mean, dev);
            Mat noise = new Mat(img[idx].size(), img[idx].type());
            Core.randn(noise, (mean.get(0, 0)[0]) / scale, (dev.get(0, 0)[0]) / scale);
            Core.add(imgproc[idx], noise, imgproc[idx]);
///////////
            Imgcodecs.imwrite(filepathSaveNoise + "Noise" + idx + ".png", imgproc[idx]);
            idx++;
        }
    }

    public void Blur(int idx) {

        for (File file : files) {
            filename = filepath + file.getName();
            img[idx] = Imgcodecs.imread(filename);
            imgproc[idx] = img[idx].clone();
            imgproc_[idx] = img[idx].clone();
            /////////// gaussian blur part
            for (int j = 1; j < coeffs10; j += 2) {
                Imgproc.GaussianBlur(img[idx], imgproc[idx], new Size(j, j), 0, 0);
            }
            for (int j = 1; j < coeffs20; j += 2) {
                Imgproc.GaussianBlur(img[idx], imgproc_[idx], new Size(j, j), 0, 0);
            }
            //////////
            Imgcodecs.imwrite(filepathSaveBlur + "GaussianBlur10_" + idx + ".png", imgproc[idx]);
            Imgcodecs.imwrite(filepathSaveBlur + "GaussianBlur20_" + idx + ".png", imgproc_[idx]);
            idx++;
        }
    }

    public void Rotation(int idx) {

        Mat[] img = new Mat[files.length]; // loaded images
        Mat[] imgproc = new Mat[files.length];  // images to work on and to save
        Mat[] imgproc_ = new Mat[files.length];
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble dev = new MatOfDouble();

        for (File file : files) {
            filename = filepath + file.getName();
            img[idx] = Imgcodecs.imread(filename);
            imgproc[idx] = img[idx].clone();
            // System.out.println(filename);

            //////////// rotation part

            Point pointsin[] = new Point[]{new Point((int) (Math.random() * (50 - 20) + 1) + 20, (int) (Math.random() * (50 - 20) + 1) + 20),
                    new Point((int) (Math.random() * (200 - 100) + 1) + 100, (int) (Math.random() * (50 - 20) + 1) + 20),
                    new Point((int) (Math.random() * (50 - 20) + 1) + 20, (int) (Math.random() * (300 - 250) + 1) + 250)};

            Point pointsout[] = new Point[]{new Point((int) (Math.random() * (50 - 20) + 1) + 10, (int) (Math.random() * (50 - 20) + 1) + 10),
                    new Point((int) (Math.random() * (200 - 100) + 1) + 50, (int) (Math.random() * (50 - 20) + 1) + 10),
                    new Point((int) (Math.random() * (50 - 20) + 1) + 10, (int) (Math.random() * (300 - 250) + 1) + 100)};

            MatOfPoint2f psrc = new MatOfPoint2f(pointsin);
            MatOfPoint2f pout = new MatOfPoint2f(pointsout);

            Mat m = Imgproc.getAffineTransform(psrc, pout);
            Imgproc.warpAffine(imgproc[idx], imgproc[idx], m, img[idx].size());
            ///////////

            Imgcodecs.imwrite(filepathSaveRotation + "Rotation" + idx + ".png", imgproc[idx]);
            idx++;
        }



    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        NoiseGen gen = new NoiseGen();
        gen.Noise(0);
        gen.Blur(0);
        gen.Rotation(0);
    }
}