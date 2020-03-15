package simon.app.testgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bird {
	private int x = (int) (GameView.screenWidth * 0.2);
	private double y = 200;

	private double accelerationY = 1.6;
	private Matrix matrix;
	private double velocityY = 0;
	public boolean isGroundCollide = false;
	public int groundLevel;

	private Bitmap image;

	public Bird(Bitmap bmp, int groundLevel) {

		image = Bitmap.createScaledBitmap(bmp, 180, 127, true);
		this.groundLevel = groundLevel;
		matrix = new Matrix();
	}

	public void update() {
		velocityY += accelerationY;
		y += velocityY;

		if (isGroundCollide) {
			velocityY = 0;
			y = groundLevel - image.getHeight();
			isGroundCollide = false;
		}

		if (topCollide()) {
			y = 0;
			velocityY = 0;
		}

		int rotation = (int) velocityY;
		if (rotation > 90) rotation = 90;

		matrix.reset();
		matrix.postRotate(rotation, image.getWidth() / 2f, image.getHeight() / 2f);
		matrix.postTranslate(x, (int) y);

//

	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, matrix, null);
	}

	public void flap() {
		velocityY = -30;
	}

	public Rect getRect() {
		Rect temp = new Rect();
		temp.set(x, (int) Math.round(y), x + image.getWidth(), (int) (Math.round(y) + image.getHeight()));
		return temp;
	}

	public boolean groundCollide(Rect ground) {
		return Rect.intersects(getRect(), ground);
	}

	public boolean topCollide() {
		return (y < 0);
	}

	public void drawGameOver(Canvas canvas) {
		y = groundLevel + 200;
		x = canvas.getWidth() / 2 - image.getWidth() / 2;
		matrix.reset();
		matrix.postTranslate(x, (int) y);
		canvas.drawBitmap(image, matrix, null);

		Paint textPaint = new Paint();
		textPaint.setTextSize(80);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextAlign(Paint.Align.CENTER);


		int xPos = (canvas.getWidth() / 2);
		int yPos = (int) (groundLevel + 400 - ((textPaint.descent() + textPaint.ascent()) / 2));

		canvas.drawText("Game Over", xPos, yPos, textPaint);
	}

	public int getCenterX () {
		return x + image.getWidth();
	}


}
