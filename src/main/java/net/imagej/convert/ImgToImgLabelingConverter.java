package net.imagej.convert;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.IntegerType;

/**
 * This {@code Converter} converts an {@code Img} to an {@code ImgLabeling}. The
 * pixel values will be the labels of the result {@code ImgLabeling}.
 * 
 * @author Jan Eglinger
 *
 * @param <T>
 *            the ImgLib2 type of the input {@code Img} and the index image of
 *            the resulting {@code ImgLabeling}
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class)
public class ImgToImgLabelingConverter<T extends IntegerType<T>> extends AbstractConverter<Img, ImgLabeling> {

	@SuppressWarnings("unchecked")
	@Override
	public <L> L convert(Object img, Class<L> type) {
		Img<T> indexImg = ((Img<T>) img).factory().create(((Img<T>) img));
		ImgLabeling<Integer, T> labeling = new ImgLabeling<>(indexImg);

		LoopBuilder.setImages(((Img<T>) img), labeling).forEachPixel((i, l) -> {
			int v = i.getInteger();
			if (v != 0)
				l.add(v);
		});

//		Cursor<T> imgCursor = ((Img<T>) img).cursor();
//		Cursor<LabelingType<Integer>> labelCursor = labeling.cursor();
//		while (imgCursor.hasNext()) {
//			T value = imgCursor.next();
//			if (value.getInteger() == 0) {
//				labelCursor.fwd();
//			} else {
//				labelCursor.next().add(value.getInteger());
//			}
//		}

		return (L) labeling;
	}

	@Override
	public Class<Img> getInputType() {
		return Img.class;
	}

	@Override
	public Class<ImgLabeling> getOutputType() {
		return ImgLabeling.class;
	}

}
