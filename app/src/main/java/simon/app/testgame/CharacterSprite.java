package simon.app.testgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;


public class CharacterSprite {
	private Bitmap image;
	private int x, y;
	private int velocityX = 10;
	private int velocityY = 10;
	private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
	private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

	public void update() {
		x += velocityX;
		y += velocityY;

		if ((x > screenWidth - image.getWidth()) || (x < 0)) {
			velocityX *= -1;
		}
		if ((y > screenHeight - image.getHeight()) || (y < 0)) {
			velocityY *= -1;
		}
	}

	public CharacterSprite(Bitmap bitmap) {
		x = 100;
		y = 100;
		image = Bitmap.createScaledBitmap(bitmap, 300, 600, true);
		bitmap.recycle();
	}

	public void draw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setFilterBitmap(true);
		p.setDither(true);

		canvas.drawBitmap(image, x, y, p);
	}
}
