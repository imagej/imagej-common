
package net.imagej.convert;

import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;
import org.scijava.convert.Converter;
import org.scijava.convert.AbstractConverter;
import org.scijava.plugin.Plugin;
import org.scijava.util.TreeNode;
import org.scijava.util.Types;

import java.lang.reflect.Type;

/**
 * Abstract {@link Converter} from ROITree to SuperEllipsoid
 *
 * @author Gabriel Selzer
 */
@Plugin(type = Converter.class)
public class ROITreeToMaskPredicateConverter extends
	AbstractConverter<ROITree, MaskPredicate> //
{

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return canConvert(src, Types.raw(dest));
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		// Assert that src is a ROITree and dest a MaskPredicate
		if (!(src instanceof ROITree)) return false;
		if (!(MaskPredicate.class.isAssignableFrom(dest))) return false;
		ROITree srcTree = (ROITree) src;
		// Assert exactly one ROI in the tree
		if (srcTree.children().size() != 1) return false;
		TreeNode<?> onlyChild = srcTree.children().get(0);
		if (!onlyChild.children().isEmpty()) return false;

		// Assert the only ROI is of the type we are looking for
		return dest.isInstance(onlyChild.data());
	}

	/**
	 * Overriding {@link AbstractConverter#canConvert(Class, Class)} to avoid
	 * failures from that behavior.
	 * 
	 * @param src the {@link Class} converted from
	 * @param dest the {@link Class} converted to
	 * @return true iff this {@link Converter} can convert from {@code from} to
	 *         {@code to}
	 */
	@Override
	public boolean canConvert(final Class<?> src, final Class<?> dest) {
		if (!(ROITree.class.isAssignableFrom(src))) return false;
		if (!(MaskPredicate.class.isAssignableFrom(dest))) return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		ROITree srcTree = (ROITree) src;
		return (T) srcTree.children().get(0).data();
	}

	@Override
	public Class<MaskPredicate> getOutputType() {
		return MaskPredicate.class;
	}

	@Override
	public Class<ROITree> getInputType() {
		return ROITree.class;
	}
}
