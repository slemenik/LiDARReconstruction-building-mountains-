package com.slemenik.lidar.reconstruction.main;

import com.slemenik.lidar.reconstruction.buildings.ColorController;
import com.slemenik.lidar.reconstruction.jni.JniLibraryHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.slemenik.lidar.reconstruction.buildings.BuildingController;
import com.slemenik.lidar.reconstruction.mountains.EvenFieldController;
import com.slemenik.lidar.reconstruction.mountains.InterpolationController.Interpolation;

import com.slemenik.lidar.reconstruction.mountains.MountainController;
import com.slemenik.lidar.reconstruction.mountains.triangulation.Triangulation;
import delaunay_triangulation.Delaunay_Triangulation;
import delaunay_triangulation.Point_dt;
import org.geotools.util.IntegerList;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3d;


public class Main {

    public static final String DATA_FOLDER = ".";

    public static final String INPUT_FILE_NAME = DATA_FOLDER + "original+odsek";//"410_137_triglav";//"original+odsek";//"triglav okrnjen.laz";
    public static String OUTPUT_FILE_NAME = DATA_FOLDER + "out";
    private static final String TEMP_FILE_NAME = DATA_FOLDER + "temp";
    private static final String DMR_FILE_NAME = DATA_FOLDER + "GK1_410_137.asc";

    private static final double DISTANCE_FROM_ORIGINAL_POINT_THRESHOLD = 0.8; //manjše je bolj natančno za detajle, ne prekrije celega
    public static final double CREATED_POINTS_SPACING = 0.6;//2.0;//0.2;
    private static final boolean CONSIDER_EXISTING_POINTS = false; //rešetke
    private static final double BOUNDING_BOX_FACTOR = 1.0;// za koliko povečamo mejo boundingboxa temp laz file-a
    private static final boolean CREATE_TEMP_FILE = true;
    private static final int[] TEMP_BOUNDS = new int[]{462264, 100575, 462411, 100701};
    private static  int COLOR = 5; //6rdeča //5rumena*/; //4-zelena*/;//2-rjava;//3-temno zelena;

    private static final double MOUNTAINS_ANGLE_TOLERANCE_DEGREES = 10;

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        System.out.println("start main");

        long heapMaxSize = Runtime.getRuntime().maxMemory();
        System.out.println("heapmaxsize "+HelperClass.formatHeapSize(heapMaxSize));

        tempTestFunction();
        double[][] pointListDoubleArray = mainTest(args);

        if (pointListDoubleArray.length > 0) {
            System.out.println("zacetek pisanja... ");
            System.out.println("Points to write: " + pointListDoubleArray.length);
            int returnValue = JniLibraryHelpers.writePointList(pointListDoubleArray, INPUT_FILE_NAME, OUTPUT_FILE_NAME, COLOR);
            System.out.println("End writing. Points written: " + returnValue);

            ////////temp////////////7
//            if (args.length > 0) {
                int a = args.length+1;
                System.out.println("jovo na novo");
                COLOR = a;
                main(new String[a]);


//            }
            ////////temp////////////7

        }

        System.out.println();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        long time = TimeUnit.SECONDS.convert(duration, TimeUnit.NANOSECONDS);
        System.out.println("Sekunde izvajanja: " + time);
        System.out.println("end");
    }

    public static void tempTestFunction() {

//        public static int point2Index(double coordinate, double min, double pointSpace) {//temp spremeni v private
//            return (int) ((coordinate - min) / pointSpace);
//        }
//
//        public static double index2Point(int x, double min, double pointSpace) {//temp spremeni v private
//            return ((double) x) * pointSpace + min;
//        }

//        System.out.println(EvenFieldController.point2Index(410839.68766673, 410531.88766673254, 0.6));
//        double min = 410531.88766673254;
//        System.out.println(EvenFieldController.point2Index(410839.6876667325, min, 0.6));
//        double coordinate = 410839.6876667325;
//
//        double pointSpace = 0.6;
//        double temp = (coordinate - min) / pointSpace;
//
//        System.out.println(EvenFieldController.index2Point(512, min, 0.6));
//        System.out.println(EvenFieldController.index2Point(513, min, 0.6));



        return;


    }

    private static double[][] mainTest(String[] args) {

                OUTPUT_FILE_NAME = DATA_FOLDER + "nevem.laz";

//        List<double[]> arr2 = new MountainController().parseFolder("C:/Users/Matej/Documents/My Documents/Šola/4. FRI/Magistrska/DMV1000", DATA_FOLDER + "410_137_triglav.laz");


//        List<double[]> arr2 = testMountains(Interpolation.SPLINE);
//        List<double[]> arr2 = testBuildingCreation();
//        List<double[]> arr2 = ArrayList<double[]>(Arrays.asList(arr));
        double[][] arr2 = new double[][]{};
//        testHeapSpace();
        if (args.length <= 0) {
            arr2 = testMountainController(args);
        }
        return arr2;

    }

    public static void testHeapSpace(){

        double[][] arr = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);
        double[][] arr2 = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);
        double[][] arr3 = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);
        double[][] arr4 = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);

    }

    public static void writeOBJ() {
        MountainController mc = new MountainController(new double[][]{});
        Point_dt[] list = mc.getDmrFromFile(DMR_FILE_NAME);
        Delaunay_Triangulation dt = new Delaunay_Triangulation(list);
        try { dt.write_smf("smf.obj"); }catch (Exception e) {}
    }


    public static double[][] testMountainController(String[] args) {
        HelperClass.memory();
        double[][] arr = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);
        HelperClass.memory();
//        double[][] arr = new double[][]{};
        MountainController mc = new MountainController(arr);
        HelperClass.memory();
        arr = null; //!! essential, without it we get OutOfMemoryError: Java heap space
        HelperClass.memory();
        mc.dmrFileName = DMR_FILE_NAME;
        mc.tempStopCount = args.length;
        MountainController.similarAngleToleranceDegrees = MOUNTAINS_ANGLE_TOLERANCE_DEGREES;
//        MountainController.numberOfSegments = 25; //temp - 19 is infinte loop
//        double[][] newPoints = mc.start();

        //temp///
        Transform3D[] transformations = mc.getRotationTransformation(-46.30999999997357, 0.010000000009313226, 0.010000000009313226);
        mc.calculateNewPoints(transformations[0]);
        double[][] newPoints = mc.points2writeTemp;
        Arrays.asList(newPoints).stream().forEach(p -> {

        });
        ////////
        OUTPUT_FILE_NAME = DATA_FOLDER + "allTheAddedPoints";
        return newPoints;
    }

    public static List<double[]> testMountainGrid3d(){
        //naredi triglav, 3d, ne naredi praznih mest, ampak tista ki so že nafilana, ampak so x,y koordinate diskretne, mreža
        double[][]  arr = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);
        OUTPUT_FILE_NAME = DATA_FOLDER + "triglav grid diskretno.laz";
        EvenFieldController ef = new EvenFieldController(arr, CREATED_POINTS_SPACING);
        ef.interpolation = Interpolation.OWN_VALUE;
        return ef.getPointsFromFieldArray(ef.getBooleanPointField(Arrays.asList(arr).stream().map(d -> new Point3d(d[0], d[1], d[2])).collect(Collectors.toList())), true);
    }

    public static List<double[]> testBoundary() {
        double[][] arr = JniLibraryHelpers.getPointArray( INPUT_FILE_NAME);
        return testBoundary(arr);
    }

    public static List<double[]> testBoundary(double[][] arr) {

        EvenFieldController mc = new EvenFieldController(arr, CREATED_POINTS_SPACING);
        boolean[][] field = mc.getBooleanPointField(Arrays.asList(arr).stream().map(d -> new Point3d(d[0], d[1], d[2])).collect(Collectors.toList()));
        boolean[][] newField = mc.getBoundaryField(field);
//        boolean[][] newField = field;

        return mc.getPointsFromFieldArray(newField, true);

    }

//    public static double[][] testMountains(double[][] arr, double minX, double maxX, double minY, double maxY) {
//        EvenFieldController efc = new EvenFieldController( minX, maxX, minY, maxY, CREATED_POINTS_SPACING);
//        efc.interpolation = Interpolation.BICUBIC;
//        return HelperClass.toResultDoubleArray(efc.fillHoles(arr));
//    }
//
//    public static double[][] testMountains(double[][] arr) {
//        EvenFieldController efc = new EvenFieldController(arr, CREATED_POINTS_SPACING);
//        efc.interpolation = Interpolation.SPLINE;
//        return HelperClass.toResultDoubleArray(efc.fillHoles(arr));
//    }
//
//    public static double[][] testMountains(Interpolation interp) {
//        System.out.println("method testMountains()");
//        OUTPUT_FILE_NAME = DATA_FOLDER + "triglav4 spacing-"+ CREATED_POINTS_SPACING + " interpolation-"+ interp.toString()+ ".laz";
//        double[][]  arr = JniLibraryHelpers.getPointArray(INPUT_FILE_NAME);
//        EvenFieldController efc = new EvenFieldController(arr, CREATED_POINTS_SPACING);
//        efc.interpolation = interp;
//        return HelperClass.toResultDoubleArray(efc.fillHoles(arr));
//    }

    public static List<double[]> testBuildingCreation() {
        BuildingController bc = new BuildingController();
        bc.inputLazFileName = INPUT_FILE_NAME;
        bc.createTempLazFile = CREATE_TEMP_FILE;
        bc.tempLazFileName = TEMP_FILE_NAME;
        bc.boundingBoxFactor = BOUNDING_BOX_FACTOR;
        bc.createdPointsSpacing = CREATED_POINTS_SPACING;
        bc.distanceFromOriginalPointThreshold = DISTANCE_FROM_ORIGINAL_POINT_THRESHOLD;
        bc.considerExistingPoints = CONSIDER_EXISTING_POINTS;
        bc.outputFileName = OUTPUT_FILE_NAME;
        bc.shpFileName = DATA_FOLDER + "BU_STAVBE_P.shp";
        bc.write(TEMP_BOUNDS);
        return bc.points2Insert;
    }

    public static void testTriangulation() {
        Triangulation.triangulate(DMR_FILE_NAME);
    }

    public static void testGoogleMaps() {
        ColorController cc = new ColorController();
        try {
            String a = cc.getHTML("http://maps.googleapis.com/maps/api/streetview?size=600x400&location=12420+timber+heights,+Austin&key=AIzaSyCkUOdZ5y7hMm0yrcCQoCvLwzdM6M8s5qk");
            System.out.println(a);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

}
