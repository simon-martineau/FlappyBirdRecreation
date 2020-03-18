package simon.app.testgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Planet {
	private int x, y;
	private int velocityX;
	private Bitmap image;

	public Planet(int height, Bitmap bmp, int speed, int distance) {
		velocityX = speed;
		image = bmp;
		y = height;
		x = distance;
	}

	public void update() {
		x += velocityX;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, x, y, null);
	}


}
