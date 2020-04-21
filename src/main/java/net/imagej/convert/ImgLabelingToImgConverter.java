package net.imagej.convert;

import java.util.Set;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.Types;

import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.type.numeric.IntegerType;

/**
 * This {@code Converter} converts an {@code ImgLabeling} to an {@code Img}.
 * 
 * If the {@code ImgLabeling} has non-overlapping integer labels, the result
 * {@code Img} will be filled with values corresponding to those labels.
 * Otherwise, {@code ImgLabeling#getIndexImg()} is returned.
 * 
 * @author Jan Eglinger
 *
 * @param <T>
 *            the ImgLib2 type used for the index image of the labeling
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class)
public class ImgLabelingToImgConverter<T extends IntegerType<T>> extends AbstractConverter<ImgLabeling, Img>{

	@Parameter
	private LogService log;

	@SuppressWarnings("unchecked")
	@Override
	public <I> I convert(Object object, Class<I> type) {
		ImgLabeling<Integer, T> labeling = (ImgLabeling<Integer, T>) object;
		// check if only singleton labels
		if (!singletonLabels(labeling)) {
			log.warn("Converting ImgLabeling with overlapping labels. Labels cannot be preserved in output, creating continuous integers.");
			return (I) ((ImgLabeling) labeling).getIndexImg();
		}
		// check if labeling type is integer
		if (!integerLabels(labeling)) {
			log.warn("Converting non-integer label type. Labels cannot be preserved in output, creating continuous integers.");
			return (I) (labeling).getIndexImg();
		}
		// else, create new img, populate with actual labels
		Img<T> indexImg = (Img<T>) labeling.getIndexImg();
		Img<T> img = indexImg.factory().create(indexImg);
		LabelingMapping<Integer> mapping = labeling.getMapping();
		LoopBuilder.setImages(labeling, img).forEachPixel( (l, i) -> {
			Set<Integer> labels = mapping.labelsAtIndex(l.getIndex().getInteger());
			if (!labels.isEmpty()) {
				i.setInteger(labels.iterator().next()); // assuming singleton set here
			}
		});
		return (I) img;
	}

	private boolean integerLabels(ImgLabeling labeling) {
		// Check if the first non-empty label set is an integer
		return Types.isAssignable(labeling.getMapping().labelsAtIndex(1).iterator().next().getClass(), Integer.class);
	}

	private boolean singletonLabels(ImgLabeling labeling) {
		LabelingMapping mapping = labeling.getMapping();
		// TODO should we actually iterate over sets to assert that each is a singleton?
		return mapping.getLabels().size() == mapping.getLabelSets().size() - 1; // the empty set is always present
	}

	@Override
	public Class<ImgLabeling> getInputType() {
		return ImgLabeling.class;
	}

	@Override
	public Class<Img> getOutputType() {
		return Img.class;
	}

}
