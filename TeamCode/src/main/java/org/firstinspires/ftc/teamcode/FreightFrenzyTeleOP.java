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
    private int liftMotorMin;

    // Doubles for the power of our driving motors
    private double frontLeftPower, frontRightPower, backLeftPower, backRightPower;
    private double frontLeftPan, frontRightPan, backLeftPan, backRightPan;

    // Scale variable for all drive, turn, and pan functions
    private double powerScale = 0.8;

    double duckMax = 0.8;
    double duckPower = 0.0;
    double spinPos = -0.53;
    double extendPos = 0.05;

    boolean spinning = false;
    boolean extending = false;
    boolean debug = false;
    boolean debugToggle = false;

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
        liftMotorZero = liftMotor.getCurrentPosition();
        
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
    }
    @Override
    public void init_loop()
    {
        // pls do
    }

    @Override
    public void loop()
    {
        // sets min and max positions
        // change these value later
        liftMotorMax = liftMotorZero + 7000;
        liftMotorMin = liftMotorZero - 500;

        liftMotorPos = liftMotor.getCurrentPosition();

        double drive = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;
        double pan = -gamepad1.left_stick_x;

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
        if (gamepad1.touchpad)
        {
            telemetry.addData("duckPower: ", "" + duckPower);
            if (duckPower < duckMax)
            {
                duckPower += 0.005;
            }
            duckMotor.setPower(duckPower);
        }
        else
        {
            duckPower = 0.0;
            duckMotor.setPower(0.0);
        }


        // toggle between "debug" mode for claw height
        if (!debugToggle && gamepad1.square)
        {
            debug = !debug;
            debugToggle = true;
        }
        if (debugToggle && !gamepad1.square)
        {
            debugToggle = false;
        }

        // Controls the vertical movement of the claw
        if (!debug)
        {
            // does not allow the claw to go above the max position
            if (gamepad1.dpad_up && liftMotorPos <= liftMotorMax)
            {
                liftMotor.setPower(1.0);
            }
            //  does not allow the claw to go below the max position
            if (gamepad1.dpad_down && liftMotorPos >= liftMotorZero)
            {
                liftMotor.setPower(-1.0);
            }
            if (!gamepad1.dpad_up && !gamepad1.dpad_down)
            {
                // corrects position if claw goes below or above limit
                if (liftMotorPos > liftMotorMax)
                {
                    liftMotor.setPower(-0.3);
                }
                else if (liftMotorPos < liftMotorZero)
                {
                    liftMotor.setPower(0.3);
                }
                else
                {
                    liftMotor.setPower(0.0);
                }
            }
        }
        else
        {
            // does not allow the claw to go above the max position
            if (gamepad1.dpad_up && liftMotorPos <= liftMotorMax)
            {
                liftMotor.setPower(1.0);
            }
            //  does not allow the claw to go below the max position
            if (gamepad1.dpad_down && liftMotorPos >= liftMotorMin)
            {
                liftMotor.setPower(-1.0);
            }
            if (!gamepad1.dpad_up && !gamepad1.dpad_down)
            {
                // corrects position if claw goes below or above limit
                if (liftMotorPos > liftMotorMax)
                {
                    liftMotor.setPower(-0.3);
                }
                else if (liftMotorPos < liftMotorMin)
                {
                    liftMotor.setPower(0.3);
                }
                else
                {
                    liftMotor.setPower(0.0);
                }
            }
        }

        // Controls the extending pf the claw arm
        if (gamepad1.right_bumper)
        {
            clawServo.setPower(-0.348);
        }
        else if (gamepad1.left_bumper)
        {
            clawServo.setPower(-0.13);
        }

        // Controls the spinning of the pick-and-place
        if (gamepad1.dpad_left && !spinning)
        {
            spinning = true;
            spinPos += 0.044;
        }
        else if (gamepad1.dpad_right && !spinning)
        {
            spinning = true;
            spinPos -= 0.044;
        }
        else if (gamepad1.triangle && !spinning) // center
        {
            spinning = true;
            spinPos = -0.65;
        }

        if (!gamepad1.dpad_left && !gamepad1.dpad_right && spinning) // reset spinning flag
        {
            spinning = false;
        }

        // Clamp for spin motor
        if (spinPos < -0.773)
        {
            spinPos = -0.773;
        }
        if (spinPos > -0.392)
        {
            spinPos = -0.392;
        }


        if (gamepad1.share && !extending)
        {
            extending = true;
            extendPos += 0.06875;
        }
        else if (gamepad1.options && !extending)
        {
            extending = true;
            extendPos -= 0.06875;
        }

        if (!gamepad1.options && !gamepad1.share && extending) // reset extending flag
        {
            extending = false;
        }

        // Clamp for extend motor
        if (extendPos > 0.19)
        {
            extendPos = 0.19;
        }
        if (extendPos < -0.44)
        {
            extendPos = -0.44;
        }

        spinServo.setPower(spinPos);
        extendServo.setPower(extendPos);
        
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
