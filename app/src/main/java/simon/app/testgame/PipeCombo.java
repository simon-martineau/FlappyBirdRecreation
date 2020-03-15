package simon.app.testgame;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class PipeCombo {
	private List<Pipe> pipes = new ArrayList<Pipe>();
	private int x;

	private boolean passedBird = false;

	public PipeCombo(Pipe top, Pipe bottom) {
		pipes.add(top);
		pipes.add(bottom);
		x = (top.getRect().left + top.getRect().right) / 2;
	}

	public void update() {
		for (Pipe p : pipes) {
			p.update();
		}
		x = pipes.get(0).getMiddleX();
	}

	public void draw(Canvas canvas) {
		for (Pipe p : pipes) {
			p.draw(canvas);
		}
	}

	public boolean addScore(Bird bird) {
		if (!passedBird) {
			if (bird.getCenterX() > x) {
				passedBird = true;
				return true;
			}
		}
		return false;
	}

	public boolean birdCollide(Bird bird) {
		for (Pipe p : pipes) {
			if (p.birdCollide(bird)) return true;
		}
		return false;
	}

}
