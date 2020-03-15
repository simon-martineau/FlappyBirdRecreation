package simon.app.testgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	public MainThread thread;
	private List<PipeCombo> mPipeCombos = new ArrayList<PipeCombo>();
	public static final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
	public static final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
	private long ticks = 0;

	private Rect ground;
	private Paint groundPaint;
	private Rect sky;
	private Paint skyPaint;

	private Bird bird;

	private Bitmap pipeBitmap;
	private Bitmap reversePipeBitmap;
	private Bitmap background;

	private int pipeCoolDown = 120;
	public int score = 0;

	private boolean userTouched = false;
	private boolean collision = false;
	public boolean gameOver = false;



	public GameView(Context context) {
		super(context);

		getHolder().addCallback(this);

		thread = new MainThread(getHolder(), this);
		setFocusable(true);
	}

	public void update() {
		if (!gameOver) {

			if (pipeCoolDown > 0) {
				pipeCoolDown--;
			} else {
				if (ticks % 80 == 0) {
					generatePipeCombo(550, (int) (45 + Math.random() * 35));
				}
			}

			if (userTouched) {
				bird.flap();
				userTouched = false;
			} else {
				bird.isGroundCollide = bird.groundCollide(ground);
			}
			bird.update();

			for (PipeCombo pipeCombo : mPipeCombos) {
				if (pipeCombo.birdCollide(bird)) {
					collision = true;
				}
				if (pipeCombo.addScore(bird)) score ++;
				pipeCombo.update();
			}

			if (collision) {
				gameOver = true;
			}

			ticks++;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		canvas.drawBitmap(background, 0, 0, null);
//		canvas.drawRect(sky, skyPaint);
		bird.draw(canvas);
		if (canvas != null) {
			for (PipeCombo pipeCombo : mPipeCombos) {
				pipeCombo.draw(canvas);
			}

			canvas.drawRect(ground, groundPaint);

			Paint p = new Paint();
			p.setColor(Color.BLACK);
			p.setTextSize(60);
			canvas.drawText("FPS : " + thread.averageFPS, 100, screenHeight - 50, p);
			drawGameScore(canvas);
			if (gameOver) bird.drawGameOver(canvas);
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();

		pipeBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe), 280, 1608, true);
		reversePipeBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe_inverted), 280, 1608, true);
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.space_background), screenWidth, screenHeight, true);

		skyPaint = new Paint();
		skyPaint.setColor(Color.rgb(168, 234, 240));
		sky = new Rect();
		sky.set(0, 0, screenWidth, (int) (screenHeight - 0.25 * screenHeight));

		groundPaint = new Paint();
		groundPaint.setColor(Color.rgb(50, 50, 50));
		ground = new Rect();
		ground.set(0, (int) (screenHeight - 0.25 * screenHeight), screenWidth, screenHeight);

		bird = new Bird(BitmapFactory.decodeResource(getResources(), R.drawable.bird), ground.top);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while (retry) {
			try {
				thread.setRunning(false);
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry = false;
		}
	}

	 void generatePipeCombo(int separation, int heightPercentage) {
		if (separation % 2 != 0) separation += 1;

		int middleY =  (int) (screenHeight - (screenHeight * (heightPercentage / 100.0)));

		Pipe bottomPipe = new Pipe(pipeBitmap, false, middleY + separation / 2, screenWidth + 300, -10);
		Pipe topPipe = new Pipe(reversePipeBitmap, true, middleY - separation / 2, screenWidth + 300, -10);

		PipeCombo combo = new PipeCombo(topPipe, bottomPipe);

		mPipeCombos.add(combo);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		userTouched = true;
		return super.onTouchEvent(event);
	}

	private void drawGameScore(Canvas canvas) {
		Paint textPaint = new Paint();
		textPaint.setTextSize(80);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextAlign(Paint.Align.CENTER);


		int xPos = (canvas.getWidth() / 2);
		int yPos = (int) (ground.top + 500 - ((textPaint.descent() + textPaint.ascent()) / 2));

		canvas.drawText("Score: " + score, xPos, yPos, textPaint);
	}
}
