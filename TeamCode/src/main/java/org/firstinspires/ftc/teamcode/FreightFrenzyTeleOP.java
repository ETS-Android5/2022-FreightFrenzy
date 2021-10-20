package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="LogisNICS TeleOP", group="Interactive Opmode")

public class FreightFrenzyTeleOP extends OpMode
{

    // Control Hub
    private DcMotor frontRightMotor = null;
    private DcMotor frontLeftMotor = null;
    private DcMotor backLeftMotor = null;
    private DcMotor backRightMotor = null;

    // Expansion Hub
    private DcMotor intakeMotor = null;

    // Doubles for the power of our motors
    private double frontLeftPower, frontRightPower, backLeftPower, backRightPower;
    private double frontLeftPan, frontRightPan, backLeftPan, backRightPan;

    // Scale variable for all drive, turn, and pan functions
    private double powerScale = 0.8;

    @Override
    public void init ()
    {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        intakeMotor = hardwareMap.dcMotor.get("intakeMotor");

        frontLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("say: ", "Working");
        telemetry.addData("say: ", "Last Update: 2021-10-20 16:09");
    }
    @Override
    public void init_loop()
    {
        // pls do
    }

    @Override
    public void loop()
    {
        double drive = gamepad1.left_stick_y;
        double turn = gamepad1.left_stick_x;
        double pan = gamepad1.right_stick_x;

        frontLeftPower = Range.clip(drive + turn, -1.0, 1.0);
        frontRightPower = Range.clip(drive + turn, -1.0, 1.0);
        backLeftPower = Range.clip(drive - turn, -1.0, 1.0);
        backRightPower = Range.clip(drive - turn, -1.0, 1.0);
        frontLeftMotor.setPower(powerScale * frontLeftPower);
        frontRightMotor.setPower(powerScale * frontRightPower);
        backLeftMotor.setPower(powerScale * backLeftPower);
        backRightMotor.setPower(powerScale * backRightPower);

        frontLeftPan = Range.clip(drive + pan, -1.0, 1.0);
        frontRightPan = Range.clip(drive - pan, -1.0, 1.0);
        backLeftPan = Range.clip(drive + pan, -1.0, 1.0);
        backRightPan = Range.clip(drive - pan, -1.0, 1.0);
        frontLeftMotor.setPower(powerScale * frontLeftPan);
        frontRightMotor.setPower(powerScale * frontRightPan);
        backLeftMotor.setPower(powerScale * backLeftPan);
        backRightMotor.setPower(powerScale * backRightPan);

        if (gamepad1.right_trigger > 0)
        {
            intakeMotor.setPower(0.9);
        }
        else
        {
            intakeMotor.setPower(0.7);
        }
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
    }
}
