/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.vision.VisionPipeline;
import edu.wpi.first.vision.VisionThread;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.first.cameraserver.CameraServer;
//import edu.wpi.first.wpilibj.vision.VisionThread;



/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ]
           }
       ]
   }
 */

public final class Main {
  private static String configFile = "/boot/frc.json";

  private static double centerX = 0.0;
  private static NetworkTableEntry centerXEntry;


  @SuppressWarnings("MemberName")
  public static class CameraConfig {
    public String name;
    public String path;
    public JsonObject config;
  }

  public static int team;
  public static boolean server;
  public static List<CameraConfig> cameraConfigs = new ArrayList<>();

  


  private Main() {
  }

  /**
   * Report parse error.
   */
  public static void parseError(String str) {
    System.err.println("config error in '" + configFile + "': " + str);
  }

  /**
   * Read single camera configuration.
   */
  public static boolean readCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      parseError("could not read camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement pathElement = config.get("path");
    if (pathElement == null) {
      parseError("camera '" + cam.name + "': could not read path");
      return false;
    }
    cam.path = pathElement.getAsString();

    cam.config = config;

    cameraConfigs.add(cam);
    return true;
  }

  /**
   * Read configuration file.
   */
  @SuppressWarnings("PMD.CyclomaticComplexity")
  public static boolean readConfig() {
    // parse file
    JsonElement top;
    try {
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));
    } catch (IOException ex) {
      System.err.println("could not open '" + configFile + "': " + ex);
      return false;
    }

    // top level must be an object
    if (!top.isJsonObject()) {
      parseError("must be JSON object");
      return false;
    }
    JsonObject obj = top.getAsJsonObject();

    // team number
    JsonElement teamElement = obj.get("team");
    if (teamElement == null) {
      parseError("could not read team number");
      return false;
    }
    team = teamElement.getAsInt();

    // ntmode (optional)
    if (obj.has("ntmode")) {
      String str = obj.get("ntmode").getAsString();
      if ("client".equalsIgnoreCase(str)) {
        server = false;
      } else if ("server".equalsIgnoreCase(str)) {
        server = true;
      } else {
        parseError("could not understand ntmode value '" + str + "'");
      }
    }

    // cameras
    JsonElement camerasElement = obj.get("cameras");
    if (camerasElement == null) {
      parseError("could not read cameras");
      return false;
    }
    JsonArray cameras = camerasElement.getAsJsonArray();
    for (JsonElement camera : cameras) {
      if (!readCameraConfig(camera.getAsJsonObject())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Start running the camera.
   */
  public static VideoSource startCamera(CameraConfig config) {
    System.out.println("Starting camera '" + config.name + "' on " + config.path);
    VideoSource camera = CameraServer.getInstance().startAutomaticCapture(
        config.name, config.path);

    Gson gson = new GsonBuilder().create();

    camera.setConfigJson(gson.toJson(config.config));

    return camera;
  }

  /**
   * Example pipeline.
   */
  public static class MyPipeline implements VisionPipeline {
    public int val;

    @Override
    public void process(Mat mat) {
      val += 1;
    }
  }

  /**
   * Main.
   */
  public static void main(String... args) {
    if (args.length > 0) {
      configFile = args[0];
    }

    // read configuration
    if (!readConfig()) {
      return;
    }

    // start NetworkTables
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    if (server) {
      System.out.println("Setting up NetworkTables server");
      ntinst.startServer();
    } else {
      System.out.println("Setting up NetworkTables client for team " + team);
      ntinst.startClientTeam(team);
    }
    NetworkTable table = ntinst.getTable("visionTable");
    centerXEntry = table.getEntry("centerX");

    // start cameras
    List<VideoSource> cameras = new ArrayList<>();
    for (CameraConfig cameraConfig : cameraConfigs) {
      cameras.add(startCamera(cameraConfig));
    }

    // start image processing on camera 0 if present
    if (cameras.size() >= 1) {
      VisionThread visionThread = new VisionThread(cameras.get(0),new VisionTracking(), pipeline -> {

        
        
        System.out.println("Here 1");

        /*CvSink cvSink = new CvSink("tmp");
        cvSink.setSource(cameras.get(0));
        Mat mat = new Mat();                
        CvSource outputStream = CameraServer.getInstance().putVideo("TargetVision", 320, 200);
        System.out.println("Here 2");*/

        Rect bb = new Rect();
        Rect nb = new Rect();

          if (!pipeline.convexHullsOutput().isEmpty()) {
            MatOfPoint biggestContour = pipeline.convexHullsOutput().get(0);
            int bigInt = 0;
        
            for(int i = 0; i < pipeline.convexHullsOutput().size(); i++) {
              final MatOfPoint contour = pipeline.convexHullsOutput().get(i);
              double area = Imgproc.contourArea(contour);
              double biggestArea = Imgproc.contourArea(biggestContour);
              if (area > biggestArea) {
                biggestContour = contour;
                bigInt = i;
              }
            }
            if (biggestContour != null) {
              pipeline.convexHullsOutput().remove(bigInt);
            }
  
            MatOfPoint nextBiggestContour = null;
            if (!pipeline.convexHullsOutput().isEmpty()) {
              nextBiggestContour = pipeline.convexHullsOutput().get(0);
              for(int i = 0; i < pipeline.convexHullsOutput().size(); i++) {
                final MatOfPoint contour = pipeline.convexHullsOutput().get(i);
                double area = Imgproc.contourArea(contour);
                double biggestArea = Imgproc.contourArea(biggestContour);
                if (area > biggestArea) {
                  nextBiggestContour = contour;
                }
              }
            }
            if (nextBiggestContour != null) {
              if (Imgproc.contourArea(biggestContour) > 20.0 && Imgproc.contourArea(nextBiggestContour) > 20.0) {
                final Rect bbx = Imgproc.boundingRect(biggestContour);
                final Rect nbx = Imgproc.boundingRect(nextBiggestContour);
                //System.out.println("bb: " + bb.x + " nb: " +nb.x);
                centerX = (bbx.x + (bbx.width/2.0) + nbx.x + (nbx.width/2.0))/2.0;
              } else {
                centerX = -1;
                System.out.println("TOO SMALL!!!");
              }
            }
          } else {
            System.out.println("no targets");
            centerX = -1;
          }
          System.out.println("Center = " + centerX);
          centerXEntry.setDouble(centerX);
          /*
        while (!Thread.interrupted()) {

          System.out.println("Here 3");
          // Tell the CvSink to grab a frame from the camera and put it
          // in the source mat.  If there is an error notify the output.
          if (cvSink.grabFrame(mat) == 0) {
            System.out.println("Here 00");
            // Send the output the error.
            outputStream.notifyError(cvSink.getError());
            // skip the rest of the current iteration
            //continue;
          }
          System.out.println("Here 5");
          Imgproc.rectangle(mat, new Point(0, 0), new Point(320, 10),new Scalar(0, 255, 0), 1);
          
          Imgproc.rectangle(mat, new Point(centerX, 0), new Point(centerX, 10),new Scalar(0, 0, 255), 5);

          Imgproc.rectangle(mat, new Point(bb.x ,bb.y), new Point(bb.x + bb.width, bb.y + bb.height),new Scalar(0, 0, 255), 2);
          Imgproc.rectangle(mat, new Point(nb.x ,nb.y), new Point(nb.x + nb.width, nb.y + nb.height),new Scalar(0, 0, 255), 2);  
          outputStream.putFrame(mat);
          mat.release();
          System.out.println("Here 6");
        }*/
        System.out.println("Here 7");
      });
      /* something like this for GRIP:
      VisionThread visionThread = new VisionThread(cameras.get(0),
              new GripPipeline(), pipeline -> {
        ...
      });
       */
      visionThread.start();
    }

    // loop forever
    for (;;) {


      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }
  }
}
