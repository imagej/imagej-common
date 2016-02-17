package net.imagej.convert;

import java.lang.reflect.Type;

import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.img.Img;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ConversionUtils;
import org.scijava.util.GenericUtils;

@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class, priority = Priority.LOW_PRIORITY)
public class ImgPlusToImgConverter extends AbstractConverter<ImgPlus, Img> {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object src, Class<T> dest) {
		ImgPlus imgPlus = (ImgPlus)src;
		return (T) imgPlus.getImg();
	}

	@Override
	public Class<Img> getOutputType() {
		return Img.class;
	}

	@Override
	public Class<ImgPlus> getInputType() {
		return ImgPlus.class;
	}
//
//	@Override
//	public boolean supports(final ConversionRequest c) {
//		final Object sourceObject = c.sourceObject();
//		if (sourceObject != null && Dataset.class.isAssignableFrom(sourceObject.getClass())) {
//			final Dataset data = (Dataset)sourceObject;
//			return super.supports(new ConversionRequest(data.getImgPlus().getImg(), c.destType()));
//		}
//		return super.supports(c);
//	}

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		if (src == null) return false;
		final Class<?> destClass = GenericUtils.getClass(dest);
		return canConvert(src, destClass);
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		if (src == null) return false;
		Class<?> srcClass = src.getClass();
		if (Dataset.class.isAssignableFrom(srcClass)) {
			final Dataset data = (Dataset)src;
			srcClass = data.getImgPlus().getImg().getClass();
		}

		return ConversionUtils.canCast(srcClass, dest);
	}
}
