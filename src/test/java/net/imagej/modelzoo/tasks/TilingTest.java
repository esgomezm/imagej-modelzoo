
package net.imagej.modelzoo.tasks;

import net.imagej.Dataset;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.modelzoo.AbstractModelZooTest;
import net.imagej.modelzoo.consumer.task.DefaultTask;
import net.imagej.modelzoo.consumer.task.Task;
import net.imagej.modelzoo.consumer.tiling.AdvancedTiledView;
import net.imagej.modelzoo.consumer.tiling.DefaultTiling;
import net.imagej.modelzoo.consumer.tiling.Tiling;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TilingTest extends AbstractModelZooTest {

	@Test
	public void testTilingZXY() {

		final Tiling tiling = new DefaultTiling(8, 1, 32, 32);
		final long[] datasetSize = { 10, 50, 100 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y };
		final Task task = new DefaultTask();

		createImageJ();

		final Dataset dataset = ij.dataset().create(new FloatType(), datasetSize,
			"", axes);
		final RandomAccessibleInterval<FloatType> input =
			(RandomAccessibleInterval<FloatType>) dataset.getImgPlus();
		final AdvancedTiledView<FloatType> tiledView = tiling.preprocess(input,
				axes, getTilingActions(dataset), task);

		tiledView.getProcessedTiles().clear();
		final Cursor<RandomAccessibleInterval<FloatType>> cursor = Views.iterable(
			tiledView).cursor();
		while (cursor.hasNext()) {
			tiledView.getProcessedTiles().add(cursor.next());
		}

		final RandomAccessibleInterval<FloatType> output = tiling.postprocess(task,
			tiledView, axes);

		assertNotNull(tiledView);
		assertNotNull(output);

		tiledView.dispose();

		compareDimensions(input, output);
	}

	@Test
	public void testTilingZXYC() {

		final Tiling tiling = new DefaultTiling(8, 1, 32, 32);
		final long[] datasetSize = { 10, 50, 100, 1 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y, Axes.CHANNEL };
		final Task task = new DefaultTask();

		createImageJ();

		final Dataset dataset = ij.dataset().create(new FloatType(), datasetSize,
				"", axes);
		final RandomAccessibleInterval<FloatType> input =
				(RandomAccessibleInterval<FloatType>) dataset.getImgPlus();
		final AdvancedTiledView<FloatType> tiledView = tiling.preprocess(input,
				axes, getTilingActions(dataset), task);

		tiledView.getProcessedTiles().clear();
		final Cursor<RandomAccessibleInterval<FloatType>> cursor = Views.iterable(
				tiledView).cursor();
		while (cursor.hasNext()) {
			tiledView.getProcessedTiles().add(cursor.next());
		}

		final RandomAccessibleInterval<FloatType> output = tiling.postprocess(task,
				tiledView, axes);

		assertNotNull(tiledView);
		assertNotNull(output);

		tiledView.dispose();

		compareDimensions(input, output);
	}

	@Test
	public void testNoTiling() {

		final Tiling tiling = new DefaultTiling(8, 1, 32, 32);
		final long[] datasetSize = { 10, 50, 100 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y };
		Tiling.TilingAction[] actions = new Tiling.TilingAction[axes.length];
		Arrays.fill(actions, Tiling.TilingAction.NO_TILING);

		final AdvancedTiledView<FloatType> tiledView = runTiling(datasetSize, axes, tiling, actions);

		assertEquals(1, getNumTiles(tiledView));

		tiledView.dispose();
	}

	@Test
	public void testSingleDimensionTiling() {

		final Tiling tiling = new DefaultTiling(2, 1, 32, 32);
		final long[] datasetSize = { 10, 50, 100 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y };
		Tiling.TilingAction[] actions = new Tiling.TilingAction[axes.length];
		Arrays.fill(actions, Tiling.TilingAction.TILE_WITH_PADDING);

		final AdvancedTiledView<FloatType> tiledView = runTiling(datasetSize, axes, tiling, actions);

		assertEquals(1, tiledView.dimension(0));
		assertEquals(1, tiledView.dimension(1));
		assertEquals(2, tiledView.dimension(2));

		assertEquals(32, tiledView.getBlockSize()[0]);
		assertEquals(64, tiledView.getBlockSize()[1]);
		assertEquals(64, tiledView.getBlockSize()[2]);

		assertEquals(0, tiledView.getOverlap()[0]);
		assertEquals(0, tiledView.getOverlap()[1]);
		assertEquals(32, tiledView.getOverlap()[2]);

		tiledView.dispose();

	}

	@Test
	public void testMultiDimensionTiling() {

		final Tiling tiling = new DefaultTiling(8, 1, 32, 32);
		final long[] datasetSize = { 10, 50, 100 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y };
		Tiling.TilingAction[] actions = new Tiling.TilingAction[axes.length];
		Arrays.fill(actions, Tiling.TilingAction.TILE_WITH_PADDING);

		final AdvancedTiledView<FloatType> tiledView = runTiling(datasetSize, axes, tiling, actions);

		assertEquals(1, tiledView.dimension(0));
		assertEquals(2, tiledView.dimension(1));
		assertEquals(4, tiledView.dimension(2));

		assertEquals(32, tiledView.getBlockSize()[0]);
		assertEquals(32, tiledView.getBlockSize()[1]);
		assertEquals(32, tiledView.getBlockSize()[2]);

		assertEquals(0, tiledView.getOverlap()[0]);
		assertEquals(32, tiledView.getOverlap()[1]);
		assertEquals(32, tiledView.getOverlap()[2]);

		tiledView.dispose();

	}

	@Test
	public void testNoPaddingTiling() {

		final Tiling tiling = new DefaultTiling(10, 1, 10, 0);
		final long[] datasetSize = { 10, 20, 30 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y };
		Tiling.TilingAction[] actions = new Tiling.TilingAction[axes.length];
		Arrays.fill(actions, Tiling.TilingAction.TILE_WITH_PADDING);

		final AdvancedTiledView<FloatType> tiledView = runTiling(datasetSize, axes, tiling, actions);

		assertEquals(1, tiledView.dimension(0));
		assertEquals(2, tiledView.dimension(1));
		assertEquals(3, tiledView.dimension(2));

		assertEquals(10, tiledView.getBlockSize()[0]);
		assertEquals(10, tiledView.getBlockSize()[1]);
		assertEquals(10, tiledView.getBlockSize()[2]);

		assertEquals(0, tiledView.getOverlap()[0]);
		assertEquals(0, tiledView.getOverlap()[1]);
		assertEquals(0, tiledView.getOverlap()[2]);

		tiledView.dispose();

	}


	@Test
	public void testBatching() {

		final Tiling tiling = new DefaultTiling(1, 5, 10, 32);
		final long[] datasetSize = { 10, 50, 100 };
		final AxisType[] axes = { Axes.Z, Axes.X, Axes.Y };
		Tiling.TilingAction[] actions = new Tiling.TilingAction[axes.length];
		Arrays.fill(actions, Tiling.TilingAction.TILE_WITHOUT_PADDING);

		final AdvancedTiledView<FloatType> tiledView = runTiling(datasetSize, axes, tiling, actions);

		assertEquals(2, tiledView.dimension(0));
		assertEquals(10, tiledView.dimension(1));
		assertEquals(20, tiledView.dimension(2));

		assertEquals(5, tiledView.getBlockSize()[0]);
		assertEquals(5, tiledView.getBlockSize()[1]);
		assertEquals(5, tiledView.getBlockSize()[2]);

		assertEquals(0, tiledView.getOverlap()[0]);
		assertEquals(0, tiledView.getOverlap()[1]);
		assertEquals(0, tiledView.getOverlap()[2]);

		tiledView.dispose();

	}

	@Test
	public void testSmallDataset() {

		final Tiling tiling = new DefaultTiling(8, 1, 32, 32);
		final long[] datasetSize = { 3, 4, 5 };
		final AxisType[] axes = { Axes.X, Axes.Y, Axes.TIME };
		Tiling.TilingAction[] actions = new Tiling.TilingAction[axes.length];
		Arrays.fill(actions, Tiling.TilingAction.TILE_WITH_PADDING);

		final AdvancedTiledView<FloatType> tiledView = runTiling(datasetSize, axes, tiling, actions);

		assertEquals(1, tiledView.dimension(0));
		assertEquals(1, tiledView.dimension(1));
		assertEquals(1, tiledView.dimension(2));

		assertEquals(32, tiledView.getBlockSize()[0]);
		assertEquals(32, tiledView.getBlockSize()[1]);
		assertEquals(32, tiledView.getBlockSize()[2]);

		assertEquals(0, tiledView.getOverlap()[0]);
		assertEquals(0, tiledView.getOverlap()[1]);
		assertEquals(0, tiledView.getOverlap()[2]);

		tiledView.dispose();
	}
//
//	@Test
//	public void testNetworkTiling() {
//
//		final long[] datasetSize = { 3, 4, 5 };
//		final AxisType[] datasetAxes = { Axes.X, Axes.Y, Axes.TIME };
//		final long[] nodeShape = {-1,-1,-1,1};
//
//		launchImageJ();
//
//		Dataset dataset = ij.dataset().create(new FloatType(), datasetSize, "", datasetAxes);
//		final Tiling tiling = new DefaultTiling(8, 1, 32, 32);
//
//		InputNode node = new InputNode();
//		node.initialize(dataset);
//		node.setNodeShape(nodeShape);
//		node.setMapping(new AxisType[]{Axes.TIME, Axes.Y, Axes.X, Axes.CHANNEL});
//
//		Tiling.TilingAction[] actions = node.getTilingActions();
//
//		System.out.println(Arrays.toString(actions));
//
//		assertEquals(4, actions.length);
//		assertEquals(Tiling.TilingAction.TILE_WITH_PADDING, actions[0]); //x
//		assertEquals(Tiling.TilingAction.TILE_WITH_PADDING, actions[1]); //y
//		assertEquals(Tiling.TilingAction.TILE_WITHOUT_PADDING, actions[2]); //t
//		assertEquals(Tiling.TilingAction.NO_TILING, actions[3]); //channel
//
//		final RandomAccessibleInterval<FloatType> input =
//				(RandomAccessibleInterval<FloatType>) dataset.getImgPlus();
//
//		final AdvancedTiledView<FloatType> tiledView = tiling.preprocess(input,
//				datasetAxes, actions, new DefaultTask());
//
//		assertEquals(1, tiledView.dimension(0));
//		assertEquals(1, tiledView.dimension(1));
//		assertEquals(5, tiledView.dimension(2));
//
//		assertEquals(32, tiledView.getBlockSize()[0]);
//		assertEquals(32, tiledView.getBlockSize()[1]);
//		assertEquals(1, tiledView.getBlockSize()[2]);
//
//		assertEquals(0, tiledView.getOverlap()[0]);
//		assertEquals(0, tiledView.getOverlap()[1]);
//		assertEquals(0, tiledView.getOverlap()[2]);
//
//		tiledView.dispose();
//	}

	private AdvancedTiledView<FloatType> runTiling(long[] datasetSize, AxisType[] axes, Tiling tiling, Tiling.TilingAction[] actions) {

		createImageJ();

		final Dataset dataset = ij.dataset().create(new FloatType(), datasetSize,
				"", axes);
		final RandomAccessibleInterval<FloatType> input =
				(RandomAccessibleInterval<FloatType>) dataset.getImgPlus();

		return (AdvancedTiledView<FloatType>) tiling.preprocess(input,
				axes, actions, new DefaultTask());

	}

	private Tiling.TilingAction[] getTilingActions(Dataset input) {
		Tiling.TilingAction[] actions = new Tiling.TilingAction[input
			.numDimensions()];
		Arrays.fill(actions, Tiling.TilingAction.NO_TILING);
		for (int i = 0; i < actions.length; i++) {
			AxisType type = input.axis(i).type();
			if (type.isSpatial()) {
				actions[i] = Tiling.TilingAction.TILE_WITH_PADDING;
			}
		}
		return actions;
	}

}
