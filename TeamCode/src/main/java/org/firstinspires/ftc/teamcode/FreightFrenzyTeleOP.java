package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="LogisNICS TeleOp", group="Interactive Opmode")

public class FreightFrenzyTeleOP extends OpMode
{

    // Control Hub
    private DcMotor frontRightMotor = null;
    private DcMotor frontLeftMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor backRightMotor = null;
    private CRServo clawServo = null;
    private CRServo spinServo = null;
    private CRServo extendServo = null;

    // Expansion Hub
    private DcMotor intakeMotor = null;
    private DcMotor duckMotor = null;
    private DcMotor liftMotor = null;

    private int liftMotorPos;
    private int liftMotorZero;
    private int liftMotorMax;

    // Doubles for the power of our driving motors
    private double frontLeftPower, frontRightPower, backLeftPower, backRightPower;
    private double frontLeftPan, frontRightPan, backLeftPan, backRightPan;

    private double liftPower;

    // Scale variable for all drive, turn, and pan functions
    private double powerScale = 0.8;

    double duckMax = 0.8;
    double duckPower = 0.0;
    double spinPos = -0.0777;
    double extendPos = -0.6;

    boolean startLift = true;

    boolean spinning = false;
    boolean extending = false;

    private boolean autoHome = false;

    //frames
    private long currentFrame;
    private long startHomeFrame;


    @Override
    public void init ()
    {
        // Initalizations to connect our motor variables to the motor on the robot
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");
        duckMotor = hardwareMap.dcMotor.get("duckMotor");
        
        liftMotor = hardwareMap.dcMotor.get("liftMotor");
        liftMotorZero = liftMotor.getCurrentPosition()+ 200;

        clawServo = hardwareMap.crservo.get("clawServo");
        spinServo = hardwareMap.crservo.get("spinServo");
        extendServo = hardwareMap.crservo.get("extendServo");

        // Direction setting for the motors, depending on their physical orientation on the robot
        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        duckMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("", "Working");
        telemetry.addData("", "Last Update: 2021-11-08 16:57");

        currentFrame = 0;
        startHomeFrame = 0;
    }
    @Override
    public void init_loop()
    {
        // pls do
    }

    private void autoHoming()
    {
        long retractTime = startHomeFrame + 100; // how long it takes to retract arm
        long centerTime = retractTime + 200; // how long it takes to center and lower arm


        extendPos = -0.65;
        spinPos = -0.0777;

        if(liftMotorPos > liftMotorZero)
        {
            liftMotor.setPower(-1.0);
        }
        else
        {
            liftMotor.setPower(0.0);
        }

        if (currentFrame > startHomeFrame && currentFrame <= retractTime)
        {
            extendServo.setPower(extendPos);
            telemetry.addData("Retracting","");
        }
        if (currentFrame > retractTime && currentFrame <= centerTime) // raising
        {
            spinServo.setPower(spinPos);
            //liftMotor.setTargetPosition(liftMotorZero);
            telemetry.addData("Centering and Lowering","");
        }
        if (currentFrame > centerTime)
        {
            // stop
            autoHome = false;
        }
    }

    @Override
    public void loop()
    {
        // sets max positions
        // change these value later
        liftMotorMax = liftMotorZero + 7000;

        //frame incrementer
        currentFrame += 1;

        liftMotorPos = liftMotor.getCurrentPosition();

        double drive = gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        double pan = gamepad1.left_stick_x;

        double lift = -gamepad2.left_stick_y;

        // Driving controls
        frontLeftPower = Range.clip(drive + turn, -1.0, 1.0);
        frontRightPower = Range.clip(drive - turn, -1.0, 1.0);
        backLeftPower = Range.clip(drive + turn, -1.0, 1.0);
        backRightPower = Range.clip(drive - turn, -1.0, 1.0);
        frontLeftMotor.setPower(powerScale * frontLeftPower);
        frontRightMotor.setPower(powerScale * frontRightPower);
        backLeftMotor.setPower(powerScale * backLeftPower);
        backRightMotor.setPower(powerScale * backRightPower);

        // Panning controls
        frontLeftPan = Range.clip(drive - pan, -1.0, 1.0);
        frontRightPan = Range.clip(drive - pan, -1.0, 1.0);
        backLeftPan = Range.clip(drive + pan, -1.0, 1.0);
        backRightPan = Range.clip(drive + pan, -1.0, 1.0);
        frontLeftMotor.setPower(powerScale * frontLeftPan);
        frontRightMotor.setPower(powerScale * frontRightPan);
        backLeftMotor.setPower(powerScale * backLeftPan);
        backRightMotor.setPower(powerScale * backRightPan);


        if (startLift && liftMotor.getCurrentPosition() < liftMotorZero) // set lift to starting position
        {
            liftPower = 1.0;
        }
        else // normal function
        {
            startLift = false;
            liftPower = Range.clip(lift, -1.0, 1.0);
        }
        liftMotor.setPower(liftPower);


        // Intake motor power: includes "boost" mode when right trigger is pressed, also includes reverse function
        if (gamepad1.right_trigger > 0)
        {
            intakeMotor.setPower(0.9);
        }
        else if(gamepad1.left_trigger > 0)
        {
            intakeMotor.setPower(-0.7);
        }
        else
        {
            intakeMotor.setPower(0.7);
        }

        // Duck carousel spinner
        if (gamepad1.touchpad || gamepad1.y)
        {
            if (duckPower < duckMax)
            {
                duckPower += 0.005;
            }
            duckMotor.setPower(duckPower);
        }
        else if (gamepad1.x)
        {
            if (duckPower > -duckMax)
            {
                duckPower -= 0.005;
            }
            duckMotor.setPower(duckPower);
        }
        else
        {
            duckPower = 0.0;
            duckMotor.setPower(0.0);
        }

        // Controls the vertical movement of the claw
        // does not allow the claw to go above the max position
        /*if (gamepad2.)
        {
            liftMotor.setPower(0.8);
        }
        //  does not allow the claw to go below the max position
        else if (gamepad1.dpad_down)
        {
            liftMotor.setPower(-0.8);
        }
        else
        {
            liftMotor.setPower(0.0);
        }*/

        // Controls the claw of the claw arm
        if (gamepad2.right_bumper)
        {
            clawServo.setPower(-0.38);
        }
        else if (gamepad2.left_bumper)
        {
            clawServo.setPower(-0.17);
        }
        else if (gamepad2.start)
        {
            clawServo.setPower(-0.075);
        }

        // Controls the spinning of the pick-and-place
        if (gamepad2.right_stick_x < 0 && !spinning)
        {
            spinning = true;
            spinPos += 0.033;
        }
        else if (gamepad2.right_stick_x > 0 && !spinning)
        {
            spinning = true;
            spinPos -= 0.033;
        }
        else if (gamepad2.right_stick_button && !spinning) // center
        {
            spinning = true;
            spinPos = -0.0777;
        }

        if (gamepad2.right_stick_x == 0 && spinning) // reset spinning flag
        {
            spinning = false;
        }

        // Clamp for spin motor
        if (spinPos < -0.11)
        {
            spinPos = -0.11;
        }
        if (spinPos > 0.3)
        {
            spinPos = 0.3;
        }


        if (-gamepad2.right_stick_y > 0 && !extending)
        {
            extending = true;
            extendPos += 0.04;
        }
        else if (-gamepad2.right_stick_y < 0 && !extending)
        {
            extending = true;
            extendPos -= 0.04;
        }
        else if (gamepad2.circle)  //home position for extend arm
        {
            extendPos = -0.6;
        }

        if (gamepad2.right_stick_y == 0 && extending) // reset extending flag
        {
            extending = false;
        }

        // Clamp for extend motor
        if (extendPos > -0.12)
        {
            extendPos = -0.12;
        }
        if (extendPos < -0.56)
        {
            extendPos = -0.56;
        }

        spinServo.setPower(spinPos);
        extendServo.setPower(extendPos);

        if (gamepad2.cross || autoHome)
        {
            if (!autoHome)
            {
                startHomeFrame = currentFrame;
            }

            autoHome = true;
            autoHoming();
        }

        if (gamepad2.triangle)
        {
            liftMotorZero = liftMotor.getCurrentPosition();
        }

        
        liftMotorPos = liftMotor.getCurrentPosition();

        telemetry.addData("Extend Postion: ", "" + extendPos);
        telemetry.addData("Spin Position:", ""+spinPos);
        telemetry.addData("Spinning Flag:", ""+spinning);
        telemetry.addData("Extending Flag:", ""+extending);
        telemetry.addData("Lift Motor Position: ", "" + liftMotorPos);

    }

    @Override
    public void stop()
    {
        // pls do, part two
        frontLeftMotor.setPower(0.0);
        frontRightMotor.setPower(0.0);
        backLeftMotor.setPower(0.0);
        backRightMotor.setPower(0.0);
        intakeMotor.setPower(0.0);
        duckMotor.setPower(0.0);
        liftMotor.setPower(0.0);
    }
}
