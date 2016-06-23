/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imagej.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.MixedTransformView;

public class MetaViews
{

	public static void main( final String[] args )
	{
		final int numDimensions = 5; // XYZCT

		final ArrayList< MetaDataItem< ? > > metadata = new ArrayList<>();

		metadata.add(
				new SimpleItem< >( "X Calib", "2 um",
						axisCollectionToFlags( numDimensions, new int[] { 0 } ) ) );
		metadata.add(
				new SimpleItem< >( "Y Calib", "5 um",
						axisCollectionToFlags( numDimensions, new int[] { 1 } ) ) );
		metadata.add(
				new SimpleItem< >( "Z Calib", "10 um",
						axisCollectionToFlags( numDimensions, new int[] { 2 } ) ) );
		metadata.add(
				new SimpleItem< >( "Spatial", "isSpatial",
						axisCollectionToFlags( numDimensions, new int[] { 0, 1, 2 } ) ) );

		final int[] numbersXY = new int[] {
				0, 1,
				2, 3 };
		final VaryingItem< IntType > numbersXYItem = new VaryingItem<>( "Numbers [XY]", ArrayImgs.ints( numbersXY, 2, 2 ),
				axisCollectionToFlags( numDimensions, new int[] { 0, 1 } ) );
		metadata.add( numbersXYItem );


		System.out.println( "XYZCT" );
		for ( final MetaDataItem< ? > item : metadata )
			System.out.println( item );


		System.out.println();
		System.out.println( "ZYXCT rotate" );
		for ( final MetaDataItem< ? > item : metadata )
			System.out.println( rotate( item, 0, 2 ) );


		System.out.println();
		System.out.println( "XYTCZ permute" );
		for ( final MetaDataItem< ? > item : metadata )
			System.out.println( permute( item, 2, 4 ) );


		System.out.println();
		System.out.println( "XYTC permute hyperSlice" );
		for ( final MetaDataItem< ? > item : metadata )
			System.out.println( hyperSlice( permute( item, 2, 4 ), 4, 0 ) );

/*
		System.out.println();
		System.out.println( "--------------------" );
		System.out.println();


		IntervalIterator i;
		i = new IntervalIterator( new int[] { 2, 2, 2, 2, 2 } );
		while( i.hasNext() )
		{
			i.fwd();
			final int[] coords = new int[ i.numDimensions() ];
			i.localize( coords );
			System.out.println( Util.printCoordinates( coords ) + " -> " + numbersXYItem.getAt( i ) );
		}

		System.out.println();
		System.out.println();

		System.out.println( "XCZTY permute" );
		i = new IntervalIterator( new int[] { 2, 2, 2, 2, 2 } );
		while( i.hasNext() )
		{
			i.fwd();
			final int[] coords = new int[ i.numDimensions() ];
			i.localize( coords );
			System.out.println( Util.printCoordinates( coords ) + " -> " + permute( numbersXYItem, 1, 4 ).getAt( i ) );
		}

		System.out.println();
		System.out.println();

		System.out.println( "CZTY permute hyperslice" );
		i = new IntervalIterator( new int[] { 2, 2, 2, 2 } );
		while( i.hasNext() )
		{
			i.fwd();
			final int[] coords = new int[ i.numDimensions() ];
			i.localize( coords );
			System.out.println( Util.printCoordinates( coords ) + " -> " +
					hyperSlice( permute( numbersXYItem, 1, 4 ), 0, 1 ).getAt( i ) );
		}

*/
	}







	public static interface MetaDataItem< T >
	{
		public T getAt( Localizable pos );
	}

	public static class SimpleItem< T > implements MetaDataItem< T >
	{
		private final String name;

		private final T data;

		public final boolean[] attachedToAxes;

		public SimpleItem( final String name, final T data )
		{
			this( name, data, null );
		}

		public SimpleItem( final String name, final T data, final boolean[] attachedToAxes )
		{
			this.name = name;
			this.data = data;
			this.attachedToAxes = attachedToAxes;
		}

		public boolean isAttachedToAxes()
		{
			return attachedToAxes != null;
		}

		@Override
		public T getAt( final Localizable pos )
		{
			return data;
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder( "SimpleItem \"" );
			sb.append( name );
			sb.append( "\"; " );

			if ( isAttachedToAxes() )
			{
				sb.append( "attached to axes {" );
				final int[] axes = flagsToAxisList( attachedToAxes );
				sb.append( axes[ 0 ] );
				for ( int i = 1; i < axes.length; ++i )
					sb.append( ", " + axes[i] );
				sb.append( "}; " );
			}
			else
				sb.append( "not attached to any axis; " );

			sb.append( "value = " + data );

			return sb.toString();
		}
	}

	public static class VaryingItem< T > implements MetaDataItem< T >
	{
		private final String name;

		private final RandomAccessible< T > data;

		public final boolean[] variesWithAxes;

		public final boolean[] attachedToAxes;

		public VaryingItem( final String name, final RandomAccessible< T > data, final boolean[] variesWithAxes )
		{
			this( name, data, variesWithAxes, null );
		}

		public VaryingItem( final String name, final RandomAccessible< T > data, final boolean[] variesWithAxes, final boolean[] attachedToAxes )
		{
			this.name = name;
			this.data = data;
			this.variesWithAxes = variesWithAxes;
			this.attachedToAxes = attachedToAxes;
		}

		public boolean isAttachedToAxes()
		{
			return attachedToAxes != null;
		}

		@Override
		public T getAt( final Localizable pos )
		{
			final RandomAccess< T > access = data.randomAccess();
			for ( int d = 0, i = 0; d < variesWithAxes.length; ++d )
				if ( variesWithAxes[ d ] )
					access.setPosition( pos.getLongPosition( d ), i++ );
			return access.get();
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder( "VaryingItem \"" );
			sb.append( name );
			sb.append( "\"; " );

			if ( isAttachedToAxes() )
			{
				sb.append( "attached to axes {" );
				final int[] axes = flagsToAxisList( attachedToAxes );
				sb.append( axes[ 0 ] );
				for ( int i = 1; i < axes.length; ++i )
					sb.append( ", " + axes[i] );
				sb.append( "}; " );
			}
			else
				sb.append( "not attached to any axis; " );

			return sb.toString();
		}

	}



//	public static < T > MixedTransformView< T > rotate( final RandomAccessible< T > randomAccessible, final int fromAxis, final int toAxis )
//	public static < T > IntervalView< T > rotate( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )


	public static < T > MetaDataItem< T > rotate( final MetaDataItem< T > item, final int fromAxis, final int toAxis )
	{
		if ( fromAxis == toAxis )
			return item;
		final int numSourceDimensions = getNumSourceDimensions( item );
		if ( numSourceDimensions < 0 )
			return item;
		return mixedTransform( item, getRotationTransform( fromAxis, toAxis, numSourceDimensions ) );
	}


	public static < T > MetaDataItem< T > permute( final MetaDataItem< T > item, final int fromAxis, final int toAxis )
	{
		if ( fromAxis == toAxis )
			return item;
		final int numSourceDimensions = getNumSourceDimensions( item );
		if ( numSourceDimensions < 0 )
			return item;
		return mixedTransform( item, getPermuteTransform( fromAxis, toAxis, numSourceDimensions ) );
	}


	public static < T > MetaDataItem< T > hyperSlice( final MetaDataItem< T > item, final int d, final long pos )
	{
		final int numSourceDimensions = getNumSourceDimensions( item );
		if ( numSourceDimensions < 0 )
			return item;
		return mixedTransform( item, getHyperSliceTransform( d, pos, numSourceDimensions ) );
	}


	/*
	 * Actual work is done here...
	 */

	public static < T > MetaDataItem< T > mixedTransform( final MetaDataItem< T > item, final Mixed transformToSource )
	{
		if ( item instanceof SimpleItem )
			return mix( ( SimpleItem< T > ) item, transformToSource );
		else if ( item instanceof VaryingItem )
			return mix( ( VaryingItem< T > ) item, transformToSource );
		else
			throw new IllegalArgumentException();
	}

	private static < T > SimpleItem< T > mix( final SimpleItem< T > item, final Mixed transformToSource )
	{
		if ( !item.isAttachedToAxes() )
			return item;

		final int n = transformToSource.numSourceDimensions();
		final int m = transformToSource.numTargetDimensions();

		final boolean[] attachedToAxes = new boolean[ n ];
		boolean remove = true;
		for ( int d = 0; d < m; ++d )
		{
			if ( item.attachedToAxes[ d ] && !transformToSource.getComponentZero( d ) )
			{
				attachedToAxes[ transformToSource.getComponentMapping( d ) ] = true;
				remove = false;
			}
		}

		if ( remove )
			// not attached to any axis anymore
			// remove item ...
			return null; // ???
		else
			return new SimpleItem< T >( item.name, item.data, attachedToAxes );
	}

	private static < T > MetaDataItem< T > mix( final VaryingItem< T > item, final Mixed transformToSource )
	{
		final int n = transformToSource.numSourceDimensions();
		final int m = transformToSource.numTargetDimensions();

		final boolean[] attachedToAxes;
		if ( item.isAttachedToAxes() )
		{
			attachedToAxes = new boolean[ n ];
			boolean remove = true;
			for ( int d = 0; d < m; ++d )
			{
				if ( item.attachedToAxes[ d ] && !transformToSource.getComponentZero( d ) )
				{
					attachedToAxes[ transformToSource.getComponentMapping( d ) ] = true;
					remove = false;
				}
			}

			if ( remove )
				// not attached to any axis anymore
				// remove item ...
				return null; // ???
		}
		else
			attachedToAxes = null;

		final boolean[] variesWithAxes = new boolean[ n ];
		int numSourceDimensions = 0; // for MixedTransformView of the item.data RandomAccessible
		for ( int d = 0; d < m; ++d )
		{
			if ( item.variesWithAxes[ d ] && !transformToSource.getComponentZero( d ) )
			{
				variesWithAxes[ transformToSource.getComponentMapping( d ) ] = true;
				++numSourceDimensions;
			}
		}

		if ( numSourceDimensions == 0 )
		{
			// last dependent axis sliced away
			// create new SimpleItem< T > with the T

			final Point p = new Point( n );
			final Point q = new Point( m );
			transformToSource.apply( p, q );
			final T data = item.getAt( q );
			return new SimpleItem< T >( item.name, data, attachedToAxes );
		}

		final int[] sourceDimMap = new int[ n ];
		for ( int i = 0, d = 0; d < n; ++d )
			if ( variesWithAxes[ d ] )
				sourceDimMap[ d ] = i++;

		final int numTargetDimensions = item.data.numDimensions();
		final long[] translation = new long[ numTargetDimensions ];
		final boolean[] zero = new boolean[ numTargetDimensions ];
		final boolean[] invert = new boolean[ numTargetDimensions ];
		final int[] component = new int[ numTargetDimensions ];
		for ( int i = 0, d = 0; d < m; ++d )
		{
			if ( item.variesWithAxes[ d ] )
			{
				translation[ i ] = transformToSource.getTranslation( d );
				zero[ i ] = transformToSource.getComponentZero( d );
				invert[ i ] = transformToSource.getComponentInversion( d );
				component[ i ] = sourceDimMap[ transformToSource.getComponentMapping( d ) ];
				++i;
			}
		}

		final MixedTransform dataTransform = new MixedTransform( numTargetDimensions, numTargetDimensions );
		dataTransform.setTranslation( translation );
		dataTransform.setComponentZero( zero );
		dataTransform.setComponentInversion( invert );
		dataTransform.setComponentMapping( component );

		final MixedTransformView< T > transformedData = new MixedTransformView<>( item.data, dataTransform );

		return new VaryingItem<>( item.name, transformedData, variesWithAxes, attachedToAxes );
	}

	/*
	 * MixedTransform helpers
	 */

	// TODO: this is copied from Views.rotate. Should be reused.
	public static MixedTransform getRotationTransform( final int fromAxis, final int toAxis, final int n )
	{
		final MixedTransform t = new MixedTransform( n, n );
		if ( fromAxis != toAxis )
		{
			final int[] component = new int[ n ];
			final boolean[] inv = new boolean[ n ];
			for ( int e = 0; e < n; ++e )
			{
				if ( e == toAxis )
				{
					component[ e ] = fromAxis;
					inv[ e ] = true;
				}
				else if ( e == fromAxis )
				{
					component[ e ] = toAxis;
				}
				else
				{
					component[ e ] = e;
				}
			}
			t.setComponentMapping( component );
			t.setComponentInversion( inv );
		}
		return t;
	}

	// TODO: this is copied from Views.permute. Should be reused.
	public static MixedTransform getPermuteTransform( final int fromAxis, final int toAxis, final int n )
	{
		final int[] component = new int[ n ];
		for ( int e = 0; e < n; ++e )
			component[ e ] = e;
		component[ fromAxis ] = toAxis;
		component[ toAxis ] = fromAxis;
		final MixedTransform t = new MixedTransform( n, n );
		t.setComponentMapping( component );
		return t;
	}

	// TODO: this is copied from Views.hyperSlice. Should be reused.
	public static MixedTransform getHyperSliceTransform( final int d, final long pos, final int m )
	{
		final int n = m - 1;
		final MixedTransform t = new MixedTransform( n, m );
		final long[] translation = new long[ m ];
		translation[ d ] = pos;
		final boolean[] zero = new boolean[ m ];
		final int[] component = new int[ m ];
		for ( int e = 0; e < m; ++e )
		{
			if ( e < d )
			{
				zero[ e ] = false;
				component[ e ] = e;
			}
			else if ( e > d )
			{
				zero[ e ] = false;
				component[ e ] = e - 1;
			}
			else
			{
				zero[ e ] = true;
				component[ e ] = 0;
			}
		}
		t.setTranslation( translation );
		t.setComponentZero( zero );
		t.setComponentMapping( component );
		return t;
	}

	private static int getNumSourceDimensions( final MetaDataItem< ? > item )
	{
		if ( item instanceof SimpleItem )
		{
			final SimpleItem< ? > si = ( SimpleItem< ? > ) item;
			if ( si.isAttachedToAxes() )
				return si.attachedToAxes.length;
		}
		else if ( item instanceof VaryingItem )
		{
			final VaryingItem< ? > vi = ( VaryingItem< ? > ) item;
			return vi.variesWithAxes.length;
		}
		else
			throw new IllegalArgumentException();
		return -1;
	}

	/*
	 * axis list helpers...
	 */

	public static boolean[] axisCollectionToFlags( final int numDimensions, final int[] axes )
	{
		final boolean[] flags = new boolean[ numDimensions ];
		for ( final int d : axes )
			flags[ d ] = true;
		return flags;
	}

	public static boolean[] axisCollectionToFlags( final int numDimensions, final Collection< Integer > axes )
	{
		final boolean[] flags = new boolean[ numDimensions ];
		for ( final int d : axes )
			flags[ d ] = true;
		return flags;
	}

	public static int[] flagsToAxisList( final boolean[] flags )
	{
		final int[] tmp = new int[ flags.length ];
		int i = 0;
		for ( int d = 0; d < flags.length; ++d )
			if ( flags[ d ] )
				tmp[ i++ ] = d;
		return Arrays.copyOfRange( tmp, 0, i );
	}

	/**
	 * Swap values of {@code flags[i]} and {@code flags[j]} in-place.
	 *
	 * @param flags
	 * @param i
	 *            index in {@code flags}
	 * @param j
	 *            index in {@code flags}
	 * @return {@code flags}.
	 */
	private static boolean[] swap( final boolean[] flags, final int i, final int j )
	{
		if ( flags[ i ] != flags[ j ] )
		{
			flags[ i ] = !flags[ i ];
			flags[ j ] = !flags[ j ];
		}
		return flags;
	}

	/*
	 * Types of metadata:
	 *
	 * 1.) simple item attached to one axis
	 *
	 * Example: XYZ calibration in a XYZTC dataset. Represented as 3 attributes.
	 * X calibration attached to X axis, etc.
	 *
	 * 2.) simple item attached to several axes
	 *
	 * Example: attribute "spatial" attached to X,Y,Z axes in XYZTC dataset.
	 *
	 * 3.) simple item not attached to any axis
	 *
	 * Example: color-table
	 *
	 * 4.) item varying with one or more axes
	 *
	 * Example: per-slice color-table
	 *
	 *
	 *
	 *
	 *
	 * varies with axes: indices : int[] data : RandomAccessible< T >
	 * metadata.numDimensions() == indices.length OR does not vary with any
	 * axis: data : T
	 *
	 * attached to axes: indices : int[]
	 *
	 *
	 *
	 *
	 * What about Views.extend()??? RichViews can deal with border, mirror, and
	 * periodic automatically. But probably not random, zero, and value.
	 *
	 *
	 */

//	public static < T, F extends EuclideanSpace > RealRandomAccessible< T > interpolate( final F source, final InterpolatorFactory< T, F > factory )
//	public static < T > RandomAccessibleOnRealRandomAccessible< T > raster( final RealRandomAccessible< T > source )
//	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extend( final F source, final OutOfBoundsFactory< T, ? super F > factory )
//	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendMirrorSingle( final F source )
//	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendMirrorDouble( final F source )
//	public static < T extends Type< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendValue( final F source, final T value )
//	public static < T extends NumericType< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendZero( final F source )
//	public static < T extends RealType< T >, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendRandom( final F source, final double min, final double max )
//	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendPeriodic( final F source )
//	public static < T, F extends RandomAccessibleInterval< T > > ExtendedRandomAccessibleInterval< T, F > extendBorder( final F source )
//	public static < T > IntervalView< T > interval( final RandomAccessible< T > randomAccessible, final long[] min, final long[] max )
//	public static < T > IntervalView< T > interval( final RandomAccessible< T > randomAccessible, final Interval interval )
//	public static < T > MixedTransformView< T > rotate( final RandomAccessible< T > randomAccessible, final int fromAxis, final int toAxis )
//	public static < T > IntervalView< T > rotate( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )
//	public static < T > MixedTransformView< T > permute( final RandomAccessible< T > randomAccessible, final int fromAxis, final int toAxis )
//	public static < T > IntervalView< T > permute( final RandomAccessibleInterval< T > interval, final int fromAxis, final int toAxis )
//	public static < T > MixedTransformView< T > translate( final RandomAccessible< T > randomAccessible, final long... translation )
//	public static < T > IntervalView< T > translate( final RandomAccessibleInterval< T > interval, final long... translation )
//	public static < T > MixedTransformView< T > offset( final RandomAccessible< T > randomAccessible, final long... offset )
//	public static < T > IntervalView< T > offset( final RandomAccessibleInterval< T > interval, final long... offset )
//	public static < T > IntervalView< T > zeroMin( final RandomAccessibleInterval< T > interval )
//	public static < T > MixedTransformView< T > hyperSlice( final RandomAccessible< T > view, final int d, final long pos )
//	public static < T > IntervalView< T > hyperSlice( final RandomAccessibleInterval< T > view, final int d, final long pos )
//	public static < T > MixedTransformView< T > addDimension( final RandomAccessible< T > randomAccessible )
//	public static < T > IntervalView< T > addDimension( final RandomAccessibleInterval< T > interval, final long minOfNewDim, final long maxOfNewDim )
//	public static < T > MixedTransformView< T > invertAxis( final RandomAccessible< T > randomAccessible, final int d )
//	public static < T > IntervalView< T > invertAxis( final RandomAccessibleInterval< T > interval, final int d )
//	public static < T > IntervalView< T > offsetInterval( final RandomAccessible< T > randomAccessible, final long[] offset, final long[] dimension )
//	public static < T > IntervalView< T > offsetInterval( final RandomAccessible< T > randomAccessible, final Interval interval )
//	public static boolean isZeroMin( final Interval interval )
//	public static < T > IterableInterval< T > iterable( final RandomAccessibleInterval< T > randomAccessibleInterval )
//	public static < T > IterableInterval< T > flatIterable( final RandomAccessibleInterval< T > randomAccessibleInterval )
//	public static < T > CompositeIntervalView< T, ? extends GenericComposite< T > > collapse( final RandomAccessibleInterval< T > source )
//	public static < T extends RealType< T > > CompositeIntervalView< T, RealComposite< T > > collapseReal( final RandomAccessibleInterval< T > source )
//	public static < T extends NumericType< T > > CompositeIntervalView< T, NumericComposite< T > > collapseNumeric( final RandomAccessibleInterval< T > source )
//	public static < T > CompositeView< T, ? extends GenericComposite< T > > collapse( final RandomAccessible< T > source )
//	public static < T extends RealType< T > > CompositeView< T, RealComposite< T > > collapseReal( final RandomAccessible< T > source, final int numChannels )
//	public static < T extends NumericType< T > > CompositeView< T, NumericComposite< T > > collapseNumeric( final RandomAccessible< T > source, final int numChannels )
//	public static < T > SubsampleIntervalView< T > subsample( final RandomAccessibleInterval< T > source, final long step )
//	public static < T > SubsampleIntervalView< T > subsample( final RandomAccessibleInterval< T > source, final long... steps )
//	public static < T > SubsampleView< T > subsample( final RandomAccessible< T > source, final long step )
//	public static < T > SubsampleView< T > subsample( final RandomAccessible< T > source, final long... steps )
//	public static < T > RandomAccessibleInterval< T > dropSingletonDimensions( final RandomAccessibleInterval< T > source )
//	public static < T > RandomAccessibleInterval< T > stack( final List< RandomAccessibleInterval< T > > hyperslices )
//	public static < T > RandomAccessibleInterval< T > stack( final RandomAccessibleInterval< T >... hyperslices )
//	public static < T > RandomAccessibleInterval< T > stack( final StackAccessMode stackAccessMode, final List< RandomAccessibleInterval< T > > hyperslices )
//	public static < T > RandomAccessibleInterval< T > stack( final StackAccessMode stackAccessMode, final RandomAccessibleInterval< T >... hyperslices )
//	public static < T > TransformView< T > shear(
//	public static < T > TransformView< T > unshear(
//	public static < T > IntervalView< T > shear(
//	public static < T > IntervalView< T > unshear(
//	public static < T > IntervalView< T > permuteCoordinates(
//	public static < T > IntervalView< T > permuteCoordinates(
//	public static < T > IntervalView< T > permuteCoordinatesInverse(
//	public static < T > IntervalView< T > permuteCoordinateInverse(
//	public static < A, B > RandomAccessible< Pair< A, B > > pair( final RandomAccessible< A > sourceA, final RandomAccessible< B > sourceB )



}
