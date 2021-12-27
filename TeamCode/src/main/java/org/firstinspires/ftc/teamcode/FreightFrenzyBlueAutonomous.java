package org.firstinspires.ftc.teamcode;

import java.util.ArrayList;
import java.util.List;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Blue Autonomous Default Route", group = "Concept")
public class FreightFrenzyBlueAutonomous extends LinearOpMode {
    private static final String FORWARD = "forward";
    private static final String BACKWARD = "backward";
    private static final String STOP = "stop";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final Boolean ON = true;
    private static final Boolean OFF = false;

    private double duckPower = 0.0;

    private long loopCount = 0;
    private boolean elementFound = false;
    private float elementCoordinate = -1;
    private int elementPosition = 0;
    private int resetTime = 0;
    private boolean timeReset = false;
    private boolean completedLower = false;

    private DcMotor frontLeftMotor = null;
    private DcMotor frontRightMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor backRightMotor = null;
    private CRServo clawServo = null;
    private CRServo spinServo = null;
    private CRServo extendServo = null;

    private DcMotor duckMotor = null;
    private DcMotor liftMotor = null;

    private int liftMotorPos;
    private int liftMotorZero;

    private int drive1 = 500; // modify this based on optimal battery speed
    private int stop1 = drive1 + 100;
    private int pan1 = stop1 + 2600;
    private int drive2 = pan1 + 200;
    private int duck1 = drive2 + 3200;
    private int pan2 = duck1 + 1750;
    private int drive3 = pan2 + 750;
    private int pan3 = drive3 + 3350;
    private int drive4 = pan3 + 1000;

    public void drive (String fb, double speedMod)
    {
        if (fb.equals("forward"))
        {
            frontLeftMotor.setPower(speedMod);
            frontRightMotor.setPower(speedMod);
            backLeftMotor.setPower(speedMod);
            backRightMotor.setPower(speedMod);
        }
        if (fb.equals("backward"))
        {
            frontLeftMotor.setPower(-speedMod);
            frontRightMotor.setPower(-speedMod);
            backLeftMotor.setPower(-speedMod);
            backRightMotor.setPower(-speedMod);
        }
        if (fb.equals("stop"))
        {
            frontLeftMotor.setPower(0.0);
            frontRightMotor.setPower(0.0);
            backLeftMotor.setPower(0.0);
            backRightMotor.setPower(0.0);
        }
    }

    public void turn (String lr)
    {
        if (lr.equals("left"))
        {
            frontLeftMotor.setPower(-0.15);
            frontRightMotor.setPower(0.15);
            backLeftMotor.setPower(-0.15);
            backRightMotor.setPower(0.15);
        }
        if (lr.equals("right"))
        {
            frontLeftMotor.setPower(0.15);
            frontRightMotor.setPower(-0.15);
            backLeftMotor.setPower(0.15);
            backRightMotor.setPower(-0.15);
        }
        if (lr.equals("stop"))
        {
            frontLeftMotor.setPower(0.0);
            frontRightMotor.setPower(0.0);
            backLeftMotor.setPower(0.0);
            backRightMotor.setPower(0.0);
        }
    }

    public void pan (String lr)
    {
        if (lr.equals("left"))
        {
            frontLeftMotor.setPower(-0.3);
            frontRightMotor.setPower(-0.3);
            backLeftMotor.setPower(0.3);
            backRightMotor.setPower(0.3);
        }
        if (lr.equals("right"))
        {
            frontLeftMotor.setPower(0.3);
            frontRightMotor.setPower(0.3);
            backLeftMotor.setPower(-0.3);
            backRightMotor.setPower(-0.3);
        }
        if (lr.equals("stop"))
        {
            frontLeftMotor.setPower(0.0);
            frontRightMotor.setPower(0.0);
            backLeftMotor.setPower(0.0);
            backRightMotor.setPower(0.0);
        }
    }

    public void duck (Boolean quack)
    {
        if (quack)
        {
            if (duckPower < 0.8)
            {
                duckPower += 0.05;
            }
        }
        else
        {
            duckPower = 0.0;
        }
        duckMotor.setPower(duckPower);
    }

    public void lift (Boolean uber, double speedMod)
    {
        if (uber)
        {
            liftMotor.setPower(speedMod);
        }
        else
            liftMotor.setPower(0.0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TFOD_MODEL_ASSET = "amogus.tflite";
    private static final String[] LABELS =
    {
      "element"
    };

    private static final String VUFORIA_KEY =
            "AX54Lyj/////AAABmSsIALipi0y4oiZBAoZS4o4Jppp+qbLTWgVQVVuyveVi7sLhVC8XAwvTGDzKpxm1tiMRMLgYEV3Y5YXvqKMiA7R7TUZQcZeyL9MMGoqcq7rIeFMX01KOuZUmfs754hgbnsINn38JjhLLAH3g2GuKF9QZBF/CJqw/UFKKzR8bDlv4TkkTP8AyxvF9Vyv9G9gQhK2HoOWuSCWQHzIWl+op5LEPLXU7RmdrWzxDm1zEY3DZoax5pYLMRR349NoNzpUFBzwNu+nmEzT3eXQqtppz/vE/gHA0LRys9MAktPmeXQfvaS2YUi4UdE4PcFxfCUPuWe6L9xOQmUBE7hB39jTRkYxGADmTxILyBZB6fD3qyFHv";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {
        // control hub 1
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        duckMotor = hardwareMap.dcMotor.get("duckMotor");
        liftMotor = hardwareMap.dcMotor.get("liftMotor");

        //setting the direction for each motor
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        duckMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        clawServo = hardwareMap.crservo.get("clawServo");
        spinServo = hardwareMap.crservo.get("spinServo");
        extendServo = hardwareMap.crservo.get("extendServo");

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            long initTime = System.currentTimeMillis();
            liftMotorZero = liftMotor.getCurrentPosition();
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    List<String> labels = new ArrayList<String>();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());

                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                            i++;
                            labels.add(recognition.getLabel());
                            elementCoordinate = recognition.getLeft();
                        }
                        telemetry.update();

                        long finalTime = System.currentTimeMillis() - initTime;
                        long timeDifference = 0;
                        loopCount += 1;
                        liftMotorPos = liftMotor.getCurrentPosition() - liftMotorZero;

                        telemetry.addData("Loop count:", loopCount);
                        if (resetTime == 0)
                        {
                            telemetry.addData("Time is:", finalTime);
                            telemetry.addData("Ms/loop:", finalTime / loopCount);
                        }
                        if (resetTime > 0)
                        {
                            telemetry.addData("Time is:", finalTime + timeDifference);
                            telemetry.addData("Ms/loop:", (finalTime + timeDifference) / loopCount);
                        }
                        telemetry.addData("Element position: ", elementPosition);
                        telemetry.addData("Lift motor position: ", liftMotorPos);

                        // initial step - detect what position duck/team element is in
                        if (finalTime > 1500 && !elementFound)
                        {
                            if (elementCoordinate >= 0 && elementCoordinate < 640)
                            {
                                elementPosition = 1;
                            }
                            else if (elementCoordinate >= 640)
                            {
                                elementPosition = 2;
                            }
                            else
                            {
                                elementPosition = 3;
                            }
                            elementFound = true;
                            resetTime = 1;
                        }
                        // spin duck wheel
                        if (resetTime == 1)
                        {
                            if (!timeReset)
                            {
                                timeDifference = finalTime;
                                initTime = System.currentTimeMillis();
                                timeReset = true;
                            }
                            if (finalTime < drive1)
                            {
                                drive(FORWARD, 0.3);
                            }
                            if (finalTime < stop1 && finalTime > drive1)
                            {
                                drive(STOP, 0.0);
                            }
                            if (finalTime < pan1 && finalTime > stop1)
                            {
                                pan(RIGHT);
                            }
                            if (finalTime < drive2 && finalTime > pan1)
                            {
                                drive(BACKWARD, 0.15);
                            }
                            if (finalTime < duck1 && finalTime > drive2)
                            {
                                drive(STOP, 0.0);
                                duck(ON);
                            }
                            if (finalTime < pan2 && finalTime > duck1)
                            {
                                duck(OFF);
                                pan(LEFT);
                            }
                            if (finalTime < drive3 & finalTime > pan2)
                            {
                                drive(BACKWARD, 0.3);
                            }
                            if (finalTime < pan3 & finalTime > drive3)
                            {
                                pan(LEFT);
                                spinServo.setPower(0.1863); // position for it to deliver duck, obtained through testing in TeleOp
                                if ((elementPosition == 2 & liftMotorPos <= 2300) || (elementPosition == 3 & liftMotorPos <= 6000))
                                {
                                    lift(ON, 1.0);
                                    telemetry.addData("Lift Motor Position: ", liftMotorPos);
                                }
                                else
                                {
                                    lift(OFF, 0.0);
                                }
                            }
                            if (finalTime < drive4 & finalTime > pan3)
                            {
                                drive(FORWARD, 0.3);
                                if (elementPosition == 3 & liftMotorPos <= 5300)
                                {
                                    lift(ON, 1.0);
                                    telemetry.addData("Lift Motor Position: ", liftMotorPos);
                                }
                                else
                                {
                                    lift(OFF, 0.0);
                                }
                                extendServo.setPower(-0.4);
                            }
                            if (finalTime > drive4)
                            {
                                if (elementPosition == 1 & liftMotorPos >= -125)
                                {
                                    lift(ON, -0.3);
                                }
                                else
                                {
                                    lift(OFF, 0.0);
                                    completedLower = true;
                                }
                                drive(STOP, 0.0);
                            }
                            if (completedLower)
                            {
                                clawServo.setPower(-0.38);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
            "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}
