package simon.app.testgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Pipe {
	private int x, y;

	private int velocityX;
	private Bitmap image;

	public Pipe(Bitmap bmp, boolean inverted, int yTipPosition, int xPosition, int velocity) {
		velocityX = velocity;
		x = xPosition;
		image = bmp;

		if (inverted) {
			y = yTipPosition - image.getHeight();
		} else {
			y = yTipPosition;
		}

	}

	public Rect getRect() {
		Rect temp = new Rect();
		temp.set(x, y, x + image.getWidth(), y + image.getHeight());
		return temp;
	}

	public void update() {
		x += velocityX;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(image, x, y, null);
	}

	public boolean birdCollide(Bird bird) {
		return Rect.intersects(getRect(), bird.getRect());
	}

	public int getMiddleX() {
		return x + image.getWidth() / 2;
	}

}
