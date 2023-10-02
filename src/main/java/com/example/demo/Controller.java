package com.example.demo;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.KeyCode;


import javafx.scene.paint.Color;


import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField end;

    @FXML
    private Button execute;

    @FXML
    private Canvas mainCanvas;

    @FXML
    private TextField start;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        execute.setOnAction(actionEvent -> {
            GraphicsContext ctx = mainCanvas.getGraphicsContext2D();
            ctx.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
            draw(ctx, (int) mainCanvas.getWidth() / 2, (int) mainCanvas.getHeight() / 2,
                    100,
                    Double.parseDouble(start.getText()),
                    Double.parseDouble(end.getText()),
                    Color.BLUE, Color.RED);
        });
        start.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                execute.fire();
            }
        });
        end.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                execute.fire();
            }
        });
    }

    @FXML
    void draw(final GraphicsContext graphicsContext, int center_x, int center_y, // центр окружности (x,y)
              int radius, // радиус
              double startAngle, // начальный угол сегмента в градусах
              double endAngle, // конечный угол сегмента в градусах
              Color startColor, // начальный цвет для интерполяции
              Color endColor) { // конечный цвет для интерполяции
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        for (int x = -radius; x <= radius; x++) { // алгоритм заполнения окружности
            int height = (int) Math.sqrt(radius * radius - x * x);
            for (int y = -height; y <= height; y++) {
                if (isInsideSegment(x + center_x, y + center_y, center_x, center_y, radius, startAngle, endAngle)) {
                    double distanceFromCenter = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                    double colorRatio = distanceFromCenter / radius;
                    int red = (int) (startColor.getRed() * (1 - colorRatio) * 255 + endColor.getRed() * colorRatio * 255);
                    int green = (int) (startColor.getGreen() * (1 - colorRatio) * 255 + endColor.getGreen() * colorRatio * 255);
                    int blue = (int) (startColor.getBlue() * (1 - colorRatio) * 255 + endColor.getBlue() * colorRatio * 255);
                    Color color = Color.rgb(red, green, blue);
                    pixelWriter.setColor(x + center_x, y + center_y, color); // установка точки
                } else if (Math.abs(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) - radius) < 1) {
                    pixelWriter.setColor(x + center_x, y + center_y, Color.BLACK); // рисуем окружность
                }
            }
        }
    }

    private boolean isInsideSegment(int x, int y, int center_x, int center_y, int radius, double startAngle, double endAngle) {
        int relpointx = x - center_x;
        int relpointy = y - center_y;
        double angle = Math.toDegrees(Math.atan2(-relpointy, relpointx));
        if (angle < 0) {
            angle += 360;
        }
        if (endAngle > 360) {
            endAngle -= 360;
            if (angle >= startAngle || angle <= endAngle) {
                double distanceFromCenter = Math.sqrt(Math.pow(relpointx, 2) + Math.pow(relpointy, 2));
                return distanceFromCenter <= radius;
            }
        } else if (angle >= startAngle && angle <= endAngle) {
            double distanceFromCenter = Math.sqrt(Math.pow(relpointx, 2) + Math.pow(relpointy, 2));
            return distanceFromCenter <= radius;
        }
        return false;
    }


}
