
package net.imagej.convert;

import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.ClosedWritableBox;
import net.imglib2.roi.geom.real.ClosedWritableEllipsoid;
import net.imglib2.roi.geom.real.ClosedWritablePolygon2D;
import net.imglib2.roi.geom.real.DefaultWritableLine;
import net.imglib2.roi.geom.real.DefaultWritablePolyline;
import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * {@link ROITreeToMaskPredicateConverter} implementations.
 *
 * @author Gabriel Selzer
 */
public class ROITreeToMaskPredicateConverters {

	@Plugin(type = Converter.class)
	public static class ROITreeToClosedWritableEllipsoidConverter extends
		ROITreeToMaskPredicateConverter<ClosedWritableEllipsoid>
	{

		@Override
		public Class<ClosedWritableEllipsoid> getOutputType() {
			return ClosedWritableEllipsoid.class;
		}
	}

	@Plugin(type = Converter.class)
	public static class ROITreeToClosedWritableBoxConverter extends
		ROITreeToMaskPredicateConverter<ClosedWritableBox>
	{

		@Override
		public Class<ClosedWritableBox> getOutputType() {
			return ClosedWritableBox.class;
		}
	}

	@Plugin(type = Converter.class)
	public static class ROITreeToClosedWritablePolygon2DConverter extends
		ROITreeToMaskPredicateConverter<ClosedWritablePolygon2D>
	{

		@Override
		public Class<ClosedWritablePolygon2D> getOutputType() {
			return ClosedWritablePolygon2D.class;
		}
	}

	@Plugin(type = Converter.class)
	public static class ROITreeToDefaultWritableLineConverter extends
		ROITreeToMaskPredicateConverter<DefaultWritableLine>
	{

		@Override
		public Class<DefaultWritableLine> getOutputType() {
			return DefaultWritableLine.class;
		}
	}

	@Plugin(type = Converter.class)
	public static class ROITreeToDefaultWritablePolylineConverter extends
		ROITreeToMaskPredicateConverter<DefaultWritablePolyline>
	{

		@Override
		public Class<DefaultWritablePolyline> getOutputType() {
			return DefaultWritablePolyline.class;
		}
	}

	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ROITreeToRealMaskConverter extends
			ROITreeToMaskPredicateConverter<RealMask>
	{

		@Override
		public Class<RealMask> getOutputType() {
			return RealMask.class;
		}
	}

}
