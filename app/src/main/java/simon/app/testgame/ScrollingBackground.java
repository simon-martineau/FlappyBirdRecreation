package simon.app.testgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class ScrollingBackground {
	private int x;
	private int velocityX;

	private Bitmap image;

	public ScrollingBackground(int speed, Bitmap bmp) {
		velocityX = speed;
		image = bmp;
		x = image.getWidth();
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, x, 0, null);
		canvas.drawBitmap(image, x - image.getWidth(), 0, null);
		// Log.i("BackgroundHeight", "Height = " + image.getWidth());
	}

	public void update() {
		x += velocityX;
		if (x <= 0) x = image.getWidth();
	}

}
