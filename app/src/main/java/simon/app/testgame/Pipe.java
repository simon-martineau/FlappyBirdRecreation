package simon.app.testgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

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

	public boolean birdCollide(Player player) {
		return Rect.intersects(getRect(), player.getRect());
	}

	public int getMiddleX() {
		return x + image.getWidth() / 2;
	}


}
