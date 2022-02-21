
package net.imagej.convert;

import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.*;
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
	public static class ROITreeToOpenWritableEllipsoidConverter extends
			ROITreeToMaskPredicateConverter<OpenWritableEllipsoid>
	{

		@Override
		public Class<OpenWritableEllipsoid> getOutputType() {
			return OpenWritableEllipsoid.class;
		}
	}

	@Plugin(type = Converter.class)
	public static class ROITreeToClosedWritableSphereConverter extends
			ROITreeToMaskPredicateConverter<ClosedWritableSphere>
	{

		@Override
		public Class<ClosedWritableSphere> getOutputType() {
			return ClosedWritableSphere.class;
		}
	}

	@Plugin(type = Converter.class)
	public static class ROITreeToOpenWritableSphereConverter extends
			ROITreeToMaskPredicateConverter<OpenWritableSphere>
	{

		@Override
		public Class<OpenWritableSphere> getOutputType() {
			return OpenWritableSphere.class;
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
	public static class ROITreeToOpenWritableBoxConverter extends
			ROITreeToMaskPredicateConverter<OpenWritableBox>
	{

		@Override
		public Class<OpenWritableBox> getOutputType() {
			return OpenWritableBox.class;
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
	public static class ROITreeToOpenWritablePolygon2DConverter extends
			ROITreeToMaskPredicateConverter<OpenWritablePolygon2D>
	{

		@Override
		public Class<OpenWritablePolygon2D> getOutputType() {
			return OpenWritablePolygon2D.class;
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

}
