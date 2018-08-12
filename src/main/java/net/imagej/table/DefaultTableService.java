package net.imagej.table;

import java.util.List;

import net.imagej.Dataset;

import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.table.Table;

// TODO keep deprecated version of this with net.imagej.table.Table ?

@Plugin(type = Service.class)
public class DefaultTableService extends AbstractService implements TableService{

	@Override
	@SuppressWarnings("unchecked")
	public List<Table<?, ?>> getTables(Dataset img) {
		final Object tables = img.getProperties().get(TABLE_PROPERTY);
		if (tables != null && tables instanceof List)
			return (List<Table<?, ?>>) tables;
		return null;
	}

}
