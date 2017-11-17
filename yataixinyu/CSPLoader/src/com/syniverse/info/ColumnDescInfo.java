package com.syniverse.info;

import java.io.Serializable;

/**
 * A javabean class which describes a database column. These field are all from
 * java.sql.ResultSetMetaData
 * 
 */
public class ColumnDescInfo implements Serializable {

	/**
	 * @param columnName
	 * @param displaySize
	 * @param fullyQualifiedJavaType
	 * @param sqlType
	 * @param dbSpecificColumnTypeName
	 * @param nullable
	 *            , Option value:
	 *            ResultSetMetaData.columnNoNulls,ResultSetMetaData
	 *            .columnNullable ,ResultSetMetaData.columnNullableUnknown
	 * 
	 * @param precision
	 * @param scale
	 */
	public ColumnDescInfo(String columnName, int displaySize,
			String fullyQualifiedJavaType, int sqlType,
			String dbSpecificColumnTypeName, int nullable, int precision,
			int scale) {
		this.columnName = columnName;
		this.displaySize = displaySize;
		this.fullyQualifiedJavaType = fullyQualifiedJavaType;
		this.sqlType = sqlType;
		this.dbSpecificColumnTypeName = dbSpecificColumnTypeName;
		this.nullable = nullable;
		this.precision = precision;
		this.scale = scale;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String fullyQualifiedJavaType;
	// Indicates the designated column's normal maximum width in characters.
	public final int displaySize;
	// column's name
	public final String columnName;
	/**
	 * SQL type from java.sql.Types
	 */
	public final int sqlType;

	/**
	 * column's database-specific type name
	 */
	public final String dbSpecificColumnTypeName;

	/**
	 * column's specified column size.
	 * 
	 * For numeric data, this is the maximum precision. For character data, this
	 * is the length in characters. For datetime datatypes, this is the length
	 * in characters of the String representation (assuming the maximum allowed
	 * precision of the fractional seconds component). For binary data, this is
	 * the length in bytes. For the ROWID datatype, this is the length in bytes.
	 * 0 is returned for data types where the column size is not applicable.
	 * 
	 */
	public final int precision;

	/**
	 * column's number of digits to right of the decimal point. 0 is returned
	 * for data types where the scale is not applicable.
	 */
	public final int scale;
	/**
	 * Indicates the nullability of values in the designated column.
	 * 
	 * Option value:
	 * ResultSetMetaData.columnNoNulls,ResultSetMetaData.columnNullable
	 * ,ResultSetMetaData.columnNullableUnknown
	 */
	public final int nullable;

}
