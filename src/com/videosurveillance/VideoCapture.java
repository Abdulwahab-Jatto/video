/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videosurveillance;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.OpenCVFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateCameraCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateFileCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.cvReleaseCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import com.googlecode.javacv.cpp.opencv_imgproc;
import com.googlecode.javacv.cpp.opencv_objdetect;

/**
 *
 * @author User 2
 */
public class VideoCapture extends javax.swing.JFrame {

    /**
     * Creates new form VideoCapture
     */
    public VideoCapture() {
        initComponents();

        //this.setIconImage(new ImageIcon(getClass().getResource("/Pictures/logo.png")).getImage());
           w = 640;
        h = 480;
        myTime();
        myDate();
        myRec = vidUrl();
        //vidCap();

    }

    String time, compDate;
    opencv_highgui.CvCapture capture1;
    private DaemonThread mythread = new DaemonThread();
    FrameRecorder record1;
    String myRec;
    int w, h;
    opencv_core.IplImage image;

    public void myTime() {
        Calendar cal = new GregorianCalendar();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int Am_Pm = cal.get(Calendar.AM_PM);
        int mymill = cal.get(Calendar.MILLISECOND);
        String AM_PM;
        if (Am_Pm == 1) {
            AM_PM = "PM";
        } else {
            AM_PM = "AM";
        }
        String myhour;
        String mymin;
        String mysec;
        if (hour > 0 && hour <= 9) {
            myhour = "0" + hour;
        } else if (hour == 0) {
            myhour = "12";
        } else {
            myhour = String.valueOf(hour);
        }

        if (minute <= 9) {
            mymin = "0" + minute;
        } else {
            mymin = String.valueOf(minute);
        }

        if (sec <= 9) {
            mysec = "0" + sec;
        } else {
            mysec = String.valueOf(sec);
        }

        time = myhour + "-" + mymin + "-" + mysec + "-" + mymill + " " + AM_PM;
        System.out.println(time);
    }

    public void myDate() {
        Date mydat1 = new Date();
        DateFormat datfom2 = new SimpleDateFormat("yyyy-MM-dd");
        String stformat2 = datfom2.format(mydat1);
        String stry2 = stformat2.substring(0, 4);
        String strm2 = stformat2.substring(5, 7);
        String strd2 = stformat2.substring(8, 10);
        compDate = stry2 + "-" + strm2 + "-" + strd2;
        System.out.println(compDate);
    }

    public String vidUrl() {
        File homedir = new File(System.getProperty("user.home"));
        System.out.println(homedir);
        String url = homedir.getPath() + "\\Documents";
        System.out.println(url);
        File Photo2 = new File(url, "Cam");
        Photo2.mkdir();
        String url1 = homedir.getPath() + "\\Documents\\Cam";
        String myloc = url + "\\Cam" + "\\Video";
        File checkFile = new File(myloc);
        if (!checkFile.isDirectory() || !checkFile.exists()) {
            File Photo1 = new File(url1, "Video");
            Photo1.mkdir();
            System.out.println("file created");
            return myloc;
            
        } else {
            System.out.println("file already exist");
            return myloc;
            }

    }

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        @SuppressWarnings("empty-statement")
        public void run() {
            synchronized (this) {  
                while (runnable) {

                    System.out.println("Working!!!");
                    String saveDate = compDate + "-" + time;
                    //opencv_highgui.VideoCapture capture1 = new opencv_highgui.VideoCapture("http://192.168.173.9:4747/mjpegfeed?640x480.mjpg");
                   //capture1 =cvCreateFileCapture("http://192.168.173.9:4747/mjpegfeed?640x480.mjpg");//.VideoCapture(0);
                   capture1 = cvCreateCameraCapture(2);
                    //opencv_highgui.CvCapture capture = cvCreateFileCapture("Test.avi");//new opencv_highgui.CvCapture();
                    //opencv_highgui.cvSetCaptureProperty(capture,CV_CAP_PROP_FRAME_WIDTH,w);
                    //opencv_highgui.cvSetCaptureProperty(capture,CV_CAP_PROP_FRAME_HEIGHT,h);

                    record1 = new OpenCVFrameRecorder(myRec + "\\REC-" + saveDate + ".avi", w, h);
                    record1.setVideoCodec(opencv_highgui.CV_FOURCC((byte) 'M', (byte) 'J', (byte) 'P', (byte) 'G'));
                    record1.setFrameRate(30);
                    record1.setPixelFormat(1);
                    record1.setAudioCodec(1);

                    record1.setFormat("avi");

                    try {
                        record1.start();
                    } catch (FrameRecorder.Exception ex) {

                    }
                    //opencv_core.CvMat frame = new opencv_core.Mat();
                    Graphics g;
                    g = dislbl.getGraphics();
                    
                    
                    opencv_objdetect.CvHaarClassifierCascade frontface = new opencv_objdetect.CvHaarClassifierCascade(opencv_core.cvLoad("haarcascade_frontalface_alt2.xml"));
//                    opencv_objdetect.CvHaarClassifierCascade fullbody = new opencv_objdetect.CvHaarClassifierCascade(opencv_core.cvLoad("haarcascade_fullbody.xml"));
//                    opencv_objdetect.CvHaarClassifierCascade upperbody = new opencv_objdetect.CvHaarClassifierCascade(opencv_core.cvLoad("haarcascade_mcs_upperbody.xml"));
//                    opencv_objdetect.CvHaarClassifierCascade upperbody1 = new opencv_objdetect.CvHaarClassifierCascade(opencv_core.cvLoad("haarcascade_upperbody.xml"));
//                    
                    
                    
                    while (true) {
                        opencv_core.CvMemStorage faceStorage = opencv_core.CvMemStorage.create();
                        image = cvQueryFrame(capture1);
                        
                            //capture1.retrieve(frame);
                              //frame.asIplImage();
                            opencv_core.cvFlip(image, image, 1);
                            opencv_core.IplImage frame1 = image;
                            opencv_core.cvClearMemStorage(faceStorage);
                            opencv_core.IplImage frame_gray = opencv_core.IplImage.create(frame1.width(), frame1.height(), IPL_DEPTH_8U, 1);
                            if (frame1.isNull()) {
                                break;
                            } else {

                                opencv_imgproc.cvCvtColor(frame1, frame_gray, opencv_imgproc.CV_BGR2GRAY);
                                opencv_imgproc.cvEqualizeHist(frame_gray, frame_gray);

                                opencv_core.CvSeq faces = opencv_objdetect.cvHaarDetectObjects(frame_gray, frontface, faceStorage, 1.1, 3, opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING, cvSize(80, 80), cvSize(250, 250));
//                                opencv_core.CvSeq fbody = opencv_objdetect.cvHaarDetectObjects(frame_gray, fullbody, faceStorage, 1.1, 3, opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING, cvSize(80, 80), cvSize(250, 250));
//                                opencv_core.CvSeq ubody = opencv_objdetect.cvHaarDetectObjects(frame_gray, upperbody, faceStorage, 1.1, 3, opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING, cvSize(80, 80), cvSize(250, 250));
//                                opencv_core.CvSeq ubody1 = opencv_objdetect.cvHaarDetectObjects(frame_gray, upperbody1, faceStorage, 1.1, 3, opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING, cvSize(80, 80), cvSize(250, 250));
//                                
                                System.out.println(faces.total());

                                if (faces.total() > 0 ){//|| fbody.total()> 0||ubody.total() > 0||ubody1.total()>0) {
                                    try {
                                        record1.record(image);

                                    } catch (FrameRecorder.Exception ex) {

                                    }
                                }
                                for (int i = 0; i < faces.total(); i++) {
                                    opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(faces, i));
                                    int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                                    opencv_core.cvRectangle(frame1, cvPoint(x, y), cvPoint(x + w, y + h), opencv_core.CvScalar.GREEN, 2, CV_AA, 0);

                                }

                            }
                            BufferedImage dis = image.getBufferedImage();
                            if (g.drawImage(dis, 0, 0, dislbl.getWidth(), dislbl.getHeight(), 0, 0, dis.getWidth(), dis.getHeight(), null)) {
                            }

                            char c = (char) cvWaitKey(15);
                            if (c == 'q') {
                                break;
                            }

                        
                        if (runnable == false) {
                            System.out.println("going to wait");
                            try {
                                this.wait();
                            } catch (InterruptedException ex) {

                            }
                        }
                    }

                }
            }
        }
    }

    public void vidCap() {
        mythread = new DaemonThread();
        Thread t = new Thread(mythread);
        t.setDaemon(true);
        mythread.runnable = true;
        t.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dislbl = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("<html><center>Start<br>Camera</center>");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("<html><center>Stop<br>Camera</center>");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dislbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(198, 198, 198)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(226, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dislbl, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        vidCap();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        mythread.runnable = false;
        try {
            record1.stop();
        } catch (FrameRecorder.Exception ex) {

        }
        cvReleaseCapture(capture1);
        this.dispose();

    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VideoCapture.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VideoCapture.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VideoCapture.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VideoCapture.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VideoCapture().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel dislbl;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    // End of variables declaration//GEN-END:variables
}
