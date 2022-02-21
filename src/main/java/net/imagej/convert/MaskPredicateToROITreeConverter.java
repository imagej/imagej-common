
package net.imagej.convert;

import net.imagej.roi.DefaultROITree;
import net.imglib2.roi.MaskPredicate;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import java.util.Collections;

@Plugin(type= Converter.class)
public class MaskPredicateToROITreeConverter extends
	AbstractConverter<MaskPredicate, DefaultROITree>
{

	@Override
	public <T> T convert(Object src, Class<T> dest) {
		DefaultROITree tree = new DefaultROITree();
		tree.addROIs(Collections.singletonList((MaskPredicate<?>) src));
		return (T) tree;
	}

	@Override
	public Class<DefaultROITree> getOutputType() {
		return DefaultROITree.class;
	}

	@Override
	public Class<MaskPredicate> getInputType() {
		return MaskPredicate.class;
	}
}
