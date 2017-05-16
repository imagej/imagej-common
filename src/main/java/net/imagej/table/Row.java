package net.imagej.table;


public interface Row {
	Object get(int columnIndex);
	<T> T get(int columnIndex, Class<T> columnType);
}
