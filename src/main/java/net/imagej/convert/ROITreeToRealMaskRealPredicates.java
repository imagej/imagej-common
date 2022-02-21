package net.imagej.convert;

import net.imagej.roi.ROITree;
import net.imglib2.roi.geom.real.SuperEllipsoid;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;
import org.scijava.util.TreeNode;
import org.scijava.util.Types;

import java.lang.reflect.Type;

public class ROITreeToRealMaskRealPredicates
{


	/**
	 * Converter from ROITree to SuperEllipsoid
	 * @author Gabriel Selzer
	 */
	@Plugin(type = Converter.class)
	public static class ConvertIntArrayToFinalInterval //
			extends AbstractConverter<ROITree, SuperEllipsoid> //
	{
		@Override
		public boolean canConvert(final Object src, final Type dest) {
			return canConvert( src, Types.raw(dest));
		}

		@Override
		public boolean canConvert(final Object src, final Class<?> dest) {
			// Assert that src is a ROITree and dest SuperEllipsoid
			if (!super.canConvert( src, dest)) return false;
			ROITree srcTree = (ROITree) src;
			// Assert exactly one ROI in the tree
			if (srcTree.children().size() != 1) return false;
			TreeNode<?> onlyChild = srcTree.children().get( 0 );
			if (!onlyChild.children().isEmpty()) return false;

			// Assert the only ROI is a SuperEllipsoid
			return getOutputType().isInstance( onlyChild.data() );
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T convert(final Object src, final Class<T> dest) {
			ROITree srcTree = (ROITree ) src;
			return (T) srcTree.children().get( 0 ).data();
		}


		@Override
		public Class<SuperEllipsoid> getOutputType() {
			return SuperEllipsoid.class;
		}

		@Override
		public Class<ROITree> getInputType() {
			return ROITree.class;
		}
	}
}
