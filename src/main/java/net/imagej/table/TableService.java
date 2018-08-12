
package net.imagej.table;

import java.util.ArrayList;
import java.util.List;

import net.imagej.Dataset;
import net.imagej.ImageJService;

import org.scijava.service.Service;
import org.scijava.table.Table;

//TODO keep deprecated version of this with net.imagej.table.Table ?

/**
 * {@link Service} for working with {@link Table}s.
 *
 * @author Alison Walter
 */
public interface TableService extends ImageJService {

	public static final String TABLE_PROPERTY = "tables";

	/**
	 * Retrieves the {@link Table}s attached to the given {@link Dataset}.
	 *
	 * @param img {@link Dataset} whose {@link Table}s are desired
	 * @return {@link Table}s associated with {@code img}
	 */
	List<Table<?, ?>> getTables(final Dataset img);

	/**
	 * Attaches the given {@link Table} to the {@link Dataset}
	 *
	 * @param table {@link Table} to be attached
	 * @param img {@link Dataset} to attach the table to
	 */
	@SuppressWarnings("unchecked")
	default void add(final Table<?, ?> table, final Dataset img) {
		if (img.getProperties().get(TABLE_PROPERTY) != null) {
			((List<Table<?, ?>>) img.getProperties().get(TABLE_PROPERTY)).add(table);
		}
		else {
			final List<Table<?, ?>> t = new ArrayList<>();
			t.add(table);
			img.getProperties().put(TABLE_PROPERTY, t);
		}
	}

	/**
	 * Clears any {@link Table}s associated with the given {@link Dataset}.
	 *
	 * @param img the {@link Dataset} whose attached {@link Table}s will be
	 *          cleared.
	 */
	default void clear(final Dataset img) {
		img.getProperties().put(TABLE_PROPERTY, null);
	}

}
