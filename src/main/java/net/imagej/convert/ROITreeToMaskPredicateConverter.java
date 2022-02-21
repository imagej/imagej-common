
package net.imagej.convert;

import net.imagej.roi.ROITree;
import net.imglib2.RealLocalizable;
import net.imglib2.roi.MaskPredicate;
import org.scijava.convert.Converter;
import org.scijava.convert.AbstractConverter;
import org.scijava.util.TreeNode;
import org.scijava.util.Types;

import java.lang.reflect.Type;

/**
 * Abstract {@link Converter} from ROITree to SuperEllipsoid
 *
 * @author Gabriel Selzer
 */
public abstract class ROITreeToMaskPredicateConverter<M extends MaskPredicate<? extends RealLocalizable>>
	extends AbstractConverter<ROITree, M> //
{

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return canConvert(src, Types.raw(dest));
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		// Assert that src is a ROITree and dest a MaskPredicate
		if (!super.canConvert(src, dest)) return false;
		ROITree srcTree = (ROITree) src;
		// Assert exactly one ROI in the tree
		if (srcTree.children().size() != 1) return false;
		TreeNode<?> onlyChild = srcTree.children().get(0);
		if (!onlyChild.children().isEmpty()) return false;

		// Assert the only ROI is of the type we are looking for
		return dest.isInstance(onlyChild.data());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		ROITree srcTree = (ROITree) src;
		return (T) srcTree.children().get(0).data();
	}

	@Override
	public abstract Class<M> getOutputType();

	@Override
	public Class<ROITree> getInputType() {
		return ROITree.class;
	}
}
